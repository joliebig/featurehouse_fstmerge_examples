package com.sparshui.gestures;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import com.sparshui.common.Location;
import com.sparshui.common.messages.events.RotateEvent;
import com.sparshui.server.TouchPoint;


public class RotateGesture extends StandardDynamicGesture {

	
	private float _rotation = 0f;

	
	private Location _offset = null;

	
	protected Location _offsetCentroid = null;

	
	public String getName() {
		return "RotateGesture";
	}
	
	
	public int getGestureType() {
		return GestureType.ROTATE_GESTURE;
	}

	
	
	protected TouchData createTouchData(TouchPoint touchPoint) {
		return new RotateData(touchPoint.getLocation());
	}

	
	protected Vector processBirth(TouchData touchData) {
		if (_offset == null) {
			_offset = new Location(0, 0);
			_offsetCentroid = _newCentroid;
		} else {
			adjustOffset();
		}
		calculateRotation();
		return null;
	}

	
	protected Vector processDeath(TouchData touchData) {
		if (_knownPoints.size() == 0) {
			_offset = null;
			_offsetCentroid = null;
		} else {
			adjustOffset();
		}
		calculateRotation();
		return null;
	}

	
	protected Vector processMove(TouchData touchData) {
		Vector events = new Vector();
		adjustOffset();
		updateOffsetCentroid();
		_rotation = calculateRotation();
		events.add(new RotateEvent(_rotation, _newCentroid));
		
		return events;
	}

	
	protected class RotateData extends TouchData {
		private float _angle;
		private float _oldAngle;

		public RotateData(Location location) {
			super(location);
			_oldAngle = _angle = 0f;
		}

		public float getAngle() {
			return _angle;
		}

		public float getOldAngle() {
			return _oldAngle;
		}

		public void setAngle(float angle) {
			_oldAngle = _angle;
			_angle = angle;
		}
	}

	private float calculateRotation() {
		
		float retRotation = 0;
		Collection touchPoints = _knownPoints.values();
		Iterator touchDataIterator = touchPoints.iterator();
		while (touchDataIterator.hasNext()) {
			TouchData pointData = (TouchData) touchDataIterator.next();
			RotateData pointRotateData = null;
			
			if (pointData instanceof RotateData) {
				
				pointRotateData = (RotateData) pointData;
				
				float angle = (float) Math.atan2(pointRotateData.getLocation()
						.getY()
						- _newCentroid.getY(), pointRotateData.getLocation()
						.getX()
						- _newCentroid.getX());
				pointRotateData.setAngle(angle);

				
				float deltaAngle = pointRotateData.getAngle()
						- pointRotateData.getOldAngle();
				
				
				if (deltaAngle > Math.PI)
					deltaAngle -= 2 * Math.PI;
				else if (deltaAngle < -Math.PI)
					deltaAngle += 2 * Math.PI;
				
				
				
				
				retRotation += deltaAngle;
			}
		}
		retRotation = retRotation / _knownPoints.size();
		return retRotation;

	}

	
	protected void adjustOffset() {
		_offset = new Location(_newCentroid.getX() - _oldCentroid.getX(),
				_newCentroid.getY() - _oldCentroid.getY());
	}

	
	protected void updateOffsetCentroid() {
		float x = _newCentroid.getX() - _offset.getX();
		float y = _newCentroid.getY() - _offset.getY();
		_offsetCentroid = new Location(x, y);
	}

	
}
