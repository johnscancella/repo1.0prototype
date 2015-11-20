package gov.loc.rdc.controllers;

import gov.loc.rdc.entities.KeyValuePair;
import gov.loc.rdc.entities.Metadata;
import gov.loc.rdc.repositories.MetadataRepository;
import gov.loc.rdc.utils.KeyValueJsonConverter;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * tests just the restful URLs and pulling data from the URL/request params
 */
public class MetadataStoreControllerTest extends Assert{
  private MockMvc mockMvc;
  
  @Mock
  private MetadataRepository mockRepository;
  
  private static final Metadata MOCK_METADATA = new Metadata("hash"); 
  
  @Before
  public void setup(){
    MockitoAnnotations.initMocks(this);
    MetadataStoreController controller = new MetadataStoreController();
    controller.setRepository(mockRepository);
    controller.setThreadExecutor(new MockThreadpool());
    this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
  }
  
  @Test
  public void testFindByHash() throws Exception{
    Mockito.when(mockRepository.findByHash("ABC123")).thenReturn(MOCK_METADATA);
    mockMvc.perform(MockMvcRequestBuilders.get("/v1/metadata/search/hash/ABC123"))
    .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
  }
  
  @Test
  public void testFindByTag() throws Exception{
    Mockito.when(mockRepository.findByTag("foo")).thenReturn(Arrays.asList(MOCK_METADATA));
    mockMvc.perform(MockMvcRequestBuilders.get("/v1/metadata/search/tag/foo"))
    .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void testFindByTags() throws Exception{
    Mockito.when(mockRepository.findByTags(Mockito.anyList())).thenReturn(Arrays.asList(MOCK_METADATA));
    mockMvc.perform(MockMvcRequestBuilders.get("/v1/metadata/search/tags")
        .param("tags", "fooTag", "barTag"))
    .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void testFindByKeyValuePair() throws Exception{
    Mockito.when(mockRepository.findByKeyValuePair(Mockito.any(KeyValuePair.class))).thenReturn(Arrays.asList(MOCK_METADATA));
    mockMvc.perform(MockMvcRequestBuilders.get("/v1/metadata/search/key/fookey/value/foovalue"))
    .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void testFindByKeyValuePairs() throws Exception{
    List<KeyValuePair<String, String>> pairs = Arrays.asList(new KeyValuePair<String, String>("fooKey", "fooValue"));
    Mockito.when(mockRepository.findByKeyValuePairs(Mockito.anyList())).thenReturn(Arrays.asList(MOCK_METADATA));
    mockMvc.perform(MockMvcRequestBuilders.get("/v1/metadata/search/keyvaluepairs/json")
        .param("keyValuePairsAsJson", KeyValueJsonConverter.convertToJson(pairs)))
    .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
  }
  
  @Test
  public void testDeleteMetadata() throws Exception{
    mockMvc.perform(MockMvcRequestBuilders.delete("/v1/metadata/delete/ABC123"))
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
  }
  
  @Test
  public void testDeleteTag() throws Exception{
    mockMvc.perform(MockMvcRequestBuilders.delete("/v1/metadata/delete/tag/footag/fromhash/foohash"))
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
  }
  
  @Test
  public void testDeleteKeyValuePair() throws Exception{
    mockMvc.perform(MockMvcRequestBuilders.delete("/v1/metadata/delete/key/fookey/value/foovalue/fromhash/foohash"))
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
  }
  
  @Test
  public void testStoreMetadata() throws Exception{
    List<KeyValuePair<String, String>> pairs = Arrays.asList(new KeyValuePair<String, String>("fooKey", "fooValue"));
    
    mockMvc.perform(MockMvcRequestBuilders.put("/v1/metadata/store/hash/ABC123")
        .param("tags", "tag1", "tag2", "tag3")
        .param("keyValuePairsAsJson", KeyValueJsonConverter.convertToJson(pairs)))
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
  }
  
  @Test
  public void testAddTag() throws Exception{
    mockMvc.perform(MockMvcRequestBuilders.put("/v1/metadata/add/tag/footag/tohash/ABC123"))
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
  }
  
  @Test
  public void testAddKeyValue() throws Exception{
    mockMvc.perform(MockMvcRequestBuilders.put("/v1/metadata/add/key/fookey/value/foovalue/tohash/ABC123"))
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
  }
  
  private static class MockThreadpool extends ThreadPoolTaskExecutor {
    private static final long serialVersionUID = 1L;
    
    @Override
    public void execute(Runnable task){
      task.run();
    }
  }
}
