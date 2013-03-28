
package gj.geom;

import java.awt.geom.PathIterator;


public interface PathIteratorKnowHow {

      
  public static final int[] SEG_SIZES = {2, 2, 4, 6, 0};
    
  
  public static final byte SEG_MOVETO  = (byte) PathIterator.SEG_MOVETO;
  public static final byte SEG_LINETO  = (byte) PathIterator.SEG_LINETO;
  public static final byte SEG_QUADTO  = (byte) PathIterator.SEG_QUADTO;
  public static final byte SEG_CUBICTO = (byte) PathIterator.SEG_CUBICTO;
  public static final byte SEG_CLOSE   = (byte) PathIterator.SEG_CLOSE;

  
  public final static String[] SEG_NAMES = {
    "moveto", "lineto", "quadto", "cubicto", "close",
  };

}

