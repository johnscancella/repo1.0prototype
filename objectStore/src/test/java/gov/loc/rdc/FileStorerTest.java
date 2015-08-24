package gov.loc.rdc;

import gov.loc.rdc.hash.Hasher;
import gov.loc.rdc.hash.SHA256Hasher;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class FileStorerTest extends Assert {
  private FileStorer sut;
  
  @Rule
  public TemporaryFolder folder= new TemporaryFolder();
  
  @Test
  public void testRun() throws IOException{
    ClassLoader classLoader = getClass().getClassLoader();
    File testFile = new File(classLoader.getResource("testFile.txt").getFile());
    
    Hasher hasher = new SHA256Hasher();
    File rootDir = folder.newFolder();
    sut = new FileStorer(testFile, rootDir, hasher);
    sut.run();
    
    File expectedFile = new File(rootDir, 
        "cf/c3/69/28/81/16/9a/ca/9c/48/7a/ab/cc/24/d9/73/61/7a/5b/a1/f9/48/34/96/ad/e6/28/19/94/80/9d/73/cfc3692881169aca9c487aabcc24d973617a5ba1f9483496ade6281994809d73");
    assertTrue(expectedFile.exists());
  }
}
