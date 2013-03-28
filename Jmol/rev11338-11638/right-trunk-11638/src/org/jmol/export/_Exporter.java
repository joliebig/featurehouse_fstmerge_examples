

package org.jmol.export;

import java.awt.Image;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.BitSet;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Point3i;
import javax.vecmath.Vector3f;

import org.jmol.api.JmolRendererInterface;
import org.jmol.g3d.Font3D;
import org.jmol.g3d.Graphics3D;
import org.jmol.modelset.Atom;
import org.jmol.shape.Text;
import org.jmol.viewer.Viewer;



public abstract class _Exporter {

  
  

  protected Viewer viewer;
  protected JmolRendererInterface jmolRenderer;
  protected StringBuffer output;
  protected BufferedWriter bw;
  private FileOutputStream os;
  protected String fileName;
  protected String commandLineOptions;
  
  protected boolean isToFile;
  protected Graphics3D g3d;

  protected int screenWidth;
  protected int screenHeight;
  protected int slabZ;
  protected int depthZ;

  boolean use2dBondOrderCalculation;
  boolean canDoTriangles;
  boolean isCartesianExport;

  protected Point3f center = new Point3f();
  protected Point3f tempP1 = new Point3f();
  protected Point3f tempP2 = new Point3f();
  protected Point3f tempP3 = new Point3f();
  protected Vector3f tempV1 = new Vector3f();
  protected Vector3f tempV2 = new Vector3f();
  protected Vector3f tempV3 = new Vector3f();
  protected AxisAngle4f tempA = new AxisAngle4f();
  
  public _Exporter() {
  }

  public void setRenderer(JmolRendererInterface jmolRenderer) {
    this.jmolRenderer = jmolRenderer;
  }
  
  public boolean initializeOutput(Viewer viewer, Graphics3D g3d, Object output) {
    this.viewer = viewer;
    this.g3d = g3d;
    center.set(viewer.getRotationCenter());
    if ((screenWidth <= 0) || (screenHeight <= 0)) {
      screenWidth = viewer.getScreenWidth();
      screenHeight = viewer.getScreenHeight();
    }
    slabZ = g3d.getSlab();
    depthZ = g3d.getDepth();
    isToFile = (output instanceof String);
    if (isToFile) {
      fileName = (String) output;
      int pt = fileName.indexOf(":::"); 
      if (pt > 0) {
        commandLineOptions = fileName.substring(pt + 3);
        fileName = fileName.substring(0, pt);
      }
      
      try {
        os = new FileOutputStream(fileName);
        bw = new BufferedWriter(new OutputStreamWriter(os));
      } catch (FileNotFoundException e) {
        return false;
      }
    } else {
      this.output = (StringBuffer) output;
    }
    getHeader();
    return true;
  }

  public String finalizeOutput() {
    getFooter();
    if (!isToFile)
      return output.toString();
    try {
      bw.flush();
      bw.close();
      os = null;
    } catch (IOException e) {
      
    }
    return null;
  }

  protected static String getExportDate() {
    return new SimpleDateFormat("yyyy-MM-dd', 'HH:mm").format(new Date());
  }

  final protected static float degreesPerRadian = (float) (360 / (2 * Math.PI));

  protected float getFieldOfView() {
    float zoffset = (viewer.getCameraDepth()+ 0.5f);
    return (float) (2 * Math.atan(0.5 / zoffset));
  }

  final protected Point3f pt = new Point3f();

  protected void getViewpointPosition(Point3f ptAtom) {
    pt.set(screenWidth / 2, screenHeight / 2, 0);
    viewer.unTransformPoint(pt, ptAtom);
    ptAtom.sub(center);
  }

  protected void adjustViewpointPosition(Point3f ptAtom) {
    
    float zoffset = (viewer.getCameraDepth()+ 0.5f);
    float scalePixelsPerAngstrom = viewer.getScalePixelsPerAngstrom(false);
    float rotationRadius = viewer.getRotationRadius();
    float scale = viewer.getZoomPercentFloat() / 100f;
    float z0 = zoffset * 2 * rotationRadius * scalePixelsPerAngstrom / scale;
    
    
    pt.set(screenWidth / 2, screenHeight / 2, z0);
    viewer.unTransformPoint(pt, pt);
    pt.sub(center);
    ptAtom.add(pt);
  }

