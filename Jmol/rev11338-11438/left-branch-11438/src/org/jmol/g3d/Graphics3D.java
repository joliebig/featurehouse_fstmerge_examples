
package org.jmol.g3d;

import java.awt.Component;
import java.awt.Image;
import java.util.BitSet;
import java.util.Hashtable;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point3i;
import javax.vecmath.Point3f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;
import javax.vecmath.Matrix3f;

import org.jmol.api.JmolRendererInterface;
import org.jmol.util.Logger;
import org.jmol.util.TextFormat;
import org.jmol.viewer.Viewer;



final public class Graphics3D implements JmolRendererInterface {

  Platform3D platform;
  Line3D line3d;
  Circle3D circle3d;
  Sphere3D sphere3d;
  
  Triangle3D triangle3d;
  Cylinder3D cylinder3d;
  Hermite3D hermite3d;
  Normix3D normix3d;
  boolean isFullSceneAntialiasingEnabled;
  private boolean antialiasThisFrame;
  private boolean antialias2; 
  private boolean antialiasEnabled;
    
  public void destroy() {
    releaseBuffers();
    platform = null;
    
  }

  
  public boolean isDisplayAntialiased() {
    return antialiasEnabled;
  }

  
  public boolean isAntialiased() {
    return antialiasThisFrame;
  }

  boolean inGreyscaleMode;
  byte[] anaglyphChannelBytes;
  
  boolean twoPass = false;
  boolean isPass2;
  boolean addAllPixels;
  boolean haveTranslucentObjects;
  
  int windowWidth, windowHeight;
  int width, height;
  
  int displayMinX, displayMaxX, displayMinY, displayMaxY;
  int slab, depth;
  boolean zShade;
  int xLast, yLast;
  private int[] pbuf;
  private int[] pbufT;
  int[] zbuf;
  private int[] zbufT;
  int bufferSize;

  
  
  
  

  short colixCurrent;
  int[] shadesCurrent;
  int argbCurrent;
  boolean isTranslucent;
  boolean isScreened;
  int translucencyMask;
  int argbNoisyUp, argbNoisyDn;

  Font3D font3dCurrent;

  public final static byte ENDCAPS_NONE = 0;
  public final static byte ENDCAPS_OPEN = 1;
  public final static byte ENDCAPS_FLAT = 2;
  public final static byte ENDCAPS_SPHERICAL = 3;
  public final static byte ENDCAPS_OPENEND = 4;

  
  public final static byte shadeMax = Shade3D.shadeMax;
  public final static byte shadeLast = Shade3D.shadeMax - 1;
  public final static byte shadeNormal = Shade3D.shadeNormal;
  public final static byte intensitySpecularSurfaceLimit = Shade3D.intensitySpecularSurfaceLimit;

  public final static short INHERIT_ALL = 0;
  public final static short USE_PALETTE = 2;
  public final static short BLACK = 4;
  public final static short ORANGE = 5;
  public final static short PINK = 6;
  public final static short BLUE = 7;
  public final static short WHITE = 8;
  public final static short CYAN = 9;
  public final static short RED = 10;
  public final static short GREEN = 11;
  public final static short GRAY = 12;
  public final static short SILVER = 13;
  public final static short LIME = 14;
  public final static short MAROON = 15;
  public final static short NAVY = 16;
  public final static short OLIVE = 17;
  public final static short PURPLE = 18;
  public final static short TEAL = 19;
  public final static short MAGENTA = 20;
  public final static short YELLOW = 21;
  public final static short HOTPINK = 22;
  public final static short GOLD = 23;


  
  public Graphics3D(Component awtComponent) {
    platform = Platform3D.createInstance(awtComponent);
    this.line3d = new Line3D(this);
    this.circle3d = new Circle3D(this);
    this.sphere3d = new Sphere3D(this);
    this.triangle3d = new Triangle3D(this);
    this.cylinder3d = new Cylinder3D(this);
    this.hermite3d = new Hermite3D(this);
    this.normix3d = new Normix3D();
  }
  
  int newWindowWidth, newWindowHeight;
  boolean newAntialiasing;

  public boolean currentlyRendering() {
    return currentlyRendering;
  }
  
  public void setWindowParameters(int width, int height, boolean antialias) {
    newWindowWidth = width;
    newWindowHeight = height;
    newAntialiasing = antialias;
    if (currentlyRendering)
      endRendering();
  }
  
  private void setWidthHeight(boolean isAntialiased) {
    width = windowWidth;
    height = windowHeight;
    if (isAntialiased) {
      width <<= 1;
      height <<= 1;
    }

    xLast = width - 1;
    yLast = height - 1;
    displayMinX = -(width >> 1);
    displayMaxX = width - displayMinX;
    displayMinY = -(height >> 1);
    displayMaxY = height - displayMinY;
    bufferSize = width * height;
  }
  
  public boolean checkTranslucent(boolean isAlphaTranslucent) {
    if (isAlphaTranslucent)
      haveTranslucentObjects = true;
    return (!twoPass || twoPass && (isPass2 == isAlphaTranslucent));
  }
  
  public void beginRendering(Matrix3f rotationMatrix) {
    

    if (currentlyRendering)
      endRendering();
    if (windowWidth != newWindowWidth || windowHeight != newWindowHeight
        || newAntialiasing != isFullSceneAntialiasingEnabled) {
      windowWidth = newWindowWidth;
      windowHeight = newWindowHeight;
      isFullSceneAntialiasingEnabled = newAntialiasing;
      releaseBuffers();
    }
    
    normix3d.setRotationMatrix(rotationMatrix);
    antialiasEnabled = antialiasThisFrame = newAntialiasing;
    currentlyRendering = true;
    twoPass = true; 
    isPass2 = false;
    
      
    
    colixCurrent = 0;
    haveTranslucentObjects = false;
    addAllPixels = true;
    if (pbuf == null) {
      platform.allocateBuffers(windowWidth, windowHeight,
                              antialiasThisFrame);
      pbuf = platform.pBuffer;
      zbuf = platform.zBuffer;
    }
    setWidthHeight(antialiasThisFrame);
    
      
    
    
    platform.obtainScreenBuffer();
    if (backgroundImage != null)
      plotImage(Integer.MIN_VALUE, 0, Integer.MIN_VALUE, backgroundImage, null, (short) 0, 0, 0);
    
    random = Math.random();
  }
  public double random;

  private void releaseBuffers() {
    pbuf = null;
    zbuf = null;
    pbufT = null;
    zbufT = null;
    platform.releaseBuffers();
  }
  
  public boolean setPass2(boolean antialiasTranslucent) {
    if (!haveTranslucentObjects || !currentlyRendering)
      return false;
    isPass2 = true;
    
    colixCurrent = 0;
    addAllPixels = true;
    if (pbufT == null || antialias2 != antialiasTranslucent) {
      platform.allocateTBuffers(antialiasTranslucent);
      pbufT = platform.pBufferT;
      zbufT = platform.zBufferT;
    }    
    antialias2 = antialiasTranslucent;
    if (antialiasThisFrame && !antialias2)
      downsampleFullSceneAntialiasing(true);
    
    platform.clearTBuffer();
    return true;
  }
  
  
  public void endRendering() {
    if (!currentlyRendering)
      return;
    if (pbuf != null) {
      if (isPass2)
        mergeOpaqueAndTranslucentBuffers();
      if (antialiasThisFrame)
        downsampleFullSceneAntialiasing(false);
    }
    platform.setBackgroundColor(bgcolor);
    platform.notifyEndOfRendering();
    
    currentlyRendering = false;
  }

  int anaglyphLength;
  public void snapshotAnaglyphChannelBytes() {
    if (currentlyRendering)
      throw new NullPointerException();
    anaglyphLength = windowWidth * windowHeight;
    if (anaglyphChannelBytes == null ||
  anaglyphChannelBytes.length != anaglyphLength)
      anaglyphChannelBytes = new byte[anaglyphLength];
    for (int i = anaglyphLength; --i >= 0; )
      anaglyphChannelBytes[i] = (byte)pbuf[i];
  }

  public void applyCustomAnaglyph(int[] stereoColors) {
    
    int color1 = stereoColors[0];
    int color2 = stereoColors[1] & 0x00FFFFFF;
    for (int i = anaglyphLength; --i >= 0;) {
      int a = anaglyphChannelBytes[i] & 0x000000FF;
      a = (a | ((a | (a << 8)) << 8)) & color2;
      pbuf[i] = (pbuf[i] & color1) | a;
    }
  }

  public void applyGreenAnaglyph() {
    for (int i = anaglyphLength; --i >= 0; ) {
      int green = (anaglyphChannelBytes[i] & 0x000000FF) << 8;
      pbuf[i] = (pbuf[i] & 0xFFFF0000) | green;
    }
  }

  public void applyBlueAnaglyph() {
    for (int i = anaglyphLength; --i >= 0; ) {
      int blue = anaglyphChannelBytes[i] & 0x000000FF;
      pbuf[i] = (pbuf[i] & 0xFFFF0000) | blue;
    }
  }

