package gov.loc.rdc.consumers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Envelope;

import gov.loc.rdc.domain.PutInfo;
import gov.loc.rdc.repositories.FileStoreMetadataRepository;

public class FilePutRequestConsumerTest extends Assert{
  @Rule
  public TemporaryFolder folder = new TemporaryFolder();
  
  private FilePutRequestConsumer sut;
  private Envelope mockEnvelope;
  private String jsonPutInfo;
  
  @Before
  public void setup() throws Exception{
    mockEnvelope = Mockito.mock(Envelope.class);
    Mockito.when(mockEnvelope.getDeliveryTag()).thenReturn(1l);
    
    String testFilePath = getClass().getClassLoader().getResource("helloWorld.txt").getFile();
    PutInfo info = new PutInfo(Files.readAllBytes(Paths.get(testFilePath)), "a948904f2f0f479b8f8197694b30184b0d2ed1c1cd2a1ec0fb85d299a192a447");
    
    ObjectWriter writer = new ObjectMapper().writer().withDefaultPrettyPrinter();
    jsonPutInfo = writer.writeValueAsString(info);
    
    FileStoreMetadataRepository mockFileStoreRepo = Mockito.mock(FileStoreMetadataRepository.class);
    Channel mockChannel = Mockito.mock(Channel.class);
    sut = new FilePutRequestConsumer(mockChannel, folder.getRoot(), mockFileStoreRepo);
  }
  
  @Test
  public void testHandleDeliveryWhenHashDoesntExist() throws IOException{
    sut.handleDelivery("consumerTag", mockEnvelope, null, jsonPutInfo.getBytes());
    File expectedToExist = new File(folder.getRoot(), 
        "a9/48/90/4f/2f/0f/47/9b/8f/81/97/69/4b/30/18/4b/0d/2e/d1/c1/cd/2a/1e/c0/fb/85/d2/99/a1/92/a4/47/a948904f2f0f479b8f8197694b30184b0d2ed1c1cd2a1ec0fb85d299a192a447");
    assertTrue(expectedToExist.exists());
  }
  
  @Test
  public void testHandleDeliveryWhenHashDoesExist() throws IOException{
    sut.handleDelivery("consumerTag", mockEnvelope, null, jsonPutInfo.getBytes());
    File expectedToExist = new File(folder.getRoot(), 
        "a9/48/90/4f/2f/0f/47/9b/8f/81/97/69/4b/30/18/4b/0d/2e/d1/c1/cd/2a/1e/c0/fb/85/d2/99/a1/92/a4/47/a948904f2f0f479b8f8197694b30184b0d2ed1c1cd2a1ec0fb85d299a192a447");
    assertTrue(expectedToExist.exists());
    sut.handleDelivery("consumerTag", mockEnvelope, null, jsonPutInfo.getBytes());
  }
  
  @Test
  public void testHandleDeliveryWhenHashIsNotCorrect() throws IOException{
    String testFilePath = getClass().getClassLoader().getResource("helloWorld.txt").getFile();
    PutInfo info = new PutInfo(Files.readAllBytes(Paths.get(testFilePath)), "wrongHash");
    
    ObjectWriter writer = new ObjectMapper().writer().withDefaultPrettyPrinter();
    jsonPutInfo = writer.writeValueAsString(info);
    sut.handleDelivery("consumerTag", mockEnvelope, null, jsonPutInfo.getBytes());
  }
}
