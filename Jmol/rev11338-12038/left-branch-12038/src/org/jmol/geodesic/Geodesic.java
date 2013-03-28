

package org.jmol.geodesic;

import javax.vecmath.Vector3f;
import java.util.Hashtable;

  

public class Geodesic {
  
  private final static float halfRoot5 = (float)(0.5 * Math.sqrt(5));
  private final static float oneFifth = 2 * (float)Math.PI / 5;
  private final static float oneTenth = oneFifth / 2;
  
  
  
  
  
  private final static short[] faceVertexesIcosahedron = {
    0, 1, 2,
    0, 2, 3,
    0, 3, 4,
    0, 4, 5,
    0, 5, 1,

    1, 6, 2,
    2, 7, 3,
    3, 8, 4,
    4, 9, 5,
    5, 10, 1,

    6, 1, 10,
    7, 2, 6,
    8, 3, 7,
    9, 4, 8,
    10, 5, 9,

    11, 6, 10,
    11, 7, 6,
    11, 8, 7,
    11, 9, 8,
    11, 10, 9,
  };

  
  private final static short[] neighborVertexesIcosahedron = {
    1, 2, 3, 4, 5,-1, 
    0, 5,10, 6, 2,-1, 
    0, 1, 6, 7, 3,-1, 
    0, 2, 7, 8, 4,-1, 

    0, 3, 8, 9, 5,-1, 
    0, 4, 9,10, 1,-1, 
    1,10,11, 7, 2,-1, 
    2, 6,11, 8, 3,-1, 

    3, 7,11, 9, 4,-1, 
    4, 8,11,10, 5,-1, 
    5, 9,11, 6, 1,-1, 
    6, 7, 8, 9,10,-1 
  };

  
  
  public final static int standardLevel = 3;
  private final static int maxLevel = 4;
  private static short[] vertexCounts;
  private static Vector3f[] vertexVectors;
  private static short[][] faceVertexesArrays;
  private static short[][] neighborVertexesArrays;

  static public short[][] getFaceVertexesArrays() {
    return faceVertexesArrays;
  }
  
  static public short[][] getNeighborVertexesArrays() {
     return neighborVertexesArrays;
  }
  
  static public int getVertexCount(int level) {
    if (vertexCounts == null)
      createGeodesic();
    return vertexCounts[level];
  }
  
  static public Vector3f[] getVertexVectors() {
    return vertexVectors;
  }

  static public int getVertexVectorsCount() {
    return (vertexVectors == null ? 0 : vertexVectors.length);
  }

  static public Vector3f getVertexVector(int i) {
    return vertexVectors[i];
  }

  static public short[] getFaceVertexes(int level) {
    return faceVertexesArrays[level];
  }

  
  
  synchronized private static void createGeodesic() {
    
    if (vertexCounts != null)
      return;
    short[] v = new short[maxLevel];
    neighborVertexesArrays = new short[maxLevel][];
    faceVertexesArrays = new short[maxLevel][];
    vertexVectors = new Vector3f[12];
    vertexVectors[0] = new Vector3f(0, 0, halfRoot5);
    for (int i = 0; i < 5; ++i) {
      vertexVectors[i + 1] = new Vector3f((float) Math.cos(i * oneFifth),
          (float) Math.sin(i * oneFifth), 0.5f);
      vertexVectors[i + 6] = new Vector3f((float) Math.cos(i * oneFifth
          + oneTenth), (float) Math.sin(i * oneFifth + oneTenth), -0.5f);
    }
    vertexVectors[11] = new Vector3f(0, 0, -halfRoot5);
    for (int i = 12; --i >= 0;)
      vertexVectors[i].normalize();
    faceVertexesArrays[0] = faceVertexesIcosahedron;
    neighborVertexesArrays[0] = neighborVertexesIcosahedron;
    v[0] = 12;

    for (int i = 0; i < maxLevel - 1; ++i)
      quadruple(i, v);

    
    vertexCounts = v;
  }

  

  private static short vertexNext;
  private static Hashtable htVertex;
    
  private final static boolean VALIDATE = true;

