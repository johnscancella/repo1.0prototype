package gov.loc.rdc.hash;

import java.io.InputStream;

/**
 * Public interface for hashing an inputStream. 
 * This allows us to replace hashes in the future with a different algorthm if needed.
 */
public interface Hasher {
  /**
   * Return the hash as a String.
   * 
   * @param inputStream
   * @return
   * @throws Exception
   */
  public String hash(InputStream inputStream) throws Exception;
}
