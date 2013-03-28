package org.jmol.jvxl.api;

import javax.vecmath.Point3f;
import javax.vecmath.Point3i;
import javax.vecmath.Vector3f;

public interface VertexDataServer {
  
  
  
  
  
  public abstract int getSurfacePointIndexAndFraction(float cutoff,
                                           boolean isCutoffAbsolute, int x,
                                           int y, int z, Point3i offset,
                                           int vertexA, int vertexB, 
                                           float valueA, float valueB,
                                           Point3f pointA, Vector3f edgeVector,
                                           boolean isContourType, float[] fReturn);

  
  public abstract int addVertexCopy(Point3f vertexXYZ, float value, int assocVertex);

  
  public abstract void addTriangleCheck(int iA, int iB, int iC, int check,
                                        boolean isAbsolute, int color);
  
  
  public abstract float getValue(int x, int y, int z);
  
}
