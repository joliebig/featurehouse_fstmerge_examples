

































package org.jmol.export.image;

import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.awt.Image;
import java.awt.image.*;

import org.jmol.util.Logger;








public class GifEncoder extends ImageEncoder {

  private boolean interlace = false;

  public static void write(Image image, OutputStream os) throws IOException {
    (new GifEncoder(image, os)).encode();
  }

  public static byte[] getBytes(Image image) {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    try {
      write(image, os);
      os.flush();
      os.close();
    } catch (IOException e) {
      
    }
    return os.toByteArray();
  }

  
  
  
  public GifEncoder(Image img, OutputStream out) {
    super(img, out);
  }

  
  
  
  
  public GifEncoder(Image img, OutputStream out, boolean interlace) {
    super(img, out);
    this.interlace = interlace;
  }

  
  
  
  public GifEncoder(ImageProducer prod, OutputStream out) {
    super(prod, out);
  }

  
  
  
  public GifEncoder(ImageProducer prod, OutputStream out, boolean interlace) {
    super(prod, out);
    this.interlace = interlace;
  }

  
  static class ColorItem {

    AdaptiveColorCollection acc;
    int rgb;
    int rgb2;
    int count;
    int index;
    boolean isTransparent;

    ColorItem(int rgb, int count, int index, boolean isTransparent) {
      this.rgb = this.rgb2 = rgb;
      this.count = count;
      this.index = index;
      this.isTransparent = isTransparent;
    }
  }

  static class ColorVector extends Vector {
    void sort() {
      CountComparator comparator = new CountComparator();
      Arrays.sort(elementData, comparator);
    }
    
    static class CountComparator implements Comparator {
      public int compare(Object arg0, Object arg1) {
        ColorItem a = (ColorItem)arg0;
        ColorItem b = (ColorItem)arg1;
        return (a == null ? 1 : b == null ? -1 : a.count < b.count ? -1 : a.count > b.count ? 1 : 0);
      }    
    }
  }
  
  static class AdaptiveColorCollection {
    int rgb;
    int index;
    long red;
    long green;
    long blue;
    int count;
    AdaptiveColorCollection(int rgb) {
      this.rgb = rgb;
    }
    
    void addRgb(int rgb, int count) {
      this.count += count;
      blue += (rgb & 0xFF) * count;
      green += ((rgb >> 8) & 0xFF) * count;
      red += ((rgb >> 16) & 0xFF) * count;
    }  
    void setRgb(byte[] reds, byte[] grns, byte[] blus) {
      
      reds[index] = (byte) ((red / count) & 0xff);
      grns[index] = (byte) ((green / count) & 0xff);
      blus[index] = (byte) ((blue / count) & 0xff);
    }
  }

  int width, height;
  int[][] rgbPixels;

  void encodeStart(int width, int height) throws IOException {
    this.width = width;
    this.height = height;
    rgbPixels = new int[height][width];
  }

  void encodePixels(int x, int y, int w, int h, int[] rgbPixels, int off,
                    int scansize) throws IOException {
    
    for (int row = 0; row < h; ++row)
      System.arraycopy(rgbPixels, row * scansize + off,
          this.rgbPixels[y + row], x, w);
  }

  Hashtable colorHash;
  ColorVector colorVector;

