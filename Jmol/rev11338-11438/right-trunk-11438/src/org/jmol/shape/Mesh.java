

package org.jmol.shape;

import java.util.BitSet;
import java.util.Vector;

import org.jmol.util.ArrayUtil;
import org.jmol.util.Escape;
import org.jmol.util.Measure;
import org.jmol.viewer.JmolConstants;
import org.jmol.api.JmolRendererInterface;
import org.jmol.g3d.*;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

public class Mesh {
  
  public final static String PREVIOUS_MESH_ID = "+PREVIOUS_MESH+";
  private JmolRendererInterface g3d;
  
  public String[] title = null;
  public Point3f[] vertices;
  public short[] normixes;
  public int[][] polygonIndexes = null;
  public BitSet[] bitsets; 

  public String thisID;
  public boolean isValid = true;
  public String scriptCommand;
  public String colorCommand;
 
  public boolean visible = true;
  public short colix;
  public int vertexCount;
  public int polygonCount;  
  
  public float scale = 1;
  public boolean haveXyPoints;
  public int diameter;
  public float width;
  public Point3f ptCenter = new Point3f(0,0,0);
  public String meshType = null;
  public Mesh linkedMesh = null; 
  
  public int index;
  public int atomIndex = -1;
  public int modelIndex = -1;  
  public int visibilityFlags;
  public boolean insideOut;
  
  public void setVisibilityFlags(int n) {
    visibilityFlags = n;
  }
  
  public boolean showContourLines = false;
  public boolean showPoints = false;
  public boolean drawTriangles = false;
  public boolean fillTriangles = true;
  public boolean showTriangles = false; 
  public boolean frontOnly = false;
  public boolean isTwoSided = true;
  public boolean isColorSolid = true;
  public boolean havePlanarContours = false;
  
  public final static int SEED_COUNT = 25; 
  
  public Mesh(String thisID, JmolRendererInterface g3d, short colix, int index) {
    if (PREVIOUS_MESH_ID.equals(thisID))
      thisID = null;
    this.thisID = thisID;
    this.g3d = g3d;
    this.colix = colix;
    this.index = index;
    
  }

  
  
  
  

  public void clear(String meshType) {
    vertexCount = polygonCount = 0;
    scale = 1;
    havePlanarContours = false;
    haveXyPoints = false;
    showPoints = false;
    showContourLines = false;
    drawTriangles = false;
    fillTriangles = true;
    showTriangles = false; 
    frontOnly = false;
    title = null;
    normixes = null;
    bitsets = null;    
    vertices = null;
    polygonIndexes = null;
    
    this.meshType = meshType;
  }

  public int lighting = JmolConstants.FRONTLIT;
  
  public void initialize(int lighting) {
    Vector3f[] vectorSums = getVertexNormals();
    normixes = new short[vertexCount];
    initializeNormixes(lighting, vectorSums);
  }

  public Vector3f[] getVertexNormals() {
    Vector3f[] vectorSums = new Vector3f[vertexCount];
    for (int i = vertexCount; --i >= 0;)
      vectorSums[i] = new Vector3f();
    sumVertexNormals(vectorSums);
    for (int i = vertexCount; --i >= 0;)
      vectorSums[i].normalize();
    return vectorSums;
  }
  
  public void initializeNormixes(int lighting, Vector3f[] vectorSums) {
    isTwoSided = (lighting == JmolConstants.FULLYLIT);
    normixes = new short[vertexCount];
    if (haveXyPoints)
      for (int i = vertexCount; --i >= 0;)
        normixes[i] = Graphics3D.NORMIX_NULL;
    else
      for (int i = vertexCount; --i >= 0;)
        normixes[i] = g3d.getNormix(vectorSums[i]);
    this.lighting = JmolConstants.FRONTLIT;
    if (insideOut)
      invertNormixes();
    setLighting(lighting);
  }
  
  public void setLighting(int lighting) {
    if (lighting == this.lighting)
      return;
    flipLighting(this.lighting);
    flipLighting(this.lighting = lighting);
  }
  
  private void flipLighting(int lighting) {
    if (lighting == JmolConstants.FULLYLIT)
      for (int i = vertexCount; --i >= 0;)
        normixes[i] = (short)~normixes[i];
    else if ((lighting == JmolConstants.FRONTLIT) == insideOut)
      invertNormixes();
  }

  private void invertNormixes() {
    for (int i = vertexCount; --i >= 0;)
      normixes[i] = g3d.getInverseNormix(normixes[i]);
  }

  public void setTranslucent(boolean isTranslucent, float iLevel) {
    colix = Graphics3D.getColixTranslucent(colix, isTranslucent, iLevel);
  }

  public final Vector3f vAB = new Vector3f();
  public final Vector3f vAC = new Vector3f();
  public final Vector3f vTemp = new Vector3f();

  protected boolean haveCheckByte;
  public Vector data1;
  public Vector data2;
  
  public void sumVertexNormals(Vector3f[] vectorSums) {
    int adjustment = (haveCheckByte ? 1 : 0);
    for (int i = polygonCount; --i >= 0;) {
      int[] pi = polygonIndexes[i];
      try {
        if (pi != null) {
          Measure.calcNormalizedNormal(vertices[pi[0]], vertices[pi[1]],
              vertices[pi[2]], vTemp, vAB, vAC);
          
          
          
          float l = vTemp.length();
          if (l > 0.9 && l < 1.1) 
            for (int j = pi.length - adjustment; --j >= 0;) {
              int k = pi[j];
              vectorSums[k].add(vTemp);
            }
        }
      } catch (Exception e) {
      }
    }
  }

  public void setPolygonCount(int polygonCount) {
    this.polygonCount = polygonCount;
    if (polygonCount < 0)
      return;
    if (polygonIndexes == null || polygonCount > polygonIndexes.length)
      polygonIndexes = new int[polygonCount][];
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
    if (polygonCount == 0)
      polygonIndexes = new int[SEED_COUNT][];
    else if (polygonCount == polygonIndexes.length)
      polygonIndexes = (int[][]) ArrayUtil.doubleLength(polygonIndexes);
    polygonIndexes[polygonCount++] = new int[] {vertexA, vertexB, vertexC};
  }

  public void addQuad(int vertexA, int vertexB, int vertexC, int vertexD) {
    if (polygonCount == 0)
      polygonIndexes = new int[SEED_COUNT][];
    else if (polygonCount == polygonIndexes.length)
      polygonIndexes = (int[][]) ArrayUtil.doubleLength(polygonIndexes);
    polygonIndexes[polygonCount++] = new int[] {vertexA, vertexB, vertexC, vertexD};
  }

  public void setColix(short colix) {
    this.colix = colix;
  }

  public String getState(String type) {
    StringBuffer s = new StringBuffer(type);
    if (!type.equals("mo"))
      s.append(" ID ").append(Escape.escape(thisID));
    s.append(fillTriangles ? " fill" : " noFill");
    s.append(drawTriangles ? " mesh" : " noMesh");
    s.append(showPoints ? " dots" : " noDots");
    s.append(frontOnly ? " frontOnly" : " notFrontOnly");
    if (showContourLines)
      s.append(" contourlines");
    if (showTriangles)
      s.append(" triangles");
    s.append(lighting == JmolConstants.BACKLIT ? " backlit"
        : lighting == JmolConstants.FULLYLIT ? " fullylit" : " frontlit");
    if (!visible)
      s.append(" hidden");
    return s.toString();
  }
}
