

package org.jmol.export;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Point3i;
import javax.vecmath.Vector3f;

import org.jmol.g3d.Graphics3D;
import org.jmol.modelset.Atom;



abstract class __RayTracerExporter extends ___Exporter {

  protected boolean isSlabEnabled;
  protected float zoom;
  protected int minScreenDimension;
  

  public __RayTracerExporter() {
    isCartesianExport = false;
  }

  protected void outputVertex(Point3f pt, Point3f offset) {
    setTempVertex(pt, offset, tempP1);
    viewer.transformPoint(tempP1, tempP1);
    output(tempP1);
  }

  abstract protected void outputCircle(int x, int y, int z, float radius, short colix,
                                       boolean doFill);

  abstract protected void outputCylinder(Point3f screenA, Point3f screenB, float radius,
                                         short colix, boolean withCaps);
             
  abstract protected void outputCylinderConical(Point3f screenA,
                                                Point3f screenB, float radius1,
                                                float radius2, short colix);

  abstract protected void outputEllipsoid(Point3f center, float radius, double[] coef, short colix);
  
  abstract protected void outputSphere(float x, float y, float z, float radius,
                                    short colix);
  
  abstract protected void outputTextPixel(int x, int y, int z, int argb);

  abstract protected void outputTriangle(Point3f ptA, Point3f ptB, Point3f ptC, short colix);

  abstract protected void outputCone(Point3f screenBase, Point3f screenTip, float radius,
                                     short colix);

  abstract protected void outputCircleScreened(int x, int y, int z, float radius, short colix);
  
  protected Point3f getScreenNormal(Point3f pt, Vector3f normal, float factor) {
    if (Float.isNaN(normal.x)) {
      tempP3.set(0, 0, 0);
      return tempP3;
    }
    tempP1.set(pt);
    tempP1.add(normal);
    viewer.transformPoint(pt, tempP2);
    viewer.transformPoint(tempP1, tempP3);
    tempP3.sub(tempP2);
    tempP3.scale(factor);
    return tempP3;
  }

  protected void outputHeader() {
    nBytes = 0;
    isSlabEnabled = viewer.getSlabEnabled();
    zoom = viewer.getRotationRadius() * 2;
    zoom *= 1.1f; 
    zoom /= viewer.getZoomPercentFloat() / 100f;
    minScreenDimension = Math.min(screenWidth, screenHeight);
    
  }

  
  
  void drawAtom(Atom atom) {
    outputSphere(atom.screenX, atom.screenY, atom.screenZ,
        atom.screenDiameter / 2f, atom.getColix());
  }

  void drawCircle(int x, int y, int z,
                         int diameter, short colix, boolean doFill) {
    
    float radius = diameter / 2f;
    outputCircle(x, y, z, radius, colix, doFill);
  }

  void drawPixel(short colix, int x, int y, int z) {
    
    outputSphere(x, y, z, 0.75f, colix);
  }

  void drawTextPixel(int argb, int x, int y, int z) {
    outputTextPixel(x, y, z, argb);
  }
    
  void fillCone(short colix, byte endcap, int diameter, Point3f screenBase,
                Point3f screenTip) {
    outputCone(screenBase, screenTip, diameter / 2f, colix);
  }

  void fillCylinder(Point3f screenA, Point3f screenB, short colix1,
                           short colix2, byte endcaps, int madBond,
                           int bondOrder) {
    
    if (colix1 == colix2) {
      fillConicalCylinder(screenA, screenB, madBond, colix1, endcaps);
    } else {
      tempV2.set(screenB);
      tempV2.add(screenA);
      tempV2.scale(0.5f);
      tempP1.set(tempV2);
      fillConicalCylinder(screenA, tempP1, madBond, colix1, endcaps);
      fillConicalCylinder(tempP1, screenB, madBond, colix2, endcaps);
    }
    if (endcaps != Graphics3D.ENDCAPS_SPHERICAL)
      return;
    
    float radius = viewer.scaleToScreen((int) screenA.z, madBond) / 2f;
    if (radius <= 1)
      return;
    outputSphere(screenA.x, screenA.y, screenA.z, radius, colix1);
    radius = viewer.scaleToScreen((int) screenB.z, madBond) / 2f;
    if (radius <= 1)
      return;
    outputSphere(screenB.x, screenB.y, screenB.z, radius, colix2);

  }

  protected void fillConicalCylinder(Point3f screenA, Point3f screenB,
                                    int madBond, short colix, byte endcaps) {
    float radius1 = viewer.scaleToScreen((int) screenA.z, madBond) / 2f;
    if (radius1 == 0)
      return;
    if (radius1 < 1)
      radius1 = 1;
    if (screenA.distance(screenB) == 0) {
      outputSphere(screenA.x, screenA.y, screenA.z, radius1, colix);
      return;
    }
    float radius2 = viewer.scaleToScreen((int) screenB.z, madBond) / 2f;
    if (radius2 == 0)
      return;
    if (radius2 < 1)
      radius2 = 1;
    outputCylinderConical(screenA, screenB, radius1, radius2, colix);
  }

  void fillCylinder(short colix, byte endcaps, int diameter,
                           Point3f screenA, Point3f screenB) {
    float radius = diameter / 2f;
    if (radius == 0)
      return;
    if (radius < 1)
      radius = 1;
    if (screenA.distance(screenB) == 0) {
      outputSphere(screenA.x, screenA.y, screenA.z, radius, colix);
      return;
    }
    outputCylinder(screenA, screenB, radius, colix, endcaps == Graphics3D.ENDCAPS_FLAT);
    if (endcaps != Graphics3D.ENDCAPS_SPHERICAL || radius <= 1)
      return;
    outputSphere(screenA.x, screenA.y, screenA.z, radius, colix);
    outputSphere(screenB.x, screenB.y, screenB.z, radius, colix);

  }

  void fillSphere(short colix, int diameter, Point3f pt) {
    outputSphere(pt.x, pt.y, pt.z, diameter / 2f, colix);
  }
  
  void fillScreenedCircle(short colix, int diameter, int x, int y, int z) {
    outputCircleScreened(x, y, z, diameter / 2f, colix);
  }

  void fillTriangle(short colix, Point3f ptA, Point3f ptB, Point3f ptC) {
    outputTriangle(ptA, ptB, ptC, colix);
  }

  void fillEllipsoid(Point3f center, Point3f[] points, short colix, int x,
                       int y, int z, int diameter, Matrix3f toEllipsoidal,
                       double[] coef, Matrix4f deriv, Point3i[] octantPoints) {
    float radius = diameter / 2f;
    if (radius == 0)
      return;
    if (radius < 1)
      radius = 1;
    outputEllipsoid(center, radius, coef, colix); 
  }

}
