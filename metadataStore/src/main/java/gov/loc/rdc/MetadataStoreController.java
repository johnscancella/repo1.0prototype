package gov.loc.rdc;

import gov.loc.rdc.entities.KeyValuePair;
import gov.loc.rdc.entities.Metadata;
import gov.loc.rdc.repositories.MetadataRepository;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MetadataStoreController {
  private static final Logger logger = LoggerFactory.getLogger(MetadataStoreController.class);
  private static final String BASE_DELETE_URL = "/deletemeta/{algorithm}/{hash}";
  private static final String DELETE_TAG_URL = BASE_DELETE_URL + "/deletetag/{tag}";
  private static final String DELETE_KEY_VALUE_URL = BASE_DELETE_URL + "/deletekeyvalue/{key}/{value}";
  private static final String BASE_STORE_URL = "/storemeta/{algorithm}/{hash}";
  private static final String ADD_TAG_URL = BASE_STORE_URL + "/addtag/{tag}";
  private static final String ADD_KEY_VALUE_URL = BASE_STORE_URL + "/addkeyvalue/{key}/{value}";

  @Autowired
  private MetadataRepository repository;
  
  @RequestMapping(value=BASE_DELETE_URL, method=RequestMethod.DELETE)
  public void deleteMetadata(@PathVariable String algorithm, @PathVariable String hash){
    //TODO check algorithm
    repository.deleteHash(hash);
  }
  
  @RequestMapping(value=DELETE_TAG_URL, method=RequestMethod.DELETE)
  public void deleteTag(@PathVariable String algorithm, @PathVariable String hash, @PathVariable String tag){
    //TODO check algorithm
    repository.deleteTagFromHash(tag, hash);
  }
  
  @RequestMapping(value=DELETE_KEY_VALUE_URL, method=RequestMethod.DELETE)
  public void deleteKeyValue(@PathVariable String algorithm, 
                             @PathVariable String hash, 
                             @PathVariable String key, 
                             @PathVariable String value){
    //TODO check algorithm
    repository.deleteKeyValueFromHash(new KeyValuePair<String, String>(key, value), hash);
  }
  
  @RequestMapping(value=BASE_STORE_URL, method={RequestMethod.POST, RequestMethod.PUT})
  public void storeMetadata(@PathVariable String algorithm, 
                            @PathVariable String hash,
                            @RequestParam Set<String> tags,
                            @RequestParam List<KeyValuePair<String, String>> keyValuePairs){
    //TODO check algorithm
    Metadata data = new Metadata(hash, tags, keyValuePairs);
    logger.debug("Saving metadata [{}]", data);
    repository.save(data);
  }
  
  @RequestMapping(value=ADD_TAG_URL, method={RequestMethod.POST, RequestMethod.PUT})
  public void addTag(@PathVariable String algorithm, @PathVariable String hash, @PathVariable String tag){
    //TODO check algorithm    
    logger.debug("Adding tag [{}] to hash [{}]", tag, hash);
    repository.saveTagToHash(tag, hash);
  }
  
  @RequestMapping(value=ADD_KEY_VALUE_URL, method={RequestMethod.POST, RequestMethod.PUT})
  public void addKeyValue(@PathVariable String algorithm, 
                          @PathVariable String hash, 
                          @PathVariable String key, 
                          @PathVariable String value){
    //TODO check algorithm
    logger.debug("Adding key:value [{}:{}] to hash [{}]", key, value, hash);
    repository.saveKeyValuePairToHash(new KeyValuePair<String, String>(key, value), hash);
  }
}
