package gov.loc.rdc.tasks;

import gov.loc.rdc.repositories.MetadataRepository;

import org.springframework.web.context.request.async.DeferredResult;

public class DeleteMetadataTask extends AbstractMetadataStoreTask implements Runnable {
  private final DeferredResult<Boolean> result;
  
  public DeleteMetadataTask(final DeferredResult<Boolean> result, final MetadataRepository repository, final String hash) {
    super(repository, hash);
    this.result = result;
  }

  @Override
  public void run() {
    repository.deleteHash(hash);
    result.setResult(true);
  }


  @Override
  protected DeferredResult<?> getResult() {
    return result;
  }
}
