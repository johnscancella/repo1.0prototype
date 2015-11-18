package gov.loc.rdc.tasks;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.async.DeferredResult;

import gov.loc.rdc.errors.InternalErrorException;
import gov.loc.rdc.errors.ResourceNotFoundException;
import gov.loc.rdc.hash.HashPathUtils;

/**
 * responsible for getting a file from the object store
 */
public class RetrieveFileTask implements Runnable, HashPathUtils {
  private static final Logger logger = LoggerFactory.getLogger(RetrieveFileTask.class);
  private final DeferredResult<byte[]> result;
  private final File objectStoreRootDir;
  private final String hash;

  public RetrieveFileTask(DeferredResult<byte[]> result, File objectStoreRootDir, String hash) {
    this.result = result;
    this.objectStoreRootDir = objectStoreRootDir;
    this.hash = hash;
  }

  @Override
  public void run() {
    logger.debug("Searching for file with hash [{}].", hash);
    File storedFile = computeStoredFileLocation(objectStoreRootDir, hash);

    if (storedFile.exists()) {
      logger.debug("Found stored file that matches hash [{}].", hash);
      try {
        result.setResult(Files.readAllBytes(storedFile.toPath()));
      }
      catch (IOException e) {
        logger.error("Unable to read file [{}]", storedFile.toURI(), e);
        result.setErrorResult(new InternalErrorException(e));
      }
   }
    else {
      logger.warn("Unable to find stored file [{}]. Returning 404 instead.", storedFile.toURI());
      result.setErrorResult(new ResourceNotFoundException());
    }
  }
}
