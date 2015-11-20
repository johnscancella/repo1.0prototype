package gov.loc.rdc.tasks;

import gov.loc.rdc.entities.KeyValuePair;
import gov.loc.rdc.repositories.MetadataRepository;

import org.springframework.web.context.request.async.DeferredResult;

public class DeleteKeyValuePairTask extends AbstractMetadataStoreTask implements Runnable {
  private final KeyValuePair<String, String> pair;
  private final DeferredResult<Boolean> result;
  
  public DeleteKeyValuePairTask(final DeferredResult<Boolean> result, final MetadataRepository repository, final String hash, final KeyValuePair<String, String> pair) {
    super(repository, hash);
    this.result = result;
    this.pair = pair;
  }

  @Override
  public void run() {
    repository.deleteKeyValueFromHash(pair, hash);
    result.setResult(true);
  }

  @Override
  protected DeferredResult<?> getResult() {
    return result;
  }
}
