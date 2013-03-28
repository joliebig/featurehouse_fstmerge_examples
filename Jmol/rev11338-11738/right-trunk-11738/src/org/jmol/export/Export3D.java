
package org.jmol.export;

import java.awt.Image;
import java.util.BitSet;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point3i;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.jmol.api.JmolRendererInterface;
import org.jmol.g3d.Font3D;
import org.jmol.g3d.Graphics3D;
import org.jmol.g3d.Hermite3D;
import org.jmol.viewer.Viewer;



final public class Export3D implements JmolRendererInterface {

  private _Exporter exporter;
  
  private Graphics3D g3d;
  private short colix;
  private Hermite3D hermite3d;
  private int width;
  private int height;
  private int slab;
  
  public _Exporter getExporter() {
    return exporter;
  }

  public Export3D() {
    this.hermite3d = new Hermite3D(this);
  }
  
  public boolean initializeExporter(String type, Viewer viewer,
                                    Graphics3D g3d, Object output) {
    try {
      Class exporterClass = Class.forName("org.jmol.export._" + type
          + "Exporter");
      exporter = (_Exporter) exporterClass.newInstance();
    } catch (Exception e) {
      return false;
    }
    this.g3d = g3d;
    width = g3d.getRenderWidth();
    height = g3d.getRenderHeight();
    slab = g3d.getSlab();
    exporter.setRenderer(this);
    return exporter.initializeOutput(viewer, g3d, output);
  }

  public String finalizeOutput() {
    return exporter.finalizeOutput();
  }

  public boolean canDoTriangles() {
    return exporter.canDoTriangles;
  }

  public boolean isCartesianExport() {
    return exporter.isCartesianExport;
  }

  public void setSlab(int slabValue) {
    slab = slabValue;
    g3d.setSlab(slabValue);
  }
  
  public void renderBackground() {
    if (!exporter.isCartesianExport)
      g3d.renderBackground(this);
  }
  
  
  
  public void fillScreenedCircleCentered(short colixFill, int diameter, int x,
                                           int y, int z) {
    
    if (isClippedZ(z))
      return;
    exporter.fillScreenedCircleCentered(colixFill, diameter, x, y, z);
  }

  
  
  public void drawCircleCentered(short colix, int diameter, int x,
                                           int y, int z, boolean doFill) {
    
    if (isClippedZ(z))
      return;
    exporter.drawCircleCentered(colix, diameter, x, y, z, doFill);
  }

  private Point3f ptA = new Point3f();
  private Point3f ptB = new Point3f();
  private Point3f ptC = new Point3f();
  private Point3f ptD = new Point3f();
  
  private Point3i ptAi = new Point3i();
  private Point3i ptBi = new Point3i();
  
  
  public void fillSphereCentered(int diameter, int x, int y, int z) {
    ptA.set(x, y, z);
    fillSphereCentered(diameter, ptA);
  }

  

  public void fillSphereCentered(int diameter, Point3i center) {
    ptA.set(center.x, center.y, center.z);
    fillSphereCentered(diameter, ptA);
  }

  
  public void fillSphereCentered(int diameter, Point3f center) {
    if (diameter == 0)
      return;
    exporter.fillSphereCentered(colix, diameter, center);
  }

  
  public void drawRect(int x, int y, int z, int zSlab, int rWidth, int rHeight) {
    
    if (zSlab != 0 && isClippedZ(zSlab))
      return;
    int w = rWidth - 1;
    int h = rHeight - 1;
    int xRight = x + w;
    int yBottom = y + h;
    if (y >= 0 && y < height)
      drawHLine(x, y, z, w);
    if (yBottom >= 0 && yBottom < height)
      drawHLine(x, yBottom, z, w);
    if (x >= 0 && x < width)
      drawVLine(x, y, z, h);
    if (xRight >= 0 && xRight < width)
      drawVLine(xRight, y, z, h);
  }

