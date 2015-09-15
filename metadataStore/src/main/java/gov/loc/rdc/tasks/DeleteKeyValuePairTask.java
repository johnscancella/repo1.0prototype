package gov.loc.rdc.tasks;

import gov.loc.rdc.entities.KeyValuePair;
import gov.loc.rdc.errors.UnsupportedAlgorithmException;
import gov.loc.rdc.hash.HashAlgorithm;
import gov.loc.rdc.repositories.MetadataRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.async.DeferredResult;

public class DeleteKeyValuePairTask implements Runnable{
  private static final Logger logger = LoggerFactory.getLogger(FindByTagsTask.class);
  
  private final DeferredResult<Boolean> result;
  private final MetadataRepository repository;
  private final String algorithm;
  private final String hash;
  private final KeyValuePair<String, String> pair;
  
  public DeleteKeyValuePairTask(final DeferredResult<Boolean> result, 
                                final MetadataRepository repository, 
                                final String algorithm, final String hash, 
                                final KeyValuePair<String, String> pair){
    this.result = result;
    this.repository = repository;
    this.algorithm = algorithm;
    this.hash = hash;
    this.pair = pair;
  }
  
  @Override
  public void run() {
    if(!HashAlgorithm.algorithmSupported(algorithm)){
      logger.info("User tried to get stored file using unsupported hashing algorithm [{}]", algorithm);
      result.setErrorResult(new UnsupportedAlgorithmException("Only sha256 is currently supported for hashing algorithm"));
    }
    
    repository.deleteKeyValueFromHash(pair, hash);
    result.setResult(true);
  }
}
