

package org.jmol.shapesurface;

import java.util.BitSet;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.vecmath.Point3f;
import javax.vecmath.Point4f;
import javax.vecmath.Vector3f;

import org.jmol.g3d.Graphics3D;
import org.jmol.util.ArrayUtil;
import org.jmol.util.Logger;
import org.jmol.util.Measure;
import org.jmol.viewer.Viewer;
import org.jmol.jvxl.data.JvxlCoder;
import org.jmol.jvxl.data.JvxlData;

import org.jmol.jvxl.calc.MarchingSquares;
import org.jmol.shape.Mesh;

public class IsosurfaceMesh extends Mesh {
  JvxlData jvxlData = new JvxlData();
  int vertexIncrement = 1;
  int firstRealVertex = -1;
  int dataType;
  boolean hasGridPoints;
  Object calculatedArea;
  Object calculatedVolume;
  public boolean isSolvent;
  
  public float[] vertexValues;  
  public short[] vertexColixes;
  
  IsosurfaceMesh(String thisID, Graphics3D g3d, short colix, int index) {
    super(thisID, g3d, colix, index);
    haveCheckByte = true;
    jvxlData.version = Viewer.getJmolVersion();
  }

  void clear(String meshType, boolean iAddGridPoints) {
    super.clear(meshType);  
    nSets = 0;
    thisSet = -1;
    firstRealVertex = -1;
    hasGridPoints = iAddGridPoints;
    showPoints = iAddGridPoints;
    jvxlData.jvxlSurfaceData = "";
    jvxlData.jvxlEdgeData = "";
    jvxlData.jvxlColorData = "";
    jvxlData.jvxlVolumeDataXml = "";
    isColorSolid = true;
    vertexColixes = null;
    vertexValues = null;
    polygonColixes = null;
    jvxlData.contourValues = null;
    jvxlData.contourValuesUsed = null;
    jvxlData.contourColixes = null;
    jvxlData.contourColors = null;
    assocGridPointMap = null;
    assocGridPointNormals = null;
    vertexSets = null;
    centers = null;
    jvxlData.vContours = null;
    surfaceSet = null;
  }  

  void allocVertexColixes() {
    if (vertexColixes == null) {
      vertexColixes = new short[vertexCount];
      for (int i = vertexCount; --i >= 0; )
        vertexColixes[i] = colix;
    }
    isColorSolid = false;
  }

  Hashtable assocGridPointMap ;
  Hashtable assocGridPointNormals;

  int addVertexCopy(Point3f vertex, float value, int assocVertex, boolean associateNormals) {
    int vPt = addVertexCopy(vertex, value);
    switch (assocVertex) {
    case MarchingSquares.CONTOUR_POINT:
      if (firstRealVertex < 0)
        firstRealVertex = vPt;
      break;
    case MarchingSquares.VERTEX_POINT:
      hasGridPoints = true;
      break;
    case MarchingSquares.EDGE_POINT:
      vertexIncrement = 3;
      break;
    default:
      if (firstRealVertex < 0)
        firstRealVertex = vPt;
      if (associateNormals) {
        if (assocGridPointMap == null) {
          assocGridPointMap = new Hashtable();
          assocGridPointNormals = new Hashtable();
        }
        Integer key = new Integer(assocVertex);
        assocGridPointMap.put(new Integer(vPt), key);
        if (!assocGridPointNormals.containsKey(key))
          assocGridPointNormals.put(key, new Vector3f(0, 0, 0));
      }
    }
    return vPt;
  }

  int addVertexCopy(Point3f vertex, float value) {
    if (vertexCount == 0)
      vertexValues = new float[SEED_COUNT];
    else if (vertexCount >= vertexValues.length)
      vertexValues = (float[]) ArrayUtil.doubleLength(vertexValues);
    vertexValues[vertexCount] = value;
    return addVertexCopy(vertex);
  }

  public void setTranslucent(boolean isTranslucent, float iLevel) {
    super.setTranslucent(isTranslucent, iLevel);
    if (vertexColixes != null)
      for (int i = vertexCount; --i >= 0; )
        vertexColixes[i] =
          Graphics3D.getColixTranslucent(vertexColixes[i], isTranslucent, iLevel);
  }

  public short[] polygonColixes;
  private int lastColor;
  private short lastColix;
  
