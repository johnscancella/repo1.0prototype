package gov.loc.rdc.tasks;

import gov.loc.rdc.entities.Metadata;
import gov.loc.rdc.errors.MissingParameters;
import gov.loc.rdc.repositories.MetadataRepository;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.async.DeferredResult;

public class FindByTagsTask implements Runnable{
  private static final Logger logger = LoggerFactory.getLogger(FindByTagsTask.class);
  
  private final DeferredResult<List<Metadata>> result;
  private final MetadataRepository repository;
  private final String[] tags;
  
  public FindByTagsTask(final DeferredResult<List<Metadata>> result, final MetadataRepository repository, final String... tags) {
    this.result = result;
    this.repository = repository;
    this.tags = tags;
  }

  @Override
  public void run() {
    if(tags.length == 0){
      logger.error("At least one tag is required to search by tag.");
      result.setErrorResult(new MissingParameters("At least one tag is required to search by tag."));
    }
    else if(tags.length == 1){
      searchForOneTag();
    }
    else{
      searchForManyTags();
    }
  }
  
  private void searchForOneTag(){
    List<Metadata> datas = repository.findByTag(tags[0]);
    if(datas.size() == 0){
      logger.debug("No metadata found for tag [{}]", tags[0]);
    }
    
    result.setResult(datas);
  }
  
  private void searchForManyTags(){
    List<Metadata> datas = repository.findByTags(Arrays.asList(tags));
    if(datas.size() == 0){
      logger.debug("No metadata found for tags [{}]", Arrays.asList(tags));
    }
    
    result.setResult(datas);
  }
}
