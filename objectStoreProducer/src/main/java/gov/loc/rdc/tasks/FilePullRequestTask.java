package gov.loc.rdc.tasks;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.rabbitmq.client.Channel;

import gov.loc.rdc.domain.ScpInfo;

public class FilePullRequestTask implements Runnable{
  private static final Logger logger = LoggerFactory.getLogger(FilePullRequestTask.class);
  private static final String DEFAULT_EXCHANGE = "";
  private static final int NUMBER_OF_COPIES = 3;
  private final Channel channel;
  private final ScpInfo scpInfo;
  private final List<String> queueNames;
  
  public FilePullRequestTask(ScpInfo scpInfo, Channel channel, List<String> queueNames){
    this.scpInfo = scpInfo;
    this.channel = channel;
    this.queueNames = queueNames;
  }
  
  public void run() {
    try {
      for(String storageType : queueNames){
        byte[] serializedScpInfo = convertToJsonByteArray(scpInfo);
        for(int copy=1; copy<=NUMBER_OF_COPIES; copy++){
          logger.info("Sending number [{}] of [{}] file pull requests to [{}] storage queue.", copy, NUMBER_OF_COPIES, storageType);
          channel.basicPublish(DEFAULT_EXCHANGE, storageType, null, serializedScpInfo);
        }
      }
    } catch (Exception e) {
      logger.error("Failed to send message to queue.", e);
    }
  }
  
  protected byte[] convertToJsonByteArray(ScpInfo info) throws Exception{
    ObjectWriter writer = new ObjectMapper().writer().withDefaultPrettyPrinter();
    String jsonScpInfo = writer.writeValueAsString(info);
    
    return jsonScpInfo.getBytes();
  }
}
