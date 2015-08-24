package gov.loc.rdc.controllers;

import gov.loc.rdc.hash.Hasher;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileStoreController {
  private static final Logger logger = LoggerFactory.getLogger(FileStoreController.class);
  private static final String TWO_LETTER_REGEX = "(?<=\\G.{2})";
  private static final String NO_HASH = "Hash was not computed.";
  private static final List<String> ACCEPTED_HASH_ALGORITHMS = Arrays.asList("sha256", "sha-256");
  
  @Autowired
  private Hasher hasher;
  
  @Value("${rootDir:/tmp}")
  private File objectStoreRootDir;
  
  @PostConstruct
  public void info(){
    logger.info("Storing hashed files in [{}] directory", objectStoreRootDir.toURI());
  }
  
  @RequestMapping(value="/get/{algorithm}/{hash}", method=RequestMethod.GET)
  public File getFile(@PathVariable String algorithm, @PathVariable String hash){
    if(!ACCEPTED_HASH_ALGORITHMS.contains(algorithm)){
      logger.info("User tried to get stored file using unsupported hashing algorithm [{}]", algorithm);
      throw new UnsupportedAlgorithm("Only sha256 is currently supported for hashing algorithm");
    }
    
    logger.debug("Searching for file with hash [{}].", hash);
    File storedFile = computeStoredLocation(objectStoreRootDir, hash);
    
    if(storedFile.exists()){
      logger.debug("Found stored file that matches hash [{}].", hash);
      return storedFile;
    }
    
    logger.warn("Unable to find stored file [{}]. Returning 404 instead.", storedFile.toURI());
    throw new ResourceNotFoundException();
  }
  
  @RequestMapping(value="/store", method={RequestMethod.POST, RequestMethod.PUT})
  public String storeFile(@RequestParam(value="file") MultipartFile file){
    String hash = NO_HASH;
    
    if(!file.isEmpty()){
      try{
        hash = store(file.getInputStream(), objectStoreRootDir, hasher);
      }catch(Exception e){
        logger.error("Failed to store file into object store.", e);
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
      logger.error("Unable to store file with hash [{}].", hash, e);
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
    logger.debug("Computed the new file location to be [{}].", computedLocation.toURI());

    return computedLocation;
  }
  
  @ResponseStatus(value = HttpStatus.NOT_FOUND)
  public static class ResourceNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;
  }
  
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  public static class UnsupportedAlgorithm extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public UnsupportedAlgorithm(String message){
      super(message);
    }
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
