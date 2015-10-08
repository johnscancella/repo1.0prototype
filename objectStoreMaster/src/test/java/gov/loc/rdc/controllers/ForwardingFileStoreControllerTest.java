package gov.loc.rdc.controllers;

import java.io.File;
import java.io.FileInputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@RunWith(MockitoJUnitRunner.class)
public class ForwardingFileStoreControllerTest extends Assert {
  private MockMvc mockMvc;
  private ForwardingFileStoreController sut;
  
  @Mock
  private RoundRobinServerController mockRoundRobinServerController;
  
  @Mock
  private ThreadPoolTaskExecutor mockThreadPoolTaskExecutor;
  
  @Before
  public void setup(){
    sut = new ForwardingFileStoreController();
    sut.setRoundRobinServerController(mockRoundRobinServerController);
    sut.setThreadExecutor(mockThreadPoolTaskExecutor);
    mockMvc = MockMvcBuilders.standaloneSetup(sut).build();
  }
  
  @Test
  public void testGetFile() throws Exception{
    mockMvc.perform(MockMvcRequestBuilders.get("/getfile/SHA-256/foo")).andExpect(MockMvcResultMatchers.status().isOk());
    Mockito.verify(mockRoundRobinServerController).getAvailableServers();
    Mockito.verify(mockThreadPoolTaskExecutor).execute(Mockito.any(Runnable.class));
  }
  
  @Test
  public void testStoreFile() throws Exception{
    File testFile = new File(getClass().getClassLoader().getResource("emptyTestFile.txt").getFile());
    FileInputStream fis = new FileInputStream(testFile);
    MockMultipartFile multipartFile = new MockMultipartFile("file", fis);
    
    mockMvc.perform(MockMvcRequestBuilders.fileUpload("/storefile").file(multipartFile)).andExpect(MockMvcResultMatchers.status().isOk());
    Mockito.verify(mockRoundRobinServerController).getAvailableServers();
    Mockito.verify(mockThreadPoolTaskExecutor).execute(Mockito.any(Runnable.class));
  }
  
  @Test
  public void testFileExists() throws Exception{
    mockMvc.perform(MockMvcRequestBuilders.get("/fileexists/SHA-256/foo")).andExpect(MockMvcResultMatchers.status().isOk());
    Mockito.verify(mockRoundRobinServerController).getAvailableServers();
    Mockito.verify(mockThreadPoolTaskExecutor).execute(Mockito.any(Runnable.class));
  }

}
