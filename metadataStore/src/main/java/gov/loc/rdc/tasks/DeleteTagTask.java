package gov.loc.rdc.tasks;

import gov.loc.rdc.repositories.MetadataRepository;

import org.springframework.web.context.request.async.DeferredResult;

public class DeleteTagTask extends AbstractTagTask implements Runnable {
  private final DeferredResult<Boolean> result;

  public DeleteTagTask(final DeferredResult<Boolean> result, final MetadataRepository repository, final String algorithm, final String hash, final String tag) {
    super(repository, algorithm, hash, tag);
    this.result = result;
  }

  @Override
  protected void doTaskWork() {
    repository.deleteTagFromHash(tag, hash);
    result.setResult(true);
  }

  @Override
  protected DeferredResult<?> getResult() {
    return result;
  }
}
