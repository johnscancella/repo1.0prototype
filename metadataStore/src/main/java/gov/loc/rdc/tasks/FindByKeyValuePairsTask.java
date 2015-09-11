package gov.loc.rdc.tasks;

import gov.loc.rdc.entities.KeyValuePair;
import gov.loc.rdc.entities.Metadata;
import gov.loc.rdc.errors.JsonParamParseFail;
import gov.loc.rdc.repositories.MetadataRepository;
import gov.loc.rdc.utils.KeyValueJsonConverter;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.async.DeferredResult;

public class FindByKeyValuePairsTask implements Runnable{
  private static final Logger logger = LoggerFactory.getLogger(FindByKeyValuePairsTask.class);
  
  private final DeferredResult<List<Metadata>> result;
  private final MetadataRepository repository;
  private final String keyValuePairsAsJson;
  
  public FindByKeyValuePairsTask(final DeferredResult<List<Metadata>> result, final MetadataRepository repository, final String keyValuePairsAsJson) {
    this.result = result;
    this.repository = repository;
    this.keyValuePairsAsJson = keyValuePairsAsJson;
  }
  
  @Override
  public void run() {
    List<Metadata> datas;
    try{
      List<KeyValuePair<String, String>> keyValuePairs = KeyValueJsonConverter.convertToPairs(keyValuePairsAsJson);
      datas = repository.findByKeyValuePairs(keyValuePairs);
      if(datas.size() == 0){
        logger.debug("No metadata found for json key value pairs [{}]", keyValuePairsAsJson);
      }
      result.setResult(datas);
    }
    catch(Exception e){
      logger.error("Failed to find metadata. Perhaps [{}] is not valid JSON?", keyValuePairsAsJson, e);
      result.setErrorResult(new JsonParamParseFail("Failed to find metadata. Perhaps it is not valid JSON"));
    }
  }
}
