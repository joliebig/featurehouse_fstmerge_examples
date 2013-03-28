
package genj.print;

import java.awt.Dimension;
import java.awt.Graphics2D;


public interface PageRenderer {
  
  
  public Dimension getPages(Page page);
  
    
  public void renderPage(Graphics2D g, Page page);
  
} 
