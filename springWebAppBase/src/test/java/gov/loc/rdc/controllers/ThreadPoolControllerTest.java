package gov.loc.rdc.controllers;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class ThreadPoolControllerTest extends Assert{

  private ThreadPoolTaskExecutor threadExecutor;
  private MockMvc mockMvc;
  
  @Before
  public void setup(){
    threadExecutor = new ThreadPoolTaskExecutor();
    threadExecutor.setCorePoolSize(5);
    threadExecutor.setMaxPoolSize(5);
    threadExecutor.setWaitForTasksToCompleteOnShutdown(true);
    
    ThreadPoolController threadPoolController = new ThreadPoolController();
    threadPoolController.setThreadExecutor(threadExecutor);
 
    mockMvc = MockMvcBuilders.standaloneSetup(threadPoolController).build();
  }
  
  @Test
  public void testUpdateCorePoolSizeSuccessful() throws Exception{
    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/setcorepoolsize/1")).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    assertEquals(1, threadExecutor.getCorePoolSize());
    assertEquals(true, Boolean.parseBoolean(result.getResponse().getContentAsString()));
    
    result = mockMvc.perform(MockMvcRequestBuilders.post("/setcorepoolsize/2")).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    assertEquals(2, threadExecutor.getCorePoolSize());
    assertEquals(true, Boolean.parseBoolean(result.getResponse().getContentAsString()));
    
    result = mockMvc.perform(MockMvcRequestBuilders.get("/setcorepoolsize/3")).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    assertEquals(3, threadExecutor.getCorePoolSize());
    assertEquals(true, Boolean.parseBoolean(result.getResponse().getContentAsString()));
  }
  
  @Test
  public void testUpdateCorePoolSizeFail() throws Exception{
    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/setcorepoolsize/foo")).andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    assertEquals(false, Boolean.parseBoolean(result.getResponse().getContentAsString()));
    
    result = mockMvc.perform(MockMvcRequestBuilders.post("/setcorepoolsize/foo")).andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    assertEquals(false, Boolean.parseBoolean(result.getResponse().getContentAsString()));
    
    result = mockMvc.perform(MockMvcRequestBuilders.get("/setcorepoolsize/foo")).andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    assertEquals(false, Boolean.parseBoolean(result.getResponse().getContentAsString()));
  }
  
  @Test
  public void testUpdateMaxPoolSizeSuccessful() throws Exception{
    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/setmaxpoolsize/1")).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    assertEquals(1, threadExecutor.getMaxPoolSize());
    assertEquals(true, Boolean.parseBoolean(result.getResponse().getContentAsString()));
    
    result = mockMvc.perform(MockMvcRequestBuilders.post("/setmaxpoolsize/2")).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    assertEquals(2, threadExecutor.getMaxPoolSize());
    assertEquals(true, Boolean.parseBoolean(result.getResponse().getContentAsString()));
    
    result = mockMvc.perform(MockMvcRequestBuilders.get("/setmaxpoolsize/3")).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    assertEquals(3, threadExecutor.getMaxPoolSize());
    assertEquals(true, Boolean.parseBoolean(result.getResponse().getContentAsString()));
  }
  
  @Test
  public void testUpdateMaxPoolSizeFail() throws Exception{
    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/setmaxpoolsize/foo")).andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    assertEquals(false, Boolean.parseBoolean(result.getResponse().getContentAsString()));
    
    result = mockMvc.perform(MockMvcRequestBuilders.post("/setmaxpoolsize/foo")).andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    assertEquals(false, Boolean.parseBoolean(result.getResponse().getContentAsString()));
    
    result = mockMvc.perform(MockMvcRequestBuilders.get("/setmaxpoolsize/foo")).andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    assertEquals(false, Boolean.parseBoolean(result.getResponse().getContentAsString()));
  }
  
  @Test
  public void testUpdateWaitForTasksSuccessful() throws Exception{
    //no get, so unable to test that value actually changed...
    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/setwaitfortasks/false")).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    assertEquals(true, Boolean.parseBoolean(result.getResponse().getContentAsString()));
    result = mockMvc.perform(MockMvcRequestBuilders.post("/setwaitfortasks/true")).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    assertEquals(true, Boolean.parseBoolean(result.getResponse().getContentAsString()));
    result = mockMvc.perform(MockMvcRequestBuilders.get("/setwaitfortasks/false")).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    assertEquals(true, Boolean.parseBoolean(result.getResponse().getContentAsString()));
  }
  
  @Test
  public void testUpdateWaitForTasksFail() throws Exception{
    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/setwaitfortasks/foo")).andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    assertEquals(false, Boolean.parseBoolean(result.getResponse().getContentAsString()));
    result = mockMvc.perform(MockMvcRequestBuilders.post("/setwaitfortasks/foo")).andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    assertEquals(false, Boolean.parseBoolean(result.getResponse().getContentAsString()));
    result = mockMvc.perform(MockMvcRequestBuilders.get("/setwaitfortasks/foo")).andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    assertEquals(false, Boolean.parseBoolean(result.getResponse().getContentAsString()));
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testParseBooleanWithNull(){
    ThreadPoolController sut = new ThreadPoolController();
    sut.parseBoolean(null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testParseBooleanWithBlankString(){
    ThreadPoolController sut = new ThreadPoolController();
    sut.parseBoolean("");
  }
  
  @Test
  public void testParseBoolean(){
    ThreadPoolController sut = new ThreadPoolController();
    boolean bool = sut.parseBoolean("true");
    assertTrue(bool);
    bool = sut.parseBoolean("True");
    assertTrue(bool);
    bool = sut.parseBoolean("false");
    assertFalse(bool);
    bool = sut.parseBoolean("False");
    assertFalse(bool);
  }
  
}
