package gov.loc.rdc.tasks;

import gov.loc.rdc.entities.KeyValuePair;
import gov.loc.rdc.entities.Metadata;
import gov.loc.rdc.errors.UnsupportedAlgorithmException;

import java.util.ArrayList;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.springframework.web.context.request.async.DeferredResult;

public class AddKeyValuePairTaskTest extends TaskTest{
  
  @Before
  public void setup(){
    clearDatabase();
  }
  
  @Test
  public void testAddKeyValuePairToNonExistingHash(){
    DeferredResult<Boolean> result = new DeferredResult<>();
    AddKeyValuePairTask sut = new AddKeyValuePairTask(result, repository, ALGORITHM, BAD_HASH, new KeyValuePair<String, String>(KEY1, VALUE1));
    sut.run();
    assertTrue(result.getResult() == Boolean.TRUE);
  }
  
  @Test
  public void testAddKeyValuePair(){
    saveData();
    DeferredResult<Boolean> result = new DeferredResult<>();
    AddKeyValuePairTask sut = new AddKeyValuePairTask(result, repository, ALGORITHM, HASH, new KeyValuePair<String, String>(KEY1, VALUE1));
    sut.run();
    assertTrue(result.getResult() == Boolean.TRUE);
  }
  
  @Test
  public void testAlgorithmNotSupported(){
    saveData();
    DeferredResult<Boolean> result = new DeferredResult<>();
    AddKeyValuePairTask sut = new AddKeyValuePairTask(result, repository, BAD_ALGORITHM, HASH, new KeyValuePair<String, String>(KEY1, VALUE1));
    sut.run();
    assertTrue(result.getResult() instanceof UnsupportedAlgorithmException);
  }
  
  private void saveData(){
    tags = new HashSet<>();
    keyValuePairs = new ArrayList<>();
    
    Metadata data = new Metadata(HASH, tags, keyValuePairs);
    repository.save(data);
  }

}
