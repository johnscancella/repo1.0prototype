package gov.loc.rdc.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.async.DeferredResult;

import gov.loc.rdc.repositories.MetadataRepository;

public abstract class AbstractMetadataStoreTask implements Runnable {
  protected static final Logger logger = LoggerFactory.getLogger(FindByTagsTask.class);

  protected final MetadataRepository repository;
  protected final String hash;

  public AbstractMetadataStoreTask(final MetadataRepository repository, final String hash) {
    this.repository = repository;
    this.hash = hash;
  }
  
  protected abstract DeferredResult<?> getResult();

}
