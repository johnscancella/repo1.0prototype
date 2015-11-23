package gov.loc.rdc.runners;

import org.junit.Test;
import org.mockito.Mockito;

import com.rabbitmq.client.Channel;

public class FilePullRequestMessageHandlerTest {

  @Test
  public void smokeTest() throws Exception{
    FilePullRequestMessageHandler sut = Mockito.spy(FilePullRequestMessageHandler.class);
    Channel mockChannel = Mockito.mock(Channel.class);
    Mockito.doReturn(mockChannel).when(sut).createChannel("queue", "host");
    sut.setMqHost("host");
    sut.setQueueName("queue");
    sut.run();
  }
}
