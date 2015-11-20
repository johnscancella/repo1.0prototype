package gov.loc.rdc.tasks;

import org.junit.Test;
import org.springframework.web.context.request.async.DeferredResult;

import gov.loc.rdc.entities.KeyValuePair;

public class AddKeyValuePairTaskTest extends AbstractTaskTest{
    
  @Test
  public void testAddKeyValuePair(){
    DeferredResult<Boolean> result = new DeferredResult<>();
    AddKeyValuePairTask sut = new AddKeyValuePairTask(result, mockRepository, "hash", new KeyValuePair<String, String>("key", "value"));
    sut.run();
    assertTrue(result.getResult() == Boolean.TRUE);
  }
}
