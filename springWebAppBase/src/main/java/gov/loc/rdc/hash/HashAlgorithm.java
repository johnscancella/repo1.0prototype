package gov.loc.rdc.hash;

import java.util.Arrays;
import java.util.List;

public enum HashAlgorithm {
  SHA256,
  MD5;
  
  private static final List<String> ACCEPTED_HASH_ALGORITHMS = Arrays.asList("sha256", "SHA256", "sha-256", "SHA-256");
  
  public static boolean algorithmSupported(String algorithm){
    if(!ACCEPTED_HASH_ALGORITHMS.contains(algorithm)){
      return false;
    }
    
    return true;
  }
  
  public static boolean algorithmSupported(HashAlgorithm algorithm){
    if(HashAlgorithm.SHA256 != algorithm){
      return false;
    }
    
    return true;
  } 
}
