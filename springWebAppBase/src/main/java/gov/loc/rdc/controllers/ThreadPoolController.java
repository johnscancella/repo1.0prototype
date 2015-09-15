package gov.loc.rdc.controllers;

import gov.loc.rdc.errors.MissingParametersException;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ThreadPoolController {
  private static final Logger logger = LoggerFactory.getLogger(ThreadPoolController.class);

  @Resource(name="threadPoolTaskExecutor")
  private ThreadPoolTaskExecutor threadExecutor;
  
  @RequestMapping(value="/setcorepoolsize/{size}", method={RequestMethod.GET, RequestMethod.PUT, RequestMethod.POST})
  public @ResponseBody boolean updateCorePoolSize(@PathVariable String size){
    try{
      Integer corePoolSize = Integer.parseInt(size);
      logger.info("Setting core pool size to [{}]", size);
      threadExecutor.setCorePoolSize(corePoolSize);
      return true;
    }catch(Exception e){
      logger.error("Failed to set thread core pool core size to [{}]", size, e);
      throw new MissingParametersException("Failed to set thread core pool core size to [" + size + "] cause it is not a number");
    }
  }
  
  @RequestMapping(value="/setmaxpoolsize/{size}", method={RequestMethod.GET, RequestMethod.PUT, RequestMethod.POST})
  public @ResponseBody boolean updateMaxPoolSize(@PathVariable String size){
    try{
      Integer maxPoolSize = Integer.parseInt(size);
      logger.info("Setting max pool size to [{}]", size);
      threadExecutor.setMaxPoolSize(maxPoolSize);
      return true;
    }catch(NumberFormatException e){
      logger.error("Failed to set thread max pool core size to [{}]", size, e);
      throw new MissingParametersException("Failed to set thread max pool core size to [" + size + "] cause it is not a number");
    }
  }
  
  @RequestMapping(value="/setwaitfortasks/{bool}", method={RequestMethod.GET, RequestMethod.PUT, RequestMethod.POST})
  public @ResponseBody boolean updateWaitForTasks(@PathVariable String bool){
    try{
      Boolean waitForJobsToCompleteOnShutdown = parseBoolean(bool);
      logger.info("Setting wait for shutdown to [{}]", waitForJobsToCompleteOnShutdown);
      threadExecutor.setWaitForTasksToCompleteOnShutdown(waitForJobsToCompleteOnShutdown);
      return true;
    }catch(Exception e){
      logger.error("Failed to set thread wait for shutdown to [{}]", bool, e);
      throw new MissingParametersException("Failed to set thread wait for shutdown to [" + bool + "] cause it is not a boolean");
    }
  }
  
  protected Boolean parseBoolean(String bool) throws IllegalArgumentException{
    if(bool != null && bool.equalsIgnoreCase("true")){
      return Boolean.TRUE;
    }
    else if(bool != null && bool.equalsIgnoreCase("false")){
      return Boolean.FALSE;
    }
    throw new IllegalArgumentException("[" + bool + "] can't be parsed to a boolean.");
  }

  //only used for unit testing
  protected void setThreadExecutor(ThreadPoolTaskExecutor threadExecutor) {
    this.threadExecutor = threadExecutor;
  }
}
