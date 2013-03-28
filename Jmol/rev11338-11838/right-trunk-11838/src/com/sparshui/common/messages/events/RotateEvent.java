package com.sparshui.common.messages.events;

import com.sparshui.common.Event;
import com.sparshui.common.Location;
import com.sparshui.common.utils.Converter;

public class RotateEvent implements Event {
	private static final long serialVersionUID = -5467788080845086125L;
	
	
	private float _rotation;
	private Location _center;
	
	public RotateEvent() {
		_rotation = 0;
		_center = new Location();
	}
	
	public RotateEvent(float rotation, Location center) {
		_rotation = rotation;
		_center = center;
	}
	
	
  public RotateEvent(byte[] data) {
    if (data.length < 12) {
      
      System.err.println("Error constructing Rotate Event.");
      _rotation = 0;
      _center = new Location(0, 0);
    } else {
      _rotation = Converter.byteArrayToFloat(data, 0);
      _center = new Location(Converter.byteArrayToFloat(data, 4),
          Converter.byteArrayToFloat(data, 8));
    }
  }
	

	
	public int getEventType() {
		return EventType.ROTATE_EVENT; 
	}
	
	
	public byte[] serialize() {
		byte[] data = new byte[16];
    Converter.intToByteArray(data, 0, getEventType());
    Converter.floatToByteArray(data, 4, _rotation);
    Converter.floatToByteArray(data, 8, _center.getX());
    Converter.floatToByteArray(data, 12, _center.getY());
		return data;
	}

	
	public String toString() {
		return ("Rotate Event - Rotation: " + _rotation + ", Center: " + _center.toString());
	}
	
	public float getRotation() {
		return _rotation;
	}
	
	public Location getCenter() {
		return _center;
	}
	
	public void setCenter(Location center) {
		_center = center;
	}

}
