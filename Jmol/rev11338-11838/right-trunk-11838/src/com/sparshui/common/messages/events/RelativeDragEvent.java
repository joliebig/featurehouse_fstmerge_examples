package com.sparshui.common.messages.events;

import com.sparshui.common.Event;
import com.sparshui.common.utils.Converter;

public class RelativeDragEvent implements Event 
{
	private float _changeInX;
	private float _changeInY;
	
	
	private static final long serialVersionUID = 7743528698376037703L;

	
	public RelativeDragEvent()
	{
		_changeInX = 0;
		_changeInY = 0;
	}
	
	public RelativeDragEvent(float changeInX, float changeInY) 
	{
		_changeInX = changeInX;
		_changeInY = changeInY;
	}
	
	public float getChangeInX()
	{
		return _changeInX;
	}
	
	public float getChangeInY()
	{
		return _changeInY;
	}
	
	
  public RelativeDragEvent(byte[] data) 
  {
    if (data.length < 8) 
    {
      
      System.err.println("Error constructing Drag Event.");
      _changeInX = 0;
      _changeInY = 0;
    } else 
    {
      _changeInX = Converter.byteArrayToFloat(data, 0);
      _changeInY = Converter.byteArrayToFloat(data, 4);
    }
  }

	
	public int getEventType() 
	{
		return EventType.RELATIVE_DRAG_EVENT;
	}

	
	public byte[] serialize() 
	{
		byte[] data = new byte[12];
    Converter.intToByteArray(data, 0, getEventType());
    Converter.floatToByteArray(data, 4, _changeInX);
    Converter.floatToByteArray(data, 8, _changeInY);
		return data;
	}

}
