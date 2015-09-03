package gov.loc.rdc.tasks;

import gov.loc.rdc.entities.Metadata;

import java.util.ArrayList;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.springframework.web.context.request.async.DeferredResult;

public class DeleteMetadataTaskTest extends TaskTest {
  @Before
  public void setup(){
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
}