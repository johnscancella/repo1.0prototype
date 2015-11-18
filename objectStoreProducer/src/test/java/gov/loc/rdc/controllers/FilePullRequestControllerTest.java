package gov.loc.rdc.controllers;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.rabbitmq.client.Channel;


public class FilePullRequestControllerTest extends Assert {
  private MockMvc mockMvc;
  private FilePullRequestController sut;
  
  @Before
  public void setup(){
    ThreadPoolTaskExecutor mockThreadPoolTaskExecutor = Mockito.mock(ThreadPoolTaskExecutor.class);
    sut = new FilePullRequestController();
    sut.setThreadExecutor(mockThreadPoolTaskExecutor);
    this.mockMvc = MockMvcBuilders.standaloneSetup(sut).build();
  }
  
  @Test
  public void testFilePullRequest() throws Exception{
    mockMvc.perform(MockMvcRequestBuilders.put("/v1/filepull/server/hash").param("file", "/foo/bar/ham.txt")).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
  }
  
  @Test
  public void testGoodSetup() throws Exception{
    Channel mockChannel = Mockito.mock(Channel.class);
    sut = Mockito.spy(new FilePullRequestController());
    Mockito.doReturn(mockChannel).when(sut).createChannel(Mockito.anyString()); 
    
    sut.setMqHost("localhost");
    sut.setMaxNumberOfMessagesToProcessConcurrently(1);
    sut.setup();
    Mockito.verify(mockChannel).queueDeclare("scpLongTerm", true, false, false, null);
    Mockito.verify(mockChannel).queueDeclare("scpAccess", true, false, false, null);
  }
  
  @Test
  public void testBadSetupDoesntThrowError(){
    sut.setup();
  }
}
