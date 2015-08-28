package gov.loc.rdc.entities;

import gov.loc.rdc.utils.KeyValueJsonConverter;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class KeyValuePairTest extends Assert{
  
  @Test
  public void testConvert() throws Exception{
    List<KeyValuePair<String, String>> keyValuePairs = new ArrayList<KeyValuePair<String,String>>();
    keyValuePairs.add(new KeyValuePair<String, String>("fooKey", "fooValue"));
    keyValuePairs.add(new KeyValuePair<String, String>("barKey", "barValue"));
    keyValuePairs.add(new KeyValuePair<String, String>("hamKey", "hamValue"));
    String keyValuePairsAsString = KeyValueJsonConverter.convertToJson(keyValuePairs);
    assertEquals("[{\"key\":\"fooKey\",\"value\":\"fooValue\"},{\"key\":\"barKey\",\"value\":\"barValue\"},{\"key\":\"hamKey\",\"value\":\"hamValue\"}]", keyValuePairsAsString);
    
    List<KeyValuePair<String, String>> convertedPairs = KeyValueJsonConverter.convertToPairs(keyValuePairsAsString);
    
    assertEquals(keyValuePairs, convertedPairs);
  }
}
