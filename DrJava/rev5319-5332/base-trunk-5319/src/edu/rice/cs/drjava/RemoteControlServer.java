

package edu.rice.cs.drjava;

import edu.rice.cs.drjava.ui.MainFrame;
import edu.rice.cs.util.FileOpenSelector;
import edu.rice.cs.util.OperationCanceledException;
import edu.rice.cs.drjava.config.OptionConstants;

import java.io.*;
import java.net.*;


public final class RemoteControlServer {
  
  public static final String QUERY_PREFIX = "DrJava Remote Control?";
  
  
  public static final String RESPONSE_PREFIX = "DrJava Remote Control ";
  
  
  public static final String RESPONSE_PREFIX_WITH_USER = RESPONSE_PREFIX+System.getProperty("user.name") + "!";
  
  
  public RemoteControlServer(MainFrame frame) throws IOException {
    RCServerThread rcsThread = new RCServerThread(frame);
    rcsThread.setDaemon(true);
    rcsThread.start();
  }
  
  
  public static class RCServerThread extends Thread {
    
    protected MainFrame _frame;
    
    
    protected DatagramSocket socket = null;
    
    
    public RCServerThread(MainFrame frame) throws IOException {
      this("RCServerThread", frame);
    }
    
    
    public RCServerThread(String name, MainFrame frame) throws IOException {
      super(name);
      _frame = frame;
      socket = new DatagramSocket(DrJava.getConfig().getSetting(OptionConstants.REMOTE_CONTROL_PORT));
    }

    
    public void run() {
      while (true) {
        try {
          byte[] buf = new byte[256];
          
          
          DatagramPacket packet = new DatagramPacket(buf, buf.length);
          socket.receive(packet);
          
          String request = new String(packet.getData(), 0, packet.getLength());
          
          
          if (request.startsWith(QUERY_PREFIX)) {
            
            String dString = RESPONSE_PREFIX_WITH_USER;
            request = request.substring(QUERY_PREFIX.length());
            
            
            if ((request.length() > 0) && (request.charAt(0) == ' ')) {
              request = request.substring(1);
              
              
              int lineNo = -1;
              int pathSepIndex = request.indexOf(File.pathSeparatorChar);
              if (pathSepIndex >= 0) {
                try {
                  lineNo = Integer.valueOf(request.substring(pathSepIndex+1));
                }
                catch(NumberFormatException nfe) {
                  lineNo = -1;
                }
                request = request.substring(0,pathSepIndex);
              }
              
              final File f = new File(request);
              if (f.exists()) {
                DrJavaRoot.handleRemoteOpenFile(f, lineNo);
              }
            }
            else {
              dString = dString + " Cannot open file!";
            }
            
            buf = dString.getBytes();
            
            
            InetAddress address = packet.getAddress();
            int port = packet.getPort();
            packet = new DatagramPacket(buf, buf.length, address, port);
            
            socket.send(packet);
          }
        }
        catch (SocketTimeoutException e) {
          
        }
        catch (IOException e) { e.printStackTrace(); }
      }
    }
    
    protected void finalize() { if (socket != null) socket.close(); }
  }
  
  
  public static void main(String[] args) {
    try {
      (new RCServerThread(null)).start();
    }
    catch(IOException ioe) {
      System.out.println(ioe);
      ioe.printStackTrace();
    }
  }
}
