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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
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
  
  private static final Metadata MOCK_METADATA = new Metadata(); 
  private static final String METADATA_REPOSNSE_AS_STRING= "{\"hash\":\"NO HASH\",\"tags\":[],\"keyValuePairs\":[]}";
  
  @Before
  public void setup(){
    MockitoAnnotations.initMocks(this);
    MetadataStoreController controller = new MetadataStoreController();
    controller.setRepository(mockRepository);
    this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
  }
  
  @Test
  public void testFindByHash() throws Exception{
    Mockito.when(mockRepository.findByHash("ABC123")).thenReturn(MOCK_METADATA);
    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/searchmeta/sha256/ABC123"))
    .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    assertEquals(METADATA_REPOSNSE_AS_STRING, result.getResponse().getContentAsString());
  }
  
  @Test
  public void testFindByTag() throws Exception{
    Mockito.when(mockRepository.findByTag("foo")).thenReturn(Arrays.asList(MOCK_METADATA));
    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/searchmeta/tag/foo"))
    .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    assertEquals("[" + METADATA_REPOSNSE_AS_STRING + "]", result.getResponse().getContentAsString());
  }
  
  @Test
  public void testFindByTags() throws Exception{
    Mockito.when(mockRepository.findByTags(Mockito.anyList())).thenReturn(Arrays.asList(MOCK_METADATA));
    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/searchmeta/tags")
        .param("tags", "fooTag", "barTag"))
    .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    assertEquals("[" + METADATA_REPOSNSE_AS_STRING + "]", result.getResponse().getContentAsString());
  }
  
  @Test
  public void testFindByKeyValuePair() throws Exception{
    Mockito.when(mockRepository.findByKeyValuePair(Mockito.any(KeyValuePair.class))).thenReturn(Arrays.asList(MOCK_METADATA));
    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/searchmeta//key/fooKey/value/fooValue"))
    .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    assertEquals("[" + METADATA_REPOSNSE_AS_STRING + "]", result.getResponse().getContentAsString());
  }
  
  @Test
  public void testFindByKeyValuePairs() throws Exception{
    List<KeyValuePair<String, String>> pairs = Arrays.asList(new KeyValuePair<String, String>("fooKey", "fooValue"));
    Mockito.when(mockRepository.findByKeyValuePairs(Mockito.anyList())).thenReturn(Arrays.asList(MOCK_METADATA));
    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/searchmeta/keyvaluepairs")
        .param("keyValuePairsAsJson", KeyValueJsonConverter.convertToJson(pairs)))
    .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    assertEquals("[" + METADATA_REPOSNSE_AS_STRING + "]", result.getResponse().getContentAsString());
  }
  
  @Test
  public void testDeleteMetadata() throws Exception{
    mockMvc.perform(MockMvcRequestBuilders.delete("/deletemeta/sha256/ABC123"))
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
  }
  
  @Test
  public void testDeleteTag() throws Exception{
    mockMvc.perform(MockMvcRequestBuilders.delete("/deletemeta/sha256/ABC123/deletetag/fooTag"))
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
  }
  
  @Test
  public void testDeleteKeyValuePair() throws Exception{
    mockMvc.perform(MockMvcRequestBuilders.delete("/deletemeta/sha256/ABC123/deletekeyvalue/fooKey/fooValue"))
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
  }
  
  @Test
  public void testStoreMetadata() throws Exception{
    List<KeyValuePair<String, String>> pairs = Arrays.asList(new KeyValuePair<String, String>("fooKey", "fooValue"));
    
    mockMvc.perform(MockMvcRequestBuilders.put("/storemeta/sha256/ABC123")
        .param("tags", "tag1", "tag2", "tag3")
        .param("keyValuePairsAsJson", KeyValueJsonConverter.convertToJson(pairs)))
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
  }
  
  @Test
  public void testAddTag() throws Exception{
    mockMvc.perform(MockMvcRequestBuilders.put("/storemeta/sha256/ABC123/addtag/fooTag"))
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
  }
  
  @Test
  public void testAddKeyValue() throws Exception{
    mockMvc.perform(MockMvcRequestBuilders.put("/storemeta/sha256/ABC123/addkeyvalue/fooKey/fooValue"))
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
  }
}