  int addTriangleCheck(int vertexA, int vertexB, int vertexC, int check, int check2,
                        int color) {
    if (vertices == null
        || vertexValues != null
        && (Float.isNaN(vertexValues[vertexA])
            || Float.isNaN(vertexValues[vertexB]) || Float
            .isNaN(vertexValues[vertexC]))
        || Float.isNaN(vertices[vertexA].x) 
        || Float.isNaN(vertices[vertexB].x)
        || Float.isNaN(vertices[vertexC].x))
      return -1;
    int n = polygonCount;
    if (n == 0)
      polygonIndexes = new int[SEED_COUNT][];
    else if (n == polygonIndexes.length)
      polygonIndexes = (int[][]) ArrayUtil.doubleLength(polygonIndexes);
    if (color != 0) {
      if (polygonColixes == null) {
        polygonColixes = new short[SEED_COUNT];
        lastColor = 0;
      } else if (n == polygonColixes.length) {
        polygonColixes = (short[]) ArrayUtil.doubleLength(polygonColixes);
      }
      polygonColixes[n] = (color == lastColor ? lastColix
          : (lastColix = Graphics3D.getColix(lastColor = color)));
    }
    polygonIndexes[polygonCount++] = new int[] { vertexA, vertexB, vertexC,
        check, check2 };
    return n;
  }
  
  void invalidateTriangles() {
    for (int i = polygonCount; --i >= 0;)
      if (!setABC(i))
        polygonIndexes[i] = null;
  }
  
  private int iA, iB, iC;
  
  private boolean setABC(int i) {
    int[] vertexIndexes = polygonIndexes[i];
    return vertexIndexes != null
          && !(Float.isNaN(vertexValues[iA = vertexIndexes[0]])
            || Float.isNaN(vertexValues[iB = vertexIndexes[1]]) 
            || Float.isNaN(vertexValues[iC = vertexIndexes[2]]));
  }
  
  Object calculateArea() {
    if (calculatedArea != null)
      return calculatedArea;
    boolean justOne = (nSets == 0 || thisSet >= 0); 
    int n = (justOne ? 1 : nSets);
    double[] v = new double[n];
    for (int i = polygonCount; --i >= 0;) {
      if (!setABC(i)) 
        continue;
      int iSet = (nSets == 0 ? 0 : vertexSets[iA]);
      if (thisSet >= 0 && iSet != thisSet)
        continue;
      vAB.sub(vertices[iB], vertices[iA]);
      vAC.sub(vertices[iC], vertices[iA]);
      vTemp.cross(vAB, vAC);
      v[justOne ? 0 : iSet] += vTemp.length();
    }
    for (int i = 0; i < n; i++)
      v[i] /= 2;
    if (justOne)
      return calculatedArea = new Float(v[0]);
    return calculatedArea = v;
  }

  Object calculateVolume() {
    if (calculatedVolume != null)
      return calculatedVolume;
    boolean justOne = (nSets == 0 || thisSet >= 0); 
    int n = (justOne ? 1 : nSets);
    double[] v = new double[n];
    for (int i = polygonCount; --i >= 0;) {
      if (!setABC(i))
        continue;
      int iSet = (nSets == 0 ? 0 : vertexSets[iA]);
      if (thisSet >= 0 && iSet != thisSet)
        continue;
      vAB.set(vertices[iB]);
      vAC.set(vertices[iC]);
      vTemp.cross(vAB, vAC);
      vAC.set(vertices[iA]);
      v[justOne ? 0 : iSet] += vAC.dot(vTemp);
    }
    for (int i = 0; i < n; i++)
      v[i] /= 6;
    if (justOne)
      return calculatedVolume = new Float(v[0]);
    return calculatedVolume = v;
  }

  BitSet[] surfaceSet;
  int[] vertexSets;
  int nSets;
  int thisSet = -1;
  
  public void sumVertexNormals(Vector3f[] vectorSums) {
    super.sumVertexNormals(vectorSums);
    
    if (assocGridPointMap != null) {
      Enumeration e = assocGridPointMap.keys();
      while (e.hasMoreElements()) {
        Integer I = (Integer) e.nextElement();
        ((Vector3f) assocGridPointNormals.get(assocGridPointMap.get(I)))
            .add(vectorSums[I.intValue()]);
      }
      e = assocGridPointMap.keys();
      while (e.hasMoreElements()) {
        Integer I = (Integer) e.nextElement();
        vectorSums[I.intValue()] = ((Vector3f) assocGridPointNormals
            .get(assocGridPointMap.get(I)));
      }
    }
  }
  
