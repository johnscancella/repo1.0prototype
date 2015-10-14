package gov.loc.rdc.tasks;

import java.util.List;

import org.springframework.web.client.RestTemplate;

public abstract class OrderServerForwardedTask {
  protected final List<String> serversToVisit;
  protected final RestTemplate restTemplate;
  
  public OrderServerForwardedTask(List<String> serversToVisit){
    this.serversToVisit = serversToVisit;
    this.restTemplate = new RestTemplate();
  }
  
  //for unit testing
  protected OrderServerForwardedTask(List<String> serversToVisit, RestTemplate restTemplate){
    this.serversToVisit = serversToVisit;
    this.restTemplate = restTemplate;
  }
}
