
package launcher.ipc;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;


public class Server {
  
  private final static Logger LOG = Logger.getLogger("genj.ipc");

  
  private int maxBuffer = 256;
  
  
  private ServerSocket local;
  
  
  private CallHandler handler;
  
  
  private int timeout = 200;
  
  
  private String[] terminators = IPCHelper.TERMINATORS;
  
  
  private Thread thread;
  
  
  public Server(int port, CallHandler handler) throws IOException {
    
    this.handler = handler;
    
    
    local = new ServerSocket(port);
    
    
    thread = new Thread(new Runnable() {
      public void run() {
        while (true) try {
          read();
        } catch (Throwable t) {
        }
      }
    });
    thread.setDaemon(true);
    thread.start();
    
    
  }
  
  
  private void read() throws IOException {
    
    Socket remote = local.accept();
    
    try {
      
      
      remote.setSoTimeout(timeout);
      
      
      String in = IPCHelper.read(remote.getInputStream(), terminators, maxBuffer);
      
      
      String out = handler.handleCall(in);
      
      
      IPCHelper.write(remote.getOutputStream(), out+terminators[0]);
      
    } catch (IOException e){
      LOG.info(e.getMessage()+" ("+remote.getRemoteSocketAddress()+")");
    } finally {
      remote.close();
    }
    
  }

}
