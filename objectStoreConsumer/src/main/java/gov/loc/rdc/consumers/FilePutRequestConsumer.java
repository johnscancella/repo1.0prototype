package gov.loc.rdc.consumers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Envelope;

import gov.loc.rdc.domain.PutInfo;
import gov.loc.rdc.entities.FileStoreData;
import gov.loc.rdc.hash.HashPathUtils;
import gov.loc.rdc.hash.SHA256Hasher;
import gov.loc.rdc.host.HostUtils;
import gov.loc.rdc.repositories.FileStoreMetadataRepository;

public class FilePutRequestConsumer extends AbstractFileRequestConsumer implements HashPathUtils, HostUtils {
  public FilePutRequestConsumer(Channel channel, File objectStoreRootDir, FileStoreMetadataRepository fileStoreRepo) {
    super(channel, objectStoreRootDir, fileStoreRepo);
  }

  @Override
  public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body) throws IOException {
    try {
      PutInfo info = convertMessageBody(body);
      throwErrorIfHashesDiffer(info);
      File storedFile = computeStoredFileLocation(objectStoreRootDir, info.getHash());
      processFile(info, storedFile);
      getChannel().basicAck(envelope.getDeliveryTag(), false);
    } catch (Exception e) {
      logger.error("Error while processing file put request [{}].", envelope.getDeliveryTag(), e);
      getChannel().basicNack(envelope.getDeliveryTag(), false, true);
    }
  }
  
  protected PutInfo convertMessageBody(byte[] body) throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    PutInfo info = mapper.readValue(body, PutInfo.class);
    return info;
  }
  
  protected void throwErrorIfHashesDiffer(PutInfo info) throws Exception{
    String computedHash = SHA256Hasher.hash(info.getFileBytes());
    if(!info.getHash().equals(computedHash)){
      throw new Exception("Computed hash [" + computedHash + "] doesn't match supplied hash [" + info.getHash() + "]!");
    }
  }
  
  protected void processFile(PutInfo info, File storedFile) throws IOException{
    if(!storedFile.exists()){ //TODO might become a problem if same server gets multiple requests for the same hash.
      logger.debug("creating path [{}] if it does not already exist.", storedFile.getParent());
      Files.createDirectories(storedFile.getParentFile().toPath());
      logger.info("copying byte[] to [{}]", storedFile.toURI());
      Files.write(storedFile.toPath(), info.getFileBytes());
      fileStoreRepo.upsert(new FileStoreData(info.getHash(), getHostName()));
    }
    else{
      logger.info("Already stored file with hash [{}], skipping.", info.getHash());
    }
  }
}
