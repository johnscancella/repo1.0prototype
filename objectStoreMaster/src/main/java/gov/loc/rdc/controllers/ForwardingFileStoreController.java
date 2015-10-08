package gov.loc.rdc.controllers;

import gov.loc.rdc.tasks.OrderedServerForwardedFileExistsTask;
import gov.loc.rdc.tasks.OrderedServerForwardedGetFileTask;
import gov.loc.rdc.tasks.OrderedServerForwardedStoreFileTask;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

/**
 * Handles forwarding storing and getting files based on their hash to the proper slave node.
 */
@RestController
public class ForwardingFileStoreController implements FileStoreControllerApi{
  @Resource(name="threadPoolTaskExecutor")
  private ThreadPoolTaskExecutor threadExecutor;
  
  @Autowired
  private RoundRobinServerController roundRobinServerController;
  
  @Override
  @RequestMapping(value=RequestMappings.GET_FILE_URL, method=RequestMethod.GET, produces=MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public DeferredResult<byte[]> getFile(@PathVariable String algorithm, @PathVariable String hash){
    DeferredResult<byte[]> result = new DeferredResult<>();
    List<String> roundRobinServerList = roundRobinServerController.getAvailableServers();
    
    OrderedServerForwardedGetFileTask task = new OrderedServerForwardedGetFileTask(roundRobinServerList, result, algorithm, hash);
    threadExecutor.execute(task);
    
    return result;
  }
  
  @Override
  @RequestMapping(value=RequestMappings.STORE_FILE_URL, method={RequestMethod.POST, RequestMethod.PUT})
  public DeferredResult<String> storeFile(@RequestParam(value="file") MultipartFile file){
    DeferredResult<String> result = new DeferredResult<String>();
    List<String> roundRobinServerList = roundRobinServerController.getAvailableServers();
    
    OrderedServerForwardedStoreFileTask task = new OrderedServerForwardedStoreFileTask(roundRobinServerList, result, file);
    threadExecutor.execute(task);
    
    return result;
  }
  
  @Override
  @RequestMapping(value=RequestMappings.FILE_EXISTS_URL, method={RequestMethod.GET, RequestMethod.POST})
  public DeferredResult<Boolean> fileExists(@PathVariable String algorithm, @PathVariable String hash){
    DeferredResult<Boolean> result = new DeferredResult<Boolean>();
    List<String> roundRobinServerList = roundRobinServerController.getAvailableServers();
    
    OrderedServerForwardedFileExistsTask task = new OrderedServerForwardedFileExistsTask(roundRobinServerList, result, algorithm, hash);
    threadExecutor.execute(task);
    
    return result;
  }

  //only used in unit test
  protected void setThreadExecutor(ThreadPoolTaskExecutor threadExecutor) {
    this.threadExecutor = threadExecutor;
  }
}
