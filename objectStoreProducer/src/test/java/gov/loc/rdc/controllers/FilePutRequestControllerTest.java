package gov.loc.rdc.controllers;

import java.io.File;
import java.io.FileInputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.rabbitmq.client.Channel;


public class FilePutRequestControllerTest extends Assert {
  private MockMvc mockMvc;
  private FilePutRequestController sut;
  
  @Before
  public void setup(){
    ThreadPoolTaskExecutor mockThreadPoolTaskExecutor = Mockito.mock(ThreadPoolTaskExecutor.class);
    sut = new FilePutRequestController();
    sut.setThreadExecutor(mockThreadPoolTaskExecutor);
    this.mockMvc = MockMvcBuilders.standaloneSetup(sut).build();
  }
  
  @Test
  public void testFilePutRequest() throws Exception{
    File testFile = new File(getClass().getClassLoader().getResource("emptyTestFile.txt").getFile());
    FileInputStream fis = new FileInputStream(testFile);
    MockMultipartFile multipartFile = new MockMultipartFile("file", fis);
    
    mockMvc.perform(MockMvcRequestBuilders.fileUpload("/v1/file/put/hash").file(multipartFile)).andExpect(MockMvcResultMatchers.status().isOk());
  }
  
  @Test
  public void testGoodSetup() throws Exception{
    Channel mockChannel = Mockito.mock(Channel.class);
    sut = Mockito.spy(new FilePutRequestController());
    Mockito.doReturn(mockChannel).when(sut).createChannel(Mockito.anyString()); 
    
    sut.setMqHost("localhost");
    sut.setMaxNumberOfMessagesToProcessConcurrently(1);
    sut.setup();
    Mockito.verify(mockChannel).queueDeclare("putLongTerm", true, false, false, null);
    Mockito.verify(mockChannel).queueDeclare("putAccess", true, false, false, null);
  }
  
  @Test
  public void testBadSetupDoesntThrowError(){
    sut.setup();
  }
}
