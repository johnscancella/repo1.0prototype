package gov.loc.rdc.tasks;

import java.util.List;

import org.springframework.web.client.RestTemplate;

public abstract class OrderServerForwardedTask {
  protected final List<String> serversToVisit;
  protected final RestTemplate restTemplate = new RestTemplate();
  
  public OrderServerForwardedTask(List<String> serversToVisit){
    this.serversToVisit = serversToVisit;
  }
}
