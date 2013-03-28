
package genj.print;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Dimension2D;

import javax.swing.JComponent;


public interface Printer {
  
    public void setView(JComponent view);
  
  
  public Dimension calcSize(Dimension2D pageSizeInInches, Point dpi);
  
    
  public void renderPage(Graphics2D g, Point page, Dimension2D pageSizeInInches, Point dpi, boolean preview);
  
} 