  void encodeDone() throws IOException {
    int transparentIndex = -1;
    int transparentRgb = -1;
    
    colorHash = new Hashtable();
    colorVector = new ColorVector();
    int index = 0;
    String srgb;
    for (int row = 0; row < height; ++row) {
      
      for (int col = 0; col < width; ++col) {
        int rgb = rgbPixels[row][col];
        boolean isTransparent = ((rgb >>> 24) < 0x80);
        if (isTransparent) {
          if (transparentIndex < 0) {
            
            transparentIndex = index;
            transparentRgb = rgb;
          } else if (rgb != transparentRgb) {
            
            
            rgbPixels[row][col] = rgb = transparentRgb;
          }
        }
        ColorItem item = (ColorItem) colorHash
            .get(srgb = getKey(rgb));
        if (item == null) {
          if (index < 0)
            throw new IOException("too many colors for a GIF");
          
          
          item = new ColorItem(rgb, 1, index, isTransparent);
          ++index;
          colorHash.put(srgb, item);
          colorVector.add(item);
        } else
          ++item.count;
      }
    }
    
    colorVector.sort();
    
    int mask = 0x010101;
    colorHash = null;
    int nTotal = index;
  
    int nMax = Math.max(index - 1, 0); 





    
    Logger.debug("# colors = " + nTotal);
    while (true) {
      nTotal = index;
      colorHash = new Hashtable();
      AdaptiveColorCollection acc;
      for (int i = 0; i < nMax; i++) {
        ColorItem item = (ColorItem) colorVector.get(i);
        int rgb = (nTotal <= 256 ? item.rgb : item.rgb & ~mask);
        item.rgb2 = rgb;
        srgb = getKey(rgb);
        if ((acc = (AdaptiveColorCollection) colorHash.get(srgb)) == null) {
          colorHash.put(srgb, acc = new AdaptiveColorCollection(rgb));
        } else {
          nTotal--;
        }
        item.acc = acc;
      }
      mask |= (mask <<= 1);
      if (nTotal <= 256)
        break;
    }
    
    int logColors;
    if (nTotal <= 2)
      logColors = 1;
    else if (nTotal <= 4)
      logColors = 2;
    else if (nTotal <= 16)
      logColors = 4;
    else
      logColors = 8;

    
    int mapSize = 1 << logColors;
    byte[] reds = new byte[mapSize];
    byte[] grns = new byte[mapSize];
    byte[] blus = new byte[mapSize];
    Hashtable ht = new Hashtable();
    for (int i = 0; i < index; i++) {
      ColorItem item = (ColorItem) colorVector.get(i);
      int rgb = item.rgb;
      int count = item.count;
      srgb = getKey(rgb);
      if (item.acc == null)
        colorHash.put(srgb, item.acc = new AdaptiveColorCollection(rgb));
      item.acc.addRgb(rgb, count);
      ht.put(srgb, item.acc);
    }
    int iindex = 0;
    for (Enumeration e = colorHash.elements(); e.hasMoreElements();) {
      AdaptiveColorCollection acc = (AdaptiveColorCollection) e.nextElement();
      acc.index = iindex++;
      acc.setRgb(reds, grns, blus);
    }
    Logger.debug("# colors = " + iindex);
    colorHash = ht;
    GIFEncode(out, width, height, interlace, (byte) 0, transparentIndex,
        logColors, reds, grns, blus);
  }

  private static String getKey(int rgb) {
    return Integer.toHexString(rgb).substring(2);
  }

  byte GetPixel(int x, int y) {
    
    int rgb = rgbPixels[y][x];
    int iindex;
    try{
      iindex = ((AdaptiveColorCollection) colorHash.get(getKey(rgb))).index;
    } catch (Exception e) {
      iindex = 0;
    }
    return (byte) iindex;
  }

  static void writeString(OutputStream out, String str) throws IOException {
    byte[] buf = str.getBytes();
    out.write(buf);
  }

  
  
  

  int Width, Height;
  boolean Interlace;
  int curx, cury;
  int CountDown;
  int Pass = 0;

