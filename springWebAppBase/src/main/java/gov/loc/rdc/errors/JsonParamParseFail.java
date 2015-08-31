package gov.loc.rdc.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * For when a request contains one or more params as JSON an an error occurred.
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class JsonParamParseFail  extends RuntimeException {
  private static final long serialVersionUID = 1L;
  
  public JsonParamParseFail(String message){
    super(message);
  }
}