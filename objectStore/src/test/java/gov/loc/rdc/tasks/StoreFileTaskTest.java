package gov.loc.rdc.tasks;

import gov.loc.rdc.hash.HashPathUtils;
import gov.loc.rdc.hash.SHA256Hasher;
import gov.loc.rdc.repositories.FileStoreRepository;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.context.request.async.DeferredResult;

@RunWith(MockitoJUnitRunner.class)
public class StoreFileTaskTest extends Assert implements HashPathUtils{
  @Rule
  public TemporaryFolder folder= new TemporaryFolder();
  
  @Mock
  private FileStoreRepository mockFileStoreRepo;

  @Test
  public void testStoreFile() throws Exception{
    ClassLoader classLoader = getClass().getClassLoader();
    File testFile = new File(classLoader.getResource("testFile.txt").getFile());
    byte[] data = Files.readAllBytes(Paths.get(testFile.toURI()));
    String hash = SHA256Hasher.hash(data);
    File hashFolder = folder.newFolder("hashFolder");
    File expectedFile = computeStoredLocation(hashFolder, hash);
    
    DeferredResult<String> result = new DeferredResult<String>();
    MockMultipartFile file = new MockMultipartFile("fooName", data);
    
    StoreFileTask sut = new StoreFileTask(result, file, hashFolder, mockFileStoreRepo);
    sut.run();
    assertTrue(expectedFile.exists());
  }
  
  @Test
  public void testStoreEmptyFile() throws Exception{
    File hashFolder = folder.newFolder("AB", "C1", "23");
    DeferredResult<String> result = new DeferredResult<String>();
    MockMultipartFile file = new MockMultipartFile("fooName", new byte[]{});
    StoreFileTask sut = new StoreFileTask(result, file, hashFolder, mockFileStoreRepo);
    sut.run();
    assertEquals("Hash was not computed.", result.getResult()); 
  }
}
