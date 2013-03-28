
package genj.util.swing;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;

public class GraphicsHelper {

  
  public static void render(Graphics2D graphics, String str, double x, double y, double xalign, double yalign) {
    
    FontMetrics fm = graphics.getFontMetrics();
    Rectangle2D r = fm.getStringBounds(str, graphics);
    LineMetrics lm = fm.getLineMetrics(str, graphics);
    
    float
      w = (float)r.getWidth(),
      h = (float)r.getHeight();
      
    x = x- w*xalign;
    y = y - h*yalign + h - lm.getDescent(); 
      
    graphics.drawString(str, (float)x, (float)y);
  }
  
}