  private void drawHLine(int x, int y, int z, int w) {
    
    int argbCurrent = g3d.getColorArgbOrGray(colix);
    if (w < 0) {
      x += w;
      w = -w;
    }
    for (int i = 0; i <= w; i++) {
      exporter.drawTextPixel(argbCurrent, x + i, y, z);
    }
  }

  private void drawVLine(int x, int y, int z, int h) {
    
    int argbCurrent = g3d.getColorArgbOrGray(colix);
    if (h < 0) {
      y += h;
      h = -h;
    }
    for (int i = 0; i <= h; i++) {
      exporter.drawTextPixel(argbCurrent, x, y + i, z);
    }
  }

  
  public void fillRect(int x, int y, int z, int zSlab, int widthFill, int heightFill) {
    
    if (isClippedZ(zSlab))
      return;
    ptA.set(x, y, z);
    ptB.set(x + widthFill, y, z);
    ptC.set(x + widthFill, y + heightFill, z);
    ptD.set(x, y + heightFill, z);
    fillQuadrilateral(ptA, ptB, ptC, ptD);
  }
  
  
  
  public void drawString(String str, Font3D font3d,
                         int xBaseline, int yBaseline, int z, int zSlab) {
    
    if (str == null)
      return;
    if (isClippedZ(zSlab))
      return;
    drawStringNoSlab(str, font3d, xBaseline, yBaseline, z); 
  }

  
  
  public void drawStringNoSlab(String str, Font3D font3d, 
                               int xBaseline, int yBaseline,
                               int z) {
    
    if (str == null)
      return;
    z = Math.max(slab, z);
    if(font3d == null)
      font3d = g3d.getFont3DCurrent();
    else
      g3d.setFont(font3d);
    exporter.plotText(xBaseline, yBaseline,
                z, colix, str, font3d);
  }
  
  public void drawImage(Image image, int x, int y, int z, int zSlab, 
                        short bgcolix, int width, int height) {
    if (image == null || width == 0 || height == 0)
      return;
    if (isClippedZ(zSlab))
      return;
    z = Math.max(slab, z);
    exporter.plotImage(x, y, z, image, bgcolix, width, height);
  }

  

  

  
  public void drawPixel(int x, int y, int z) {
    
    plotPixelClipped(x, y, z);
  }

  void plotPixelClipped(int x, int y, int z) {
    
    if (isClipped(x, y, z))
      return;
    exporter.drawPixel(colix, x, y, z);
  }

  public void plotPixelClippedNoSlab(int argb, int x, int y, int z) {
    
    z = Math.max(slab, z);
    exporter.drawTextPixel(argb, x, y, z);
  }

  public void plotPixelClipped(Point3i screen) {
    if (isClipped(screen.x, screen.y, screen.z))
      return;
    
    exporter.drawPixel(colix, screen.x, screen.y, screen.z);
  }
  
  public void drawPoints(int count, int[] coordinates) {
    for (int i = count * 3; i > 0; ) {
      int z = coordinates[--i];
      int y = coordinates[--i];
      int x = coordinates[--i];
      if (isClipped(x, y, z))
        continue;
      exporter.drawPixel(colix, x, y, z);
    }
  }

  

  public void drawDashedLine(int run, int rise, Point3i pointA, Point3i pointB) {
    
    drawLine(pointA, pointB); 
    
    
    
  }

  public void drawDottedLine(Point3i pointA, Point3i pointB) {
    
     
    drawLine(pointA, pointB); 
    
    
    
  }

  public void drawLine(int x1, int y1, int z1, int x2, int y2, int z2) {
    
    ptAi.set(x1, y1, z1);
    ptBi.set(x2, y2, z2);
    drawLine(ptAi, ptBi);
  }

  public void drawLine(short colixA, short colixB, int xA, int yA, int zA, int xB, int yB, int zB) {
    
    fillCylinder(colixA, colixB, Graphics3D.ENDCAPS_FLAT, 2, xA, yA, zA, xB, yB, zB);
  }

  public void drawLine(Point3i pointA, Point3i pointB) {
    
    ptA.set(pointA.x, pointA.y, pointA.z);
    ptB.set(pointB.x, pointB.y, pointB.z);
    exporter.fillCylinder(colix, Graphics3D.ENDCAPS_FLAT, 1, ptA, ptB);
  }
  
