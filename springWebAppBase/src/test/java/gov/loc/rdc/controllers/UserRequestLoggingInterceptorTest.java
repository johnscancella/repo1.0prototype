package gov.loc.rdc.controllers;

import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;

import javax.servlet.http.Cookie;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

public class UserRequestLoggingInterceptorTest extends Assert {
  private UserRequestLoggingInterceptor sut = new UserRequestLoggingInterceptor();
  
  @Test
  public void testLoggingWithoutCookies() throws Exception{
    MockHttpServletRequest request = new MyMockHttpServletRequest(false); 
    boolean returnValue = sut.preHandle(request, null, null);
    assertTrue(returnValue);
  }
  
  @Test
  public void testLoggingWithCookies() throws Exception{
    MockHttpServletRequest request = new MyMockHttpServletRequest(false);
    Cookie cookie = new Cookie("cookieName", "cookieValue");
    request.setCookies(cookie);
    
    boolean returnValue = sut.preHandle(request, null, null);
    assertTrue(returnValue);
  }
  
  @Test
  public void handleNulls() throws Exception{
    MockHttpServletRequest request = new MyMockHttpServletRequest(false);
    request.setServletPath(null);
    
    boolean returnValue = sut.preHandle(request, null, null);
    assertTrue(returnValue);
  }
  
  @Test
  public void handleFilterError() throws Exception{
    MockHttpServletRequest request = new MyMockHttpServletRequest(true);
    request.setServletPath("/error");
    
    boolean returnValue = sut.preHandle(request, null, null);
    assertTrue(returnValue);
  }
  
  private class MyMockHttpServletRequest extends MockHttpServletRequest{
    boolean hasNullHeader = false;
    
    MyMockHttpServletRequest(boolean hasNullHeader){
      this.hasNullHeader = hasNullHeader;
    }
    
    @Override
    public Enumeration<String> getHeaderNames() {
      if(hasNullHeader){
        return null;
      }
      return Collections.enumeration(Arrays.asList("fooHeader1", "fooHeader2", "fooHeader3"));
    }
  }

}
