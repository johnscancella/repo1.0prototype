package gov.loc.rdc.tasks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.web.context.request.async.DeferredResult;

import com.fasterxml.jackson.core.JsonProcessingException;

import gov.loc.rdc.entities.KeyValuePair;
import gov.loc.rdc.entities.Metadata;
import gov.loc.rdc.errors.JsonParamParseFailException;
import gov.loc.rdc.utils.KeyValueJsonConverter;

public class FindByKeyValuePairsTaskTest extends AbstractTaskTest {
  
  @SuppressWarnings("unchecked")
  @Test
  public void testFindByKeyValuePairs() throws JsonProcessingException{
    List<KeyValuePair<String, String>> keyValuePairs = new ArrayList<>();
    keyValuePairs.add(new KeyValuePair<String, String>("key1", "value1"));
    keyValuePairs.add(new KeyValuePair<String, String>("key2", "value2"));
    Metadata data = new Metadata("hash", new HashSet<>(), keyValuePairs);
    Mockito.when(mockRepository.findByKeyValuePairs(keyValuePairs)).thenReturn(Arrays.asList(data));
    
    DeferredResult<List<Metadata>> result = new DeferredResult<>();
    String keyValuePairsAsJson = KeyValueJsonConverter.convertToJson(keyValuePairs);
    FindByKeyValuePairsTask sut = new FindByKeyValuePairsTask(result, mockRepository, keyValuePairsAsJson);
    
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
    FindByKeyValuePairsTask sut = new FindByKeyValuePairsTask(result, mockRepository, keyValuePairsAsJson);
    sut.run();
    assertTrue(result.getResult() instanceof JsonParamParseFailException);
  }
}
