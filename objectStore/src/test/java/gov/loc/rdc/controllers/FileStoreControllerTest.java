package gov.loc.rdc.controllers;

import gov.loc.rdc.errors.InternalErrorException;
import gov.loc.rdc.errors.ResourceNotFoundException;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
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
  public TemporaryFolder folder = new TemporaryFolder();

  @Before
  public void setup() throws Exception {
    MockitoAnnotations.initMocks(this);
    FileStoreController fileStoreController = new FileStoreController();
    fileStoreController.setObjectStoreRootDir(folder.newFolder());
    fileStoreController.setThreadExecutor(new MockThreadpool());
    this.mockMvc = MockMvcBuilders.standaloneSetup(fileStoreController).build();
  }

  @Test
  public void testStoringNonZeroLengthFile() throws Exception {
    storeData();
  }

  @Test
  public void testStoringZeroLengthFile() throws Exception {
    ClassLoader classLoader = getClass().getClassLoader();
    File testFile = new File(classLoader.getResource("emptyTestFile.txt").getFile());
    FileInputStream fis = new FileInputStream(testFile);
    MockMultipartFile multipartFile = new MockMultipartFile("file", fis);

    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.fileUpload("/storefile").file(multipartFile))
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    assertEquals("Hash was not computed.", result.getAsyncResult());
  }

  @Test
  public void testGettingNonexistingFile() throws Exception {
    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/getfile/123ABC")).andExpect(MockMvcResultMatchers.status().isOk())
        .andReturn();
    assertEquals(ResourceNotFoundException.class, result.getAsyncResult().getClass());
  }

  @Test
  public void testSavingSameFileTwice() throws Exception {
    storeData();
    storeData();
  }

  @Test
  public void testThrowingErrorWithFilesystem() throws Exception {
    FileStoreController fileStoreController = new FileStoreController();
    fileStoreController.setObjectStoreRootDir(new File("/foo"));
    fileStoreController.setThreadExecutor(new MockThreadpool());
    mockMvc = MockMvcBuilders.standaloneSetup(fileStoreController).build();

    ClassLoader classLoader = getClass().getClassLoader();
    File testFile = new File(classLoader.getResource("testFile.txt").getFile());
    FileInputStream fis = new FileInputStream(testFile);
    MockMultipartFile multipartFile = new MockMultipartFile("file", fis);

    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.fileUpload("/storefile").file(multipartFile)).andReturn();
    assertEquals(InternalErrorException.class, result.getAsyncResult().getClass());
  }

  @Test
  public void testGettingExistingFile() throws Exception {
    URL url = getClass().getClassLoader().getResource("testFile.txt");
    String hash = storeData();
    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/getfile/" + hash)).andExpect(MockMvcResultMatchers.status().isOk())
        .andReturn();
    assertArrayEquals(Files.readAllBytes(Paths.get(url.toURI())), (byte[]) result.getAsyncResult());
  }

  private String storeData() throws Exception {
    ClassLoader classLoader = getClass().getClassLoader();
    File testFile = new File(classLoader.getResource("testFile.txt").getFile());
    FileInputStream fis = new FileInputStream(testFile);
    MockMultipartFile multipartFile = new MockMultipartFile("file", fis);

    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.fileUpload("/storefile").file(multipartFile))
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    return (String) result.getAsyncResult();
  }
  
  @Test
  public void testIfFileExists() throws Exception{
    String hash = storeData();
    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/fileexists/" + hash)).andExpect(MockMvcResultMatchers.status().isOk())
        .andReturn();
    assertEquals(true, result.getAsyncResult());
  }

  private static class MockThreadpool extends ThreadPoolTaskExecutor {
    private static final long serialVersionUID = 1L;

    @Override
    public void execute(Runnable task) {
      task.run();
    }
  }
}
