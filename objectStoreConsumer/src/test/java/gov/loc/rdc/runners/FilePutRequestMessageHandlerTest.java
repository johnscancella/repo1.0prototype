package gov.loc.rdc.runners;

import org.junit.Test;
import org.mockito.Mockito;

import com.rabbitmq.client.Channel;

public class FilePutRequestMessageHandlerTest {

  @Test
  public void smokeTest() throws Exception{
    FilePutRequestMessageHandler sut = Mockito.spy(FilePutRequestMessageHandler.class);
    Channel mockChannel = Mockito.mock(Channel.class);
    Mockito.doReturn(mockChannel).when(sut).createChannel("queue", "host");
    sut.setMqHost("host");
    sut.setQueueName("queue");
    sut.run();
  }
}
