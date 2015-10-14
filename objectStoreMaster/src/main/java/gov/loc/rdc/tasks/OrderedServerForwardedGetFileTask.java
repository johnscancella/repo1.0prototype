package gov.loc.rdc.tasks;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.DeferredResult;

public class OrderedServerForwardedGetFileTask extends OrderServerForwardedTask implements Runnable{
  private static final Logger logger = LoggerFactory.getLogger(OrderedServerForwardedGetFileTask.class);
  
  private final DeferredResult<byte[]> result;
  private final String algorithm;
  private final String hash;
  
  public OrderedServerForwardedGetFileTask(List<String> serversToVisit, DeferredResult<byte[]> result, String algorithm, String hash){
    super(serversToVisit);
    this.result = result;
    this.algorithm = algorithm;
    this.hash = hash;
  }
  
  //for unit testing
  protected OrderedServerForwardedGetFileTask(List<String> serversToVisit, DeferredResult<byte[]> result, String algorithm, String hash, RestTemplate restTemplate){
    super(serversToVisit, restTemplate);
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
        ByteArrayResource contentsAsResource = restTemplate.getForObject(url, ByteArrayResource.class);
        result.setResult(contentsAsResource.getByteArray());
        return;
      }
      catch(Exception e){
        logger.warn("Failed to get file from [{}]", url, e);
        lastException = e;
      }
    }
    
    logger.error("Failed all forwards to get file. Tried servers [{}]", serversToVisit);
    result.setErrorResult(lastException);
  }
  
  protected String createForwardUrl(String server){
    StringBuilder sb = new StringBuilder();
    sb.append(server).append("/getfile/").append(algorithm).append('/').append(hash);
    
    return sb.toString();
  }
}
