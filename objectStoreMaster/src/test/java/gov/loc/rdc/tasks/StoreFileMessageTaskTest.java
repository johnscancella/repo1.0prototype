package gov.loc.rdc.tasks;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.context.request.async.DeferredResult;

import com.rabbitmq.client.Channel;

@RunWith(MockitoJUnitRunner.class)
public class StoreFileMessageTaskTest extends Assert{
  @Mock
  private Channel mockChannel;
  
  @Test
  public void test() throws IOException{
    DeferredResult<String> result = new DeferredResult<>();
    InputStream contentStream = this.getClass().getClassLoader().getResourceAsStream("emptyTestFile.txt");
    MockMultipartFile file = new MockMultipartFile("name", contentStream);
    StoreFileMessageTask sut = new StoreFileMessageTask(result, file, mockChannel, "queueName");
    
    sut.run();
    assertEquals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855", result.getResult());
    Mockito.verify(mockChannel).basicPublish(Mockito.anyString(), Mockito.anyString(), Mockito.any(), Mockito.any());
  }
  
  @Test
  public void testError() throws IOException{
    DeferredResult<String> result = new DeferredResult<>();
    InputStream contentStream = this.getClass().getClassLoader().getResourceAsStream("emptyTestFile.txt");
    MockMultipartFile file = new MockMultipartFile("name", contentStream);
    StoreFileMessageTask sut = new StoreFileMessageTask(result, file, mockChannel, "queueName");
    //channel.basicPublish(DEFAULT_EXCHANGE, queueName, null, file.getBytes());
    Mockito.doThrow(new IOException("foo IO exception")).when(mockChannel).basicPublish(Mockito.anyString(), Mockito.anyString(), Mockito.any(), Mockito.any());
    
    sut.run();
    assertTrue(result.getResult() instanceof IOException);
  }
}
