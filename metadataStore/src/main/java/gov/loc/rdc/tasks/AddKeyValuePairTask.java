package gov.loc.rdc.tasks;

import org.springframework.web.context.request.async.DeferredResult;

import gov.loc.rdc.entities.KeyValuePair;
import gov.loc.rdc.repositories.MetadataRepository;

public class AddKeyValuePairTask extends AbstractMetadataStoreTask implements Runnable {
  private final KeyValuePair<String, String> pair;
  private final DeferredResult<Boolean> result; 
  
  public AddKeyValuePairTask(final DeferredResult<Boolean> result, final MetadataRepository repository, final String hash,final KeyValuePair<String, String> pair) {
    super(repository, hash);
    this.result = result;
    this.pair = pair;
  }

  @Override
  public void run() {
    logger.debug("Adding key value pair [{}] to hash [{}]", pair, hash);
    repository.saveKeyValuePairToHash(pair, hash);
    result.setResult(true);
  }

  @Override
  protected DeferredResult<?> getResult() {
    return result;
  }
}
