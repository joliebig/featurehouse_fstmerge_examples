

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
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

import org.jmol.api.JmolRendererInterface;
import org.jmol.g3d.Font3D;
import org.jmol.g3d.Graphics3D;
import org.jmol.modelset.Atom;
import org.jmol.script.Token;
import org.jmol.util.MeshSurface;
import org.jmol.util.Quaternion;
import org.jmol.viewer.StateManager;
import org.jmol.viewer.Viewer;



public abstract class ___Exporter {

  
  

  protected Viewer viewer;
  protected JmolRendererInterface jmolRenderer;
  protected StringBuffer output;
  protected BufferedWriter bw;
  private FileOutputStream os;
  protected String fileName;
  protected String commandLineOptions;
  
  protected boolean isToFile;
  protected Graphics3D g3d;

  protected short backgroundColix;
  protected int screenWidth;
  protected int screenHeight;
  protected int slabZ;
  protected int depthZ;
  protected Point3f lightSource = Graphics3D.getLightSource();

  
  
  
  
  
  
  
  
  
  
  
  
  int exportType;
  
  final protected static float degreesPerRadian = (float) (180 / Math.PI);

  final protected Point3f tempP1 = new Point3f();
  final protected Point3f tempP2 = new Point3f();
  final protected Point3f tempP3 = new Point3f();
  final protected Point3f center = new Point3f();
  final protected Vector3f tempV1 = new Vector3f();
  final protected Vector3f tempV2 = new Vector3f();
  final protected Vector3f tempV3 = new Vector3f();
  final protected AxisAngle4f tempA = new AxisAngle4f();
  
  public ___Exporter() {
  }

  void setRenderer(JmolRendererInterface jmolRenderer) {
    this.jmolRenderer = jmolRenderer;
  }
  
  boolean initializeOutput(Viewer viewer, Graphics3D g3d, Object output) {
    this.viewer = viewer;
    this.g3d = g3d;
    backgroundColix = viewer.getObjectColix(StateManager.OBJ_BACKGROUND);
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
    outputHeader();
    return true;
  }

  abstract protected void outputHeader();
  
  protected int nBytes;
  protected void output(String data) {
    nBytes += data.length();
    try {
      if (bw == null)
        output.append(data);
      else
        bw.write(data);
    } catch (IOException e) {
      
    }
  }

  abstract protected void outputComment(String comment);

  protected static void setTempVertex(Point3f pt, Point3f offset, Point3f ptTemp) {
    ptTemp.set(pt);
    if (offset != null)
      ptTemp.add(offset);
  }

  protected void outputVertices(Point3f[] vertices, int nVertices, Point3f offset) {
    for (int i = 0; i < nVertices; i++) {
      if (Float.isNaN(vertices[i].x))
        continue;
      outputVertex(vertices[i], offset);
      output("\n");
    }
  }

  protected void outputVertex(Point3f pt, Point3f offset) {
    setTempVertex(pt, offset, tempP1);
    output(tempP1);
  }

  abstract protected void output(Tuple3f pt);

  protected void outputJmolPerspective() {
    outputComment("Jmol perspective:");
    outputComment("screen width height dim: " + screenWidth + " " + screenHeight + " " + viewer.getScreenDim());
    outputComment("scalePixelsPerAngstrom: " + viewer.getScalePixelsPerAngstrom(false));
    outputComment("perspectiveDepth: " + viewer.getPerspectiveDepth());
    outputComment("cameraDepth: " + viewer.getCameraDepth());
    outputComment("light source: " + lightSource);
    outputComment("lighting: " + viewer.getSpecularState().replace('\n', ' '));
    outputComment("center: " + center);
    outputComment("rotationRadius: " + viewer.getRotationRadius());
    outputComment("boundboxCenter: " + viewer.getBoundBoxCenter());
    outputComment("translationOffset: " + viewer.getTranslationScript());
    outputComment("zoom: " + viewer.getZoomPercentFloat());
    outputComment("moveto command: " + viewer.getOrientationText(Token.moveto));
  }

  protected void outputFooter() {
    
  }

