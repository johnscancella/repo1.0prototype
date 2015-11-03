package gov.loc.rdc.host;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Sharable methods for dealing with getting host information.
 */
public interface HostUtils {
  public default String getHostName() throws UnknownHostException{
    InetAddress localMachine = InetAddress.getLocalHost();
    return localMachine.getHostName();
  }
}
