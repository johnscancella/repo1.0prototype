package gov.loc.rdc.consumers;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.optional.ssh.Scp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Envelope;

import gov.loc.rdc.domain.ScpInfo;
import gov.loc.rdc.entities.FileStoreData;
import gov.loc.rdc.hash.HashPathUtils;
import gov.loc.rdc.hash.SHA256Hasher;
import gov.loc.rdc.host.HostUtils;
import gov.loc.rdc.repositories.FileStoreMetadataRepository;

public class FilePullRequestConsumer extends AbstractFileRequestConsumer implements HashPathUtils, HostUtils {
  private static final Logger logger = LoggerFactory.getLogger(FilePullRequestConsumer.class);
  private static final String username = System.getProperty("user.name");
  private static final String TMP_DIR = System.getProperty("java.io.tmpdir");
  private static final String SSH_KEY_FILE = "~/.ssh/id_rsa";
  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss-SSS");
  
  public FilePullRequestConsumer(Channel channel, File objectStoreRootDir, FileStoreMetadataRepository fileStoreRepo) {
    super(channel, objectStoreRootDir, fileStoreRepo);
  }

  @Override
  public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body) throws IOException {
    try {
      ScpInfo info = convertMessageBody(body);
      if(fileAlreadyExists(info.getHash())){ //TODO might become a problem if same server gets multiple requests for same hash.
        logger.info("File with hash [{}] is already stored on [{}]. Skipping scp pull request.", info.getHash(), getHostName());
      }
      else{
        Path tempFile = scpFile(info);
        move(tempFile, info.getHash());
        fileStoreRepo.upsert(new FileStoreData(info.getHash(), getHostName()));
      }
      getChannel().basicAck(envelope.getDeliveryTag(), false);
    } catch (Exception e) {
      logger.error("Error while processing file pull request [{}].", envelope.getDeliveryTag(), e);
      getChannel().basicNack(envelope.getDeliveryTag(), false, true);
    }
  }
  
  protected ScpInfo convertMessageBody(byte[] body) throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    ScpInfo info = mapper.readValue(body, ScpInfo.class);
    return info;
  }
  
  protected boolean fileAlreadyExists(String hash){
    Path file = computeStoredPathLocation(objectStoreRootDir, hash);
    return Files.exists(file);
  }
  
  protected Path scpFile(ScpInfo scpInfo) throws UnknownHostException{
    StringBuilder remoteFile = new StringBuilder();
    remoteFile.append(username).append('@').append(scpInfo.getServer()).append(':').append(scpInfo.getFilepath());
    
    StringBuilder localFile = new StringBuilder(TMP_DIR);
    localFile.append(File.separatorChar).append(scpInfo.getHash()).append('_').append(LocalDateTime.now().format(DATE_TIME_FORMATTER));
    logger.info("Copying [{}] to [{}] on [{}]", remoteFile.toString(), localFile.toString(), getHostName());
    
    Scp scpCommand = createScpCommand(remoteFile.toString(), localFile.toString(), scpInfo.getPort());
    scpCommand.execute();
    
    return Paths.get(localFile.toString());
  }
  
  protected Scp createScpCommand(String remoteFile, String localFile, int port){
    Scp scpCommand = new Scp();
    
    scpCommand.setFile(remoteFile);
    //assumes only downloading one at a time.
    scpCommand.setLocalTofile(localFile);
    scpCommand.setKeyfile(SSH_KEY_FILE);
    scpCommand.setPort(port);
    scpCommand.setProject(new Project()); // prevent a NPE (Ant works with projects)
    scpCommand.setTrust(true); // workaround for not supplying known hosts file
    
    return scpCommand;
  }
  
  protected void move(Path tempFile, String suppliedHash) throws Exception{
    String hash = SHA256Hasher.hash(Files.newInputStream(tempFile, StandardOpenOption.READ));
    if(suppliedHash != hash){
      throw new Exception("Supplied hash [" + suppliedHash + "] does not match computed hash [" + hash + "]!");
    }
    Path finalDestination = computeStoredPathLocation(objectStoreRootDir, hash);
    
    logger.info("Moving [{}] to [{}] on [{}].", tempFile, finalDestination, getHostName());
    Path parentDirPath = finalDestination.getParent();
    if(!Files.exists(parentDirPath)){
      Files.createDirectories(parentDirPath);
    }
    
    Files.move(tempFile, finalDestination);
  }
}
