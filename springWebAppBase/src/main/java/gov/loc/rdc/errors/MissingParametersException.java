package gov.loc.rdc.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * For when a request does not contains one or more params.
 * Have spring throw http 400 response when encountering this error.
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class MissingParametersException  extends RuntimeException {
  private static final long serialVersionUID = 1L;
  public MissingParametersException(String message){
    super(message);
  }
}