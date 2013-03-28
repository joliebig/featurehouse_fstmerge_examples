
package genj.util.swing;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;

public class GraphicsHelper {

  
  public static void render(Graphics2D graphics, String str, Rectangle2D box, double xalign, double yalign) {
    
    FontMetrics fm = graphics.getFontMetrics();
    LineMetrics lm = fm.getLineMetrics(str, graphics);

    float h = 0;
    String[] lines = str.split("\\\n");
    float[] ws = new float[lines.length];
    for (int i=0;i<lines.length;i++) {
      Rectangle2D r = fm.getStringBounds(lines[i], graphics);
      ws[i] = (float)r.getWidth();
      h = Math.max(h,(float)r.getHeight());
    }

    Shape clip = graphics.getClip();
    graphics.clip(box);
    
    for (int i=0;i<lines.length;i++) {
      double x = Math.max(box.getX(), box.getCenterX() - ws[i]*xalign);
      double y = Math.max(box.getY(), box.getY() + (box.getHeight()-lines.length*h)*yalign + i*h + h - lm.getDescent()); 
      
      graphics.drawString(lines[i], (float)x, (float)y);
    }
    
    graphics.setClip(clip);
  }
  
  
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
