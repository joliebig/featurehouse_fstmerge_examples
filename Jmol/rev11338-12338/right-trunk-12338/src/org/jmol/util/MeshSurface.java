package org.jmol.util;

import java.util.BitSet;
import java.util.Vector;

import javax.vecmath.Point3f;
import javax.vecmath.Point4f;
import javax.vecmath.Vector3f;

import org.jmol.g3d.Graphics3D;

public class MeshSurface {

  protected static final int SEED_COUNT = 25;
  public boolean haveQuads;
  public short colix;
  public boolean isColorSolid = true;
  public int vertexCount;
  public Point3f[] vertices;
  public Point3f[] offsetVertices;
  public short[] vertexColixes;
  public int polygonCount;
  public int[][] polygonIndexes;
  public short[] polygonColixes;
  public Vector3f[] vertexNormals;
  public BitSet bsFaces;
  public Point3f ptOffset;
  public float scale3d;

  public MeshSurface() {
  }

  public void setColix(short colix) {
    this.colix = colix;
  }

  public int addVertexCopy(Point3f vertex) { 
    if (vertexCount == 0)
      vertices = new Point3f[SEED_COUNT];
    else if (vertexCount == vertices.length)
      vertices = (Point3f[]) ArrayUtil.doubleLength(vertices);
    vertices[vertexCount] = new Point3f(vertex);
    
    return vertexCount++;
  }

  public void addTriangle(int vertexA, int vertexB, int vertexC) {
    addPolygon(new int[] { vertexA, vertexB, vertexC });
  }

  public void addQuad(int vertexA, int vertexB, int vertexC, int vertexD) {
    haveQuads = true;
    addPolygon(new int[] { vertexA, vertexB, vertexC, vertexD });
  }

  protected int addPolygon(int[] polygon) {
    int n = polygonCount;
    if (polygonCount == 0)
      polygonIndexes = new int[SEED_COUNT][];
    else if (polygonCount == polygonIndexes.length)
      polygonIndexes = (int[][]) ArrayUtil.doubleLength(polygonIndexes);
    polygonIndexes[polygonCount++] = polygon;
    return n;
  }

  public void setPolygonCount(int polygonCount) {    
    this.polygonCount = polygonCount;
    if (polygonCount < 0)
      return;
    if (polygonIndexes == null || polygonCount > polygonIndexes.length)
      polygonIndexes = new int[polygonCount][];
  }

  public Point3f[] dots;
  public float[] vertexValues;
  public BitSet[] surfaceSet;
  public int[] vertexSets;
  public int nSets = 0;
  
  public int addVertexCopy(Point3f vertex, float value) {
    if (vertexCount == 0)
      vertexValues = new float[SEED_COUNT];
    else if (vertexCount >= vertexValues.length)
      vertexValues = (float[]) ArrayUtil.doubleLength(vertexValues);
    vertexValues[vertexCount] = value;
    return addVertexCopy(vertex);
  }

  public int addTriangleCheck(int vertexA, int vertexB, int vertexC, int check,
                              int check2, int color) {
    return (vertices == null
        || vertexValues != null
        && (Float.isNaN(vertexValues[vertexA])
            || Float.isNaN(vertexValues[vertexB]) 
            || Float.isNaN(vertexValues[vertexC])) 
        || Float.isNaN(vertices[vertexA].x)
        || Float.isNaN(vertices[vertexB].x) 
        || Float.isNaN(vertices[vertexC].x) 
        ? -1 
      : addPolygon(new int[] { vertexA, vertexB, vertexC, check, check2 },
        color));
  }

  private int lastColor;
  private short lastColix;
    
  private int addPolygon(int[] polygon, int color) {
    if (color != 0) {
      if (polygonColixes == null || polygonCount == 0)
        lastColor = 0;
      short colix = (color == lastColor ? lastColix : (lastColix = Graphics3D
          .getColix(lastColor = color)));
      setPolygonColix(polygonCount, colix);
    }
    return addPolygon(polygon);
  }

  private void setPolygonColix(int index, short colix) {
    if (polygonColixes == null) {
      polygonColixes = new short[SEED_COUNT];
    } else if (index == polygonColixes.length) {
      polygonColixes = (short[]) ArrayUtil.doubleLength(polygonColixes);
    }
    polygonColixes[index] = colix;
  }
  
  public void invalidatePolygons() {
    for (int i = polygonCount; --i >= 0;)
      if (!setABC(i))
        polygonIndexes[i] = null;
  }

