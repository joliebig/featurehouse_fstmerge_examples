
package org.jmol.util;

import java.util.Enumeration;
import java.util.Hashtable;

import org.jmol.viewer.JmolConstants;
import org.jmol.g3d.Graphics3D;
import org.jmol.util.ArrayUtil;




 public class ColorEncoder {
  public ColorEncoder() {
  }
    
  public final static String BYELEMENT_PREFIX  = "byelement";
  public final static String BYRESIDUE_PREFIX = "byresidue";
  private final static String BYELEMENT_JMOL = BYELEMENT_PREFIX + "_jmol"; 
  private final static String BYELEMENT_RASMOL = BYELEMENT_PREFIX + "_rasmol";
  private final static String BYRESIDUE_SHAPELY = BYRESIDUE_PREFIX + "_shapely"; 
  private final static String BYRESIDUE_AMINO = BYRESIDUE_PREFIX + "_amino"; 
  
  private final static String[] colorSchemes = {
    "roygb", "bgyor", "rwb", "bwr", "low", "high",   
    BYELEMENT_JMOL, BYELEMENT_RASMOL, BYRESIDUE_SHAPELY, 
    BYRESIDUE_AMINO, "colorRGB", "user", "resu"};

  private final static int schemeIndex(String colorScheme) {
    for (int i = 0; i < colorSchemes.length; i++)
      if (colorSchemes[i].equalsIgnoreCase(colorScheme))
        return (i < -USER ? i : -i);
    return -1;
  }
  

  public final static int ROYGB = 0;
  public final static int BGYOR = 1;
  public final static int RWB   = 2;
  public final static int BWR   = 3;
  public final static int LOW   = 4;
  public final static int HIGH  = 5;
  public final static int JMOL = 6;
  public final static int RASMOL = 7;
  public final static int SHAPELY = 8;
  public final static int AMINO = 9;
  public final static int COLOR_RGB = 10;
  public final static int USER = -11;
  public final static int RESU = -12;

  private int palette = ROYGB;

  private static int[] userScale = new int[] {0xFF808080};
  private static int[] thisScale = new int[] {0xFF808080};
  private static String thisName = "scheme";
  private static boolean isColorIndex;
  private static Hashtable schemes = new Hashtable();
  private static int[] rasmolScale = new int[JmolConstants.argbsCpk.length];
  private static int[] argbsCpk = JmolConstants.argbsCpk;
  private static int[] argbsRoygb = JmolConstants.argbsRoygbScale;
  private static int[] argbsRwb = JmolConstants.argbsRwbScale;
  private static int[] argbsShapely = JmolConstants.argbsShapely;
  private static int[] argbsAmino = JmolConstants.argbsAmino;
  private static int ihalf = JmolConstants.argbsRoygbScale.length/3;
  
  public static synchronized int[] getRasmolScale(boolean forceNew) {
    if (rasmolScale[0] == 0 || forceNew) {
      int argb = JmolConstants.argbsCpkRasmol[0] | 0xFF000000;
      for (int i = rasmolScale.length; --i >= 0; )
        rasmolScale[i] = argb;
      for (int i = JmolConstants.argbsCpkRasmol.length; --i >= 0; ) {
        argb = JmolConstants.argbsCpkRasmol[i];
        rasmolScale[argb >> 24] = argb | 0xFF000000;
      }
    }
    return rasmolScale;
  }

  public static synchronized int makeColorScheme(String name, int[] scale,
                                                  boolean isOverloaded) {
    name = fixName(name);
    if (scale == null) {
      schemes.remove(name);
      int iScheme = getColorScheme(name, false, isOverloaded);
      if (isOverloaded)
        switch (iScheme) {
        case ROYGB:
        case BGYOR:
          argbsRoygb = JmolConstants.argbsRoygbScale;
          break;
        case RWB:
        case BWR:
          argbsRwb = JmolConstants.argbsRwbScale;
          break;
        case JMOL:
          argbsCpk = JmolConstants.argbsCpk;
          break;
        case RASMOL:
          getRasmolScale(true);
          break;
        case AMINO:
          argbsAmino = JmolConstants.argbsAmino;
          break;
        case SHAPELY:
          argbsShapely = JmolConstants.argbsShapely;
          break;
        }
      return (iScheme == Integer.MAX_VALUE ? ROYGB : iScheme);
    }
    schemes.put(thisName = name, thisScale = scale);
    checkColorIndex();
    int iScheme = getColorScheme(name, false, isOverloaded);
    if (isOverloaded)
      switch (iScheme) {
      case ROYGB:
      case BGYOR:
        argbsRoygb = thisScale;
        break;
      case RWB:
      case BWR:
        argbsRwb = thisScale;
        break;
      case JMOL:
        argbsCpk = thisScale;
        break;
      case RASMOL:
        break;
      case AMINO:
        argbsAmino = thisScale;
        break;
      case SHAPELY:
        argbsShapely = thisScale;
        break;
      }
    return -1;
  }

  private static void checkColorIndex() {
    isColorIndex = (thisName.indexOf(BYELEMENT_PREFIX) == 0 
        || thisName.indexOf(BYRESIDUE_PREFIX) == 0);
  }
  
  private static String fixName(String name) {
    name = name.toLowerCase();
    if (name.equals(BYELEMENT_PREFIX))
      return BYELEMENT_JMOL;
    if (name.equals("jmol"))
      return BYELEMENT_JMOL;
    if (name.equals("rasmol"))
      return BYELEMENT_RASMOL;
    if (name.equals(BYRESIDUE_PREFIX))
      return BYRESIDUE_SHAPELY;
    return name;  
  }
  
  public int setColorScheme(String colorScheme) {
    return palette = getColorScheme(colorScheme, false);
  }

  public String getColorSchemeName() {
    return getColorSchemeName(palette);  
  }
  
  public final static String getColorSchemeName(int i) {
    int absi = Math.abs(i);
    return (i == -1 ? thisName : absi < colorSchemes.length && absi >= 0 ? colorSchemes[absi] : null);  
  }

  public final static int getColorScheme(String colorScheme, boolean isOverloaded) {
    return getColorScheme(colorScheme, true, isOverloaded);
  }

  private final static int getColorScheme(String colorScheme,
                                          boolean defaultToRoygb,
                                          boolean isOverloaded) {
    colorScheme = colorScheme.toLowerCase();
    int pt = Math.max(colorScheme.indexOf("=")
        , colorScheme.indexOf("["));
    if (pt >= 0) {
      String name = TextFormat.replaceAllCharacters(colorScheme
          .substring(0, pt), " =", "");
      if (name.length() > 0)
        isOverloaded = true;
      int n = 0;
      pt = -1;
      while ((pt = colorScheme.indexOf("[", pt + 1)) >= 0)
        n++;
      if (n == 0)
        return makeColorScheme(name, null, isOverloaded);
      int[] scale = new int[n];
      pt = -1;
      n = 0;
      int c;
      int pt2;
      while ((pt = colorScheme.indexOf("[", pt + 1)) >= 0) {
        pt2 = colorScheme.indexOf("]", pt);
        if (pt2 < 0)
          pt2 = colorScheme.length() - 1;
        scale[n++] = c = Graphics3D.getArgbFromString(colorScheme.substring(pt,
            pt2 + 1));
        if (c == 0) {
          Logger.error("error in color value: "
              + colorScheme.substring(pt, pt2 + 1));
          return ROYGB;
        }
      }
      if (name.equals("user")) {
        setUserScale(scale);
        return USER;
      }
      return makeColorScheme(name, scale, isOverloaded);
    }
    colorScheme = fixName(colorScheme);
    int ipt = schemeIndex(colorScheme) ;
    if (schemes.containsKey(colorScheme)) {
      thisName = colorScheme;
      thisScale = (int[]) schemes.get(colorScheme);
      checkColorIndex();
      return ipt;
    }
    return (ipt != -1 ? ipt : defaultToRoygb ? ROYGB 
        : Integer.MAX_VALUE);
  }

  
  public final static void setUserScale(int[] scale) {
    userScale = scale;  
    makeColorScheme("user", scale, false);
  }
  
  public final static String getState(StringBuffer sfunc) {
    StringBuffer s = new StringBuffer();
    Enumeration e = schemes.keys();
    int n = 0;
    while (e.hasMoreElements()) {
      String name = (String) e.nextElement();
      if (name.length() > 0 & n++ >= 0) 
        s.append("color \"" + name + "=" + getColorSchemeList((int[])schemes.get(name)) + "\";\n");
    }
    
    
      
    if (n > 0 && sfunc != null)
      sfunc.append("\n  _setColorState\n");
    return (n > 0 && sfunc != null ? "function _setColorState() {\n" 
        + s.append("}\n\n").toString() : s.toString());
  }
  
  public static String getColorSchemeList(int[] scheme) {
    String colors = "";
    for (int i = 0; i < scheme.length; i++)
      colors += (i == 0 ? "" : " ") + Escape.escapeColor(scheme[i]);
    return colors;
  }

  public final static int[] getColorSchemeArray(int palette) {
    switch (palette) {
    
    case -1:
      return ArrayUtil.arrayCopy(thisScale, 0, -1, false);      
    case ROYGB:
      return ArrayUtil.arrayCopy(argbsRoygb, 0, -1, false);
    case BGYOR:
      return ArrayUtil.arrayCopy(argbsRoygb, 0, -1, true);
    case LOW:
      return ArrayUtil.arrayCopy(argbsRoygb, 0, ihalf, false);
    case HIGH:
      int[] a = ArrayUtil.arrayCopy(argbsRoygb, ihalf, -1, false);
      int[] b = new int[ihalf];
      for (int i = ihalf; --i >= 0;)
        b[i] = a[i + i];
      return b;
    case RWB:
      return ArrayUtil.arrayCopy(argbsRwb, 0, -1, false);
    case BWR:
      return ArrayUtil.arrayCopy(argbsRwb, 0, -1, true);
    case JMOL:
      return ArrayUtil.arrayCopy(argbsCpk, 0, -1, false);
    case RASMOL:
      return ArrayUtil.arrayCopy(getRasmolScale(false), 0, -1, false);
    case SHAPELY:
      return ArrayUtil.arrayCopy(argbsShapely, 0, -1, false);
    case AMINO:
      return ArrayUtil.arrayCopy(argbsAmino, 0, -1, false);
    case COLOR_RGB:
      return new int[0];
    case USER:
      return ArrayUtil.arrayCopy(userScale, 0, -1, false);
    case RESU:
      return ArrayUtil.arrayCopy(userScale, 0, -1, true);
    default:
      return null;
    }

  }
  
  private final static int GRAY = 0xFF808080;
  
  public final int getArgbFromPalette(float val, float lo, float hi) {
    return getArgbFromPalette(val, lo, hi, palette);
  }

  public final static int getArgbFromPalette(float val, float lo, float hi, int palette) {
    if (Float.isNaN(val))
      return GRAY;
    switch (palette) {
    case -1:
      if (isColorIndex) {
        lo = 0;
        hi = thisScale.length;
      }
      return thisScale[quantize(val, lo, hi, thisScale.length)];
    case ROYGB:
      return JmolConstants.argbsRoygbScale[quantize(val, lo, hi, JmolConstants.argbsRoygbScale.length)];
    case BGYOR:
      return JmolConstants.argbsRoygbScale[quantize(-val, -hi, -lo, JmolConstants.argbsRoygbScale.length)];
    case LOW:
      return JmolConstants.argbsRoygbScale[quantize(val, lo, hi, ihalf)];
    case HIGH:
      return JmolConstants.argbsRoygbScale[ihalf + quantize(val, lo, hi, ihalf) * 2];
    case RWB:
      return JmolConstants.argbsRwbScale[quantize(val, lo, hi, JmolConstants.argbsRwbScale.length)];
    case BWR:
      return JmolConstants.argbsRwbScale[quantize(-val, -hi, -lo, JmolConstants.argbsRwbScale.length)];
    case USER:
      return (userScale.length == 0 ? 0xFF808080 : userScale[quantize(val, lo, hi, userScale.length)]);
    case RESU:
      return (userScale.length == 0 ? 0xFF808080 : userScale[quantize(-val, -hi, -lo, userScale.length)]);
    case JMOL:
      return argbsCpk[colorIndex((int)val, argbsCpk.length)];
    case RASMOL:
      return getRasmolScale(false)[colorIndex((int)val, rasmolScale.length)];
    case SHAPELY:
      return JmolConstants.argbsShapely[colorIndex((int)val, JmolConstants.argbsShapely.length)];
    case AMINO:
      return JmolConstants.argbsAmino[colorIndex((int)val, JmolConstants.argbsAmino.length)];
    case COLOR_RGB:
      return (int)val;
    default:
      return GRAY;
    }
  }

  public final static short getColorIndexFromPalette(float val, float lo, float hi, int palette) {
     return getColorIndex(getArgbFromPalette(val, lo, hi, palette));
  }

  public final static short getColorIndex(int c) {
    return Graphics3D.getColix(c);
  }

  public final static int quantize(float val, float lo, float hi, int segmentCount) {
    
    float range = hi - lo;
    if (range <= 0 || Float.isNaN(val))
      return segmentCount / 2;
    float t = val - lo;
    if (t <= 0)
      return 0;
    float quanta = range / segmentCount;
    int q = (int)(t / quanta + 0.0001f);  
    if (q >= segmentCount)
      q = segmentCount - 1;
    return q;
  }

  public final static int colorIndex(int q, int segmentCount) {
    return (q <= 0 | q >= segmentCount ? 0 : q);
  }

  public short getColorIndexFromPalette(float val, float lo, float hi) {
    return getColorIndexFromPalette(val, lo, hi, palette);
  }


}
