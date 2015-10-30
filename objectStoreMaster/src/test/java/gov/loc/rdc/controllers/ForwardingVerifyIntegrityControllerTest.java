package gov.loc.rdc.controllers;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;

@RunWith(MockitoJUnitRunner.class)
public class ForwardingVerifyIntegrityControllerTest extends Assert {
  private MockMvc mockMvc;
  private ForwardingVerifyIntegrityController sut;
  
  @Mock
  private RestTemplate mockRestTemplate;
  
  @Mock
  private ServerRegistraController mockServerRegistraController;
  
  @Before
  public void setup(){
    sut = new ForwardingVerifyIntegrityController(mockRestTemplate, mockServerRegistraController);
    mockMvc = MockMvcBuilders.standaloneSetup(sut).build();
  }
  
  @Test
  public void testDefaultConstructor(){
    sut = new ForwardingVerifyIntegrityController();
  }
  
  @Test
  public void testVerifyWithDefaultDirectory() throws Exception{
    Mockito.when(mockServerRegistraController.listServers()).thenReturn(Arrays.asList("aServer"));

    mockMvc.perform(MockMvcRequestBuilders.get(RequestMappings.VERIFY_INTEGRITY_URL)).andExpect(MockMvcResultMatchers.status().isOk());
    mockMvc.perform(MockMvcRequestBuilders.put(RequestMappings.VERIFY_INTEGRITY_URL)).andExpect(MockMvcResultMatchers.status().isOk());
    mockMvc.perform(MockMvcRequestBuilders.post(RequestMappings.VERIFY_INTEGRITY_URL)).andExpect(MockMvcResultMatchers.status().isOk());
    
    Mockito.verify(mockRestTemplate, new Times(3)).getForObject("aServer" + RequestMappings.VERIFY_INTEGRITY_URL, Void.class);
  }
  
  @Test
  public void testVerifyWithSpecifiedDirectory() throws Exception{
    Mockito.when(mockServerRegistraController.listServers()).thenReturn(Arrays.asList("aServer"));

    mockMvc.perform(MockMvcRequestBuilders.get(RequestMappings.VERIFY_INTEGRITY_URL).param("rootdir", "/foo")).andExpect(MockMvcResultMatchers.status().isOk());
    mockMvc.perform(MockMvcRequestBuilders.put(RequestMappings.VERIFY_INTEGRITY_URL).param("rootdir", "/foo")).andExpect(MockMvcResultMatchers.status().isOk());
    mockMvc.perform(MockMvcRequestBuilders.post(RequestMappings.VERIFY_INTEGRITY_URL).param("rootdir", "/foo")).andExpect(MockMvcResultMatchers.status().isOk());
    
    Mockito.verify(mockRestTemplate, new Times(3)).getForObject("aServer" + RequestMappings.VERIFY_INTEGRITY_URL + "?rootdir={dirToCheck}", Void.class, "/foo");
  }
}
