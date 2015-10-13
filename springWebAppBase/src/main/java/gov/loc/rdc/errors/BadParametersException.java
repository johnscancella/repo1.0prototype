package gov.loc.rdc.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BadParametersException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public BadParametersException(String message, Throwable cause) {
    super(message, cause);
  }
}