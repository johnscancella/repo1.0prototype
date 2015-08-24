package gov.loc.rdc.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("gov.loc.rdc")
@EnableConfigurationProperties
public class Main {

  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(Main.class);
    app.setShowBanner(false);
    app.run(args);
  }
}
