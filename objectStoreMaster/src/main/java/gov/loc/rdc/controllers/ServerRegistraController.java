package gov.loc.rdc.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles keeping track of server addresses
 */
@RestController
public class ServerRegistraController {
  private Map<String, String> serverMap;
  
  public ServerRegistraController(){serverMap = new HashMap<>();}
  
  public List<String> getUrls(Set<String> hostNames){
    List<String> urls = new ArrayList<>();
    
    for(String hostName : hostNames){
      String url = serverMap.get(hostName);
      if(url != null){
        urls.add(url);
      }
    }
    
    return urls;
  }
  
  @RequestMapping(value="/list/objectstorenodes", method={RequestMethod.POST, RequestMethod.PUT, RequestMethod.GET})
  public List<String> listServers(){
    return new ArrayList<>(serverMap.values());
  }
  
  /*
   * servername is the url to make requests to, like https://localhost:8443
   * hostname is the shortened name, like localhost 
   */
  @RequestMapping(value=RequestMappings.ADD_SERVER_TO_CLUSTER_POOL_URL, method={RequestMethod.POST, RequestMethod.PUT, RequestMethod.GET})
  public boolean addServer(@PathVariable String serverName){
    String hostName = getHostname(serverName);
    serverMap.put(hostName, serverName);
    return true;
  }
  
  protected String getHostname(String serverName){
    int startingIndex = 0;
    int endingIndex = serverName.length();
    
    if(serverName.startsWith("http://")){
      startingIndex = 7;
    }
    if(serverName.startsWith("https://")){
      startingIndex = 8;
    }
    if(serverName.contains(":")){
      endingIndex = serverName.lastIndexOf(':');
    }
    
    return serverName.substring(startingIndex, endingIndex);
  }
  
  @RequestMapping(value="/remove/objectstorenode/{serverName}", method={RequestMethod.POST, RequestMethod.PUT, RequestMethod.GET})
  public boolean removeServer(@PathVariable String serverName){
    String hostName = getHostname(serverName);
    boolean isSuccessful = serverMap.remove(hostName, serverName);
    
    return isSuccessful;
  }
}
