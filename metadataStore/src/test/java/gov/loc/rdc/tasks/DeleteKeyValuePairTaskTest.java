package gov.loc.rdc.tasks;

import org.junit.Test;
import org.springframework.web.context.request.async.DeferredResult;

import gov.loc.rdc.entities.KeyValuePair;

public class DeleteKeyValuePairTaskTest extends AbstractTaskTest {
  
  @Test
  public void testDeleteKeyValuePair(){
    DeferredResult<Boolean> result = new DeferredResult<>();
    KeyValuePair<String, String> pair = new KeyValuePair<String, String>("key", "value");
    DeleteKeyValuePairTask sut = new DeleteKeyValuePairTask(result, mockRepository, "hash", pair);
    sut.run();
    assertTrue(result.getResult() == Boolean.TRUE);
  }
}
