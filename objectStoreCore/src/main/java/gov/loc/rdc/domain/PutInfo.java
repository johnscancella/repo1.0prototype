package gov.loc.rdc.domain;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PutInfo implements Serializable{
  private static final long serialVersionUID = 1L;

  private final byte[] fileBytes;
  private final String hash;
  
  public PutInfo(@JsonProperty("fileBytes")byte[] fileBytes, @JsonProperty("hash")String hash){
    this.fileBytes = fileBytes;
    this.hash = hash;
  }
  
  public byte[] getFileBytes() {
    return fileBytes;
  }
  public String getHash() {
    return hash;
  }
}
