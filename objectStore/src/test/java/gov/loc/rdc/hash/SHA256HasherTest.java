package gov.loc.rdc.hash;

import java.io.File;
import java.io.FileInputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SHA256HasherTest extends Assert {
  
  private SHA256Hasher sut;
  
  @Before
  public void setup(){
    sut = new SHA256Hasher();
  }

  @Test
  public void testHash() throws Exception{
    ClassLoader classLoader = getClass().getClassLoader();
    File testFile = new File(classLoader.getResource("testFile.txt").getFile());
    FileInputStream fis = null;
    try{
      fis = new FileInputStream(testFile);
      
      String hash = sut.hash(fis);
      assertNotNull(hash);
      assertEquals("cfc3692881169aca9c487aabcc24d973617a5ba1f9483496ade6281994809d73", hash);
    }
    finally{
      if(fis != null){
        fis.close();
      }
    }
  }
}
