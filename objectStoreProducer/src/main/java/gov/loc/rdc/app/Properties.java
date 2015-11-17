package gov.loc.rdc.app;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="objectstore.producer", ignoreUnknownFields=true)
public class Properties {
  private Map<String, Integer> storageTypesToCopiesMap = new HashMap<>();

  public Map<String, Integer> getStorageTypesToCopiesMap() {
    return storageTypesToCopiesMap;
  }

  public void setStorageTypesToCopiesMap(
      Map<String, Integer> storageTypesToCopiesMap) {
    this.storageTypesToCopiesMap = storageTypesToCopiesMap;
  }

}
