package gov.loc.rdc.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class UnsupportedOcrImplementation  extends RuntimeException {
  private static final long serialVersionUID = 1L;
  public UnsupportedOcrImplementation(String message){
    super(message);
  }
}