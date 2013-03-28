package com.sparshui.server;

import com.sparshui.common.Location;
import com.sparshui.common.TouchState;


public class TouchPoint {
	
	private static int nextID = 0;
	
	
	private static Object idLock = new Object();
	
	
	private int _id;
	
	
	private Location _location;
	
	
	private int _state;
	
	
	private boolean _changed;
	
	private long _time;
	
	
	private Group _group;

	
	public TouchPoint(Location location) {
		synchronized(idLock) {
			_id = nextID++;
		}
		_location = location;
		_state = TouchState.BIRTH;
		_time = System.currentTimeMillis();
	}
	
	
	public TouchPoint(TouchPoint tp) {
		_id = tp._id;
		_location = tp._location;
		_state = tp._state;
		_time = tp._time;
	}

	public long getTime() {
		return _time;
	}
	
	
	public int getID() {
		return _id;
	}
	
	
	public Location getLocation() {
		return _location;
	}
	
	
	public int getState() {
		return _state;
	}
	
	
	public void setGroup(Group group) {
		_group = group;
		
		_group.update(this);
	}
	
	
	public void update(Location location, int state) {
		_location = location;
		_state = state;
		_changed = true;
		if(_group != null) _group.update(this);
	}
	
	
	public void resetChanged() {
		_changed = false;
	}
	
	
	public boolean isChanged() {
		return _changed;
	}
	
	
	public Object clone() {
		return new TouchPoint(this);
	}

}
