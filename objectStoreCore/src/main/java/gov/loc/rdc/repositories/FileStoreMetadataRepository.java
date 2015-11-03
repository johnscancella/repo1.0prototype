package gov.loc.rdc.repositories;

import java.util.List;

import gov.loc.rdc.entities.FileStoreData;

/**
 * Responsible for inserting, updating, and getting data related to stored objects on the filesystem.
 */
public interface FileStoreMetadataRepository {
  public void upsert(FileStoreData fileStoreData);
  public FileStoreData get(String hash);
  public List<String> getHashesForServer(String server); 
}
