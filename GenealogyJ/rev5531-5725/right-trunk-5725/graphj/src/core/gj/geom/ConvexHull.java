
package gj.geom;

import java.awt.Shape;
import java.awt.geom.AffineTransform;


public interface ConvexHull extends Shape {

  
  public abstract void transform(AffineTransform at);

} 
