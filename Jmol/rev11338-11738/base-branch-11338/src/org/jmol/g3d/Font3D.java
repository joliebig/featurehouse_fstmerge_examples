
package org.jmol.g3d;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.font.TextAttribute;
import java.util.Hashtable;

final public class Font3D {

  public final byte fid;
  public final String fontFace;
  public final String fontStyle;
  public final float fontSizeNominal;
  public final int idFontFace;
  public final int idFontStyle;
  public final float fontSize;
  public final Font font;
  public final FontMetrics fontMetrics;

  private Font3D(byte fid,
                 int idFontFace, int idFontStyle, float fontSize,
                 float fontSizeNominal, Font font, FontMetrics fontMetrics) {
    this.fid = fid;
    this.fontFace = fontFaces[idFontFace];
    this.fontStyle = fontStyles[idFontStyle];
    this.idFontFace = idFontFace;
    this.idFontStyle = idFontStyle;
    this.fontSize = fontSize;
    this.fontSizeNominal = fontSizeNominal;
    this.font = font;
    this.fontMetrics = fontMetrics;
    
  }

  
  
  private final static int FONT_ALLOCATION_UNIT = 8;
  private static int fontkeyCount = 1;
  private static int[] fontkeys = new int[FONT_ALLOCATION_UNIT];
  private static Font3D[] font3ds = new Font3D[FONT_ALLOCATION_UNIT];

  static synchronized Font3D getFont3D(int fontface, int fontstyle, float fontsize,
                          float fontsizeNominal, Platform3D platform) {
    if (platform == null)
      return null;
    if (fontsize > 0xFF)
      fontsize = 0xFF;
    int fontsizeX16 = ((int)fontsize) << 4;
    int fontkey =
      ((fontface & 3) | ((fontstyle & 3) << 2) | (fontsizeX16 << 4));
    
    for (int i = fontkeyCount; --i > 0; )
      if (fontkey == fontkeys[i] && font3ds[i].fontSizeNominal == fontsizeNominal)
        return font3ds[i];
    
    int fontIndexNext = fontkeyCount++;
    if (fontIndexNext == fontkeys.length) {
      int[] t0 = new int[fontIndexNext + FONT_ALLOCATION_UNIT];
      System.arraycopy(fontkeys, 0, t0, 0, fontIndexNext);
      fontkeys = t0;
      
      Font3D[] t1 = new Font3D[fontIndexNext + FONT_ALLOCATION_UNIT];
      System.arraycopy(font3ds, 0, t1, 0, fontIndexNext);
      font3ds = t1;
    }
    Font font = new Font(getFontMap(fontFaces[fontface], fontstyle, fontsize));
    FontMetrics fontMetrics = platform.graphicsOffscreen.getFontMetrics(font);
    Font3D font3d = new Font3D((byte)fontIndexNext,
                               fontface, fontstyle, fontsize, fontsizeNominal,
                               font, fontMetrics);
    
    
    font3ds[fontIndexNext] = font3d;
    fontkeys[fontIndexNext] = fontkey;
    return font3d;
  }

  private static Hashtable getFontMap(String fontFace, int idFontStyle, float fontSize) {
    Hashtable fontMap = new Hashtable();
    fontMap.put(TextAttribute.FAMILY, fontFace);
    if ((idFontStyle & 1) == 1)
      fontMap.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
    if ((idFontStyle & 2) == 2)
      fontMap.put(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);
    fontMap.put(TextAttribute.SIZE, new Float(fontSize));
    return fontMap;
  }
  
  public final static int FONT_FACE_SANS  = 0;
  public final static int FONT_FACE_SERIF = 1;
  public final static int FONT_FACE_MONO  = 2;
  
  private final static String[] fontFaces =
  {"SansSerif", "Serif", "Monospaced", ""};

  public final static int FONT_STYLE_PLAIN      = 0;
  public final static int FONT_STYLE_BOLD       = 1;
  public final static int FONT_STYLE_ITALIC     = 2;
  public final static int FONT_STYLE_BOLDITALIC = 3;
  
  private final static String[] fontStyles =
  {"Plain", "Bold", "Italic", "BoldItalic"};
  
  public static int getFontFaceID(String fontface) {
    if ("Monospaced".equalsIgnoreCase(fontface))
      return FONT_FACE_MONO;
    if ("Serif".equalsIgnoreCase(fontface))
      return FONT_FACE_SERIF;
    return FONT_FACE_SANS;
  }

  public static int getFontStyleID(String fontstyle) {
    int i = 4;
    while (--i > 0)
      if (fontStyles[i].equalsIgnoreCase(fontstyle))
        break;
    return i;
  }

  public static Font3D getFont3D(byte fontID) {
    return font3ds[fontID & 0xFF];
  }
}

