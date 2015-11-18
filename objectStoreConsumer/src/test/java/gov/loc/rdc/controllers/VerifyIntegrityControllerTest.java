package gov.loc.rdc.controllers;

import java.io.File;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import gov.loc.rdc.errors.InternalErrorException;
import gov.loc.rdc.hash.HashPathUtils;
import gov.loc.rdc.repositories.FileStoreMetadataRepository;

public class VerifyIntegrityControllerTest implements HashPathUtils{
  private VerifyIntegrityController sut;
  private MockMvc mockMvc;
  private File testDir;
  
  @Before
  public void setup(){
    sut = new VerifyIntegrityController();
    
    testDir = new File(getClass().getClassLoader().getResource("validObjectStore").getFile());
    sut.setObjectStoreRootDir(testDir);
    
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
    mockMvc.perform(MockMvcRequestBuilders.get("/v1/verifyintegrity")).andExpect(MockMvcResultMatchers.status().isOk());
    mockMvc.perform(MockMvcRequestBuilders.put("/v1/verifyintegrity")).andExpect(MockMvcResultMatchers.status().isOk());
    mockMvc.perform(MockMvcRequestBuilders.post("/v1/verifyintegrity")).andExpect(MockMvcResultMatchers.status().isOk());
  }
  
  @Test
  public void testRestfulVerifyIntegrityWithSpecifiedStartingDir() throws Exception{
    mockMvc.perform(MockMvcRequestBuilders.get("/v1/verifyintegrity").param("rootdir", testDir.getAbsolutePath())).andExpect(MockMvcResultMatchers.status().isOk());
    mockMvc.perform(MockMvcRequestBuilders.put("/v1/verifyintegrity").param("rootdir", testDir.getAbsolutePath())).andExpect(MockMvcResultMatchers.status().isOk());
    mockMvc.perform(MockMvcRequestBuilders.post("/v1/verifyintegrity").param("rootdir", testDir.getAbsolutePath())).andExpect(MockMvcResultMatchers.status().isOk());
  }
  
  @Test
  public void testRestfulVerifyIntegrityWithBlankStartingDir() throws Exception{
    mockMvc.perform(MockMvcRequestBuilders.get("/v1/verifyintegrity").param("rootdir", "")).andExpect(MockMvcResultMatchers.status().isOk());
    mockMvc.perform(MockMvcRequestBuilders.put("/v1/verifyintegrity").param("rootdir", "")).andExpect(MockMvcResultMatchers.status().isOk());
    mockMvc.perform(MockMvcRequestBuilders.post("/v1/verifyintegrity").param("rootdir", "")).andExpect(MockMvcResultMatchers.status().isOk());
  }
  
  @Test
  public void testRestfulVerifyIntegrityWithBadStartingDir() throws Exception{
    String badStartingDir = "/foo";
    mockMvc.perform(MockMvcRequestBuilders.get("/v1/verifyintegrity").param("rootdir", badStartingDir)).andExpect(MockMvcResultMatchers.status().isBadRequest());
    mockMvc.perform(MockMvcRequestBuilders.put("/v1/verifyintegrity").param("rootdir", badStartingDir)).andExpect(MockMvcResultMatchers.status().isBadRequest());
    mockMvc.perform(MockMvcRequestBuilders.post("/v1/verifyintegrity").param("rootdir", badStartingDir)).andExpect(MockMvcResultMatchers.status().isBadRequest());
  }
  
  @Test
  public void testRestfulVerifyIntegrityWithFileInsteadOfDir() throws Exception{
    File file = new File(getClass().getClassLoader().getResource("validObjectStore/b6/66/8c/f8/c4/6c/70/75/e1/82/15/d9/22/e7/81/2c/a0/82/fa/6c/c3/46/68/d0/0a/6c/20/ae/e4/55/1f/b6/b6668cf8c46c7075e18215d922e7812ca082fa6cc34668d00a6c20aee4551fb6").getFile());
    mockMvc.perform(MockMvcRequestBuilders.get("/v1/verifyintegrity").param("rootdir", file.getAbsolutePath())).andExpect(MockMvcResultMatchers.status().isBadRequest());
    mockMvc.perform(MockMvcRequestBuilders.put("/v1/verifyintegrity").param("rootdir", file.getAbsolutePath())).andExpect(MockMvcResultMatchers.status().isBadRequest());
    mockMvc.perform(MockMvcRequestBuilders.post("/v1/verifyintegrity").param("rootdir", file.getAbsolutePath())).andExpect(MockMvcResultMatchers.status().isBadRequest());
  }
  
  @Test
  public void testVerifyFilesExist(){
    FileStoreMetadataRepository mockFileStoreMetadataRepository = Mockito.mock(FileStoreMetadataRepository.class);
    Mockito.when(mockFileStoreMetadataRepository.getHashesForServer(Mockito.anyString())).thenReturn(Arrays.asList("b6668cf8c46c7075e18215d922e7812ca082fa6cc34668d00a6c20aee4551fb6"));
    sut.setFileStoreRepo(mockFileStoreMetadataRepository);
    
    sut.verifyFilesExist();
  }

}
