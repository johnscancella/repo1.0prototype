package gov.loc.rdc.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Instead of supplying one or more spring configurations in xml define them in java classes
 */
@Configuration
public class AppConfig {
  private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);
  
  @Value("${corePoolSize:5}")
  private int corePoolSize;
  
  @Value("${maxPoolSize:100}")
  private int maxPoolSize;
  
  @Value("${waitForTasks:false}")
  private boolean waitForTasks;

  @Bean
  public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
    ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
    logger.debug("setting core(starting) thread pool size to");
    pool.setCorePoolSize(corePoolSize);
    pool.setMaxPoolSize(maxPoolSize);
    pool.setWaitForTasksToCompleteOnShutdown(waitForTasks);
    return pool;
  }
}
