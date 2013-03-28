package com.sparshui.server;

import java.io.IOException;
import java.util.Vector;

import com.sparshui.common.TouchState;
import com.sparshui.gestures.Gesture;


public class Group {

	private int _id;
	private Vector _gestureIDs;
	private Vector _gestures;
	private Vector _touchPoints;
	private ServerToClientProtocol _clientProtocol;

	
	public Group(int id, Vector gestureIDs,
			ServerToClientProtocol clientProtocol) {
		_id = id;
		_gestureIDs = gestureIDs;
		_gestures = new Vector();
		_touchPoints = new Vector();
		_clientProtocol = clientProtocol;
		for (int i = 0; i < _gestureIDs.size(); i++) {
		  Gesture gesture = GestureFactory.createGesture(_gestureIDs.get(i));
		  if (gesture != null)
  			_gestures.add(gesture);
		}
	}

	
	public int getID() {
		return _id;
	}

	
  public synchronized void update(TouchPoint changedPoint) {
    Vector events = new Vector();
    
    int state = changedPoint.getState();

    if (state == TouchState.BIRTH)
      _touchPoints.add(changedPoint);

    
    
    Vector clonedPoints = null;
    
    for (int i = 0; i < _gestures.size(); i++) {
      Gesture gesture = (Gesture) _gestures.get(i);
      
      
      events.addAll(gesture.processChange(clonedPoints == null ? _touchPoints
          : clonedPoints, changedPoint));
      
    }

    
    if (state == TouchState.DEATH)
      _touchPoints.remove(changedPoint);

    try {
      _clientProtocol.processEvents(_id, events);
    } catch (IOException e) {
      
    }
  }

}
