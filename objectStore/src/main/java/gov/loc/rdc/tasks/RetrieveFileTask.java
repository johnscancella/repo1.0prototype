package gov.loc.rdc.tasks;

import gov.loc.rdc.errors.InternalError;
import gov.loc.rdc.errors.ResourceNotFoundException;
import gov.loc.rdc.errors.UnsupportedAlgorithm;
import gov.loc.rdc.hash.HashAlgorithm;
import gov.loc.rdc.hash.HashPathUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.async.DeferredResult;

/**
 * responsible for storing a file in the object store and returning the file
 * hash
 */
public class RetrieveFileTask implements Runnable, HashPathUtils {
  private static final Logger logger = LoggerFactory.getLogger(RetrieveFileTask.class);

  private DeferredResult<byte[]> result;
  private File objectStoreRootDir;
  String algorithm;
  private String hash;

  public RetrieveFileTask(DeferredResult<byte[]> result, File objectStoreRootDir, String algorithm, String hash) {
    this.result = result;
    this.objectStoreRootDir = objectStoreRootDir;
    this.algorithm = algorithm;
    this.hash = hash;
  }

  @Override
  public void run() {
    if (!HashAlgorithm.algorithmSupported(algorithm)) {
      logger.info("User tried to get stored file using unsupported hashing algorithm [{}]", algorithm);
      result.setErrorResult(new UnsupportedAlgorithm("Only sha256 is currently supported for hashing algorithm"));
      return;
    }

    logger.debug("Searching for file with hash [{}].", hash);
    File storedFile = computeStoredLocation(objectStoreRootDir, hash);

    if (storedFile.exists()) {
      logger.debug("Found stored file that matches hash [{}].", hash);
      try {
        result.setResult(Files.readAllBytes(storedFile.toPath()));
      }
      catch (IOException e) {
        logger.error("Unable to read file [{}]", storedFile.toURI(), e);
        result.setErrorResult(new InternalError(e));
      }
   }
    else {
      logger.warn("Unable to find stored file [{}]. Returning 404 instead.", storedFile.toURI());
      result.setErrorResult(new ResourceNotFoundException());
    }
  }

}
