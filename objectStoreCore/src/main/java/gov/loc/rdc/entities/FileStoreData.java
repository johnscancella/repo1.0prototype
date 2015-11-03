package gov.loc.rdc.entities;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;

/**
 * For storing which servers have the associated hashed file on their filesystem.
 */
public class FileStoreData {

  @Id
  private String hash;
  private Set<String> servers;
  
  public FileStoreData(){}
  public FileStoreData(String hash, String ... servers){
    this.hash = hash;
    this.servers = new HashSet<>(Arrays.asList(servers));
  }
  
  public String getHash() {
    return hash;
  }
  public void setHash(String hash) {
    this.hash = hash;
  }
  public Set<String> getServers() {
    return servers;
  }
  public void setServers(Set<String> servers) {
    this.servers = servers;
  }
}
