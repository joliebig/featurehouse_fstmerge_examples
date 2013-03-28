

package edu.rice.cs.drjava;

import java.io.*;
import java.net.*;
import edu.rice.cs.drjava.config.OptionConstants;


public class RemoteControlClient {
  
  protected static Boolean _serverRunning = null;
  
  
  protected static String _serverUser = null;
  
  
  public static final int REMOTE_CONTROL_TIMEOUT = 250;
  
  
  public static synchronized boolean isServerRunning() {
    if (_serverRunning == null) {
      try {
        openFile(null);
      }
      catch(IOException e) { _serverRunning = false; }
    }
    return _serverRunning;
  }
  
  
  public static String getServerUser() { return _serverUser; }
  
  
  public static synchronized boolean openFile(File f) throws IOException {
    try {
      
      DatagramSocket socket = new DatagramSocket();
      socket.setSoTimeout(REMOTE_CONTROL_TIMEOUT);
      
      
      String dString = RemoteControlServer.QUERY_PREFIX;
      if (f != null) {
        dString = dString + " " + f.getAbsolutePath();
      }
      byte[] buf = dString.getBytes();
      InetAddress address = InetAddress.getByName("127.0.0.1");
      DatagramPacket packet = new DatagramPacket(buf, buf.length, address,
                                                 DrJava.getConfig().getSetting(OptionConstants.REMOTE_CONTROL_PORT));
      socket.send(packet);
      
      
      buf = new byte[512];
      packet = new DatagramPacket(buf, buf.length);
      socket.receive(packet);
      
      
      String received = new String(packet.getData(), 0, packet.getLength());
      _serverRunning = received.startsWith(RemoteControlServer.RESPONSE_PREFIX);
      if (_serverRunning) {
        int pos = received.indexOf('!');
        _serverUser = received.substring(RemoteControlServer.RESPONSE_PREFIX.length(), pos);
      }
      else {
        _serverUser = null;
      }
      socket.close();
      
      return (received.equals(RemoteControlServer.RESPONSE_PREFIX_WITH_USER));
    }
    catch (SocketTimeoutException e) {
      _serverRunning = false;
      return false;
    }
  }
  
  
  public static void main(String[] args) {
    for (int i = 0; i < args.length; ++i) {
      try {
        boolean ret = openFile(new File(args[i]));
        System.out.println("openFile returned " + ret);
      }
      catch(IOException ioe) {
        ioe.printStackTrace();
      }
    }
  }
}