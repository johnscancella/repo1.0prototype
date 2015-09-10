package gov.loc.rdc.tasks;

import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import gov.loc.rdc.app.MainApplication;
import gov.loc.rdc.entities.KeyValuePair;
import gov.loc.rdc.repositories.MetadataRepository;

import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { MainApplication.class})
public abstract class TaskTest extends Assert{
  protected static final String HASH = "ABC123";
  protected static final String BAD_HASH = "FOO123";
  protected static final String ALGORITHM = "sha256";
  protected static final String BAD_ALGORITHM = "md5";
  protected static final String TAG1 = "fooTag1";
  protected static final String TAG2 = "fooTag2";
  protected static final String KEY1 = "fooKey";
  protected static final String KEY2 = "fooKey2";
  protected static final String VALUE1 = "fooValue";
  protected static final String VALUE2 = "fooValue2";
  
  @Autowired
  protected MetadataRepository repository;
  
  @Resource(name="threadPoolTaskExecutor")
  protected ThreadPoolTaskExecutor threadExecutor;
  
  protected Set<String> tags;
  protected List<KeyValuePair<String, String>> keyValuePairs;
}
