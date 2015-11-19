package gov.loc.rdc.entities;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
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
  
  @Override
  public int hashCode() {
    return Objects.hash(hash, servers);
  }
  @Override
  public boolean equals(Object obj) {
    if (this == obj){ return true; }
    if (obj == null){ return false; }
    if (!(obj instanceof FileStoreData)){ return false; }
    FileStoreData other = (FileStoreData) obj;
    return Objects.equals(hash, other.getHash()) && Objects.equals(servers, other.getServers());
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
