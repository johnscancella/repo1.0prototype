package gov.loc.rdc.tasks;

import org.junit.Test;
import org.springframework.web.context.request.async.DeferredResult;

public class DeleteMetadataTaskTest extends AbstractTaskTest {
  
  @Test
  public void testDeleteMetadata(){
    DeferredResult<Boolean> result = new DeferredResult<>();
    DeleteMetadataTask sut = new DeleteMetadataTask(result, mockRepository, "hash");
    sut.run();
    assertTrue(result.getResult() == Boolean.TRUE);
  }
}
