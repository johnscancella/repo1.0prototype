package gov.loc.rdc.tasks;

import gov.loc.rdc.entities.Metadata;
import gov.loc.rdc.errors.UnsupportedAlgorithm;
import gov.loc.rdc.hash.HashAlgorithm;
import gov.loc.rdc.repositories.MetadataRepository;

import java.util.ArrayList;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.async.DeferredResult;

public class FindByHashTask implements Runnable{
  private static final Logger logger = LoggerFactory.getLogger(FindByHashTask.class);
  
  private final DeferredResult<Metadata> result;
  private final MetadataRepository repository;
  private final String algorithm;
  private final String hash;
  
  public FindByHashTask(final DeferredResult<Metadata> result, final MetadataRepository repository, final String algorithm, final String hash) {
    this.result = result;
    this.repository = repository;
    this.algorithm = algorithm;
    this.hash = hash;
  }
  
  @Override
  public void run() {
    if(!HashAlgorithm.algorithmSupported(algorithm)){
      logger.info("User tried to get stored file using unsupported hashing algorithm [{}]", algorithm);
      result.setErrorResult(new UnsupportedAlgorithm("Only sha256 is currently supported for hashing algorithm"));
    }
    else{
      Metadata data = repository.findByHash(hash);
      if(data == null){
        data = new Metadata("NO HASH", new HashSet<>(), new ArrayList<>());
      }
      result.setResult(data);
    }
  }
}