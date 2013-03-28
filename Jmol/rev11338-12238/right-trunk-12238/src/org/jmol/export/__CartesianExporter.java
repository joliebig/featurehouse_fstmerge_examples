

package org.jmol.export;

import java.awt.Image;
import java.util.BitSet;
import java.util.Hashtable;
import java.util.Vector;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Point3i;
import javax.vecmath.Vector3f;

import org.jmol.g3d.Font3D;
import org.jmol.g3d.Graphics3D;
import org.jmol.modelset.Atom;


abstract public class __CartesianExporter extends ___Exporter {

  public __CartesianExporter() {
    exportType = Graphics3D.EXPORT_CARTESIAN;
    lineWidth = 50;
  }

  private void untransformData(Point3f ptA, Point3f ptB,
                                boolean isCartesian) {
    if (isCartesian) {
      
      tempP1.set(ptA);
      tempP2.set(ptB);
    } else {
      viewer.unTransformPoint(ptA, tempP1);
      viewer.unTransformPoint(ptB, tempP2);
    }
  }

  protected int getCoordinateMap(Point3f[] vertices, int[] coordMap) {
    int n = 0;
    for (int i = 0; i < coordMap.length; i++) {
      if (Float.isNaN(vertices[i].x))
        continue;
      coordMap[i] = n++;
    }      
    return n;
  }

  protected int[] getNormalMap(Vector3f[] normals, int nNormals, Vector vNormals) {
    Hashtable htNormals = new Hashtable();
    int[] normalMap = new int[nNormals];
    for (int i = 0; i < nNormals; i++) {
      String s;
      if (Float.isNaN(normals[i].x))
        continue;
      s = (round(normals[i].x) + " " + round(normals[i].y) + " " + round(normals[i].z) + "\n");
      if (htNormals.containsKey(s)) {
        normalMap[i] = ((Integer) htNormals.get(s)).intValue();
      } else {
        normalMap[i] = vNormals.size();
        vNormals.add(s);
        htNormals.put(s, new Integer(normalMap[i]));
      }
    }
    return normalMap;
  }

  protected void outputIndices(int[][] indices, int[] map, int nPolygons,
                                       BitSet bsFaces, int faceVertexMax) {
    for (int i = nPolygons; --i >= 0;)
      if (bsFaces == null || bsFaces.get(i))
        outputFace(indices[i], map, faceVertexMax);
  }

  

  protected abstract void outputFace(int[] is, int[] coordMap, int faceVertexMax);

  abstract protected void outputCircle(Point3f pt1, Point3f pt2, float radius,
                                       short colix, boolean doFill);

  abstract protected void outputCone(Point3f ptBase, Point3f ptTip,
                                     float radius, short colix);

  abstract protected void outputCylinder(Point3f pt1, Point3f pt2,
                                         short colix1, byte endcaps,
                                         float radius);

  abstract protected void outputEllipsoid(Point3f center, Point3f[] points,
                                          short colix);

  abstract protected void outputSphere(Point3f ptAtom2, float f, short colix);

  abstract protected void outputTextPixel(Point3f pt, int argb);

  abstract protected void outputTriangle(Point3f pt1, Point3f pt2, Point3f pt3,
                                         short colix);

  

  void drawAtom(Atom atom) {
    outputSphere(atom, atom.getMadAtom() / 2000f, atom.getColix());
  }

  void drawCircle(int x, int y, int z, int diameter, short colix, boolean doFill) {
    
    tempP3.set(x, y, z);
    viewer.unTransformPoint(tempP3, tempP1);
    float radius = viewer.unscaleToScreen(z, diameter) / 2;
    tempP3.set(x, y, z + 1);
    viewer.unTransformPoint(tempP3, tempP3);
    outputCircle(tempP1, tempP3, radius, colix, doFill);
  }

  void drawPixel(short colix, int x, int y, int z) {
    tempP3.set(x, y, z);
    viewer.unTransformPoint(tempP3, tempP1);
    outputSphere(tempP1, 0.02f, colix);
  }

  void drawTextPixel(int argb, int x, int y, int z) {
    
    tempP3.set(x, y, z);
    viewer.unTransformPoint(tempP3, tempP1);
    outputTextPixel(tempP1, argb);
  }

  void fillConeScreen(short colix, byte endcap, int screenDiameter, Point3f screenBase,
                Point3f screenTip) {
    viewer.unTransformPoint(screenBase, tempP1);
    viewer.unTransformPoint(screenTip, tempP2);
    float radius = viewer.unscaleToScreen(screenBase.z, screenDiameter) / 2;
    if (radius < 0.05f)
      radius = 0.05f;
    outputCone(tempP1, tempP2, radius, colix);
  }

  void drawCylinder(Point3f ptA, Point3f ptB, short colix1, short colix2,
                    byte endcaps, int mad, int bondOrder) {
    untransformData(ptA, ptB, bondOrder == -1);
    float radius = mad / 2000f;
    if (colix1 == colix2) {
      outputCylinder(tempP1, tempP2, colix1, endcaps, radius);
    } else {
      tempV2.set(tempP2);
      tempV2.add(tempP1);
      tempV2.scale(0.5f);
      tempP3.set(tempV2);
      outputCylinder(tempP1, tempP3, colix1, Graphics3D.ENDCAPS_FLAT, radius);
      outputCylinder(tempP3, tempP2, colix2, Graphics3D.ENDCAPS_FLAT, radius);
      if (endcaps == Graphics3D.ENDCAPS_SPHERICAL) {
        outputSphere(tempP1, radius * 1.01f, colix1);
        outputSphere(tempP2, radius * 1.01f, colix2);
      }
    }
  }

  void fillCylinder(short colix, byte endcaps, int mad,
                    Point3f screenA, Point3f screenB) {
    float radius = mad / 2000f;
    untransformData(screenA, screenB, false);
    outputCylinder(tempP1, tempP2, colix, endcaps, radius);
  }

  void fillCylinderScreen(short colix, byte endcaps, int screenDiameter, Point3f screenA, 
                          Point3f screenB) {
   
  int mad = (int) (viewer.unscaleToScreen((screenA.z + screenB.z) / 2, screenDiameter) * 1000);
  fillCylinder(colix, endcaps, mad, screenA, screenB);
  }

  void fillEllipsoid(Point3f center, Point3f[] points, short colix, int x,
                     int y, int z, int diameter, Matrix3f toEllipsoidal,
                     double[] coef, Matrix4f deriv, Point3i[] octantPoints) {
    outputEllipsoid(center, points, colix);
  }

  void fillScreenedCircle(short colix, int diameter, int x, int y, int z) {
    drawCircle(x, y, z, diameter, colix, false);
    drawCircle(x, y, z, diameter, Graphics3D.getColixTranslucent(colix, true,
        0.5f), true);
  }

  void fillSphere(short colix, int diameter, Point3f pt) {
    viewer.unTransformPoint(pt, tempP1);
    outputSphere(tempP1, viewer.unscaleToScreen(pt.z, diameter) / 2,
        colix);
  }

  void fillTriangle(short colix, Point3f ptA, Point3f ptB, Point3f ptC) {
    outputTriangle(ptA, ptB, ptC, colix);
  }

  void plotImage(int x, int y, int z, Image image, short bgcolix, int width,
                 int height) {
    g3d.plotImage(x, y, z, image, jmolRenderer, bgcolix, width, height);
  }

  void plotText(int x, int y, int z, short colix, String text, Font3D font3d) {
    
    
    
    
    g3d.plotText(x, y, z, g3d.getColorArgbOrGray(colix), text, font3d,
        jmolRenderer);
  }

}
