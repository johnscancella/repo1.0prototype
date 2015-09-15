package gov.loc.rdc.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class MissingParametersException  extends RuntimeException {
  private static final long serialVersionUID = 1L;
  public MissingParametersException(String message){
    super(message);
  }
}