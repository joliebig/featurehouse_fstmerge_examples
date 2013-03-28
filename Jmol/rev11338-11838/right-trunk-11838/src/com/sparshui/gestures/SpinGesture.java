package com.sparshui.gestures;

import java.util.Iterator;
import java.util.Vector;

import com.sparshui.common.Event;
import com.sparshui.common.TouchState;
import com.sparshui.common.messages.events.*;
import com.sparshui.server.TouchPoint;

public class SpinGesture extends StandardDynamicGesture {
	private class AXIS {
		protected final static int XAXIS = 0;
		protected final static int YAXIS = 1;
		protected final static int ZAXIS = 2;
	}

	private final double EPSSPIN = 10.0f;

	private Vector _axisPoints;
	MultiPointDragGesture _multiPointDragGesture;
	int _fixedAxis;

	public SpinGesture() {
		_axisPoints = new Vector();
		_fixedAxis = AXIS.ZAXIS; 
		_multiPointDragGesture = new MultiPointDragGesture();
	}

	
	public String getName() {
		return "SpinGesture";
	}
	
	
	public int getGestureType() {
		return GestureType.SPIN_GESTURE;
	}

	
	protected Vector processBirth(TouchData touchData) {
		
		return null;
	}

	
	protected Vector processDeath(TouchData touchData) {
		
		return null;
	}

	
	protected Vector processMove(TouchData touchData) {
		
		return null;
	}

	protected void handleAxisPointBirth(TouchPoint touchPoint) {
		float dx, dy;
		switch (_axisPoints.size()) {
		case 1:
		  TouchPoint tp = (TouchPoint) _axisPoints.elementAt(0);
			
			dx = Math.abs(touchPoint.getLocation().getX()
					- tp.getLocation().getX());
			dy = Math.abs(touchPoint.getLocation().getY()
					- tp.getLocation().getY());
			if (Math.abs(dx) > Math.abs(dy)) {
				_fixedAxis = AXIS.YAXIS;
			} else {
				_fixedAxis = AXIS.XAXIS;
			}
			
		case 0:
			_axisPoints.add(touchPoint);
		default:
			
		}
	}

	protected boolean isAxisPoint(TouchPoint touchPoint) {
		Iterator touchPointIterator = _axisPoints.iterator();
		while (touchPointIterator.hasNext()) {
			if (((TouchPoint) touchPointIterator.next()).getID() == touchPoint.getID()) {
				return true;
			}
		}
		return false;
	}

	public Vector processChange(Vector touchPoints, TouchPoint touchPoint) {
    
    if (_axisPoints.size() < 2 || isAxisPoint(touchPoint)) {
      switch (touchPoint.getState()) {
      case TouchState.BIRTH:
        handleAxisPointBirth(touchPoint);
        break;
      case TouchState.MOVE:
        
        break;
      case TouchState.DEATH:
        
        
      }
      return new Vector();
    }
    
    Vector ev = _multiPointDragGesture.processChange(touchPoints, touchPoint);
    int i = 0;

    
    for (i = 0; i < ev.size(); ++i) {
      if (ev.elementAt(i) instanceof DragEvent) {
        ev.insertElementAt(dragToSpin((Event) ev.elementAt(i)), i);
        ev.remove(i + 1);
      }
    }
    return ev;
  }

	protected Event dragToSpin(Event event) {
    float xTheta, yTheta;
    if (event instanceof DragEvent) {

      switch (_fixedAxis) {
      case AXIS.XAXIS:
        yTheta = (float) (((DragEvent) event).getAbsY() * EPSSPIN);
        return new SpinEvent((float) 0, yTheta, (float) 0);
      case AXIS.YAXIS:
        xTheta = (float) (((DragEvent) event).getAbsX() * EPSSPIN);
        return new SpinEvent(xTheta, 0, 0);
      default:
        return new SpinEvent(0, 0, 0);
      }
    }
    return null;

  }

}
