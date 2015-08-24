package gov.loc.rdc.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

/**
 * starts the spring-boot application. Also controls which packages get scanned and what spring beans to add to the context.
 */
@SpringBootApplication
@ComponentScan("gov.loc.rdc")
@EnableConfigurationProperties
public class ObjectStoreApplication {

  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(ObjectStoreApplication.class);
    app.setShowBanner(false);
    app.run(args);
  }
}
