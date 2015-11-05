package gov.loc.rdc.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.loc.rdc.entities.KeyValuePair;

/**
 * Utilities for converting a list of {@link KeyValuePair} to and from a JSON string.
 */
public class KeyValueJsonConverter {

  public static String convertToJson(List<KeyValuePair<String, String>> pairs) throws JsonProcessingException{
    ObjectMapper objectMapper = new ObjectMapper();
    String keyValuePairsAsString = objectMapper.writeValueAsString(pairs);
    return keyValuePairsAsString;
  }
  
  public static List<KeyValuePair<String, String>> convertToPairs(String json) throws JsonParseException, JsonMappingException, IOException{
    ObjectMapper objectMapper = new ObjectMapper();
    TypeReference<List<Map<String, String>>> typeRef = new TypeReference<List<Map<String,String>>>() {};
    List<HashMap<String, String>> maps = objectMapper.readValue(json, typeRef);
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
