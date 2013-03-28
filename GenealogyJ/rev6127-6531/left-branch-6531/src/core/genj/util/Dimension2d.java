
package genj.util;

import java.awt.Dimension;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;


public class Dimension2d extends Dimension2D {
  
  private float width  = 0;
  private float height = 0;

  public Dimension2d(Rectangle2D rectangle) {
    width = (float)rectangle.getWidth();
    height= (float)rectangle.getHeight();
  }
  
  public Dimension2d() {
  }
  
  public Dimension2d(double width, double height) {
    setSize(width, height);
  }
  
  public double getHeight() {
    return height;
  }

  public double getWidth() {
    return width;
  }

  public void setSize(double width, double height) {
    this.width = (float)width;
    this.height= (float)height;
  }
  
  public String toString() {
    return width + " x " + height;
  }
  
  public Dimension toDimension() {
    return new Dimension(
      (int)Math.ceil(width),
      (int)Math.ceil(height)
    );
  }

  public static Dimension getDimension(Dimension2D dim) {
    return new Dimension(
      (int)Math.ceil(dim.getWidth()),
      (int)Math.ceil(dim.getHeight())
    );
  }
  
} 

