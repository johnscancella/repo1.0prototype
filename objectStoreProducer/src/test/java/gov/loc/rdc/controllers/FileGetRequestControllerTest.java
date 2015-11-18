package gov.loc.rdc.controllers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import gov.loc.rdc.entities.FileStoreData;
import gov.loc.rdc.repositories.FileStoreMetadataRepository;

public class FileGetRequestControllerTest {
  private MockMvc mockMvc;
  private FileStoreMetadataRepository mockFileStoreMetadataRepository;
  
  @Before
  public void setup(){
    mockFileStoreMetadataRepository = Mockito.mock(FileStoreMetadataRepository.class);
    FileGetRequestController sut = new FileGetRequestController();
    sut.setRepo(mockFileStoreMetadataRepository);
    this.mockMvc = MockMvcBuilders.standaloneSetup(sut).build();
  }
  
  @Test
  public void testGetFileRedirects() throws Exception{
    FileStoreData data = new FileStoreData("hash", "serverA");
    Mockito.when(mockFileStoreMetadataRepository.get("hash")).thenReturn(data);
    mockMvc.perform(MockMvcRequestBuilders.get("/v1/file/get/hash")).andExpect(MockMvcResultMatchers.status().is3xxRedirection());
  }
  
  @Test
  public void testGetFileWhenNotInObjectStore() throws Exception{
    FileStoreData data = new FileStoreData("hash");
    Mockito.when(mockFileStoreMetadataRepository.get("hash")).thenReturn(data);
    mockMvc.perform(MockMvcRequestBuilders.get("/v1/file/get/hash")).andExpect(MockMvcResultMatchers.status().isNotFound());
  }
}
