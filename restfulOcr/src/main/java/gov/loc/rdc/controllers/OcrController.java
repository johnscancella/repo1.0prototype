package gov.loc.rdc.controllers;

import gov.loc.rdc.errors.UnsupportedOcrImplementation;
import gov.loc.rdc.tasks.TesseractOcrTask;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class OcrController {
  private static final Logger logger = LoggerFactory.getLogger(OcrController.class);
  private static final String TESSERACT = "tesseract";
  
  @Resource(name="threadPoolTaskExecutor")
  private ThreadPoolTaskExecutor threadExecutor;
  
  //Sets path to tessdata for tesseract
  @Value("${tessdata-path:/tmp}")
  private String tessdataPath;
  
  @PostConstruct
  public void info(){
    logger.info("Tesseract tessdata directory is set to [{}]", tessdataPath);
  }
  
  @RequestMapping(value="/ocr/{ocrImpl}", method={RequestMethod.POST, RequestMethod.PUT})
  public DeferredResult<String> performOcr(@RequestParam(value="file") MultipartFile file, @PathVariable String ocrImpl){
    DeferredResult<String> result = new DeferredResult<String>();
    
    if(!TESSERACT.equalsIgnoreCase(ocrImpl)){
      logger.error("Unsupported OCR implementation was requested [{}]. Currently only [{}] is acceptable", ocrImpl, TESSERACT);
      StringBuilder sb = new StringBuilder();
      sb.append("OCR implementation request of [").append(ocrImpl).append("] is unrecognized.").append("\n");
      sb.append("Currently only ").append(TESSERACT).append(" is supported");
      throw new UnsupportedOcrImplementation("OCR implementation request of [" + ocrImpl + "] is unrecognized. Currently only tesseract is supported.");
    }
    
    TesseractOcrTask task = new TesseractOcrTask(result, file, tessdataPath);
    threadExecutor.execute(task);
    
    return result;
  }

  //only for testing
  protected void setThreadExecutor(ThreadPoolTaskExecutor threadExecutor) {
    this.threadExecutor = threadExecutor;
  }

  //only for testing
  protected void setTessdataPath(String tessdataPath) {
    this.tessdataPath = tessdataPath;
  }
}
