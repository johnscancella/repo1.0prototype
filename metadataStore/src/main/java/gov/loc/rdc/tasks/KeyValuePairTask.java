package gov.loc.rdc.tasks;

import gov.loc.rdc.entities.KeyValuePair;
import gov.loc.rdc.repositories.MetadataRepository;

import org.springframework.web.context.request.async.DeferredResult;

public abstract class KeyValuePairTask extends MetadataStoreTask{
  protected final KeyValuePair<String, String> pair;
  
  public KeyValuePairTask(final DeferredResult<?> result, 
                                final MetadataRepository repository, 
                                final String algorithm, final String hash, 
                                final KeyValuePair<String, String> pair){
    super(result, repository, algorithm, hash);
    this.pair = pair;
  }
}
