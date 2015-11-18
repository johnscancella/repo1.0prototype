package gov.loc.rdc.tasks;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.rabbitmq.client.Channel;

import gov.loc.rdc.domain.PutInfo;

public class FilePutRequestTask implements Runnable {
  private static final Logger logger = LoggerFactory.getLogger(FilePutRequestTask.class);
  private static final String DEFAULT_EXCHANGE = "";
  private static final int NUMBER_OF_COPIES = 3;
  private final Channel channel;
  private final List<String> queueNames;
  private final PutInfo putInfo;
  
  public FilePutRequestTask(Channel channel, List<String> queueNames, PutInfo putInfo){
    this.channel = channel;
    this.queueNames = queueNames;
    this.putInfo = putInfo;
  }

  @Override
  public void run() {
    try {
      for(String storageType : queueNames){
        byte[] serializedPutInfo = convertToJsonByteArray(putInfo);
        for(int copy=1; copy<=NUMBER_OF_COPIES; copy++){
          logger.info("Sending number [{}] of [{}] file pull requests to [{}] storage queue.", copy, NUMBER_OF_COPIES, storageType);
          channel.basicPublish(DEFAULT_EXCHANGE, storageType, null, serializedPutInfo);
        }
      }
    } catch (Exception e) {
      logger.error("Failed to send message to queue.", e);
    }
  }
  
  protected byte[] convertToJsonByteArray(PutInfo info) throws Exception{
    ObjectWriter writer = new ObjectMapper().writer().withDefaultPrettyPrinter();
    String jsonPutInfo = writer.writeValueAsString(info);
    
    return jsonPutInfo.getBytes();
  }

}
