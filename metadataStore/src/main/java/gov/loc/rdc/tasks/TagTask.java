package gov.loc.rdc.tasks;

import gov.loc.rdc.repositories.MetadataRepository;

import org.springframework.web.context.request.async.DeferredResult;

public abstract class TagTask extends MetadataStoreTask{
  protected final String tag;
  
  public TagTask(final DeferredResult<?> result, 
                            final MetadataRepository repository, 
                            final String algorithm, final String hash,
                            final String tag){
    super(result, repository, algorithm, hash);
    this.tag = tag;
  }
}
