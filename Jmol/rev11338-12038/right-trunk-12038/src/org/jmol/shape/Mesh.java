

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
import javax.vecmath.Vector3f;

public class Mesh extends MeshSurface {
  
  public final static String PREVIOUS_MESH_ID = "+PREVIOUS_MESH+";
  private JmolRendererInterface g3d;
  
  public String[] title;
  
  public short[] normixes;
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
    frontOnly = false;
    title = null;
    normixes = null;
    bitsets = null;    
    vertices = null;
    polygonIndexes = null;
    data1 = null;
    data2 = null;
    
    this.meshType = meshType;
  }

  public void initialize(int lighting) {
    Vector3f[] normals = getVertexNormals();
    normixes = new short[vertexCount];
    initializeNormixes(lighting, normals);
  }

  public Vector3f[] getVertexNormals() {
    Vector3f[] normals = new Vector3f[vertexCount];
    for (int i = vertexCount; --i >= 0;)
      normals[i] = new Vector3f();
    sumVertexNormals(normals);
    for (int i = vertexCount; --i >= 0;)
      normals[i].normalize();
    return normals;
  }
  
  public void initializeNormixes(int lighting, Vector3f[] normals) {
    isTwoSided = (lighting == JmolConstants.FULLYLIT);
    normixes = new short[vertexCount];
    if (haveXyPoints)
      for (int i = vertexCount; --i >= 0;)
        normixes[i] = Graphics3D.NORMIX_NULL;
    else
      for (int i = vertexCount; --i >= 0;)
        normixes[i] = g3d.getNormix(normals[i]);
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
  
  protected void sumVertexNormals(Vector3f[] normals) {
    
    int adjustment = (haveCheckByte ? 2 : 0);
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
              normals[k].add(vTemp);
            }
        }
      } catch (Exception e) {
      }
    }
  }

  public String getState(String type) {
    StringBuffer s = new StringBuffer(type);
    if (!type.equals("molecularOrbital"))
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
