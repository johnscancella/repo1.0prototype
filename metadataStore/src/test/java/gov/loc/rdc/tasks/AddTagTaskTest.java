package gov.loc.rdc.tasks;

import gov.loc.rdc.entities.Metadata;
import gov.loc.rdc.errors.UnsupportedAlgorithm;

import java.util.ArrayList;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.springframework.web.context.request.async.DeferredResult;

public class AddTagTaskTest extends TaskTest {

  @Before
  public void setup(){
    tags = new HashSet<>();
    keyValuePairs = new ArrayList<>();
    
    Metadata data = new Metadata(HASH, tags, keyValuePairs);
    repository.save(data);
  }
  
  @Test
  public void testAddTag(){
    DeferredResult<Boolean> result = new DeferredResult<>();
    AddTagTask sut = new AddTagTask(result, repository, ALGORITHM, HASH, TAG1);
    sut.run();
    assertTrue(result.getResult() == Boolean.TRUE);
  }
  
  @Test
  public void testAlgorithmNotSupported(){
    DeferredResult<Boolean> result = new DeferredResult<>();
    AddTagTask sut = new AddTagTask(result, repository, BAD_ALGORITHM, HASH, TAG1);
    sut.run();
    assertTrue(result.getResult() instanceof UnsupportedAlgorithm);
  }
}
