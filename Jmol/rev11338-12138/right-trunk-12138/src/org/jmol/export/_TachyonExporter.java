

package org.jmol.export;

import java.util.BitSet;
import java.util.Hashtable;
import java.util.Vector;

import javax.vecmath.Matrix3f;
import javax.vecmath.Point3f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

import org.jmol.g3d.Graphics3D;
import org.jmol.viewer.Viewer;



public class _TachyonExporter extends __RayTracerExporter {

  boolean wasPerspectiveDepth;
  String lighting;
  String phong;
  
  UseTable textures = new UseTable(" ");
 
  boolean initializeOutput(Viewer viewer, Graphics3D g3d, Object output) {
    
    
    getLightingInfo();
    return super.initializeOutput(viewer, g3d, output);    
  }
  
  private void getLightingInfo() {
    lighting = " AMBIENT " + round(Graphics3D.getAmbientPercent() / 100f)
        + " DIFFUSE " + round(Graphics3D.getDiffusePercent()/100f) 
        + " SPECULAR " + round(Graphics3D.getSpecularPercent() / 100f);
    phong = " Phong Plastic 0.5 Phong_size " + Graphics3D.getSpecularExponent();
  }  
  
  

  protected void outputHeader() {
    super.outputHeader();
    output("# ******************************************************\n");
    output("# Created by Jmol " + Viewer.getJmolVersion() + "\n");
    output("#\n");
    output("# This script was generated on " + getExportDate() + "\n");
    output("#\n");
    output("# Requires Tachyon version 0.98.7 or newer\n");
    output("#\n");
    output("# Default tachyon rendering command for this scene:\n");
    output("#   tachyon  -aasamples 12 %s -format TARGA -o %s.tga\n");
    output("#\n");
    output("# ******************************************************\n");
    output("\n");
    outputJmolPerspective();
    output("\n");
    output("Begin_Scene\n");
    output("Resolution " + screenWidth + " " + screenHeight + "\n");
    output("Shader_Mode Medium\n"); 
    output("  Trans_VMD\n");
    output("  Fog_VMD\n");
    output("End_Shader_Mode\n");
    output("Camera\n");

    output("  Zoom 3.0\n");
    output("  Aspectratio 1\n");
    output("  Antialiasing 12\n");
    output("  Raydepth 8\n");
    output("  Center " + triad(screenWidth / 2, screenHeight / 2, 0) + "\n");
    output("  Viewdir 0 0 1\n");
    output("  Updir   0 1 0\n");
    output("End_Camera\n");
    output("Directional_Light Direction " + round(lightSource) + " Color 1 1 1\n");
    output("\n");
    output("Background " + rgbFractionalFromColix(backgroundColix, ' ')
        + "\n");
    output("\n");
  }

  protected void outputFooter() {
    output("End_Scene\n");
  }

  protected void output(Tuple3f pt) {
    output(triad(pt));
  }

  private String triad(float x, float y, float z) {
    return (int) x + " " + (int) (-y) + " " + (int) z;
  }

  private String triad(Tuple3f pt) {
    if (Float.isNaN(pt.x))
      return "0 0 0";
    return triad(pt.x, pt.y, pt.z);
  }

  private String textureCode;
  
  private void outputTextureCode() {
    output(textureCode);
    output("\n");
  }

  private void outputTexture(short colix, boolean useTexDef) {
    outputTexture2(rgbFractionalFromColix(colix, ' '), 
        opacityFractionalFromColix(colix), useTexDef);
  }

  private void outputTexture(int argb, boolean useTexDef) {
    outputTexture2(rgbFractionalFromArgb(argb, ' '), 
        opacityFractionalFromArgb(argb), useTexDef);
  }
  
  private void outputTexture2(String rgb, String opacity, boolean useTexDef) {
    textureCode = (useTexDef ? textures.getDef("t" + rgb + opacity) : null);
    if (useTexDef && textureCode.startsWith(" "))
      return;
    StringBuffer sb = new StringBuffer();
    sb.append(lighting);
    sb.append(" Opacity " + opacity);
    sb.append(phong);
    sb.append(" Color " + rgb);
    sb.append(" TexFunc 0\n");
    if (!useTexDef) {
      textureCode = "Texture " + sb;
      return;
    }
    output("TexDef " + textureCode);
    output(sb.toString());
    textureCode = " " + textureCode;
  }

  protected void outputCircle(int x, int y, int z, float radius, short colix,
                              boolean doFill) {
    tempV1.set(0,0,-1);
    outputRing(x, y, z, tempV1, radius, colix, doFill);
  }

  protected void outputCircleScreened(int x, int y, int z, float radius, short colix) {
    colix = Graphics3D.getColixTranslucent(colix, true, 0.8f);
    tempV1.set(0,0,-1);
    outputRing(x, y, z, tempV1, radius, colix, true);
  }

  private void outputRing(int x, int y, int z, Vector3f tempV1, float radius,
                          short colix, boolean doFill) {
    outputTexture(colix, true);
    output("Ring Center ");
    output(triad(x, y, z));
    output(" Normal " + triad(tempV1));
    output(" Inner " + round((doFill ? 0 : radius * 0.95)));
    output(" Outer " + round(radius));
    outputTextureCode();
  }

