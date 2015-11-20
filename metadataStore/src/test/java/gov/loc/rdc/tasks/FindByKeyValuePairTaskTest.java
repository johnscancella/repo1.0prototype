package gov.loc.rdc.tasks;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.web.context.request.async.DeferredResult;

import gov.loc.rdc.entities.KeyValuePair;
import gov.loc.rdc.entities.Metadata;

public class FindByKeyValuePairTaskTest extends AbstractTaskTest {
  
  @SuppressWarnings("unchecked")
  @Test
  public void testFindByKeyValuePair() {
    DeferredResult<List<Metadata>> result = new DeferredResult<>();
    KeyValuePair<String, String> pair = new KeyValuePair<String, String>("key", "value");
    FindByKeyValuePairTask sut = new FindByKeyValuePairTask(result, mockRepository, pair);
    Metadata data = new Metadata("hash", new HashSet<>(), Arrays.asList(pair));
    Mockito.when(mockRepository.findByKeyValuePair(pair)).thenReturn(Arrays.asList(data));
    
    sut.run();
    
    assertTrue(result.getResult() instanceof List<?>);
    List<Metadata> typedResult = (List<Metadata>) result.getResult();
    assertEquals(1, typedResult.size());
    assertEquals(data, typedResult.get(0));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void testDataNotFound(){
    DeferredResult<List<Metadata>> result = new DeferredResult<>();
    FindByKeyValuePairTask sut = new FindByKeyValuePairTask(result, mockRepository, new KeyValuePair<String, String>("nonexistingKey", "nonexistingValue"));
    sut.run();
    assertTrue(result.getResult() instanceof List<?>);
    List<Metadata> typedResult = (List<Metadata>) result.getResult();
    assertEquals(0, typedResult.size());
  }
}
