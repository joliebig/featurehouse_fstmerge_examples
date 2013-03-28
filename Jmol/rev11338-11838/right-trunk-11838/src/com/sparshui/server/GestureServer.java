package com.sparshui.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import org.jmol.api.JmolGestureServerInterface;
import org.jmol.util.Logger;

import com.sparshui.common.ConnectionType;
import com.sparshui.common.NetworkConfiguration;



public class GestureServer implements Runnable, JmolGestureServerInterface {
  private ServerSocket _serverSocket;
  private Vector _clients = new Vector();
  private Thread gsThread;
  public void startGestureServer() {
    gsThread = new Thread(new GestureServer());
    gsThread.setName("Jmol SparshUI GestureServer on port " + NetworkConfiguration.PORT);
    gsThread.start();
  }

  public void dispose() {
    try {
      _serverSocket.close();
    } catch (Exception e) {
      
    }
    try {
      gsThread.interrupt();
    } catch (Exception e) {
      
    }
    _serverSocket = null;
    gsThread = null;
  }

  
  public void run() {
    try {
      openSocket();
      acceptConnections();
    } catch (Exception e) {
      Logger.info("[GestureServer] connection unavailable");
    }
  }

  
  void processBirth(TouchPoint touchPoint) {
    Vector clients_to_remove = null;
    for (int i = 0; i < _clients.size(); i++) {
      ClientConnection client = (ClientConnection) _clients.get(i);
      
      try {
        if (client.processBirth(touchPoint))
          break;
      } catch (IOException e) {
        
        
        
        if (clients_to_remove == null)
          clients_to_remove = new Vector();
        clients_to_remove.add(client);
      }
    }
    if (clients_to_remove == null)
      return;
    for (int i = 0; i < clients_to_remove.size(); i++) {
      _clients.remove(clients_to_remove.elementAt(i));
      Logger.info("[GestureServer] Client Disconnected");
    }
  }

  
  private void openSocket() {
    try {
      _serverSocket = new ServerSocket(NetworkConfiguration.PORT);
      Logger.info("[GestureServer] Socket Open");
    } catch (IOException e) {
      Logger.error("[GestureServer] Failed to open a server socket.");
      e.printStackTrace();
    }
  }

  
  private void acceptConnections() {
    Logger.info("[GestureServer] Accepting Connections");
    while (!_serverSocket.isClosed()) {
      try {
        acceptConnection(_serverSocket.accept());
      } catch (IOException e) {
        Logger.error("[GestureServer] Failed to establish client connection");
        e.printStackTrace();
      }
    }
    Logger.info("[GestureServer] Socket Closed");
  }

  
  private void acceptConnection(Socket socket) throws IOException {
    
    byte[] add = socket.getInetAddress().getAddress();
    if (add[0] != 127 || add[1] != 0 || add[2] != 0 || add[3] != 1)
      return;
    int type = socket.getInputStream().read();
    if (type == ConnectionType.CLIENT) {
      acceptClientConnection(socket);
    } else if (type == ConnectionType.INPUT_DEVICE) {
      acceptInputDeviceConnection(socket);
    }
  }

  
  private void acceptClientConnection(Socket socket) throws IOException {
    Logger.info("[GestureServer] ClientConnection Accepted");
    ClientConnection cc = new ClientConnection(socket);
    _clients.add(cc);
  }

  
  private void acceptInputDeviceConnection(Socket socket) throws IOException {
    Logger.info("[GestureServer] InputDeviceConnection Accepted");
    new InputDeviceConnection(this, socket);
  }
}
