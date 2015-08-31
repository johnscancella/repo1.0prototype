package gov.loc.rdc.controllers;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ThreadPoolController {
  private static final Logger logger = LoggerFactory.getLogger(ThreadPoolController.class);

  @Resource(name="threadPoolTaskExecutor")
  private ThreadPoolTaskExecutor threadExecutor;
  
  @RequestMapping(value="/setcorepoolsize/{size}", method=RequestMethod.GET)
  public boolean updateCorePoolSize(@PathVariable String size){
    try{
      Integer corePoolSize = Integer.parseInt(size);
      threadExecutor.setCorePoolSize(corePoolSize);
      return true;
    }catch(Exception e){
      logger.error("Failed to set thread core pool core size to [{}]", size, e);
      return false;
    }
  }
  
  @RequestMapping(value="/setmaxpoolsize/{size}", method=RequestMethod.GET)
  public boolean updateMaxPoolSize(@PathVariable String size){
    try{
      Integer maxPoolSize = Integer.parseInt(size);
      threadExecutor.setMaxPoolSize(maxPoolSize);
      return true;
    }catch(Exception e){
      logger.error("Failed to set thread max pool core size to [{}]", size, e);
      return false;
    }
  }
  
  @RequestMapping(value="/setwaitfortasks/{bool}", method=RequestMethod.GET)
  public boolean updateWaitForTasks(@PathVariable String bool){
    try{
      Boolean waitForJobsToCompleteOnShutdown = Boolean.parseBoolean(bool);
      threadExecutor.setWaitForTasksToCompleteOnShutdown(waitForJobsToCompleteOnShutdown);
      return true;
    }catch(Exception e){
      logger.error("Failed to set thread wait for shutdown to [{}]", bool, e);
      return false;
    }
  }
}
