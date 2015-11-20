package gov.loc.rdc.tasks;

import org.junit.Test;
import org.springframework.web.context.request.async.DeferredResult;

public class AddTagTaskTest extends AbstractTaskTest {
  
  @Test
  public void testAddTag(){
    DeferredResult<Boolean> result = new DeferredResult<>();
    AddTagTask sut = new AddTagTask(result, mockRepository, "hash", "tag");
    sut.run();
    assertTrue(result.getResult() == Boolean.TRUE);
  }
}
