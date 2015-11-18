package gov.loc.rdc.controllers;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public abstract class AbstractRequestController {
  protected static final Logger logger = LoggerFactory.getLogger(AbstractRequestController.class);
  
  @Value("${mq_host}")
  protected String mqHost;

  @Value("${max_number_of_messages_to_process_concurrently}")
  protected Integer maxNumberOfMessagesToProcessConcurrently;

  @Resource(name = "threadPoolTaskExecutor")
  protected ThreadPoolTaskExecutor threadExecutor;

  protected Channel channel;
  
  protected Channel createChannel(String host) throws Exception {
    logger.info("Creating channel to host [{}].", host);
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(host);
    Connection connection = factory.newConnection();
    Channel newChannel = connection.createChannel();

    return newChannel;
  }

  protected void createQueue(String queueName, Channel theChannel, int prefetchCount) throws Exception {
    boolean durable = true;
    logger.info("Creating queue [{}] that is durable [{}] and has a prefetch count [{}].", queueName, durable, prefetchCount);
    theChannel.queueDeclare(queueName, durable, false, false, null);
    theChannel.basicQos(prefetchCount);
  }
  
  //only for unit tests
  protected void setThreadExecutor(ThreadPoolTaskExecutor threadExecutor) {
    this.threadExecutor = threadExecutor;
  }

  //only for unit tests
  protected void setMqHost(String mqHost) {
    this.mqHost = mqHost;
  }

  //only for unit tests
  protected void setMaxNumberOfMessagesToProcessConcurrently(Integer maxNumberOfMessagesToProcessConcurrently) {
    this.maxNumberOfMessagesToProcessConcurrently = maxNumberOfMessagesToProcessConcurrently;
  }
}
