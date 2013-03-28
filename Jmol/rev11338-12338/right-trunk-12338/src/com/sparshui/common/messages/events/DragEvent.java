package com.sparshui.common.messages.events;

import com.sparshui.common.Event;
import com.sparshui.common.utils.Converter;

public class DragEvent implements Event {
  private static final long serialVersionUID = -2305607021385835330L;
  
  private float _dx;
  private float _dy;
  private byte _nPoints = 1;
  private long _time;

  public DragEvent() {
    _dx = 0;
    _dy = 0;
  }
  
  public DragEvent(float dx, float dy, int nPoints, long time) {
    _dx = dx;
    _dy = dy;
    _nPoints = (byte) nPoints;
    _time = time;
  }

  
  public DragEvent(byte[] data) {
    if (data.length < 17) {
      
      System.err.println("Error constructing Drag Event.");
      _dx = 0;
      _dy = 0;
    } else {
      _dx = Converter.byteArrayToFloat(data, 0);
      _dy = Converter.byteArrayToFloat(data, 4);
      _nPoints = data[8];
      _time = Converter.byteArrayToLong(data, 9);
    }
  }

  public long getTime() {
    return _time;
  }
  
  public int getNPoints() {
    return _nPoints;
  }
  
  public float getDx() {
    return _dx;
  }

  public float getDy() {
    return _dy;
  }
  
  public void setDx(float dx) {
    _dx = dx;
  }
  
  public void setDy(float dy) {
    _dy = dy;
  }

  
  public int getEventType() {
    return EventType.DRAG_EVENT; 
  }

  
  public String toString() {
    String ret = "Drag Event: dx = " + _dx + ", dy = " + _dy;
    return ret;
  }

  
  public byte[] serialize() {
    byte[] data = new byte[21];
    Converter.intToByteArray(data, 0, getEventType());
    Converter.floatToByteArray(data, 4, _dx);
    Converter.floatToByteArray(data, 8, _dy);
    data[12] = _nPoints;
    Converter.longToByteArray(data, 13, _time);
    return data;
  }

}
