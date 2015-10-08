package gov.loc.rdc.controllers;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RoundRobinServerControllerTest extends Assert{

private RoundRobinServerController sut;
  
  @Before
  public void setup(){
    sut = new RoundRobinServerController();
  }
  
  @Test
  public void testGetAvailableServers(){
    sut.setServerAddresses(Arrays.asList("a", "b", "c", "d", "e", "f"));
    List<String> expected = Arrays.asList("b", "c", "d", "e", "f", "a");
    List<String> actual = sut.getAvailableServers();
    assertEquals(expected, actual);
  }
  
  @Test
  public void testReorder(){
    List<String> strings = Arrays.asList("a", "b", "c", "d", "e", "f");
    List<String> expected1 = Arrays.asList("a", "b", "c", "d", "e", "f");
    List<String> expected2 = Arrays.asList("b", "c", "d", "e", "f", "a");
    List<String> expected3 = Arrays.asList("c", "d", "e", "f", "a", "b");
    List<String> expected4 = Arrays.asList("d", "e", "f", "a", "b", "c");
    List<String> expected5 = Arrays.asList("e", "f", "a", "b", "c", "d");
    List<String> expected6 = Arrays.asList("f", "a", "b", "c", "d", "e");
    
    List<String> returned = sut.reorder(0, strings);
    assertEquals(expected1, returned);
    
    returned = sut.reorder(1, strings);
    assertEquals(expected2, returned);
    
    returned = sut.reorder(2, strings);
    assertEquals(expected3, returned);
    
    returned = sut.reorder(3, strings);
    assertEquals(expected4, returned);
    
    returned = sut.reorder(4, strings);
    assertEquals(expected5, returned);
    
    returned = sut.reorder(5, strings);
    assertEquals(expected6, returned);
  }
  
  @Test
  public void testReorderWithIntegerRollOver(){
    int index = Integer.MAX_VALUE;
    List<String> strings = Arrays.asList("a", "b", "c", "d", "e", "f");
    List<String> expected1 = Arrays.asList("b", "c", "d", "e", "f", "a");
    List<String> expected2 = Arrays.asList("c", "d", "e", "f", "a", "b");
    
    List<String> returned = sut.reorder(index, strings);
    assertEquals(expected1, returned);
    
    index++;
    returned = sut.reorder(index, strings);
    assertEquals(expected2, returned);
  }
}
