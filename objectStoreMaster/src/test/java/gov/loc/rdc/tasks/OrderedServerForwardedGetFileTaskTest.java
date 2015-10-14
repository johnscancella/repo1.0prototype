package gov.loc.rdc.tasks;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.DeferredResult;

@RunWith(MockitoJUnitRunner.class)
public class OrderedServerForwardedGetFileTaskTest extends Assert {
  @Mock
  private RestTemplate mockRestTemplate;
  
  @Test
  public void testGetFile(){
    ByteArrayResource response = new ByteArrayResource(new byte[]{});
    Mockito.when(mockRestTemplate.getForObject(Mockito.anyString(), Mockito.any())).thenReturn(response);
    DeferredResult<byte[]> result = new DeferredResult<>();
    OrderedServerForwardedGetFileTask sut = 
        new OrderedServerForwardedGetFileTask(Arrays.asList("a"), result, "SHA-256", "ABC123", mockRestTemplate);
    
    sut.run();
    assertArrayEquals(response.getByteArray(), (byte[]) result.getResult());
  }
  
  @Test
  public void testError(){
    RestClientException error = new RestClientException("fooException");
    Mockito.when(mockRestTemplate.getForObject(Mockito.anyString(), Mockito.any())).thenThrow(error);
    DeferredResult<byte[]> result = new DeferredResult<>();
    OrderedServerForwardedGetFileTask sut = 
        new OrderedServerForwardedGetFileTask(Arrays.asList("a"), result, "SHA-256", "ABC123", mockRestTemplate);
    
    sut.run();
    assertEquals(error, result.getResult());
  }
}
