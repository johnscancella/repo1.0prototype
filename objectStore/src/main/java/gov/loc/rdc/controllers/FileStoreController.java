package gov.loc.rdc.controllers;

import gov.loc.rdc.hash.Hasher;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileStoreController {
  private static final Logger logger = LoggerFactory.getLogger(FileStoreController.class);
  private static final String TWO_LETTER_REGEX = "(?<=\\G.{2})";
  private static final String NO_HASH = "Hash was not computed.";
  
  @Autowired
  private Hasher hasher;
  
  @Value("${rootDir:/tmp}")
  private File objectStoreRootDir;
  
  @RequestMapping(value="/store", method=RequestMethod.POST)
  public String storeFile(@RequestParam(value="file") MultipartFile file){
    String hash = NO_HASH;
    
    if(!file.isEmpty()){
      try{
        hash = store(file.getInputStream(), objectStoreRootDir, hasher);
      }catch(Exception e){
        logger.error("Failed to store file into object store", e);
      }
    }else{
      logger.warn("Received empty file to store.");
    }
    
    return hash;
  }
  
  protected String store(InputStream inputStream, File dirToStore, Hasher hasherImpl){
    String hash = NO_HASH;
    
    try {
      hash = hasherImpl.hash(inputStream);
      File storedFile = computeStoredLocation(dirToStore, hash);
      
      if(!storedFile.exists()){
        Files.createDirectories(storedFile.getParentFile().toPath());
        Files.copy(inputStream, storedFile.toPath());
      }
    }
    catch (Exception e) {
      logger.error("Unable to store file with hash [{}]", hash, e);
    }
    
    return hash;
  }
  
  protected File computeStoredLocation(File rootDir, String hash){
    String[] splits = hash.split(TWO_LETTER_REGEX);
    StringBuilder sb = new StringBuilder();
    
    for(String dir : splits){
      sb.append(dir).append(File.separator);
    }
    sb.append(hash);
    
    File computedLocation = new File(rootDir, sb.toString());
    logger.debug("Computed the new file location to be {}", computedLocation.toURI());

    return computedLocation;
  }

  //only used in unit test
  protected void setHasher(Hasher hasher) {
    this.hasher = hasher;
  }

  //only used in unit test
  protected void setObjectStoreRootDir(File objectStoreRootDir) {
    this.objectStoreRootDir = objectStoreRootDir;
  }
}
