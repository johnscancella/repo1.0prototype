package gov.loc.rdc.hash;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Test;

public class HashPathUtilsTest extends Assert implements HashPathUtils{

  @Test
  public void testComputeStoredFileLocation(){
    File rootDir = new File("/tmp");
    String hash = "ABC123";
    File expectedFile = new File("/tmp/AB/C1/23/ABC123");
    File actualFile = computeStoredFileLocation(rootDir, hash);
    assertEquals(expectedFile, actualFile);
  }
  
  @Test
  public void testComputeStoredPathLocation(){
    File rootDir = new File("/tmp");
    String hash = "ABC123";
    Path expectedPath = Paths.get("/tmp/AB/C1/23/ABC123");
    Path actualPath = computeStoredPathLocation(rootDir, hash);
    assertEquals(expectedPath, actualPath);
  }
}
