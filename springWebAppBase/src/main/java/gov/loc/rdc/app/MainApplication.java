package gov.loc.rdc.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

/**
 * starts the spring-boot application. 
 * Also controls which packages get scanned and what spring beans to add to the context.
 */
@SpringBootApplication
@ComponentScan("gov.loc.rdc")
@EnableAutoConfiguration
@EnableConfigurationProperties
public class MainApplication{
  
  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(MainApplication.class);
    app.setShowBanner(false);
    app.run(args);
  }
}
