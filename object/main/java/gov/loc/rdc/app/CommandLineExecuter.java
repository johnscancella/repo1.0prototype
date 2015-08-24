package gov.loc.rdc.app;

import gov.loc.rdc.FileStorer;
import gov.loc.rdc.hash.Hasher;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class CommandLineExecuter implements CommandLineRunner{
  private static final Logger logger = LoggerFactory.getLogger(CommandLineExecuter.class);
  
  @Autowired
  private Hasher hasher;
  
  @Autowired
  private ThreadPoolTaskExecutor threadExecutor;
  
  @Override
  public void run(String... args) throws Exception {
    if(args.length == 1 && (args[0].equalsIgnoreCase("-h") || args[0].equalsIgnoreCase("--help"))){
      logger.info("Usage: objectStore [source file...] [destination directory]");
      logger.info("       Where 'source file...' is one or more files or directories");
      logger.info("       and 'destination directory' is where to start storing the objects based on hash code.");
    }
    else if(args.length < 2){
      logger.error("Wrong number of arguments. See usage with -h or --help");
    }
    else {
      File destinationDir = new File(args[args.length -1]);
      List<File> files = new ArrayList<>();
      for(int index=0; index < args.length-2; index++){
        files.add(new File(args[index]));
      }
      storeFiles(destinationDir, files);
    }
  }
  
  protected void storeFiles(File destination, Collection<File> files){
    for(File file : files){
      if(file.isDirectory()){
        Collection<File> subFiles = FileUtils.listFiles(file, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        storeFiles(destination, subFiles);
      }else{
        logger.debug("creating thread to hash and store file {}", file);
        FileStorer storerThread = new FileStorer(file, destination, hasher);
        threadExecutor.execute(storerThread);
      }
    }
  }
}
