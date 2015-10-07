package gov.loc.rdc.tasks;

import gov.loc.rdc.errors.InternalErrorException;
import gov.loc.rdc.errors.ResourceNotFoundException;
import gov.loc.rdc.hash.HashPathUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.springframework.web.context.request.async.DeferredResult;

/**
 * responsible for getting a file from the object store
 */
public class RetrieveFileTask extends AbstractFileInfoTask implements Runnable, HashPathUtils {
  private final DeferredResult<byte[]> result;

  public RetrieveFileTask(DeferredResult<byte[]> result, File objectStoreRootDir, String algorithm, String hash) {
    super(objectStoreRootDir, algorithm, hash);
    this.result = result;
  }

  @Override
  public void doTaskWork() {
    logger.debug("Searching for file with hash [{}].", hash);
    File storedFile = computeStoredLocation(objectStoreRootDir, hash);

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

  @Override
  protected DeferredResult<?> getResult() {
    return result;
  }

}
