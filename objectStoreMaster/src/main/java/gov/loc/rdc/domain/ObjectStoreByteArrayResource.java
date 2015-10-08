package gov.loc.rdc.domain;

import java.io.IOException;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;


public class ObjectStoreByteArrayResource extends ByteArrayResource {
  private String filename;
  
  public ObjectStoreByteArrayResource(MultipartFile file) throws IOException {
    super(file.getBytes());
    filename = file.getOriginalFilename();
  }
  
  public ObjectStoreByteArrayResource(byte[] byteArray) {
    super(byteArray);
  }
  
  @Override
  public String getFilename(){
      return filename;
  }

}
