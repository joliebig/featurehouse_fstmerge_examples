

package org.jmol.export;

import java.awt.Image;
import java.io.IOException;
import java.util.BitSet;
import java.util.Hashtable;
import java.util.Vector;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Point3i;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

import org.jmol.g3d.Font3D;
import org.jmol.g3d.Graphics3D;
import org.jmol.modelset.Atom;
import org.jmol.shape.Text;
import org.jmol.util.TextFormat;
import org.jmol.viewer.JmolConstants;
import org.jmol.viewer.Viewer;



public class _PovrayExporter extends _Exporter {

  private int nBytes;
  private boolean isSlabEnabled;

  public _PovrayExporter() {
    use2dBondOrderCalculation = true;
    canDoTriangles = true;
    isCartesianExport = false;
  }

  private void output(String data) {
    nBytes += data.length();
    try {
      bw.write(data);
    } catch (IOException e) {
      
    }
  }

  public String finalizeOutput() {
    super.finalizeOutput();
    return getAuxiliaryFileData();
  }

  public void getHeader() {
    nBytes = 0;
    isSlabEnabled = viewer.getSlabEnabled();
    float zoom = viewer.getRotationRadius() * 2;
    zoom *= 1.1f; 
    zoom /= viewer.getZoomPercentFloat() / 100f;
    int minScreenDimension = Math.min(screenWidth, screenHeight);
    output("// ******************************************************\n");
    output("// Created by Jmol " + Viewer.getJmolVersion() + "\n");
    output("//\n");
    output("// This script was generated on " + getExportDate() + "\n");
    output("// ******************************************************\n");
    output("\n/* " + JmolConstants.EMBEDDED_SCRIPT_TAG + " \n");
    output(TextFormat.simpleReplace(viewer.getSavedState("_Export"),"/*file*/", ""));
    output("\n*/\n");
    output("\n");
    output("// ******************************************************\n");
    output("// Declare the resolution, camera, and light sources.\n");
    output("// ******************************************************\n");
    output("\n");
    output("// NOTE: if you plan to render at a different resolution,\n");
    output("// be sure to update the following two lines to maintain\n");
    output("// the correct aspect ratio.\n" + "\n");
    output("#declare Width = " + screenWidth + ";\n");
    output("#declare Height = " + screenHeight + ";\n");
    output("#declare minScreenDimension = " + minScreenDimension + ";\n");
    output("#declare showAtoms = true;\n");
    output("#declare showBonds = true;\n");
    output("#declare noShadows = true;\n");
    output("camera{\n");
    output("  orthographic\n");
    output("  location < " + screenWidth / 2f + ", " + screenHeight / 2f
        + ", 0>\n" + "\n");
    output("  // Negative right for a right hand coordinate system.\n");
    output("\n");
    output("  sky < 0, -1, 0 >\n");
    output("  right < -" + screenWidth + ", 0, 0>\n");
    output("  up < 0, " + screenHeight + ", 0 >\n");
    output("  look_at < " + screenWidth / 2f + ", " + screenHeight / 2f
        + ", 1000 >\n");
    output("}\n");
    output("\n");

    output("background { color rgb <" + 
        rgbFractionalFromColix(viewer.getObjectColix(0), ',')
        + "> }\n");
    output("\n");

    

    tempP1.set(Graphics3D.getLightSource());
    output("// " + tempP1 + " \n");
    float distance = Math.max(screenWidth, screenHeight);
    output("light_source { <" + tempP1.x * distance + "," + tempP1.y * distance
        + ", " + (-1 * tempP1.z * distance) + "> " + " rgb <0.6,0.6,0.6> }\n");
    output("\n");
    output("\n");

    output("// ***********************************************\n");
    output("// macros for common shapes\n");
    output("// ***********************************************\n");
    output("\n");

    writeMacros();
  }

  public void getFooter() {
    
  }

