
package genj.renderer;

import java.awt.Graphics2D;
import java.awt.RenderingHints.Key;
import java.awt.geom.Dimension2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;


public class DPI {

  public final static Key KEY = new DPIHintKey();

  public final static double INCH = 2.54D;

  private int horizontal, vertical;
  
  public int horizontal() {
    return horizontal;
  }
  
  public int vertical() {
    return vertical;
  }
  
  public DPI(int horizontal, int vertical) {
    this.horizontal = horizontal;
    this.vertical = vertical;
  }
  
  
  public static DPI get(Graphics2D graphics) {
    DPI dpi = (DPI)graphics.getRenderingHint(KEY);
    if (dpi==null)
      dpi = Options.getInstance().getDPI();
    return dpi;
  }
  
  public Dimension2D toPixel(Dimension2D inches) {
    return new gj.awt.geom.Dimension2D.Double(
        inches.getWidth() * horizontal,
        inches.getHeight() * vertical
    );
  }
  
  public Rectangle2D toPixel(Rectangle2D inches) {
    return new Rectangle2D.Double(
      inches.getX() * horizontal,
      inches.getY() * vertical,
      inches.getWidth() * horizontal,
      inches.getHeight() * vertical
    );
  }
  
  public Line2D toPixel(Line2D inches) {
    return new Line2D.Double(toPixel(inches.getP1()), toPixel(inches.getP2()));
  }
  
  public Point2D toPixel(Point2D inches) {
    return new Point2D.Double(inches.getX() * horizontal, inches.getY() * vertical);
  }
  
  @Override
  public String toString() {
    return horizontal+" by "+vertical+" dpi";
  }
 
  
  private static class DPIHintKey extends Key {
    
    private DPIHintKey() {
      super(0);
    }

    @Override
    public boolean isCompatibleValue(Object val) {
      return val instanceof DPI;
    }
  }  
}
