package gov.loc.rdc.tasks;

import gov.loc.rdc.errors.UnsupportedAlgorithmException;
import gov.loc.rdc.hash.HashAlgorithm;
import gov.loc.rdc.repositories.MetadataRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.async.DeferredResult;

public abstract class MetadataStoreTask implements Runnable{
  protected static final Logger logger = LoggerFactory.getLogger(FindByTagsTask.class);
  
  protected final DeferredResult<?> result;
  protected final MetadataRepository repository;
  protected final String algorithm;
  protected final String hash;
  
  public MetadataStoreTask(final DeferredResult<?> result, 
                             final MetadataRepository repository, 
                             final String algorithm, 
                             final String hash){
     this.result = result;
     this.repository = repository;
     this.algorithm = algorithm;
     this.hash = hash;
   }
  
  protected boolean checkAlgorithmIsSupported(){
    if(!HashAlgorithm.algorithmSupported(algorithm)){
      logger.info("User tried to get stored file using unsupported hashing algorithm [{}]", algorithm);
      result.setErrorResult(new UnsupportedAlgorithmException("Only sha256 is currently supported for hashing algorithm"));
      return false;
    }
    
    return true;
  }
  
  @Override
  public void run() {
    if(checkAlgorithmIsSupported()){
      doTaskWork();
    }
  }
  
  protected abstract void doTaskWork();

}
