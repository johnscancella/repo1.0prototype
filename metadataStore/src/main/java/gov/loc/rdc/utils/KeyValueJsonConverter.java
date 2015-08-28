package gov.loc.rdc.utils;

import gov.loc.rdc.entities.KeyValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class KeyValueJsonConverter {

  public static String convertToJson(List<KeyValuePair<String, String>> pairs) throws JsonProcessingException{
    ObjectMapper objectMapper = new ObjectMapper();
    String keyValuePairsAsString = objectMapper.writeValueAsString(pairs);
    return keyValuePairsAsString;
  }
  
  public static List<KeyValuePair<String, String>> convertToPairs(String json) throws JsonParseException, JsonMappingException, IOException{
    ObjectMapper objectMapper = new ObjectMapper();
    List<HashMap<String, String>> maps = objectMapper.readValue(json, List.class);
    List<KeyValuePair<String, String>> convertedPairs = new ArrayList<>();
    
    for(HashMap<String, String> map : maps){
      String key = map.get("key");
      String value = map.get("value");
      KeyValuePair<String, String> pair = new KeyValuePair<String, String>(key, value);
      convertedPairs.add(pair);
    }
    
    return convertedPairs;
  }
}
