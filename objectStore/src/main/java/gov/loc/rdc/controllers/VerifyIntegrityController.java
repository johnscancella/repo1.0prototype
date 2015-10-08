package gov.loc.rdc.controllers;

import gov.loc.rdc.errors.InternalErrorException;
import gov.loc.rdc.errors.MissingParametersException;
import gov.loc.rdc.hash.Hasher;

import java.io.File;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VerifyIntegrityController implements VerifyIntegrityControllerApi{
  private static final Logger logger = LoggerFactory.getLogger(VerifyIntegrityControllerApi.class);

  @Value("${rootDir:/tmp}")
  private File objectStoreRootDir;

  @Autowired
  private Hasher hasher;
  
  @Override
  @RequestMapping(value=RequestMappings.VERIFY_INTEGRITY_URL, method={RequestMethod.GET, RequestMethod.PUT, RequestMethod.POST})
  public void restfulVerifyIntegrity(@RequestParam(required=false) String rootDir){
    if(rootDir == null || rootDir.equals("")){
      logger.debug("rootDir not supplied during http request. Defaulting to [{}]", objectStoreRootDir);
      scan(objectStoreRootDir);
      return;
    }
    
    File startingDir = new File(rootDir);
    if(startingDir.exists() && startingDir.isDirectory()){
      scan(objectStoreRootDir);
    }
    else{
      throw new MissingParametersException("[" + rootDir + "] is not a directory. Please supply a starting directory for integrity verification");
    }
  }

  @Scheduled(cron = "${integrity-wait-cron:0 0 0 * * *}")
  public void verifyIntegrity() {
    scan(objectStoreRootDir);
  }
  
  protected void scan(File StartingDir){
    logger.info("Starting integrity verification on [{}] directory", StartingDir);
    try {
      Files.walk(StartingDir.toPath(), FileVisitOption.FOLLOW_LINKS).filter(path -> path.toFile().isFile()).forEach((path) -> visitFile(path));
    }
    catch (Exception e) {
      logger.error("Failed to walk tree", e);
      throw new InternalErrorException(e);
      // TODO some other notification?
    }
    logger.info("Finished integrity verification on [{}] directory", StartingDir);
  }

  protected void visitFile(Path path) {
    try {
      String hash = hasher.hash(Files.newInputStream(path, StandardOpenOption.READ));
      boolean isVerified = hash.equals(path.toFile().getName());
      if (!isVerified) {
        StringBuilder sb = new StringBuilder();
        sb.append("Found integrity error with file ").append(path).append("]. Computed hash is [").append(hash).append("]");
        logger.error(sb.toString());
        throw new InternalErrorException(sb.toString());
        // TODO some other notification?
      }
      logger.debug("Verified [{}] matches computed hash.", path);
    }
    catch (Exception e) {
      logger.error("Failed to compute hash for file [{}]", path, e);
      throw new InternalErrorException(e);
      // TODO some other notification
    }
  }

  // for testing only
  protected void setObjectStoreRootDir(File objectStoreRootDir) {
    this.objectStoreRootDir = objectStoreRootDir;
  }

  // for testing only
  protected void setHasher(Hasher hasher) {
    this.hasher = hasher;
  }
}
