package gov.loc.rdc.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * Handles forwarding requests to verify file integrity to all slave nodes.
 */
@RestController
public class ForwardingVerifyIntegrityController implements VerifyIntegrityControllerApi {
  private final RestTemplate restTemplate;
  
  @Autowired
  private ServerRegistraController serverRegistraController;
  
  public ForwardingVerifyIntegrityController(){
    restTemplate = new RestTemplate();
  }
  
  //for unit testing only
  protected ForwardingVerifyIntegrityController(RestTemplate restTemplate, ServerRegistraController roundRobinServerController){
    this.restTemplate = restTemplate;
    this.serverRegistraController = roundRobinServerController;
  }

  @Override
  @RequestMapping(value=RequestMappings.VERIFY_INTEGRITY_URL, method={RequestMethod.GET, RequestMethod.PUT, RequestMethod.POST})
  public void restfulVerifyIntegrity(@RequestParam(value="rootdir",required=false) String rootDir) {
    List<String> serversToVisit = serverRegistraController.listServers();
    
    for(String serverToVisit : serversToVisit){
      String url = constructUrl(serverToVisit, false);
      if(rootDir != null && !rootDir.equals("")){
        url = constructUrl(serverToVisit, true);
        restTemplate.getForObject(url, Void.class, rootDir);
      }
      else{
        restTemplate.getForObject(url, Void.class);
      }
    }
  }
  
  protected String constructUrl(String serverToVisit, boolean addParam){
    StringBuilder url = new StringBuilder();
    url.append(serverToVisit).append(RequestMappings.VERIFY_INTEGRITY_URL);
    if(addParam){
      url.append("?rootdir={dirToCheck}");
    }
    
    return url.toString();
  }
}
