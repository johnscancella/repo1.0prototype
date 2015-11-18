package gov.loc.rdc.consumers;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;

import gov.loc.rdc.repositories.FileStoreMetadataRepository;

public abstract class AbstractFileRequestConsumer extends DefaultConsumer{
  protected static final Logger logger = LoggerFactory.getLogger(AbstractFileRequestConsumer.class);
  protected final File objectStoreRootDir;
  protected final FileStoreMetadataRepository fileStoreRepo;

  public AbstractFileRequestConsumer(Channel channel, File objectStoreRootDir, FileStoreMetadataRepository fileStoreRepo) {
    super(channel);
    this.objectStoreRootDir = objectStoreRootDir;
    this.fileStoreRepo = fileStoreRepo;
  }
}
