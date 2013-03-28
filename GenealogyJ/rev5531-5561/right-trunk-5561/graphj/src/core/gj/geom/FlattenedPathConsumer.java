
package gj.geom;

import java.awt.geom.Point2D;


public interface FlattenedPathConsumer {

  
  public boolean consumeLine(Point2D start, Point2D end);
  
}
