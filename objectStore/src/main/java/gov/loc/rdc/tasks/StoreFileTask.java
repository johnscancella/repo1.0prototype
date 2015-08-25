package gov.loc.rdc.tasks;

import gov.loc.rdc.hash.HashUtils;
import gov.loc.rdc.hash.Hasher;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;
import gov.loc.rdc.errors.InternalError;

/**
 * responsible for storing a file in the object store and returning the file hash
 */
public class StoreFileTask implements Runnable, HashUtils{
  private static final Logger logger = LoggerFactory.getLogger(StoreFileTask.class);
  private static final String NO_HASH = "Hash was not computed.";
  
  private DeferredResult<String> result;
  private MultipartFile file;
  private File objectStoreRootDir;
  private Hasher hasher;
  
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
        String hash = store(file.getInputStream(), objectStoreRootDir, hasher);
        result.setResult(hash);
      }catch(Exception e){
        logger.error("Failed to store file into object store.", e);
        result.setErrorResult(new InternalError(e));
      }
    }else{
      result.setResult(NO_HASH);
      logger.warn("Received empty file to store.");
    }
  }
  
  protected String store(InputStream inputStream, File dirToStore, Hasher hasherImpl) throws Exception{
    String hash = hasherImpl.hash(inputStream);
    File storedFile = computeStoredLocation(dirToStore, hash);
    
    if(!storedFile.exists()){
      Files.createDirectories(storedFile.getParentFile().toPath());
      Files.copy(inputStream, storedFile.toPath());
    }
    
    return hash;
  }
}
