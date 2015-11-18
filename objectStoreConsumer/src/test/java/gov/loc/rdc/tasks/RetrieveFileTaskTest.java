package gov.loc.rdc.tasks;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.web.context.request.async.DeferredResult;

import gov.loc.rdc.errors.ResourceNotFoundException;

public class RetrieveFileTaskTest extends Assert {
  @Test
  public void testRetrieveFileWhenItDoesNotExist(){
    DeferredResult<byte[]> result = new DeferredResult<>();
    RetrieveFileTask sut = new RetrieveFileTask(result, new File("/tmp"), "ABC123");
    
    sut.run();
    assertTrue(result.getResult() instanceof ResourceNotFoundException);
  }
  
  @Test
  public void testRetrieveFileWhenItDoesExist(){
    DeferredResult<byte[]> result = new DeferredResult<>();
    File testDir = new File(getClass().getClassLoader().getResource("validObjectStore").getFile());
    RetrieveFileTask sut = new RetrieveFileTask(result, testDir, "b6668cf8c46c7075e18215d922e7812ca082fa6cc34668d00a6c20aee4551fb6");
    
    sut.run();
    assertFalse(result.getResult() instanceof ResourceNotFoundException);
  }
}
