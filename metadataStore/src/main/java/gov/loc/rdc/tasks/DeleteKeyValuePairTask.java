package gov.loc.rdc.tasks;

import gov.loc.rdc.entities.KeyValuePair;
import gov.loc.rdc.repositories.MetadataRepository;

import org.springframework.web.context.request.async.DeferredResult;

public class DeleteKeyValuePairTask extends AbstractKeyValuePairTask implements Runnable {
  private final DeferredResult<Boolean> result;
  
  public DeleteKeyValuePairTask(final DeferredResult<Boolean> result, final MetadataRepository repository, final String algorithm, final String hash, final KeyValuePair<String, String> pair) {
    super(repository, algorithm, hash, pair);
    this.result = result;
  }

  @Override
  protected void doTaskWork() {
    repository.deleteKeyValueFromHash(pair, hash);
    result.setResult(true);
  }

  @Override
  protected DeferredResult<?> getResult() {
    return result;
  }
}
