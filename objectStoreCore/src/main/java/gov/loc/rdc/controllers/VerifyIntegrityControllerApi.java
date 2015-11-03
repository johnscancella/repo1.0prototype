package gov.loc.rdc.controllers;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Responsible for verifying that the object store hasn't changed
 */
@RestController
public interface VerifyIntegrityControllerApi {
  public void restfulVerifyIntegrity(@RequestParam(required=false) String rootDir);
}
