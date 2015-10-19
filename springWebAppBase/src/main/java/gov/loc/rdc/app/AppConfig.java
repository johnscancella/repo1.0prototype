package gov.loc.rdc.app;

import gov.loc.rdc.controllers.UserRequestLoggingInterceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Instead of supplying one or more spring configurations in xml define them in java classes
 */
@Configuration
public class AppConfig extends WebMvcConfigurerAdapter{
private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);
  
  @Value("${core_pool_size:5}")
  private int corePoolSize;
  
  @Value("${max_pool_size:100}")
  private int maxPoolSize;
  
  @Value("${wait_for_tasks:false}")
  private boolean waitForTasks;

  @Bean
  public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
    ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
    logger.debug("setting core(starting) thread pool size to {}", corePoolSize);
    pool.setCorePoolSize(corePoolSize);
    logger.debug("setting max thread pool size to {}", maxPoolSize);
    pool.setMaxPoolSize(maxPoolSize);
    logger.debug("setting wait for tasks to complete on shutdown for threadpool? {}", waitForTasks);
    pool.setWaitForTasksToCompleteOnShutdown(waitForTasks);
    return pool;
  }
  
  @Override
  public void addInterceptors(InterceptorRegistry registry){
    registry.addInterceptor(new UserRequestLoggingInterceptor());
  }
}
