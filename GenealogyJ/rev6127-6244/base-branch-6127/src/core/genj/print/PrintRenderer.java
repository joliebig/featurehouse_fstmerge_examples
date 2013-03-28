
package genj.print;

import gj.awt.geom.Dimension2D;

import java.awt.Graphics2D;


public interface PrintRenderer {
  
  
  public Dimension2D getSize();
  
    
  public void render(Graphics2D g);
  
} 
