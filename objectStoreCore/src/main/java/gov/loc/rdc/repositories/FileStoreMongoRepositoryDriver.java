package gov.loc.rdc.repositories;

import gov.loc.rdc.entities.FileStoreData;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

/**
 * Responsible for inserting, updating, and getting data related to stored objects on the filesystem.
 */
@Component
public class FileStoreMongoRepositoryDriver implements FileStoreRepository{
  
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
}