  private void writeMacros() {
    output("#default { finish {\n" + "  ambient "
        + (float) Graphics3D.getAmbientPercent() / 100f + "\n" + "  diffuse "
        + (float) Graphics3D.getDiffusePercent() / 100f + "\n" + "  specular "
        + (float) Graphics3D.getSpecularPercent() / 100f + "\n"
        + "  roughness .00001\n  metallic\n  phong 0.9\n  phong_size 120\n}}"
        + "\n\n");

    output("#macro check_shadow()\n"
        + " #if (noShadows)\n"
        + "  no_shadow \n"
        + " #end\n"
        + "#end\n\n");

    output("#declare slabZ = " + slabZ + ";\n"
        + "#declare depthZ = " + depthZ + ";\n"
        + "#declare dzSlab = 10;\n"
        + "#declare dzDepth = dzSlab;\n"
        + "#declare dzStep = 0.001;\n\n");
    
    output("#macro clip()\n"
        + "  clipped_by { box {<0,0,slabZ>,<Width,Height,depthZ>} }\n"
        + "#end\n\n");

    output("#macro circleCap(Z,RADIUS,R,G,B,T)\n"
        + "// cap for lower clip\n"
        + " #local cutDiff = Z - slabZ;\n"
        + " #local cutRadius2 = (RADIUS*RADIUS) - (cutDiff*cutDiff);\n"
        + " #if (cutRadius2 > 0)\n"
        + "  #local cutRadius = sqrt(cutRadius2);\n"
        + "  #if (dzSlab > 0)\n" 
        + "   #declare dzSlab = dzSlab - dzStep;\n"
        + "  #end\n"
        + "  cylinder{<X,Y,slabZ-dzSlab>,"
        + "<X,Y,(slabZ+1)>,cutRadius\n"
        + "   pigment{rgbt<R,G,B,T>}\n"
        + "   translucentFinish(T)\n"
        + "   check_shadow()}\n"
        + " #end\n"
        + "// cap for upper clip\n"
        + " #declare cutDiff = Z - depthZ;\n"
        + " #declare cutRadius2 = (RADIUS*RADIUS) - (cutDiff*cutDiff);\n"
        + " #if (cutRadius2 > 0)\n"
        + "  #local cutRadius = sqrt(cutRadius2);\n"
        + "  #if (dzDepth > 0)\n"
        + "   #declare dzDepth = dzDepth - dzStep;\n"
        + "  #end\n"
        + "  cylinder{<X,Y,depthZ+dzDepth>,"
        + "<X,Y,(depthZ-1)>,cutRadius\n"
        + "   pigment{rgbt<R,G,B,T>}\n"
        + "   translucentFinish(T)\n"
        + "   check_shadow()}\n"
        + " #end\n"
        + "#end\n\n");

    writeMacrosFinish();
    writeMacrosAtom();
    writeMacrosBond();
    writeMacrosTriangle();
    writeMacrosTextPixel();
    
  }

  private void writeMacrosFinish() {
    output("#macro translucentFinish(T)\n"
        + " #local shineFactor = T;\n"
        + " #if (T <= 0.25)\n"
        + "  #declare shineFactor = (1.0-4*T);\n"
        + " #end\n"
        + " #if (T > 0.25)\n"
        + "  #declare shineFactor = 0;\n"
        + " #end\n"
        + " finish {\n" + "  ambient "
        + (float) Graphics3D.getAmbientPercent() / 100f + "\n" + "  diffuse "
        + (float) Graphics3D.getDiffusePercent() / 100f + "\n" + "  specular "
        + (float) Graphics3D.getSpecularPercent() / 100f + "\n"
        + "  roughness .00001\n"  
        + "  metallic shineFactor\n"  
        + "  phong 0.9*shineFactor\n"  
        + "  phong_size 120*shineFactor\n}"
        + "#end\n\n");
  }


  private void writeMacrosAtom() {
    output("#macro a(X,Y,Z,RADIUS,R,G,B,T)\n" 
        + " sphere{<X,Y,Z>,RADIUS\n"
        + "  pigment{rgbt<R,G,B,T>}\n"
        + "  translucentFinish(T)\n"
        + "  clip()\n"
        + "  check_shadow()}\n"
        + (isSlabEnabled? " circleCap(Z,RADIUS,R,G,B,T)\n" : "")
        + "#end\n\n");

    output("#macro q(XX,YY,ZZ,XY,XZ,YZ,X,Y,Z,J,R,G,B,T)\n" 
        + " quadric{<XX,YY,ZZ>,<XY,XZ,YZ>,<X,Y,Z>,J\n"
        + "  pigment{rgbt<R,G,B,T>}\n"
        + "  translucentFinish(T)\n"
        + "  clip()\n"
        + "  check_shadow()}\n"

        + "#end\n\n");



  }

