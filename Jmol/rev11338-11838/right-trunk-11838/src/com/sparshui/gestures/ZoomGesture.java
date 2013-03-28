package com.sparshui.gestures;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import com.sparshui.common.Location;
import com.sparshui.common.messages.events.ZoomEvent;
import com.sparshui.server.TouchPoint;

public class ZoomGesture extends StandardDynamicGesture {

  private float _scale;
  protected Location _offset = null;
  protected Location _offsetCentroid = null;

  
  public String getName() {
    return "ZoomGesture";
  }

  
  public int getGestureType() {
    return GestureType.ZOOM_GESTURE;
  }

  
  protected TouchData createTouchData(TouchPoint touchPoint) {
    return new ZoomData(touchPoint.getLocation());
  }

  
  protected Vector processBirth(TouchData touchData) {
    if (_offset == null) {
      _offset = new Location(0, 0);
      _offsetCentroid = _newCentroid;
    } else {
      adjustOffset();
    }
    calculateScaleChange();
    return null;
  }

  
  protected Vector processDeath(TouchData touchData) {
    if (_knownPoints.size() == 0) {
      _offset = null;
      _offsetCentroid = null;
    } else {
      adjustOffset();
    }
    calculateScaleChange();
    return null;
  }

  
  protected Vector processMove(TouchData touchData) {
    Vector events = new Vector();
    adjustOffset();
    updateOffsetCentroid();
    _scale = calculateScaleChange();
    events.add(new ZoomEvent(_scale, _newCentroid));
    return events;
  }

  private float calculateScaleChange() {
    float retScale = 0;
    Collection touchPoints = _knownPoints.values();
    Iterator touchDataIterator = touchPoints.iterator();
    while (touchDataIterator.hasNext()) {
      TouchData pointData = (TouchData) touchDataIterator.next();
      ZoomData pointZoomData = null;
      
      if (pointData instanceof ZoomData) {
        
        pointZoomData = (ZoomData) pointData;
        float distance = (float) Math.hypot(_newCentroid.getX()
            - pointData.getLocation().getX(), _newCentroid.getY()
            - pointData.getLocation().getY());
        pointZoomData.setDistance(distance);
        
        

        
        
        retScale += pointZoomData.getDistance()
            / pointZoomData.getOldDistance();

      }
    }
    retScale = retScale / _knownPoints.size();
    if (retScale < 5 && retScale > 0.2) {
      
      return retScale;
    }
    return 1;
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

  
  protected class ZoomData extends TouchData {
    private float _distance;
    private float _oldDistance;

    public ZoomData(Location location) {
      super(location);
      _oldDistance = _distance = (float) Math.hypot(location.getX()
          - _newCentroid.getX(), location.getY() - _newCentroid.getY());
      
      
    }

    public float getDistance() {
      return _distance;
    }

    public float getOldDistance() {
      return _oldDistance;
    }

    public void setDistance(float distance) {
      _oldDistance = _distance;
      _distance = distance;
    }
  }

  
}
