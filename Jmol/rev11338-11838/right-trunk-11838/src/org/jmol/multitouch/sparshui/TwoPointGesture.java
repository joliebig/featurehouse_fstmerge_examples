package org.jmol.multitouch.sparshui;

import java.util.Vector;

import javax.vecmath.Vector3f;

import org.jmol.util.Logger;
import org.jmol.viewer.ActionManagerMT;

import com.sparshui.common.Event;
import com.sparshui.common.Location;
import com.sparshui.common.messages.events.DragEvent;
import com.sparshui.common.messages.events.RotateEvent;
import com.sparshui.common.messages.events.ZoomEvent;
import com.sparshui.gestures.StandardDynamicGesture;


public class TwoPointGesture extends StandardDynamicGesture {

  

  private int _myType = ActionManagerMT.INVALID_GESTURE;

  protected Location _offset = null;

  protected Location _offsetCentroid = null;
  private Vector _traces1 = new Vector();
  private Vector _traces2 = new Vector();
  private int _nTraces = 0;
  private int _nCurrent = 0;
  private float _scale;
  private float _rotation;
  private float _distance0;
  private Vector3f _v00;


  
  public String getName() {
    return "TwoPointGesture";
  }

  
  public int getGestureType() {
    return _myType;
  }

  
  protected Vector processBirth(TouchData touchData) {
    _nCurrent++;
    Location location = touchData.getLocation();
    switch (_nTraces) {
    case 0:
      _traces1 = new Vector();
      _traces1.add(Location.pixelLocation(location));
      break;
    case 1:
      _traces2 = new Vector();
      _traces2.add(Location.pixelLocation(location));
      break;
    default:
      _myType = ActionManagerMT.INVALID_GESTURE;
    }
    _nTraces++;
    return null;
  }

  
  protected Vector processMove(TouchData touchData) {
    Vector events = new Vector();
    if (_nTraces != 2)
      return events;
    Location location = Location.pixelLocation(touchData.getLocation());
    updateLocations(location);
    if (_nCurrent < 2)
      return events;
    if (_myType == ActionManagerMT.INVALID_GESTURE)
      checkType();
    if (_myType != ActionManagerMT.INVALID_GESTURE && updateCentroid())
      location = Location.screenLocation(_offsetCentroid);
    Event event = null;
    switch (_myType) {
    case ActionManagerMT.INVALID_GESTURE:
    default:
      break;
    case ActionManagerMT.ZOOM_GESTURE:
      event = new ZoomEvent(_scale, location);
      break;
    case ActionManagerMT.ROTATE_GESTURE:
      event = new RotateEvent(_rotation, location);
      break;
    case ActionManagerMT.MULTI_POINT_DRAG_GESTURE:
      event = new DragEvent(location, (byte) 2);
      break;
    }
    if (event != null)
      events.add(event);
    return events;
  }

  
  protected Vector processDeath(TouchData touchData) {
    _nCurrent--;
    if (_nCurrent <= 0) {
      _nTraces = _nCurrent = 0;
      _traces1 = null;
      _traces2 = null;
      _v00 = null;
      _myType = ActionManagerMT.INVALID_GESTURE;
    }
    return null;
  }

  private void updateLocations(Location location) {
    boolean id1 = (_nCurrent < 2
        || location.distance2((Location) _traces1.lastElement()) <= location
           .distance2((Location) _traces2.lastElement()));
    if (id1) {
      _traces1.add(location);
    } else {
      _traces2.add(location);
    }
    if (_nCurrent == 2) {
      
      
      if (_v00 == null) {
        Location l1 = (Location) _traces1.firstElement();
        Location l2 = (Location) _traces2.firstElement();
        _v00 = new Vector3f(l2.getX() - l1.getX(), l2.getY() - l1.getY(), 0);
        _distance0 = _v00.length();
        _v00.normalize();
      }
    }
  }

  private void checkType() {
    if (_traces1.size() < 10 || _traces2.size() < 10)
      return;
    Location loc10 = (Location) _traces1.firstElement();
    Location loc20 = (Location) _traces2.firstElement();
    Location loc11 = (Location) _traces1.lastElement();
    Location loc21 = (Location) _traces2.lastElement();
    Vector3f v1 = new Vector3f(loc11.getX() - loc10.getX(), loc11.getY()
        - loc10.getY(), 0);
    Vector3f v2 = new Vector3f(loc21.getX() - loc20.getX(), loc21.getY()
        - loc20.getY(), 0);
    float d1 = v1.length();
    float d2 = v2.length();
    v1.normalize();
    v2.normalize();
    float cos01 = Math.abs(_v00.dot(v1));
    float cos02 = Math.abs(_v00.dot(v2));
    float cos12 = v1.dot(v2);
    if (d1 < 2 || d2 < 2 || Math.abs(cos12) > 0.90) {
      if (d1 >= 2 && d2 >= 2 && cos12 > 0.9) {
        
        _myType = ActionManagerMT.MULTI_POINT_DRAG_GESTURE;
      } else if (d1 < 2 && cos02 < 0.80 || d2 < 2 && cos01 < 0.80 || cos01 < 0.80
          && cos02 < 0.80) {
        
        _myType = ActionManagerMT.ROTATE_GESTURE;
      } else if (d1 < 2 && cos02 > 0.9 || d2 < 2 && cos01 > 0.9 || cos01 > 0.9
          && cos02 > 0.9) {
        
        _myType = ActionManagerMT.ZOOM_GESTURE;
      }
    }
    if (Logger.debugging)
      Logger.debug("TwoPointGesture type=" + _myType + " cos:" + cos01 + "/" + cos02 + "/" + cos12);
  }

  private boolean updateCentroid() {
    Location loc10 = (Location) _traces1.firstElement();
    Location loc20 = (Location) _traces2.firstElement();
    Location loc11 = (Location) _traces1.lastElement();
    Location loc21 = (Location) _traces2.lastElement();
    float d1 = loc11.distance(loc10);
    float d2 = loc21.distance(loc20);
    switch (_myType) {
    case ActionManagerMT.ROTATE_GESTURE:
      _offsetCentroid = new Location((loc10.getX() + loc20.getX()) / 2, 
          (loc10.getY() + loc20.getY()) / 2);
      Vector3f v1;
      Vector3f v2;
      if (d2 < 2) {
        loc10 = (Location) _traces1.get(_traces1.size() - 2);
        v1 = loc20.directionTo(loc10);
        v2 = loc20.directionTo(loc11);
      } else {
        loc20 = (Location) _traces2.get(_traces2.size() - 2);
        v1 = loc10.directionTo(loc20);
        v2 = loc10.directionTo(loc21);
      }
      v1.cross(v1, v2);
      _rotation = (v1.z < 0 ? 1 : -1);
      return true;
    case ActionManagerMT.ZOOM_GESTURE:
      float w1 = d2 / (d1 + d2);
      float w2 = 1 - w1;
      _offsetCentroid = new Location(loc10.getX() * w1 + loc20.getX() * w2,
          loc10.getY() * w1 + loc20.getY() * w2);
      d1 = loc21.distance(loc11);
      _scale = (d1 < _distance0 ? -1 : 1);
      return true;
    case ActionManagerMT.MULTI_POINT_DRAG_GESTURE:
      _offsetCentroid = new Location((loc11.getX() + loc21.getX()) / 2, (loc11
          .getY() + loc21.getY()) / 2);
      return true;
    }
    return false;
  }
  
}
