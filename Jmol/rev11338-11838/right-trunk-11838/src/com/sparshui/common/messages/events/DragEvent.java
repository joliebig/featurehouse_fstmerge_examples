package com.sparshui.common.messages.events;

import com.sparshui.common.Event;
import com.sparshui.common.Location;
import com.sparshui.common.utils.Converter;

public class DragEvent implements Event {
  private static final long serialVersionUID = -2305607021385835330L;
  
  private float _absx;
  private float _absy;
  private byte _nPoints = 1;
  private long _time;

  public DragEvent() {
    _absx = 0;
    _absy = 0;
  }
  
  public DragEvent(float absx, float absy) {
    _absx = absx;
    _absy = absy;
  }

  public DragEvent(Location location, byte nPoints) {
    _absx = location.getX();
    _absy = location.getY();
    _nPoints = nPoints;
  }
  
  
  public DragEvent(byte[] data) {
    if (data.length < 17) {
      
      System.err.println("Error constructing Drag Event.");
      _absx = 0;
      _absy = 0;
    } else {
      _absx = Converter.byteArrayToFloat(data, 0);
      _absy = Converter.byteArrayToFloat(data, 4);
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
  
  public float getAbsX() {
    return _absx;
  }

  public float getAbsY() {
    return _absy;
  }
  
  public void setAbsX(float x) {
    _absx = x;
  }
  
  public void setAbsY(float y) {
    _absy = y;
  }

  
  public int getEventType() {
    return EventType.DRAG_EVENT; 
  }

  
  public String toString() {
    String ret = "Drag Event: absx = " + _absx + ", absy = " + _absy;
    return ret;
  }

  
  public byte[] serialize() {
    byte[] data = new byte[21];
    Converter.intToByteArray(data, 0, getEventType());
    Converter.floatToByteArray(data, 4, _absx);
    Converter.floatToByteArray(data, 8, _absy);
    data[12] = _nPoints;
    Converter.longToByteArray(data, 13, _time);
    return data;
  }

}
