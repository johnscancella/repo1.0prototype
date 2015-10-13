package gov.loc.rdc.controllers;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Responsible for replying to master node requests for heartbeats.
 */
@RestController
public class HeartBeatController {
  private static final Logger logger = LoggerFactory.getLogger(HeartBeatController.class);
  
  @RequestMapping(value=RequestMappings.IS_ALIVE_URL, method={RequestMethod.POST, RequestMethod.PUT, RequestMethod.GET})
  public Boolean reportALive(){
    logger.debug("Reporting alive at [{}].", LocalDateTime.now());
   return true;
  }
}
