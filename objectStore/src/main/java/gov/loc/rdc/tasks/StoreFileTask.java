package gov.loc.rdc.tasks;

import gov.loc.rdc.errors.InternalErrorException;
import gov.loc.rdc.hash.HashPathUtils;
import gov.loc.rdc.hash.Hasher;

import java.io.File;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

/**
 * responsible for storing a file in the object store and returning the file hash
 */
public class StoreFileTask implements Runnable, HashPathUtils{
  private static final Logger logger = LoggerFactory.getLogger(StoreFileTask.class);
  private static final String NO_HASH = "Hash was not computed.";
  
  private final DeferredResult<String> result;
  private final MultipartFile file;
  private final File objectStoreRootDir;
  private final Hasher hasher;
  
  public StoreFileTask(DeferredResult<String> result, MultipartFile file, File objectStoreRootDir, Hasher hasher) {
    this.result = result;
    this.file = file;
    this.objectStoreRootDir = objectStoreRootDir;
    this.hasher = hasher;
  }
  
  @Override
  public void run() {
    if(!file.isEmpty()){
      try{
        String hash = store(file, objectStoreRootDir, hasher);
        result.setResult(hash);
      }catch(Exception e){
        logger.error("Failed to store file into object store.", e);
        result.setErrorResult(new InternalErrorException(e));
      }
    }else{
      result.setResult(NO_HASH);
      logger.warn("Received empty file to store.");
    }
  }
  
  protected String store(MultipartFile multipartFile, File dirToStore, Hasher hasherImpl) throws Exception{
    String hash = hasherImpl.hash(multipartFile.getInputStream());
    File storedFile = computeStoredLocation(dirToStore, hash);
    
    if(!storedFile.exists()){
      logger.debug("creating path [{}] if it does not already exist.", storedFile.getParent());
      Files.createDirectories(storedFile.getParentFile().toPath());
      logger.info("copying stream to {}", storedFile.toURI());
      Files.copy(file.getInputStream(), storedFile.toPath());
    }
    else{
      logger.info("Already stored file with hash {}, skipping.", hash);
    }
    
    return hash;
  }
}
