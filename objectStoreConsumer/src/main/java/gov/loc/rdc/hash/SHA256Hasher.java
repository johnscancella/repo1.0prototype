package gov.loc.rdc.hash;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.Formatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility for getting the the SHA-256 hash. 
 */
public class SHA256Hasher {
  private static final Logger logger = LoggerFactory.getLogger(SHA256Hasher.class);
  
  public static String hash(final byte[] data) throws Exception{
    MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
    messageDigest.update(data);
    
    return formatMessageDigest(messageDigest);
  }

  public static String hash(InputStream inputStream) throws Exception {
    MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
    String hash = hash(inputStream, messageDigest);
    logger.debug("Computed SHA256 hash to be {}", hash);
    
    return hash;
  }

  protected static String hash(final InputStream inputStream, final MessageDigest messageDigest) throws IOException {
    try (InputStream is = new BufferedInputStream(inputStream)) {
      final byte[] buffer = new byte[1024];
      for (int read = 0; (read = is.read(buffer)) != -1;) {
        messageDigest.update(buffer, 0, read);
      }
    }

    // Convert the byte to hex format
    return formatMessageDigest(messageDigest);
  }
  
  protected static String formatMessageDigest(final MessageDigest messageDigest){
    try (Formatter formatter = new Formatter()) {
      for (final byte b : messageDigest.digest()) {
        formatter.format("%02x", b);
      }
      return formatter.toString();
    }
  }

}