  private void writeMacrosBond() {
    
    
    
    output("#macro b(X1,Y1,Z1,RADIUS1,X2,Y2,Z2,RADIUS2,R,G,B,T)\n"
        + " cone{<X1,Y1,Z1>,RADIUS1,<X2,Y2,Z2>,RADIUS2\n"
        + "  pigment{rgbt<R,G,B,T>}\n"
        + "  translucentFinish(T)\n"
        + "  clip()\n"
        + "  check_shadow()}\n" 
        + "#end\n\n");
    
    output("#macro c(X1,Y1,Z1,RADIUS1,X2,Y2,Z2,RADIUS2,R,G,B,T)\n"
        + " cone{<X1,Y1,Z1>,RADIUS1,<X2,Y2,Z2>,RADIUS2 open\n"
        + "  pigment{rgbt<R,G,B,T>}\n"
        + "  translucentFinish(T)\n"
        + "  clip()\n"
        + "  check_shadow()}\n" 
        + "#end\n\n");
    

  }

  private void writeMacrosTriangle() {
    output("#macro r(X1,Y1,Z1,X2,Y2,Z2,X3,Y3,Z3,R,G,B,T)\n"
        + " triangle{<X1,Y1,Z1>,<X2,Y2,Z2>,<X3,Y3,Z3>\n"
        + "  pigment{rgbt<R,G,B,T>}\n"
        + "  translucentFinish(T)\n"
        + "  clip()\n"
        + "  check_shadow()}\n" 
        + "#end\n\n");
  }

  private void writeMacrosTextPixel() {
    output("#macro p(X,Y,Z,R,G,B)\n" 
        + " box{<X,Y,Z>,<X+1,Y+1,Z+1>\n"
        + "  pigment{rgb<R,G,B>}\n"
        + "  clip()\n"
        + "  check_shadow()}\n" 
        + "#end\n\n");
  }

  

  private String triad(Tuple3f pt) {
    if (Float.isNaN(pt.x))
      return "0,0,0";
    return pt.x + "," + pt.y + "," + pt.z;
  }

  private String triad(int[] i) {
    return i[0] + "," + i[1] + "," + i[2];
  }

  private String color4(short colix) {
    return rgbFractionalFromColix(colix, ',') + ","
        + translucencyFractionalFromColix(colix);
  }

  private String getAuxiliaryFileData() {
    return "; Created by: Jmol " + Viewer.getJmolVersion()
        + "\n; Creation date: " + getExportDate() 
        + "\n; File created: "  + fileName + " (" + nBytes + " bytes)\n\n" 
        + (commandLineOptions != null ? commandLineOptions :
          "\n; Jmol state: (embedded in input file)" 
        + "\nInput_File_Name=" + fileName 
        + "\nOutput_to_File=true"
        + "\nOutput_File_Type=N"
        + "\nOutput_File_Name=" + fileName + ".png" 
        + "\nWidth=" + screenWidth 
        + "\nHeight=" + screenHeight
        + "\nAntialias=true"
        + "\nAntialias_Threshold=0.1" 
        + "\nDisplay=true"
        + "\nPause_When_Done=true"
        + "\nWarning_Level=5"
        + "\nVerbose=false" + "\n");

  }

  public void renderAtom(Atom atom, short colix) {
    fillSphereCentered(atom.screenDiameter, atom.screenX, atom.screenY,
        atom.screenZ, colix);
  }

  public void fillCylinder(Point3f atom1, Point3f atom2, short colix1,
                           short colix2, byte endcaps, int madBond,
                           int bondOrder) {

    if (colix1 == colix2) {
      renderJoint(atom1, colix1, endcaps, madBond);
      renderCylinder(atom1, atom2, colix1, endcaps, madBond);
      renderJoint(atom2, colix2, endcaps, madBond);
      return;
    }

    tempV2.set(atom2);
    tempV2.add(atom1);
    tempV2.scale(0.5f);
    tempP1.set(tempV2);
    renderJoint(atom1, colix1, endcaps, madBond);
    renderCylinder(atom1, tempP1, colix1, endcaps, madBond);
    renderCylinder(tempP1, atom2, colix2, endcaps, madBond);
    renderJoint(atom2, colix2, endcaps, madBond);
  }

  public void renderCylinder(Point3f pt1, Point3f pt2, short colix,
                             byte endcaps, int madBond) {
    short d = viewer.scaleToScreen((int) pt1.z, madBond);
    if (pt1.distance(pt2) == 0) {
      fillSphereCentered(colix, d, pt1);
      return;
    }
    
    
    
    float radius1 = d / 2f;
    float radius2 = viewer.scaleToScreen((int) pt2.z, madBond / 2);

    

    output("b(" + triad(pt1) + "," + radius1 + ","
        + triad(pt2) + "," + radius2 + "," + color4(colix) + ")\n");
  }

