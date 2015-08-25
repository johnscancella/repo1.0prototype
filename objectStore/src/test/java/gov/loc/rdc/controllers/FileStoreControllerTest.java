package gov.loc.rdc.controllers;

import gov.loc.rdc.errors.InternalError;
import gov.loc.rdc.hash.Hasher;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class FileStoreControllerTest extends Assert {
  private MockMvc mockMvc;
  
  @Rule
  public TemporaryFolder folder= new TemporaryFolder();
  
  @Mock
  private Hasher mockHasher;
  
  @Before
  public void setup() throws Exception {
    MockitoAnnotations.initMocks(this);
    FileStoreController fileStoreController = new FileStoreController();
    fileStoreController.setHasher(mockHasher);
    fileStoreController.setObjectStoreRootDir(folder.newFolder());
    fileStoreController.setThreadExecutor(new MockThreadpool());
    this.mockMvc = MockMvcBuilders.standaloneSetup(fileStoreController).build();
  }
  
  @Test
  public void testStoringNonZeroLengthFile() throws Exception{
    storeData();
  }
  
  @Test
  public void testStoringZeroLengthFile() throws Exception{
    ClassLoader classLoader = getClass().getClassLoader();
    File testFile = new File(classLoader.getResource("emptyTestFile.txt").getFile());
    FileInputStream fis = new FileInputStream(testFile);
    MockMultipartFile multipartFile = new MockMultipartFile("file", fis);
    
    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.fileUpload("/store")
      .file(multipartFile))
      .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    assertEquals("Hash was not computed.", result.getAsyncResult());
  }
  
  @Test
  public void testGettingNonexistingFile() throws Exception{
    mockMvc.perform(MockMvcRequestBuilders.get("/get/sha256/123ABC"))
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }
  
  @Test
  public void testBadAlgorithm() throws Exception{
    mockMvc.perform(MockMvcRequestBuilders.get("/get/foo/123ABC"))
    .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }
  
  @Test
  public void testSavingSameFileTwice() throws Exception{
    storeData();
    storeData();
  }
  
  @Test
  public void testThrowingErrorWithFilesystem() throws Exception {
    FileStoreController fileStoreController = new FileStoreController();
    fileStoreController.setHasher(mockHasher);
    fileStoreController.setObjectStoreRootDir(new File("/foo"));
    fileStoreController.setThreadExecutor(new MockThreadpool());
    mockMvc = MockMvcBuilders.standaloneSetup(fileStoreController).build();
    
    ClassLoader classLoader = getClass().getClassLoader();
    File testFile = new File(classLoader.getResource("testFile.txt").getFile());
    String mockHash = "123ABC";
    Mockito.when(mockHasher.hash(Mockito.any(InputStream.class))).thenReturn(mockHash);
    FileInputStream fis = new FileInputStream(testFile);
    MockMultipartFile multipartFile = new MockMultipartFile("file", fis);
    
    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.fileUpload("/store")
      .file(multipartFile))
          .andReturn();
    assertEquals(InternalError.class, result.getAsyncResult().getClass());
  }
  
  @Test
  public void testGettingExistingFile() throws Exception{
    storeData();
    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/get/sha256/123ABC"))
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    assertEquals("this is a test file that will be used during testing and contains text.", 
        result.getResponse().getContentAsString());
  }
  
  @Test
  public void testStoringAsync() throws Exception{
    ClassLoader classLoader = getClass().getClassLoader();
    File testFile = new File(classLoader.getResource("testFile.txt").getFile());
    String mockHash = "123ABC";
    Mockito.when(mockHasher.hash(Mockito.any(InputStream.class))).thenReturn(mockHash);
    FileInputStream fis = new FileInputStream(testFile);
    MockMultipartFile multipartFile = new MockMultipartFile("file", fis);
    
    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.fileUpload("/store")
      .file(multipartFile))
      .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    assertEquals(mockHash, result.getAsyncResult());
  }
  
  private void storeData() throws Exception{
    ClassLoader classLoader = getClass().getClassLoader();
    File testFile = new File(classLoader.getResource("testFile.txt").getFile());
    String mockHash = "123ABC";
    Mockito.when(mockHasher.hash(Mockito.any(InputStream.class))).thenReturn(mockHash);
    FileInputStream fis = new FileInputStream(testFile);
    MockMultipartFile multipartFile = new MockMultipartFile("file", fis);
    
    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.fileUpload("/store")
      .file(multipartFile))
      .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    assertEquals(mockHash, result.getAsyncResult());
  }
  
  private static class MockThreadpool extends ThreadPoolTaskExecutor {
    private static final long serialVersionUID = 1L;
    
    @Override
    public void execute(Runnable task){
      task.run();
    }
  }
}
