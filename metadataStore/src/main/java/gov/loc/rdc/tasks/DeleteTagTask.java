package gov.loc.rdc.tasks;

import gov.loc.rdc.repositories.MetadataRepository;

import org.springframework.web.context.request.async.DeferredResult;

public class DeleteTagTask extends TagTask implements Runnable {

  public DeleteTagTask(final DeferredResult<Boolean> result, final MetadataRepository repository, final String algorithm, final String hash, final String tag) {
    super(result, repository, algorithm, hash, tag);
  }

  @SuppressWarnings("unchecked")
  @Override
  protected void doTaskWork() {
    repository.deleteTagFromHash(tag, hash);
    ((DeferredResult<Boolean>) result).setResult(true);
  }
}
