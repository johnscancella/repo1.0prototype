package gov.loc.rdc.tasks;

import org.junit.Test;
import org.springframework.web.context.request.async.DeferredResult;

import gov.loc.rdc.entities.Metadata;

public class FindByHashTaskTest extends AbstractTaskTest {
  
  @Test
  public void testFindMetadata(){
    Metadata expectedData = new Metadata("hash");
    DeferredResult<Metadata> result = new DeferredResult<>();
    FindByHashTask sut = new FindByHashTask(result, mockRepository, "hash");
    sut.run();
    assertEquals(expectedData, result.getResult());
  }
}
