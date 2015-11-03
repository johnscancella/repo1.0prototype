package gov.loc.rdc.tasks;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractFileInfoTask{
  protected static final Logger logger = LoggerFactory.getLogger(AbstractFileInfoTask.class);

  protected final File objectStoreRootDir;
  protected final String hash;

  public AbstractFileInfoTask(File objectStoreRootDir, String hash) {
    this.objectStoreRootDir = objectStoreRootDir;
    this.hash = hash;
  }
}