  Point3f[] centers;
  Point3f[] getCenters() {
    if (centers != null)
      return centers;
    centers = new Point3f[polygonCount];
    for (int i = 0; i < polygonCount; i++) {
      Point3f pt = centers[i] = new Point3f();
      pt.add(vertices[polygonIndexes[i][0]]);
      pt.add(vertices[polygonIndexes[i][1]]);
      pt.add(vertices[polygonIndexes[i][2]]);
      pt.scale(1/3f);
    }
    return centers;
  }
  
  Point4f getFacePlane(int i, Vector3f vNorm) {
    return Measure.getPlaneThroughPoints(vertices[polygonIndexes[i][0]], 
        vertices[polygonIndexes[i][1]], vertices[polygonIndexes[i][2]], 
        vNorm, vAB, vAC);
  }
  
  
  Vector[] getContours() {
    int n = jvxlData.nContours;
    if (n == 0 || polygonIndexes == null)
      return null;
    havePlanarContours = (jvxlData.jvxlPlane != null);
    if (havePlanarContours)
      return null; 
    if (n < 0)
      n = -1 - n;
    Vector[] vContours = jvxlData.vContours;
    if (vContours != null) {
      for (int i = 0; i < n; i++) {
        if (vContours[i].size() > JvxlCoder.CONTOUR_POINTS)
          return jvxlData.vContours;
        JvxlCoder.set3dContourVector(vContours[i], polygonIndexes, vertices);
      }
      
      return jvxlData.vContours;
    }
    
    vContours = new Vector[n];
    for (int i = 0; i < n; i++)
      vContours[i] = new Vector();
    if (jvxlData.contourValuesUsed == null) {
      float dv = (jvxlData.valueMappedToBlue - jvxlData.valueMappedToRed)
          / (n + 1);
      
      for (int i = 0; i < n; i++) {
        float value = jvxlData.valueMappedToRed + (i + 1) * dv;
        get3dContour(vContours[i], value, jvxlData.contourColixes[i]);
      }
      Logger.info(n + " contour lines; separation = " + dv);
    } else {
      for (int i = 0; i < n; i++) {
        float value = jvxlData.contourValuesUsed[i];
        get3dContour(vContours[i], value, jvxlData.contourColixes[i]);
      }      
    }
    jvxlData.contourColixes = new short[n];
    jvxlData.contourValues = new float[n];
    for (int i = 0; i < n; i++) {
      jvxlData.contourValues[i] = ((Float) vContours[i].get(2)).floatValue();
      jvxlData.contourColixes[i] = ((short[]) vContours[i].get(3))[0];
    }
    return jvxlData.vContours = vContours;
  }
  
  private void get3dContour(Vector v, float value, short colix) {
    BitSet bsContour = new BitSet(polygonCount);
    StringBuffer fData = new StringBuffer();
    int color = Graphics3D.getArgb(colix);
    setContourVector(v, polygonCount, bsContour, value, colix, color, fData);
    for (int i = 0; i < polygonCount; i++)
      if (setABC(i))
        addContourPoints(v, bsContour, i, fData, vertices,
            vertexValues, iA, iB, iC, value);
  }

  public static void setContourVector(Vector v, int nPolygons,
                                      BitSet bsContour, float value, short colix,
                                      int color, StringBuffer fData) {
    v.add(JvxlCoder.CONTOUR_NPOLYGONS, new Integer(nPolygons));
    v.add(JvxlCoder.CONTOUR_BITSET, bsContour);
    v.add(JvxlCoder.CONTOUR_VALUE, new Float(value));
    v.add(JvxlCoder.CONTOUR_COLIX, new short[] { colix });
    v.add(JvxlCoder.CONTOUR_COLOR, new int[] { color });
    v.add(JvxlCoder.CONTOUR_FDATA, fData);
  }