  void GIFEncode(OutputStream outs, int Width, int Height, boolean Interlace,
                 byte Background, int Transparent, int BitsPerPixel,
                 byte[] Red, byte[] Green, byte[] Blue) throws IOException {
    byte B;
    int LeftOfs, TopOfs;
    int ColorMapSize;
    int InitCodeSize;
    int i;

    this.Width = Width;
    this.Height = Height;
    this.Interlace = Interlace;
    ColorMapSize = 1 << BitsPerPixel;
    LeftOfs = TopOfs = 0;

    
    CountDown = Width * Height;

    
    Pass = 0;

    
    if (BitsPerPixel <= 1)
      InitCodeSize = 2;
    else
      InitCodeSize = BitsPerPixel;

    
    curx = 0;
    cury = 0;

    
    writeString(outs, "GIF89a");

    
    Putword(Width, outs);
    Putword(Height, outs);

    
    B = (byte) 0x80; 
    
    B |= (byte) ((8 - 1) << 4);
    
    
    B |= (byte) ((BitsPerPixel - 1));

    
    Putbyte(B, outs);

    
    Putbyte(Background, outs);

    
    
    
    
    
    
    Putbyte((byte) 0, outs);

    
    for (i = 0; i < ColorMapSize; ++i) {
      Putbyte(Red[i], outs);
      Putbyte(Green[i], outs);
      Putbyte(Blue[i], outs);
    }

    
    if (Transparent != -1) {
      Putbyte((byte) '!', outs);
      Putbyte((byte) 0xf9, outs);
      Putbyte((byte) 4, outs);
      Putbyte((byte) 1, outs);
      Putbyte((byte) 0, outs);
      Putbyte((byte) 0, outs);
      Putbyte((byte) Transparent, outs);
      Putbyte((byte) 0, outs);
    }

    
    Putbyte((byte) ',', outs);

    
    Putword(LeftOfs, outs);
    Putword(TopOfs, outs);
    Putword(Width, outs);
    Putword(Height, outs);

    
    if (Interlace)
      Putbyte((byte) 0x40, outs);
    else
      Putbyte((byte) 0x00, outs);

    
    Putbyte((byte) InitCodeSize, outs);

    
    compress(InitCodeSize + 1, outs);

    
    Putbyte((byte) 0, outs);

    
    Putbyte((byte) ';', outs);
  }

  
  void BumpPixel() {
    
    ++curx;

    
    
    
    if (curx == Width) {
      curx = 0;

      if (!Interlace)
        ++cury;
      else {
        switch (Pass) {
        case 0:
          cury += 8;
          if (cury >= Height) {
            ++Pass;
            cury = 4;
          }
          break;

        case 1:
          cury += 8;
          if (cury >= Height) {
            ++Pass;
            cury = 2;
          }
          break;

        case 2:
          cury += 4;
          if (cury >= Height) {
            ++Pass;
            cury = 1;
          }
          break;

        case 3:
          cury += 2;
          break;
        }
      }
    }
  }

  static final int EOF = -1;

  
  int GIFNextPixel() {
    byte r;

    if (CountDown == 0)
      return EOF;

    --CountDown;

    r = GetPixel(curx, cury);

    BumpPixel();

    return r & 0xff;
  }

  
  void Putword(int w, OutputStream outs) throws IOException {
    Putbyte((byte) (w & 0xff), outs);
    Putbyte((byte) ((w >> 8) & 0xff), outs);
  }

  
  void Putbyte(byte b, OutputStream outs) throws IOException {
    outs.write(b);
  }

  
  
  
  

  

  static final int BITS = 12;

  static final int HSIZE = 5003; 

  
  
  
  
  
  
  
  
  
  

  int n_bits; 
  int maxbits = BITS; 
  int maxcode; 
  int maxmaxcode = 1 << BITS; 

  final int MAXCODE(int n_bits) {
    return (1 << n_bits) - 1;
  }

  int[] htab = new int[HSIZE];
  int[] codetab = new int[HSIZE];

  int hsize = HSIZE; 

  int free_ent = 0; 

  
  
  boolean clear_flg = false;

  
  
  
  
  
  
  
  
  
  
  

  int g_init_bits;

  int ClearCode;
  int EOFCode;