  private static void quadruple(int level, short[] counts) {
    htVertex = new Hashtable();
    int oldVertexCount = vertexVectors.length;
    short[] oldFaceVertexes = faceVertexesArrays[level];
    int oldFaceVertexesLength = oldFaceVertexes.length;
    int oldFaceCount = oldFaceVertexesLength / 3;
    int oldEdgesCount = oldVertexCount + oldFaceCount - 2;
    int newVertexCount = oldVertexCount + oldEdgesCount;
    int newFaceCount = 4 * oldFaceCount;
    Vector3f[] newVertexVectors = new Vector3f[newVertexCount];
    System.arraycopy(vertexVectors, 0, newVertexVectors, 0, oldVertexCount);
    vertexVectors = newVertexVectors;

    short[] newFacesVertexes = new short[3 * newFaceCount];
    faceVertexesArrays[level + 1] = newFacesVertexes;
    short[] neighborVertexes = new short[6 * newVertexCount];
    neighborVertexesArrays[level + 1] = neighborVertexes;
    for (int i = neighborVertexes.length; --i >= 0; )
      neighborVertexes[i] = -1;

    counts[level + 1] = (short)newVertexCount;

    vertexNext = (short)oldVertexCount;

    int iFaceNew = 0;
    for (int i = 0; i < oldFaceVertexesLength; ) {
      short iA = oldFaceVertexes[i++];
      short iB = oldFaceVertexes[i++];
      short iC = oldFaceVertexes[i++];
      short iAB = getVertex(iA, iB);
      short iBC = getVertex(iB, iC);
      short iCA = getVertex(iC, iA);
        
      newFacesVertexes[iFaceNew++] = iA;
      newFacesVertexes[iFaceNew++] = iAB;
      newFacesVertexes[iFaceNew++] = iCA;

      newFacesVertexes[iFaceNew++] = iB;
      newFacesVertexes[iFaceNew++] = iBC;
      newFacesVertexes[iFaceNew++] = iAB;

      newFacesVertexes[iFaceNew++] = iC;
      newFacesVertexes[iFaceNew++] = iCA;
      newFacesVertexes[iFaceNew++] = iBC;

      newFacesVertexes[iFaceNew++] = iCA;
      newFacesVertexes[iFaceNew++] = iAB;
      newFacesVertexes[iFaceNew++] = iBC;

      addNeighboringVertexes(neighborVertexes, iAB, iA);
      addNeighboringVertexes(neighborVertexes, iAB, iCA);
      addNeighboringVertexes(neighborVertexes, iAB, iBC);
      addNeighboringVertexes(neighborVertexes, iAB, iB);

      addNeighboringVertexes(neighborVertexes, iBC, iB);
      addNeighboringVertexes(neighborVertexes, iBC, iCA);
      addNeighboringVertexes(neighborVertexes, iBC, iC);

      addNeighboringVertexes(neighborVertexes, iCA, iC);
      addNeighboringVertexes(neighborVertexes, iCA, iA);
    }
    if (VALIDATE) {
      int vertexCount = vertexVectors.length;
      if (iFaceNew != newFacesVertexes.length)
        throw new NullPointerException();
      if (vertexNext != newVertexCount)
        throw new NullPointerException();
      for (int i = 0; i < 12; ++i) {
        for (int j = 0; j < 5; ++j) {
          int neighbor = neighborVertexes[i * 6 + j];
          if (neighbor < 0)
            throw new NullPointerException();
          if (neighbor >= vertexCount)
            throw new NullPointerException();
        if (neighborVertexes[i * 6 + 5] != -1)
          throw new NullPointerException();
        }
      }
      for (int i = 12 * 6; i < neighborVertexes.length; ++i) {
        int neighbor = neighborVertexes[i];
        if (neighbor < 0)
          throw new NullPointerException();
        if (neighbor >= vertexCount)
          throw new NullPointerException();
      }
      for (int i = 0; i < newVertexCount; ++i) {
        int neighborCount = 0;
        for (int j = neighborVertexes.length; --j >= 0; )
          if (neighborVertexes[j] == i)
            ++neighborCount;
        if ((i < 12 && neighborCount != 5) ||
            (i >= 12 && neighborCount != 6))
          throw new NullPointerException();
        int faceCount = 0;
        for (int j = newFacesVertexes.length; --j >= 0; )
          if (newFacesVertexes[j] == i)
            ++faceCount;
        if ((i < 12 && faceCount != 5) ||
            (i >= 12 && faceCount != 6))
          throw new NullPointerException();
      }
    }
    htVertex = null;
  }

  private static void addNeighboringVertexes(short[] neighborVertexes,
                                             short v1, short v2) {
    for (int i = v1 * 6, iMax = i + 6; i < iMax; ++i) {
      if (neighborVertexes[i] == v2)
        return;
      if (neighborVertexes[i] < 0) {
        neighborVertexes[i] = v2;
        for (int j = v2 * 6, jMax = j + 6; j < jMax; ++j) {
          if (neighborVertexes[j] == v1)
            return;
          if (neighborVertexes[j] < 0) {
            neighborVertexes[j] = v1;
            return;
          }
        }
      }
    }
    throw new NullPointerException();
  }

  
    
  private static short getVertex(short v1, short v2) {
    if (v1 > v2) {
      short t = v1;
      v1 = v2;
      v2 = t;
    }
    Integer hashKey = new Integer((v1 << 16) + v2);
    Short iv = (Short)htVertex.get(hashKey);
    if (iv != null)
      return iv.shortValue();
    Vector3f newVertexVector = new Vector3f(vertexVectors[v1]);
    vertexVectors[vertexNext] = newVertexVector;
    newVertexVector.add(vertexVectors[v2]);
    newVertexVector.scale(0.5f);
    newVertexVector.normalize();
    htVertex.put(hashKey, new Short(vertexNext));
    return vertexNext++;
  }
}
