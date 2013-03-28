

package org.jmol.export;

import java.util.BitSet;
import java.util.Hashtable;
import java.util.Vector;

import javax.vecmath.Point3f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

import org.jmol.g3d.Graphics3D;
import org.jmol.util.TextFormat;
import org.jmol.viewer.JmolConstants;
import org.jmol.viewer.Viewer;



public class _PovrayExporter extends __RayTracerExporter {

  String finalizeOutput() {
    super.finalizeOutput();
    return getAuxiliaryFileData();
  }

  protected void outputHeader() {
    super.outputHeader();
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
        rgbFractionalFromColix(backgroundColix, ',')
        + "> }\n");
    output("\n");

    

    float distance = Math.max(screenWidth, screenHeight);
    output("light_source { <" + lightSource.x * distance + "," + lightSource.y * distance
        + ", " + (-1 * lightSource.z * distance) + "> " + " rgb <0.6,0.6,0.6> }\n");
    output("\n");
    output("\n");

    output("// ***********************************************\n");
    output("// macros for common shapes\n");
    output("// ***********************************************\n");
    output("\n");

    writeMacros();
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

  protected void output(Tuple3f pt) {
    output(", <" + triad(pt) + ">");    
  }
  
  protected void outputCircle(int x, int y, int z, float radius, short colix,
                              boolean doFill) {
    output((doFill ? "b(" : "c(") + x + "," + y + "," + z + "," + radius + ","
        + x + "," + y + "," + (z + 1) + "," + (radius + 2) + ","
        + color4(colix) + ")\n");
  }

  protected void outputCircleScreened(int x, int y, int z, float radius, short colix) {
    
    output("b(" + x + "," + y + "," + z + "," + radius + "," 
        + x + "," + y + "," + (z + 1) + "," + radius + "," 
        + rgbFractionalFromColix(colix, ',') + ",0.8)\n");
  }

  protected void outputComment(String comment) {
    output("// ");
    output(comment);
    output("\n");
  }

  protected void outputCone(Point3f screenBase, Point3f screenTip, float radius,
                            short colix) {
    output("b(" + triad(screenBase) + "," + radius + ","
        + triad(screenTip) + ",0" + "," + color4(colix) + ")\n");
  }

  protected void outputCylinder(Point3f screenA, Point3f screenB, float radius,
                              short colix, boolean withCaps) {
    String color = color4(colix);
    output((withCaps ? "b(" : "c(") 
        + triad(screenA) + "," + radius + "," + triad(screenB) + ","
        + radius + "," + color + ")\n");
  }
  
  protected void outputCylinderConical(Point3f screenA, Point3f screenB,
                                       float radius1, float radius2, short colix) {
    output("b(" + triad(screenA) + "," + radius1 + "," + triad(screenB) + ","
        + radius2 + "," + color4(colix) + ")\n");
  }

  protected void outputEllipsoid(Point3f center, float radius, double[] coef, short colix) {
    
    String s = coef[0] + "," + coef[1] + "," + coef[2] + "," + coef[3] + ","
        + coef[4] + "," + coef[5] + "," + coef[6] + "," + coef[7] + ","
        + coef[8] + "," + coef[9] + "," + color4(colix);
    output("q(" + s + ")\n");
  }

  protected void outputSurface(Point3f[] vertices, Vector3f[] normals,
                                  short[] colixes, int[][] indices, 
                                  short[] polygonColixes,
                                  int nVertices, int nPolygons, int nFaces, BitSet bsFaces,
                                  int faceVertexMax, short colix, Vector colorList, Hashtable htColixes, Point3f offset) {
    if (polygonColixes != null) {
      for (int i = nPolygons; --i >= 0;) {
        if (bsFaces != null && !bsFaces.get(i))
          continue;
        
        
        output("polygon { 4\n"); 
        for (int j = 0; j <= 3; j++)
          outputVertex(vertices[indices[i][j % 3]], offset);
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
    for (int i = 0; i < nVertices; i++)
      outputVertex(vertices[i], offset);
    output("\n}\n");

    boolean haveNormals = (normals != null);
    if (haveNormals) {
      output("normal_vectors { " + nVertices);
      for (int i = 0; i < nVertices; i++) {
        setTempVertex(vertices[i], offset, tempP1);
        output(getScreenNormal(tempP1, normals[i], 1));
        output("\n");
      }
      output("\n}\n");
    }

    if (colixes != null) {
      int nColix = colorList.size();
      output("texture_list { " + nColix);
      
      String finish = ">}" + " translucentFinish("
        + translucencyFractionalFromColix(colixes[0]) + ")}";
      for (int i = 0; i < nColix; i++)
        output("\n, texture{pigment{rgbt<" + color4(((Short)colorList.get(i)).shortValue()) + finish);
      output("\n}\n");
    }
    output("face_indices { " + nFaces);
    for (int i = nPolygons; --i >= 0;) {
      if (bsFaces != null && !bsFaces.get(i))
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

  protected void outputSphere(float x, float y, float z, float radius,
                                  short colix) {
   output("a(" + x + "," + y + "," + z + "," + radius + ","
        + color4(colix) + ")\n");
  }
  
  protected void outputTextPixel(int x, int y, int z, int argb) {
    
    output("p(" + x + "," + y + "," + z + "," + 
        rgbFractionalFromArgb(argb, ',') + ")\n");
  }
  
  protected void outputTriangle(Point3f ptA, Point3f ptB, Point3f ptC, short colix) {
    
    output("r(" + triad(ptA) + "," + triad(ptB) + "," + triad(ptC) + ","
        + color4(colix) + ")\n");
  }
}
