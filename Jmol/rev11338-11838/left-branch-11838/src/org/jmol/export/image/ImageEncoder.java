



























package org.jmol.export.image;

import java.io.*;
import java.util.Hashtable;
import java.awt.Image;
import java.awt.image.*;



















public abstract class ImageEncoder implements ImageConsumer {

  protected OutputStream out;

  private ImageProducer producer;
  private int width = -1;
  private int height = -1;
  private int hintflags = 0;
  private boolean started = false;
  private boolean encoding;
  private IOException iox;
  private static final ColorModel rgbModel = ColorModel.getRGBdefault();

  
  
  
  
  public ImageEncoder(Image img, OutputStream out) {
    this(img.getSource(), out);
  }

  
  
  
  public ImageEncoder(ImageProducer producer, OutputStream out) {
    this.producer = producer;
    this.out = out;
  }

  

  
  abstract void encodeStart(int w, int h) throws IOException;

  
  
  
  
  abstract void encodePixels(int x, int y, int w, int h, int[] rgbPixels,
                             int off, int scansize) throws IOException;

  
  abstract void encodeDone() throws IOException;

  

  
  public synchronized void encode() throws IOException {
    encoding = true;
    iox = null;
    producer.startProduction(this);
    while (encoding)
      try {
        wait();
      } catch (InterruptedException e) {
      }
    if (iox != null)
      throw iox;
  }

  private boolean accumulate = false;
  private int[] accumulator;

  private void encodePixelsWrapper(int x, int y, int w, int h, int[] rgbPixels,
                                   int off, int scansize) throws IOException {
    if (!started) {
      started = true;
      encodeStart(width, height);
      if ((hintflags & TOPDOWNLEFTRIGHT) == 0) {
        accumulate = true;
        accumulator = new int[width * height];
      }
    }
    if (accumulate)
      for (int row = 0; row < h; ++row)
        System.arraycopy(rgbPixels, row * scansize + off, accumulator,
            (y + row) * width + x, w);
    else
      encodePixels(x, y, w, h, rgbPixels, off, scansize);
  }

  private void encodeFinish() throws IOException {
    if (accumulate) {
      encodePixels(0, 0, width, height, accumulator, 0, width);
      accumulator = null;
      accumulate = false;
    }
  }

  private synchronized void stop() {
    encoding = false;
    notifyAll();
  }

  

  public void setDimensions(int width, int height) {
    this.width = width;
    this.height = height;
  }

  public void setProperties(Hashtable props) {
    
  }

  public void setColorModel(ColorModel model) {
    
  }

  public void setHints(int hintflags) {
    this.hintflags = hintflags;
  }

  public void setPixels(int x, int y, int w, int h, ColorModel model,
                        byte[] pixels, int off, int scansize) {
    int[] rgbPixels = new int[w];
    for (int row = 0; row < h; ++row) {
      int rowOff = off + row * scansize;
      for (int col = 0; col < w; ++col)
        rgbPixels[col] = model.getRGB(pixels[rowOff + col] & 0xff);
      try {
        encodePixelsWrapper(x, y + row, w, 1, rgbPixels, 0, w);
      } catch (IOException e) {
        iox = e;
        stop();
        return;
      }
    }
  }

  public void setPixels(int x, int y, int w, int h, ColorModel model,
                        int[] pixels, int off, int scansize) {
    if (model == rgbModel) {
      try {
        encodePixelsWrapper(x, y, w, h, pixels, off, scansize);
      } catch (IOException e) {
        iox = e;
        stop();
        return;
      }
    } else {
      int[] rgbPixels = new int[w];
      for (int row = 0; row < h; ++row) {
        int rowOff = off + row * scansize;
        for (int col = 0; col < w; ++col)
          rgbPixels[col] = model.getRGB(pixels[rowOff + col]);
        try {
          encodePixelsWrapper(x, y + row, w, 1, rgbPixels, 0, w);
        } catch (IOException e) {
          iox = e;
          stop();
          return;
        }
      }
    }
  }

  public void imageComplete(int status) {
    producer.removeConsumer(this);
    if (status == ImageConsumer.IMAGEABORTED)
      iox = new IOException("image aborted");
    else {
      try {
        encodeFinish();
        encodeDone();
      } catch (IOException e) {
        iox = e;
      }
    }
    stop();
  }

}
