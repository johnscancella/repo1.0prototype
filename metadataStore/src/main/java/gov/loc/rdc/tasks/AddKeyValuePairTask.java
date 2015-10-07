package gov.loc.rdc.tasks;

import gov.loc.rdc.entities.KeyValuePair;
import gov.loc.rdc.repositories.MetadataRepository;

import org.springframework.web.context.request.async.DeferredResult;

public class AddKeyValuePairTask extends KeyValuePairTask implements Runnable {
  public AddKeyValuePairTask(final DeferredResult<Boolean> result, final MetadataRepository repository, final String algorithm, final String hash,final KeyValuePair<String, String> pair) {
    super(result, repository, algorithm, hash, pair);
  }

  @SuppressWarnings("unchecked")
  @Override
  protected void doTaskWork() {
    logger.debug("Adding key value pair [{}] to hash [{}]", pair, hash);
    repository.saveKeyValuePairToHash(pair, hash);
    ((DeferredResult<Boolean>) result).setResult(true);
  }
}
