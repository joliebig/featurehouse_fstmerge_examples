
package gj.util;

import gj.layout.LayoutContext;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.logging.Logger;


public class DefaultLayoutContext implements LayoutContext {
  
  private Collection<Shape> debugShapes = null;
  private Logger logger;
  private Rectangle2D preferredBounds;
  
  private final static Logger DEFAULTLOGGER = Logger.getLogger("layout");
  
  
  public DefaultLayoutContext() {
    this(null, DEFAULTLOGGER, null);
  }
  
  
  public DefaultLayoutContext(Collection<Shape> debugShape, Logger logger, Rectangle2D preferredBounds) {
    this.debugShapes = debugShape;
    this.logger = logger;
    this.preferredBounds = preferredBounds;
  }

  
  public boolean isDebug() {
    return debugShapes!=null;
  }
  
  
  public void addDebugShape(Shape shape) {
    if (debugShapes!=null)
      debugShapes.add(shape);  
  }

  
  public Logger getLogger() {
    return logger;
  }

  
  public Rectangle2D getPreferredBounds() {
    return preferredBounds;
  }


}
