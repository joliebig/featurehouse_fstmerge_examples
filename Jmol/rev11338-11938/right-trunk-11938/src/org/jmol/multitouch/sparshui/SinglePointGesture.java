package org.jmol.multitouch.sparshui;

import java.util.Vector;

import org.jmol.util.Logger;

import com.sparshui.common.TouchState;
import com.sparshui.common.messages.events.TouchEvent;
import com.sparshui.gestures.Gesture;
import com.sparshui.gestures.GestureType;
import com.sparshui.server.TouchPoint;


public class SinglePointGesture implements Gesture {

  private static final long MAXIMUM_CLICK_TIME = 200;
  private int _nCurrent, _nMoves, _myId;
  private TouchPoint _birth;

  
  public String getName() {
    return "SinglePointGesture";
  }

  
  public int getGestureType() {
    return GestureType.TOUCH_GESTURE;
  }
  
  
  
  public Vector processChange(Vector touchPoints, TouchPoint changedTouchPoint) {
    Vector retEvents = new Vector();
    int nPoints = touchPoints.size();
    if (Logger.debugging) {
      Logger.info("\nSinglePointGesture " + _myId + " nPoints: " + nPoints);
    }
    
    
    
    if (nPoints > 1) {
      if (_myId != Integer.MAX_VALUE) {
        _myId = Integer.MAX_VALUE;
        _nMoves = 1000;
        _nCurrent = 0;
        
        retEvents.add(new TouchEvent());
      }
      return retEvents;
    }
    int id = changedTouchPoint.getID();
    if (Logger.debugging)
      Logger.info("\nSinglePointGesture id=" + id + " state="
          + changedTouchPoint.getState() + " ncurrent=" + _nCurrent
          + " nMoves=" + _nMoves);
    switch (changedTouchPoint.getState()) {
    case TouchState.BIRTH:
      _myId = id;
      _birth = new TouchPoint(changedTouchPoint);
      _nCurrent = 1;
      _nMoves = 0;
      break;
    case TouchState.MOVE:
      if (id != _myId)
        return retEvents;
      switch (++_nMoves) {
      case 2:
        if (checkClick(changedTouchPoint, retEvents, false))
          return retEvents;
        break;
      }
      break;
    case TouchState.DEATH:
      if (id != _myId)
        return retEvents;
      _nCurrent = 0;
      if (_nMoves < 2 && checkClick(changedTouchPoint, retEvents, true))
        return retEvents;
      break;
    }
    retEvents.add(new TouchEvent(changedTouchPoint));
    return retEvents;
  }

  private boolean checkClick(TouchPoint tpNew, Vector retEvents, boolean isDeath) {
    TouchPoint tp;
    long dt = tpNew.getTime() - _birth.getTime();
    boolean isSingleClick = (isDeath && dt < MAXIMUM_CLICK_TIME);
    if (dt < 500 && !isSingleClick)
      return false;
    _nMoves += 2;
    
    tp = new TouchPoint(_birth);
    tp.setState(TouchState.DEATH);
    retEvents.add(new TouchEvent(tp));
    tp.setState(TouchState.CLICK);
    retEvents.add(new TouchEvent(tp));
    if (isSingleClick)
      return true;
    tp.setState(TouchState.BIRTH);
    retEvents.add(new TouchEvent(tp));
    if (!isDeath)
      return true;
    tp.setState(TouchState.DEATH);
    retEvents.add(new TouchEvent(tp));
    tp.setState(TouchState.CLICK);
    retEvents.add(new TouchEvent(tp));
    return true;
  }
}
