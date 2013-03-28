package com.sparshui.common;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.Serializable;

import javax.vecmath.Vector3f;


public class Location implements Serializable {
	
	
	private static final long serialVersionUID = -3472243250219991476L;
	private float _x;
	private float _y;

	
	public Location() {
		_x = 0;
		_y = 0;
	}
	
	
	public Location(float x, float y) {
		_x = x;
		_y = y;
	}
	
	
	public float getX() {
		return _x;
	}
	
	
	public float getY() {
		return _y;
	}
	
	public String toString() {
		return "x = " + _x + ", y = " + _y 
		        + (_x < 1 && _x > 0 ? "(" 
		        + pixelLocation(this).getX() + " " + pixelLocation(this).getY() + ")"
		    : "");
	}
	

  public float getDistance(Location location) {
    float dx, dy;
    return (float) Math.sqrt((dx = _x - location._x) * dx + (dy = _y - location._y) * dy);
  }

  public Vector3f getVector(Location location) {
    return new Vector3f(location._x - _x, location._y - _y, 0);  
  }
  
  public static Location getCenter(Location a, Location b) {
    return getCentroid(a, b, 0.5f);
  }

  
  public static Location getCentroid(Location a, Location b, float w) {
    float w1 = 1 - w;
    return new Location(a._x * w1 + b._x * w, a._y * w1 + b._y * w);
  }

  static final Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();

  public static Location pixelLocation(Location location) {
    return location == null ? null : new Location(location.getX()
        * screenDim.width, location.getY() * screenDim.height);
  }

  public static Location screenLocation(Location location) {
    return (location == null ? null : new Location(location.getX()
        / screenDim.width, location.getY() / screenDim.height));
  }

}
