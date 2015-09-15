package gov.loc.rdc.tasks;

import gov.loc.rdc.entities.KeyValuePair;
import gov.loc.rdc.entities.Metadata;
import gov.loc.rdc.errors.JsonParamParseFailException;
import gov.loc.rdc.utils.KeyValueJsonConverter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.web.context.request.async.DeferredResult;

import com.fasterxml.jackson.core.JsonProcessingException;

public class FindByKeyValuePairsTaskTest extends TaskTest {
  private Metadata data;
  
  @Before
  public void setup(){
    clearDatabase();
    
    tags = new HashSet<>();
    keyValuePairs = new ArrayList<>();
    keyValuePairs.add(new KeyValuePair<String, String>(KEY1, VALUE1));
    keyValuePairs.add(new KeyValuePair<String, String>(KEY2, VALUE2));
    
    data = new Metadata(HASH, tags, keyValuePairs);
    repository.save(data);
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void testFindByKeyValuePairs() throws JsonProcessingException{
    DeferredResult<List<Metadata>> result = new DeferredResult<>();
    String keyValuePairsAsJson = KeyValueJsonConverter.convertToJson(keyValuePairs);
    FindByKeyValuePairsTask sut = new FindByKeyValuePairsTask(result, repository, keyValuePairsAsJson);
    sut.run();
    assertTrue(result.getResult() instanceof List<?>);
    List<Metadata> typedResult = (List<Metadata>) result.getResult();
    assertEquals(1, typedResult.size());
    assertEquals(data, typedResult.get(0));
  }
  
  @Test
  public void testInvalidJson() {
    DeferredResult<List<Metadata>> result = new DeferredResult<>();
    String keyValuePairsAsJson = "some invalid json";
    FindByKeyValuePairsTask sut = new FindByKeyValuePairsTask(result, repository, keyValuePairsAsJson);
    sut.run();
    assertTrue(result.getResult() instanceof JsonParamParseFailException);
  }
  
  //TODO test invalid json
  @SuppressWarnings("unchecked")
  @Test
  public void testDataNotFound() throws JsonProcessingException{
    keyValuePairs = new ArrayList<>();
    keyValuePairs.add(new KeyValuePair<String, String>("noneExistingKey", "noneExistingValue"));
    
    DeferredResult<List<Metadata>> result = new DeferredResult<>();
    String keyValuePairsAsJson = KeyValueJsonConverter.convertToJson(keyValuePairs);
    FindByKeyValuePairsTask sut = new FindByKeyValuePairsTask(result, repository, keyValuePairsAsJson);
    sut.run();
    assertTrue(result.getResult() instanceof List<?>);
    List<Metadata> typedResult = (List<Metadata>) result.getResult();
    assertEquals(0, typedResult.size());
  }
}
