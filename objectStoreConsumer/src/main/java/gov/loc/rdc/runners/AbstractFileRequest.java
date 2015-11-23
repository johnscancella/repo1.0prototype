package gov.loc.rdc.runners;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import gov.loc.rdc.repositories.FileStoreMetadataRepository;

public abstract class AbstractFileRequest {
  @Value("${mq_host}")
  protected String mqHost;

  @Value("${root_dir:/tmp}")
  protected File objectStoreRootDir;
  
  @Autowired
  protected FileStoreMetadataRepository fileStoreRepo;
  
  protected Channel createChannel(String queue, String host) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(host);
    Connection connection = factory.newConnection();
    Channel newChannel = connection.createChannel();

    boolean durable = true;
    newChannel.queueDeclare(queue, durable, false, false, null);
    return newChannel;
  }

  //for unit test
  protected void setMqHost(String mqHost) {
    this.mqHost = mqHost;
  }
}
