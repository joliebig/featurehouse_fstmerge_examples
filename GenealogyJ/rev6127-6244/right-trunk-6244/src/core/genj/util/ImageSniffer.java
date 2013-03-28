
package genj.util;

import genj.renderer.DPI;

import java.awt.Dimension;
import java.awt.geom.Dimension2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


public class ImageSniffer {

  
  private String suffix = null;

  
  private int read = 0;

    
  protected Dimension dimension;
  protected DPI dpi = new DPI(72,72);
    
  
  public ImageSniffer(File file) {
    try {
      FileInputStream in = new FileInputStream(file);
      init(in);
      in.close();
    } catch (IOException e) {
    }
  }
  
  
  public ImageSniffer(InputStream in) {
    init(in);
  }

  private void init(InputStream in) {
    
    try {
  
      
      int tag = (read(in)&0xff) << 8 | (read(in)&0xff);
  
      switch (tag) {
        case 0x4749: sniffGif(in);break;
        case 0x8950: sniffPng(in);break;
        case 0xffd8: sniffJpg(in);break;
        case 0x424d: sniffBmp(in);break;
        default:
      }    
      
    } catch (IOException e) {
    }

    
    if (dpi!=null&&(dpi.horizontal()<=0||dpi.vertical()<=0))
      dpi = null;
    if (dimension!=null&&(dimension.width<1||dimension.height<1))
      dimension = null;
    
    
  }
  
  
  public String getSuffix() {
    return suffix;
  }
  
  
  public DPI getDPI() {
    return dpi;
  }

  
  public Dimension getDimension() {
    return dimension;
  }

  
  public Dimension2D getDimensionInInches() {
    
    if (dpi==null||dimension==null) 
      return null;
    return new Dimension2d(
      (double)dimension.width/dpi.horizontal(), 
      (double)dimension.height/dpi.vertical()
    );
  }
  
  
  private void sniffGif(InputStream in) throws IOException {
    
    
    final int
      F89A = string2int("F89a"),
      F87A = string2int("F87a");
      
    
    int magic = sniffIntBigEndian(in);
    if (magic!=F89A&&magic!=F87A)
      return;

    
    dimension = new Dimension(
    	sniffShortLittleEndian(in),
    	sniffShortLittleEndian(in)
    );

    
          
    
    

    
    suffix = "gif";
  }

  
  private void sniffPng(InputStream in) throws IOException {

    
    final int
      IHDR = string2int("IHDR"),
      IDAT = string2int("IDAT"),
      IEND = string2int("IEND"),
      PHYS = string2int("pHYs");
            
    
    if (!sniff(in, new byte[]{0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a}))
      return;

    
    int len = sniffIntBigEndian(in);
    int type = sniffIntBigEndian(in);
    if (IHDR!=type)
      return;

    
    dimension = new Dimension(
      sniffIntBigEndian(in),
      sniffIntBigEndian(in)
    );
    
    
    skip(len-8+4, in);

    
    while (true) {
      
      
      len = sniffIntBigEndian(in);
      type = sniffIntBigEndian(in);

      
      if (IDAT==type) break;

      
      if (IEND==type) break;

      
      if (PHYS==type) {
        int
          x = sniffIntBigEndian(in),
          y = sniffIntBigEndian(in);
        if (read(in)==1) { 
          dpi = new DPI(
          	(int)Math.round(2.54D*x/100),
          	(int)Math.round(2.54D*y/100)
          );
        }
        break;
      }
      
      
      skip(len+4, in);
      
    }
    
    
    suffix = "png";
  }

  
  private boolean sniffTiff(InputStream in) throws IOException {
    
    int start = read;

    
    boolean intel;
    switch (sniffShortLittleEndian(in)) {
      case 0x4949: 
        intel = true;
        break;
      case 0x4d4d: 
        intel = false; 
        break;
      default:
        return false;
    }
    
    
    skip(2, in);
    
    
    skip(sniffInt(in, intel)-(read-start), in);
    
    
    int xres = 0, yres = 0;
    for (int i=0,j=sniffShort(in,intel);i<j;i++) {
      
      int tag = sniffShort(in,intel),
          format = sniffShort(in,intel),
          components = sniffInt(in, intel),
          value = sniffInt(in, intel);
      switch (tag) {
      	case 0x011a: 
      	  xres = value;
      		break;
      	case 0x011b: 
      	  yres = value;
      		break;
      }
      
    }
    
    
    if (xres<(read-start)||yres<(read-start)) 
      return false;
    
    
    if (xres<yres) {
      skip(xres-(read-start), in);
      xres = sniffInt(in, intel) / sniffInt(in, intel);
      skip(yres-(read-start), in);
      yres = sniffInt(in, intel) / sniffInt(in, intel);
    } else {
      skip(yres-(read-start), in);
      yres = sniffInt(in, intel) / sniffInt(in, intel);
      skip(xres-(read-start), in);
      xres = sniffInt(in, intel) / sniffInt(in, intel);
    }
    dpi = new DPI(xres, yres);
    
    
    return true;
    
  }
  
  
  private void sniffJpg(InputStream in) throws IOException {

    final byte[] 
      JFIF = "JFIF".getBytes(),
      EXIF = "Exif".getBytes();

    
    chunks: while (true) {
    
      
      int 
        marker = sniffShortBigEndian(in),
        size = sniffShortBigEndian(in) - 2, 
        start = read; 
        
      
      switch (marker) {
        case 0xffe1: 
          
          if (!sniff(in, EXIF))
            break;
          skip(2, in);
          
          sniffTiff(in);
          break;
        case 0xffe0: 
          
          if (sniff(in, JFIF)) {
            
            skip(3, in);
            
            switch (read(in)) {
              case 1: 
                dpi = new DPI(sniffShortBigEndian(in), sniffShortBigEndian(in));
                break;
              case 2: 
                dpi = new DPI(
	                (int)(sniffShortBigEndian(in) * 2.54f),
	                (int)(sniffShortBigEndian(in) * 2.54f)
                );
                break;
              }
          }
          break;
        case 0xffc0: 
        case 0xffc1:
        case 0xffc2:
        case 0xffc3:
        
        case 0xffc5:
        case 0xffc6:
        case 0xffc7:
        
        case 0xffc9:
        case 0xffca:
        case 0xffcb:
        case 0xffcc:
        case 0xffcd:
        case 0xffce:
        case 0xffcf:
          
          read(in); 
          dimension = new Dimension(
	          sniffShortBigEndian(in),
	          sniffShortBigEndian(in)
          );
          read(in); 
          break;
        case 0xffd9:
          
          break chunks;
        default:
          if ((marker & 0xff00) != 0xff00)
            return; 
      }
      
      
      skip(size-(read-start), in);
    }    
    
    
    suffix = "jpg";
  }

  
  private void sniffBmp(InputStream in) throws IOException {

    
    skip(16, in);
    
    
    dimension = new Dimension(
	    sniffIntLittleEndian(in),
	    sniffIntLittleEndian(in)
    );
    
    
    skip(2, in);

    
    int bitsPerPixel = sniffShortLittleEndian(in);
    if (bitsPerPixel != 1 && bitsPerPixel != 4 &&
        bitsPerPixel != 8 && bitsPerPixel != 16 &&
        bitsPerPixel != 24 && bitsPerPixel != 32) {
        return;
    }
    
    
    skip(8, in);
    
    
    dpi = new DPI(
      (int)Math.round(2.54D*sniffIntLittleEndian(in)/100), 
      (int)Math.round(2.54D*sniffIntLittleEndian(in)/100)  
    );

    
    suffix = "bmp";
  }

  
  private int read(InputStream in) throws IOException {
    read++;
    return in.read();
  }
  
  
  private void skip(int num, InputStream in) throws IOException {
    read += num;
    if (num!=in.skip(num))
      throw new IOException("cannot skip");
  }

  
  private boolean sniff(InputStream in, byte[] magic) throws IOException {
    for (int m=0;m<magic.length;m++) {
      int i = read(in);
      if (i==-1||i!=magic[m]) return false;
    }
    return true;
  }

  
  private boolean sniff(InputStream in, String magic) throws IOException {
    return sniff(in, magic.getBytes());
  }
  
  
  private int sniffInt(InputStream in, boolean intel) throws IOException {
    return intel ? sniffIntLittleEndian(in) : sniffIntBigEndian(in);  
  }
  
  
  private int sniffIntBigEndian(InputStream in) throws IOException {
    return
      (read(in) & 0xff) << 24 | 
      (read(in) & 0xff) << 16 | 
      (read(in) & 0xff) <<  8 | 
      (read(in) & 0xff)       ;
  }
  
  
  private int sniffIntLittleEndian(InputStream in) throws IOException {
    return
      (read(in) & 0xff)       | 
      (read(in) & 0xff) <<  8 | 
      (read(in) & 0xff) << 16 | 
      (read(in) & 0xff) << 24 ;
  }

  
  private int sniffShort(InputStream in, boolean intel) throws IOException {
    return intel ? sniffShortLittleEndian(in) : sniffShortBigEndian(in);  
  }
  
    
  private int sniffShortBigEndian(InputStream in) throws IOException {
    return
      (read(in) & 0xff) << 8 | 
      (read(in) & 0xff)      ;
  }
  
  
  private int sniffShortLittleEndian(InputStream in) throws IOException {
    return 
      (read(in) & 0xff)      | 
      (read(in) & 0xff) << 8 ;
  }
  
  
  private int string2int(String s) {
    if (s.length()!=4) throw new IllegalArgumentException();
    return
      (s.charAt(0) & 0xff) << 24 | 
      (s.charAt(1) & 0xff) << 16 | 
      (s.charAt(2) & 0xff) <<  8 | 
      (s.charAt(3) & 0xff)       ;
  }
  
} 
