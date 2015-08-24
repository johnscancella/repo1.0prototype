package gov.loc.rdc;

import gov.loc.rdc.app.Main;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Main.class)
public class FOOTest {
  @Autowired
  private ThreadPoolTaskExecutor sut;
  
  @Test
  public void test(){
    System.err.println("does sut exist? " + sut != null);
    System.err.println(sut.getCorePoolSize());
    System.err.println(sut.getMaxPoolSize());
  }
}
