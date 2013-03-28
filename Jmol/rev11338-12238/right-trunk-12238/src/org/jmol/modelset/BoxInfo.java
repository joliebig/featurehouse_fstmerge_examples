

package org.jmol.modelset;

import java.util.Hashtable;
import java.util.Vector;

import javax.vecmath.Point3f;
import javax.vecmath.Point3i;
import javax.vecmath.Point4f;
import javax.vecmath.Vector3f;

import org.jmol.util.Point3fi;
import org.jmol.util.TriangleData;

public class BoxInfo {

 
  private final Point3f bbCorner0 = new Point3f(-10, -10, -10);
  private final Point3f bbCorner1 = new Point3f(10, 10, 10);
  private final Point3f bbCenter = new Point3f();
  private final Vector3f bbVector = new Vector3f(-1, -1, -1);
  private final Point3fi[] bbVertices = new Point3fi[8];
  {
    for (int i = 8; --i >= 0;)
      bbVertices[i] = new Point3fi();
  }

  public static char[] bbcageTickEdges = {
    'z', 0, 0, 'y', 
    'x', 0, 0, 0, 
      0, 0, 0, 0};
  
  public static char[] uccageTickEdges = {
    'z', 'y', 'x', 0, 
     0, 0, 0, 0, 
     0, 0, 0, 0};
  
  public final static byte edges[] = {
      0,1, 0,2, 0,4, 1,3, 
      1,5, 2,3, 2,6, 3,7, 
      4,5, 4,6, 5,7, 6,7
      };

  
  public Vector intersectPlane(Point4f plane, float scale, int flags) {
    Vector v = new Vector();
    v.add(getCanonicalCopy(scale));
    return TriangleData.intersectPlane(plane, v, flags);
  }


  public Point3f[] getCanonicalCopy(float scale) {
    return getCanonicalCopy(bbVertices, scale);
  }

  public final static Point3f[] getCanonicalCopy(Point3f[] bbUcPoints, float scale) {
    Point3f[] pts = new Point3f[8];
    for (int i = 0; i < 8; i++)
      pts[toCanonical[i]] = new Point3f(bbUcPoints[i]);
    scaleBox(pts, scale);
    return pts;
  }
  
  public static void scaleBox(Point3f[] pts, float scale) {
    if (scale == 0 || scale == 1)
      return;
    Point3f center = new Point3f();
    Vector3f v = new Vector3f();
    for (int i = 0; i < 8; i++)
      center.add(pts[i]);
    center.scale(1/8f);
    for (int i = 0; i < 8; i++) {
      v.sub(pts[i], center);
      v.scale(scale);
      pts[i].add(center, v);
    }
  }
  
  public final static Point3f[] unitCubePoints = { 
    new Point3f(0, 0, 0),
    new Point3f(0, 0, 1), 
    new Point3f(0, 1, 0), 
    new Point3f(0, 1, 1),
    new Point3f(1, 0, 0), 
    new Point3f(1, 0, 1), 
    new Point3f(1, 1, 0),
    new Point3f(1, 1, 1), };

  public final static int[] toCanonical = new int[] {0, 3, 4, 7, 1, 2, 5, 6};

  protected final static Point3i[] cubeVertexOffsets = { 
    new Point3i(0, 0, 0), 
    new Point3i(1, 0, 0), 
    new Point3i(1, 0, 1), 
    new Point3i(0, 0, 1), 
    new Point3i(0, 1, 0), 
    new Point3i(1, 1, 0), 
    new Point3i(1, 1, 1), 
    new Point3i(0, 1, 1)  
  };

  private final static Point3f[] unitBboxPoints = new Point3f[8];
  { 
    for (int i = 0; i < 8; i++) {
      unitBboxPoints[i] = new Point3f(-1, -1, -1);
      unitBboxPoints[i].scaleAdd(2, unitCubePoints[i], unitBboxPoints[i]); 
    }
  }

  public Point3f getBoundBoxCenter() {
    return bbCenter;
  }

  public Vector3f getBoundBoxCornerVector() {
    return bbVector;
  }

  public Point3f[] getBoundBoxPoints() {
    return new Point3f[] {bbCenter, new Point3f(bbVector), bbCorner0, bbCorner1};
  }

  public Point3fi[] getBboxVertices() {
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

  void setBoundBox(Point3f pt1, Point3f pt2, boolean byCorner, float scale) {
    if (pt1 != null) {
      if (pt1.distance(pt2) == 0 || scale == 0)
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
    }
    setBbcage(scale);
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

  void setBbcage(float scale) {
    bbCenter.add(bbCorner0, bbCorner1);
    bbCenter.scale(0.5f);
    bbVector.sub(bbCorner1, bbCenter);
    if (scale > 0) {
      bbVector.scale(scale);
    } else {
      bbVector.x -= scale / 2;
      bbVector.y -= scale / 2;
      bbVector.z -= scale / 2;
    }
    for (int i = 8; --i >= 0;) {
      Point3f pt = bbVertices[i];
      pt.set(unitBboxPoints[i]);
      pt.x *= bbVector.x;
      pt.y *= bbVector.y;
      pt.z *= bbVector.z;
      pt.add(bbCenter);
    }
    bbCorner0.set(bbVertices[0]);
    bbCorner1.set(bbVertices[7]);
  }
  
  boolean isWithin(Point3f pt) {
   return (pt.x >= bbCorner0.x && pt.x <= bbCorner1.x 
       && pt.y >= bbCorner0.y && pt.y <= bbCorner1.y
       && pt.z >= bbCorner0.z && pt.z <= bbCorner1.z); 
  }

}
