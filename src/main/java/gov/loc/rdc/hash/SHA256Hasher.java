package gov.loc.rdc.hash;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.Formatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SHA256Hasher implements Hasher {
  private static final Logger logger = LoggerFactory.getLogger(SHA256Hasher.class);

  @Override
  public String hash(File file) throws Exception {
    MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
    String hash = hash(file, messageDigest);
    logger.debug("Computed SHA256 hash to be {} for file {}", hash, file.toURI());
    
    return hash;
  }

  protected String hash(final File file, final MessageDigest messageDigest) throws IOException {
    try (InputStream is = new BufferedInputStream(new FileInputStream(file))) {
      final byte[] buffer = new byte[1024];
      for (int read = 0; (read = is.read(buffer)) != -1;) {
        messageDigest.update(buffer, 0, read);
      }
    }

    // Convert the byte to hex format
    try (Formatter formatter = new Formatter()) {
      for (final byte b : messageDigest.digest()) {
        formatter.format("%02x", b);
      }
      return formatter.toString();
    }
  }

}
