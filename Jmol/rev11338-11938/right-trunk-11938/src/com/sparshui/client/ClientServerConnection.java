package com.sparshui.client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.sparshui.common.ConnectionType;
import com.sparshui.common.NetworkConfiguration;


public class ClientServerConnection extends Thread {
	
	private SparshClient _client;
	private Socket _socket;
	private ClientToServerProtocol _protocol;
	
	
	public ClientServerConnection(String address, SparshClient client) throws UnknownHostException, IOException {
		_client = client;
		_socket = new Socket(address, NetworkConfiguration.CLIENT_PORT);
		DataOutputStream out = new DataOutputStream(_socket.getOutputStream());
		out.writeByte(ConnectionType.CLIENT);
		_protocol = new ClientToServerProtocol(_socket);
		this.start();
	}

	
	
	public void run() {
	  Thread.currentThread().setName("SparshUI Client->ServerConnection");
		while(_socket.isConnected()) {
			if (!_protocol.processRequest(_client)) 
			  break;
		}
	}
	
	public void close() {
	  try {
      _socket.close();
    } catch (IOException e) {
      
    }
	}

}
