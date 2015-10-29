package gov.loc.rdc.tasks;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.DeferredResult;

@RunWith(MockitoJUnitRunner.class)
public class OrderedServerForwardedFileExistsTaskTest extends Assert {
  @Mock
  private RestTemplate mockRestTemplate;
  
  @Test
  public void testFileExists(){
    Mockito.when(mockRestTemplate.getForObject(Mockito.anyString(), Mockito.any())).thenReturn(true);
    DeferredResult<Boolean> result = new DeferredResult<>();
    OrderedServerForwardedFileExistsTask sut = new 
        OrderedServerForwardedFileExistsTask(Arrays.asList("fooServer"), result, "ABC123", mockRestTemplate);
    
    sut.run();
    assertTrue((Boolean)result.getResult());
  }
  
  @Test
  public void testFileDoesntExists(){
    Mockito.when(mockRestTemplate.getForObject(Mockito.anyString(), Mockito.any())).thenReturn(false);
    DeferredResult<Boolean> result = new DeferredResult<>();
    OrderedServerForwardedFileExistsTask sut = new 
        OrderedServerForwardedFileExistsTask(Arrays.asList("fooServer"), result, "ABC123", mockRestTemplate);
    
    sut.run();
    assertFalse((Boolean)result.getResult());
  }
  
  @Test
  public void testError(){
    RestClientException error = new RestClientException("fooException");
    Mockito.when(mockRestTemplate.getForObject(Mockito.anyString(), Mockito.any())).thenThrow(error);
    DeferredResult<Boolean> result = new DeferredResult<>();
    OrderedServerForwardedFileExistsTask sut = new 
        OrderedServerForwardedFileExistsTask(Arrays.asList("fooServer"), result, "ABC123", mockRestTemplate);
    
    sut.run();
    assertEquals(error, result.getResult());
  }
}
