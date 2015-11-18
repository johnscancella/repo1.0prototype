package gov.loc.rdc.runners;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;

import gov.loc.rdc.consumers.FilePutRequestConsumer;
import gov.loc.rdc.hash.HashPathUtils;
import gov.loc.rdc.host.HostUtils;

@Component
public class FilePutRequestMessageHandler extends AbstractFileRequest implements CommandLineRunner, HashPathUtils, HostUtils {
  
  @Value("${putqueue.name}")
  protected String queueName;

  @Override
  public void run(String... args) throws Exception {
    Channel channel = createChannel(queueName, mqHost);
    handleFilePullRequest(channel, queueName);
  }

  protected void handleFilePullRequest(Channel storeFileChannel, String queue) throws Exception {
    Consumer consumer = new FilePutRequestConsumer(storeFileChannel, objectStoreRootDir, fileStoreRepo);
    boolean autoAck=false;
    storeFileChannel.basicConsume(queue, autoAck, consumer);
  }
}
