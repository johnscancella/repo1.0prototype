package gov.loc.rdc.controllers;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * Responsible for load balancing slave servers based on queue name and creating all the various queues.
 */
@RestController
public class MessageQueueController {
  private static final Logger logger = LoggerFactory.getLogger(MessageQueueController.class);
  private static final String QUEUE_BASE_NAME = "fileSendingQueue";
  
  @Value("${number_of_copies_to_keep}")
  private Integer numberOfCopies; 
  
  @Value("${mq_host}")
  private String mqHost; 
  
  @Value("${max_number_of_messages_to_process_concurrently}")
  private Integer maxNumberOfMessagesToProcessConcurrently; 
  
  private Set<String> fileSendingQueueNames;
  private Channel channel;
  private int queueIndex;
  
  public MessageQueueController(){}
  
  //used for unit testing
  protected MessageQueueController(Integer numberOfCopies, String mqHost, Integer maxNumberOfMessagesToProcessConcurrently){
    this.numberOfCopies = numberOfCopies;
    this.mqHost = mqHost;
    this.maxNumberOfMessagesToProcessConcurrently = maxNumberOfMessagesToProcessConcurrently;
  }

  @PostConstruct
  protected void setup() throws Exception{
    queueIndex = numberOfCopies;
    fileSendingQueueNames = new HashSet<>();
    channel = createChannel(mqHost);
    for(int queueNum=1; queueNum<= numberOfCopies; queueNum++){
      String queueName = QUEUE_BASE_NAME + queueNum;
      logger.info("Creating queue [{}] for sending files to be stored.", queueName);
      fileSendingQueueNames.add(queueName);
      createQueue(queueName, channel, maxNumberOfMessagesToProcessConcurrently);
    }
  }
  
  protected Channel createChannel(String host) throws Exception{
    logger.debug("Creating channel to host [{}].", host);
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(host);
    Connection connection = factory.newConnection();
    Channel newChannel = connection.createChannel();
    
    return newChannel;
  }
  
  protected void createQueue(String queueName, Channel theChannel, int prefetchCount) throws Exception{
    boolean durable = true;
    logger.debug("Creating queue [{}] that is durable [{}] and has a prefetch count [{}].", queueName, durable, prefetchCount);
    theChannel.queueDeclare(queueName, durable, false, false, null);
    theChannel.basicQos(prefetchCount);
  }
  
  @RequestMapping(value=RequestMappings.GET_FILE_STORE_QUEUE_NAME_URL, method=RequestMethod.GET)
  public String getNextQueueName(){
    queueIndex++;
    if(queueIndex > numberOfCopies) queueIndex = 1;
    int index = queueIndex;
    index %= numberOfCopies;
    index++; //make sure it starts with 1 and not 0
    
    return QUEUE_BASE_NAME + index;
  }

  public Set<String> getFileSendingQueueNames() {
    return fileSendingQueueNames;
  }

  public Channel getChannel() {
    return channel;
  }
}
