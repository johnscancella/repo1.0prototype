package gov.loc.rdc.controllers;

import java.io.File;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import gov.loc.rdc.tasks.RetrieveFileTask;

/**
 * Handles storing and getting files based on their hash.
 */
@RestController
public class FileGetRequestController{
  @Resource(name="threadPoolTaskExecutor")
  private ThreadPoolTaskExecutor threadExecutor;
  
  @Value("${root_dir:/tmp}")
  private File objectStoreRootDir;
  
  @RequestMapping(value="/v1/file/get/{hash}", method=RequestMethod.GET, produces=MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public DeferredResult<byte[]> getFile(@PathVariable String hash){
    DeferredResult<byte[]> result = new DeferredResult<>();
    
    RetrieveFileTask task = new RetrieveFileTask(result, objectStoreRootDir, hash);
    threadExecutor.execute(task);
    
    return result;
  }

  //only used in unit test
  protected void setObjectStoreRootDir(File objectStoreRootDir) {
    this.objectStoreRootDir = objectStoreRootDir;
  }

  //only used in unit test
  protected void setThreadExecutor(ThreadPoolTaskExecutor threadExecutor) {
    this.threadExecutor = threadExecutor;
  }
}
