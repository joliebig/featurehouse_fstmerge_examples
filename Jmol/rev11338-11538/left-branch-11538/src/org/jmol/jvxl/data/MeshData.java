




package org.jmol.jvxl.data;

import java.util.BitSet;

import javax.vecmath.Point3f;

import org.jmol.g3d.Graphics3D;
import org.jmol.util.*;

public class MeshData {
  private final static int SEED_COUNT = 25;
  
  public final static int MODE_GET_VERTICES = 1;
  public final static int MODE_GET_COLOR_INDEXES = 2;
  public final static int MODE_PUT_SETS = 3;
  public final static int MODE_PUT_VERTICES = 4;

  public int polygonCount;
  public Point3f[] vertices;
  public short[] vertexColixes;
  public int vertexCount;
  public float[] vertexValues;
  public int[][] polygonIndexes;
  public short[] polygonColixes;

  public BitSet[] surfaceSet;
  public int[] vertexSets;
  public int nSets = 0;
  
  public Point3f[] dots;
  
  private boolean setsSuccessful;
  public int vertexIncrement = 1;


  public void clear(String meshType) {
    vertexCount = polygonCount = 0;
    vertices = null;
    polygonIndexes = null;
    surfaceSet = null;
  }
  
  public int addVertexCopy(Point3f vertex, float value, int assocVertex) {
    if (assocVertex < 0)
      vertexIncrement = -assocVertex;  
    if (vertexCount == 0)
      vertexValues = new float[SEED_COUNT];
    else if (vertexCount >= vertexValues.length)
      vertexValues = (float[]) ArrayUtil.doubleLength(vertexValues);
    vertexValues[vertexCount] = value;
    return addVertexCopy(vertex);
  }

  private int addVertexCopy(Point3f vertex) {
    if (vertexCount == 0)
      vertices = new Point3f[SEED_COUNT];
    else if (vertexCount == vertices.length)
      vertices = (Point3f[]) ArrayUtil.doubleLength(vertices);
    vertices[vertexCount] = new Point3f(vertex);
    
    return vertexCount++;
  }

  private int lastColor;
  private short lastColix;

  public void addTriangleCheck(int vertexA, int vertexB, int vertexC, int check, int color) {
  if (vertexValues != null && (Float.isNaN(vertexValues[vertexA])||Float.isNaN(vertexValues[vertexB])||Float.isNaN(vertexValues[vertexC])))
    return;
  if (Float.isNaN(vertices[vertexA].x)||Float.isNaN(vertices[vertexB].x)||Float.isNaN(vertices[vertexC].x))
    return;
  if (polygonCount == 0)
    polygonIndexes = new int[SEED_COUNT][];
  else if (polygonCount == polygonIndexes.length)
    polygonIndexes = (int[][]) ArrayUtil.doubleLength(polygonIndexes);
  if (color != 0) {
    if (polygonColixes == null) {
      polygonColixes = new short[SEED_COUNT];
      lastColor = 0;
    }
    else if (polygonCount == polygonColixes.length) {
      polygonColixes = (short[]) ArrayUtil.doubleLength(polygonColixes);
    }
    polygonColixes[polygonCount] = (color == lastColor ? lastColix : (lastColix = Graphics3D.getColix(lastColor = color)));
  }    
  polygonIndexes[polygonCount++] = new int[] {vertexA, vertexB, vertexC, check};
 }
  
  public BitSet[] getSurfaceSet() {
    return (surfaceSet == null ? getSurfaceSet(0) : surfaceSet);
  }
  
  public BitSet[] getSurfaceSet(int level) {
    if (level == 0) {
      surfaceSet = new BitSet[100];
      nSets = 0;
    }
    setsSuccessful = true;
    for (int i = 0; i < polygonCount; i++)
      if (polygonIndexes[i] != null) {
        int[] p = polygonIndexes[i];
        int pt0 = findSet(p[0]);
        int pt1 = findSet(p[1]);
        int pt2 = findSet(p[2]);
        if (pt0 < 0 && pt1 < 0 && pt2 < 0) {
          createSet(p[0], p[1], p[2]);
          continue;
        }
        if (pt0 == pt1 && pt1 == pt2)
          continue;
        if (pt0 >= 0) {
          surfaceSet[pt0].set(p[1]);
          surfaceSet[pt0].set(p[2]);
          if (pt1 >= 0 && pt1 != pt0)
            mergeSets(pt0, pt1);
          if (pt2 >= 0 && pt2 != pt0 && pt2 != pt1)
            mergeSets(pt0, pt2);
          continue;
        }
        if (pt1 >= 0) {
          surfaceSet[pt1].set(p[0]);
          surfaceSet[pt1].set(p[2]);
          if (pt2 >= 0 && pt2 != pt1)
            mergeSets(pt1, pt2);
          continue;
        }
        surfaceSet[pt2].set(p[0]);
        surfaceSet[pt2].set(p[1]);
      }
    int n = 0;
    for (int i = 0; i < nSets; i++)
      if (surfaceSet[i] != null)
        n++;
    BitSet[] temp = new BitSet[100];
    n = 0;
    for (int i = 0; i < nSets; i++)
      if (surfaceSet[i] != null)
        temp[n++] = surfaceSet[i];
    nSets = n;
    surfaceSet = temp;
    if (!setsSuccessful && level < 2)
      getSurfaceSet(level + 1);
    if (level == 0) {
      vertexSets = new int[vertexCount];
      for (int i = 0; i < nSets; i++)
        for (int j = 0; j < vertexCount; j++)
          if (surfaceSet[i].get(j))
            vertexSets[j] = i;
    }
    return surfaceSet;
  }

  private int findSet(int vertex) {
    for (int i = 0; i < nSets; i++)
      if (surfaceSet[i] != null && surfaceSet[i].get(vertex))
        return i;
    return -1;
  }

  private void createSet(int v1, int v2, int v3) {
    int i;
    for (i = 0; i < nSets; i++)
      if (surfaceSet[i] == null)
        break;
    if (i >= 100) {
      setsSuccessful = false;
      return;
    }
    surfaceSet[i] = new BitSet();
    surfaceSet[i].set(v1);
    surfaceSet[i].set(v2);
    surfaceSet[i].set(v3);
    if (i == nSets)
      nSets++;
  }

  private void mergeSets(int a, int b) {
    surfaceSet[a].or(surfaceSet[b]);
    surfaceSet[b] = null;
  }
  
  public void invalidateSurfaceSet(int i) {
    for (int j = surfaceSet[i].length(); --j >= 0;)
      if (surfaceSet[i].get(j))
        vertexValues[j] = Float.NaN;
    surfaceSet[i] = null;
  }
  
  public static boolean checkCutoff(int iA, int iB, int iC, float[] vertexValues) {
    
    
    
    
    if (iA < 0 || iB < 0 || iC < 0)
      return false;

    float val1 = vertexValues[iA];
    float val2 = vertexValues[iB];
    float val3 = vertexValues[iC];
    return (val1 >= 0 && val2 >= 0 && val3 >= 0 
        || val1 <= 0 && val2 <= 0 && val3 <= 0);
  }
  
  private boolean setABC(int i) {
    int[] vertexIndexes = polygonIndexes[i];
    return vertexIndexes != null
          && !(Float.isNaN(vertexValues[vertexIndexes[0]])
            || Float.isNaN(vertexValues[vertexIndexes[1]]) 
            || Float.isNaN(vertexValues[vertexIndexes[2]]));
  }
  

  public void invalidateTriangles() {
    for (int i = polygonCount; --i >= 0;)
      if (!setABC(i))
        polygonIndexes[i] = null;
  }
}

