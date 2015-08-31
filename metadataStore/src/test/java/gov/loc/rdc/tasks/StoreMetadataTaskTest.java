package gov.loc.rdc.tasks;

import gov.loc.rdc.entities.KeyValuePair;
import gov.loc.rdc.utils.KeyValueJsonConverter;

import java.util.ArrayList;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.springframework.web.context.request.async.DeferredResult;

import com.fasterxml.jackson.core.JsonProcessingException;

public class StoreMetadataTaskTest extends TaskTest {
  
  @Before
  public void setup(){
    tags = new HashSet<>();
    tags.add(KEY1);
    tags.add(KEY2);
    keyValuePairs = new ArrayList<>();
    keyValuePairs.add(new KeyValuePair<String, String>(KEY1, VALUE1));
    keyValuePairs.add(new KeyValuePair<String, String>(KEY2, VALUE2));
  }
  
  @Test
  public void testStoreMetadata() throws JsonProcessingException{
    DeferredResult<Boolean> result = new DeferredResult<>();
    String keyValuePairsAsJson = KeyValueJsonConverter.convertToJson(keyValuePairs);
    StoreMetadataTask sut = new StoreMetadataTask(result, repository, ALGORITHM, HASH, tags, keyValuePairsAsJson);
    sut.run();
    assertTrue(result.getResult() == Boolean.TRUE);
  }
}
