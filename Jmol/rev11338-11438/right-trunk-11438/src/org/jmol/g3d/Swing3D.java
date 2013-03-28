

package org.jmol.g3d;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Image;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.SinglePixelPackedSampleModel;


  
final class Swing3D extends Platform3D {

  private final static DirectColorModel rgbColorModel =
    new DirectColorModel(24, 0x00FF0000, 0x0000FF00, 0x000000FF, 0x00000000);

  private final static int[] sampleModelBitMasks =
  { 0x00FF0000, 0x0000FF00, 0x000000FF };

  private final static DirectColorModel rgbColorModelT =
    new DirectColorModel(32, 0x00FF0000, 0x0000FF00, 0x000000FF, 0xFF000000);

  private final static int[] sampleModelBitMasksT =
  { 0x00FF0000, 0x0000FF00, 0x000000FF, 0xFF000000 };



  Image allocateImage() {
    
    if (false && backgroundTransparent)
      return new BufferedImage(
          rgbColorModelT,
          Raster.createWritableRaster(
              new SinglePixelPackedSampleModel(
                  DataBuffer.TYPE_INT,
                  windowWidth,
                  windowHeight,
                  sampleModelBitMasksT), 
              new DataBufferInt(pBuffer, windowSize),
              null),
          false, 
          null);
    return new BufferedImage(
        rgbColorModel,
        Raster.createWritableRaster(
            new SinglePixelPackedSampleModel(
                DataBuffer.TYPE_INT,
                windowWidth,
                windowHeight,
                sampleModelBitMasks), 
            new DataBufferInt(pBuffer, windowSize),
            null),
        false, 
        null);
  }

  private static boolean backgroundTransparent = false;
  
  void setBackgroundTransparent(boolean tf) {
    backgroundTransparent = tf;
  }

  Image allocateOffscreenImage(int width, int height) {
    return new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
  }

  Graphics getGraphics(Image image) {
    return getStaticGraphics(image);
  }
  
  static Graphics getStaticGraphics(Image image) {
    Graphics2D g2d = ((BufferedImage) image).createGraphics();
    if (backgroundTransparent) {
      
    }
    
    
    
    g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                         RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
    
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                         RenderingHints.VALUE_ANTIALIAS_OFF);
    g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                         RenderingHints.VALUE_RENDER_SPEED);
    return g2d;
  }
}
