package com.sparshui.server;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Vector;

import com.sparshui.common.Location;
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
	
	
	private TouchPoint createTouchPoint(Location location) {
		TouchPoint touchPoint = new TouchPoint(location);
		_gestureServer.processBirth(touchPoint);
		
		return touchPoint;
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
	
	
	private void processTouchPoint(int id, Location location, int state) {
	  Integer iid = new Integer(id);
		if(_touchPoints.containsKey(iid)) {
			TouchPoint touchPoint = (TouchPoint) _touchPoints.get(iid);
			synchronized(touchPoint) {
				touchPoint.update(location, state);
			}
		} else {
			
			_touchPoints.put(iid, createTouchPoint(location));
		}
	}
	
	
	private void receiveData() {
		try {
			while(!_socket.isInputShutdown()) {
				readTouchPoints();
			}
		} catch (IOException e) {
			
			
			System.out.println("[GestureServer] InputDevice Disconnected");
			
		}
	}
	
	
	private void readTouchPoint() throws IOException {
		int id = _in.readInt();
		float x = _in.readFloat();
		float y = _in.readFloat();
		Location location = new Location(x, y);
		int state = (int)_in.readByte();
		processTouchPoint(id, location, state);

		
		
			
		
		if (state == TouchState.DEATH) {
			flagTouchPointForRemoval(id);
		}
	}
	
	
	private void readTouchPoints() throws IOException {
		
		
		
		int count = _in.readInt();
		if(count < 0) {
			_in.close();
			return;
		}
		
		for(int i = 0; i < count; i++) {
			readTouchPoint();
		}
		removeDeadTouchPoints();

		
	}
	
	
	private void startListening() {
		Thread thread = new Thread(this);
		thread.setName("SparshUI InputDeviceConnection");
		thread.start();
	}

	
	
	public void run() {
		receiveData();
	}

	
	
}
