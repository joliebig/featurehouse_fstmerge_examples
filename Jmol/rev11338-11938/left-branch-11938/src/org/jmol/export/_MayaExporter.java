

package org.jmol.export;

import java.awt.Image;
import java.util.BitSet;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Point3i;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

import org.jmol.g3d.Font3D;
import org.jmol.modelset.Atom;
import org.jmol.shape.Text;

public class _MayaExporter extends _Exporter {

  private int nBalls = 0;
  private int nCyl = 0;
  private String name;
  private String id;

  public _MayaExporter() {
    use2dBondOrderCalculation = false;
    canDoTriangles = false;
    isCartesianExport = true;
  }
  
 public void getHeader() {
    output.append("//  Maya ASCII 8.5 scene\n");
    output.append("//  Name: ball_stripped.ma\n");
    
    output.append("//  Last modified: Thu, Jul 5, 2007 10:25:55 PM\n");
    output.append("//  Codeset: UTF-8\n");
    output.append("requires maya \"8.5\";\n");
    output.append("currentUnit -l centimeter -a degree -t film;\n");
    output.append("fileInfo \"application\" \"maya\";\n");
    output.append("fileInfo \"product\" \"Maya Unlimited 8.5\";\n");
    output.append("fileInfo \"version\" \"8.5\";\n");
    output.append("fileInfo \"cutIdentifier\" \"200612170012-692032\";\n");
    output.append("fileInfo \"osv\" \"Mac OS X 10.4.9\";  \n");
  }

  public void getFooter() {
    
  }

  public void renderAtom(Atom atom, short colix) {
    
    nBalls++;
    name = "nurbsSphere" + nBalls;
    id = "nurbsSphereShape" + nBalls;

    output.append("createNode transform -n \"" + name + "\";\n");
    setAttr("t", atom);
    output.append("createNode nurbsSurface -n \"" + id + "\" -p \"" + name
        + "\";\n");
    addAttr();
    output.append("createNode makeNurbSphere -n \"make" + name + "\";\n");
    output.append(" setAttr \".ax\" -type \"double3\" 0 1 0;\n");
    setAttr("r", atom.getMadAtom() / 2000f);
    setAttr("s", 4);
    setAttr("nsp", 3);
    addConnect();
  }

  public void fillCylinder(Point3f atom1, Point3f atom2, short colix1, short colix2,
                      byte endcaps, int madBond, int bondOrder) {
    
    if (colix1 == colix2) {
      renderCylinder(atom1, atom2, colix1, endcaps, madBond);
      return;
    }
    tempV2.set(atom2);
    tempV2.add(atom1);
    tempV2.scale(0.5f);
    tempP1.set(tempV2);
    renderCylinder(atom1, tempP1, colix1, endcaps, madBond);
    renderCylinder(tempP1, atom2, colix2, endcaps, madBond);
  }

  public void renderCylinder(Point3f pt1, Point3f pt2, short colix, byte endcaps,
                      int madBond) {
    nCyl++;
    name = "nurbsCylinder" + nCyl;
    id = "nurbsCylinderShape" + nCyl;
    output.append(" createNode transform -n \"" + name + "\";\n");
    float length = pt1.distance(pt2);
    tempV1.set(pt2);
    tempV1.add(pt1);
    tempV1.scale(0.5f);
    setAttr("t", tempV1);
    tempV1.sub(pt1);
    setAttr("r", getRotation(tempV1));
    output.append(" createNode nurbsSurface -n \"" + id + "\" -p \"" + name
        + "\";\n");
    addAttr();
    output.append("createNode makeNurbCylinder -n \"make" + name + "\";\n");
    output.append(" setAttr \".ax\" -type \"double3\" 0 1 0;\n");
    float radius = madBond / 2000f;
    setAttr("r", radius);
    setAttr("s", 4);
    setAttr("hr", length / radius);
    addConnect();
  }

  private void setAttr(String attr, float val) {
    output.append(" setAttr \"." + attr + "\" " + val + ";\n");
  }

  private void setAttr(String attr, int val) {
    output.append(" setAttr \"." + attr + "\" " + val + ";\n");
  }

  private void setAttr(String attr, Tuple3f pt) {
    output.append(" setAttr \"." + attr + "\" -type \"double3\" " + pt.x + " "
        + pt.y + " " + pt.z + ";\n");
  }

  private void addAttr() {
    output.append(" setAttr -k off \".v\";\n");
    output.append(" setAttr \".vir\" yes;\n");
    output.append(" setAttr \".vif\" yes;\n");
    output.append(" setAttr \".tw\" yes;\n");
    output.append(" setAttr \".covm[0]\"  0 1 1;\n");
    output.append(" setAttr \".cdvm[0]\"  0 1 1;\n");
  }

  private void addConnect() {
    output.append(" connectAttr \"make" + name + ".os\" \"" + id + ".cr\";\n");
    output.append("connectAttr \"" + id
        + ".iog\" \":initialShadingGroup.dsm\" -na;\n");
  }

  
  
  public void renderIsosurface(Point3f[] vertices, short colix,
                               short[] colixes, Vector3f[] normals,
                               int[][] indices, BitSet bsFaces,
                               int nVertices, int faceVertexMax, 
                               short[] polygonColixes, int nPolygons) {
  }

  public void renderText(Text t) {
  }  

  public void drawString(short colix, String str, Font3D font3d, 
                         int xBaseline, int yBaseline, int z, int zSlab) {
  }

  public void fillCylinder(short colix, byte endcaps, int diameter, 
                           Point3f screenA, Point3f screenB) {
  }

  public void drawCircleCentered(short colix, int diameter, int x, int y, int z, boolean doFill) {
    
  }

  public void fillScreenedCircleCentered(short colixFill, int diameter, int x,
                                         int y, int z) {
    
  }
  
  public void drawPixel(short colix, int x, int y, int z) {
    
  }

  public void drawTextPixel(int argb, int x, int y, int z) {
    
  }

  public void fillTriangle(short colix, Point3f ptA, Point3f ptB, Point3f ptC) {
    
  }

  public void fillCone(short colix, byte endcap, int diameter, 
                Point3f screenBase, Point3f screenTip) {
    
  }
  
  public void fillSphereCentered(short colix, int diameter, Point3f pt) {
    
  }

  public void plotText(int x, int y, int z, short colix, 
                       String text, Font3D font3d) {    
  }
  
  public void plotImage(int x, int y, int z, Image image, short bgcolix, int width, int height) {
    
    
  }

  public void endShapeBuffer() {
    
    
  }

  public void startShapeBuffer(int iShape) {
    
    
  }

  void renderEllipsoid(Point3f center, Point3f[] points, short colix, int x,
                       int y, int z, int diameter, Matrix3f toEllipsoidal,
                       double[] coef, Matrix4f deriv, Point3i[] octantPoints) {
    
    
  }

}
