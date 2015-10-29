package gov.loc.rdc.tasks;

import gov.loc.rdc.hash.HashPathUtils;

import java.io.File;

import org.springframework.web.context.request.async.DeferredResult;

/**
 * responsible for testing if a file exists on the system.
 */
public class RetrieveFileExistenceTask extends AbstractFileInfoTask implements Runnable, HashPathUtils {
  private final DeferredResult<Boolean> result;

  public RetrieveFileExistenceTask(DeferredResult<Boolean> result, File objectStoreRootDir, String hash) {
    super(objectStoreRootDir, hash);
    this.result = result;
  }

  @Override
  public void doTaskWork() {
    logger.debug("Searching for file with hash [{}].", hash);
    File storedFile = computeStoredLocation(objectStoreRootDir, hash);

    if (storedFile.exists()) {
      result.setResult(true);
    }
    else {
      result.setResult(false);
    }
  }

  @Override
  protected DeferredResult<?> getResult() {
    return result;
  }

}
