package gov.loc.rdc.controllers;

import gov.loc.rdc.tasks.RetrieveFileExistenceTask;
import gov.loc.rdc.tasks.RetrieveFileTask;
import gov.loc.rdc.tasks.StoreFileTask;

import java.io.File;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
 * Handles storing and getting files based on their hash.
 */
@RestController
public class FileStoreController implements FileStoreControllerApi{
  private static final Logger logger = LoggerFactory.getLogger(FileStoreController.class);
  
  @Resource(name="threadPoolTaskExecutor")
  private ThreadPoolTaskExecutor threadExecutor;
  
  @Value("${root_dir:/tmp}")
  private File objectStoreRootDir;
  
  @PostConstruct
  public void info(){
    logger.info("Storing hashed files in [{}] directory", objectStoreRootDir.toURI());
  }
  
  @Override
  @RequestMapping(value=RequestMappings.GET_FILE_URL, method=RequestMethod.GET, produces=MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public DeferredResult<byte[]> getFile(@PathVariable String hash){
    DeferredResult<byte[]> result = new DeferredResult<>();
    
    RetrieveFileTask task = new RetrieveFileTask(result, objectStoreRootDir, hash);
    threadExecutor.execute(task);
    
    return result;
  }
  
  @Override
  @RequestMapping(value=RequestMappings.STORE_FILE_URL, method={RequestMethod.POST, RequestMethod.PUT})
  public DeferredResult<String> storeFile(@RequestParam(value="file") MultipartFile file){
    DeferredResult<String> result = new DeferredResult<String>();
    
    StoreFileTask task = new StoreFileTask(result, file, objectStoreRootDir);
    threadExecutor.execute(task);
    
    return result;
  }
  
  @Override
  @RequestMapping(value=RequestMappings.FILE_EXISTS_URL, method={RequestMethod.GET, RequestMethod.POST})
  public DeferredResult<Boolean> fileExists(@PathVariable String hash){
    DeferredResult<Boolean> result = new DeferredResult<Boolean>();
    
    RetrieveFileExistenceTask task = new RetrieveFileExistenceTask(result, objectStoreRootDir, hash);
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
