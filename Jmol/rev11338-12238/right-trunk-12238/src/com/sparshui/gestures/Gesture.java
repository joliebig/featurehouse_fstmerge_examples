package com.sparshui.gestures;

import java.util.Vector;

import com.sparshui.server.TouchPoint;


public interface Gesture {

	
	public abstract Vector processChange(Vector touchPoints,
			TouchPoint changedTouchPoint);

	
	public abstract String getName();
	
	
	public int getGestureType();
}