  protected void outputComment(String comment) {
    output("# ");
    output(comment);
    output("\n");
  }

  protected void outputCone(Point3f screenBase, Point3f screenTip, float radius,
                            short colix) {
    
    
    
    viewer.unTransformPoint(screenBase, tempP1);
    viewer.unTransformPoint(screenTip, tempP2);
    radius = viewer.unscaleToScreen(screenBase.z, radius);
    Matrix3f matRotateScale = getRotationMatrix(tempP1, tempP2, radius);
    jmolRenderer.drawSurface(getConeMesh(tempP1, matRotateScale, colix), null);
  }

  protected void outputCylinder(Point3f screenA, Point3f screenB,
                                      float radius, short colix, boolean withCaps) {
    outputTexture(colix, true);
    output("FCylinder Base ");
    output(triad(screenA));
    output(" Apex ");
    output(triad(screenB));
    output(" Rad " + round(radius));
    outputTextureCode();
    if (withCaps && radius > 1) {
      tempV1.sub(screenA, screenB);
      outputRing((int) screenA.x, (int) screenA.y, (int) screenA.z, tempV1, radius, colix, true);
      tempV1.scale(-1);
      outputRing((int) screenB.x, (int) screenB.y, (int) screenB.z, tempV1, radius, colix, true);
    }
  }  
  
  protected void fillConicalCylinder(Point3f screenA, Point3f screenB,
                                     int madBond, short colix, byte endcaps) {
    
    int diameter = viewer.scaleToScreen((int) ((screenA.z + screenB.z)/2f), madBond);
    fillCylinder(colix, endcaps, diameter, screenA, screenB);
   }


  protected void outputCylinderConical(Point3f screenA, Point3f screenB,
                                       float radius1, float radius2, short colix) {
    
  }

  protected void outputEllipsoid(Point3f center, float radius, double[] coef, short colix) {
    viewer.transformPoint(center, tempP1);
    
    outputSphere(tempP1.x, tempP1.y, tempP1.z, radius, colix);
  }

  protected void outputSurface(Point3f[] vertices, Vector3f[] normals,
                                  short[] colixes, int[][] indices,
                                  short[] polygonColixes, int nVertices,
                                  int nPolygons, int nFaces, BitSet bsFaces,
                                  int faceVertexMax, short colix, Vector colorList, Hashtable htColixes, Point3f offset) {
    if (polygonColixes != null) {
      for (int i = nPolygons; --i >= 0;) {
        if (bsFaces != null && !bsFaces.get(i))
          continue;
        setTempVertex(vertices[indices[i][0]], offset, tempP1);
        setTempVertex(vertices[indices[i][1]], offset, tempP2);
        setTempVertex(vertices[indices[i][2]], offset, tempP3);
        viewer.transformPoint(tempP1, tempP1);
        viewer.transformPoint(tempP2, tempP2);
        viewer.transformPoint(tempP3, tempP3);
        outputTriangle(tempP1, tempP2, tempP3, colix);
      }
      return;
    }
    outputTexture(colixes == null ? colix : colixes[0], false);
    output("VertexArray  Numverts " + nVertices + "\nCoords\n");
    for (int i = 0; i < nVertices; i++)
      outputVertex(vertices[i], offset);
    output("\nNormals\n");
    for (int i = 0; i < nVertices; i++) {
      setTempVertex(vertices[i], offset, tempP1);
      output(triad(getScreenNormal(tempP1, normals[i], 10)) + "\n");
    }
    String rgb = (colixes == null ? rgbFractionalFromColix(colix, ' ') : null);
    output("\nColors\n");
    for (int i = 0; i < nVertices; i++) {
      output((colixes == null ? rgb : rgbFractionalFromColix(colixes[i], ' ')) + "\n");
    }
    outputTextureCode();
    output("\nTriMesh " + nFaces + "\n");
    for (int i = nPolygons; --i >= 0;) {
      if (bsFaces != null && !bsFaces.get(i))
        continue;
      output(indices[i][0] + " " + indices[i][1] + " " + indices[i][2] + "\n");
      if (faceVertexMax == 4 && indices[i].length == 4) {
        output(indices[i][0] + " " + indices[i][2] + " " + indices[i][3] + "\n");
      }
    }
    output("\nEnd_VertexArray\n");
  }

  protected void outputSphere(float x, float y, float z, float radius,
                                  short colix) {

    outputTexture(colix, true);
    output("Sphere Center ");
    output(triad(x, y, z));
    output(" Rad " + round(radius));
    outputTextureCode();
  }

  protected void outputTextPixel(int x, int y, int z, int argb) {
    outputTexture(argb, true);
    output("Sphere Center ");
    output(triad(x, y, z));
    output(" Rad 1");
    
    outputTextureCode();
  }
  
  protected void outputTriangle(Point3f ptA, Point3f ptB, Point3f ptC, short colix) {
    outputTexture(colix, true);
    output("TRI");
    output(" V0 " + triad(ptA));
    output(" V1 " + triad(ptB));
    output(" V2 " + triad(ptC));
    outputTextureCode();
  }

}
