package gov.loc.rdc.hash;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Test;

public class SHA256HasherTest extends Assert {
  private static final String TEST_HASH = "cfc3692881169aca9c487aabcc24d973617a5ba1f9483496ade6281994809d73";
  
  @Test
  public void testHashWithByteArray() throws Exception{
    URI testFileUri = getClass().getClassLoader().getResource("testFile.txt").toURI();
    Path testFilePath = Paths.get(testFileUri);
    String hash = SHA256Hasher.hash(Files.readAllBytes(testFilePath)); 
    assertNotNull(hash);
    assertEquals(TEST_HASH, hash);
  }
  
  @Test
  public void testHashWithInputStream() throws Exception{
    ClassLoader classLoader = getClass().getClassLoader();
    File testFile = new File(classLoader.getResource("testFile.txt").getFile());
    FileInputStream fis = new FileInputStream(testFile);
    
    String hash = SHA256Hasher.hash(fis);
    assertNotNull(hash);
    assertEquals(TEST_HASH, hash);
  }
  
  @Test(expected=IOException.class)
  public void testHashThrowException() throws Exception{
    FileInputStream fis = null;
    SHA256Hasher.hash(fis);
  }
}
