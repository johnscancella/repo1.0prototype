package gov.loc.rdc.tasks;

import gov.loc.rdc.entities.Metadata;
import gov.loc.rdc.repositories.MetadataRepository;

import org.springframework.web.context.request.async.DeferredResult;

public class FindByHashTask extends AbstractMetadataStoreTask implements Runnable {
  private final DeferredResult<Metadata> result;
  
  public FindByHashTask(final DeferredResult<Metadata> result, final MetadataRepository repository, final String hash) {
    super(repository, hash);
    this.result = result;
  }

  @Override
  public void run() {
    Metadata data = repository.findByHash(hash);
    if (data == null) {
      data = new Metadata(hash);
    }
    result.setResult(data);
  }

  @Override
  protected DeferredResult<?> getResult() {
    return result;
  }

}
