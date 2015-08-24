package gov.loc.rdc;

import gov.loc.rdc.hash.Hasher;

import java.io.File;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileStorer implements Runnable {
  private static final Logger logger = LoggerFactory.getLogger(FileStorer.class);
  private static final String TWO_LETTER_REGEX = "(?<=\\G.{2})";
  
  private final File file;
  private final File dirToStore;
  private final Hasher hasher;
  
  public FileStorer(final File file, final File dirToStore, final Hasher hasher){
    this.file = file;
    this.dirToStore = dirToStore;
    this.hasher = hasher;
  }

  @Override
  public void run() {
    String hash = "hash not computed";
    
    try {
      hash = hasher.hash(file);
      File storedFile = computeStoredLocation(dirToStore, hash);
      
      if(!storedFile.exists()){
        Files.createDirectories(storedFile.getParentFile().toPath());
        Files.copy(file.toPath(), storedFile.toPath());
      }
    }
    catch (Exception e) {
      logger.error("Unable to store file with hash [{}]", hash, e);
    }
  }
  
  protected File computeStoredLocation(File rootDir, String hash){
    String[] splits = hash.split(TWO_LETTER_REGEX);
    StringBuilder sb = new StringBuilder();
    
    for(String dir : splits){
      sb.append(dir).append(File.separator);
    }
    sb.append(hash);
    
    File computedLocation = new File(rootDir, sb.toString());
    logger.debug("Computed the new file location to be {}", file.toURI());

    return computedLocation;
  }

}
