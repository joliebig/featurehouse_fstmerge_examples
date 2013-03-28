
package launcher.ipc;

import java.io.IOException;
import java.net.Socket;


public class Client {
  
  
  private int timeout = 500;
  
  
  private String[] terminators = IPCHelper.TERMINATORS;
  
  
  private int maxBuffer = 256;
  
  
  public String send(int port, String msg) throws IOException {
    
    Socket remote = new Socket((String)null, port);
    try {
      remote.setSoTimeout(timeout);
      IPCHelper.write(remote.getOutputStream(), msg);
      return IPCHelper.read(remote.getInputStream(), terminators, 16);
    } finally {
      remote.close();
    }
    
  }

}
