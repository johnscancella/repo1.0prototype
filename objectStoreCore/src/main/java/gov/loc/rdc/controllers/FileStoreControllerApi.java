package gov.loc.rdc.controllers;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

/**
 * Handles storing and getting files based on their hash.
 */
public interface FileStoreControllerApi{
  public DeferredResult<byte[]> getFile(@PathVariable String algorithm, @PathVariable String hash);
  
  public DeferredResult<String> storeFile(@RequestParam(value="file") MultipartFile file);
  
  public DeferredResult<Boolean> fileExists(@PathVariable String algorithm, @PathVariable String hash);
}
