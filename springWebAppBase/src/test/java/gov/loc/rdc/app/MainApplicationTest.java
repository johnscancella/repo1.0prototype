package gov.loc.rdc.app;

import org.junit.Test;

public class MainApplicationTest {
  
  @Test
  public void testSpringStartsCorrectly(){
    System.setProperty("server.port", "0"); //grab a random port so it doesn't interfere with jenkins
    MainApplication.main(new String[] {});
  }
}
