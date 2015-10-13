package gov.loc.rdc.controllers;

import gov.loc.rdc.errors.BadParametersException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * Handles load balancing the servers in a round robin style
 */
@RestController
public class RoundRobinServerController {
  private static final Logger logger = LoggerFactory.getLogger(RoundRobinServerController.class);
  private final RestTemplate restTemplate = new RestTemplate();
  
  @Value("#{'${slave_addresses}'.split(',')}")
  private List<String> serverAddresses; //TODO convert to a set instead of list?
  @Value("${heartbeat_timeout_in_minutes}")
  private Long heartBeatTimeoutInMinutes;
  
  private Map<String, LocalDateTime> heartbeatMap = new HashMap<>();
  private int startingIndex = 0;
  
  @PostConstruct
  private void init(){
    LocalDateTime now = LocalDateTime.now();
    for(String serverAddress : serverAddresses){
      heartbeatMap.put(serverAddress, now);
    }
  }
  
  //defaults to every 5 minutes
  @Scheduled(cron = "${server_heartbeat_check_cron:*/5 * * * * *}")
  public void checkServersStillAvailable() {
    logger.info("CRON - Checking to make sure servers are still alive.");
    for(String serverAddress : serverAddresses){
      pingServer(serverAddress);
      removeServerIfStale(serverAddress);
    }
  }
  
  protected void pingServer(String server){
    String requestUrl = server + RequestMappings.IS_ALIVE_URL;
    boolean isAlive = restTemplate.getForObject(requestUrl, Boolean.class);
    if(isAlive){
      logger.debug("Server [{}] is alive. Updating heartbeat info.", server);
      heartbeatMap.put(server, LocalDateTime.now());
    }
  }
  
  protected void removeServerIfStale(String server){
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime lastHeartbeat = heartbeatMap.get(server);
    
    if(lastHeartbeat.plusMinutes(heartBeatTimeoutInMinutes).isBefore(now)){
      logger.warn("Server [{}] last heartbeat was [{}] which is past the timeout of [{}] minutes. Removing server from available list.",
          server, lastHeartbeat, heartBeatTimeoutInMinutes);
      serverAddresses.remove(server);
    }
  }
  
  @RequestMapping(value="/set/heartbeattimeout/minutes/{timeout}", method={RequestMethod.POST, RequestMethod.PUT, RequestMethod.GET})
  public Boolean setHeartbeatTimeoutInMinutes(@PathVariable String timeout){
    try{
      Long requestedTimeout = Long.valueOf(timeout);
      logger.info("Setting the heartbeat timeout to [{}] miliseconds.", requestedTimeout);
      heartBeatTimeoutInMinutes = requestedTimeout;
      return true;
    }
    catch(Exception e){
      logger.error("Could not set the heartbeat timeout.", e);
      throw new BadParametersException("Could not set the heartbeat timeout. Did you supply timeout in whole minutes?", e);
    }
  }
  
  @RequestMapping(value="/list/objectstorenodes", method={RequestMethod.POST, RequestMethod.PUT, RequestMethod.GET})
  public List<String> listServers(){
    return serverAddresses;
  }
  
  @RequestMapping(value=RequestMappings.ADD_SERVER_TO_CLUSTER_POOL_URL, method={RequestMethod.POST, RequestMethod.PUT, RequestMethod.GET})
  public boolean addServer(@PathVariable String serverName){
    heartbeatMap.put(serverName, LocalDateTime.now());
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
