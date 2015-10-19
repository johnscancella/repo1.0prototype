package gov.loc.rdc.tasks;

import gov.loc.rdc.controllers.RequestMappings;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.DeferredResult;

public class OrderedServerForwardedScpFileTransferTask extends OrderServerForwardedTask implements Runnable{
  private static final Logger logger = LoggerFactory.getLogger(OrderedServerForwardedScpFileTransferTask.class);
  
  private final DeferredResult<Boolean> result;
  private final String filePath;
  private final String toUrl;

  public OrderedServerForwardedScpFileTransferTask(List<String> serversToVisit, DeferredResult<Boolean> result, String filePath, String toUrl){
    super(serversToVisit);
    this.result = result;
    this.filePath = filePath;
    this.toUrl = toUrl;
  }
  
  //for unit testing
  protected OrderedServerForwardedScpFileTransferTask(List<String> serversToVisit, DeferredResult<Boolean> result, String filePath, String toUrl, RestTemplate restTemplate){
    super(serversToVisit, restTemplate);
    this.result = result;
    this.filePath = filePath;
    this.toUrl = toUrl;
  }

  @Override
  public void run() {
    Exception lastException = null;
    
    for(String serverToVisit : serversToVisit){
      try{
        String url = serverToVisit + RequestMappings.SCP_FILE;
        Boolean wasScped = restTemplate.getForObject(url, Boolean.class);
        if(wasScped){
          logger.debug("Sucessfully forwarded scp request to server [{}] for file [{}] to [{}].", serverToVisit, filePath, toUrl);
          result.setResult(wasScped);
          return;
        }
      }
      catch(Exception e){
        logger.warn("Failed to scp file from [{}] to [{}]", filePath, toUrl, e);
        lastException = e;
      }
    }
    
    logger.error("Failed all forwards to scp file. Tried servers [{}]", serversToVisit);
    if(lastException != null){
      result.setErrorResult(lastException);
    }
    else{
      result.setResult(false);
    }
  }
}