  public void fillCylinder(short colixA, short colixB, byte endcaps,
                              int mad, int xA, int yA, int zA, int xB, int yB, int zB) {
    
    ptA.set(xA, yA, zA);
    ptB.set(xB, yB, zB);
    exporter.fillCylinder(ptA, ptB, colixA, colixB, endcaps, mad, 1);
  }

  public void fillCylinder(byte endcaps,
                           int mad,
                           int xA, int yA, int zA, int xB, int yB, int zB) {
    
    fillCylinder(colix, colix, endcaps, mad, xA, yA, zA, xB, yB, zB);
  }

  public void fillCylinder(byte endcaps, int diameter, Point3i pointA,
                              Point3i pointB) {
    if (diameter <= 0)
      return;
    ptA.set(pointA.x, pointA.y, pointA.z);
    ptB.set(pointB.x, pointB.y, pointB.z);
    exporter.fillCylinder(colix, endcaps, diameter, ptA, ptB);
  }

  public void fillCylinderBits(byte endcaps, int diameter, Point3f pointA,
                                  Point3f pointB) {
    if (diameter <= 0)
      return;
    exporter.fillCylinder(colix, endcaps, diameter, pointA, pointB);
  }


  public void fillCone(byte endcap, int diameter,
                          Point3i pointBase, Point3i screenTip) {
    
    ptA.set(pointBase.x, pointBase.y, pointBase.z);
    ptB.set(screenTip.x, screenTip.y, screenTip.z);
    fillCone(endcap, diameter, ptA, ptB);
  }

  public void fillCone(byte endcap, int diameter,
                       Point3f pointBase, Point3f screenTip) {
    
    exporter.fillCone(colix, endcap, diameter, pointBase, screenTip);
  }

  public void drawHermite(int tension,
                          Point3i s0, Point3i s1, Point3i s2, Point3i s3) {
    
    hermite3d.render(false, tension, 0, 0, 0, s0, s1, s2, s3);
    
    
  }

  public void drawHermite(boolean fill, boolean border,
                          int tension, Point3i s0, Point3i s1, Point3i s2,
                          Point3i s3, Point3i s4, Point3i s5, Point3i s6,
                          Point3i s7, int aspectRatio) {
    hermite3d.render2(fill, border, tension, s0, s1, s2, s3, s4, s5, s6,
        s7, aspectRatio);

  }

  public void fillHermite(int tension, int diameterBeg,
                          int diameterMid, int diameterEnd,
                          Point3i s0, Point3i s1, Point3i s2, Point3i s3) {
    hermite3d.render(true, tension,
        diameterBeg, diameterMid, diameterEnd,
        s0, s1, s2, s3);
  }
  
  

  public void drawTriangle(Point3i pointA, short colixA, Point3i pointB,
                           short colixB, Point3i pointC, short colixC, int check) {
    
    int xA = pointA.x;
    int yA = pointA.y;
    int zA = pointA.z;
    int xB = pointB.x;
    int yB = pointB.y;
    int zB = pointB.z;
    int xC = pointC.x;
    int yC = pointC.y;
    int zC = pointC.z;
    if ((check & 1) == 1)
      drawLine(colixA, colixB, xA, yA, zA, xB, yB, zB);
    if ((check & 2) == 2)
      drawLine(colixB, colixC, xB, yB, zB, xC, yC, zC);
    if ((check & 4) == 4)
      drawLine(colixA, colixC, xA, yA, zA, xC, yC, zC);
  }

  public void drawTriangle(Point3i pointA, Point3i pointB,
                           Point3i pointC, int check) {
  }

  public void drawCylinderTriangle(int xA, int yA, int zA, int xB,
                                   int yB, int zB, int xC, int yC, int zC,
                                   int diameter) {
    
    fillCylinder(Graphics3D.ENDCAPS_SPHERICAL, diameter, xA, yA,
        zA, xB, yB, zB);
    fillCylinder(Graphics3D.ENDCAPS_SPHERICAL, diameter, xA, yA,
        zA, xC, yC, zC);
    fillCylinder(Graphics3D.ENDCAPS_SPHERICAL, diameter, xB, yB,
        zB, xC, yC, zC);
  }

