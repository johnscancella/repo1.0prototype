package gov.loc.rdc.tasks;

import java.io.File;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.web.context.request.async.DeferredResult;

public class RetrieveFileExistenceTaskTest extends Assert{
  @Rule
  public TemporaryFolder folder= new TemporaryFolder();

  @Test
  public void testFileExists() throws Exception{
    File hashFolder = folder.newFolder("AB", "C1", "23");
    File file = new File(hashFolder, "ABC123");
    file.createNewFile();
    DeferredResult<Boolean> result = new DeferredResult<Boolean>();
    RetrieveFileExistenceTask sut = new RetrieveFileExistenceTask(result, folder.getRoot(), "SHA-256", "ABC123");
    sut.doTaskWork();
    assertTrue((Boolean)result.getResult());
  }
  
  @Test
  public void testFileDoesntExists(){
    DeferredResult<Boolean> result = new DeferredResult<Boolean>();
    RetrieveFileExistenceTask sut = new RetrieveFileExistenceTask(result, folder.getRoot(), "SHA-256", "ABC123XYZ");
    sut.doTaskWork();
    assertFalse((Boolean)result.getResult());
  }
}
