

package org.jmol.g3d;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.PixelGrabber;
import java.util.Hashtable;

import org.jmol.api.JmolRendererInterface;


 
public class Text3D {
  
  private int height; 
  private int ascent;
  private int width;
  private int mapWidth;
  private int size;
  private int[] bitmap;
  private boolean isInvalid;
  
  public int getWidth() {
    return width;
  }
  
  public static int plot(int x, int y, int z, int argb, String text,
                         Font3D font3d, Graphics3D g3d,
                         JmolRendererInterface jmolRenderer, boolean antialias) {
    if (text.length() == 0)
      return 0;
    
    if (text.indexOf("<su") >= 0)
      return plotByCharacter(x, y, z, argb, text, font3d, g3d, jmolRenderer,
          antialias);
    int offset = font3d.fontMetrics.getAscent();
    
      
    y -= offset;

    
    
    Text3D text3d = getText3D(x, y, g3d, text, font3d, antialias);
    if (text3d.isInvalid)
      return text3d.width;
    
    
    if (antialias && (argb & 0xC0C0C0) == 0) {
      
      
      argb = argb | 0x040404;
    }
    if (jmolRenderer != null
        || (x < 0 || x + text3d.width > g3d.width || y < 0 || y + text3d.height > g3d.height))
      plotClipped(x, y, z, argb, g3d, jmolRenderer, text3d.mapWidth,
          text3d.height, text3d.bitmap);
    else
      plotUnclipped(x, y, z, argb, g3d, text3d.mapWidth, text3d.height,
          text3d.bitmap);
    return text3d.width;
  }

