package gov.loc.rdc.controllers;

import java.util.Arrays;

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

      String cookies = request.getCookies() == null ? "" : Arrays.toString(request.getCookies());
      logger.debug("Request from [{}] on [{}] for [{}]. Query for request is: [{}]. Header names are: [{}]. Cookies are: [{}]",
          request.getRemoteUser(), 
          request.getRemoteAddr(), 
          request.getServletPath(), 
          request.getQueryString(), 
          request.getHeaderNames(), 
          cookies);
    }
    return true;
  }
}
