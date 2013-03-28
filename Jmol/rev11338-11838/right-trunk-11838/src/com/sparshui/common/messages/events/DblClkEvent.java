package com.sparshui.common.messages.events;

import com.sparshui.common.Event;
import com.sparshui.common.utils.Converter;


public class DblClkEvent implements Event {
	private static final long serialVersionUID = -1643892133742179717L;
	
  
  public int getEventType() {
    return EventType.DBLCLK_EVENT;
  }

	private int _id;
	private float _x;
	private float _y;
  private long _time;
  private int _state;

	
	public DblClkEvent() {
		_id = 0;
		_x = 0;
		_y = 0;
	}
	
	
	
  public DblClkEvent(int id, float x, float y, int state) {
    _id = id;
    _x = x;
    _y = y;
    _state = state;
    _time = System.currentTimeMillis();
  }
  	
	public int getTouchID() {
		return _id;
	}
	
	public int getState() {
	  return _state;
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
	
  
  public DblClkEvent(byte[] data) {
    if (data.length < 24) {
      System.err.println("An error occurred while deserializing a DblClkEvent.");
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
}
