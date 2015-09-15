package gov.loc.rdc.controllers;

import gov.loc.rdc.errors.InternalErrorException;
import gov.loc.rdc.hash.SHA256Hasher;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class VerifyIntegrityControllerTest {
  private VerifyIntegrityController sut;
  private MockMvc mockMvc;
  private File testDir;
  
  @Before
  public void setup(){
    sut = new VerifyIntegrityController();
    
    testDir = new File(getClass().getClassLoader().getResource("validObjectStore").getFile());
    sut.setObjectStoreRootDir(testDir);
    
    sut.setHasher(new SHA256Hasher());
    this.mockMvc = MockMvcBuilders.standaloneSetup(sut).build();
  }

  
  @Test
  public void testVerifyIntegrity(){
    sut.verifyIntegrity();
  }
  
  @Test(expected=InternalErrorException.class)
  public void testInvalidFilesThrowError(){
    File badFilesDir = new File(getClass().getClassLoader().getResource("invalidObjectStore").getFile());
    sut.setObjectStoreRootDir(badFilesDir);
    sut.verifyIntegrity();
  }
  
  @Test
  public void testRestfulVerifyIntegrityWithDefaultDir() throws Exception{
    mockMvc.perform(MockMvcRequestBuilders.get("/verifyintegrity")).andExpect(MockMvcResultMatchers.status().isOk());
    mockMvc.perform(MockMvcRequestBuilders.put("/verifyintegrity")).andExpect(MockMvcResultMatchers.status().isOk());
    mockMvc.perform(MockMvcRequestBuilders.post("/verifyintegrity")).andExpect(MockMvcResultMatchers.status().isOk());
  }
  
  @Test
  public void testRestfulVerifyIntegrityWithSpecifiedStartingDir() throws Exception{
    mockMvc.perform(MockMvcRequestBuilders.get("/verifyintegrity").param("rootDir", testDir.getAbsolutePath())).andExpect(MockMvcResultMatchers.status().isOk());
    mockMvc.perform(MockMvcRequestBuilders.put("/verifyintegrity").param("rootDir", testDir.getAbsolutePath())).andExpect(MockMvcResultMatchers.status().isOk());
    mockMvc.perform(MockMvcRequestBuilders.post("/verifyintegrity").param("rootDir", testDir.getAbsolutePath())).andExpect(MockMvcResultMatchers.status().isOk());
  }
  
  @Test
  public void testRestfulVerifyIntegrityWithBlankStartingDir() throws Exception{
    mockMvc.perform(MockMvcRequestBuilders.get("/verifyintegrity").param("rootDir", "")).andExpect(MockMvcResultMatchers.status().isOk());
    mockMvc.perform(MockMvcRequestBuilders.put("/verifyintegrity").param("rootDir", "")).andExpect(MockMvcResultMatchers.status().isOk());
    mockMvc.perform(MockMvcRequestBuilders.post("/verifyintegrity").param("rootDir", "")).andExpect(MockMvcResultMatchers.status().isOk());
  }
  
  @Test
  public void testRestfulVerifyIntegrityWithBadStartingDir() throws Exception{
    String badStartingDir = "/foo";
    mockMvc.perform(MockMvcRequestBuilders.get("/verifyintegrity").param("rootDir", badStartingDir)).andExpect(MockMvcResultMatchers.status().isBadRequest());
    mockMvc.perform(MockMvcRequestBuilders.put("/verifyintegrity").param("rootDir", badStartingDir)).andExpect(MockMvcResultMatchers.status().isBadRequest());
    mockMvc.perform(MockMvcRequestBuilders.post("/verifyintegrity").param("rootDir", badStartingDir)).andExpect(MockMvcResultMatchers.status().isBadRequest());
  }
  
  @Test
  public void testRestfulVerifyIntegrityWithFileInsteadOfDir() throws Exception{
    File file = new File(getClass().getClassLoader().getResource("validObjectStore/dir1/b6668cf8c46c7075e18215d922e7812ca082fa6cc34668d00a6c20aee4551fb6").getFile());
    mockMvc.perform(MockMvcRequestBuilders.get("/verifyintegrity").param("rootDir", file.getAbsolutePath())).andExpect(MockMvcResultMatchers.status().isBadRequest());
    mockMvc.perform(MockMvcRequestBuilders.put("/verifyintegrity").param("rootDir", file.getAbsolutePath())).andExpect(MockMvcResultMatchers.status().isBadRequest());
    mockMvc.perform(MockMvcRequestBuilders.post("/verifyintegrity").param("rootDir", file.getAbsolutePath())).andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

}
