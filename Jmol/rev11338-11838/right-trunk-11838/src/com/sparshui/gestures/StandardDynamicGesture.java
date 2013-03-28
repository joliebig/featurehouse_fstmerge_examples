package com.sparshui.gestures;

import java.util.HashMap;
import java.util.Vector;

import com.sparshui.common.Location;
import com.sparshui.common.TouchState;
import com.sparshui.server.TouchPoint;


public abstract class StandardDynamicGesture implements Gesture {
	
	protected HashMap _knownPoints;
	protected HashMap _traceData;
	protected Location _oldCentroid;
	protected Location _newCentroid;
	protected float _sumX, _sumY;
	
	
	protected StandardDynamicGesture() {
		_knownPoints = new HashMap();
		_traceData = new HashMap();
		_oldCentroid = new Location(0, 0);
		_newCentroid = new Location(0, 0);
	}

	
	
	public abstract String getName();

	
	public Vector processChange(Vector touchPoints,
			TouchPoint changedPoint) {
		Vector events = null;
		
		switch(changedPoint.getState()) {
			case TouchState.BIRTH:
				events = handleBirth(changedPoint);
				break;
			case TouchState.MOVE:
				events = handleMove(changedPoint);
				break;
			case TouchState.DEATH:
				
				events = handleDeath(changedPoint);
				
				break;
		}
		
		return (events != null) ? events : new Vector();
	}
	
	
	protected TouchData createTouchData(TouchPoint touchPoint) {
		return new TouchData(touchPoint.getLocation());
	}
	
	
	protected abstract Vector processBirth(TouchData touchData);
	
	
	protected abstract Vector processMove(TouchData touchData);
	
	
	protected abstract Vector processDeath(TouchData touchData);
	
	
	private Vector handleBirth(TouchPoint touchPoint) {
		TouchData touchData = createTouchData(touchPoint);
		_knownPoints.put(new Integer(touchPoint.getID()), touchData);
		moveCentroid(touchData.getLocation().getX(), touchData.getLocation().getY());
		return processBirth(touchData);
	}
	
	
	private Vector handleMove(TouchPoint touchPoint) {
		TouchData touchData = (TouchData) _knownPoints.get(new Integer(touchPoint.getID()));
		touchData.setLocation(touchPoint.getLocation());
		moveCentroid(
				touchData.getLocation().getX() - touchData.getOldLocation().getX(),
				touchData.getLocation().getY() - touchData.getOldLocation().getY()
		);
		return processMove(touchData);
	}
	
	
	private Vector handleDeath(TouchPoint touchPoint) {
	  Integer iid = new Integer(touchPoint.getID());
		TouchData touchData = (TouchData) _knownPoints.get(iid);
		touchData.setLocation(touchPoint.getLocation());
		_knownPoints.remove(iid);
		moveCentroid(-touchData.getOldLocation().getX(), -touchData.getOldLocation().getY());
		return processDeath(touchData);
	}
	
	
	private void moveCentroid(float dx, float dy) {
		_sumX += dx;
		_sumY += dy;
		_oldCentroid = _newCentroid;
		_newCentroid = new Location(_sumX / _knownPoints.size(), _sumY / _knownPoints.size());
	}

	
	public class TouchData {
		private Location _location;
		private Location _oldLocation;
		public TouchData(Location location) {
			_oldLocation = _location = location;
		}
		public Location getLocation() {
			return _location;
		}
		public Location getOldLocation() {
			return _oldLocation;
		}
		public void setLocation(Location location) {
			_oldLocation = _location;
			_location = location;
		}
	}
}