  protected int iA, iB, iC;
  
  protected boolean setABC(int i) {
    int[] vertexIndexes = polygonIndexes[i];
    return vertexIndexes != null
          && !(Float.isNaN(vertexValues[iA = vertexIndexes[0]])
            || Float.isNaN(vertexValues[iB = vertexIndexes[1]]) 
            || Float.isNaN(vertexValues[iC = vertexIndexes[2]]));
  }

  public void slabPolygons(Point4f plane) {
    getIntersection(plane, null);
  }

  public boolean getIntersection(Point4f plane, Vector vData) {
    boolean isSlab = (vData == null);
    for (int i = polygonIndexes.length; --i >= 0;) {
      if (!setABC(i))
        continue;
      Point3f vA, vB, vC;
      float d1 = Measure.distanceToPlane(plane, vA = vertices[iA]);
      float d2 = Measure.distanceToPlane(plane, vB = vertices[iB]);
      float d3 = Measure.distanceToPlane(plane, vC = vertices[iC]);
      int test1 = (d1 < 0 ? 1 : 0) + (d2 < 0 ? 2 : 0) + (d3 < 0 ? 4 : 0);
      int test2 = (d1 > 0 ? 1 : 0) + (d2 > 0 ? 2 : 0) + (d3 > 0 ? 4 : 0);
      Point3f[] pts = null;
      switch (test1) {
      case 0:
      case 7:
        
        break;
      case 1:
      case 6:
        
        pts = new Point3f[] { interpolatePoint(vA, vB, -d1, d2),
            interpolatePoint(vA, vC, -d1, d3)};
        break;
      case 2:
      case 5:
        
        pts = new Point3f[] { interpolatePoint(vB, vA, -d2, d1),
            interpolatePoint(vB, vC, -d2, d3)};
        break;
      case 3:
      case 4:
        
        pts = new Point3f[] { interpolatePoint(vC, vA, -d3, d1),
            interpolatePoint(vC, vB, -d3, d2)};
        break;
      }
      if (isSlab) {
        int iD = 0;
        int iE = 0;
        
        
        
        switch (test2) {
        case 0:
          
          continue;
        case 7:
          
          break;
        case 1:
          
          iD = addVertexCopy(pts[0], vertexValues[iA]);  
          iE = addVertexCopy(pts[1], vertexValues[iA]);  
          addTriangleCheck(iD, iB, iC, 0, 0, 0);
          addTriangleCheck(iD, iC, iE, 0, 0, 0);
          break;
        case 2:
          
          iD = addVertexCopy(pts[0], vertexValues[iB]);  
          iE = addVertexCopy(pts[1], vertexValues[iB]);  
          addTriangleCheck(iA, iD, iC, 0, 0, 0);
          addTriangleCheck(iD, iE, iC, 0, 0, 0);
          break;
        case 3:
          
          iD = addVertexCopy(pts[0], vertexValues[iA]);  
          iE = addVertexCopy(pts[1], vertexValues[iB]);  
          addTriangleCheck(iE, iC, iD, 0, 0, 0);
          break;
        case 4:
          
          iD = addVertexCopy(pts[0], vertexValues[iC]);  
          iE = addVertexCopy(pts[1], vertexValues[iC]);  
          addTriangleCheck(iA, iB, iD, 0, 0, 0);
          addTriangleCheck(iD, iB, iE, 0, 0, 0);
          break;
        case 5:
          
          iD = addVertexCopy(pts[0], vertexValues[iA]);  
          iE = addVertexCopy(pts[1], vertexValues[iC]);  
          addTriangleCheck(iD, iB, iE, 0, 0, 0);
          break;
        case 6:
          
          iD = addVertexCopy(pts[0], vertexValues[iB]); 
          iE = addVertexCopy(pts[1], vertexValues[iC]); 
          addTriangleCheck(iA, iD, iE, 0, 0, 0);
          break;
        }
        polygonIndexes[i] = null;
      } else if (pts != null) {
        vData.add(pts);
      }
    }
    return false;
  }

  private Point3f interpolatePoint(Point3f v1, Point3f v2, float d1, float d2) {
    float f = d1 / (d1 + d2);
    return new Point3f(v1.x + (v2.x - v1.x) * f, 
        v1.y + (v2.y - v1.y) * f, 
        v1.z + (v2.z - v1.z) * f);    
  }
  
}
