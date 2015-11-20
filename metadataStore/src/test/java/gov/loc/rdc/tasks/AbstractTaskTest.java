package gov.loc.rdc.tasks;

import org.junit.Assert;
import org.junit.Before;
import org.mockito.Mockito;

import gov.loc.rdc.repositories.MetadataRepository;

public abstract class AbstractTaskTest extends Assert{
  
  protected MetadataRepository mockRepository;
  
  @Before
  public void setup(){
    mockRepository = Mockito.mock(MetadataRepository.class);
  }
}
