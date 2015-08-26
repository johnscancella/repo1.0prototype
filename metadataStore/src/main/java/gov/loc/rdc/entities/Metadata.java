package gov.loc.rdc.entities;

import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.Id;

/**
 * For storing metadata about a particular file defined by the hash.
 * See classes that implement {@link gov.loc.rdc.hash} for supported hash algorithms.
 */
public class Metadata {
  @Id
  private String hash;
  private Set<String> tags;
  private List<KeyValuePair<String, String>> keyValuePairs;
  
  public Metadata() {}
  public Metadata(String hash, Set<String> tags, List<KeyValuePair<String, String>> keyValuePairs){
    this.hash = hash;
    this.tags = tags;
    this.keyValuePairs = keyValuePairs;
  }
  
  @Override
  public String toString() {
    return "Metadata [hash=" + hash + ", tags=" + tags + ", keyValuePairs=" + keyValuePairs + "]";
  }
  
  public String getHash() {
    return hash;
  }
  public void setHash(String hash) {
    this.hash = hash;
  }
  public Set<String> getTags() {
    return tags;
  }
  public void setTags(Set<String> tags) {
    this.tags = tags;
  }
  public List<KeyValuePair<String, String>> getKeyValuePairs() {
    return keyValuePairs;
  }
  public void setKeyValuePairs(List<KeyValuePair<String, String>> keyValuePairs) {
    this.keyValuePairs = keyValuePairs;
  }
}
