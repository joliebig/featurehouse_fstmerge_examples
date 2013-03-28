package org.jmol.api;

import java.awt.Image;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Point3i;
import javax.vecmath.Vector3f;

import org.jmol.g3d.Font3D;
import org.jmol.g3d.Graphics3D;
import org.jmol.modelset.Atom;
import org.jmol.util.MeshSurface;
import org.jmol.viewer.Viewer;

public interface JmolRendererInterface {

  
  
  public abstract int getExportType();
  
  public abstract boolean initializeExporter(String type, Viewer viewer,
                                             Graphics3D g3d, Object output);

  public abstract boolean isAntialiased();
  
  public abstract boolean checkTranslucent(boolean isAlphaTranslucent);

  public abstract boolean haveTranslucentObjects();

  
  public abstract int getRenderWidth();

  
  public abstract int getRenderHeight();

  
  public abstract int getSlab();

  public abstract void setSlab(int slabValue);

  
  public abstract int getDepth();

  
  public abstract boolean setColix(short colix);

  public abstract void renderBackground();
  
  public abstract void drawAtom(Atom atom);

  
  public abstract void drawCircle(short colix, int diameter, int x, int y, int z,
                                          boolean doFill);


  
  public abstract void fillScreenedCircle(short colixFill,
                                                  int diameter, int x, int y,
                                                  int z);

  
  public abstract void fillSphere(int diameter, int x, int y, int z);

  

  public abstract void fillSphere(int diameter, Point3i center);

  
  public abstract void fillSphere(int diameter, Point3f center);

  
  public abstract void drawRect(int x, int y, int z, int zSlab, int rWidth,
                                int rHeight);

  
  public abstract void fillRect(int x, int y, int z, int zSlab, int widthFill,
                                int heightFill);

  

  public abstract void drawString(String str, Font3D font3d, int xBaseline,
                                  int yBaseline, int z, int zSlab);

  public abstract void plotPixelClippedNoSlab(int argb, int x, int y, int z);
    
  

  public abstract void drawStringNoSlab(String str, Font3D font3d,
                                        int xBaseline, int yBaseline, int z);

  public abstract void setFont(byte fid);

  public abstract Font3D getFont3DCurrent();

  public abstract void drawPixel(int x, int y, int z);

  public abstract void plotPixelClipped(Point3i a);

  public abstract void drawPoints(int count, int[] coordinates);

  public abstract void drawDashedLine(int run, int rise, Point3i pointA,
                                      Point3i pointB);

  public abstract void drawDottedLine(Point3i pointA, Point3i pointB);

  public abstract void drawLine(int x1, int y1, int z1, int x2, int y2, int z2);

  public abstract void drawLine(Point3i pointA, Point3i pointB);

  public abstract void drawLine(short colixA, short colixB, int x1, int y1,
                                int z1, int x2, int y2, int z2);

  public abstract void drawBond(Atom atomA, Atom atomB, short colixA,
                                    short colixB, byte endcaps, short mad);

  public abstract void fillCylinder(short colixA, short colixB, byte endcaps,
                                    int diameter, int xA, int yA, int zA,
                                    int xB, int yB, int zB);

  public abstract void fillCylinder(byte endcaps, int diameter,
                                    Point3i screenA, Point3i screenB);

  public abstract void fillCylinderBits(byte endcaps, int diameter,
                                        Point3f screenA, Point3f screenB);

  public abstract void fillCylinderScreen(byte endcaps, int diameter, int xA, int yA,
                                          int zA, int xB, int yB, int zB);


  public abstract void fillCylinderScreen(byte endcapsOpenend, int diameter,
                                                Point3i pt0i, Point3i pt1i);

  public abstract void fillConeScreen(byte endcap, int screenDiameter, Point3i screenBase,
                                Point3i screenTip);

  public abstract void fillConeSceen(byte endcap, int screenDiameter, Point3f screenBase,
                                Point3f screenTip);

  public abstract void drawHermite(int tension, Point3i s0, Point3i s1,
                                   Point3i s2, Point3i s3);

  public abstract void drawHermite(boolean fill, boolean border, int tension,
                                   Point3i s0, Point3i s1, Point3i s2,
                                   Point3i s3, Point3i s4, Point3i s5,
                                   Point3i s6, Point3i s7, int aspectRatio);

  public abstract void fillHermite(int tension, int diameterBeg,
                                   int diameterMid, int diameterEnd,
                                   Point3i s0, Point3i s1, Point3i s2,
                                   Point3i s3);

  
  public abstract void drawTriangle(Point3i screenA, short colixA,
                                    Point3i screenB, short colixB,
                                    Point3i screenC, short colixC, int check);

  
  public abstract void drawTriangle(Point3i screenA, Point3i screenB,
                                    Point3i screenC, int check);

  
  
  
  public abstract void fillTriangle(Point3i screenA, short colixA,
                                    short normixA, Point3i screenB,
                                    short colixB, short normixB,
                                    Point3i screenC, short colixC, short normixC);

  
  public abstract void fillTriangle(short normix, int xScreenA, int yScreenA,
                                    int zScreenA, int xScreenB, int yScreenB,
                                    int zScreenB, int xScreenC, int yScreenC,
                                    int zScreenC);

  public abstract void fillTriangle(Point3f screenA, Point3f screenB,
                                    Point3f screenC);

  public abstract void fillTriangle(Point3i screenA, Point3i screenB,
                                    Point3i screenC);

  public abstract void fillTriangle(Point3i screenA, short colixA,
                                    short normixA, Point3i screenB,
                                    short colixB, short normixB,
                                    Point3i screenC, short colixC,
                                    short normixC, float factor);

  public abstract void drawQuadrilateral(short colix, Point3i screenA,
                                         Point3i screenB, Point3i screenC,
                                         Point3i screenD);

  public abstract void fillQuadrilateral(Point3f screenA, Point3f screenB,
                                         Point3f screenC, Point3f screenD);

  public abstract void fillQuadrilateral(Point3i screenA, short colixA,
                                         short normixA, Point3i screenB,
                                         short colixB, short normixB,
                                         Point3i screenC, short colixC,
                                         short normixC, Point3i screenD,
                                         short colixD, short normixD);

  public abstract void drawSurface(MeshSurface meshSurface, Point3f[] vertices);

  public abstract boolean isInDisplayRange(int x, int y);

  public abstract boolean isClippedZ(int z);

  public abstract boolean isClippedXY(int i, int screenX, int screenY);

  public abstract int getColorArgbOrGray(short colix);

  public abstract void setNoisySurfaceShade(Point3i screenA, Point3i screenB,
                                        Point3i screenC);

  public abstract byte getFontFid(String fontFace, float fontSize);

  public abstract short getNormix(Vector3f vector);
  
  public abstract short getInverseNormix(short normix);

  public abstract boolean isDirectedTowardsCamera(short normix);

  public abstract Vector3f[] getTransformedVertexVectors();

  public abstract Vector3f getNormixVector(short normix);

  public abstract Font3D getFont3DScaled(Font3D font3d, float imageFontScaling);

  public abstract byte getFontFid(float fontSize);

  public abstract void fillEllipsoid(Point3f center, Point3f[] points, int x, int y, int z, 
      int diameter, Matrix3f mToEllipsoidal, double[] coef, Matrix4f mDeriv, int selectedOctant, Point3i[] octantPoints);

  public abstract void drawImage(Image image, int x, int y, int z, int zslab, short bgcolix, int width, int height);

  public abstract String finalizeOutput();

  public abstract short[] getBgColixes(short[] bgcolixes);

  public abstract void setTranslucentCoverOnly(boolean TF);
}
