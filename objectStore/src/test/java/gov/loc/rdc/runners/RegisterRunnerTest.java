package gov.loc.rdc.runners;

import gov.loc.rdc.controllers.RequestMappings;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@RunWith(MockitoJUnitRunner.class)
public class RegisterRunnerTest {
  private static final String MASTER_URL = "masterNodeUrl";
  private static final String MY_URL = "myUrl";
  
  private RegisterRunner sut;
  
  @Mock
  private RestTemplate mockRestTemplate;
  
  @Before
  public void setup(){
    sut = new RegisterRunner(MASTER_URL, 1, MY_URL, mockRestTemplate);
  }

  @Test
  public void testRegister() throws Exception{
    Mockito.when(mockRestTemplate.getForObject(
        MASTER_URL + RequestMappings.ADD_SERVER_TO_CLUSTER_POOL_URL, Boolean.class, MY_URL)).thenReturn(false, true);
    sut.run(new String[]{});
  }
  
  @Test
  public void testError() throws Exception{
    Mockito.when(mockRestTemplate.getForObject(
        MASTER_URL + RequestMappings.ADD_SERVER_TO_CLUSTER_POOL_URL, Boolean.class, MY_URL)).
          thenThrow(new RestClientException("foo reset client error")).thenReturn(true);
    sut.run(new String[]{});
  }
}
