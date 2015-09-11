package gov.loc.rdc.controllers;

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
import org.springframework.stereotype.Component;

@Component
public class VerifyIntegrityController {
  private static final Logger logger = LoggerFactory.getLogger(VerifyIntegrityController.class);

  @Value("${rootDir:/tmp}")
  private File objectStoreRootDir;

  @Autowired
  private Hasher hasher;

  @Scheduled(cron = "${integrity-wait-cron:0 0 0 * * *}")
  public void verifyIntegrity() {
    logger.info("Starting integrity verification on [{}] directory", objectStoreRootDir);
    try {
      Files.walk(objectStoreRootDir.toPath(), FileVisitOption.FOLLOW_LINKS).filter(path -> path.toFile().isFile()).forEach((path) -> visitFile(path));
    }
    catch (Exception e) {
      logger.error("Failed to walk tree", e);
      // TODO some other notification?
    }
    logger.info("Finished integrity verification on [{}] directory", objectStoreRootDir);
  }

  private void visitFile(Path path) {
    try {
      String hash = hasher.hash(Files.newInputStream(path, StandardOpenOption.READ));
      boolean isVerified = hash.equals(path.toFile().getName());
      if (!isVerified) {
        logger.error("Found integrity error with file [{}]. Computed hash is [{}]", path, hash);
        // TODO some other notification?
      }
      else {
        logger.debug("Verified [{}] matches computed hash.", path);
      }
    }
    catch (Exception e) {
      logger.error("Failed to compute hash for file [{}]", path, e);
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