  void compress(int init_bits, OutputStream outs) throws IOException {
    int fcode;
    int i ;
    int c;
    int ent;
    int disp;
    int hsize_reg;
    int hshift;

    
    g_init_bits = init_bits;

    
    clear_flg = false;
    n_bits = g_init_bits;
    maxcode = MAXCODE(n_bits);

    ClearCode = 1 << (init_bits - 1);
    EOFCode = ClearCode + 1;
    free_ent = ClearCode + 2;

    char_init();

    ent = GIFNextPixel();

    hshift = 0;
    for (fcode = hsize; fcode < 65536; fcode *= 2)
      ++hshift;
    hshift = 8 - hshift; 

    hsize_reg = hsize;
    cl_hash(hsize_reg); 

    output(ClearCode, outs);

    outer_loop: while ((c = GIFNextPixel()) != EOF) {
      fcode = (c << maxbits) + ent;
      i = (c << hshift) ^ ent; 

      if (htab[i] == fcode) {
        ent = codetab[i];
        continue;
      } else if (htab[i] >= 0) 
      {
        disp = hsize_reg - i; 
        if (i == 0)
          disp = 1;
        do {
          if ((i -= disp) < 0)
            i += hsize_reg;

          if (htab[i] == fcode) {
            ent = codetab[i];
            continue outer_loop;
          }
        } while (htab[i] >= 0);
      }
      output(ent, outs);
      ent = c;
      if (free_ent < maxmaxcode) {
        codetab[i] = free_ent++; 
        htab[i] = fcode;
      } else
        cl_block(outs);
    }
    
    output(ent, outs);
    output(EOFCode, outs);
  }

  
  
  
  
  
  
  
  
  
  
  
  
  
  

  int cur_accum = 0;
  int cur_bits = 0;

  int masks[] = { 0x0000, 0x0001, 0x0003, 0x0007, 0x000F, 0x001F, 0x003F,
      0x007F, 0x00FF, 0x01FF, 0x03FF, 0x07FF, 0x0FFF, 0x1FFF, 0x3FFF, 0x7FFF,
      0xFFFF };

  void output(int code, OutputStream outs) throws IOException {
    cur_accum &= masks[cur_bits];

    if (cur_bits > 0)
      cur_accum |= (code << cur_bits);
    else
      cur_accum = code;

    cur_bits += n_bits;

    while (cur_bits >= 8) {
      char_out((byte) (cur_accum & 0xff), outs);
      cur_accum >>= 8;
      cur_bits -= 8;
    }

    
    
    if (free_ent > maxcode || clear_flg) {
      if (clear_flg) {
        maxcode = MAXCODE(n_bits = g_init_bits);
        clear_flg = false;
      } else {
        ++n_bits;
        if (n_bits == maxbits)
          maxcode = maxmaxcode;
        else
          maxcode = MAXCODE(n_bits);
      }
    }

    if (code == EOFCode) {
      
      while (cur_bits > 0) {
        char_out((byte) (cur_accum & 0xff), outs);
        cur_accum >>= 8;
        cur_bits -= 8;
      }

      flush_char(outs);
    }
  }

  

  
  void cl_block(OutputStream outs) throws IOException {
    cl_hash(hsize);
    free_ent = ClearCode + 2;
    clear_flg = true;

    output(ClearCode, outs);
  }

  
  void cl_hash(int hsize) {
    for (int i = 0; i < hsize; ++i)
      htab[i] = -1;
  }

  

  
  int a_count;

  
  void char_init() {
    a_count = 0;
  }

  
  byte[] accum = new byte[256];

  
  
  void char_out(byte c, OutputStream outs) throws IOException {
    accum[a_count++] = c;
    if (a_count >= 254)
      flush_char(outs);
  }

  
  void flush_char(OutputStream outs) throws IOException {
    if (a_count > 0) {
      outs.write(a_count);
      outs.write(accum, 0, a_count);
      a_count = 0;
    }
  }

}

