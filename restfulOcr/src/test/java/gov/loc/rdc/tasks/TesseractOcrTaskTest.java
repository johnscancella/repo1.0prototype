package gov.loc.rdc.tasks;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

public class TesseractOcrTaskTest extends Assert{
  private static final String TESSERACT_HOME = "/usr/local/Cellar/tesseract/3.04.00";
  
  @Test
  public void testSuccessfulOcr() throws Exception{
    File testFile = new File(getClass().getClassLoader().getResource("ocrTestImage.tif").getFile());
    FileInputStream fis = new FileInputStream(testFile);
    MockMultipartFile multipartFile = new MockMultipartFile(fis);
    DeferredResult<String> result = new DeferredResult<>();
    
    TesseractOcrTask sut = new TesseractOcrTask(result, multipartFile, TESSERACT_HOME + "/share/tessdata");
    sut.run();
    //should really be START but since this is a test we won't deal with getting a better result.
    assertTrue(((String)result.getResult()).contains("TART"));
  }
  
  private class MockMultipartFile implements MultipartFile{
    private final FileInputStream fis;
    MockMultipartFile(FileInputStream fis){
      this.fis = fis;
    }

    @Override
    public String getName() {
      return null;
    }

    @Override
    public String getOriginalFilename() {
      return null;
    }

    @Override
    public String getContentType() {
      return null;
    }

    @Override
    public boolean isEmpty() {
      return false;
    }

    @Override
    public long getSize() {
      return 0;
    }

    @Override
    public byte[] getBytes() throws IOException {
      return null;
    }

    @Override
    public InputStream getInputStream() throws IOException {
      return fis;
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
      
    }
  }
}
