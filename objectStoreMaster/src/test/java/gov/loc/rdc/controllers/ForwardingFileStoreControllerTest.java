package gov.loc.rdc.controllers;

import gov.loc.rdc.entities.FileStoreData;
import gov.loc.rdc.repositories.FileStoreMetadataRepository;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

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
  private ServerRegistraController mockServerRegistraController;
  
  @Mock
  private ThreadPoolTaskExecutor mockThreadPoolTaskExecutor;
  
  @Mock
  private FileStoreMetadataRepository MockfileStoreRepo;
  
  @Mock
  private MessageQueueController mockMessageQueueController;
  
  @Before
  public void setup(){
    sut = new ForwardingFileStoreController();
    sut.setServerRegistraController(mockServerRegistraController);
    sut.setThreadExecutor(mockThreadPoolTaskExecutor);
    sut.setFileStoreRepo(MockfileStoreRepo);
    sut.setMessageQueueController(mockMessageQueueController);
    mockMvc = MockMvcBuilders.standaloneSetup(sut).build();
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void testGetFile() throws Exception{
    Mockito.when(MockfileStoreRepo.get("foo")).thenReturn(new FileStoreData());
    Mockito.when(mockServerRegistraController.getUrls(Mockito.anySet())).thenReturn(new ArrayList<>());
    mockMvc.perform(MockMvcRequestBuilders.get("/getfile/foo")).andExpect(MockMvcResultMatchers.status().isOk());
    
    Mockito.verify(mockThreadPoolTaskExecutor).execute(Mockito.any(Runnable.class));
  }
  
  @Test
  public void testStoreFile() throws Exception{
    Mockito.when(mockMessageQueueController.getFileSendingQueueNames()).thenReturn(new HashSet<>(Arrays.asList("foo")));
    File testFile = new File(getClass().getClassLoader().getResource("emptyTestFile.txt").getFile());
    FileInputStream fis = new FileInputStream(testFile);
    MockMultipartFile multipartFile = new MockMultipartFile("file", fis);
    
    mockMvc.perform(MockMvcRequestBuilders.fileUpload("/storefile").file(multipartFile)).andExpect(MockMvcResultMatchers.status().isOk());
    Mockito.verify(mockThreadPoolTaskExecutor).execute(Mockito.any(Runnable.class));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void testFileExists() throws Exception{
    Mockito.when(MockfileStoreRepo.get("foo")).thenReturn(new FileStoreData());
    Mockito.when(mockServerRegistraController.getUrls(Mockito.anySet())).thenReturn(new ArrayList<>());
    
    mockMvc.perform(MockMvcRequestBuilders.get("/fileexists/foo")).andExpect(MockMvcResultMatchers.status().isOk());
    
    Mockito.verify(mockThreadPoolTaskExecutor).execute(Mockito.any(Runnable.class));
  }

}
