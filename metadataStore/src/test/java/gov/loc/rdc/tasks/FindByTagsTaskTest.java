package gov.loc.rdc.tasks;

import gov.loc.rdc.entities.Metadata;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.web.context.request.async.DeferredResult;

public class FindByTagsTaskTest extends TaskTest {
  private Metadata data;
  
  @Before
  public void setup(){
    tags = new HashSet<>();
    tags.add(TAG1);
    tags.add(TAG2);
    keyValuePairs = new ArrayList<>();
    
    data = new Metadata(HASH, tags, keyValuePairs);
    repository.save(data);
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void testFindByTags() {
    DeferredResult<List<Metadata>> result = new DeferredResult<>();
    FindByTagsTask sut = new FindByTagsTask(result, repository, TAG1, TAG2);
    sut.run();
    assertTrue(result.getResult() instanceof List<?>);
    List<Metadata> typedResult = (List<Metadata>) result.getResult();
    assertEquals(1, typedResult.size());
    assertEquals(data, typedResult.get(0));
  }
}
