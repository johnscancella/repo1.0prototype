package gov.loc.rdc.tasks;

import gov.loc.rdc.entities.FileStoreData;
import gov.loc.rdc.errors.InternalErrorException;
import gov.loc.rdc.hash.HashPathUtils;
import gov.loc.rdc.hash.SHA256Hasher;
import gov.loc.rdc.host.HostUtils;
import gov.loc.rdc.repositories.FileStoreMetadataRepository;

import java.io.File;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

/**
 * responsible for storing a file in the object store and returning the file hash
 */
public class StoreFileTask implements Runnable, HashPathUtils, HostUtils{
  private static final Logger logger = LoggerFactory.getLogger(StoreFileTask.class);
  private static final String NO_HASH = "Hash was not computed.";
  
  private final DeferredResult<String> result;
  private final MultipartFile file;
  private final File objectStoreRootDir;
  private final FileStoreMetadataRepository fileStoreRepo;
  
  public StoreFileTask(DeferredResult<String> result, MultipartFile file, File objectStoreRootDir, FileStoreMetadataRepository fileStoreRepo) {
    this.result = result;
    this.file = file;
    this.objectStoreRootDir = objectStoreRootDir;
    this.fileStoreRepo = fileStoreRepo;
  }
  
  @Override
  public void run() {
    if(!file.isEmpty()){
      try{
        String hash = store(file, objectStoreRootDir);
        String host = getHostName();
        fileStoreRepo.upsert(new FileStoreData(hash, host));
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
  
  protected String store(MultipartFile multipartFile, File dirToStore) throws Exception{
    String hash = SHA256Hasher.hash(multipartFile.getInputStream());
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
