package gov.loc.rdc.tasks;

import gov.loc.rdc.errors.ResourceNotFoundException;

import java.io.File;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.web.context.request.async.DeferredResult;

public class RetrieveFileTaskTest extends Assert{
  @Rule
  public TemporaryFolder folder= new TemporaryFolder();

  @Test
  public void testGetFile() throws Exception{
    File hashFolder = folder.newFolder("AB", "C1", "23");
    File file = new File(hashFolder, "ABC123");
    file.createNewFile();
    DeferredResult<byte[]> result = new DeferredResult<byte[]>();
    RetrieveFileTask sut = new RetrieveFileTask(result, folder.getRoot(), "ABC123");
    sut.run();
    assertTrue(result.getResult() != null);
  }
  
  @Test
  public void testGetFileWhenFileDoesntExistReturnsResourceNotFoundException(){
    DeferredResult<byte[]> result = new DeferredResult<byte[]>();
    RetrieveFileTask sut = new RetrieveFileTask(result, folder.getRoot(), "ABC123XYZ");
    sut.run();
    assertTrue(result.getResult() instanceof ResourceNotFoundException);
  }
}
