package gov.loc.rdc.controllers;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gov.loc.rdc.domain.ScpInfo;
import gov.loc.rdc.tasks.FilePullRequestTask;

/**
 * Manages pull requests for files.
 */
@RestController
public class FilePullRequestController extends AbstractRequestController{
  private static final Logger logger = LoggerFactory.getLogger(FilePullRequestController.class);
  private static final List<String> QUEUE_NAMES = Arrays.asList("scpLongTerm", "scpAccess");

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

  @RequestMapping(value = "/v1/file/pull/{server}/{hash}", method = {RequestMethod.POST, RequestMethod.PUT })
  public void filePullRequest(@PathVariable String server, @PathVariable String hash, @RequestParam String file) {
    ScpInfo scpInfo = new ScpInfo(server, 22, file, hash);
    FilePullRequestTask task = new FilePullRequestTask(scpInfo, channel, QUEUE_NAMES);
    threadExecutor.execute(task);
  }
}
