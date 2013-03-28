
package org.jmol.g3d;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;

 
abstract class Platform3D {

  int windowWidth, windowHeight, windowSize;
  int bufferWidth, bufferHeight, bufferSize, bufferSizeT;

  Image imagePixelBuffer;
  int[] pBuffer, pBufferT;
  int[] zBuffer, zBufferT;

  int widthOffscreen, heightOffscreen;
  Image imageOffscreen;
  Graphics gOffscreen;

  final static boolean forcePlatformAWT = false;
  final static boolean desireClearingThread = false;
  boolean useClearingThread = true;

  ClearingThread clearingThread;

  static Platform3D createInstance(Component awtComponent) {
    if (awtComponent == null)
      return null;
    boolean jvm12orGreater =
      System.getProperty("java.version").compareTo("1.2") >= 0;
    boolean useSwing = jvm12orGreater && !forcePlatformAWT;
    Platform3D platform =(useSwing
                          ? allocateSwing3D() : new Awt3D(awtComponent));
    platform.initialize(desireClearingThread && useSwing);
    platform.graphicsOffscreen = platform.allocateOffscreenImage(1, 1).getGraphics();

    return platform;
  }

  private static Platform3D allocateSwing3D() {
    
    
    
    return new Swing3D();
  }

  final void initialize(boolean useClearingThread) {
    this.useClearingThread = useClearingThread;
    if (useClearingThread) {
      
      clearingThread = new ClearingThread();
      clearingThread.start();
    }
  }

  abstract Image allocateImage();

  void allocateTBuffers(boolean antialiasTranslucent) {
    bufferSizeT = (antialiasTranslucent ? bufferSize : windowSize);
    zBufferT = new int[bufferSizeT];
    pBufferT = new int[bufferSizeT];    
  }
  
  void allocateBuffers(int width, int height, boolean antialias) {
    windowWidth = width;
    windowHeight = height;
    windowSize = width * height;
    if (antialias) {
      bufferWidth = width * 2;
      bufferHeight = height * 2;
    } else {
      bufferWidth = width;
      bufferHeight = height;
    }
    
    bufferSize = bufferWidth * bufferHeight;
    zBuffer = new int[bufferSize];
    pBuffer = new int[bufferSize];
    
    
    
    
    imagePixelBuffer = allocateImage();
    
  }
  
  void releaseBuffers() {
    windowWidth = windowHeight = bufferWidth = bufferHeight = bufferSize = -1;
    if (imagePixelBuffer != null) {
      imagePixelBuffer.flush();
      imagePixelBuffer = null;
    }
    pBuffer = null;
    zBuffer = null;
    pBufferT = null;
    zBufferT = null;
  }

  boolean hasContent() {
    for (int i = bufferSize; --i >= 0; )
      if (zBuffer[i] != Integer.MAX_VALUE)
        return true;
    return false;
  }

  void clearScreenBuffer() {
    for (int i = bufferSize; --i >= 0; ) {
      zBuffer[i] = Integer.MAX_VALUE;
      pBuffer[i] = 0;
    }
  }

  void setBackgroundColor(int bgColor) {
    if (pBuffer == null)
      return;
    for (int i = bufferSize; --i >= 0; )
      if (pBuffer[i] == 0)
        pBuffer[i] = bgColor;
  }
  
  void clearTBuffer() {
    for (int i = bufferSizeT; --i >= 0; ) {
      zBufferT[i] = Integer.MAX_VALUE;
      pBufferT[i] = 0;
    }
  }
  
  final void obtainScreenBuffer() {
    if (useClearingThread) {
      clearingThread.obtainBufferForClient();
    } else {
      clearScreenBuffer();
    }
  }

  final void clearScreenBufferThreaded() {
    if (useClearingThread)
      clearingThread.releaseBufferForClearing();
  }
  
  void notifyEndOfRendering() {
  }

  Graphics graphicsOffscreen;
  abstract Image allocateOffscreenImage(int width, int height);
  abstract Graphics getGraphics(Image imageOffscreen);
  
  boolean checkOffscreenSize(int width, int height) {
    if (width <= widthOffscreen && height <= heightOffscreen)
      return true;
    if (imageOffscreen != null) {
        gOffscreen.dispose();
        imageOffscreen.flush();
    }
    if (width > widthOffscreen)
      widthOffscreen = (width + 63) & ~63;
    if (height > heightOffscreen)
      heightOffscreen = (height + 15) & ~15;
    imageOffscreen = allocateOffscreenImage(widthOffscreen, heightOffscreen);
    gOffscreen = getGraphics(imageOffscreen);
    return false;
  }

  class ClearingThread extends Thread implements Runnable {


    boolean bufferHasBeenCleared = false;
    boolean clientHasBuffer = false;

    synchronized void notifyBackgroundChange(int argbBackground) {
      
      bufferHasBeenCleared = false;
      notify();
      
    }

    synchronized void obtainBufferForClient() {
      
      while (! bufferHasBeenCleared)
        try { wait(); } catch (InterruptedException ie) {}
      clientHasBuffer = true;
    }

    synchronized void releaseBufferForClearing() {
      
      clientHasBuffer = false;
      bufferHasBeenCleared = false;
      notify();
    }

    synchronized void waitForClientRelease() {
      
      while (clientHasBuffer || bufferHasBeenCleared)
        try { wait(); } catch (InterruptedException ie) {}
    }

    synchronized void notifyBufferReady() {
      
      bufferHasBeenCleared = true;
      notify();
    }

    public void run() {
      
      while (true) {
        waitForClientRelease();
        clearScreenBuffer();
        notifyBufferReady();
      }
    }
  }

  void setBackgroundTransparent(boolean tf) {
  }
}