  private void renderJoint(Point3f pt, short colix, byte endcaps, int madBond) {
    
    
    if (endcaps == Graphics3D.ENDCAPS_SPHERICAL) {
      float radius = viewer.scaleToScreen((int) pt.z, madBond / 2);
      output("a(" + triad(pt) + "," + radius + "," + color4(colix) + ")\n");
    }
  }

  public void renderIsosurface(Point3f[] vertices, short colix,
                               short[] colixes, Vector3f[] normals,
                               int[][] indices, BitSet bsFaces, int nVertices,
                               int faceVertexMax, short[] polygonColixes, int nPolygons) {
    if (nVertices == 0)
      return;
    int nFaces = 0;
    for (int i = nPolygons; --i >= 0;)
      if (bsFaces.get(i))
        nFaces += (faceVertexMax == 4 && indices[i].length == 4 ? 2 : 1);
    if (nFaces == 0)
      return;
    
    if (polygonColixes != null) {
      for (int i = nPolygons; --i >= 0;) {
        if (!bsFaces.get(i))
          continue;
        
        
        output("polygon { 4\n"); 
        for (int j = 0; j <= 3; j++) {
          viewer.transformPoint(vertices[indices[i][j % 3]], tempP1);
          output(", <" + triad(tempP1) + ">");
        }
        output("\n");
        output("pigment{rgbt<" + color4(colix = polygonColixes[i]) + ">}\n");
        output("  translucentFinish(" + translucencyFractionalFromColix(colix)
            + ")\n");
        output("  check_shadow()\n");
        output("  clip()\n");
        output("}\n");
      }
      return;
    }

    output("mesh2 {\n");
    output("vertex_vectors { " + nVertices);
    for (int i = 0; i < nVertices; i++) {
      
      
      viewer.transformPoint(vertices[i], tempP1);
      output(", <" + triad(tempP1) + ">");
      output(" //" + i + "\n");

    }
    output("\n}\n");

    boolean haveNormals = (normals != null);
    if (haveNormals) {
      output("normal_vectors { " + nVertices);
      for (int i = 0; i < nVertices; i++) {
        
        
        output(", <" + triad(getNormal(vertices[i], normals[i])) + ">");
        output(" //" + i + "\n");
      }
      output("\n}\n");
    }

    Hashtable htColixes = new Hashtable();
    if (colixes != null) {
      Vector list = getColorList(0, colixes, nVertices, null, htColixes);
      int nColix = list.size();
      output("texture_list { " + nColix);
      
      String finish = ">}" + " translucentFinish("
        + translucencyFractionalFromColix(colixes[0]) + ")}";
      for (int i = 0; i < nColix; i++)
        output("\n, texture{pigment{rgbt<" + color4(((Short)list.get(i)).shortValue()) + finish);
      output("\n}\n");
    }
    output("face_indices { " + nFaces);
    
    for (int i = nPolygons; --i >= 0;) {
      if (!bsFaces.get(i))
        continue;
      
      
      output(", <" + triad(indices[i]) + ">");
      if (colixes != null) {
        output("," + htColixes.get("" + colixes[indices[i][0]]));
        output("," + htColixes.get("" + colixes[indices[i][1]]));
        output("," + htColixes.get("" + colixes[indices[i][2]]));
      }
      if (faceVertexMax == 4 && indices[i].length == 4) {
        output(", <" + indices[i][0] + "," + indices[i][2] + "," + indices[i][3] + ">");
        if (colixes != null) {
          output("," + htColixes.get("" + colixes[indices[i][0]]));
          output("," + htColixes.get("" + colixes[indices[i][2]]));
          output("," + htColixes.get("" + colixes[indices[i][3]]));
        }
      }
      output("\n");
    }
    output("\n}\n");

    if (colixes == null) {
      output("pigment{rgbt<" + color4(colix) + ">}\n");
      output("  translucentFinish(" + translucencyFractionalFromColix(colix)
          + ")\n");
    }
    output("  check_shadow()\n");
    output("  clip()\n");
    output("}\n");

    

  }

  private Point3f getNormal(Point3f pt, Vector3f normal) {
    if (Float.isNaN(normal.x)) {
      tempP3.set(0, 0, 0);
      return tempP3;
    }
    tempP1.set(pt);
    tempP1.add(normal);
    viewer.transformPoint(pt, tempP2);
    viewer.transformPoint(tempP1, tempP3);
    tempP3.sub(tempP2);
    return tempP3;
  }

