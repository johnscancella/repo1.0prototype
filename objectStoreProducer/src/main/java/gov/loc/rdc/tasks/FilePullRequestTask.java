package gov.loc.rdc.tasks;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.rabbitmq.client.Channel;

import gov.loc.rdc.domain.ScpInfo;

public class FilePullRequestTask implements Runnable{
  private static final Logger logger = LoggerFactory.getLogger(FilePullRequestTask.class);
  private static final String DEFAULT_EXCHANGE = "";
  private final Channel channel;
  private final ScpInfo scpInfo;
  private final Map<String, Integer> storageTypesToCopiesMap;
  
  public FilePullRequestTask(ScpInfo scpInfo, Channel channel, Map<String, Integer> storageTypesToCopiesMap){
    this.scpInfo = scpInfo;
    this.channel = channel;
    this.storageTypesToCopiesMap = storageTypesToCopiesMap;
  }
  
  public void run() {
    try {
      for(String storageType : storageTypesToCopiesMap.keySet()){
        byte[] serializedScpInfo = convertToJsonByteArray(scpInfo);
        int numberOfCopies = storageTypesToCopiesMap.get(storageType);
        for(int copy=1; copy<=numberOfCopies; copy++){
          logger.info("Sending number [{}] of [{}] file pull requests to [{}] storage queue.", copy, numberOfCopies, storageType);
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
