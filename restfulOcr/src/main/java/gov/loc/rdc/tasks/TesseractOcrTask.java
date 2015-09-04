package gov.loc.rdc.tasks;

import gov.loc.rdc.errors.InternalError;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import net.sourceforge.tess4j.Tesseract;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

public class TesseractOcrTask implements Runnable{
  private static final Logger logger = LoggerFactory.getLogger(TesseractOcrTask.class);
  
  private final DeferredResult<String> result;
  private final MultipartFile file;
  private final String datapath;
  
  public TesseractOcrTask(DeferredResult<String> result, MultipartFile file, String datapath){
    this.result = result;
    this.file = file;
    this.datapath = datapath;
  }

  @Override
  public void run() {
    Tesseract tesseract = new  Tesseract();
    tesseract.setDatapath(datapath);
    try{
      BufferedImage image = ImageIO.read(file.getInputStream());
      String ocrText = tesseract.doOCR(image);
      logger.debug("OCR generated text is [{}]", ocrText);
      result.setResult(ocrText);
    }
    catch(Exception e){
      logger.error("Failed to do OCR.", e);
      result.setErrorResult(new InternalError(e));
    }
  }
}
