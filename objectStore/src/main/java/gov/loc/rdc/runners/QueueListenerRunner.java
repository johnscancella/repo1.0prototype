package gov.loc.rdc.runners;

import gov.loc.rdc.controllers.RequestMappings;
import gov.loc.rdc.entities.FileStoreData;
import gov.loc.rdc.hash.HashPathUtils;
import gov.loc.rdc.hash.SHA256Hasher;
import gov.loc.rdc.host.HostUtils;
import gov.loc.rdc.repositories.FileStoreRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

/**
 * Responsible for getting the name of the queue to listen to and processing the requests.
 */
@Component
public class QueueListenerRunner implements CommandLineRunner, HashPathUtils, HostUtils{
  private static final Logger logger = LoggerFactory.getLogger(QueueListenerRunner.class);
  
  @Autowired
  private FileStoreRepository fileStoreRepo;
  
  @Value("${master_url}")
  private String masterNodeUrl;
  
  //default to 10 minutes
  @Value("${reconnect_period_in_miliseconds:600000}")
  private Integer retryPeriod;
  
  @Value("${mq_host}")
  private String mqHost; 
  
  @Value("${root_dir:/tmp}")
  private File objectStoreRootDir;
  
  private String queueName;
  private Channel channel;
  
  private final RestTemplate restTemplate = new RestTemplate();
  
  @Override
  public void run(String... args) throws Exception {
    queueName = getQueueName(masterNodeUrl);
    channel = createChannel(queueName, mqHost);
    storeFiles(channel, queueName);
  }
  
  protected String getQueueName(String hostname){
    while(true){
      try{
        String requestUrl = hostname + RequestMappings.GET_FILE_STORE_QUEUE_NAME_URL;
        String name = restTemplate.getForObject(requestUrl, String.class);
        logger.info("Listening for file storing requests on queue [{}].", name);
        return name;
      }
      catch(Exception e){
        logger.error("Something went wrong when trying to auto join master node. Retrying again in [{}] miliseconds.", retryPeriod, e);
      }
      logger.info("Was not able to register with master. Retrying again in [{}] miliseconds.", retryPeriod);
      sleep();
    }
  }
  
  protected void sleep(){
    try{
      Thread.sleep(retryPeriod);
    }
    catch(Exception e){
      logger.error("Couldn't pause application. ", e);
    }
  }
  
  protected Channel createChannel(String queue, String host) throws Exception{
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(host);
    Connection connection = factory.newConnection();
    Channel newChannel = connection.createChannel();

    boolean durable = true;
    newChannel.queueDeclare(queue, durable, false, false, null);
    return newChannel;
  }
  
  protected void storeFiles(Channel storeFileChannel, String queue) throws IOException{
    Consumer consumer = new DefaultConsumer(storeFileChannel) {
      @Override
      public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
          throws IOException {
        try{
          String hash = storeFile(body);
          String host = getHostName();
          fileStoreRepo.upsert(new FileStoreData(hash, host));
          storeFileChannel.basicAck(envelope.getDeliveryTag(), false);
        }
        catch(Exception e){
          logger.error("Could not write bytes to filesystem.", e);
          logger.debug("Rejecting message [{}] and requeuing due to error [{}].", envelope.getDeliveryTag(), e.getMessage());
          storeFileChannel.basicNack(envelope.getDeliveryTag(), false, true);
          //TODO send email or other notification?
        }
        }};
    boolean autoAck=false;
    storeFileChannel.basicConsume(queue, autoAck, consumer);
  }
  
  protected String storeFile(byte[] data) throws Exception{
    if(data != null && data.length > 0){
      String hash = SHA256Hasher.hash(data);
      File storedFile = computeStoredLocation(objectStoreRootDir, hash);
      if(!storedFile.exists()){
        logger.debug("Creating path [{}] if it does not already exist.", storedFile.getParent());
        Files.createDirectories(storedFile.getParentFile().toPath());
        logger.info("Writing bytes to [{}] with hash [{}].", storedFile.toURI(), hash);
        Files.write(storedFile.toPath(), data, StandardOpenOption.CREATE);
      }
      else{
        logger.info("Already stored file with hash [{}], skipping.", hash);
      }
      return hash;
    }
    throw new Exception("Byte data was null or empty!");
  }
}
