package org.jmol.api;

import java.util.Vector;

import javax.vecmath.Point3f;
import javax.vecmath.Point3i;
import javax.vecmath.Point4f;

public interface TriangleServer {

  
  
  public Point3i[] getCubeVertexOffsets();
  
  public Vector intersectPlane(Point4f plane, Point3f[] vertices, int flags);

}
