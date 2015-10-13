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
 * Responsible for registering with the master node after starting up
 */
@Component
public class RegisterRunner implements CommandLineRunner{
  private static final Logger logger = LoggerFactory.getLogger(RegisterRunner.class);
  
  @Value("${is_cluster_enabled:true}")
  private boolean isClusterEnabled;
  
  @Value("${master_url}")
  private String masterNodeUrl;
  
  //default to 10 minutes
  @Value("${reconnect_period_in_miliseconds:600000}")
  private Integer retryPeriod;
  
  @Value("$url_to_register")
  private String myUrl;
  
  private final RestTemplate restTemplate = new RestTemplate();
  
  @Override
  public void run(String... args) throws Exception {
    if(isClusterEnabled){
      logger.info("Cluster mode is enabled. Registering with master objectStore node [{}].", masterNodeUrl);
      
      InetAddress localMachine = InetAddress.getLocalHost();
      
      register(localMachine.getHostName());
    }
    else{
      logger.info("Standalone mode is enabled. Your objectStore could be faster and more dependable, please consider running in cluster mode.");
    }
  }
  
  protected void register(String hostname){
    String requestUrl = hostname + RequestMappings.ADD_SERVER_TO_CLUSTER_POOL_URL;
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
