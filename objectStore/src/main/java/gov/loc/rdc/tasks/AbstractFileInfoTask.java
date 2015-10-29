package gov.loc.rdc.tasks;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.async.DeferredResult;

public abstract class AbstractFileInfoTask implements Runnable {
  protected static final Logger logger = LoggerFactory.getLogger(AbstractFileInfoTask.class);

  protected final File objectStoreRootDir;
  protected final String hash;

  public AbstractFileInfoTask(File objectStoreRootDir, String hash) {
    this.objectStoreRootDir = objectStoreRootDir;
    this.hash = hash;
  }

  @Override
  public void run() {
    doTaskWork();
  }

  protected abstract void doTaskWork();

  protected abstract DeferredResult<?> getResult();
}
