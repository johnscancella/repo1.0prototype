package gov.loc.rdc.controllers;

import java.io.File;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gov.loc.rdc.errors.InternalErrorException;
import gov.loc.rdc.errors.MissingParametersException;
import gov.loc.rdc.hash.HashPathUtils;
import gov.loc.rdc.hash.SHA256Hasher;
import gov.loc.rdc.host.HostUtils;
import gov.loc.rdc.repositories.FileStoreMetadataRepository;

/**
 * Responsible for continuously and programmically verifying that files haven't become corrupt. 
 */
@RestController
public class VerifyIntegrityController implements HashPathUtils, HostUtils{
  private static final Logger logger = LoggerFactory.getLogger(VerifyIntegrityController.class);
  
  @Autowired
  private FileStoreMetadataRepository fileStoreRepo;
  
  @Value("${rootDir:/tmp}")
  private File objectStoreRootDir;
  
  @RequestMapping(value="/v1/verifyintegrity", method={RequestMethod.GET, RequestMethod.PUT, RequestMethod.POST})
  public void restfulVerifyIntegrity(@RequestParam(value="rootdir", required=false) String rootDir){
    if(rootDir == null || rootDir.equals("")){
      logger.debug("rootDir not supplied during http request. Defaulting to [{}]", objectStoreRootDir);
      scan(objectStoreRootDir);
      return;
    }
    
    File startingDir = new File(rootDir);
    if(startingDir.exists() && startingDir.isDirectory()){
      scan(startingDir);
    }
    else{
      throw new MissingParametersException("[" + rootDir + "] is not a directory. Please supply a starting directory for integrity verification");
    }
  }

  //default to once a month
  @Scheduled(cron = "${integrity-wait-cron:0 0 0 * * *}")
  public void verifyIntegrity() {
    scan(objectStoreRootDir);
    verifyFilesExist();
  }
  
  protected void scan(File StartingDir){
    if(StartingDir.exists()){
      logger.info("Starting integrity verification on [{}] directory", StartingDir);
      try {
        Files.walk(StartingDir.toPath(), FileVisitOption.FOLLOW_LINKS).filter(path -> path.toFile().isFile()).forEach((path) -> visitFile(path));
      }
      catch (Exception e) {
        logger.error("Failed to walk tree", e);
        throw new InternalErrorException(e);
      }
      logger.info("Finished integrity verification on [{}] directory", StartingDir);
    }
  }

  protected void visitFile(Path path) {
    try {
      String hash = SHA256Hasher.hash(Files.newInputStream(path, StandardOpenOption.READ));
      boolean isVerified = hash.equals(path.toFile().getName());
      if (!isVerified) {
        StringBuilder sb = new StringBuilder();
        sb.append("Found integrity error with file ").append(path).append("]. Computed hash is [").append(hash).append("]");
        logger.error(sb.toString());
        throw new InternalErrorException(sb.toString());
      }
      logger.debug("Verified [{}] matches computed hash.", path);
    }
    catch (Exception e) {
      logger.error("Failed to compute hash for file [{}]", path, e);
      throw new InternalErrorException(e);
    }
  }
  
  /**
   * verify that all files that should exist on this server do.
   */
  protected void verifyFilesExist(){
    try{
      String server = getHostName();
      List<String> hashes = fileStoreRepo.getHashesForServer(server);
      for(String hash : hashes){
        File storedFile = computeStoredFileLocation(objectStoreRootDir, hash);
        if(storedFile.exists()){
          logger.debug("Verified that file [{}] exists.", storedFile);
        }
        else{
          logger.error("Could not find file [{}] on server [{}]!", hash, server);
        }
      }
    }
    catch(Exception e){
      logger.error("Could not verify files that should exist on this server do exist.", e);
    }
    
    
  }

  // for testing only
  protected void setObjectStoreRootDir(File objectStoreRootDir) {
    this.objectStoreRootDir = objectStoreRootDir;
  }
}
