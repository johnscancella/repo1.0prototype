package gov.loc.rdc.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class UnsupportedAlgorithm  extends RuntimeException {
  private static final long serialVersionUID = 1L;
  public UnsupportedAlgorithm(String message){
    super(message);
  }
}