package com.sparshui.server;

import com.sparshui.common.Location;
import com.sparshui.common.TouchState;


public class TouchPoint {
	
	
	
	
	
	
	
	private int _id;
	
	
	private Location _location;
	
	
	private int _state;
	
	
	private boolean _changed;
	
	private long _time;
	
	
	private Group _group;

	
	public boolean isClaimed() {
	  return (_group != null);
	}
	
	public TouchPoint(int id, Location location, long time) {
		
		
		
	  _id = id; 
		_location = location;
		_time = time;
		_state = TouchState.BIRTH;
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
	
  public void setState(int state) {
    _state = state;
  }

  
	public void setGroup(Group group) {
		_group = group;
		_group.update(this);
	}
	
	
	public void update(Location location, long time, int state) {
		_location = location;
		_state = state;
		_changed = true;
		_time = time;
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
  public boolean isNear(TouchPoint tp) {
    
    
    return (Math.abs(_location.getX() - tp._location.getX()) < 0.005 
        && Math.abs(_location.getY() - tp._location.getY()) < 0.005);
  }

}
