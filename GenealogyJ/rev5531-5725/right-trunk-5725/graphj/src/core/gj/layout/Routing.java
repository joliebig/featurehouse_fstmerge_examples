
package gj.layout;

import java.awt.Shape;
import java.awt.geom.Point2D;



public interface Routing extends Shape {
  
  
  public Point2D getFirstPoint();
  
  
  public Point2D getLastPoint();
  
  
  public double getFirstAngle();
  
  
  public double getLastAngle();

}
