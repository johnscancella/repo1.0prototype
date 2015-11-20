package gov.loc.rdc.tasks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.web.context.request.async.DeferredResult;

import gov.loc.rdc.entities.Metadata;
import gov.loc.rdc.errors.MissingParametersException;

//TODO fix this
public class FindByTagsTaskTest extends AbstractTaskTest {
  @SuppressWarnings("unchecked")
  @Test
  public void testFindByTags() {
    Set<String> tags = new HashSet<>();
    tags.add("tag1");
    tags.add("tag2");
    Metadata data = new Metadata("hash", tags, new ArrayList<>());
    DeferredResult<List<Metadata>> result = new DeferredResult<>();
    FindByTagsTask sut = new FindByTagsTask(result, mockRepository, "tag1", "tag2");
    Mockito.when(mockRepository.findByTags(Mockito.anyList())).thenReturn(Arrays.asList(data));
    
    sut.run();
    
    assertTrue(result.getResult() instanceof List<?>);
    List<Metadata> typedResult = (List<Metadata>) result.getResult();
    assertEquals(1, typedResult.size());
    assertEquals(data, typedResult.get(0));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void testNoneExistingTags() {
    DeferredResult<List<Metadata>> result = new DeferredResult<>();
    FindByTagsTask sut = new FindByTagsTask(result, mockRepository, "nonexistingTag1", "nonexistingTag2");
    sut.run();
    assertTrue(result.getResult() instanceof List<?>);
    List<Metadata> typedResult = (List<Metadata>) result.getResult();
    assertEquals(0, typedResult.size());
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void testFindByTag() {
    Set<String> tags = new HashSet<>();
    tags.add("tag1");
    Metadata data = new Metadata("hash", tags, new ArrayList<>());
    DeferredResult<List<Metadata>> result = new DeferredResult<>();
    FindByTagsTask sut = new FindByTagsTask(result, mockRepository, "tag1");
    Mockito.when(mockRepository.findByTag("tag1")).thenReturn(Arrays.asList(data));
    
    sut.run();
    
    assertTrue(result.getResult() instanceof List<?>);
    List<Metadata> typedResult = (List<Metadata>) result.getResult();
    assertEquals(1, typedResult.size());
    assertEquals(data, typedResult.get(0));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void testNoneExistingTag() {
    DeferredResult<List<Metadata>> result = new DeferredResult<>();
    FindByTagsTask sut = new FindByTagsTask(result, mockRepository, "nonexistingTag1");
    sut.run();
    assertTrue(result.getResult() instanceof List<?>);
    List<Metadata> typedResult = (List<Metadata>) result.getResult();
    assertEquals(0, typedResult.size());
  }
  
  @Test
  public void testMissingTagReturnsError() {
    DeferredResult<List<Metadata>> result = new DeferredResult<>();
    FindByTagsTask sut = new FindByTagsTask(result, mockRepository);
    sut.run();
    assertTrue(result.getResult() instanceof MissingParametersException);
  }
}
