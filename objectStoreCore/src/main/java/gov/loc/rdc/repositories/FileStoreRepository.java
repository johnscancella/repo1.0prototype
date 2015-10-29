package gov.loc.rdc.repositories;

import gov.loc.rdc.entities.FileStoreData;

public interface FileStoreRepository {
  public void upsert(FileStoreData fileStoreData);
  public FileStoreData get(String hash);
}