  public void applyCyanAnaglyph() {
    for (int i = anaglyphLength; --i >= 0; ) {
      int blue = anaglyphChannelBytes[i] & 0x000000FF;
      int cyan = (blue << 8) | blue;
      pbuf[i] = pbuf[i] & 0xFFFF0000 | cyan;
    }
  }
  
  public Image getScreenImage() {
    return platform.imagePixelBuffer;
  }

  public void releaseScreenImage() {
    platform.clearScreenBufferThreaded();
  }

  public boolean haveTranslucentObjects() {
    return haveTranslucentObjects;
  }
  
  
  public int getRenderWidth() {
    return width;
  }

  
  public int getRenderHeight() {
    return height;
  }

  
  public int getSlab() {
    return slab;
  }

  
  public int getDepth() {
    return depth;
  }

  public Image backgroundImage;
  
  public void setBackgroundTransparent(boolean TF) {
    if (platform != null)
    platform.setBackgroundTransparent(TF);
  }

  public int bgcolor;
  
  
  public void setBackgroundArgb(int argb) {
    bgcolor = argb;
    
    
    backgroundImage = null;
  }

  public void setBackgroundImage(Image image) {
    backgroundImage = image;
  }


  
  public void setGreyscaleMode(boolean greyscaleMode) {
    this.inGreyscaleMode = greyscaleMode;
  }

  
  public void setSlabAndDepthValues(int slabValue, int depthValue,
                                    boolean zShade) {
    slab = slabValue < 0 ? 0 : slabValue;
    depth = depthValue < 0 ? 0 : depthValue;
    this.zShade = zShade;
  }

  public void setSlab(int slabValue) {
    slab = slabValue;
  }
  
  int getZShift(int z) {
    return (zShade ? (z - slab) * 5 / (depth - slab): 0);
  }
  
  private void downsampleFullSceneAntialiasing(boolean downsampleZBuffer) {
    
    int width4 = width;
    int offset1 = 0;
    int offset4 = 0;
    int bgcheck = bgcolor;
    
    
    
    
    
    
    
    
    
    
    
    if (downsampleZBuffer)
      bgcheck += ((bgcheck & 0xFF) == 0xFF ? -1 : 1); 
    for (int i =0; i < pbuf.length; i++)
      if (pbuf[i] == 0)
        pbuf[i] = bgcheck;
    bgcheck &= 0xFFFFFF;
    
    for (int i = windowHeight; --i >= 0; offset4 += width4)
      for (int j = windowWidth; --j >= 0; ++offset1) {
        
        
        
        int argb = (pbuf[offset4] >> 2) & 0x3F3F3F3F;
        argb += (pbuf[offset4++ + width4] >> 2) & 0x3F3F3F3F;
        argb += (pbuf[offset4] >> 2) & 0x3F3F3F3F;
        argb += (pbuf[offset4++ + width4] >> 2) & 0x3F3F3F3F;
        argb += (argb & 0xC0C0C0C0) >> 6;
        pbuf[offset1] = argb & 0x00FFFFFF;
      }
    if (downsampleZBuffer) {
      
      offset1 = offset4 = 0;
      for (int i = windowHeight; --i >= 0; offset4 += width4)
        for (int j = windowWidth; --j >= 0; ++offset1, ++offset4) {
          int z = Math.min(zbuf[offset4], zbuf[offset4 + width4]);
          z = Math.min(z, zbuf[++offset4]);
          z = Math.min(z, zbuf[offset4 + width4]);
          if (z != Integer.MAX_VALUE)
            z >>= 1;
          zbuf[offset1] = (pbuf[offset1] == bgcheck ? Integer.MAX_VALUE
              : z);
        }
      antialiasThisFrame = false;
      setWidthHeight(false);
    }    
  }

  void mergeOpaqueAndTranslucentBuffers() {
    if (pbufT == null)
      return;
    for (int offset = 0; offset < bufferSize; offset++)
      mergeBufferPixel(pbuf, pbufT[offset], offset, bgcolor);
  }
  
  static void averageBufferPixel(int[] pIn, int[] pOut, int pt, int dp) {
    int argbA = pIn[pt - dp];
    int argbB = pIn[pt + dp];
    if (argbA == 0 || argbB == 0)
      return;
    pOut[pt] = ((((argbA & 0xFF000000)>>1) + ((argbB & 0xFF000000)>>1))<< 1)
        | (((argbA & 0x00FF00FF) + (argbB & 0x00FF00FF)) >> 1) & 0x00FF00FF
        | (((argbA & 0x0000FF00) + (argbB & 0x0000FF00)) >> 1) & 0x0000FF00;
  }
  
  static void mergeBufferPixel(int[] pbuf, int argbB, int pt, int bgcolor) {
    if (argbB == 0)
      return;
    int argbA = pbuf[pt];
    if (argbA == argbB)
      return;
    if (argbA == 0)
      argbA = bgcolor;
    int rbA = (argbA & 0x00FF00FF);
    int gA = (argbA & 0x0000FF00);
    int rbB = (argbB & 0x00FF00FF);
    int gB = (argbB & 0x0000FF00);
    int logAlpha = (argbB >> 24) & 7;
    
    
    
    switch (logAlpha) {
    
    
    
    

    case 1: 
      rbA = (((rbB << 2) + (rbB << 1) + rbB  + rbA) >> 3) & 0x00FF00FF;
      gA = (((gB << 2) + + (gB << 1) + gB + gA) >> 3) & 0x0000FF00;
      break;
    case 2: 
      rbA = (((rbB << 1) + rbB + rbA) >> 2) & 0x00FF00FF;
      gA = (((gB << 1) + gB + gA) >> 2) & 0x0000FF00;
      break;
    case 3: 
      rbA = (((rbB << 2) + rbB + (rbA << 1) + rbA) >> 3) & 0x00FF00FF;
      gA = (((gB << 2) + gB  + (gA << 1) + gA) >> 3) & 0x0000FF00;
      break;
    case 4: 
      rbA = ((rbA + rbB) >> 1) & 0x00FF00FF;
      gA = ((gA + gB) >> 1) & 0x0000FF00;
      break;
    case 5: 
      rbA = (((rbB << 1) + rbB + (rbA << 2) + rbA) >> 3) & 0x00FF00FF;
      gA = (((gB << 1) + gB  + (gA << 2) + gA) >> 3) & 0x0000FF00;
      break;
    case 6: 
      rbA = (((rbA << 1) + rbA + rbB) >> 2) & 0x00FF00FF;
      gA = (((gA << 1) + gA + gB) >> 2) & 0x0000FF00;
      break;
    case 7: 
      rbA = (((rbA << 2) + (rbA << 1) + rbA + rbB) >> 3) & 0x00FF00FF;
      gA = (((gA << 2) + (gA << 1) + gA + gB) >> 3) & 0x0000FF00;
      break;
    }
    pbuf[pt] = 0xFF000000 | rbA | gA;    
  }
  
  public boolean hasContent() {
    return platform.hasContent();
  }

  private int currentIntensity;
  
  private void setColixAndIntensity(short colix, int intensity) {
    if (colix == colixCurrent && currentIntensity == intensity)
      return;
    currentIntensity = -1;
    setColix(colix);
    setColorNoisy(intensity);
  }

  
  
  public boolean setColix(short colix) {
    if (colix == colixCurrent && currentIntensity == -1)
      return true;
    int mask = colix & TRANSLUCENT_MASK;
    if (mask == TRANSPARENT)
      return false;
    isTranslucent = mask != 0;
    isScreened = isTranslucent && mask == TRANSLUCENT_SCREENED;
    if (!checkTranslucent(isTranslucent && !isScreened))
      return false;
    addAllPixels = isPass2 || !isTranslucent;
    if (isPass2)
      translucencyMask = (mask << ALPHA_SHIFT) | 0xFFFFFF;
    colixCurrent = colix;
    shadesCurrent = getShades(colix);
    currentIntensity = -1; 
    argbCurrent = argbNoisyUp = argbNoisyDn = getColixArgb(colix);
    return true;
  }

  void setColorNoisy(int intensity) {
    
      
    currentIntensity = intensity;
    argbCurrent = shadesCurrent[intensity];
    argbNoisyUp = shadesCurrent[intensity < shadeLast ? intensity + 1 : shadeLast];
    argbNoisyDn = shadesCurrent[intensity > 0 ? intensity - 1 : 0];
  }
  
  int zMargin;
  
  void setZMargin(int dz) {
    zMargin = dz;
  }
  
  void addPixel(int offset, int z, int p) {
    addPixelT(offset, z, p, zbuf, pbuf, zbufT, pbufT, translucencyMask, isPass2, zMargin, bgcolor);
  }
  
