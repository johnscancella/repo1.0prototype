package gov.loc.rdc.controllers;

import gov.loc.rdc.controllers.FileStoreController;
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
    this.mockMvc = MockMvcBuilders.standaloneSetup(fileStoreController).build();
  }
  
  @Test
  public void testNonZeroLengthFile() throws Exception{
    ClassLoader classLoader = getClass().getClassLoader();
    File testFile = new File(classLoader.getResource("testFile.txt").getFile());
    FileInputStream fis = new FileInputStream(testFile);
    String mockHash = "123ABC";
    Mockito.when(mockHasher.hash(Mockito.any(InputStream.class))).thenReturn(mockHash);
    
    MockMultipartFile multipartFile = new MockMultipartFile("file", fis);
    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.fileUpload("/store")
      .file(multipartFile))
      .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    assertEquals(mockHash, result.getResponse().getContentAsString());
  }
  
  @Test
  public void testZeroLengthFile() throws Exception{
    ClassLoader classLoader = getClass().getClassLoader();
    File testFile = new File(classLoader.getResource("emptyTestFile.txt").getFile());
    FileInputStream fis = new FileInputStream(testFile);
    
    MockMultipartFile multipartFile = new MockMultipartFile("file", fis);
    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.fileUpload("/store")
      .file(multipartFile))
      .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    assertEquals("Hash was not computed.", result.getResponse().getContentAsString());
  }
}
