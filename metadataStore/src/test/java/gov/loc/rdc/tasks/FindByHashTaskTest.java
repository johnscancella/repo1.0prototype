package gov.loc.rdc.tasks;

import gov.loc.rdc.entities.KeyValuePair;
import gov.loc.rdc.entities.Metadata;

import java.util.ArrayList;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.springframework.web.context.request.async.DeferredResult;

public class FindByHashTaskTest extends TaskTest {
  private Metadata data;
  
  @Before
  public void setup(){
    tags = new HashSet<>();
    tags.add(TAG1);
    keyValuePairs = new ArrayList<>();
    keyValuePairs.add(new KeyValuePair<String, String>(KEY1, VALUE1));
    
    data = new Metadata(HASH, tags, keyValuePairs);
    repository.save(data);
  }
  
  @Test
  public void testFindMetadata(){
    DeferredResult<Metadata> result = new DeferredResult<>();
    FindByHashTask sut = new FindByHashTask(result, repository, ALGORITHM, HASH);
    sut.run();
    assertEquals(data, result.getResult());
  }
}
