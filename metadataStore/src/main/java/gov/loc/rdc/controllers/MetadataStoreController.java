package gov.loc.rdc.controllers;

import gov.loc.rdc.entities.KeyValuePair;
import gov.loc.rdc.entities.Metadata;
import gov.loc.rdc.repositories.MetadataRepository;
import gov.loc.rdc.tasks.AddKeyValuePairTask;
import gov.loc.rdc.tasks.AddTagTask;
import gov.loc.rdc.tasks.DeleteKeyValuePairTask;
import gov.loc.rdc.tasks.DeleteMetadataTask;
import gov.loc.rdc.tasks.FindByHashTask;
import gov.loc.rdc.tasks.FindByKeyValuePairTask;
import gov.loc.rdc.tasks.FindByKeyValuePairsTask;
import gov.loc.rdc.tasks.FindByTagsTask;
import gov.loc.rdc.tasks.StoreMetadataTask;

import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
public class MetadataStoreController {
  private static final String BASE_DELETE_URL = "/deletemeta/{algorithm}/{hash}";
  private static final String FIND_BY_BASE_URL = "/searchmeta";
  private static final String FIND_BY_HASH_URL = FIND_BY_BASE_URL + "/{algorithm}/{hash}";
  private static final String FIND_BY_TAG_URL = FIND_BY_BASE_URL + "/tag/{tag}";
  private static final String FIND_BY_TAGS_URL = FIND_BY_BASE_URL + "/tags";
  private static final String FIND_BY_KEY_VALUE_PAIR_URL = FIND_BY_BASE_URL + "/key/{key}/value/{value}";
  private static final String FIND_BY_KEY_VALUE_PAIRS_URL = FIND_BY_BASE_URL + "/keyvaluepairs";
  private static final String DELETE_TAG_URL = BASE_DELETE_URL + "/deletetag/{tag}";
  private static final String DELETE_KEY_VALUE_URL = BASE_DELETE_URL + "/deletekeyvalue/{key}/{value}";
  private static final String BASE_STORE_URL = "/storemeta/{algorithm}/{hash}";
  private static final String ADD_TAG_URL = BASE_STORE_URL + "/addtag/{tag}";
  private static final String ADD_KEY_VALUE_URL = BASE_STORE_URL + "/addkeyvalue/{key}/{value}";

  @Autowired
  private MetadataRepository repository;
  
  @Resource(name="threadPoolTaskExecutor")
  private ThreadPoolTaskExecutor threadExecutor;
  
  @RequestMapping(value=FIND_BY_HASH_URL, method=RequestMethod.GET)
  public DeferredResult<Metadata> findByHash(@PathVariable String algorithm, @PathVariable String hash){
    DeferredResult<Metadata> result = new DeferredResult<>();
    
    FindByHashTask task = new FindByHashTask(result, repository, algorithm, hash);
    threadExecutor.execute(task);
    
    return result;
  }
  
  @RequestMapping(value=FIND_BY_TAG_URL, method=RequestMethod.GET)
  public DeferredResult<List<Metadata>> findBytag(@PathVariable String tag){
    DeferredResult<List<Metadata>> result = new DeferredResult<>();
    
    FindByTagsTask task = new FindByTagsTask(result, repository, tag);
    threadExecutor.execute(task);
    
    return result;
  }
  
  @RequestMapping(value=FIND_BY_TAGS_URL, method=RequestMethod.GET)
  public DeferredResult<List<Metadata>> findBytags(@RequestParam List<String> tags){
    DeferredResult<List<Metadata>> result = new DeferredResult<>();
    
    String[] tagsAsArray = tags.toArray(new String[tags.size()]);
    FindByTagsTask task = new FindByTagsTask(result, repository, tagsAsArray);
    threadExecutor.execute(task);
    
    return result; 
  }
  
