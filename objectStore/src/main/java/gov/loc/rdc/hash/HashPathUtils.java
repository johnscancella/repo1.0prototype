package gov.loc.rdc.hash;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * sharable methods for dealing with paths based on hashes.
 */
public interface HashPathUtils {
  public static final Logger logger = LoggerFactory.getLogger(HashPathUtils.class);
  public static final String TWO_LETTER_REGEX = "(?<=\\G.{2})";
  
  public default File computeStoredLocation(File rootDir, String hash){
    String[] splits = hash.split(TWO_LETTER_REGEX);
    StringBuilder sb = new StringBuilder();
    
    for(String dir : splits){
      sb.append(dir).append(File.separator);
    }
    sb.append(hash);
    
    File computedLocation = new File(rootDir, sb.toString());
    logger.debug("Computed the new file location to be [{}].", computedLocation.toURI());

    return computedLocation;
  }
}