  public static void addContourPoints(Vector v, BitSet bsContour, int i,
                                      StringBuffer fData, Point3f[] vertices,
                                      float[] vertexValues, int iA, int iB,
                                      int iC, float value) {
    Point3f pt1 = null;
    Point3f pt2 = null;
    int type = 0;
    
    float f1 = checkPt(vertexValues, iA, iB, value);
    if (!Float.isNaN(f1)) {
      pt1 = getContourPoint(vertices, iA, iB, f1);
      type |= 1;
    }
    
    float f2 = (f1 == 1 ? Float.NaN : checkPt(vertexValues, iB, iC, value));
    if (!Float.isNaN(f2)) {
      pt2 = getContourPoint(vertices, iB, iC, f2);
      if (type == 0) {
        pt1 = pt2;
        f1 = f2;
      }
      type |= 2;
    }
    
    switch (type) {
    case 0:
      return; 
    case 1:
      if (f1 == 0)
        return; 
      
    case 2:
      
      f2 = (f2 == 1 ? Float.NaN : checkPt(vertexValues, iC, iA, value));
      if (!Float.isNaN(f2)) {
        pt2 = getContourPoint(vertices, iC, iA, f2);
        type |= 4;
      }
      break;
    }
    
    switch (type) {
    case 3:
    case 5:
    case 6:
      break;
    default:
      return;
    }
    bsContour.set(i);
    JvxlCoder.appendContourTriangleIntersection(type, f1, f2, fData);
    v.add(pt1);
    v.add(pt2);
  }

  
  private static float checkPt(float[] vertexValues, int i, int j, float v) {
    float v1, v2;
    return (v == (v1 = vertexValues[i]) ? 0 
        : v == (v2 = vertexValues[j]) ? 1 
        : (v1 < v) == (v < v2) ? (v - v1) / (v2 - v1) 
        : Float.NaN);
  }

  private static Point3f getContourPoint(Point3f[] vertices, int i, int j, float f) {
    Point3f pt = new Point3f();
    pt.set(vertices[j]);
    pt.sub(vertices[i]);
    pt.scale(f);
    pt.add(vertices[i]);
    return pt;
  }

  float[] contourValues;
  short[] contourColixes;
  public void setDiscreteColixes(float[] values, short[] colixes) {
    if (values != null)
      jvxlData.contourValues = values;
    if (values == null)
      values = jvxlData.contourValues = jvxlData.contourValuesUsed;
    if (colixes == null && jvxlData.contourColixes != null) {
      colixes = jvxlData.contourColixes;
    } else {
      jvxlData.contourColixes = colixes;
      jvxlData.contourColors = Graphics3D.getHexCodes(colixes);
    }
    if (vertices == null || vertexValues == null || values == null)
      return;
    int n = values.length;
    float vMax = values[n - 1];
    colorCommand = null;
    boolean haveColixes = (colixes != null && colixes.length > 0);
    isColorSolid = haveColixes && jvxlData.jvxlPlane != null;
    if (jvxlData.vContours != null) {
      if (haveColixes)
        for (int i = 0; i < jvxlData.vContours.length; i++)
          ((short[]) jvxlData.vContours[i].get(3))[0] = colixes[i
              % colixes.length];
      return;
    }
    short defaultColix = 0;
    polygonColixes = new short[polygonCount];
    for (int i = 0; i < polygonCount; i++) {
      int[] pi = polygonIndexes[i];
      polygonColixes[i] = defaultColix;
      float v = 0;
      for (int j = 0; j < 3; j++) {
        v += vertexValues[pi[j]];
      }
      v /= 3;
      for (int j = n; --j >= 0;) {
        if (v > values[j] && v < vMax) {
          polygonColixes[i] = (haveColixes ? colixes[j % colixes.length] : 0);
          break;
        }
      }
    }
  }

  
  Hashtable getContourList(Viewer viewer) {
    Hashtable ht = new Hashtable();
    ht.put("values", (jvxlData.contourValuesUsed == null ? jvxlData.contourValues : jvxlData.contourValuesUsed));
    Vector colors = new Vector();
    if (jvxlData.contourColixes != null) {
      
      for (int i = 0; i < jvxlData.contourColixes.length; i++) {
        colors.add(Graphics3D.colorPointFromInt2(Graphics3D.getArgb(jvxlData.contourColixes[i])));
      }
      ht.put("colors", colors);
    }
    return ht;
  }

  
    
    
    
  
}
