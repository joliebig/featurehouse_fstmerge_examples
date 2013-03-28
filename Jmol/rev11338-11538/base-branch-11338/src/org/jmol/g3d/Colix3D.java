

package org.jmol.g3d;

import org.jmol.util.Int2IntHash;



class Colix3D {

  private static int colixMax = Graphics3D.SPECIAL_COLIX_MAX;
  private static int[] argbs = new int[128];
  private static int[] argbsGreyscale;
  private static int[][] ashades = new int[128][];
  private static int[][] ashadesGreyscale;
  private static final Int2IntHash colixHash = new Int2IntHash();

  Colix3D() {
  }
  
  static short getColix(int argb) {
    if (argb == 0)
      return 0;
    int translucentFlag = 0;
    if ((argb & 0xFF000000) != 0xFF000000) {
      
      
      
      
      argb |= 0xFF000000;
      translucentFlag = Graphics3D.TRANSLUCENT_50;
    }
    int c = colixHash.get(argb);
    return (c > 0 ? (short) (c | translucentFlag)
        : (short) (allocateColix(argb) | translucentFlag));
  }

  private synchronized static int allocateColix(int argb) {
    
    
    if ((argb & 0xFF000000) != 0xFF000000)
      throw new IndexOutOfBoundsException();
    for (int i = colixMax; --i >= Graphics3D.SPECIAL_COLIX_MAX; )
      if (argb == argbs[i])
        return (short)i;
    if (colixMax == argbs.length) {
      int oldSize = colixMax;
      int newSize = oldSize * 2;
      int[] t0 = new int[newSize];
      System.arraycopy(argbs, 0, t0, 0, oldSize);
      argbs = t0;

      if (argbsGreyscale != null) {
        t0 = new int[newSize];
        System.arraycopy(argbsGreyscale, 0, t0, 0, oldSize);
        argbsGreyscale = t0;
      }

      int[][] t2 = new int[newSize][];
      System.arraycopy(ashades, 0, t2, 0, oldSize);
      ashades = t2;

      if (ashadesGreyscale != null) {
        t2 = new int[newSize][];
        System.arraycopy(ashadesGreyscale, 0, t2, 0, oldSize);
        ashadesGreyscale = t2;
      }
    }
    argbs[colixMax] = argb;
    
    if (argbsGreyscale != null)
      argbsGreyscale[colixMax] = Graphics3D.calcGreyscaleRgbFromRgb(argb);
    colixHash.put(argb, colixMax);
    return colixMax++;
  }

  private synchronized static void calcArgbsGreyscale() {
    if (argbsGreyscale != null)
      return;
    int[] a = new int[argbs.length];
    for (int i = argbs.length; --i >= Graphics3D.SPECIAL_COLIX_MAX;)
      a[i] = Graphics3D.calcGreyscaleRgbFromRgb(argbs[i]);
    argbsGreyscale = a;
  }

  final static int getArgb(short colix) {
    return argbs[colix & Graphics3D.OPAQUE_MASK];
  }

  final static int getArgbGreyscale(short colix) {
    if (argbsGreyscale == null)
      calcArgbsGreyscale();
    return argbsGreyscale[colix & Graphics3D.OPAQUE_MASK];
  }

  final static int[] getShades(short colix) {
    colix &= Graphics3D.OPAQUE_MASK;
    int[] shades = ashades[colix];
    if (shades == null)
      shades = ashades[colix] = Shade3D.getShades(argbs[colix], false);
    return shades;
  }

  final static int[] getShadesGreyscale(short colix) {
    colix &= Graphics3D.OPAQUE_MASK;
    if (ashadesGreyscale == null)
      ashadesGreyscale = new int[ashades.length][];
    int[] shadesGreyscale = ashadesGreyscale[colix];
    if (shadesGreyscale == null)
      shadesGreyscale = ashadesGreyscale[colix] =
        Shade3D.getShades(argbs[colix], true);
    return shadesGreyscale;
  }

  final synchronized static void flushShades() {
    for (int i = colixMax; --i >= 0; )
      ashades[i] = null;
    Shade3D.sphereShadingCalculated = false;
  }

  
  
  final static int[] predefinedArgbs = {
    0xFF000000, 
    0xFFFFA500, 
    0xFFFFC0CB, 
    0xFF0000FF, 
    0xFFFFFFFF, 
    0xFF00FFFF, 
    0xFFFF0000, 
    0xFF008000, 
    0xFF808080, 
    0xFFC0C0C0, 
    0xFF00FF00, 
    0xFF800000, 
    0xFF000080, 
    0xFF808000, 
    0xFF800080, 
    0xFF008080, 
    0xFFFF00FF, 
    0xFFFFFF00, 
    0xFFFF69B4, 
    0xFFFFD700, 
  };

  static {
    for (int i = 0; i < predefinedArgbs.length; ++i)
      getColix(predefinedArgbs[i]);
  }
}
