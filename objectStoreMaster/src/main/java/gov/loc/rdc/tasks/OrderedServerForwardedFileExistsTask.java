package gov.loc.rdc.tasks;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.async.DeferredResult;

public class OrderedServerForwardedFileExistsTask extends OrderServerForwardedTask implements Runnable{
  private static final Logger logger = LoggerFactory.getLogger(OrderedServerForwardedFileExistsTask.class);
  
  private final DeferredResult<Boolean> result;
  private final String algorithm;
  private final String hash;
  
  public OrderedServerForwardedFileExistsTask(List<String> serversToVisit, DeferredResult<Boolean> result, String algorithm, String hash){
    super(serversToVisit);
    this.result = result;
    this.algorithm = algorithm;
    this.hash = hash;
  }

  @Override
  public void run() {
    Exception lastException = null;
    
    for(String serverToVisit : serversToVisit){
      String url = createForwardUrl(serverToVisit);
      try{
        Boolean fileExists = restTemplate.getForObject(url, Boolean.class);
        if(fileExists){
          result.setResult(fileExists);
          return;
        }
      }
      catch(Exception e){
        logger.warn("Failed to get if file exists from [{}]", url, e);
        lastException = e;
      }
    }
    
    if(lastException != null){
      logger.error("Failed all forwards to get if file exists. Tried servers [{}]", serversToVisit);
      result.setErrorResult(lastException);
    }
    else{
      logger.info("File with hash [{}] doesn't exist on any of the servers [{}]", hash, serversToVisit);
      result.setResult(false);
    }
  }
  
  protected String createForwardUrl(String server){
    StringBuilder sb = new StringBuilder();
    sb.append(server).append("/fileexists/").append(algorithm).append('/').append(hash);
    
    return sb.toString();
  }
}
