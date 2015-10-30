package gov.loc.rdc.repositories;

import java.util.List;

import gov.loc.rdc.entities.FileStoreData;

public interface FileStoreRepository {
  public void upsert(FileStoreData fileStoreData);
  public FileStoreData get(String hash);
  public List<String> getHashesForServer(String server); 
}
