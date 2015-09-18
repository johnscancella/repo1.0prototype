package gov.loc.rdc.controllers;

import java.util.Enumeration;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class UserRequestLoggingInterceptor extends HandlerInterceptorAdapter {
  private static final Logger logger = LoggerFactory.getLogger(UserRequestLoggingInterceptor.class);

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    if (request.getServletPath() != null && !request.getServletPath().equals("/error")) {
      logger.info("Request from [{}] on [{}] for [{}]", request.getRemoteUser(), request.getRemoteAddr(), request.getServletPath());

      logger.debug("Request from [{}] on [{}] for [{}]. Query for request is: [{}]. Header names are: [{}]. Cookies are: [{}]",
          request.getRemoteUser(), 
          request.getRemoteAddr(), 
          request.getServletPath(), 
          request.getQueryString(), 
          formatHeaderNames(request.getHeaderNames()), 
          formatCookies(request.getCookies()));
    }
    return true;
  }
  
  protected String formatHeaderNames(Enumeration<String> headerNames){
    StringBuilder sb = new StringBuilder();
    
    if(headerNames != null && headerNames.hasMoreElements()){
      sb.append(headerNames.nextElement());
      
      while(headerNames.hasMoreElements()){
        sb.append(",").append(headerNames.nextElement());
      }
    }
    
    return sb.toString();
  }
  
  protected String formatCookies(Cookie[] cookies){
    StringBuilder sb = new StringBuilder();
    
    if(cookies != null){
      for(Cookie cookie : cookies){
        sb.append("Cookie [Comment=").append(cookie.getComment());
        sb.append(", Domain=").append(cookie.getDomain());
        sb.append(", Max Age=").append(cookie.getMaxAge());
        sb.append(", Path=").append(cookie.getPath());
        sb.append(", Secure=").append(cookie.getSecure());
        sb.append(", Name=").append(cookie.getName());
        sb.append(", Value=").append(cookie.getValue());
        sb.append(", Version=").append(cookie.getVersion());
        sb.append(", Http Only=").append(cookie.isHttpOnly()).append("]");
      }
    }
    
    return sb.toString();
  }
}
