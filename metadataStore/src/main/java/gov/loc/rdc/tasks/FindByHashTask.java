package gov.loc.rdc.tasks;

import gov.loc.rdc.entities.Metadata;
import gov.loc.rdc.repositories.MetadataRepository;

import org.springframework.web.context.request.async.DeferredResult;

public class FindByHashTask extends MetadataStoreTask implements Runnable {

  public FindByHashTask(final DeferredResult<Metadata> result, final MetadataRepository repository, final String algorithm, final String hash) {
    super(result, repository, algorithm, hash);
  }

  @SuppressWarnings("unchecked")
  @Override
  protected void doTaskWork() {
    Metadata data = repository.findByHash(hash);
    if (data == null) {
      data = new Metadata();
    }
    ((DeferredResult<Metadata>) result).setResult(data);
  }

}
