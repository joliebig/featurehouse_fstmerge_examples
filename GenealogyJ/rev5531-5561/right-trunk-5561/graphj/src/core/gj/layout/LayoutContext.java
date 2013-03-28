
package gj.layout;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.logging.Logger;


public interface LayoutContext {

  
  public Rectangle2D getPreferredBounds();
  
  
  public boolean isDebug();
  
  
  public void addDebugShape(Shape shape);
  
  
  public Logger getLogger();

}
