package gov.loc.rdc.repositories;

import gov.loc.rdc.entities.KeyValuePair;
import gov.loc.rdc.entities.Metadata;

import java.util.List;

/**
 * Responsible for saving and getting metadata 
 */
public interface MetadataRepository{
  public Metadata findByHash(String hash);
  public List<Metadata> findByTag(String tag);
  public List<Metadata> findByTags(List<String> tags);
  public List<Metadata> findByKeyValuePair(KeyValuePair<String, String> keyValuePair);
  public List<Metadata> findByKeyValuePairs(List<KeyValuePair<String, String>> keyValuePairs);
  
  public void deleteHash(String hash);
  public void deleteTagFromHash(String tag, String hash);
  public void deleteKeyValueFromHash(KeyValuePair<String, String> keyValue, String hash);
  
  public void save(Metadata data);
  public void save(String hash);
  public void saveTagToHash(String tag, String hash);
  public void saveTagsToHash(List<String> tags, String hash);
  public void saveKeyValuePairToHash(KeyValuePair<String, String> keyValuePair, String hash);
  public void saveKeyValuePairsToHash(List<KeyValuePair<String, String>> keyValuePairs, String hash);
  
}