  final static void addPixelT(int offset, int z, int p, int[] zbuf, int[] pbuf, int[] zbufT, int[] pbufT, int translucencyMask, boolean isPass2, int zMargin, int bgcolor) {
    if (!isPass2) {
      zbuf[offset] = z;
      pbuf[offset] = p;
      return;
    }
    int zT = zbufT[offset]; 
    if (z < zT) {
      
      
      int argb = pbufT[offset];
      if (argb != 0 && zT - z > zMargin)
        mergeBufferPixel(pbuf, argb, offset, bgcolor);
      zbufT[offset] = z;
      pbufT[offset] = p & translucencyMask;
    } else if (z == zT) {
    } else {
      
      if (z - zT > zMargin)
        mergeBufferPixel(pbuf, p & translucencyMask, offset, bgcolor);
    }
  }

  
  public void drawCircleCentered(short colix, int diameter, int x, int y, int z, boolean doFill) {
    
    if (isClippedZ(z))
      return;
    int r = (diameter + 1) / 2;
    boolean isClipped = x < r || x + r >= width || y < r || y + r >= height;
      if (!isClipped)
        circle3d.plotCircleCenteredUnclipped(x, y, z, diameter);
      else if (!isClippedXY(diameter, x, y))
        circle3d.plotCircleCenteredClipped(x, y, z, diameter);
    if (!doFill)
      return;
    if (!isClipped)
      circle3d.plotFilledCircleCenteredUnclipped(x, y, z, diameter);
    else if (!isClippedXY(diameter, x, y))
      circle3d.plotFilledCircleCenteredClipped(x, y, z, diameter);    
  }

  public void fillScreenedCircleCentered(short colixFill, int diameter, int x, int y, int z) {
    
    if (isClippedZ(z))
      return;
    int r = (diameter + 1) / 2;
    boolean isClipped = x < r || x + r >= width || y < r || y + r >= height;
    if (setColix(getColixTranslucent(colixFill, false, 0))) {
      if (!isClipped)
        circle3d.plotCircleCenteredUnclipped(x, y, z, diameter);
      else if (!isClippedXY(diameter, x, y))
        circle3d.plotCircleCenteredClipped(x, y, z, diameter);
    }
    if (!setColix(getColixTranslucent(colixFill, true, 0.5f)))
      return;
    if (!isClipped)
      circle3d.plotFilledCircleCenteredUnclipped(x, y, z, diameter);
    else if (!isClippedXY(diameter, x, y))
      circle3d.plotFilledCircleCenteredClipped(x, y, z, diameter);
  }

  
  public void fillSphereCentered(int diameter, int x, int y, int z) {
    switch (diameter) {
    case 1:
      plotPixelClipped(argbCurrent, x, y, z);
      return;
    case 0:
      return;
    }
    if (diameter <= (antialiasThisFrame ? Sphere3D.maxSphereDiameter2
        : Sphere3D.maxSphereDiameter))
      sphere3d.render(shadesCurrent, !addAllPixels, diameter, x, y, z, null,
          null, null, -1, null);
  }

  

  public void fillSphereCentered(int diameter, Point3i center) {
    fillSphereCentered(diameter, center.x, center.y, center.z);
  }

  
  public void fillSphereCentered(int diameter, Point3f center) {
    fillSphereCentered(diameter, (int)center.x, (int)center.y, (int)center.z);
  }

  public void renderEllipsoid(Point3f center, Point3f[] points, int x, int y,
                              int z, int diameter, Matrix3f mToEllipsoidal,
                              double[] coef, Matrix4f mDeriv,
                              int selectedOctant, Point3i[] octantPoints) {
    switch (diameter) {
    case 1:
      plotPixelClipped(argbCurrent, x, y, z);
      return;
    case 0:
      return;
    }
    if (diameter <= (antialiasThisFrame ? Sphere3D.maxSphereDiameter2
        : Sphere3D.maxSphereDiameter))
      sphere3d.render(shadesCurrent, !addAllPixels, diameter, x, y, z,
          mToEllipsoidal, coef, mDeriv, selectedOctant, octantPoints);
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
    
    if (w < 0) {
      x += w;
      w = -w;
    }
    if (x < 0) {
      w += x;
      x = 0;
    }
    if (x + w >= width)
      w = width - 1 - x;
    int offset = x + width * y;
    if (addAllPixels) {
      for (int i = 0; i <= w; i++) {
        if (z < zbuf[offset])
          addPixel(offset, z, argbCurrent);
        offset++;
      }
      return;
    }
    boolean flipflop = ((x ^ y) & 1) != 0;
    for (int i = 0; i <= w; i++) {
      if ((flipflop = !flipflop) && z < zbuf[offset])
        addPixel(offset, z, argbCurrent);
      offset++;
    }
  }

  private void drawVLine(int x, int y, int z, int h) {
    
    if (h < 0) {
      y += h;
      h = -h;
    }
    if (y < 0) {
      h += y;
      y = 0;
    }
    if (y + h >= height) {
      h = height - 1 - y;
    }
    int offset = x + width * y;
    if (addAllPixels) {
      for (int i = 0; i <= h; i++) {
        if (z < zbuf[offset])
          addPixel(offset, z, argbCurrent);
        offset += width;
      }
      return;
    }
    boolean flipflop = ((x ^ y) & 1) != 0;
    for (int i = 0; i <= h; i++) {
      if ((flipflop = !flipflop) && z < zbuf[offset])
        addPixel(offset, z, argbCurrent);
      offset += width;
    }
  }


  
  public void fillRect(int x, int y, int z, int zSlab, int widthFill, int heightFill) {
    
    if (isClippedZ(zSlab))
      return;
    if (x < 0) {
      widthFill += x;
      if (widthFill <= 0)
        return;
      x = 0;
    }
    if (x + widthFill > width) {
      widthFill = width - x;
      if (widthFill <= 0)
        return;
    }
    if (y < 0) {
      heightFill += y;
      if (heightFill <= 0)
        return;
      y = 0;
    }
    if (y + heightFill > height)
      heightFill = height - y;
    while (--heightFill >= 0)
      plotPixelsUnclipped(widthFill, x, y++, z);
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
    if(font3d != null)
      font3dCurrent = font3d;
    plotText(xBaseline, yBaseline, z, argbCurrent, str, font3dCurrent, null);
  }
  
  public void plotText(int x, int y, int z, int argb,
                String text, Font3D font3d, JmolRendererInterface jmolRenderer) {
    Text3D.plot(x, y, z, argb, text, font3d, this, jmolRenderer, 
        antialiasThisFrame);    
  }
  
  public void drawImage(Image image, int x, int y, int z, int zSlab, 
                        short bgcolix, int width, int height) {
    if (image == null || width == 0 || height == 0)
      return;
    if (isClippedZ(zSlab))
      return;
    plotImage(x, y, z, image, null, bgcolix, width, height);
  }

  public void plotImage(int x, int y, int z, Image image, JmolRendererInterface jmolRenderer,
                        short bgcolix, int width, int height) {
    setColix(bgcolix);
    if (bgcolix == 0)
      argbCurrent = 0;
    Text3D.plotImage(x, y, z, image, this, jmolRenderer, antialiasThisFrame, argbCurrent, 
        width, height);
  }

  public void setFont(byte fid) {
    font3dCurrent = Font3D.getFont3D(fid);
  }
  
  public void setFont(Font3D font3d) {
    font3dCurrent = font3d;
  }
  
  public Font3D getFont3DCurrent() {
    return font3dCurrent;
  }

  boolean currentlyRendering;

  

  

  

  
  public void drawPixel(int x, int y, int z) {
    
    plotPixelClipped(x, y, z);
  }

  public void drawPoints(int count, int[] coordinates) {
    
    plotPoints(count, coordinates);
  }

  

  public void drawDashedLine(int run, int rise, Point3i pointA, Point3i pointB) {
    
    line3d.plotDashedLine(argbCurrent, !addAllPixels, run, rise, 
        pointA.x, pointA.y, pointA.z,
        pointB.x, pointB.y, pointB.z, true);
  }

  public void drawDottedLine(Point3i pointA, Point3i pointB) {
     
    line3d.plotDashedLine(argbCurrent, !addAllPixels, 2, 1,
                          pointA.x, pointA.y, pointA.z,
                          pointB.x, pointB.y, pointB.z, true);
  }

  public void drawLine(int x1, int y1, int z1, int x2, int y2, int z2) {
    
    line3d.plotLine(argbCurrent, !addAllPixels, argbCurrent, !addAllPixels,
                    x1, y1, z1, x2, y2, z2, true);
  }

  public void drawLine(short colixA, short colixB,
                       int x1, int y1, int z1, int x2, int y2, int z2) {
    
    if (!setColix(colixA))
      colixA = 0;
    boolean isScreenedA = !addAllPixels;
    int argbA = argbCurrent;
    if (!setColix(colixB))
      colixB = 0;
    if (colixA == 0 && colixB == 0)
      return;
    line3d.plotLine(argbA, isScreenedA, argbCurrent, !addAllPixels,
                    x1, y1, z1, x2, y2, z2, true);
  }
  
  public void drawLine(Point3i pointA, Point3i pointB) {
    
    line3d.plotLine(argbCurrent, !addAllPixels, argbCurrent, !addAllPixels,
                    pointA.x, pointA.y, pointA.z,
                    pointB.x, pointB.y, pointB.z, true);
  }
  
  public void fillCylinder(short colixA, short colixB, byte endcaps,
                           int diameter,
                           int xA, int yA, int zA, int xB, int yB, int zB) {
    
    if (!setColix(colixA))
      colixA = 0;
    boolean isScreenedA = !addAllPixels;
    if (!setColix(colixB))
      colixB = 0;
    if (colixA == 0 && colixB == 0)
      return;
    cylinder3d.render(colixA, colixB, isScreenedA, !addAllPixels, endcaps, diameter,
                      xA, yA, zA, xB, yB, zB);
  }

