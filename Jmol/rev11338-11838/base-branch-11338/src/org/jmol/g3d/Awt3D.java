

package org.jmol.g3d;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.Image;
import java.awt.image.ImageProducer;
import java.awt.image.ImageConsumer;
import java.awt.image.ColorModel;


final class Awt3D extends Platform3D implements ImageProducer {

  Component component;

  ColorModel colorModelRGB;
  ImageConsumer ic;

  Awt3D(Component component) {
    this.component = component;
    colorModelRGB = Toolkit.getDefaultToolkit().getColorModel();
  }

  Image allocateImage() {
    return component.createImage(this);
  }

  void notifyEndOfRendering() {
    if (this.ic != null)
      startProduction(ic);
  }

  Image allocateOffscreenImage(int width, int height) {
    
    Image img = component.createImage(width, height);
    
    return img;
  }

  Graphics getGraphics(Image image) {
    return image.getGraphics();
  }

  public synchronized void addConsumer(ImageConsumer ic) {
    startProduction(ic);
  }

  public boolean isConsumer(ImageConsumer ic) {
    return (this.ic == ic);
  }

  public void removeConsumer(ImageConsumer ic) {
    if (this.ic == ic)
      this.ic = null;
  }

  public void requestTopDownLeftRightResend(ImageConsumer ic) {
  }

  public void startProduction(ImageConsumer ic) {
    if (this.ic != ic) {
      this.ic = ic;
      ic.setDimensions(windowWidth, windowHeight);
      ic.setHints(ImageConsumer.TOPDOWNLEFTRIGHT |
                  ImageConsumer.COMPLETESCANLINES |
                  ImageConsumer.SINGLEPASS);
    }
    ic.setPixels(0, 0, windowWidth, windowHeight, colorModelRGB,
                 pBuffer, 0, windowWidth);
    ic.imageComplete(ImageConsumer.SINGLEFRAMEDONE);
  }
}
