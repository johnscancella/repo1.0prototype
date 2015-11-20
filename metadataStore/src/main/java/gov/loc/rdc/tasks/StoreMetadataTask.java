package gov.loc.rdc.tasks;

import gov.loc.rdc.entities.KeyValuePair;
import gov.loc.rdc.entities.Metadata;
import gov.loc.rdc.errors.JsonParamParseFailException;
import gov.loc.rdc.repositories.MetadataRepository;
import gov.loc.rdc.utils.KeyValueJsonConverter;

import java.util.List;
import java.util.Set;

import org.springframework.web.context.request.async.DeferredResult;

public class StoreMetadataTask extends AbstractMetadataStoreTask implements Runnable {
  private final Set<String> tags;
  private final String keyValuePairsAsJson;
  private final DeferredResult<Boolean> result;

  public StoreMetadataTask(final DeferredResult<Boolean> result, final MetadataRepository repository, final String hash, final Set<String> tags, final String keyValuePairsAsJson) {
    super(repository, hash);
    this.result = result;
    this.tags = tags;
    this.keyValuePairsAsJson = keyValuePairsAsJson;
  }

  @Override
  public void run() {
    try {
      List<KeyValuePair<String, String>> keyValuePairs = KeyValueJsonConverter.convertToPairs(keyValuePairsAsJson);
      Metadata data = new Metadata(hash, tags, keyValuePairs);
      logger.debug("Saving metadata [{}]", data);
      repository.save(data);
      result.setResult(true);
    }
    catch (Exception e) {
      logger.error("Failed to store metadata. Perhaps [{}] is not valid JSON?", keyValuePairsAsJson, e);
      result.setErrorResult(new JsonParamParseFailException("Failed to store metadata. Perhaps it is not valid JSON?"));
    }
  }

  @Override
  protected DeferredResult<?> getResult() {
    return result;
  }

}
