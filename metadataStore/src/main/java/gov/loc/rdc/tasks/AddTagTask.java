package gov.loc.rdc.tasks;

import gov.loc.rdc.repositories.MetadataRepository;

import org.springframework.web.context.request.async.DeferredResult;

public class AddTagTask extends AbstractMetadataStoreTask implements Runnable {
  private final String tag;
  private final DeferredResult<Boolean> result;
  public AddTagTask(final DeferredResult<Boolean> result, final MetadataRepository repository, final String hash, final String tag) {
    super(repository, hash);
    this.result = result;
    this.tag = tag;
  }

  @Override
  public void run() {
    logger.debug("Adding tag [{}] to hash [{}]", tag, hash);
    repository.saveTagToHash(tag, hash);
    result.setResult(true);
  }

  @Override
  protected DeferredResult<?> getResult() {
    return result;
  }
}
