package gov.loc.rdc.tasks;

import gov.loc.rdc.entities.KeyValuePair;
import gov.loc.rdc.errors.UnsupportedAlgorithm;
import gov.loc.rdc.hash.HashAlgorithm;
import gov.loc.rdc.repositories.MetadataRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.async.DeferredResult;

public class AddKeyValuePairTask implements Runnable{
  private static final Logger logger = LoggerFactory.getLogger(FindByTagsTask.class);
  
  private final DeferredResult<Boolean> result;
  private final MetadataRepository repository;
  private final String algorithm;
  private final String hash;
  private final KeyValuePair<String, String> pair;
  
  public AddKeyValuePairTask(final DeferredResult<Boolean> result, 
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
      result.setErrorResult(new UnsupportedAlgorithm("Only sha256 is currently supported for hashing algorithm"));
    }
    
    logger.debug("Adding key value pair [{}] to hash [{}]", pair, hash);
    repository.saveKeyValuePairToHash(pair, hash);
    result.setResult(true);
  }
}
