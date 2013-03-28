package com.sparshui.server;

import java.io.DataInputStream;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Vector;

import com.sparshui.common.Location;
import com.sparshui.common.NetworkConfiguration;
import com.sparshui.common.TouchState;


public class InputDeviceConnection implements Runnable {
	
	
	private GestureServer _gestureServer;
	
	
	private Socket _socket;
	
	
	private DataInputStream _in;
  
  
	
	
	private HashMap _touchPoints;

	private Vector _flaggedids;

	
	public InputDeviceConnection(GestureServer gestureServer, Socket socket) throws IOException {
		_gestureServer = gestureServer;
		_socket = socket;
    _in = new DataInputStream(socket.getInputStream());
    
		_touchPoints = new HashMap();
		_flaggedids = new Vector();
		startListening();
	}
	
	
	private void removeDeadTouchPoints() {
		for (int i = 0; i < _flaggedids.size(); i++) {
		  Integer id = (Integer)_flaggedids.get(i);
		_touchPoints.remove(id);
		}
		_flaggedids.clear();
	}
	
	
	private void flagTouchPointForRemoval(int id) {
		_flaggedids.add(new Integer(id));
	}
	
	
	private void receiveData() {
		try {
			while(!_socket.isInputShutdown()) {
				 readTouchPoints();
				
			}
		} catch (IOException e) {
			System.out.println("[InputDeviceConnection] InputDevice Disconnected");
			_gestureServer.notifyInputLost();
		}
	}
	
	
  private boolean readTouchPoints() throws IOException {
    
    int count = _in.readInt();
    if (count == 0) {
      _in.close();
      return false;
    }
    int touchPointDataLength;

    if (count < 0) {
      count = -count;
      touchPointDataLength = _in.readInt();
    } else {
      
      touchPointDataLength = 13;
    }
    boolean doConsume = false;
    
    for (int i = 0; i < count; i++)
      doConsume |= readTouchPoint(touchPointDataLength);
    removeDeadTouchPoints();
    return doConsume;
  }
	
  
  private boolean readTouchPoint(int len) throws IOException {
    int id = _in.readInt();
    float x = _in.readFloat();
    float y = _in.readFloat();
    int state = (int) _in.readByte();
    long time = (len >= 21 ? _in.readLong() : System.currentTimeMillis());
    if (len > 21) 
      _in.read(new byte[len - 21]);
    Location location = new Location(x, y);
    boolean doConsume = _gestureServer.processTouchPoint(_touchPoints, id,
        location, time, state);
    if (state == TouchState.DEATH)
      flagTouchPointForRemoval(id);
    return doConsume;
  }
  
	
	private void startListening() {
		Thread thread = new Thread(this);
		thread.setName("SparshUI Server->InputDeviceConnection on port " + NetworkConfiguration.DEVICE_PORT);
		thread.start();
	}

	
	
	public void run() {
		receiveData();
	}
	
}