  public void fillCylinder(byte endcaps,
                           int diameter,
                           int xA, int yA, int zA, int xB, int yB, int zB) {
    
    cylinder3d.render(colixCurrent, colixCurrent, !addAllPixels, !addAllPixels, endcaps, diameter,
                      xA, yA, zA, xB, yB, zB);
  }

  public void fillCylinder(byte endcaps, int diameter,
                           Point3i screenA, Point3i screenB) {
    
    cylinder3d.render(colixCurrent, colixCurrent, !addAllPixels, !addAllPixels, endcaps, diameter,
                      screenA.x, screenA.y, screenA.z,
                      screenB.x, screenB.y, screenB.z);
  }

  public void fillCylinderBits(byte endcaps, int diameter,
                               Point3f screenA, Point3f screenB) {
   
   cylinder3d.renderBits(colixCurrent, colixCurrent, !addAllPixels, !addAllPixels, endcaps, diameter,
       screenA.x, screenA.y, screenA.z,
       screenB.x, screenB.y, screenB.z);
 }

  public void fillCone(byte endcap, int diameter,
                       Point3i screenBase, Point3i screenTip) {
    
    cylinder3d.renderCone(colixCurrent, !addAllPixels, endcap, diameter,
                          screenBase.x, screenBase.y, screenBase.z,
                          screenTip.x, screenTip.y, screenTip.z, false);
  }

