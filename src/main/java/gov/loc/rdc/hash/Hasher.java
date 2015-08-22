package gov.loc.rdc.hash;

import java.io.File;

public interface Hasher {
  public String hash(File file) throws Exception;
}
