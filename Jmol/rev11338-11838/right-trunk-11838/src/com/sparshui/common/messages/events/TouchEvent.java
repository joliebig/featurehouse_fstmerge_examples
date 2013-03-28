package com.sparshui.common.messages.events;

import com.sparshui.server.TouchPoint;

import com.sparshui.common.Event;
import com.sparshui.common.TouchState;
import com.sparshui.common.utils.Converter;


public class TouchEvent implements Event {
	private static final long serialVersionUID = 370824346017492361L;
	
	private int _id;
	private float _x;
	private float _y;
	private int _state;
  private long _time;

	public TouchEvent() {
		_id = 0;
		_x = 0;
		_y = 0;
		_state = TouchState.BIRTH;
	}
	
	
	public TouchEvent(int id, float x, float y, int state) {
		_id = id;
		_x = x;
		_y = y;
		_state = state;
		_time = System.currentTimeMillis();
	}
	
	 public TouchEvent(TouchPoint tp) {
	    _id = tp.getID();
	    _x = tp.getLocation().getX();
	    _y = tp.getLocation().getY();
	    _state = tp.getState();
	    _time = System.currentTimeMillis();
	  }

	 public int getTouchID() {
		return _id;
	}
	
  public long getTime() {
    return _time;
  }
  
	public float getX() {
		return _x;
	}
	
	public float getY() {
		return _y;
	}
	
	public void setX(float x) {
		_x = x;
	}
	
	public void setY(float y) {
		_y = y;
	}
	
	public int getState() {
		return _state;
	} 

  
  public int getEventType() {
    return EventType.TOUCH_EVENT;
  }

	
  public TouchEvent(byte[] data) {
    if (data.length < 24) {
      System.err.println("An error occurred while deserializing a TouchEvent.");
    } else {
      _id = Converter.byteArrayToInt(data, 0);
      _x = Converter.byteArrayToFloat(data, 4);
      _y = Converter.byteArrayToFloat(data, 8);
      _state = Converter.byteArrayToInt(data, 12);
      _time = Converter.byteArrayToLong(data, 16);
    }
  }

	
  public byte[] serialize() {
    byte[] data = new byte[28];
    Converter.intToByteArray(data, 0, getEventType());
    Converter.intToByteArray(data, 4, _id);
    Converter.floatToByteArray(data, 8, _x);
    Converter.floatToByteArray(data, 12, _y);
    Converter.intToByteArray(data, 16, _state);
    Converter.longToByteArray(data, 20, _time);
    return data;
  }
  
	
	public String toString() {
		return ("Touch Event: ID: " + _id + ", X: " + _x + ", Y: " + _y + "State: " + _state);
	}
}
