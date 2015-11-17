package gov.loc.rdc.controllers;

import java.util.HashMap;
import java.util.Map;

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

import gov.loc.rdc.app.Properties;

public class FilePullRequestControllerTest extends Assert {
  private MockMvc mockMvc;
  private FilePullRequestController sut;
  private Properties mockProperties;
  
  @Before
  public void setup(){
    ThreadPoolTaskExecutor mockThreadPoolTaskExecutor = Mockito.mock(ThreadPoolTaskExecutor.class);
    mockProperties = Mockito.mock(Properties.class);
    sut = new FilePullRequestController();
    sut.setThreadExecutor(mockThreadPoolTaskExecutor);
    sut.setProperties(mockProperties);
    this.mockMvc = MockMvcBuilders.standaloneSetup(sut).build();
  }
  
  @Test
  public void testFilePullRequest() throws Exception{
    mockMvc.perform(MockMvcRequestBuilders.put("/v1/filepull/server").param("file", "/foo/bar/ham.txt")).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
  }
  
  @Test
  public void testGoodSetup() throws Exception{
    Channel mockChannel = Mockito.mock(Channel.class);
    sut = Mockito.spy(new FilePullRequestController());
    Mockito.doReturn(mockChannel).when(sut).createChannel(Mockito.anyString()); 
    
    Properties props = new Properties();
    Map<String, Integer> storageTypesToCopiesMap = new HashMap<>();
    storageTypesToCopiesMap.put("longterm", 2);
    props.setStorageTypesToCopiesMap(storageTypesToCopiesMap);
    
    sut.setMqHost("localhost");
    sut.setProperties(props);
    sut.setMaxNumberOfMessagesToProcessConcurrently(1);
    sut.setup();
    Mockito.verify(mockChannel).queueDeclare("longterm", true, false, false, null);
  }
  
  @Test
  public void testBadSetupDoesntThrowError(){
    sut.setup();
  }
}
