package gov.loc.rdc.repositories;

import gov.loc.rdc.entities.KeyValuePair;
import gov.loc.rdc.entities.Metadata;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

@Component
public class MetadataMongoRepositoryDriver implements MetadataRepository {
  private final MongoTemplate mongoTemplate;
  
  @Autowired
  public MetadataMongoRepositoryDriver(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  @Override
  public Metadata findByHash(String hash) {
    Query query = new Query();
    query.addCriteria(Criteria.where("hash").is(hash));
    Metadata metadata = mongoTemplate.findOne(query, Metadata.class);
    
    return metadata;
  }

  @Override
  public List<Metadata> findByTag(String tag) {
    Query query = new Query();
    query.addCriteria(Criteria.where("tags").in(tag));
    List<Metadata> metadatas = mongoTemplate.find(query, Metadata.class);
    
    return metadatas;
  }

  @Override
  public List<Metadata> findByKeyValuePair(KeyValuePair<String, String> keyValuePair) {
    Query query = new Query();
    query.addCriteria(Criteria.where("keyValuePairs").in(keyValuePair));
    List<Metadata> metadatas = mongoTemplate.find(query, Metadata.class);
    
    return metadatas;
  }
  
  @Override
  public List<Metadata> findByTags(List<String> tags) {
    Query query = new Query();
    query.addCriteria(Criteria.where("tags").in(tags));
    List<Metadata> metadatas = mongoTemplate.find(query, Metadata.class);
    
    return metadatas;
  }

  @Override
  public List<Metadata> findByKeyValuePairs(List<KeyValuePair<String, String>> keyValuePairs) {
    Query query = new Query();
    query.addCriteria(Criteria.where("keyValuePairs").in(keyValuePairs));
    List<Metadata> metadatas = mongoTemplate.find(query, Metadata.class);
    
    return metadatas;
  }
  
  @Override
  public void deleteHash(String hash) {
    Query query = new Query();
    query.addCriteria(Criteria.where("hash").is(hash));
    mongoTemplate.remove(query, Metadata.class);
  }

  @Override
  public void deleteTagFromHash(String tag, String hash) {
    Query query = new Query();
    query.addCriteria(Criteria.where("hash").is(hash).and("tags").in(tag));
    Metadata metadata = mongoTemplate.findOne(query, Metadata.class);
    
    metadata.getTags().remove(tag);
    mongoTemplate.save(metadata);
  }
  
  @Override
  public void deleteKeyValueFromHash(KeyValuePair<String, String> keyValue, String hash){
    Query query = new Query();
    query.addCriteria(Criteria.where("hash").is(hash));
    Metadata metadata = mongoTemplate.findOne(query, Metadata.class);
    
    metadata.getKeyValuePairs().remove(keyValue);
    mongoTemplate.save(metadata);
  }

  @Override
  public void save(Metadata data) {
    mongoTemplate.save(data);
  }

  @Override
  public void saveTagToHash(String tag, String hash) {
    Query query = new Query();
    query.addCriteria(Criteria.where("hash").is(hash));
    Metadata metadata = mongoTemplate.findOne(query, Metadata.class);
    
    if(metadata == null){
      metadata = new Metadata(hash);
    }
    
    metadata.getTags().add(tag);
    mongoTemplate.save(metadata);
  }

  @Override
  public void saveKeyValuePairToHash(KeyValuePair<String, String> keyValuePair, String hash) {
    Query query = new Query();
    query.addCriteria(Criteria.where("hash").is(hash));
    Metadata metadata = mongoTemplate.findOne(query, Metadata.class);
    
    if(metadata == null){
      metadata = new Metadata(hash);
    }
    
    metadata.getKeyValuePairs().add(keyValuePair);
    mongoTemplate.save(metadata);
  }
}
