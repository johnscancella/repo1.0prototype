package gov.loc.rdc.domain;

import java.io.IOException;
import java.util.Objects;

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
  
  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    ObjectStoreByteArrayResource other = (ObjectStoreByteArrayResource) obj;
    
    return Objects.equals(this.filename, other.filename) && super.equals(obj);
  }

  @Override
  public int hashCode() {
    return super.hashCode() + Objects.hash(filename);
  }
  

}
