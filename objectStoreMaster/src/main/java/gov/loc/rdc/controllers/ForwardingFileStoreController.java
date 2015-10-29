package gov.loc.rdc.controllers;

import gov.loc.rdc.entities.FileStoreData;
import gov.loc.rdc.repositories.FileStoreRepository;
import gov.loc.rdc.tasks.OrderedServerForwardedFileExistsTask;
import gov.loc.rdc.tasks.OrderedServerForwardedGetFileTask;
import gov.loc.rdc.tasks.StoreFileMessageTask;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

import com.rabbitmq.client.Channel;

/**
 * Handles forwarding storing and getting files based on their hash to the proper slave node.
 */
@RestController
public class ForwardingFileStoreController implements FileStoreControllerApi{
  @Resource(name="threadPoolTaskExecutor")
  private ThreadPoolTaskExecutor threadExecutor;
  
  @Autowired
  private ServerRegistraController serverRegistraController;
  
  @Autowired
  private MessageQueueController messageQueueController;
  
  @Autowired
  private FileStoreRepository fileStoreRepo; //TODO replace some methods with using this.
  
  @Override
  @RequestMapping(value=RequestMappings.GET_FILE_URL, method=RequestMethod.GET, produces=MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public DeferredResult<byte[]> getFile(@PathVariable String hash){
    DeferredResult<byte[]> result = new DeferredResult<>();
    FileStoreData data = fileStoreRepo.get(hash);
    List<String> urls = serverRegistraController.getUrls(data.getServers());
    
    OrderedServerForwardedGetFileTask task = new OrderedServerForwardedGetFileTask(urls, result, hash);
    threadExecutor.execute(task);
    
    return result;
  }
  
  @Override
  @RequestMapping(value=RequestMappings.STORE_FILE_URL, method={RequestMethod.POST, RequestMethod.PUT})
  public DeferredResult<String> storeFile(@RequestParam(value="file") MultipartFile file){
    DeferredResult<String> result = new DeferredResult<String>();
    Channel channel = messageQueueController.getChannel();
    
    for(String queueName : messageQueueController.getFileSendingQueueNames()){
      StoreFileMessageTask task = new StoreFileMessageTask(result, file, channel, queueName);
      threadExecutor.execute(task);
    }
    
    return result;
  }
  
  @Override
  @RequestMapping(value=RequestMappings.FILE_EXISTS_URL, method={RequestMethod.GET, RequestMethod.POST})
  public DeferredResult<Boolean> fileExists( @PathVariable String hash){
    DeferredResult<Boolean> result = new DeferredResult<Boolean>();
    FileStoreData data = fileStoreRepo.get(hash);
    List<String> urls = serverRegistraController.getUrls(data.getServers());
    
    OrderedServerForwardedFileExistsTask task = new OrderedServerForwardedFileExistsTask(urls, result, hash);
    threadExecutor.execute(task);
    
    return result;
  }

  //only used in unit test
  protected void setThreadExecutor(ThreadPoolTaskExecutor threadExecutor) {
    this.threadExecutor = threadExecutor;
  }

  //only used in unit test
  protected void setServerRegistraController(ServerRegistraController roundRobinServerController) {
    this.serverRegistraController = roundRobinServerController;
  }

  protected void setFileStoreRepo(FileStoreRepository fileStoreRepo) {
    this.fileStoreRepo = fileStoreRepo;
  }

  protected void setMessageQueueController(MessageQueueController messageQueueController) {
    this.messageQueueController = messageQueueController;
  }
}
