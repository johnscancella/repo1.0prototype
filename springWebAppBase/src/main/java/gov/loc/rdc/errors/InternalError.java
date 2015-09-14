package gov.loc.rdc.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalError extends RuntimeException {
  private static final long serialVersionUID = 1L;
  
  public InternalError(Throwable cause){
    super(cause);
  }
  
  public InternalError(String message){
    super(message);
  }
}
