package gov.loc.rdc.repositories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import gov.loc.rdc.entities.KeyValuePair;
import gov.loc.rdc.entities.Metadata;

public class MetadataMongoRepositoryDriverTest extends Assert {

  private MetadataMongoRepositoryDriver sut;
  private MongoTemplate mockMongoTemplate;
  
  @Before
  public void setup(){
    mockMongoTemplate = Mockito.mock(MongoTemplate.class);
    sut = new MetadataMongoRepositoryDriver(mockMongoTemplate);
  }
  
  @Test
  public void testFindByHash(){
    Metadata expectedData = new Metadata("hash");
    Metadata acutalData = sut.findByHash("hash");
    assertEquals(expectedData, acutalData);
  }
  
  @Test
  public void testFindByTag(){
    List<Metadata> expectedData = new ArrayList<>();
    List<Metadata> actualData = sut.findByTag("tag");
    assertEquals(expectedData, actualData);
  }
  
  @Test
  public void testFindByKeyValuePair(){
    KeyValuePair<String, String> keyValuePair = new KeyValuePair<String, String>("key", "value");
    List<Metadata> expectedData = new ArrayList<>();
    List<Metadata> actualData = sut.findByKeyValuePair(keyValuePair);
    assertEquals(expectedData, actualData);
  }
  
  @Test
  public void testFindByTags(){
    List<String> tags = Arrays.asList("tag1", "tag2");
    List<Metadata> expectedData = new ArrayList<>();
    List<Metadata> actualData = sut.findByTags(tags);
    assertEquals(expectedData, actualData);
  }
  
  @Test
  public void testFindByKeyValuePairs(){
    KeyValuePair<String, String> keyValuePair = new KeyValuePair<String, String>("key", "value");
    List<Metadata> expectedData = new ArrayList<>();
    List<Metadata> actualData = sut.findByKeyValuePairs(Arrays.asList(keyValuePair));
    assertEquals(expectedData, actualData);
  }
  
  @Test
  public void testDeleteHash(){
    sut.deleteHash("hash");
  }
  
  @Test
  public void testDeleteTagFromHash(){
    Set<String> tags = new HashSet<>();
    tags.add("tag");
    Metadata testData = new Metadata("hash", tags, new ArrayList<>());
    Metadata expectedData = new Metadata("hash");
    
    Mockito.when(mockMongoTemplate.findOne(Mockito.any(Query.class), Mockito.any())).thenReturn(testData);
    
    sut.deleteTagFromHash("tag", "hash");
    Mockito.verify(mockMongoTemplate).save(expectedData);
  }
  
  @Test
  public void testDeleteKeyValueFromHash(){
    KeyValuePair<String, String> keyValuePair = new KeyValuePair<String, String>("key", "value");
    List<KeyValuePair<String, String>> pairs = new ArrayList<>();
    pairs.add(keyValuePair);
    
    Metadata testData = new Metadata("hash", new HashSet<>(), pairs);
    Metadata expectedData = new Metadata("hash");
    
    Mockito.when(mockMongoTemplate.findOne(Mockito.any(Query.class), Mockito.any())).thenReturn(testData);
    
    sut.deleteKeyValueFromHash(keyValuePair, "hash");
    Mockito.verify(mockMongoTemplate).save(expectedData);
  }
  
  @Test
  public void testSaveKeyValuePairToHash(){
    KeyValuePair<String, String> keyValuePair = new KeyValuePair<String, String>("key", "value");
    Metadata expectedData = new Metadata("hash", new HashSet<>(), new ArrayList<>(Arrays.asList(keyValuePair)));
    sut.saveKeyValuePairToHash(keyValuePair, "hash");
    Mockito.verify(mockMongoTemplate).save(expectedData);
  }
}
