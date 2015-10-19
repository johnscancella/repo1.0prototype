package gov.loc.rdc.tasks;

import gov.loc.rdc.errors.BadParametersException;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.optional.ssh.Scp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.async.DeferredResult;

public class ScpFileTransferTask implements Runnable{
  private static final Logger logger = LoggerFactory.getLogger(StoreFileTask.class);
  
  private final DeferredResult<Boolean> result;
  private final String toUrl;
  private final String filePath;
  private final String keyPath;
  private static final String USER = System.getProperty("user.name");
  
  public ScpFileTransferTask(DeferredResult<Boolean> result, String toUrl, String filePath, String keyPath){
    this.result = result;
    this.toUrl = toUrl;
    this.filePath = filePath;
    this.keyPath = keyPath;
  }

  @Override
  public void run() {
    if(Files.exists(Paths.get(filePath))){
      logger.info("Secure copying [{}] to [{}].", filePath, toUrl);
      String remoteToUrl = generateToUrl();
      try{
        copyFile(remoteToUrl);
        result.setResult(true);
      }
      catch(Exception e){
        logger.error("Unable to scp file [{}] to [{}].", filePath, toUrl, e);
        result.setErrorResult(e);
      }
    }
    else{
      logger.error("[{}] doesn't exist on this server! Can't scp a file that doesn't exist.", filePath);
      result.setErrorResult(new BadParametersException(filePath + " doesn't exist, therefore I can't scp it to " + toUrl));
    }
  }
  
  protected String generateToUrl(){
    StringBuilder url = new StringBuilder();
    
    url.append(USER).append('@').append(toUrl);
    
    return url.toString();
  }
  
  protected void copyFile(String remoteToUrl){
    Scp scp = new Scp();
    if(remoteToUrl.endsWith("/")){
      scp.setRemoteTodir(remoteToUrl);
    }
    else{
      scp.setRemoteTofile(remoteToUrl);
    }
    
    scp.setKeyfile(keyPath);
    scp.setLocalFile(filePath);
    scp.setProject(new Project()); // prevent a NPE (Ant works with projects)
    scp.setTrust(true); // workaround for not supplying known hosts file
    scp.execute();
  }

}