  String finalizeOutput() {
    outputFooter();
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

  protected float getFieldOfView() {
    float zoffset = (viewer.getCameraDepth()+ 0.5f);
    return (float) (2 * Math.atan(0.5 / zoffset));
  }

  protected void getViewpointPosition(Point3f ptAtom) {
    tempP3.set(screenWidth / 2, screenHeight / 2, 0);
    viewer.unTransformPoint(tempP3, ptAtom);
    ptAtom.sub(center);
  }

  protected void adjustViewpointPosition(Point3f ptAtom) {
    
    float zoffset = (viewer.getCameraDepth()+ 0.5f);
    float scalePixelsPerAngstrom = viewer.getScalePixelsPerAngstrom(false);
    float rotationRadius = viewer.getRotationRadius();
    float scale = viewer.getZoomPercentFloat() / 100f;
    float z0 = zoffset * 2 * rotationRadius * scalePixelsPerAngstrom / scale;
    
    
    tempP3.set(screenWidth / 2, screenHeight / 2, z0);
    viewer.unTransformPoint(tempP3, tempP3);
    tempP3.sub(center);
    ptAtom.add(tempP3);
  }

  protected String rgbFractionalFromColix(short colix, char sep) {
    return rgbFractionalFromArgb(g3d.getColorArgbOrGray(colix), sep);
  }

  protected static String rgbFractionalFromArgb(int argb, char sep) {
    int red = (argb >> 16) & 0xFF;
    int green = (argb >> 8) & 0xFF;
    int blue = argb & 0xFF;
    return "" + round(red == 0 ? 0 : (red + 1)/ 256f) + sep 
        + round(green == 0 ? 0 : (green + 1) / 256f) + sep
        + round(blue == 0 ? 0 : (blue + 1) / 256f);
  }

  protected static String translucencyFractionalFromColix(short colix) {
    return round(Graphics3D.translucencyFractionalFromColix(colix));
  }

  protected static String opacityFractionalFromColix(short colix) {
    return round(1 - Graphics3D.translucencyFractionalFromColix(colix));
  }

  protected static String opacityFractionalFromArgb(int argb) {
    int opacity = (argb >> 24) & 0xFF;
    return round(opacity == 0 ? 0 : (opacity + 1) / 256f);
  }

  protected static String round(double number) { 
    String s;
    return (number == 0 ? "0" : number == 1 ? "1" : (s = ""
        + (Math.round(number * 1000d) / 1000d)).startsWith("0.") ? s
        .substring(1) : s.startsWith("-0.") ? "-" + s.substring(2) : 
          s.endsWith(".0") ? s.substring(0, s.length() - 2) : s);
  }

  protected static String round(Tuple3f pt) {
    return round(pt.x) + " " + round(pt.y) + " " + round(pt.z);
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

  protected static MeshSurface getConeMesh(Point3f centerBase, Matrix3f matRotateScale, short colix) {
    MeshSurface ms = new MeshSurface();
    int ndeg = 10;
    int n = 360 / ndeg;
    ms.colix = colix;
    ms.vertices = new Point3f[ms.vertexCount = n + 1];
    ms.polygonIndexes = new int[ms.polygonCount = n][];
    for (int i = 0; i < n; i++)
      ms.polygonIndexes[i] = new int[] {i, (i + 1) % n, n };
    double d = ndeg / 180. * Math.PI; 
    for (int i = 0; i < n; i++) {
      float x = (float) (Math.cos(i * d));
      float y = (float) (Math.sin(i * d));
      ms.vertices[i] = new Point3f(x, y, 0);
    }
    ms.vertices[n] = new Point3f(0, 0, 1);
    if (matRotateScale != null) {
      ms.vertexNormals = new Vector3f[ms.vertexCount];
      for (int i = 0; i < ms.vertexCount; i++) {
        matRotateScale.transform(ms.vertices[i]);
        ms.vertexNormals[i] = new Vector3f();
        ms.vertexNormals[i].set(ms.vertices[i]);
        ms.vertexNormals[i].normalize();
        ms.vertices[i].add(centerBase);
      }
    }
    return ms;
  }

  protected Matrix3f getRotationMatrix(Point3f pt1, Point3f pt2, float radius) {    
    Matrix3f m = new Matrix3f();
    Matrix3f m1;
    if (pt2.x == pt1.x && pt2.y == pt1.y) {
      m1 = new Matrix3f();
      m1.setIdentity();
      if (pt1.z > pt2.z)
        m1.mul(-1);
    } else {
      tempV1.set(pt2);
      tempV1.sub(pt1);
      tempV2.set(0, 0, 1);
      tempV2.cross(tempV2, tempV1);
      tempV1.cross(tempV1, tempV2);
      Quaternion q = Quaternion.getQuaternionFrame(tempV2, tempV1, null);
      m1 = q.getMatrix();
    }
    m.m00 = radius;
    m.m11 = radius;
    m.m22 = pt2.distance(pt1);
    m1.mul(m);
    return m1;
  }

  
  

  abstract void drawAtom(Atom atom);

  abstract void drawCircle(int x, int y, int z,
                                   int diameter, short colix, boolean doFill);  

  void drawSurface(int nVertices, int nPolygons, int faceVertexMax,
                      Point3f[] vertices, Vector3f[] normals, short[] colixes,
                      int[][] indices, short[] polygonColixes, BitSet bsFaces,
                      short colix, Point3f offset) {
    if (nVertices == 0)
      return;
    int nFaces = 0;
    for (int i = nPolygons; --i >= 0;)
      if (bsFaces == null || bsFaces.get(i))
        nFaces += (faceVertexMax == 4 && indices[i].length == 4 ? 2 : 1);
    if (nFaces == 0)
      return;
    Hashtable htColixes = new Hashtable();
    Vector colorList = null;
    if (polygonColixes != null)
      colorList = getColorList(0, polygonColixes, nPolygons, bsFaces, htColixes);
    else if (colixes != null)
      colorList = getColorList(0, colixes, nVertices, null, htColixes);
    outputSurface(vertices, normals, colixes, indices, polygonColixes,
        nVertices, nPolygons, nFaces, bsFaces, faceVertexMax, colix, colorList,
        htColixes, offset);
  }

  abstract protected void outputSurface(Point3f[] vertices, Vector3f[] normals,
                                short[] colixes, int[][] indices,
                                short[] polygonColixes,
                                int nVertices, int nPolygons, int nFaces, BitSet bsFaces,
                                int faceVertexMax, short colix, Vector colorList, Hashtable htColixes, Point3f offset);

  abstract void drawPixel(short colix, int x, int y, int z); 
  
  abstract void drawTextPixel(int argb, int x, int y, int z);

  
  abstract void fillConeScreen(short colix, byte endcap, int screenDiameter, 
                         Point3f screenBase, Point3f screenTip);
  
  abstract void drawCylinder(Point3f atom1, Point3f atom2, short colix1, short colix2,
                             byte endcaps, int madBond, int bondOrder);

  abstract void fillCylinder(short colix, byte endcaps, int diameter, 
                                        Point3f screenA, Point3f screenB);

  abstract void fillCylinderScreen(short colix, byte endcaps, int screenDiameter, 
                             Point3f screenA, Point3f screenB);

  abstract void fillEllipsoid(Point3f center, Point3f[] points, short colix, 
                              int x, int y, int z, int diameter,
                              Matrix3f toEllipsoidal, double[] coef,
                              Matrix4f deriv, Point3i[] octantPoints);

  abstract void fillScreenedCircle(short colix, int diameter, int x, int y,
                                   int z); 

  
  abstract void fillSphere(short colix, int diameter, Point3f pt);
  
  
  abstract void fillTriangle(short colix, Point3f ptA, Point3f ptB, Point3f ptC);
  
  
  private int nText;
  private int nImage;
  public int lineWidth;

  void plotImage(int x, int y, int z, Image image, short bgcolix, int width,
                 int height) {
    if (z < 3)
      z = viewer.getFrontPlane();
    outputComment("start image " + (++nImage));
    g3d.plotImage(x, y, z, image, jmolRenderer, bgcolix, width, height);
    outputComment("end image " + nImage);
  }

  void plotText(int x, int y, int z, short colix, String text, Font3D font3d) {
    
    
    
    if (z < 3)
      z = viewer.getFrontPlane();
    outputComment("start text " + (++nText) + ": " + text);
    g3d.plotText(x, y, z, g3d.getColorArgbOrGray(colix), text, font3d, jmolRenderer);
    outputComment("end text " + nText + ": " + text);
  }
}

class UseTable extends Hashtable {
  private int iObj;
  private String keyword;
  private char term;

  UseTable(String keyword) {
    this.keyword = keyword;
    term = keyword.charAt(keyword.length() - 1);
  }
  
  

  String getDef(String key) {
    if (containsKey(key))
      return keyword + get(key) + term;
    String id = "_" + (iObj++);
    put(key, id);
    return id;
  }
    
}


