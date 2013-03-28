package org.jmol.util;

import java.util.BitSet;

import javax.vecmath.Point3f;
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

}
