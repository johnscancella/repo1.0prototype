package gov.loc.rdc.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

public class RoundRobinServerControllerTest extends Assert {

  private MockMvc mockMvc;
  private RoundRobinServerController sut;
  private List<String> servers;

  @Before
  public void setup() {
    sut = new RoundRobinServerController();
    servers = new ArrayList<>(Arrays.asList("a", "b", "c", "d", "e", "f"));
    sut.setServerAddresses(servers);
    mockMvc = MockMvcBuilders.standaloneSetup(sut).build();
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
    String returnMessage = "Successfully added ";
    
    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(url + "foo")).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    assertEquals(returnMessage + "foo", result.getResponse().getContentAsString());
    
    result = mockMvc.perform(MockMvcRequestBuilders.put(url + "bar")).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    assertEquals(returnMessage + "bar", result.getResponse().getContentAsString());
    
    result = mockMvc.perform(MockMvcRequestBuilders.post(url + "ham")).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    assertEquals(returnMessage + "ham", result.getResponse().getContentAsString());
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

  @Test
  public void testGetAvailableServers() {
    List<String> expected = Arrays.asList("b", "c", "d", "e", "f", "a");
    List<String> actual = sut.getAvailableServers();
    assertEquals(expected, actual);
  }

  @Test
  public void testReorder() {
    List<String> strings = Arrays.asList("a", "b", "c", "d", "e", "f");
    List<String> expected1 = Arrays.asList("a", "b", "c", "d", "e", "f");
    List<String> expected2 = Arrays.asList("b", "c", "d", "e", "f", "a");
    List<String> expected3 = Arrays.asList("c", "d", "e", "f", "a", "b");
    List<String> expected4 = Arrays.asList("d", "e", "f", "a", "b", "c");
    List<String> expected5 = Arrays.asList("e", "f", "a", "b", "c", "d");
    List<String> expected6 = Arrays.asList("f", "a", "b", "c", "d", "e");

    List<String> returned = sut.reorder(0, strings);
    assertEquals(expected1, returned);

    returned = sut.reorder(1, strings);
    assertEquals(expected2, returned);

    returned = sut.reorder(2, strings);
    assertEquals(expected3, returned);

    returned = sut.reorder(3, strings);
    assertEquals(expected4, returned);

    returned = sut.reorder(4, strings);
    assertEquals(expected5, returned);

    returned = sut.reorder(5, strings);
    assertEquals(expected6, returned);
  }

  @Test
  public void testReorderWithIntegerRollOver() {
    int index = Integer.MAX_VALUE;
    List<String> strings = Arrays.asList("a", "b", "c", "d", "e", "f");
    List<String> expected1 = Arrays.asList("b", "c", "d", "e", "f", "a");
    List<String> expected2 = Arrays.asList("c", "d", "e", "f", "a", "b");

    List<String> returned = sut.reorder(index, strings);
    assertEquals(expected1, returned);

    index++;
    returned = sut.reorder(index, strings);
    assertEquals(expected2, returned);
  }
}