  @RequestMapping(value=FIND_BY_KEY_VALUE_PAIR_URL, method=RequestMethod.GET)
  public DeferredResult<List<Metadata>> findByKeyValuePair(@PathVariable String key, @PathVariable String value){
    DeferredResult<List<Metadata>> result = new DeferredResult<>();
    
    FindByKeyValuePairTask task = new FindByKeyValuePairTask(result, repository, new KeyValuePair<String, String>(key, value));
    threadExecutor.execute(task);
    
    return result; 
  }
  
  @RequestMapping(value=FIND_BY_KEY_VALUE_PAIRS_URL, method=RequestMethod.GET)
  public DeferredResult<List<Metadata>> findByKeyValuePairs(@RequestParam String keyValuePairsAsJson){
    DeferredResult<List<Metadata>> result = new DeferredResult<>();
    
    FindByKeyValuePairsTask task = new FindByKeyValuePairsTask(result, repository, keyValuePairsAsJson);
    threadExecutor.execute(task);
    
    return result;
  }
  
  @RequestMapping(value=BASE_DELETE_URL, method=RequestMethod.DELETE)
  public void deleteMetadata(@PathVariable String algorithm, @PathVariable String hash){
    
  }
  
  @RequestMapping(value=DELETE_TAG_URL, method=RequestMethod.DELETE)
  public DeferredResult<Boolean> deleteTag(@PathVariable String algorithm, @PathVariable String hash, @PathVariable String tag){
    DeferredResult<Boolean> result = new DeferredResult<>();
    
    DeleteMetadataTask task = new DeleteMetadataTask(result, repository, algorithm, hash);
    threadExecutor.execute(task);
    
    return result;
  }
  
  @RequestMapping(value=DELETE_KEY_VALUE_URL, method=RequestMethod.DELETE)
  public DeferredResult<Boolean> deleteKeyValue(@PathVariable String algorithm, 
                             @PathVariable String hash, 
                             @PathVariable String key, 
                             @PathVariable String value){
    DeferredResult<Boolean> result = new DeferredResult<>();
    
    DeleteKeyValuePairTask task = new DeleteKeyValuePairTask(result, repository, algorithm, hash, new KeyValuePair<String, String>(key, value));
    threadExecutor.execute(task);
    
    return result;
  }
  
  @RequestMapping(value=BASE_STORE_URL, method={RequestMethod.POST, RequestMethod.PUT})
  public DeferredResult<Boolean> storeMetadata(@PathVariable String algorithm, 
                            @PathVariable String hash,
                            @RequestParam Set<String> tags,
                            @RequestParam String keyValuePairsAsJson){
    
    DeferredResult<Boolean> result = new DeferredResult<>();
    
    StoreMetadataTask task = new StoreMetadataTask(result, repository, algorithm, hash, tags, keyValuePairsAsJson);
    threadExecutor.execute(task);
    
    return result;
  }
  
  @RequestMapping(value=ADD_TAG_URL, method={RequestMethod.POST, RequestMethod.PUT})
  public DeferredResult<Boolean> addTag(@PathVariable String algorithm, @PathVariable String hash, @PathVariable String tag){
    DeferredResult<Boolean> result = new DeferredResult<>();
    
    AddTagTask task = new AddTagTask(result, repository, algorithm, hash, tag);
    threadExecutor.execute(task);
    
    return result;
  }
  
  @RequestMapping(value=ADD_KEY_VALUE_URL, method={RequestMethod.POST, RequestMethod.PUT})
  public DeferredResult<Boolean> addKeyValue(@PathVariable String algorithm, 
                          @PathVariable String hash, 
                          @PathVariable String key, 
                          @PathVariable String value){
    DeferredResult<Boolean> result = new DeferredResult<>();
    
    AddKeyValuePairTask task = new AddKeyValuePairTask(result, repository, algorithm, hash, new KeyValuePair<String, String>(key, value));
    threadExecutor.execute(task);
    
    return result;
  }

  //used only for testing
  protected void setRepository(MetadataRepository repository) {
    this.repository = repository;
  }
  
  //only used in unit test
  protected void setThreadExecutor(ThreadPoolTaskExecutor threadExecutor) {
    this.threadExecutor = threadExecutor;
  }
}
