package gov.loc.rdc.tasks;

import gov.loc.rdc.entities.Metadata;
import gov.loc.rdc.errors.UnsupportedAlgorithmException;

import java.util.ArrayList;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.springframework.web.context.request.async.DeferredResult;

public class DeleteMetadataTaskTest extends TaskTest {
  @Before
  public void setup(){
    clearDatabase();
    
    tags = new HashSet<>();
    keyValuePairs = new ArrayList<>();
    
    Metadata data = new Metadata(HASH, tags, keyValuePairs);
    repository.save(data);
  }
  
  @Test
  public void testDeleteMetadata(){
    DeferredResult<Boolean> result = new DeferredResult<>();
    DeleteMetadataTask sut = new DeleteMetadataTask(result, repository, ALGORITHM, HASH);
    sut.run();
    assertTrue(result.getResult() == Boolean.TRUE);
  }
  
  @Test
  public void testAlgorithmNotSupported(){
    DeferredResult<Boolean> result = new DeferredResult<>();
    DeleteMetadataTask sut = new DeleteMetadataTask(result, repository, BAD_ALGORITHM, HASH);
    sut.run();
    assertTrue(result.getResult() instanceof UnsupportedAlgorithmException);
  }
}
