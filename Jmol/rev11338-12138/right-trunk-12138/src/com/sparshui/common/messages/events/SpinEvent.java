package com.sparshui.common.messages.events;

import com.sparshui.common.Event;
import com.sparshui.common.utils.Converter;

public class SpinEvent implements Event {
	private static final long serialVersionUID = 6394319277586792988L;
	
	private float _rotationX;
	private float _rotationY;
	private float _rotationZ;
	
	
	public int getEventType() {
		return EventType.SPIN_EVENT;
	}
	
	public SpinEvent(){	
		_rotationX = (float) 0.0;
		_rotationY = (float) 0.0;
		_rotationZ = (float) 0.0;
	}
	
	public SpinEvent(float rotationX, float rotationY, float rotationZ){
		_rotationX = rotationX;
		_rotationY = rotationY;
		_rotationZ = rotationZ;
	}
	
	public float getRotationX(){
		return _rotationX;
	}
	public float getRotationY(){
		return _rotationY;
	}
	public float getRotationZ(){
		return _rotationZ;
	}

	public void setRotationX(float rotation){
		_rotationX = rotation;
	}
	public void setRotationY(float rotation){
		_rotationY = rotation;
	}
	public void setRotationZ(float rotation){
		_rotationZ = rotation;
	}

  
  public SpinEvent(byte[] data) {
    if (data.length < 12) {
      System.err.println("An error occurred while deserializing a TouchEvent.");
    } else {
      _rotationX = Converter.byteArrayToFloat(data, 0);
      _rotationY = Converter.byteArrayToFloat(data, 4);
      _rotationY = Converter.byteArrayToFloat(data, 8);
    }
  }

  
  
  public byte[] serialize() {
    byte[] data = new byte[16];
    Converter.intToByteArray(data, 0, getEventType());
    Converter.floatToByteArray(data, 4, _rotationX);
    Converter.floatToByteArray(data, 8, _rotationY);
    Converter.floatToByteArray(data, 12, _rotationZ);
    return data;
  }

  
  public String toString() {
    return ("Spin Event - rotationX: " + _rotationX + ", rotationY: " + _rotationY + ", rotationZ: " + _rotationZ);
  }
}
