package gov.loc.rdc.tasks;

import gov.loc.rdc.entities.KeyValuePair;
import gov.loc.rdc.repositories.MetadataRepository;

public abstract class AbstractKeyValuePairTask extends AbstractMetadataStoreTask{
  protected final KeyValuePair<String, String> pair;
  
  public AbstractKeyValuePairTask(final MetadataRepository repository, 
                                final String algorithm, final String hash, 
                                final KeyValuePair<String, String> pair){
    super(repository, algorithm, hash);
    this.pair = pair;
  }
}
