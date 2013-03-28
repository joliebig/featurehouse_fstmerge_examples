package com.sparshui.gestures;

import java.util.Vector;

import com.sparshui.common.Location;
import com.sparshui.common.messages.events.RelativeDragEvent;

public class RelativeDragGesture extends StandardDynamicGesture
{
	Location _startLocation;
	
	
	public int getGestureType() 
	{
		return GestureType.RELATIVE_DRAG_GESTURE;
	}

	
	public String getName() 
	{
		return new String("sparshui.gestures.RelativeDragGesture");
	}

	
	protected Vector processBirth(TouchData touchData) 
	{
		System.out.println("STARTING " + touchData.getLocation());
		_startLocation = touchData.getLocation();
		return null;
	}

	
	protected Vector processDeath(TouchData touchData) 
	{
		return null;
	}

	
	protected Vector processMove(TouchData touchData) 
	{
		Vector events = new Vector();
		float endX = _newCentroid.getX();
		float endY = _newCentroid.getY();
		float startX = _startLocation.getX();
		float startY = _startLocation.getY();	
		
		
		
		
		events.add(new RelativeDragEvent(endX - startX, endY - startY));
		return events;
	}

}
