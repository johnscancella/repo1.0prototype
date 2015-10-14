package gov.loc.rdc.tasks;

import gov.loc.rdc.controllers.RequestMappings;
import gov.loc.rdc.domain.ObjectStoreByteArrayResource;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

public class OrderedServerForwardedStoreFileTask extends OrderServerForwardedTask implements Runnable{
  private static final Logger logger = LoggerFactory.getLogger(OrderedServerForwardedStoreFileTask.class);
  private final DeferredResult<String> result;
  private final MultipartFile file;
  
  public OrderedServerForwardedStoreFileTask(List<String> serversToVisit, DeferredResult<String> result, MultipartFile file){
    super(serversToVisit);
    this.result = result;
    this.file = file;
  }
  
  //for unit testing
  protected OrderedServerForwardedStoreFileTask(List<String> serversToVisit, DeferredResult<String> result, MultipartFile file, RestTemplate restTemplate){
    super(serversToVisit, restTemplate);
    this.result = result;
    this.file = file;
  }

  @Override
  public void run() {
    Exception lastException = null;
    
    for(String serverToVisit : serversToVisit){
      String url = createForwardUrl(serverToVisit);
      logger.debug("Trying to forward to [{}]", url);
      try{
        String response = sendRequest(url);
        result.setResult(response);
        return;
      }
      catch(Exception e){
        logger.warn("Failed to forward file to [{}].", url, e);
        lastException = e;
      }
    }
    
    logger.error("Failed all forwards of file to be stored. Tried servers [{}]", serversToVisit);
    result.setErrorResult(lastException);
  }
  
  protected String createForwardUrl(String server){
    StringBuilder sb = new StringBuilder();
    sb.append(server).append(RequestMappings.STORE_FILE_URL);
    
    return sb.toString();
  }
  
  protected String sendRequest(String url) throws IOException, RestClientException{
    MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
    ByteArrayResource contentsAsResource = new ObjectStoreByteArrayResource(file);
    map.add("file", contentsAsResource);
    
    String response = restTemplate.postForObject(url, map, String.class);
    
    return response;
  }
}
