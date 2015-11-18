package gov.loc.rdc.repositories;

import gov.loc.rdc.entities.FileStoreData;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

/**
 * MongoDb implementation of the {@link FileStoreMetadataRepository} interface
 */
@Component
public class FileStoreMongoRepositoryDriver implements FileStoreMetadataRepository{
  
  @Autowired
  private MongoTemplate mongoTemplate;
  
  @Override
  public void upsert(FileStoreData fileStoreData){
    FileStoreData savedData = get(fileStoreData.getHash());
    savedData.getServers().addAll(fileStoreData.getServers());
    
    mongoTemplate.save(savedData);
  }
  
  @Override
  public FileStoreData get(String hash){
    Query query = new Query();
    query.addCriteria(Criteria.where("hash").is(hash));
    FileStoreData fileStoreData = mongoTemplate.findOne(query, FileStoreData.class);
    
    if(fileStoreData == null){
      fileStoreData = new FileStoreData(hash);
    }
    
    return fileStoreData;
  }

  @Override
  public List<String> getHashesForServer(String server) {
    List<String> hashes = new ArrayList<>();
    Query query = new Query();
    query.addCriteria(Criteria.where("servers").in(server));
    
    List<FileStoreData> returnedData = mongoTemplate.find(query, FileStoreData.class);
    
    if(returnedData != null){
      hashes = new ArrayList<>(returnedData.size());
      for(FileStoreData data : returnedData){
        hashes.add(data.getHash());
      }
    }
    
    return hashes;
  }
  
  //used for testing only
  protected void setMongoTemplate(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }
}
