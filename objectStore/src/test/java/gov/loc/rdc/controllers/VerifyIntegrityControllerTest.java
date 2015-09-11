package gov.loc.rdc.controllers;

import gov.loc.rdc.hash.SHA256Hasher;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

public class VerifyIntegrityControllerTest {
  private VerifyIntegrityController sut;
  
  @Before
  public void setup(){
    sut = new VerifyIntegrityController();
    
    File testDir = new File(getClass().getClassLoader().getResource("testRepoDir").getFile());
    sut.setObjectStoreRootDir(testDir);
    
    sut.setHasher(new SHA256Hasher());
  }

  
  @Test
  public void testVerifyIntegrity(){
    sut.verifyIntegrity();
  }

}
