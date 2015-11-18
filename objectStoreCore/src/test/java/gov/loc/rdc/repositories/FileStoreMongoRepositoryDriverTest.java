package gov.loc.rdc.repositories;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import gov.loc.rdc.entities.FileStoreData;

public class FileStoreMongoRepositoryDriverTest extends Assert {

  private FileStoreMongoRepositoryDriver sut;
  private MongoTemplate mockMongoTemplate;
  
  @Before
  public void setup(){
    mockMongoTemplate = Mockito.mock(MongoTemplate.class);
    sut = new FileStoreMongoRepositoryDriver();
    sut.setMongoTemplate(mockMongoTemplate);
  }
  
  @Test
  public void testGet(){
    FileStoreData ExpectedData = new FileStoreData("hash");
    FileStoreData actualData = sut.get("hash");
    assertEquals(ExpectedData, actualData);
  }
  
  @Test
  public void testUpsert(){
    FileStoreData data = new FileStoreData("hash");
    sut.upsert(data);
    Mockito.verify(mockMongoTemplate).save(data);
  }
  
  @Test
  public void testGetHashesForServer(){
    FileStoreData data = new FileStoreData("hash", "server1");
    Mockito.when(mockMongoTemplate.find(Mockito.any(Query.class), Mockito.any())).thenReturn(Arrays.asList(data));
    List<String> expectedHashes = Arrays.asList("hash");
    
    List<String> actualHashes = sut.getHashesForServer("server1");
    assertEquals(expectedHashes, actualHashes);
  }
}