  public void fillCone(byte endcap, int diameter,
                       Point3f screenBase, Point3f screenTip) {
    
    cylinder3d.renderCone(colixCurrent, !addAllPixels, endcap, diameter,
                          screenBase.x, screenBase.y, screenBase.z,
                          screenTip.x, screenTip.y, screenTip.z, true);
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
  
  public static void getHermiteList(int tension, Tuple3f s0, Tuple3f s1, Tuple3f s2, Tuple3f s3, Tuple3f s4, Tuple3f[] list, int index0, int n) {
    Hermite3D.getHermiteList(tension, s0, s1, s2, s3, s4, list, index0, n);
  }

  

  public void drawTriangle(Point3i screenA, short colixA, Point3i screenB,
                           short colixB, Point3i screenC, short colixC, int check) {
    
    int xA = screenA.x;
    int yA = screenA.y;
    int zA = screenA.z;
    int xB = screenB.x;
    int yB = screenB.y;
    int zB = screenB.z;
    int xC = screenC.x;
    int yC = screenC.y;
    int zC = screenC.z;
    if ((check & 1) == 1)
      drawLine(colixA, colixB, xA, yA, zA, xB, yB, zB);
    if ((check & 2) == 2)
      drawLine(colixB, colixC, xB, yB, zB, xC, yC, zC);
    if ((check & 4) == 4)
      drawLine(colixA, colixC, xA, yA, zA, xC, yC, zC);
  }

  public void drawTriangle(Point3i screenA, Point3i screenB,
                           Point3i screenC, int check) {
    
    int xA = screenA.x;
    int yA = screenA.y;
    int zA = screenA.z;
    int xB = screenB.x;
    int yB = screenB.y;
    int zB = screenB.z;
    int xC = screenC.x;
    int yC = screenC.y;
    int zC = screenC.z;
    if ((check & 1) == 1)
      line3d.plotLine(argbCurrent, !addAllPixels, argbCurrent, !addAllPixels,
          xA, yA, zA, xB, yB, zB, true);
    if ((check & 2) == 2)
      line3d.plotLine(argbCurrent, !addAllPixels, argbCurrent, !addAllPixels,
          xB, yB, zB, xC, yC, zC, true);
    if ((check & 4) == 4)
      line3d.plotLine(argbCurrent, !addAllPixels, argbCurrent, !addAllPixels,
          xA, yA, zA, xC, yC, zC, true);
  }

  public void drawCylinderTriangle(int xA, int yA, int zA, int xB,
                                   int yB, int zB, int xC, int yC, int zC,
                                   int diameter) {
    
    fillCylinder(ENDCAPS_SPHERICAL, diameter, xA, yA,
        zA, xB, yB, zB);
    fillCylinder(ENDCAPS_SPHERICAL, diameter, xA, yA,
        zA, xC, yC, zC);
    fillCylinder(ENDCAPS_SPHERICAL, diameter, xB, yB,
        zB, xC, yC, zC);
  }

  public void drawfillTriangle(int xA, int yA, int zA, int xB,
                               int yB, int zB, int xC, int yC, int zC) {
    
    line3d.plotLine(argbCurrent, !addAllPixels, argbCurrent, !addAllPixels, xA,
        yA, zA, xB, yB, zB, true);
    line3d.plotLine(argbCurrent, !addAllPixels, argbCurrent, !addAllPixels, xA,
        yA, zA, xC, yC, zC, true);
    line3d.plotLine(argbCurrent, !addAllPixels, argbCurrent, !addAllPixels, xB,
        yB, zB, xC, yC, zC, true);
    triangle3d.fillTriangle(xA, yA, zA, xB, yB, zB, xC, yC, zC, false);
  }
  
  public void fillTriangle(Point3i screenA, int intensityA,
                           Point3i screenB, int intensityB,
                           Point3i screenC, int intensityC) {
    triangle3d.setGouraud(intensityA, intensityB, intensityC);
    triangle3d.fillTriangle(screenA, screenB, screenC, true);
  }
  
  public void fillTriangle(Point3i screenA, short colixA, short normixA,
                           Point3i screenB, short colixB, short normixB,
                           Point3i screenC, short colixC, short normixC) {
    
    boolean useGouraud;
    if (normixA == normixB && normixA == normixC &&
        colixA == colixB && colixA == colixC) {
      setColixAndIntensity(colixA, normix3d.getIntensity(normixA));
      useGouraud = false;
    } else {
      triangle3d.setGouraud(getShades(colixA)[normix3d.getIntensity(normixA)],
                            getShades(colixB)[normix3d.getIntensity(normixB)],
                            getShades(colixC)[normix3d.getIntensity(normixC)]);
      int translucentCount = 0;
      if (isColixTranslucent(colixA))
        ++translucentCount;
      if (isColixTranslucent(colixB))
        ++translucentCount;
      if (isColixTranslucent(colixC))
        ++translucentCount;
      isTranslucent = translucentCount >= 2;
      useGouraud = true;
    }
    triangle3d.fillTriangle(screenA, screenB, screenC, useGouraud);
  }

  public void fillTriangle(short normix,
                           int xScreenA, int yScreenA, int zScreenA,
                           int xScreenB, int yScreenB, int zScreenB,
                           int xScreenC, int yScreenC, int zScreenC) {
    
    setColorNoisy(normix3d.getIntensity(normix));
    triangle3d.fillTriangle( xScreenA, yScreenA, zScreenA,
        xScreenB, yScreenB, zScreenB,
        xScreenC, yScreenC, zScreenC, false);
  }

  public void fillTriangle(Point3f screenA, Point3f screenB, Point3f screenC) {
    
    setColorNoisy(calcIntensityScreen(screenA, screenB, screenC));
    triangle3d.fillTriangle(screenA, screenB, screenC, false);
  }

  public void fillTriangle(Point3i screenA, Point3i screenB, Point3i screenC) {
    
    triangle3d.fillTriangle(screenA, screenB, screenC, false);
  }

  public void fillTriangle(Point3i screenA, short colixA,
                                   short normixA, Point3i screenB,
                                   short colixB, short normixB,
                                   Point3i screenC, short colixC,
                                   short normixC, float factor) {
    
    boolean useGouraud;
    if (normixA == normixB && normixA == normixC && colixA == colixB
        && colixA == colixC) {
      setColixAndIntensity(colixA, normix3d.getIntensity(normixA));
      useGouraud = false;
    } else {
      triangle3d.setGouraud(getShades(colixA)[normix3d.getIntensity(normixA)],
          getShades(colixB)[normix3d.getIntensity(normixB)],
          getShades(colixC)[normix3d.getIntensity(normixC)]);
      int translucentCount = 0;
      if (isColixTranslucent(colixA))
        ++translucentCount;
      if (isColixTranslucent(colixB))
        ++translucentCount;
      if (isColixTranslucent(colixC))
        ++translucentCount;
      isTranslucent = translucentCount >= 2;
      useGouraud = true;
    }
    triangle3d.fillTriangle(screenA, screenB, screenC, factor,
        useGouraud);
  }

  
  
  public void drawQuadrilateral(short colix, Point3i screenA, Point3i screenB,
                                Point3i screenC, Point3i screenD) {
    
    setColix(colix);
    drawLine(screenA, screenB);
    drawLine(screenB, screenC);
    drawLine(screenC, screenD);
    drawLine(screenD, screenA);
  }

  public void fillQuadrilateral(Point3f screenA, Point3f screenB,
                                Point3f screenC, Point3f screenD) {
    
    setColorNoisy(calcIntensityScreen(screenA, screenB, screenC));
    triangle3d.fillTriangle(screenA, screenB, screenC, false);
    triangle3d.fillTriangle(screenA, screenC, screenD, false);
  }

  public void fillQuadrilateral(Point3i screenA, short colixA, short normixA,
                                Point3i screenB, short colixB, short normixB,
                                Point3i screenC, short colixC, short normixC,
                                Point3i screenD, short colixD, short normixD) {
    
    fillTriangle(screenA, colixA, normixA,
                 screenB, colixB, normixB,
                 screenC, colixC, normixC);
    fillTriangle(screenA, colixA, normixA,
                 screenC, colixC, normixC,
                 screenD, colixD, normixD);
  }

  public void renderIsosurface(Point3f[] vertices, short colix,
                               short[] colixes, Vector3f[] normals,
                               int[][] indices, BitSet bsFaces, int nVertices,
                               int faceVertexMax, short[] polygonColixes, int nPolygons) {
    
  }
  
  

  public boolean isClipped(int x, int y, int z) {
    
    return (x < 0 || x >= width || y < 0 || y >= height || z < slab || z > depth);
  }
  
  public boolean isClipped(int x, int y) {
    return (x < 0 || x >= width || y < 0 || y >= height);
  }

  public boolean isInDisplayRange(int x, int y) {
    return (x >= displayMinX && x < displayMaxX && y >= displayMinY && y < displayMaxY);
  }
  
  public boolean isClippedXY(int diameter, int x, int y) {
    int r = (diameter + 1) >> 1;
    return (x < -r || x >= width + r || y < -r || y >= height + r);
  }
  
  public boolean isClippedZ(int z) {
    return (z != Integer.MIN_VALUE  && (z < slab || z > depth));
  }
  
  final static int yGT = 1;
  final static int yLT = 2;
  final static int xGT = 4;
  final static int xLT = 8;
  final static int zGT = 16;
  final static int zLT = 32;

  public int clipCode(int x, int y, int z) {
    int code = 0;
    if (x < 0)
      code |= xLT;
    else if (x >= width)
      code |= xGT;
    if (y < 0)
      code |= yLT;
    else if (y >= height)
      code |= yGT;
    if (z < slab)
      code |= zLT;
    else if (z > depth) 
      code |= zGT;
  
    return code;
  }

  public int clipCode(int z) {
    int code = 0;
    if (z < slab)
      code |= zLT;
    else if (z > depth) 
      code |= zGT;  
    return code;
  }

  void plotPixelClipped(int x, int y, int z) {
    
    if (isClipped(x, y, z))
      return;
    int offset = y * width + x;
    if (z < zbuf[offset])
      addPixel(offset, z, argbCurrent);
  }

  public void plotPixelClipped(Point3i screen) {
    
    plotPixelClipped(screen.x, screen.y, screen.z);
  }

  void plotPixelClipped(int argb, int x, int y, int z) {
    
    if (isClipped(x, y, z))
      return;
    int offset = y * width + x;
    if (z < zbuf[offset])
      addPixel(offset, z, argb);
  }

  public void plotPixelClippedNoSlab(int argb, int x, int y, int z) {
    
    if (isClipped(x, y))
      return;
    int offset = y * width + x;
    if (z < zbuf[offset])
      addPixel(offset, z, argb);
  }

  void plotPixelClipped(int argb, boolean isScreened, int x, int y, int z) {
    if (isClipped(x, y, z))
      return;
    if (isScreened && ((x ^ y) & 1) != 0)
      return;
    int offset = y * width + x;
    if (z < zbuf[offset])
      addPixel(offset, z, argb);
  }

  void plotPixelUnclipped(int x, int y, int z) {
    
    int offset = y * width + x;
    if (z < zbuf[offset])
      addPixel(offset, z, argbCurrent);
  }
  
  void plotPixelUnclipped(int argb, int x, int y, int z) {
    
    int offset = y * width + x;
    if (z < zbuf[offset])
      addPixel(offset, z, argb);
  }
  
  void plotPixelsClipped(int count, int x, int y, int z) {
    
    
    if (y < 0 || y >= height || x >= width)
      return;
    if (x < 0) {
      count += x; 
      x = 0;
    }
    if (count + x > width)
      count = width - x;
    if (count <= 0)
      return;
    int offsetPbuf = y * width + x;
    int offsetMax = offsetPbuf + count;
    int step = 1;
    if (!addAllPixels) {
      step = 2;
      if (((x ^ y) & 1) != 0)
        ++offsetPbuf;
    }
    while (offsetPbuf < offsetMax) {
      if (z < zbuf[offsetPbuf])
        addPixel(offsetPbuf, z, argbCurrent);
      offsetPbuf += step;
    }
  }

  void plotPixelsClipped(int count, int x, int y, int zAtLeft, int zPastRight,
                         Rgb16 rgb16Left, Rgb16 rgb16Right) {
    
    if (count <= 0 || y < 0 || y >= height || x >= width
        || (zAtLeft < slab && zPastRight < slab)
        || (zAtLeft > depth && zPastRight > depth))
      return;
    int seed = (x << 16) + (y << 1) ^ 0x33333333;
    
    int zScaled = (zAtLeft << 10) + (1 << 9);
    int dz = zPastRight - zAtLeft;
    int roundFactor = count / 2;
    int zIncrementScaled = ((dz << 10) + (dz >= 0 ? roundFactor : -roundFactor))
        / count;
    if (x < 0) {
      x = -x;
      zScaled += zIncrementScaled * x;
      count -= x;
      if (count <= 0)
        return;
      x = 0;
    }
    if (count + x > width)
      count = width - x;
    
    
    boolean flipflop = ((x ^ y) & 1) != 0;
    int offsetPbuf = y * width + x;
    if (rgb16Left == null) {
      while (--count >= 0) {
        if (addAllPixels || (flipflop = !flipflop) == true) {
          int z = zScaled >> 10;
          if (z >= slab && z <= depth && z < zbuf[offsetPbuf]) {
            seed = ((seed << 16) + (seed << 1) + seed) & 0x7FFFFFFF;
            int bits = (seed >> 16) & 0x07;
            addPixel(offsetPbuf, z, bits == 0 ? argbNoisyDn
                : (bits == 1 ? argbNoisyUp : argbCurrent));
          }
        }
        ++offsetPbuf;
        zScaled += zIncrementScaled;
      }
    } else {
      int rScaled = rgb16Left.rScaled << 8;
      int rIncrement = ((rgb16Right.rScaled - rgb16Left.rScaled) << 8) / count;
      int gScaled = rgb16Left.gScaled;
      int gIncrement = (rgb16Right.gScaled - gScaled) / count;
      int bScaled = rgb16Left.bScaled;
      int bIncrement = (rgb16Right.bScaled - bScaled) / count;
      while (--count >= 0) {
        if (addAllPixels || (flipflop = !flipflop)) {
          int z = zScaled >> 10;
          if (z >= slab && z <= depth && z < zbuf[offsetPbuf])
            addPixel(offsetPbuf, z, 0xFF000000 | (rScaled & 0xFF0000)
                | (gScaled & 0xFF00) | ((bScaled >> 8) & 0xFF));
        }
        ++offsetPbuf;
        zScaled += zIncrementScaled;
        rScaled += rIncrement;
        gScaled += gIncrement;
        bScaled += bIncrement;
      }
    }
  }

  
  
  void plotPixelsUnclipped(int count, int x, int y, int zAtLeft,
                           int zPastRight, Rgb16 rgb16Left, Rgb16 rgb16Right) {
    
    if (count <= 0)
      return;
    int seed = (x << 16) + (y << 1) ^ 0x33333333;
    boolean flipflop = ((x ^ y) & 1) != 0;
    
    int zScaled = (zAtLeft << 10) + (1 << 9);
    int dz = zPastRight - zAtLeft;
    int roundFactor = count / 2;
    int zIncrementScaled = ((dz << 10) + (dz >= 0 ? roundFactor : -roundFactor))
        / count;
    int offsetPbuf = y * width + x;
    if (rgb16Left == null) {
      while (--count >= 0) {
        if (addAllPixels || (flipflop = !flipflop)) {
          int z = zScaled >> 10;
          if (z < zbuf[offsetPbuf]) {
            seed = ((seed << 16) + (seed << 1) + seed) & 0x7FFFFFFF;
            int bits = (seed >> 16) & 0x07;
            addPixel(offsetPbuf, z, bits == 0 ? argbNoisyDn
                : (bits == 1 ? argbNoisyUp : argbCurrent));
          }
        }
        ++offsetPbuf;
        zScaled += zIncrementScaled;
      }
    } else {
      int rScaled = rgb16Left.rScaled << 8;
      int rIncrement = ((rgb16Right.rScaled - rgb16Left.rScaled) << 8) / count;
      int gScaled = rgb16Left.gScaled;
      int gIncrement = (rgb16Right.gScaled - gScaled) / count;
      int bScaled = rgb16Left.bScaled;
      int bIncrement = (rgb16Right.bScaled - bScaled) / count;
      while (--count >= 0) {
        if (addAllPixels || (flipflop = !flipflop)) {
          int z = zScaled >> 10;
          if (z < zbuf[offsetPbuf])
            addPixel(offsetPbuf, z, 0xFF000000 | (rScaled & 0xFF0000)
                | (gScaled & 0xFF00) | ((bScaled >> 8) & 0xFF));
        }
        ++offsetPbuf;
        zScaled += zIncrementScaled;
        rScaled += rIncrement;
        gScaled += gIncrement;
        bScaled += bIncrement;
      }
    }
  }

  
  void plotPixelsUnclipped(int count, int x, int y, int z) {
    
    
    
    int offsetPbuf = y * width + x;
    if (addAllPixels) {
      while (--count >= 0) {
        if (z < zbuf[offsetPbuf])
          addPixel(offsetPbuf, z, argbCurrent);
        ++offsetPbuf;
      }
    } else {
      int offsetMax = offsetPbuf + count;
      if (((x ^ y) & 1) != 0)
        if (++offsetPbuf == offsetMax)
          return;
      do {
        if (z < zbuf[offsetPbuf])
          addPixel(offsetPbuf, z, argbCurrent);
        offsetPbuf += 2;
      } while (offsetPbuf < offsetMax);
    }
  }

  private void plotPoints(int count, int[] coordinates) {
    for (int i = count * 3; i > 0; ) {
      int z = coordinates[--i];
      int y = coordinates[--i];
      int x = coordinates[--i];
      if (isClipped(x, y, z))
        continue;
      int offset = y * width + x++;
      if (z < zbuf[offset])
        addPixel(offset, z, argbCurrent);
      if (antialiasThisFrame) {
        offset = y * width + x;
        if (!isClipped(x, y, z) && z < zbuf[offset])
          addPixel(offset, z, argbCurrent);
        offset = (++y)* width + x;
        if (!isClipped(x, y, z) && z < zbuf[offset])
          addPixel(offset, z, argbCurrent);
        offset = y * width + (--x);
        if (!isClipped(x, y, z) && z < zbuf[offset])
          addPixel(offset, z, argbCurrent);
      }

    }
  }

  
  

  
  private final static short CHANGEABLE_MASK       = (short)0x8000; 
  private final static short UNMASK_CHANGEABLE_TRANSLUCENT =0x07FF;
  private final static int   TRANSLUCENT_SHIFT        = 11;
  private final static int   ALPHA_SHIFT              = 24 - TRANSLUCENT_SHIFT;
  private final static int   TRANSLUCENT_MASK         = 0xF << TRANSLUCENT_SHIFT; 
  private final static int   TRANSLUCENT_SCREENED     = TRANSLUCENT_MASK;
  private final static int   TRANSPARENT              =  8 << TRANSLUCENT_SHIFT;  
  final static int           TRANSLUCENT_50           =  4 << TRANSLUCENT_SHIFT;  
  public final static short  OPAQUE_MASK              = ~TRANSLUCENT_MASK;


  private final static short INHERIT_COLOR       = 1;
  final static short         UNUSED_OPTION3      = 3;
  final static short         SPECIAL_COLIX_MAX   = 4;

  
  public static int calcGreyscaleRgbFromRgb(int rgb) {
    int grey = ((2989 * ((rgb >> 16) & 0xFF)) +
                (5870 * ((rgb >> 8) & 0xFF)) +
                (1140 * (rgb & 0xFF)) + 5000) / 10000;
    int greyRgb = (grey << 16) | (grey << 8) | grey | 0xFF000000;
    return greyRgb;
  }

  public static short getColix(int argb) {
    return Colix3D.getColix(argb); 
  }

  public final static Point3f colorPointFromInt(int color, Point3f pt) {
    pt.z = color & 0xFF;
    pt.y = (color >> 8) & 0xFF;
    pt.x = (color >> 16) & 0xFF;
    return pt;
  }

  public final static Point3f colorPointFromInt2(int color) {
    return new Point3f((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF);
  }

  public final static Point3f colorPointFromString(String colorName, Point3f pt) {
    return colorPointFromInt(getArgbFromString(colorName), pt);
  }

  public static short getColix(String colorName) {
    int argb = getArgbFromString(colorName);
    if (argb != 0)
      return Colix3D.getColix(argb);
    if ("none".equalsIgnoreCase(colorName))
      return INHERIT_ALL;
    if ("opaque".equalsIgnoreCase(colorName))
      return INHERIT_COLOR;
    return USE_PALETTE;
  }

  
  private final static short applyColorTranslucencyLevel(short colix,
                                                         float translucentLevel) {
    
    
    
    
    
    

    if (translucentLevel == 0) 
      return (short) (colix & ~TRANSLUCENT_MASK);
    if (translucentLevel < 0) 
      return (short) (colix | TRANSLUCENT_MASK);
    if (translucentLevel >= 255 || translucentLevel == 1.0)
      return (short) (colix | TRANSPARENT);
    int iLevel = (int) (translucentLevel < 1 ? translucentLevel * 256
            : translucentLevel <= 9 ? ((int) (translucentLevel-1)) << 5
               : translucentLevel < 15 ? 8 << 5 : translucentLevel);
    iLevel = (iLevel >> 5) % 16;
    return (short) (colix & ~TRANSLUCENT_MASK | (iLevel << TRANSLUCENT_SHIFT));
  }

  public final static int getColixTranslucencyLevel(short colix) {
    int logAlpha = (colix >> TRANSLUCENT_SHIFT) & 15;
    switch (logAlpha) {
    case 1:
    case 2:
    case 3:
    case 4:
    case 5:
    case 6:
    case 7:
      return logAlpha << 5;
    case 15:
      return -1;
    default:
      return 255;
    }
  }
  
  public static short getColix(Object obj) {
    if (obj == null)
      return INHERIT_ALL;
    if (obj instanceof Byte)
      return (((Byte) obj).byteValue() == 0 ? INHERIT_ALL
          : USE_PALETTE);
    if (obj instanceof Integer)
      return Colix3D.getColix(((Integer) obj).intValue());
    if (obj instanceof String)
      return getColix((String) obj);
    if (Logger.debugging) {
      Logger.debug("?? getColix(" + obj + ")");
    }
    return HOTPINK;
  }

  public final static short getColixTranslucent(short colix, boolean isTranslucent, float translucentLevel) {
    if (colix == INHERIT_ALL)
      colix = INHERIT_COLOR;
    colix &= ~TRANSLUCENT_MASK;
    if (!isTranslucent)
      return colix;
    return applyColorTranslucencyLevel(colix, translucentLevel);
  }

  public int getColixArgb(short colix) {
    if (colix < 0)
      colix = changeableColixMap[colix & UNMASK_CHANGEABLE_TRANSLUCENT];
    if (! inGreyscaleMode)
      return Colix3D.getArgb(colix);
    return Colix3D.getArgbGreyscale(colix);
  }

  int[] getShades(short colix) {
    if (colix < 0)
      colix = changeableColixMap[colix & UNMASK_CHANGEABLE_TRANSLUCENT];
    if (! inGreyscaleMode)
      return Colix3D.getShades(colix);
    return Colix3D.getShadesGreyscale(colix);
  }

  public final static short getChangeableColixIndex(short colix) {
    if (colix >= 0)
      return -1;
    return (short)(colix & UNMASK_CHANGEABLE_TRANSLUCENT);
  }

  public final static boolean isColixTranslucent(short colix) {
    return ((colix & TRANSLUCENT_MASK) != 0);
  }

  public final static short getColixInherited(short myColix, short parentColix) {
    switch (myColix) {
    case INHERIT_ALL:
      return parentColix;
    case INHERIT_COLOR:
      return (short) (parentColix & OPAQUE_MASK);
    default:
      
      
      return ((myColix & OPAQUE_MASK) == INHERIT_COLOR ? (short) (parentColix
          & OPAQUE_MASK | myColix & TRANSLUCENT_MASK) : myColix);
    }
  }

  public final static boolean isColixColorInherited(short colix) {
    switch (colix) {
    case INHERIT_ALL:
    case INHERIT_COLOR:
      return true;
    default: 
      return (colix & OPAQUE_MASK) == INHERIT_COLOR; 
    }
  }
  
  

  
  
  public String getHexColorFromIndex(short colix) {
    int argb = getColixArgb(colix);
    return getHexColorFromRGB(argb);
  }
  
  public static String getHexColorFromRGB(int argb) {
    if (argb == 0)
      return null;
    String r  = "00" + Integer.toHexString((argb >> 16) & 0xFF);
    r = r.substring(r.length() - 2);
    String g  = "00" + Integer.toHexString((argb >> 8) & 0xFF);
    g = g.substring(g.length() - 2);
    String b  = "00" + Integer.toHexString(argb & 0xFF);
    b = b.substring(b.length() - 2);
    return r + g + b;
  }

  

  private short[] changeableColixMap = new short[16];

  public short getChangeableColix(short id, int argb) {
    if (id >= changeableColixMap.length) {
      short[] t = new short[id + 16];
      System.arraycopy(changeableColixMap, 0, t, 0, changeableColixMap.length);
      changeableColixMap = t;
    }
    if (changeableColixMap[id] == 0)
      changeableColixMap[id] = Colix3D.getColix(argb);
    return (short)(id | CHANGEABLE_MASK);
  }

  public void changeColixArgb(short id, int argb) {
    if (id < changeableColixMap.length && changeableColixMap[id] != 0)
      changeableColixMap[id] = Colix3D.getColix(argb);
  }

  

  public static void flushShadesAndSphereCaches() { 
    Colix3D.flushShades();
    Sphere3D.flushSphereCache();
  }

  final static float[] lighting = Shade3D.lighting;
  
  public synchronized static void setSpecular(boolean specular) {
    lighting[Shade3D.SPECULAR_ON] = (specular ? 1f : 0f);
  }

  public static boolean getSpecular() {
    return (lighting[Shade3D.SPECULAR_ON] != 0);
  }

  public synchronized static void setSpecularPower(int specularPower) {
    lighting[Shade3D.SPECULAR_POWER] = specularPower;
    lighting[Shade3D.INTENSE_FRACTION] = specularPower / 100f;
  }
  
  public static int getSpecularPower() {
    return (int) lighting[Shade3D.SPECULAR_POWER];
  }
  
  public synchronized static void setSpecularPercent(int specularPercent) {
    lighting[Shade3D.SPECULAR_PERCENT]= specularPercent;
    lighting[Shade3D.INTENSITY_SPECULAR] = specularPercent / 100f;
  }

  public static int getSpecularPercent() {
    return (int) lighting[Shade3D.SPECULAR_PERCENT];
  }

  public synchronized static void setSpecularExponent(int specularExponent) {
    lighting[Shade3D.SPECULAR_EXPONENT] = specularExponent;
  }
  
  public static int getSpecularExponent() {
    return (int) lighting[Shade3D.SPECULAR_EXPONENT];
  }
  
  public synchronized static void setDiffusePercent(int diffusePercent) {
    lighting[Shade3D.DIFFUSE_PERCENT]= diffusePercent;
    lighting[Shade3D.INTENSITY_DIFFUSE]= diffusePercent / 100f;
  }

  public static int getDiffusePercent() {
    return (int) lighting[Shade3D.DIFFUSE_PERCENT];
  }
  
  public synchronized static void setAmbientPercent(int ambientPercent) {
    lighting[Shade3D.AMBIENT_PERCENT] = ambientPercent;
    lighting[Shade3D.AMBIENT_FRACTION] = ambientPercent / 100f;
  }

  public static int getAmbientPercent() {
    return (int) (lighting[Shade3D.AMBIENT_PERCENT]);
  }
  
  public static Point3f getLightSource() {
    return new Point3f(Shade3D.xLight, Shade3D.yLight, Shade3D.zLight);
  }
  
  
  
  private final Vector3f vectorAB = new Vector3f();
  private final Vector3f vectorAC = new Vector3f();
  private final Vector3f vectorNormal = new Vector3f();

  public int calcSurfaceShade(Point3i screenA, Point3i screenB, Point3i screenC) {
    
    vectorAB.set(screenB.x - screenA.x, screenB.y - screenA.y, screenB.z
        - screenA.z);
    int intensity;
    if (screenC == null) {
      intensity = Shade3D.calcIntensity(-vectorAB.x, -vectorAB.y, vectorAB.z);
    } else {
      vectorAC.set(screenC.x - screenA.x, screenC.y - screenA.y, screenC.z
          - screenA.z);
      vectorAB.cross(vectorAB, vectorAC);
      intensity = vectorAB.z >= 0 ? Shade3D.calcIntensity(-vectorAB.x,
          -vectorAB.y, vectorAB.z) : Shade3D.calcIntensity(vectorAB.x,
          vectorAB.y, -vectorAB.z);
    }
    if (intensity > intensitySpecularSurfaceLimit)
      intensity = intensitySpecularSurfaceLimit;
    setColorNoisy(intensity);
    return argbCurrent;
  }

  private int calcIntensityScreen(Point3f screenA,
                                 Point3f screenB, Point3f screenC) {
    
    vectorAB.sub(screenB, screenA);
    vectorAC.sub(screenC, screenA);
    vectorNormal.cross(vectorAB, vectorAC);
    return
      (vectorNormal.z >= 0
            ? Shade3D.calcIntensity(-vectorNormal.x, -vectorNormal.y,
                                    vectorNormal.z)
            : Shade3D.calcIntensity(vectorNormal.x, vectorNormal.y,
                                    -vectorNormal.z));
  }

  

  public Font3D getFont3D(float fontSize) {
    return Font3D.getFont3D(Font3D.FONT_FACE_SANS,
                            Font3D.FONT_STYLE_PLAIN, fontSize, fontSize, platform);
  }

  public Font3D getFont3D(String fontFace, float fontSize) {
    return Font3D.getFont3D(Font3D.getFontFaceID(fontFace),
                            Font3D.FONT_STYLE_PLAIN, fontSize, fontSize, platform);
  }
    
  
  public Font3D getFont3D(String fontFace, String fontStyle, float fontSize) {
    return Font3D.getFont3D(Font3D.getFontFaceID(fontFace),
                            Font3D.getFontStyleID(fontStyle), fontSize, fontSize, platform);
  }

  public Font3D getFont3DScaled(Font3D font, float scale) {
    float newScale = font.fontSizeNominal * scale;
    return (newScale == font.fontSize ? font : Font3D.getFont3D(font.idFontFace,
        font.idFontStyle, newScale, font.fontSizeNominal, platform));
  }

  public byte getFontFid(float fontSize) {
    return getFont3D(fontSize).fid;
  }

  public byte getFontFid(String fontFace, float fontSize) {
    return getFont3D(fontFace, fontSize).fid;
  }

  

  
  
  

  private final static String[] colorNames = {
    "black",                
    "pewhite",              
    "pecyan",               
    "pepurple",             
    "pegreen",              
    "peblue",               
    "peviolet",             
    "pebrown",              
    "pepink",               
    "peyellow",             
    "pedarkgreen",          
    "peorange",             
    "pelightblue",          
    "pedarkcyan",           
    "pedarkgray",           

    "aliceblue",            
    "antiquewhite",         
    "aqua",                 
    "aquamarine",           
    "azure",                
    "beige",                
    "bisque",               
    "blanchedalmond",       
    "blue",                 
    "blueviolet",           
    "brown",                
    "burlywood",            
    "cadetblue",            
    "chartreuse",           
    "chocolate",            
    "coral",                
    "cornflowerblue",       
    "cornsilk",             
    "crimson",              
    "cyan",                 
    "darkblue",             
    "darkcyan",             
    "darkgoldenrod",        
    "darkgray",             
    "darkgreen",            
    "darkkhaki",            
    "darkmagenta",          
    "darkolivegreen",       
    "darkorange",           
    "darkorchid",           
    "darkred",              
    "darksalmon",           
    "darkseagreen",         
    "darkslateblue",        
    "darkslategray",        
    "darkturquoise",        
    "darkviolet",           
    "deeppink",             
    "deepskyblue",          
    "dimgray",              
    "dodgerblue",           
    "firebrick",            
    "floralwhite",          
    "forestgreen",          
    "fuchsia",              
    "gainsboro",            
    "ghostwhite",           
    "gold",                 
    "goldenrod",            
    "gray",                 
    "green",                
    "greenyellow",          
    "honeydew",             
    "hotpink",              
    "indianred",            
    "indigo",               
    "ivory",                
    "khaki",                
    "lavender",             
    "lavenderblush",        
    "lawngreen",            
    "lemonchiffon",         
    "lightblue",            
    "lightcoral",           
    "lightcyan",            
    "lightgoldenrodyellow", 
    "lightgreen",           
    "lightgrey",            
    "lightpink",            
    "lightsalmon",          
    "lightseagreen",        
    "lightskyblue",         
    "lightslategray",       
    "lightsteelblue",       
    "lightyellow",          
    "lime",                 
    "limegreen",            
    "linen",                
    "magenta",              
    "maroon",               
    "mediumaquamarine",     
    "mediumblue",           
    "mediumorchid",         
    "mediumpurple",         
    "mediumseagreen",       
    "mediumslateblue",      
    "mediumspringgreen",    
    "mediumturquoise",      
    "mediumvioletred",      
    "midnightblue",         
    "mintcream",            
    "mistyrose",            
    "moccasin",             
    "navajowhite",          
    "navy",                 
    "oldlace",              
    "olive",                
    "olivedrab",            
    "orange",               
    "orangered",            
    "orchid",               
    "palegoldenrod",        
    "palegreen",            
    "paleturquoise",        
    "palevioletred",        
    "papayawhip",           
    "peachpuff",            
    "peru",                 
    "pink",                 
    "plum",                 
    "powderblue",           
    "purple",               
    "red",                  
    "rosybrown",            
    "royalblue",            
    "saddlebrown",          
    "salmon",               
    "sandybrown",           
    "seagreen",             
    "seashell",             
    "sienna",               
    "silver",               
    "skyblue",              
    "slateblue",            
    "slategray",            
    "snow",                 
    "springgreen",          
    "steelblue",            
    "tan",                  
    "teal",                 
    "thistle",              
    "tomato",               
    "turquoise",            
    "violet",               
    "wheat",                
    "white",                
    "whitesmoke",           
    "yellow",               
    "yellowgreen",          
    
    "bluetint",             
    "greenblue",            
    "greentint",            
    "grey",                 
    "pinktint",             
    "redorange",            
    "yellowtint",           
  };

  public static int getColorArgb(int i) {
    return colorArgbs[i % colorArgbs.length];
  }

  private final static int[] colorArgbs = {
    0xFF000000, 
    
    0xFFffffff, 
    0xFF00ffff, 
    0xFFd020ff, 
    0xFF00ff00, 
    0xFF6060ff, 
    0xFFff80c0, 
    0xFFa42028, 
    0xFFffd8d8, 
    0xFFffff00, 
    0xFF00c000, 
    0xFFffb000, 
    0xFFb0b0ff, 
    0xFF00a0a0, 
    0xFF606060, 
    
    0xFFF0F8FF, 
    0xFFFAEBD7, 
    0xFF00FFFF, 
    0xFF7FFFD4, 
    0xFFF0FFFF, 
    0xFFF5F5DC, 
    0xFFFFE4C4, 
    0xFFFFEBCD, 
    0xFF0000FF, 
    0xFF8A2BE2, 
    0xFFA52A2A, 
    0xFFDEB887, 
    0xFF5F9EA0, 
    0xFF7FFF00, 
    0xFFD2691E, 
    0xFFFF7F50, 
    0xFF6495ED, 
    0xFFFFF8DC, 
    0xFFDC143C, 
    0xFF00FFFF, 
    0xFF00008B, 
    0xFF008B8B, 
    0xFFB8860B, 
    0xFFA9A9A9, 
    0xFF006400, 

    0xFFBDB76B, 
    0xFF8B008B, 
    0xFF556B2F, 
    0xFFFF8C00, 
    0xFF9932CC, 
    0xFF8B0000, 
    0xFFE9967A, 
    0xFF8FBC8F, 
    0xFF483D8B, 
    0xFF2F4F4F, 
    0xFF00CED1, 
    0xFF9400D3, 
    0xFFFF1493, 
    0xFF00BFFF, 
    0xFF696969, 
    0xFF1E90FF, 
    0xFFB22222, 
    0xFFFFFAF0, 
    0xFF228B22, 
    0xFFFF00FF, 
    0xFFDCDCDC, 
    0xFFF8F8FF, 
    0xFFFFD700, 
    0xFFDAA520, 
    0xFF808080, 
    0xFF008000, 
    0xFFADFF2F, 
    0xFFF0FFF0, 
    0xFFFF69B4, 
    0xFFCD5C5C, 
    0xFF4B0082, 
    0xFFFFFFF0, 
    0xFFF0E68C, 
    0xFFE6E6FA, 
    0xFFFFF0F5, 
    0xFF7CFC00, 
    0xFFFFFACD, 
    0xFFADD8E6, 
    0xFFF08080, 
    0xFFE0FFFF, 
    0xFFFAFAD2, 
    0xFF90EE90, 
    0xFFD3D3D3, 
    0xFFFFB6C1, 
    0xFFFFA07A, 
    0xFF20B2AA, 
    0xFF87CEFA, 
    0xFF778899, 
    0xFFB0C4DE, 
    0xFFFFFFE0, 
    0xFF00FF00, 
    0xFF32CD32, 
    0xFFFAF0E6, 
    0xFFFF00FF, 
    0xFF800000, 
    0xFF66CDAA, 
    0xFF0000CD, 
    0xFFBA55D3, 
    0xFF9370DB, 
    0xFF3CB371, 
    0xFF7B68EE, 
    0xFF00FA9A, 
    0xFF48D1CC, 
    0xFFC71585, 
    0xFF191970, 
    0xFFF5FFFA, 
    0xFFFFE4E1, 
    0xFFFFE4B5, 
    0xFFFFDEAD, 
    0xFF000080, 
    0xFFFDF5E6, 
    0xFF808000, 
    0xFF6B8E23, 
    0xFFFFA500, 
    0xFFFF4500, 
    0xFFDA70D6, 
    0xFFEEE8AA, 
    0xFF98FB98, 
    0xFFAFEEEE, 
    0xFFDB7093, 
    0xFFFFEFD5, 
    0xFFFFDAB9, 
    0xFFCD853F, 
    0xFFFFC0CB, 
    0xFFDDA0DD, 
    0xFFB0E0E6, 
    0xFF800080, 
    0xFFFF0000, 
    0xFFBC8F8F, 
    0xFF4169E1, 
    0xFF8B4513, 
    0xFFFA8072, 
    0xFFF4A460, 
    0xFF2E8B57, 
    0xFFFFF5EE, 
    0xFFA0522D, 
    0xFFC0C0C0, 
    0xFF87CEEB, 
    0xFF6A5ACD, 
    0xFF708090, 
    0xFFFFFAFA, 
    0xFF00FF7F, 
    0xFF4682B4, 
    0xFFD2B48C, 
    0xFF008080, 
    0xFFD8BFD8, 
    0xFFFF6347, 
    0xFF40E0D0, 
    0xFFEE82EE, 
    0xFFF5DEB3, 
    0xFFFFFFFF, 
    0xFFF5F5F5, 
    0xFFFFFF00, 
    0xFF9ACD32, 
    
    0xFFAFD7FF, 
    0xFF2E8B57, 
    0xFF98FFB3, 
    0xFF808080, 
    0xFFFFABBB, 
    0xFFFF4500, 
    0xFFF6F675, 
  };

  private static final Hashtable mapJavaScriptColors = new Hashtable();
  static {
    for (int i = colorNames.length; --i >= 0; )
      mapJavaScriptColors.put(colorNames[i], new Integer(colorArgbs[i]));
  }

  
  public static int getArgbFromString(String strColor) {
    int len = 0;
    if (strColor == null || (len = strColor.length()) == 0)
      return 0;
    int red, grn, blu;
    if (strColor.charAt(0) == '[' && strColor.charAt(len - 1) == ']') {
      String check;
      if (strColor.indexOf(",") >= 0) {
        String[] tokens = TextFormat.split(strColor.substring(1, strColor.length() - 1), ",");
        if (tokens.length != 3)
          return 0;
        try {
          red = Integer.parseInt(tokens[0]);
          grn = Integer.parseInt(tokens[1]);
          blu = Integer.parseInt(tokens[2]);
          return (0xFF000000 | (red & 0xFF) << 16 | (grn & 0xFF) << 8 | (blu & 0xFF));
        } catch (NumberFormatException e) {
          return 0;
        }
      }
      switch (len) {
      case 9:
        check = "x";
        break;
      case 10:
        check = "0x";
        break;
      default:
        return 0;
      }
      if (strColor.indexOf(check) != 1)
        return 0;
      strColor = "#" + strColor.substring(len - 7, len - 1);
      len = 7;
    }
    if (len == 7 && strColor.charAt(0) == '#') {
      try {
        red = Integer.parseInt(strColor.substring(1, 3), 16);
        grn = Integer.parseInt(strColor.substring(3, 5), 16);
        blu = Integer.parseInt(strColor.substring(5, 7), 16);
        return (0xFF000000 | (red & 0xFF) << 16 | (grn & 0xFF) << 8 | (blu & 0xFF));
      } catch (NumberFormatException e) {
        return 0;
      }
    }
    Integer boxedArgb = 
        (Integer) mapJavaScriptColors.get(strColor.toLowerCase());
    return (boxedArgb == null ? 0 : boxedArgb.intValue());
  }

  

  public static final short NORMIX_NULL = 9999;

  public short getNormix(Vector3f vector) {
    return normix3d.getNormix(vector.x, vector.y, vector.z,
                              Normix3D.NORMIX_GEODESIC_LEVEL);
  }

  public short getInverseNormix(short normix) {
    if (normix3d.inverseNormixes == null)
      normix3d.calculateInverseNormixes();
    return normix3d.inverseNormixes[normix];
  }

  public short get2SidedNormix(Vector3f vector) {
    return (short)~normix3d.getNormix(vector.x, vector.y, vector.z,
                                      Normix3D.NORMIX_GEODESIC_LEVEL);
  }

  public boolean isDirectedTowardsCamera(short normix) {
    
    return normix3d.isDirectedTowardsCamera(normix);
  }

  public Vector3f[] getTransformedVertexVectors() {
    return normix3d.getTransformedVectors();
  }

  public Vector3f getNormixVector(short normix) {
    return normix3d.getVector(normix);
  }

  public void renderBackground() {
    renderBackground(null);
  }
  
  public void renderBackground(JmolRendererInterface jmolRenderer) {
    if (backgroundImage != null)
      plotImage(Integer.MIN_VALUE, 0, Integer.MIN_VALUE, backgroundImage,
          jmolRenderer, (short) 0, 0, 0);
  }

  
  
  public void endShapeBuffer() {
  }

  public void startShapeBuffer(int iShape) {
  }

  public boolean canDoTriangles() {
    return true;
  }
  
  public boolean isCartesianExport() {
    return false;
  }

  public boolean initializeExporter(String type, Viewer viewer, Graphics3D g3d,
                                    Object output) {
    return false;
  }

  public String finalizeOutput() {
    return null;
  }

  public short[] getBgColixes(short[] bgcolixes) {
    return bgcolixes;
  }

}
