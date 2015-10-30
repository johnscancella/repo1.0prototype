package gov.loc.rdc.notification;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Responsible for notifying people/team(s) regarding errors or other useful information.
 */
@Component
public class NotificationManager {
  private static final Logger logger = LoggerFactory.getLogger(NotificationManager.class);
  
  @Value("#{'${notification_emails}'.split(',')}")
  private List<String> emailAddresses;
  
  //TODO email notification
  //TODO slack notification?
  //TODO JIRA ticket creation?
  
  public void notify(String message){
    //TODO
    logger.warn("method 'notify(String message)' has not be implemented yet!");
  }
  
  public void notify(String message, Exception e){
    //TODO
    logger.warn("method 'notify(String message, Exception e)' has not be implemented yet!");
  }
}