  protected Vector3f getRotation(Vector3f v) {
    tempV3.set(v);
    tempV3.normalize();
    float r = (float) Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z);
    float rX = (float) Math.acos(v.y / r) * degreesPerRadian;
    if (v.x < 0)
      rX += 180;
    float rY = (float) Math.atan2(v.x, v.z) * degreesPerRadian;
    tempV3.set(rX, rY, 0);
    return tempV3;
  }

  protected AxisAngle4f getAxisAngle(Vector3f v, int x, int y, int z) {
    tempV3.set(x, y, z);
    tempV2.set(v);
    tempV2.normalize();
    tempV3.add(tempV2);
    tempA.set(tempV3.x, tempV3.y, tempV3.z, 3.14159f);
    return tempA;
  }

  protected String rgbFromColix(short colix, char sep) {
    int argb = g3d.getColorArgbOrGray(colix);
    return new StringBuffer().append((argb >> 16) & 0xFF).append(sep).append(
        (argb >> 8) & 0xFF).append(sep).append((argb) & 0xFF).toString();
  }

  protected String rgbFractionalFromColix(short colix, char sep) {
    return rgbFractionalFromArgb(g3d.getColorArgbOrGray(colix), sep);
  }

  protected String rgbFractionalFromArgb(int argb, char sep) {
    return "" + round(((argb >> 16) & 0xFF) / 255f) + sep 
        + round(((argb >> 8) & 0xFF) / 255f) + sep
        + round(((argb) & 0xFF) / 255f);
  }

  protected String translucencyFractionalFromColix(short colix) {
    int translevel = Graphics3D.getColixTranslucencyLevel(colix);
    if (Graphics3D.isColixTranslucent(colix))
      return new StringBuffer().append(translevel / 255f).toString();
    return new StringBuffer().append(0f).toString();
  }

  protected String opacityFractionalFromColix(short colix) {
    int translevel = Graphics3D.getColixTranslucencyLevel(colix);
    if (Graphics3D.isColixTranslucent(colix))
      return new StringBuffer().append(1 - translevel / 255f).toString();
    return new StringBuffer().append(1f).toString();
  }

  protected static float round(double number) { 
    return (float) Math.round(number*1000)/1000;  
  }

  
  protected Vector getColorList(int i0, short[] colixes, int nVertices, BitSet bsSelected, Hashtable htColixes) {
    String color;
    int nColix = 0;
    Vector list = new Vector();
    for (int i = 0; i < nVertices; i++) 
      if (bsSelected == null || bsSelected.get(i)) {
        color = "" + colixes[i];
        if (!htColixes.containsKey(color)) {
          list.add(new Short(colixes[i]));
          htColixes.put(color, "" + (i0 + nColix++));
        }
      }
   return list;
  }


  
  
  abstract void getHeader();

  abstract void getFooter();


  

  
  
  

  abstract void renderAtom(Atom atom, short colix);

  
  

  abstract void renderIsosurface(Point3f[] vertices, short colix,
                                 short[] colixes, Vector3f[] normals,
                                 int[][] indices, BitSet bsFaces,
                                 int nVertices, int faceVertexMax, 
                                 short[] polygonColixes, int nPolygons);

  abstract void renderText(Text t);
  
  abstract void drawString(short colix, String str, Font3D font3d, int xBaseline,
                            int yBaseline, int z, int zSlab);
  
  abstract void fillCylinder(Point3f atom1, Point3f atom2, short colix1, short colix2,
                             byte endcaps, int madBond, int bondOrder);

  abstract void fillCylinder(short colix, byte endcaps, int diameter, 
                             Point3f screenA, Point3f screenB);

  abstract void drawCircleCentered(short colix, int diameter, int x,
                                           int y, int z, boolean doFill);  

  abstract void fillScreenedCircleCentered(short colix, int diameter, int x,
                                                    int y, int z);  

  abstract void drawPixel(short colix, int x, int y, int z); 
 
  abstract void drawTextPixel(int argb, int x, int y, int z);

  
  abstract void fillCone(short colix, byte endcap, int diameter, 
                         Point3f screenBase, Point3f screenTip);
  
  
  abstract void fillTriangle(short colix, Point3f ptA, Point3f ptB, Point3f ptC);
  
  
  abstract void fillSphereCentered(short colix, int diameter, Point3f pt);
  
  abstract void plotText(int x, int y, int z, short colix, String text, Font3D font3d);

  abstract void plotImage(int x, int y, int z, Image image, short bgcolix, 
                          int width, int height);

  abstract void startShapeBuffer(int iShape);

  abstract void endShapeBuffer();

  abstract void renderEllipsoid(Point3f center, Point3f[] points, short colix, 
                                int x, int y, int z, int diameter,
                                Matrix3f toEllipsoidal, double[] coef,
                                Matrix4f deriv, Point3i[] octantPoints);
}
