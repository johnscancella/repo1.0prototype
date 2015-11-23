package gov.loc.rdc.runners;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;

import gov.loc.rdc.consumers.FilePullRequestConsumer;
import gov.loc.rdc.hash.HashPathUtils;
import gov.loc.rdc.host.HostUtils;

@Component
public class FilePullRequestMessageHandler extends AbstractFileRequest implements CommandLineRunner, HashPathUtils, HostUtils {
  
  @Value("${scpqueue.name}")
  private String queueName;

  @Override
  public void run(String... args) throws Exception {
    Channel channel = createChannel(queueName, mqHost);
    handleFilePullRequest(channel, queueName);
  }

  protected void handleFilePullRequest(Channel storeFileChannel, String queue) throws Exception {
    Consumer consumer = new FilePullRequestConsumer(storeFileChannel, objectStoreRootDir, fileStoreRepo);
    boolean autoAck=false;
    storeFileChannel.basicConsume(queue, autoAck, consumer);
  }

  //used for unit test only
  protected void setQueueName(String queueName) {
    this.queueName = queueName;
  }
}
