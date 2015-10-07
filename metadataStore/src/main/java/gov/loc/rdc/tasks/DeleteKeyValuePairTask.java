package gov.loc.rdc.tasks;

import gov.loc.rdc.entities.KeyValuePair;
import gov.loc.rdc.repositories.MetadataRepository;

import org.springframework.web.context.request.async.DeferredResult;

public class DeleteKeyValuePairTask extends KeyValuePairTask implements Runnable {
  public DeleteKeyValuePairTask(final DeferredResult<Boolean> result, final MetadataRepository repository, final String algorithm, final String hash, final KeyValuePair<String, String> pair) {
    super(result, repository, algorithm, hash, pair);
  }

  @SuppressWarnings("unchecked")
  @Override
  protected void doTaskWork() {
    repository.deleteKeyValueFromHash(pair, hash);
    ((DeferredResult<Boolean>) result).setResult(true);
  }
}
