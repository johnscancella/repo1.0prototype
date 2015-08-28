package gov.loc.rdc.controllers;

import gov.loc.rdc.app.MetadataStoreApplication;
import gov.loc.rdc.entities.KeyValuePair;
import gov.loc.rdc.entities.Metadata;
import gov.loc.rdc.utils.KeyValueJsonConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * just tests the database interaction with the {@link MetadataStoreController}
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { MetadataStoreApplication.class})
@Configuration
public class MetadataStoreControllerSpringTest extends Assert{
  private static final String HASH = "ABC123";
  private static final String ALGORITHM = "sha256";
  private static final String TAG1 = "fooTag1";
  private static final String TAG2 = "fooTag2";
  private static final String KEY1 = "fooKey";
  private static final String KEY2 = "fooKey2";
  private static final String VALUE1 = "fooValue";
  private static final String VALUE2 = "fooValue2";
  
  @Autowired
  private MetadataStoreController sut;
  
  private Set<String> tags;
  private List<KeyValuePair<String, String>> keyValuePairs;
  
  @Before
  public void setup() throws JsonProcessingException{
    tags = new HashSet<>();
    tags.add(TAG1);
    tags.add(TAG2);
    keyValuePairs = new ArrayList<>();
    keyValuePairs.add(new KeyValuePair<String, String>(KEY1, VALUE1));
    keyValuePairs.add(new KeyValuePair<String, String>(KEY2, VALUE2));
    keyValuePairs.add(new KeyValuePair<String, String>(KEY2, VALUE2));
    sut.storeMetadata(ALGORITHM, HASH, tags, KeyValueJsonConverter.convertToJson(keyValuePairs));
  }
  
  @Test
  public void testFindAndDeleteByHash(){
    Metadata data = sut.findByHash(ALGORITHM, HASH);
    assertNotNull(data);
    assertEquals(tags, data.getTags());
    assertEquals(keyValuePairs, data.getKeyValuePairs());
  }
  
  @Test
  public void testDeleteByHash(){
    sut.deleteMetadata(ALGORITHM, HASH);
    Metadata data = sut.findByHash(ALGORITHM, HASH);
    assertNotNull(data);
    assertEquals("NO HASH", data.getHash());
    assertEquals(0, data.getTags().size());
    assertEquals(0, data.getKeyValuePairs().size());
  }
  
  @Test
  public void testFindByTag(){
    List<Metadata> datas = sut.findBytag(TAG1);
    assertNotNull(datas);
    assertEquals(1, datas.size());
    assertEquals(HASH, datas.get(0).getHash());
    assertEquals(keyValuePairs, datas.get(0).getKeyValuePairs());
  }
  
  @Test
  public void testFindByTags(){
    List<Metadata> datas = sut.findBytags(Arrays.asList(TAG1, TAG2));
    assertNotNull(datas);
    assertEquals(1, datas.size());
    assertEquals(HASH, datas.get(0).getHash());
    assertEquals(keyValuePairs, datas.get(0).getKeyValuePairs());
  }
  
  @Test
  public void testDeleteTag(){
    sut.deleteTag(ALGORITHM, HASH, TAG1);
    List<Metadata> datas = sut.findBytag(TAG1);
    assertNotNull(datas);
    assertEquals(0, datas.size());
  }
  
  @Test
  public void testFindByKeyValuePair(){
    List<Metadata> datas = sut.findByKeyValuePair(KEY1, VALUE1);
    assertNotNull(datas);
    assertEquals(1, datas.size());
    assertEquals(HASH, datas.get(0).getHash());
    assertEquals(tags, datas.get(0).getTags());
  }
  
  @Test
  public void testFindByKeyValuePairs() throws Exception{
    List<Metadata> datas = sut.findByKeyValuePairs(KeyValueJsonConverter.convertToJson(keyValuePairs));
    assertNotNull(datas);
    assertEquals(1, datas.size());
    assertEquals(HASH, datas.get(0).getHash());
    assertEquals(tags, datas.get(0).getTags());
  }
  
  @Test
  public void testDeleteKeyValuePair(){
    sut.deleteKeyValue(ALGORITHM, HASH, KEY1, VALUE1);
    List<Metadata> datas = sut.findByKeyValuePair(KEY1, VALUE1);
    assertNotNull(datas);
    assertEquals(0, datas.size());
  }
  
}
