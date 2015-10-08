package gov.loc.rdc.controllers;

public class RequestMappings {
  public static final String VERIFY_INTEGRITY_URL = "/verifyintegrity";
  public static final String GET_FILE_URL = "/getfile/{algorithm}/{hash}";
  public static final String STORE_FILE_URL = "/storefile";
  public static final String FILE_EXISTS_URL = "/fileexists/{algorithm}/{hash}";
}