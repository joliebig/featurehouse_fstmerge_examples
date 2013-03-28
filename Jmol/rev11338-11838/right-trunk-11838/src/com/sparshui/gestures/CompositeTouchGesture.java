package com.sparshui.gestures;

import java.util.Vector;


import com.sparshui.common.TouchState;

import com.sparshui.common.messages.events.TouchEvent;


public class CompositeTouchGesture extends MultiPointDragGesture {

	
	public CompositeTouchGesture() {
		super();
	}

	
	public String getName() {
		return "CompositeTouchGesture";
	}

	
	protected Vector processBirth(TouchData touchData) {
		Vector events = new Vector();
		super.processBirth(touchData);
		if(_knownPoints.size() == 1) {
			events.add(new TouchEvent(0, _offsetCentroid.getX(), _offsetCentroid.getY(), TouchState.BIRTH));
		}
		return events;
	}

	
	protected Vector processMove(TouchData touchData) {
		Vector events = new Vector();
		updateOffsetCentroid();
		events.add(new TouchEvent(0, _offsetCentroid.getX(), _offsetCentroid.getY(), TouchState.MOVE));
		return events;
	}

	
	protected Vector processDeath(TouchData touchData) {
		Vector events = new Vector();
		if(_knownPoints.size() == 0) {
			events.add(new TouchEvent(0, _offsetCentroid.getX(), _offsetCentroid.getY(), TouchState.DEATH));
		}
		super.processDeath(touchData);
		return events;
	}
	
}
