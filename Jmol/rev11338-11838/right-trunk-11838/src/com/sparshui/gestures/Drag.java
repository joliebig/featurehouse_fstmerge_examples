package com.sparshui.gestures;

import java.util.Vector;

import com.sparshui.common.messages.events.DragEvent;


public class Drag extends StandardDynamicGesture {
	
	
	public Drag() {
		super();
	}

	
	public String getName() { return "sparshui.gestures.Drag"; }
	
	
	public int getGestureType() {
		return GestureType.DRAG_GESTURE;
	}

	
	protected Vector processBirth(TouchData touchData) {
		return null; 
	}

	
	protected Vector processDeath(TouchData touchData) {
		return null; 
	}

	
	protected Vector processMove(TouchData touchData) {
		Vector events = new Vector();
		float x = _newCentroid.getX() - _oldCentroid.getX();
		float y = _newCentroid.getY() - _oldCentroid.getY();
		if(x != 0 || y != 0) events.add(new DragEvent(x, y));
		return events;
	}

}