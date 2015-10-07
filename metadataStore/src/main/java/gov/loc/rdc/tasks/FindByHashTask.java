package gov.loc.rdc.tasks;

import gov.loc.rdc.entities.Metadata;
import gov.loc.rdc.repositories.MetadataRepository;

import org.springframework.web.context.request.async.DeferredResult;

public class FindByHashTask extends AbstractMetadataStoreTask implements Runnable {
  private final DeferredResult<Metadata> result;
  
  public FindByHashTask(final DeferredResult<Metadata> result, final MetadataRepository repository, final String algorithm, final String hash) {
    super(repository, algorithm, hash);
    this.result = result;
  }

  @Override
  protected void doTaskWork() {
    Metadata data = repository.findByHash(hash);
    if (data == null) {
      data = new Metadata();
    }
    result.setResult(data);
  }

  @Override
  protected DeferredResult<?> getResult() {
    return result;
  }

}
