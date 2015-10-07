package gov.loc.rdc.tasks;

import gov.loc.rdc.repositories.MetadataRepository;

public abstract class AbstractTagTask extends AbstractMetadataStoreTask{
  protected final String tag;
  
  public AbstractTagTask(final MetadataRepository repository, 
                            final String algorithm, final String hash,
                            final String tag){
    super(repository, algorithm, hash);
    this.tag = tag;
  }
}
