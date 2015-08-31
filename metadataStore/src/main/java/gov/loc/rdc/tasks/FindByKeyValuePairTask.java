package gov.loc.rdc.tasks;

import gov.loc.rdc.entities.KeyValuePair;
import gov.loc.rdc.entities.Metadata;
import gov.loc.rdc.repositories.MetadataRepository;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.async.DeferredResult;

public class FindByKeyValuePairTask implements Runnable{
  private static final Logger logger = LoggerFactory.getLogger(FindByKeyValuePairTask.class);
  
  private final DeferredResult<List<Metadata>> result;
  private final MetadataRepository repository;
  private final KeyValuePair<String, String> pair;
  
  public FindByKeyValuePairTask(final DeferredResult<List<Metadata>> result, final MetadataRepository repository, final KeyValuePair<String, String> pair){
    this.result = result;
    this.repository = repository;
    this.pair = pair;
  }
  
  @Override
  public void run() {
    List<Metadata> datas = repository.findByKeyValuePair(pair);
    if(datas == null){
      logger.debug("No metadata found for keyvalue pair [{}]", pair);
      datas = new ArrayList<>();
    }
    
    result.setResult(datas);
  }
}
