

package org.jmol.modelset;

import java.util.Hashtable;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

public class BoxInfo {

  private final Point3f bbCorner0 = new Point3f(-10, -10, -10);
  private final Point3f bbCorner1 = new Point3f(10, 10, 10);
  private final Point3f bbCenter = new Point3f();
  private final Vector3f bbVector = new Vector3f();
  private final Point3f[] bbVertices = new Point3f[8];
  {
    for (int i = 8; --i >= 0;)
      bbVertices[i] = new Point3f();
  }
  private final static Point3f[] unitBboxPoints = { new Point3f(1, 1, 1),
      new Point3f(1, 1, -1), new Point3f(1, -1, 1), new Point3f(1, -1, -1),
      new Point3f(-1, 1, 1), new Point3f(-1, 1, -1), new Point3f(-1, -1, 1),
      new Point3f(-1, -1, -1), };

  public Point3f getBoundBoxCenter() {
    return bbCenter;
  }

  public Vector3f getBoundBoxCornerVector() {
    return bbVector;
  }

  public Point3f[] getBoundBoxPoints() {
    return new Point3f[] {bbCenter, new Point3f(bbVector), bbCorner0, bbCorner1};
  }

  Point3f[] getBboxVertices() {
    return bbVertices;
  }

  Hashtable getBoundBoxInfo() {
    Hashtable info = new Hashtable();
    info.put("center", new Point3f(bbCenter));
    info.put("vector", new Vector3f(bbVector));
    info.put("corner0", new Point3f(bbCorner0));
    info.put("corner1", new Point3f(bbCorner1));
    return info;
  }

  void setBoundBox(Point3f pt1, Point3f pt2, boolean byCorner) {
    if (pt1.distance(pt2) == 0)
      return;
    if (byCorner) {
      bbCorner0.set(Math.min(pt1.x, pt2.x), Math.min(pt1.y, pt2.y), Math.min(
          pt1.z, pt2.z));
      bbCorner1.set(Math.max(pt1.x, pt2.x), Math.max(pt1.y, pt2.y), Math.max(
          pt1.z, pt2.z));
    } else { 
      bbCorner0.set(pt1.x - pt2.x, pt1.y - pt2.y, pt1.z - pt2.z);
      bbCorner1.set(pt1.x + pt2.x, pt1.y + pt2.y, pt1.z + pt2.z);
    }
    setBbcage();
  }

  void reset() {
    bbCorner0.set(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
    bbCorner1.set(-Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE);
  }
  
  void addBoundBoxPoint(Point3f pt) {
    float t = pt.x;
    if (t < bbCorner0.x)
      bbCorner0.x = t;
    if (t > bbCorner1.x)
      bbCorner1.x = t;
    t = pt.y;
    if (t < bbCorner0.y)
      bbCorner0.y = t;
    if (t > bbCorner1.y)
      bbCorner1.y = t;
    t = pt.z;
    if (t < bbCorner0.z)
      bbCorner0.z = t;
    if (t > bbCorner1.z)
      bbCorner1.z = t;
  }

  void setBbcage() {
    bbCenter.add(bbCorner0, bbCorner1);
    bbCenter.scale(0.5f);
    bbVector.sub(bbCorner1, bbCenter);
    for (int i = 8; --i >= 0;) {
      Point3f pt = bbVertices[i];
      pt.set(unitBboxPoints[i]);
      pt.x *= bbVector.x;
      pt.y *= bbVector.y;
      pt.z *= bbVector.z;
      pt.add(bbCenter);
    }
  }
  
  boolean isWithin(Point3f pt) {
   return (pt.x >= bbCorner0.x && pt.x <= bbCorner1.x 
       && pt.y >= bbCorner0.y && pt.y <= bbCorner1.y
       && pt.z >= bbCorner0.z && pt.z <= bbCorner1.z); 
  }

}
