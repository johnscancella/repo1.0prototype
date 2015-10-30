package gov.loc.rdc.controllers;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class MessageQueueControllerTest extends Assert{
  private MessageQueueController sut;
  
  @Before
  public void setup() throws Exception{
    sut = new MessageQueueController(2, "localhost", 1);
    sut.setup();
  }
  
  @Test
  public void testGetNextQueueNameRollsOver(){
    String name = sut.getNextQueueName();
    assertEquals("fileSendingQueue2", name);
    
    name = sut.getNextQueueName();
    assertEquals("fileSendingQueue1", name);
    
    name = sut.getNextQueueName();
    assertEquals("fileSendingQueue2", name);
  }
  
  @Test
  public void testGetNextQueueName() throws Exception{
    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(sut).build();
    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(RequestMappings.GET_FILE_STORE_QUEUE_NAME_URL)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    assertEquals("fileSendingQueue2", result.getResponse().getContentAsString());
  }
}
