package gov.loc.rdc.domain;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ScpInfo implements Serializable{
  private static final long serialVersionUID = 1L;
  
  private final String server;
  private final int port;
  private final String filepath;
  private final String hash;
  
  @JsonCreator
  public ScpInfo(@JsonProperty("server")String server, 
      @JsonProperty("port")int port, 
      @JsonProperty("filepath")String filepath, 
      @JsonProperty("hash")String hash){
    this.server = server;
    this.port = port;
    this.filepath = filepath;
    this.hash = hash;
  }
  
  public String getServer() {
    return server;
  }

  public int getPort() {
    return port;
  }

  public String getFilepath() {
    return filepath;
  }

  public String getHash() {
    return hash;
  }
}
