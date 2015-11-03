package gov.loc.rdc.runners;

import gov.loc.rdc.controllers.RequestMappings;

import java.net.InetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Responsible for registering with the master node after starting up.
 */
@Component
public class RegisterRunner implements CommandLineRunner{
  private static final Logger logger = LoggerFactory.getLogger(RegisterRunner.class);
  
  @Value("${master_url}")
  private String masterNodeUrl;
  
  //default to 10 minutes
  @Value("${reconnect_period_in_miliseconds:600000}")
  private Integer retryPeriod;
  
  @Value("$url_to_register")
  private String myUrl;
  
  private RestTemplate restTemplate = new RestTemplate();
  
  //for unit tests only
  protected RegisterRunner(String masterNodeUrl, Integer retryPeriod, String myUrl, RestTemplate template){
    this.masterNodeUrl = masterNodeUrl;
    this.retryPeriod = retryPeriod;
    this.myUrl = myUrl;
    restTemplate = template;
  }
  
  @Override
  public void run(String... args) throws Exception {
    InetAddress localMachine = InetAddress.getLocalHost();
    register(localMachine.getHostName());
  }
  
  protected void register(String hostname){
    String requestUrl = masterNodeUrl + RequestMappings.ADD_SERVER_TO_CLUSTER_POOL_URL;
    while(true){
      try{
          boolean isRegisteredWithMaster = restTemplate.getForObject(requestUrl, Boolean.class, myUrl);
          if(isRegisteredWithMaster){
            return;
          }
        }
        catch(Exception e){
          logger.error("Something went wrong when trying to auto join master node. Retrying again in [{}] miliseconds.", retryPeriod, e);
        }
      logger.info("Was not able to register with master. Retrying again in [{}] miliseconds.", retryPeriod);
      sleep();
    }
  }
  
  protected void sleep(){
    try{
      Thread.sleep(retryPeriod);
    }
    catch(Exception e){
      logger.error("Couldn't pause application. ", e);
    }
  }

}
