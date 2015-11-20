package gov.loc.rdc.tasks;

import org.junit.Test;
import org.springframework.web.context.request.async.DeferredResult;

public class DeleteTagTaskTest extends AbstractTaskTest {
  
  @Test
  public void testDeleteKeyValuePair(){
    DeferredResult<Boolean> result = new DeferredResult<>();
    DeleteTagTask sut = new DeleteTagTask(result, mockRepository, "hash", "tag");
    sut.run();
    assertTrue(result.getResult() == Boolean.TRUE);
  }
}
