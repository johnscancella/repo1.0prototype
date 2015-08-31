package gov.loc.rdc.tasks;

import gov.loc.rdc.entities.KeyValuePair;
import gov.loc.rdc.entities.Metadata;

import java.util.ArrayList;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.springframework.web.context.request.async.DeferredResult;

public class DeleteKeyValuePairTaskTest extends TaskTest {
  @Before
  public void setup(){
    tags = new HashSet<>();
    keyValuePairs = new ArrayList<>();
    keyValuePairs.add(new KeyValuePair<String, String>(KEY1, VALUE1));
    
    Metadata data = new Metadata(HASH, tags, keyValuePairs);
    repository.save(data);
  }
  
  @Test
  public void testDeleteKeyValuePair(){
    DeferredResult<Boolean> result = new DeferredResult<>();
    KeyValuePair<String, String> pair = new KeyValuePair<String, String>(KEY1, VALUE1);
    DeleteKeyValuePairTask sut = new DeleteKeyValuePairTask(result, repository, ALGORITHM, HASH, pair);
    sut.run();
    assertTrue(result.getResult() == Boolean.TRUE);
  }
}
