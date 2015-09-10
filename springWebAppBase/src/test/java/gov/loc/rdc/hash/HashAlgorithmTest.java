package gov.loc.rdc.hash;

import org.junit.Assert;
import org.junit.Test;

public class HashAlgorithmTest extends Assert{
  
  @Test
  public void testAlgorithmSupportedWithStringTrue(){
    boolean result = HashAlgorithm.algorithmSupported("sha256");
    assertTrue(result);
    result = HashAlgorithm.algorithmSupported("SHA256");
    assertTrue(result);
    result = HashAlgorithm.algorithmSupported("sha-256");
    assertTrue(result);
    result = HashAlgorithm.algorithmSupported("SHA-256");
    assertTrue(result);
  }
  
  @Test
  public void testAlgorithmSupportedWithStringFalse(){
    boolean result = HashAlgorithm.algorithmSupported("md5");
    assertFalse(result);
  }
  
  @Test
  public void testAlgorithmSupportedWithEnumTrue(){
    boolean result = HashAlgorithm.algorithmSupported(HashAlgorithm.SHA256);
    assertTrue(result);
  }
  
  @Test
  public void testAlgorithmSupportedWithEnumFalse(){
    boolean result = HashAlgorithm.algorithmSupported(HashAlgorithm.MD5);
    assertFalse(result);
  }
}
