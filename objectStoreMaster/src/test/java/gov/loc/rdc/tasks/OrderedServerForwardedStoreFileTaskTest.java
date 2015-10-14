package gov.loc.rdc.tasks;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

@RunWith(MockitoJUnitRunner.class)
public class OrderedServerForwardedStoreFileTaskTest extends Assert{
  @Mock
  private RestTemplate mockRestTemplate;
  
  @Test
  public void testStoreFile(){
    String response = "ABC123";
    Mockito.when(mockRestTemplate.postForObject(Mockito.anyString(), Mockito.any(), Mockito.any())).thenReturn(response);
    DeferredResult<String> result = new DeferredResult<>();
    MultipartFile file = new MockMultipartFile("ABC123", new byte[]{});
    OrderedServerForwardedStoreFileTask sut = 
        new OrderedServerForwardedStoreFileTask(Arrays.asList("a"), result, file, mockRestTemplate);
    
    sut.run();
    assertEquals(response, result.getResult());
  }
  
  @Test
  public void testError(){
    RestClientException error = new RestClientException("fooException");
    Mockito.when(mockRestTemplate.postForObject(Mockito.anyString(), Mockito.any(), Mockito.any())).thenThrow(error);
    DeferredResult<String> result = new DeferredResult<>();
    MultipartFile file = new MockMultipartFile("ABC123", new byte[]{});
    OrderedServerForwardedStoreFileTask sut = 
        new OrderedServerForwardedStoreFileTask(Arrays.asList("a"), result, file, mockRestTemplate);
    
    sut.run();
    assertEquals(error, result.getResult());
  }
}