  public static void plotImage(int x, int y, int z, Image image,
                               Graphics3D g3d,
                               JmolRendererInterface jmolRenderer,
                               boolean antialias, int argbBackground,
                               int width, int height) {
    boolean isBackground = (x == Integer.MIN_VALUE);
    int width0 = image.getWidth(null);
    int height0 = image.getHeight(null);
    int bgcolor = (isBackground ? g3d.bgcolor : argbBackground);
    
    if (isBackground) {
      x = 0;
      z = Integer.MAX_VALUE - 1;
      width = g3d.width;
      height = g3d.height;
    }
    if (x + width <= 0 || x >= g3d.width || y + height <= 0 || y >= g3d.height)
      return;
    g3d.platform.checkOffscreenSize(width, height);
    Graphics g = g3d.platform.gOffscreen;
    if (g instanceof Graphics2D) {
      ((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_IN, 1.0f));
      g.setColor(isBackground ? new Color(bgcolor) : new Color(0, 0, 0, 0));
      g.fillRect(0, 0, width, height);
      ((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
      g.drawImage(image, 0, 0, width, height, 0, 0, width0, height0, null);
    } else {
      g.clearRect(0, 0, width, height);
      g.drawImage(image, 0, 0, width, height, 0, 0, width0, height0, null);
    }
    PixelGrabber pixelGrabber = new PixelGrabber(g3d.platform.imageOffscreen,
        0, 0, width, height, true);
    try {
      pixelGrabber.grabPixels();
    } catch (InterruptedException e) {
      
      return;
    }
    int[] buffer = (int[]) pixelGrabber.getPixels();

    if (jmolRenderer != null
        || (x < 0 || x + width > g3d.width || y < 0 || y + height > g3d.height))
      plotImageClipped(x, y, z, g3d, jmolRenderer, width, height, buffer,
          bgcolor);
    else
      plotImageUnClipped(x, y, z, g3d, width, height, buffer, bgcolor);
    return;
  }

  private static void plotImageClipped(int x, int y, int z, Graphics3D g3d,
                                       JmolRendererInterface jmolRenderer,
                                       int width, int height,
                                       int[] buffer, int bgcolor) {
    if (jmolRenderer == null)
      jmolRenderer = g3d;
    for (int i = 0, offset = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        int argb = buffer[offset++];
        if (argb != bgcolor && (argb & 0xFF000000) == 0xFF000000)
          jmolRenderer.plotPixelClippedNoSlab(argb, x + j, y + i, z);
        else if (argb == 0 && bgcolor != 0)
          jmolRenderer.plotPixelClippedNoSlab(bgcolor, x + j, y + i, z);
      }
    }
  }

  private static void plotImageUnClipped(int x, int y, int z, Graphics3D g3d,
                                         int textWidth, int textHeight,
                                         int[] buffer, int bgcolor) {
    int[] zbuf = g3d.zbuf;
    int renderWidth = g3d.width;
    int pbufOffset = y * renderWidth + x;
    int i = 0;
    int j = 0;
    int offset = 0;
    while (i < textHeight) {
      while (j < textWidth) {
        if (z < zbuf[pbufOffset]) {
          int argb = buffer[offset];
          if (argb != bgcolor && (argb & 0xFF000000) == 0xFF000000)
            g3d.addPixel(pbufOffset, z, argb);
          else if (argb == 0 && bgcolor != 0)
            g3d.addPixel(pbufOffset, z, bgcolor);
        }
        ++offset;
        ++j;
        ++pbufOffset;
      }
      ++i;
      j -= textWidth;
      pbufOffset += (renderWidth - textWidth);
    }
  }

  private static int plotByCharacter(int x, int y, int z, int argb, 
                                      String text, Font3D font3d, 
                                      Graphics3D g3d, JmolRendererInterface jmolRenderer,
                                      boolean antialias) {
    
    int w = 0;
    int len = text.length();
    int suboffset = (int)(font3d.fontMetrics.getHeight() * 0.25);
    int supoffset = -(int)(font3d.fontMetrics.getHeight() * 0.3);
    for (int i = 0; i < len; i++) {
      if (text.charAt(i) == '<') {
        if (i + 4 < len && text.substring(i, i + 5).equals("<sub>")) {
          i += 4;
          y += suboffset;
          continue;
        }
        if (i + 4 < len && text.substring(i, i + 5).equals("<sup>")) {
          i += 4;
          y += supoffset;
          continue;
        }
        if (i + 5 < len  && text.substring(i, i + 6).equals("</sub>")) {
          i += 5;
          y -= suboffset;
          continue;
        }
        if (i + 5 < len  && text.substring(i, i + 6).equals("</sup>")) {
          i += 5;
          y -= supoffset;
          continue;
        }
      }
      int width = plot(x + w, y, z, argb, text.substring(i, i + 1), font3d, 
          g3d, jmolRenderer, antialias);
      w += width;
    }
    
    return w;
  }
  
  private static void plotUnclipped(int x, int y, int z, int argb,
                                    Graphics3D g3d, int textWidth,
                                    int textHeight, int[] bitmap) {
    int offset = 0;
    int shiftregister = 0;
    int i = 0, j = 0;
    int[] zbuf = g3d.zbuf;
    int renderWidth = g3d.width;
    int pbufOffset = y * renderWidth + x;
    while (i < textHeight) {
      while (j < textWidth) {
        if ((offset & 31) == 0)
          shiftregister = bitmap[offset >> 5];
        if (shiftregister == 0) {
          int skip = 32 - (offset & 31);
          j += skip;
          offset += skip;
          pbufOffset += skip;
          continue;
        }
        if (shiftregister < 0 && z < zbuf[pbufOffset])
          g3d.addPixel(pbufOffset, z, argb);
        shiftregister <<= 1;
        ++offset;
        ++j;
        ++pbufOffset;
      }
      while (j >= textWidth) {
        ++i;
        j -= textWidth;
        pbufOffset += (renderWidth - textWidth);
      }
    }
  }
  
  private static void plotClipped(int x, int y, int z, int argb,
                                  Graphics3D g3d,
                                  JmolRendererInterface jmolRenderer,
                                  int textWidth, int textHeight, int[] bitmap) {
    if (jmolRenderer == null)
      jmolRenderer = g3d;
    int offset = 0;
    int shiftregister = 0;
    int i = 0, j = 0;
    while (i < textHeight) {
      while (j < textWidth) {
        if ((offset & 31) == 0)
          shiftregister = bitmap[offset >> 5];
        if (shiftregister == 0) {
          int skip = 32 - (offset & 31);
          j += skip;
          offset += skip;
          continue;
        }
        if (shiftregister < 0)
          jmolRenderer.plotPixelClippedNoSlab(argb, x + j, y + i, z);
        shiftregister <<= 1;
        ++offset;
        ++j;
      }
      while (j >= textWidth) {
        ++i;
        j -= textWidth;
      }
    }
  }

  private Text3D(String text, Font3D font3d,
                 boolean antialias) {
    FontMetrics fontMetrics = font3d.fontMetrics;
    ascent = fontMetrics.getAscent();
    height = ascent + fontMetrics.getDescent();
    width = fontMetrics.stringWidth(text);
    if (width == 0)
      return;
    
    mapWidth = width;
    size = mapWidth * height;
  }

  private void renderOffscreen(String text, Font3D font3d, Platform3D platform,
                               boolean antialias) {
    Graphics g = platform.gOffscreen;
    g.setColor(Color.black);
    g.fillRect(0, 0, mapWidth, height);
    g.setColor(Color.white);
    g.setFont(font3d.font);
    g.drawString(text, 0, ascent);
  }

  private void rasterize(Platform3D platform, boolean antialias) {
    
    PixelGrabber pixelGrabber = new PixelGrabber(platform.imageOffscreen, 0, 0, 
                                                 mapWidth, height, true);
    try {
      pixelGrabber.grabPixels();
    } catch (InterruptedException e) {
      
      return;
    }
    int pixels[] = (int[])pixelGrabber.getPixels();

    int bitmapSize = (size + 31) >> 5;
    bitmap = new int[bitmapSize];

    int offset, shifter;
    for (offset = shifter = 0; offset < size; ++offset, shifter <<= 1) {
      if ((pixels[offset] & 0x00FFFFFF) != 0)
        shifter |= 1;
      if ((offset & 31) == 31)
        bitmap[offset >> 5] = shifter;
    }
    if ((offset & 31) != 0) {
      shifter <<= 31 - (offset & 31);
      bitmap[offset >> 5] = shifter;
    }
    
  }

  private final static Hashtable htFont3d = new Hashtable();
  private final static Hashtable htFont3dAntialias = new Hashtable();
  private static boolean working;
  
  public synchronized static void clearFontCache() {
    if (working)
      return;
    htFont3d.clear();
    htFont3dAntialias.clear();
  }
  
  
  
  

  
  
  
  private synchronized static Text3D getText3D(int x, int y, Graphics3D g3d,
                                               String text, Font3D font3d,
                                               boolean antialias) {
    working = true;
    Hashtable ht = (antialias ? htFont3dAntialias : htFont3d);
    Hashtable htForThisFont = (Hashtable) ht.get(font3d);
    Text3D text3d = null;
    boolean newFont = false;
    boolean newText = false;
    if (htForThisFont != null) {
      text3d = (Text3D) htForThisFont.get(text);
    } else {
      htForThisFont = new Hashtable();
      newFont = true;
    }
    if (text3d == null) {
      text3d = new Text3D(text, font3d, antialias);
      newText = true;
    }
    text3d.isInvalid = (text3d.width == 0 || x + text3d.width <= 0
        || x >= g3d.width || y + text3d.height <= 0 || y >= g3d.height);
    if (text3d.isInvalid)
      return text3d;
    if (newFont)
      ht.put(font3d, htForThisFont);
    if (newText) {
      
      text3d.setBitmap(text, font3d, g3d.platform, antialias);
      htForThisFont.put(text, text3d);
    }
    working = false;
    return text3d;
  }

  private void setBitmap(String text, Font3D font3d, Platform3D platform, boolean antialias) {
    
    platform.checkOffscreenSize(mapWidth, height);
    renderOffscreen(text, font3d, platform, antialias);
    rasterize(platform, antialias);
  }

}
