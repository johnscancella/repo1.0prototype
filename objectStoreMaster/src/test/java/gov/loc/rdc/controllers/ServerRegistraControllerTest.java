package gov.loc.rdc.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(MockitoJUnitRunner.class)
public class ServerRegistraControllerTest extends Assert {
  private static final List<String> MOCK_SERVER_LIST = Arrays.asList("a", "b", "c", "d", "http://e:80", "https://f:8443");
  private MockMvc mockMvc;
  private ServerRegistraController sut;
  private List<String> servers;
  
  @Mock
  private RestTemplate mockRestTemplate;

  @Before
  public void setup() {
    servers = new ArrayList<>(MOCK_SERVER_LIST);
    sut = new ServerRegistraController();
    for(String server : MOCK_SERVER_LIST){
      sut.addServer(server);
    }
    mockMvc = MockMvcBuilders.standaloneSetup(sut).build();
  }
  
  @Test
  public void testGetUrls(){
    Set<String> hostNames = new HashSet<>();
    hostNames.add("a");
    hostNames.add("b");
    hostNames.add("c");
    hostNames.add("d");
    hostNames.add("e");
    hostNames.add("f");
    List<String> urls = sut.getUrls(hostNames);
    assertEquals(MOCK_SERVER_LIST, urls);
  }
  
  @Test
  public void testListServers() throws Exception{
    ObjectMapper objectMapper = new ObjectMapper();
    String serversAsJsonString = objectMapper.writeValueAsString(servers);
    
    String url = "/list/objectstorenodes";
    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(url)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    assertEquals(serversAsJsonString, result.getResponse().getContentAsString());
    
    result = mockMvc.perform(MockMvcRequestBuilders.put(url)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    assertEquals(serversAsJsonString, result.getResponse().getContentAsString());
    
    result = mockMvc.perform(MockMvcRequestBuilders.post(url)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    assertEquals(serversAsJsonString, result.getResponse().getContentAsString());
  }
  
  @Test
  public void testAddServer() throws Exception{
    String url = "/add/objectstorenode/";
    
    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(url + "foo")).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    assertEquals("true", result.getResponse().getContentAsString());
    
    result = mockMvc.perform(MockMvcRequestBuilders.put(url + "bar")).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    assertEquals("true", result.getResponse().getContentAsString());
    
    result = mockMvc.perform(MockMvcRequestBuilders.post(url + "ham")).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    assertEquals("true", result.getResponse().getContentAsString());
  }
  
  @Test
  public void testRemoveServer() throws Exception{
    String url = "/remove/objectstorenode/";
    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(url + "a")).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    assertEquals("true", result.getResponse().getContentAsString());
    
    result = mockMvc.perform(MockMvcRequestBuilders.put(url + "b")).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    assertEquals("true", result.getResponse().getContentAsString());
    
    result = mockMvc.perform(MockMvcRequestBuilders.post(url + "c")).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    assertEquals("true", result.getResponse().getContentAsString());
  }
  
  @Test
  public void testRemoveServerThatDoesntExist() throws Exception{
    String url = "/remove/objectstorenode/";
    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(url + "foo")).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    assertEquals("false", result.getResponse().getContentAsString());
    
    result = mockMvc.perform(MockMvcRequestBuilders.put(url + "bar")).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    assertEquals("false", result.getResponse().getContentAsString());
    
    result = mockMvc.perform(MockMvcRequestBuilders.post(url + "ham")).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    assertEquals("false", result.getResponse().getContentAsString());
  }
}
