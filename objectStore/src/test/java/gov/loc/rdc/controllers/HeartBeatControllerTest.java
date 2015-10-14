package gov.loc.rdc.controllers;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class HeartBeatControllerTest extends Assert{
  private MockMvc mockMvc;
  
  @Before
  public void setup() {
    HeartBeatController controller = new HeartBeatController();
    mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
  }
  
  @Test
  public void testReportALive() throws Exception{
    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put(RequestMappings.IS_ALIVE_URL)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    assertEquals("true", result.getResponse().getContentAsString());
    result = mockMvc.perform(MockMvcRequestBuilders.post(RequestMappings.IS_ALIVE_URL)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    assertEquals("true", result.getResponse().getContentAsString());
    result = mockMvc.perform(MockMvcRequestBuilders.get(RequestMappings.IS_ALIVE_URL)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    assertEquals("true", result.getResponse().getContentAsString());
  }
}
