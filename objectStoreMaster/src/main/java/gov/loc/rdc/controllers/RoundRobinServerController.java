package gov.loc.rdc.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles load balancing the servers in a round robin style
 */
@RestController
public class RoundRobinServerController {
  @Value("#{'${slave_addresses}'.split(',')}")
  private List<String> serverAddresses; //TODO convert to a set instead of list?
  
  private int startingIndex = 0;
  
  //TODO add a healthcheck/heartbeat to remove down servers
  
  @RequestMapping(value="/list/objectstorenodes", method={RequestMethod.POST, RequestMethod.PUT, RequestMethod.GET})
  public List<String> listServers(){
    return serverAddresses;
  }
  
  @RequestMapping(value=RequestMappings.ADD_SERVER_TO_CLUSTER_POOL_URL, method={RequestMethod.POST, RequestMethod.PUT, RequestMethod.GET})
  public boolean addServer(@PathVariable String serverName){
    return serverAddresses.add(serverName);
  }
  
  @RequestMapping(value="/remove/objectstorenode/{serverName}", method={RequestMethod.POST, RequestMethod.PUT, RequestMethod.GET})
  public boolean removeServer(@PathVariable String serverName){
    boolean isSuccessful = serverAddresses.remove(serverName);
    
    if(isSuccessful){
      return true;
    }
    
    return false;
  }
  
  /**
   * Create a list of available servers in a round robin like fashion. 
   */
  public List<String> getAvailableServers(){
    startingIndex = unsignedIncrement(startingIndex);
    return reorder(startingIndex, serverAddresses);
  }
  
  protected List<String> reorder(int index, List<String> strings){
    List<String> reorderedList = new ArrayList<>();
    
    //make sure that the index starts inbounds
    index = index % strings.size(); 
    index = index < 0 ? index * -1 : index;
    
    for(int iter=0; iter<strings.size(); iter++){
      reorderedList.add(strings.get(index));
      //increment index and keep it inbounds
      index = (index + 1) % strings.size(); 
    }
    
    return reorderedList;
  }

  protected int unsignedIncrement(int integer){
    integer++;
    integer = integer<0 ? 0 : integer;
    return integer;
  }

  //only used in unit testing
  protected void setServerAddresses(List<String> serverAddresses) {
    this.serverAddresses = serverAddresses;
  }
}