  public void drawfillTriangle(int xA, int yA, int zA, int xB,
                               int yB, int zB, int xC, int yC, int zC) {

    ptA.set(xA, yA, zA);
    ptB.set(xB, yB, zB);
    ptC.set(xC, yC, zC);
    fillTriangle(ptA, ptB, ptC);
  }
  
  public void fillTriangle(Point3i pointA, int intensityA,
                           Point3i pointB, int intensityB,
                           Point3i pointC, int intensityC) {
    ptA.set(pointA.x, pointA.y, pointA.z);
    ptB.set(pointB.x, pointB.y, pointB.z);
    ptC.set(pointC.x, pointC.y, pointC.z);
    exporter.fillTriangle(colix, ptA, ptB, ptC);
  }
  
  public void fillTriangle(Point3i pointA, short colixA, short normixA,
                           Point3i pointB, short colixB, short normixB,
                           Point3i pointC, short colixC, short normixC) {
    
    if (colixA != colixB || colixB != colixC) {
      
      return;
    }
    ptA.set(pointA.x, pointA.y, pointA.z);
    ptB.set(pointB.x, pointB.y, pointB.z);
    ptC.set(pointC.x, pointC.y, pointC.z);
    exporter.fillTriangle(colixA, ptA, ptB, ptC);
  }

  public void fillTriangle(short normix,
                           int xpointA, int ypointA, int zpointA,
                           int xpointB, int ypointB, int zpointB,
                           int xpointC, int ypointC, int zpointC) {
    
    ptA.set(xpointA, ypointA, zpointA);
    ptB.set(xpointB, ypointB, zpointB);
    ptC.set(xpointC, ypointC, zpointC);    
    exporter.fillTriangle(colix, ptA, ptB, ptC);
  }

  public void fillTriangle(Point3f pointA, Point3f pointB, Point3f pointC) {
    
    exporter.fillTriangle(colix, pointA, pointB, pointC);
  }

  public void fillTriangle(Point3i pointA, Point3i pointB, Point3i pointC) {
    

    ptA.set(pointA.x, pointA.y, pointA.z);
    ptB.set(pointB.x, pointB.y, pointB.z);
    ptC.set(pointC.x, pointC.y, pointC.z);
    exporter.fillTriangle(colix, ptA, ptB, ptC);
  }

  public void fillTriangle(Point3i pointA, short colixA,
                                   short normixA, Point3i pointB,
                                   short colixB, short normixB,
                                   Point3i pointC, short colixC,
                                   short normixC, float factor) {
    fillTriangle(pointA, colixA, normixA, 
        pointB, colixB, normixB, pointC, colixC, normixC);
  }

  
  
  public void drawQuadrilateral(short colix, Point3i pointA, Point3i pointB,
                                Point3i pointC, Point3i screenD) {
    
    setColix(colix);
    drawLine(pointA, pointB);
    drawLine(pointB, pointC);
    drawLine(pointC, screenD);
    drawLine(screenD, pointA);
  }

  public void fillQuadrilateral(Point3f pointA, Point3f pointB,
                                Point3f pointC, Point3f pointD) {
    
    exporter.fillTriangle(colix, pointA, pointB, pointC);
    exporter.fillTriangle(colix, pointA, pointC, pointD);
  }

  public void fillQuadrilateral(Point3i pointA, short colixA, short normixA,
                                Point3i pointB, short colixB, short normixB,
                                Point3i pointC, short colixC, short normixC,
                                Point3i screenD, short colixD, short normixD) {
    
    fillTriangle(pointA, colixA, normixA,
                 pointB, colixB, normixB,
                 pointC, colixC, normixC);
    fillTriangle(pointA, colixA, normixA,
                 pointC, colixC, normixC,
                 screenD, colixD, normixD);
  }

