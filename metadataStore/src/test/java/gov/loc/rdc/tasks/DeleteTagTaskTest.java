package gov.loc.rdc.tasks;

import gov.loc.rdc.entities.Metadata;
import gov.loc.rdc.errors.UnsupportedAlgorithmException;

import java.util.ArrayList;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.springframework.web.context.request.async.DeferredResult;

public class DeleteTagTaskTest extends TaskTest {
  @Before
  public void setup(){
    clearDatabase();
    
    tags = new HashSet<>();
    tags.add(TAG1);
    tags.add(TAG2);
    keyValuePairs = new ArrayList<>();
    
    Metadata data = new Metadata(HASH, tags, keyValuePairs);
    repository.save(data);
  }
  
  @Test
  public void testDeleteKeyValuePair(){
    DeferredResult<Boolean> result = new DeferredResult<>();
    DeleteTagTask sut = new DeleteTagTask(result, repository, ALGORITHM, HASH, TAG1);
    sut.run();
    assertTrue(result.getResult() == Boolean.TRUE);
  }
  
  @Test
  public void testAlgorithmNotSupported(){
    DeferredResult<Boolean> result = new DeferredResult<>();
    DeleteTagTask sut = new DeleteTagTask(result, repository, BAD_ALGORITHM, HASH, TAG1);
    sut.run();
    assertTrue(result.getResult() instanceof UnsupportedAlgorithmException);
  }
}
