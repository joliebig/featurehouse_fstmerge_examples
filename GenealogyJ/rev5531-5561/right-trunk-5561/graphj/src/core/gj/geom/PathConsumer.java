
package gj.geom;

import java.awt.geom.Point2D;


public interface PathConsumer {

  
  
  public boolean consumeLine(Point2D start, Point2D end);
  
  
  public boolean consumeQuadCurve(Point2D start, Point2D ctrl, Point2D end);
  
  
  public boolean consumeCubicCurve(Point2D start, Point2D ctrl1, Point2D ctrl2, Point2D end);
  
}
