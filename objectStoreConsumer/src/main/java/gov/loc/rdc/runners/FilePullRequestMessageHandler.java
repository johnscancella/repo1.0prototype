package gov.loc.rdc.runners;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;

import gov.loc.rdc.consumers.FilePullRequestConsumer;
import gov.loc.rdc.hash.HashPathUtils;
import gov.loc.rdc.host.HostUtils;
import gov.loc.rdc.repositories.FileStoreMetadataRepository;

@Component
public class FilePullRequestMessageHandler implements CommandLineRunner, HashPathUtils, HostUtils {
  @Value("${mq_host}")
  private String mqHost;

  @Value("${queue.name}")
  private String queueName;

  @Value("${root_dir:/tmp}")
  private File objectStoreRootDir;
  
  @Autowired
  private FileStoreMetadataRepository fileStoreRepo;

  @Override
  public void run(String... args) throws Exception {
    Channel channel = createChannel(queueName, mqHost);
    handleFilePullRequest(channel, queueName);
  }

  protected Channel createChannel(String queue, String host) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(host);
    Connection connection = factory.newConnection();
    Channel newChannel = connection.createChannel();

    boolean durable = true;
    newChannel.queueDeclare(queue, durable, false, false, null);
    return newChannel;
  }

  protected void handleFilePullRequest(Channel storeFileChannel, String queue) throws Exception {
    Consumer consumer = new FilePullRequestConsumer(storeFileChannel, objectStoreRootDir, fileStoreRepo);
    boolean autoAck=false;
    storeFileChannel.basicConsume(queue, autoAck, consumer);
  }
}
