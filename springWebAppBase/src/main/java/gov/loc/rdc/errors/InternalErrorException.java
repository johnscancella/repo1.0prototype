package gov.loc.rdc.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Have spring throw http 500 response when encountering this error.
 */
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalErrorException extends RuntimeException {
  private static final long serialVersionUID = 1L;
  
  public InternalErrorException(Throwable cause){
    super(cause);
  }
  
  public InternalErrorException(String message){
    super(message);
  }
}
