package gov.loc.rdc.tasks;

import gov.loc.rdc.repositories.MetadataRepository;

import org.springframework.web.context.request.async.DeferredResult;

public class DeleteTagTask extends AbstractMetadataStoreTask implements Runnable {
  private final String tag;
  private final DeferredResult<Boolean> result;

  public DeleteTagTask(final DeferredResult<Boolean> result, final MetadataRepository repository, final String hash, final String tag) {
    super(repository, hash);
    this.result = result;
    this.tag = tag;
  }

  @Override
  public void run() {
    repository.deleteTagFromHash(tag, hash);
    result.setResult(true);
  }

  @Override
  protected DeferredResult<?> getResult() {
    return result;
  }
}