  public void fillCylinder(short colix, byte endcaps, int diameter,
                           Point3f screenA, Point3f screenB) {
    if (screenA.distance(screenB) == 0) {
      fillSphereCentered(diameter, screenA.x, screenA.y, screenA.z, colix);
      return;
    }
    float radius1 = diameter / 2f;
    float radius2 = radius1;
    String color = color4(colix);
    output((endcaps == Graphics3D.ENDCAPS_FLAT ? "b(" : "c(") 
        + triad(screenA) + "," + radius1 + "," + triad(screenB) + ","
        + radius2 + "," + color + ")\n");
    if (endcaps != Graphics3D.ENDCAPS_SPHERICAL)
      return;
    output("a(" + triad(screenA) + "," + radius1 + "," + color + ")\n");
    output("a(" + triad(screenB) + "," + radius2 + "," + color + ")\n");
  }

  public void drawCircleCentered(short colix, int diameter, int x,
                                         int y, int z, boolean doFill) {
    
    float r = diameter / 2.0f;
    output((doFill ? "b(" : "c(") + x + "," + y + "," + z + "," + r + "," 
        + x + "," + y + "," + (z + 1) + "," + (r + 2) + "," 
        + color4(colix) + ")\n");
  }

  public void fillScreenedCircleCentered(short colix, int diameter, int x,
                                         int y, int z) {
    
    float r = diameter / 2.0f;
    output("b(" + x + "," + y + "," + z + "," + r + "," 
        + x + "," + y + "," + (z + 1) + "," + r + "," 
        + rgbFractionalFromColix(colix, ',') + ",0.8)\n");
  }

  public void drawPixel(short colix, int x, int y, int z) {
    
    fillSphereCentered(1.5f, x, y, z, colix);
  }

  public void drawTextPixel(int argb, int x, int y, int z) {
    
    output("p(" + x + "," + y + "," + z + "," + 
        rgbFractionalFromArgb(argb, ',') + ")\n");
  }
  
  public void fillTriangle(short colix, Point3f ptA, Point3f ptB, Point3f ptC) {
    
    output("r(" + triad(ptA) + "," + triad(ptB) + "," + triad(ptC) + ","
        + color4(colix) + ")\n");
  }

  public void fillCone(short colix, byte endcap, int diameter,
                       Point3f screenBase, Point3f screenTip) {
    output("b(" + triad(screenBase) + "," + (diameter / 2f) + ","
        + triad(screenTip) + ",0" + "," + color4(colix) + ")\n");
  }

  public void fillSphereCentered(short colix, int diameter, Point3f pt) {
    
    output("a(" + triad(pt) + "," + (diameter / 2.0f) + "," + color4(colix)
        + ")\n");
  }

  private void fillSphereCentered(float diameter, float x, float y, float z,
                                  short colix) {
   output("a(" + x + "," + y + "," + z + "," + (diameter / 2.0f) + ","
        + color4(colix) + ")\n");
  }

  int nText;
  int nImage;
  public void plotText(int x, int y, int z, short colix,
                       String text, Font3D font3d) {
    
    
    
    output("// start text " + (++nText) + ": " + text + "\n");
    g3d.plotText(x, y, z, g3d.getColorArgbOrGray(colix), text, font3d, jmolRenderer);
    output("// end text " + nText + ": " + text + "\n");
  }

  public void plotImage(int x, int y, int z, Image image, short bgcolix, 
                        int width, int height) {
    output("// start image " + (++nImage) + "\n");
    g3d.plotImage(x, y, z, image, jmolRenderer, bgcolix, width, height);
    output("// end image " + nImage + "\n");
  }
  
  public void renderEllipsoid(Point3f center, Point3f[] points, short colix, 
                              int x, int y, int z, int diameter,
                              Matrix3f toEllipsoidal, double[] coef,
                              Matrix4f deriv, Point3i[] octantPoints) {
    
    String s = coef[0] + "," + coef[1] + "," + coef[2] + "," + coef[3] + ","
        + coef[4] + "," + coef[5] + "," + coef[6] + "," + coef[7] + ","
        + coef[8] + "," + coef[9] + "," + color4(colix);
    output("q(" + s + ")\n");
  }

  
  
  public void renderText(Text t) {
  }

  public void drawString(short colix, String str, Font3D font3d, int xBaseline,
                         int yBaseline, int z, int zSlab) {
  }
  
  public void endShapeBuffer() {
  }

  public void startShapeBuffer(int iShape) {
  }
}
