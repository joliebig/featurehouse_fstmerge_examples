

package org.jmol.shape;

import java.util.BitSet;
import java.util.Vector;

import org.jmol.util.Escape;
import org.jmol.util.Measure;
import org.jmol.util.MeshSurface;
import org.jmol.viewer.JmolConstants;
import org.jmol.api.JmolRendererInterface;
import org.jmol.g3d.*;

import javax.vecmath.Point3f;
import javax.vecmath.Point4f;
import javax.vecmath.Vector3f;

public class Mesh extends MeshSurface {
  
  public final static String PREVIOUS_MESH_ID = "+PREVIOUS_MESH+";
  private JmolRendererInterface g3d;
  
  public String[] title;
  
  public short[] normixes;
  private int normixCount;
  public BitSet[] bitsets; 
  public Vector lineData;
  public String thisID;
  public boolean isValid = true;
  public String scriptCommand;
  public String colorCommand;
 
  public boolean visible = true;
  public int lighting = JmolConstants.FRONTLIT;
  

  public float scale = 1;
  public boolean haveXyPoints;
  public boolean isPolygonSet; 
  public int diameter;
  public float width;
  public Point3f ptCenter = new Point3f(0,0,0);
  public String meshType;
  public Mesh linkedMesh; 
  
  public int index;
  public int atomIndex = -1;
  public int modelIndex = -1;  
  public int visibilityFlags;
  public boolean insideOut;
  public int checkByteCount;

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
  public boolean havePlanarContours = false;
  
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
    isPolygonSet = false;
    frontOnly = false;
    title = null;
    normixes = null;
    bitsets = null;    
    vertices = null;
    offsetVertices = null;
    polygonIndexes = null;
    data1 = null;
    data2 = null;
    
    this.meshType = meshType;
  }

  public void initialize(int lighting, Point3f[] vertices) {
    if (vertices == null)
      vertices = this.vertices;
    Vector3f[] normals = getNormals(vertices);
    normixes = new short[normixCount];
    isTwoSided = (lighting == JmolConstants.FULLYLIT);
    if (haveXyPoints)
      for (int i = normixCount; --i >= 0;)
        normixes[i] = Graphics3D.NORMIX_NULL;
    else
      for (int i = normixCount; --i >= 0;)
        normixes[i] = g3d.getNormix(normals[i]);
    this.lighting = JmolConstants.FRONTLIT;
    if (insideOut)
      invertNormixes();
    setLighting(lighting);
  }

  public Vector3f[] getNormals(Point3f[] vertices) {
    normixCount = (isPolygonSet ? polygonCount : vertexCount);
    Vector3f[] normals = new Vector3f[normixCount];
    for (int i = normixCount; --i >= 0;)
      normals[i] = new Vector3f();
    sumVertexNormals(vertices, normals);
    if (!isPolygonSet)
      for (int i = normixCount; --i >= 0;)
        normals[i].normalize();
    return normals;
  }
  
  public void setLighting(int lighting) {
    if (lighting == this.lighting)
      return;
    flipLighting(this.lighting);
    flipLighting(this.lighting = lighting);
  }
  
  private void flipLighting(int lighting) {
    if (lighting == JmolConstants.FULLYLIT)
      for (int i = normixCount; --i >= 0;)
        normixes[i] = (short)~normixes[i];
    else if ((lighting == JmolConstants.FRONTLIT) == insideOut)
      invertNormixes();
  }

  private void invertNormixes() {
    for (int i = normixCount; --i >= 0;)
      normixes[i] = g3d.getInverseNormix(normixes[i]);
  }

  public void setTranslucent(boolean isTranslucent, float iLevel) {
    colix = Graphics3D.getColixTranslucent(colix, isTranslucent, iLevel);
  }

  public final Vector3f vAB = new Vector3f();
  public final Vector3f vAC = new Vector3f();
  public final Vector3f vTemp = new Vector3f();

  public Vector data1;
  public Vector data2;
  
  protected void sumVertexNormals(Point3f[] vertices, Vector3f[] normals) {
    
    int adjustment = checkByteCount;
    for (int i = polygonCount; --i >= 0;) {
      int[] pi = polygonIndexes[i];
      try {
        if (pi != null) {
          Measure.calcNormalizedNormal(vertices[pi[0]], vertices[pi[1]],
              vertices[pi[2]], vTemp, vAB, vAC);
          if (isPolygonSet) {
            normals[i].set(vTemp);
            continue;
          }
          float l = vTemp.length();
          if (l > 0.9 && l < 1.1) 
            for (int j = pi.length - adjustment; --j >= 0;) {
              int k = pi[j];
              normals[k].add(vTemp);
            }
        }
      } catch (Exception e) {
      }
    }
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

  public Point3f[] getOffsetVertices(Point4f thePlane) {
    if (offsetVertices != null)
      return offsetVertices;
    offsetVertices = new Point3f[vertexCount];
    for (int i = 0; i < vertexCount; i++)
      offsetVertices[i] = new Point3f(vertices[i]);
    Vector3f normal = null;
    float val = 0;
    if (scale3d != 0 && vertexValues != null && thePlane != null) {
        normal = new Vector3f(thePlane.x, thePlane.y, thePlane.z);
        normal.normalize();
        normal.scale(scale3d);
    }
    for (int i = 0; i < vertexCount; i++) {
      if (vertexValues != null && Float.isNaN(val = vertexValues[i]))
        continue;
      Point3f pt = offsetVertices[i];
      if (ptOffset != null)
        pt.add(ptOffset);
      if (normal != null && val != 0)
        pt.scaleAdd(val, normal, pt);
    }
    initialize(lighting, offsetVertices);
    return offsetVertices;
  }
}
