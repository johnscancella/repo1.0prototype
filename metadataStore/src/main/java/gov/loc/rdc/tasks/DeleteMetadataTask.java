package gov.loc.rdc.tasks;

import gov.loc.rdc.repositories.MetadataRepository;

import org.springframework.web.context.request.async.DeferredResult;

public class DeleteMetadataTask extends MetadataStoreTask implements Runnable {
  public DeleteMetadataTask(final DeferredResult<Boolean> result, final MetadataRepository repository, final String algorithm, final String hash) {
    super(result, repository, algorithm, hash);
  }


  @SuppressWarnings("unchecked")
  @Override
  protected void doTaskWork() {
    repository.deleteHash(hash);
    ((DeferredResult<Boolean>) result).setResult(true);
  }
}
