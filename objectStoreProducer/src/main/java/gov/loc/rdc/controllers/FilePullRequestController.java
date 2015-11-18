package gov.loc.rdc.controllers;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import gov.loc.rdc.domain.ScpInfo;
import gov.loc.rdc.tasks.FilePullRequestTask;

/**
 * Manages pull requests for files.
 */
@RestController
public class FilePullRequestController {
  private static final Logger logger = LoggerFactory.getLogger(FilePullRequestController.class);
  private static final List<String> QUEUE_NAMES = Arrays.asList("scpLongTerm", "scpAccess");

  @Value("${mq_host}")
  private String mqHost;

  @Value("${max_number_of_messages_to_process_concurrently}")
  private Integer maxNumberOfMessagesToProcessConcurrently;

  @Resource(name = "threadPoolTaskExecutor")
  private ThreadPoolTaskExecutor threadExecutor;

  private Channel channel;

  @PostConstruct
  protected void setup(){
    try {
      channel = createChannel(mqHost);
      for (String type : QUEUE_NAMES) {
        logger.info("Creating queue for storage type [{}].", type);
        createQueue(type, channel, maxNumberOfMessagesToProcessConcurrently);
      }
    } catch (Exception e) {
      logger.error("Failed to create queues in message store.", e);
    }
  }

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

  @RequestMapping(value = "/v1/filepull/{server}/{hash}", method = {RequestMethod.POST, RequestMethod.PUT })
  public void filePullRequest(@PathVariable String server, @PathVariable String hash, @RequestParam String file) {
    ScpInfo scpInfo = new ScpInfo(server, 22, file, hash);
    FilePullRequestTask task = new FilePullRequestTask(scpInfo, channel, QUEUE_NAMES);
    threadExecutor.execute(task);
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
