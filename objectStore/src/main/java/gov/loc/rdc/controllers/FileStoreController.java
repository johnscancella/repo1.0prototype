package gov.loc.rdc.controllers;

import gov.loc.rdc.errors.ResourceNotFoundException;
import gov.loc.rdc.errors.UnsupportedAlgorithm;
import gov.loc.rdc.hash.HashAlgorithm;
import gov.loc.rdc.hash.HashPathUtils;
import gov.loc.rdc.hash.Hasher;
import gov.loc.rdc.tasks.StoreFileTask;

import java.io.File;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

/**
 * Handles storing and getting files based on their hash.
 */
@RestController
public class FileStoreController implements HashPathUtils{
  private static final Logger logger = LoggerFactory.getLogger(FileStoreController.class);
  private static final String GET_FILE_URL = "/getfile/{algorithm}/{hash}";
  private static final String STORE_FILE_URL = "/storefile";
  
  @Autowired
  private Hasher hasher;
  
  @Autowired
  private ThreadPoolTaskExecutor threadExecutor;
  
  @Value("${rootDir:/tmp}")
  private File objectStoreRootDir;
  
  @PostConstruct
  public void info(){
    logger.info("Storing hashed files in [{}] directory", objectStoreRootDir.toURI());
  }
  
  //TODO change to async? http://callistaenterprise.se/blogg/teknik/2014/04/22/c10k-developing-non-blocking-rest-services-with-spring-mvc/
  @RequestMapping(value=GET_FILE_URL, method=RequestMethod.GET, produces=MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public @ResponseBody FileSystemResource getFile(@PathVariable String algorithm, @PathVariable String hash){
    if(!HashAlgorithm.algorithmSupported(algorithm)){
      logger.info("User tried to get stored file using unsupported hashing algorithm [{}]", algorithm);
      throw new UnsupportedAlgorithm("Only sha256 is currently supported for hashing algorithm");
    }
    
    logger.debug("Searching for file with hash [{}].", hash);
    File storedFile = computeStoredLocation(objectStoreRootDir, hash);
    
    if(storedFile.exists()){
      logger.debug("Found stored file that matches hash [{}].", hash);
      return new FileSystemResource(storedFile);
    }
    
    logger.warn("Unable to find stored file [{}]. Returning 404 instead.", storedFile.toURI());
    throw new ResourceNotFoundException();
  }
  
  @RequestMapping(value=STORE_FILE_URL, method={RequestMethod.POST, RequestMethod.PUT})
  public DeferredResult<String> storeFile(@RequestParam(value="file") MultipartFile file){
    DeferredResult<String> result = new DeferredResult<String>();
    
    StoreFileTask task = new StoreFileTask(result, file, objectStoreRootDir, hasher);
    threadExecutor.execute(task);
    
    return result;
  }

  //only used in unit test
  protected void setHasher(Hasher hasher) {
    this.hasher = hasher;
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
