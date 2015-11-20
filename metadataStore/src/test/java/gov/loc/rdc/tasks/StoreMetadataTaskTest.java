package gov.loc.rdc.tasks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;
import org.springframework.web.context.request.async.DeferredResult;

import com.fasterxml.jackson.core.JsonProcessingException;

import gov.loc.rdc.entities.KeyValuePair;
import gov.loc.rdc.errors.JsonParamParseFailException;
import gov.loc.rdc.utils.KeyValueJsonConverter;

public class StoreMetadataTaskTest extends AbstractTaskTest {
  
  @Test
  public void testStoreMetadata() throws JsonProcessingException{
    List<KeyValuePair<String, String>> keyValuePairs = new ArrayList<>();
    keyValuePairs.add(new KeyValuePair<String, String>("key1", "value1"));
    keyValuePairs.add(new KeyValuePair<String, String>("key2", "value2"));
    
    DeferredResult<Boolean> result = new DeferredResult<>();
    String keyValuePairsAsJson = KeyValueJsonConverter.convertToJson(keyValuePairs);
    StoreMetadataTask sut = new StoreMetadataTask(result, mockRepository, "hash", new HashSet<>(), keyValuePairsAsJson);
    sut.run();
    assertTrue(result.getResult() == Boolean.TRUE);
  }
  
  @Test
  public void testInvalidJson() {
    DeferredResult<Boolean> result = new DeferredResult<>();
    String keyValuePairsAsJson = "some invalid json";
    StoreMetadataTask sut = new StoreMetadataTask(result, mockRepository, "hash", new HashSet<>(), keyValuePairsAsJson);
    sut.run();
    assertTrue(result.getResult() instanceof JsonParamParseFailException);
  }
}