  public void renderIsosurface(Point3f[] vertices, short colix,
                                        short[] colixes, Vector3f[] normals,
                                        int[][] indices, BitSet bsFaces, int nVertices,
                                        int faceVertexMax, short[] polygonColixes, int nPolygons) {
    exporter.renderIsosurface(vertices, colix, colixes, normals,
                              indices, bsFaces, nVertices, faceVertexMax, null, nPolygons);
  }

  public short[] getBgColixes(short[] bgcolixes) {
    
    return exporter.isCartesianExport ? null : bgcolixes;
  }

  public void startShapeBuffer(int iShape) {
    exporter.startShapeBuffer(iShape);
  }

  public void endShapeBuffer() {
    exporter.endShapeBuffer();
  }

  public void renderEllipsoid(Point3f center, Point3f[] points, 
                              int x, int y, int z, int diameter,
                              Matrix3f mToEllipsoidal, double[] coef,
                              Matrix4f mDeriv, int selectedOctant,
                              Point3i[] octantPoints) {
    exporter.renderEllipsoid(center, points, colix, x, y, z, 
        diameter, mToEllipsoidal, coef, mDeriv, octantPoints);
  }

  

  
  public boolean isAntialiased() {
    return false;
  }
  
  public boolean checkTranslucent(boolean isAlphaTranslucent) {
    return true;
  }
  
  public boolean haveTranslucentObjects() {
    return true;
  }
  
  
  public int getRenderWidth() {
    return g3d.getRenderWidth();
  }

  
  public int getRenderHeight() {
    return g3d.getRenderHeight();
  }

  
  public int getSlab() {
    return g3d.getSlab();
  }

  
  public int getDepth() {
    return g3d.getDepth();
  }

  
  public boolean setColix(short colix) {
    this.colix = colix;
    g3d.setColix(colix);
    return true;
  }

  public void setFont(byte fid) {
    g3d.setFont(fid);
  }
  
  public Font3D getFont3DCurrent() {
    return g3d.getFont3DCurrent();
  }


  public boolean isInDisplayRange(int x, int y) {
    if(isCartesianExport())
      return true;
    return g3d.isInDisplayRange(x, y);
  }
  
  public boolean isClippedZ(int z) {
    return g3d.isClippedZ(z);
  }
  
  public int clipCode(int x, int y, int z) {
    return (isCartesianExport() ? g3d.clipCode(z) : g3d.clipCode(x, y, z));
  }
  
  public boolean isClippedXY(int diameter, int x, int y) {
    if(isCartesianExport())
      return false;
    return g3d.isClippedXY(diameter, x, y);
  }
  
  public boolean isClipped(int x, int y, int z) {
    return (g3d.isClippedZ(z) || isClipped(x, y));
  }
  
  protected boolean isClipped(int x, int y) {
    if(isCartesianExport())
      return false;
    return g3d.isClipped(x, y);
  }

  public int getColorArgbOrGray(short colix) {
    return g3d.getColorArgbOrGray(colix);
  }

  public int calcSurfaceShade(Point3i pointA, Point3i pointB, Point3i pointC) {
    return g3d.calcSurfaceShade(pointA, pointB, pointC);
  }

  public byte getFontFid(String fontFace, float fontSize) {
    return g3d.getFontFid(fontFace, fontSize);
  }

  public boolean isDirectedTowardsCamera(short normix) {
    
    return g3d.isDirectedTowardsCamera(normix);
  }

  public short getNormix(Vector3f vector) {
    return g3d.getNormix(vector);
  }
  
  public short getInverseNormix(short normix) {
    return g3d.getInverseNormix(normix);
  }

  public Vector3f[] getTransformedVertexVectors() {
    return g3d.getTransformedVertexVectors();
  }

  public Vector3f getNormixVector(short normix) {
    return g3d.getNormixVector(normix);
  }

  public Font3D getFont3DScaled(Font3D font, float scale) {
    return g3d.getFont3DScaled(font, scale);
  }

  public byte getFontFid(float fontSize) {
    return g3d.getFontFid(fontSize);
  }
}
