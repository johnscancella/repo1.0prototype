package gov.loc.rdc.tasks;

import gov.loc.rdc.hash.SHA256Hasher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

import com.rabbitmq.client.Channel;

public class StoreFileMessageTask implements Runnable{
  private static final Logger logger = LoggerFactory.getLogger(StoreFileMessageTask.class);
  private static final String DEFAULT_EXCHANGE = "";
  private final DeferredResult<String> result;
  private final MultipartFile file;
  private final Channel channel;
  private final String queueName;
  
  public StoreFileMessageTask(DeferredResult<String> result, MultipartFile file, Channel channel, String queueName){
    this.result = result;
    this.file = file;
    this.channel = channel;
    this.queueName = queueName;
  }

  @Override
  public void run() {
    try{
      logger.info("Sending file [{}] to queue [{}].", file.getName(), queueName);
      channel.basicPublish(DEFAULT_EXCHANGE, queueName, null, file.getBytes());
      String hash = SHA256Hasher.hash(file.getInputStream());
      logger.debug("Computed hash [{}] for file [{}].", hash, file.getName());
      result.setResult(hash);
    }
    catch(Exception e){
      logger.error("Unable to send file to queue [{}].", queueName, e);
      result.setErrorResult(e);
    }
  }
  
}
