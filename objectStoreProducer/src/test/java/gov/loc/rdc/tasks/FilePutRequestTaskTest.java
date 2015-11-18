package gov.loc.rdc.tasks;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;

import gov.loc.rdc.domain.PutInfo;

public class FilePutRequestTaskTest extends Assert {
  private static final Path testPath = Paths.get(FilePutRequestTaskTest.class.getClassLoader().getResource("emptyTestFile.txt").getFile());

  @Test
  public void testRun() throws Exception{
    Channel mockChannel = Mockito.mock(Channel.class);
    
    PutInfo putInfo = new PutInfo(Files.readAllBytes(testPath), "ABC123");
    
    FilePutRequestTask sut = new FilePutRequestTask(mockChannel, Arrays.asList("putLongTerm", "putAccess"), putInfo);
    sut.run();
    Mockito.verify(mockChannel, Mockito.times(6)).basicPublish(Mockito.anyString(), Mockito.anyString(), Mockito.any(BasicProperties.class), Mockito.any(byte[].class));
  }
  
  @Test
  public void testConvertToJsonByteArray() throws Exception{
    String expectedString = "{\n" + 
        "  \"fileBytes\" : \"\",\n" + 
        "  \"hash\" : \"ABC123\"\n" + 
        "}";
    PutInfo putInfo = new PutInfo(Files.readAllBytes(testPath), "ABC123");
    FilePutRequestTask sut = new FilePutRequestTask(null, null, null);
    
    byte[] returned = sut.convertToJsonByteArray(putInfo);
    
    assertArrayEquals(expectedString.getBytes(), returned);
  }
}
