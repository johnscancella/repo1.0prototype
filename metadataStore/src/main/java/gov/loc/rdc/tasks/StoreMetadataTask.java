package gov.loc.rdc.tasks;

import java.util.List;
import java.util.Set;

import gov.loc.rdc.entities.KeyValuePair;
import gov.loc.rdc.entities.Metadata;
import gov.loc.rdc.errors.JsonParamParseFail;
import gov.loc.rdc.errors.UnsupportedAlgorithm;
import gov.loc.rdc.hash.HashAlgorithm;
import gov.loc.rdc.repositories.MetadataRepository;
import gov.loc.rdc.utils.KeyValueJsonConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.async.DeferredResult;

public class StoreMetadataTask implements Runnable{
  private static final Logger logger = LoggerFactory.getLogger(FindByTagsTask.class);
  
  private final DeferredResult<Boolean> result;
  private final MetadataRepository repository;
  private final String algorithm;
  private final String hash;
  private final Set<String> tags;
  private final String keyValuePairsAsJson;
  
  public StoreMetadataTask(final DeferredResult<Boolean> result, 
                            final MetadataRepository repository, 
                            final String algorithm, final String hash,
                            final Set<String> tags,
                            final String keyValuePairsAsJson){
    this.result = result;
    this.repository = repository;
    this.algorithm = algorithm;
    this.hash = hash;
    this.tags = tags;
    this.keyValuePairsAsJson = keyValuePairsAsJson;
  }
  
  @Override
  public void run() {
    if(!HashAlgorithm.algorithmSupported(algorithm)){
      logger.info("User tried to get stored file using unsupported hashing algorithm [{}]", algorithm);
      result.setErrorResult(new UnsupportedAlgorithm("Only sha256 is currently supported for hashing algorithm"));
    }
    
    try{
      List<KeyValuePair<String, String>> keyValuePairs = KeyValueJsonConverter.convertToPairs(keyValuePairsAsJson);
      Metadata data = new Metadata(hash, tags, keyValuePairs);
      logger.debug("Saving metadata [{}]", data);
      repository.save(data);
      result.setResult(true);
    }
    catch(Exception e){
      logger.error("Failed to store metadata. Perhaps [{}] is not valid JSON?", keyValuePairsAsJson, e);
      result.setErrorResult(new JsonParamParseFail("Failed to store metadata. Perhaps it is not valid JSON?"));
    }
  }
}
