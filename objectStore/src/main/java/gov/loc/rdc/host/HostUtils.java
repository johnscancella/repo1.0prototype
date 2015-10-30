package gov.loc.rdc.host;

import java.net.InetAddress;
import java.net.UnknownHostException;

public interface HostUtils {
  public default String getHostName() throws UnknownHostException{
    InetAddress localMachine = InetAddress.getLocalHost();
    return localMachine.getHostName();
  }
}
