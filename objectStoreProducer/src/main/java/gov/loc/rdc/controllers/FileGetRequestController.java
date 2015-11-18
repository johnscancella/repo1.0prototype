package gov.loc.rdc.controllers;

import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import gov.loc.rdc.entities.FileStoreData;
import gov.loc.rdc.errors.ResourceNotFoundException;
import gov.loc.rdc.repositories.FileStoreMetadataRepository;

@RestController
public class FileGetRequestController{
  protected static final Logger logger = LoggerFactory.getLogger(FileGetRequestController.class);
  
  @Autowired
  private FileStoreMetadataRepository repo;
  
  //redirect to a server that contains the file.
  @RequestMapping(value = "/v1/file/get/{hash}", method = RequestMethod.GET)
  public RedirectView getFile(@PathVariable String hash) throws Exception{
    FileStoreData data = repo.get(hash);
    
    if(data.getServers().size() == 0){ throw new ResourceNotFoundException(); }
    
    String server = getRandomServer(data.getServers());
    String redirectUrl = createRedirectUrl(server, "/v1/file/get/", hash);

    RedirectView redirectView = new RedirectView();
    redirectView.setUrl(redirectUrl);
    
    return redirectView;
  }
  
  protected String getRandomServer(Set<String> servers){
    Random r = new Random();
    int index = r.nextInt(servers.size());
    String server = servers.toArray(new String[servers.size()])[index];
    return server;
  }
  
  protected String createRedirectUrl(String server, String ... pathParts){
    StringBuilder url = new StringBuilder();
    
    url.append("http://").append(server);
    for(String pathPart : pathParts){
      url.append(pathPart);
    }
    
    return url.toString();
  }

  //for unit tests only
  protected void setRepo(FileStoreMetadataRepository repo) {
    this.repo = repo;
  }
                                                               
}
