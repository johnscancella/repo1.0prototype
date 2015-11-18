package gov.loc.rdc.controllers;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import gov.loc.rdc.domain.PutInfo;
import gov.loc.rdc.tasks.FilePutRequestTask;

@RestController
public class FilePutRequestController extends AbstractRequestController{
  private static final List<String> QUEUE_NAMES = Arrays.asList("putLongTerm", "putAccess");
  
  @PostConstruct
  protected void setup(){
    try {
      channel = createChannel(mqHost);
      for (String type : QUEUE_NAMES) {
        logger.info("Creating queue for storage type [{}].", type);
        createQueue(type, channel, maxNumberOfMessagesToProcessConcurrently);
      }
    } catch (Exception e) {
      logger.error("Failed to create queues in message store.", e);
    }
  }

  @RequestMapping(value = "/v1/file/put/{hash}", method = {RequestMethod.POST, RequestMethod.PUT })
  public void storeFile(@RequestParam(value="file") MultipartFile file, @PathVariable String hash) throws Exception{
    PutInfo putInfo = new PutInfo(file.getBytes(), hash);
    FilePutRequestTask task = new FilePutRequestTask(channel, QUEUE_NAMES, putInfo);
    threadExecutor.execute(task);
  }
}
