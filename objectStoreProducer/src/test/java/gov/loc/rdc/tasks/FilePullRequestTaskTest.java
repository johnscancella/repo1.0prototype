package gov.loc.rdc.tasks;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;

import gov.loc.rdc.domain.ScpInfo;

public class FilePullRequestTaskTest extends Assert {

  @Test
  public void testRun() throws Exception{
    Channel mockChannel = Mockito.mock(Channel.class);
    ScpInfo scpInfo = new ScpInfo("server", 22, "/filepath", "hash");
    
    FilePullRequestTask sut = new FilePullRequestTask(scpInfo, mockChannel, Arrays.asList("scpLongTerm", "scpAccess"));
    sut.run();
    Mockito.verify(mockChannel, Mockito.times(6)).basicPublish(Mockito.anyString(), Mockito.anyString(), Mockito.any(BasicProperties.class), Mockito.any(byte[].class));
  }
  
  @Test
  public void testConvertToJsonByteArray() throws Exception{
    String expectedString = "{\n" + 
        "  \"server\" : \"server\",\n" + 
        "  \"port\" : 22,\n" + 
        "  \"filepath\" : \"/filepath\",\n" +
        "  \"hash\" : \"hash\"\n" +
        "}";
    ScpInfo scpInfo = new ScpInfo("server", 22, "/filepath", "hash");
    FilePullRequestTask sut = new FilePullRequestTask(null, null, null);
    
    byte[] returned = sut.convertToJsonByteArray(scpInfo);
    
    assertArrayEquals(expectedString.getBytes(), returned);
  }
}
