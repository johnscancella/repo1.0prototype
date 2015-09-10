package gov.loc.rdc.controllers;

import java.io.File;
import java.io.FileInputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class OcrControllerTest extends Assert {
  private static final String TESSERACT_HOME = "/usr/local/Cellar/tesseract/3.04.00/share/tessdata";
  private MockMvc mockMvc;
  
  @Before
  public void setup(){
    OcrController ocrController = new OcrController();
    ocrController.setTessdataPath(TESSERACT_HOME);
    ocrController.setThreadExecutor(new MockThreadpool());
    this.mockMvc = MockMvcBuilders.standaloneSetup(ocrController).build();
  }
  
  @Test
  @Ignore //cause jenkins can't install tesseract right now
  public void testGetOcr() throws Exception{
    File testFile = new File(getClass().getClassLoader().getResource("ocrTestImage.tif").getFile());
    FileInputStream fis = new FileInputStream(testFile);
    MockMultipartFile multipartFile = new MockMultipartFile("file", fis);
    
    mockMvc.perform(MockMvcRequestBuilders.fileUpload("/ocr/tesseract")
        .file(multipartFile))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }
  
  @Test
  @Ignore //cause jenkins can't install tesseract right now
  public void testInvalidImpl() throws Exception{
    File testFile = new File(getClass().getClassLoader().getResource("ocrTestImage.tif").getFile());
    FileInputStream fis = new FileInputStream(testFile);
    MockMultipartFile multipartFile = new MockMultipartFile("file", fis);
    
    mockMvc.perform(MockMvcRequestBuilders.fileUpload("/ocr/badImpl")
        .file(multipartFile))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }
  
  private static class MockThreadpool extends ThreadPoolTaskExecutor {
    private static final long serialVersionUID = 1L;
    
    @Override
    public void execute(Runnable task){
      task.run();
    }
  }
}
