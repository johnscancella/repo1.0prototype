package gov.loc.rdc.tasks;

import gov.loc.rdc.entities.KeyValuePair;
import gov.loc.rdc.entities.Metadata;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.web.context.request.async.DeferredResult;

public class FindByKeyValuePairTaskTest extends TaskTest {
  private Metadata data;
  
  @Before
  public void setup(){
    tags = new HashSet<>();
    keyValuePairs = new ArrayList<>();
    keyValuePairs.add(new KeyValuePair<String, String>(KEY1, VALUE1));
    
    data = new Metadata(HASH, tags, keyValuePairs);
    repository.save(data);
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void testFindByKeyValuePair() {
    DeferredResult<List<Metadata>> result = new DeferredResult<>();
    FindByKeyValuePairTask sut = new FindByKeyValuePairTask(result, repository, new KeyValuePair<String, String>(KEY1, VALUE1));
    sut.run();
    assertTrue(result.getResult() instanceof List<?>);
    List<Metadata> typedResult = (List<Metadata>) result.getResult();
    assertEquals(1, typedResult.size());
    assertEquals(data, typedResult.get(0));
  }
}
