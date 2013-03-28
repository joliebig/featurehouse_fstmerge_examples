package com.sparshui.gestures;

import java.util.Vector;

import com.sparshui.common.Location;
import com.sparshui.common.messages.events.DragEvent;


public class MultiPointDragGesture extends StandardDynamicGesture {

	
	protected Location _offset = null;

	protected Location _offsetCentroid = null;
	
	
	public MultiPointDragGesture() {
		super();
	}

	
	public String getName() {
		return "DragGesture";
	}
	
	
	public int getGestureType() {
		return GestureType.MULTI_POINT_DRAG_GESTURE;
	}

	
	protected Vector processBirth(TouchData touchData) {
		if(_offset == null) {
			_offset = new Location(0,0);
			_offsetCentroid = _newCentroid;
		} else {
			adjustOffset();
		}
		return null;
	}

	
	protected Vector processMove(TouchData touchData) {
		Vector events = new Vector();
		updateOffsetCentroid();
		
		events.add(new DragEvent(_offsetCentroid.getX(), _offsetCentroid.getY()));
		return events;
	}

	
	protected Vector processDeath(TouchData touchData) {
		if(_knownPoints.size() == 0) {
			_offset = null;
			_offsetCentroid = null;
		} else {
			adjustOffset();
		}
		return null;
	}
	
	
	protected void adjustOffset() {
		_offset = new Location(
				_newCentroid.getX() - _oldCentroid.getX() + _offset.getX(),
				_newCentroid.getY() - _oldCentroid.getY() + _offset.getY()
		);
	}
	
	
	protected void updateOffsetCentroid() {
		float x = _newCentroid.getX() - _offset.getX();
		float y = _newCentroid.getY() - _offset.getY();
		_offsetCentroid = new Location(x, y);
	}

}