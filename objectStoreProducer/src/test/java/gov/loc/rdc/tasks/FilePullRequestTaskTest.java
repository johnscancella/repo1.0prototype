package gov.loc.rdc.tasks;

import java.util.HashMap;
import java.util.Map;

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
    ScpInfo scpInfo = new ScpInfo("server", 22, "/filepath");
    Map<String, Integer> storageTypesToCopiesMap = new HashMap<>();
    storageTypesToCopiesMap.put("longterm", 2);
    storageTypesToCopiesMap.put("access", 3);
    
    FilePullRequestTask sut = new FilePullRequestTask(scpInfo, mockChannel, storageTypesToCopiesMap);
    sut.run();
    Mockito.verify(mockChannel, Mockito.times(5)).basicPublish(Mockito.anyString(), Mockito.anyString(), Mockito.any(BasicProperties.class), Mockito.any(byte[].class));
  }
  
  @Test
  public void testConvertToJsonByteArray() throws Exception{
    String expectedString = "{\n" + 
        "  \"server\" : \"server\",\n" + 
        "  \"port\" : 22,\n" + 
        "  \"filepath\" : \"/filepath\"\n" + 
        "}";
    ScpInfo scpInfo = new ScpInfo("server", 22, "/filepath");
    FilePullRequestTask sut = new FilePullRequestTask(null, null, null);
    
    byte[] returned = sut.convertToJsonByteArray(scpInfo);
    
    assertArrayEquals(expectedString.getBytes(), returned);
  }
}
