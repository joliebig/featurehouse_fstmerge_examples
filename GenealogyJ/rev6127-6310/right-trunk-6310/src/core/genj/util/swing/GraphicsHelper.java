
package genj.util.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.font.LineMetrics;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import javax.swing.Icon;

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
  
  
  public static Rectangle render(Graphics2D graphics, String str, double x, double y, double xalign, double yalign) {
    
    FontMetrics fm = graphics.getFontMetrics();
    Rectangle2D r = fm.getStringBounds(str, graphics);
    LineMetrics lm = fm.getLineMetrics(str, graphics);
    
    float h = (float)r.getHeight();
    float w = (float)r.getWidth();
    
    x = x- w*xalign;
    y = y - h*yalign; 
      
    graphics.drawString(str, (float)x, (float)y + h - lm.getDescent());
    
    return new Rectangle((int)x,(int)y,(int)w,(int)h);
  }
  
  public static Icon getIcon(int size, Shape shape, Color color) {
    return new ShapeAsIcon(size, shape, color);
  }
  
  public static Icon getIcon(int size, Shape shape) {
    return new ShapeAsIcon(size, shape, null);
  }
  
  public static Icon getIcon(double... shape) {
    return getIcon(null, shape);
  }
  public static Icon getIcon(Color color, double... shape) {
    GeneralPath path = new GeneralPath();
    path.moveTo(shape[0],shape[1]);
    for (int i=2;i<shape.length;i+=2) 
      path.lineTo(shape[i+0], shape[i+1]);
    path.closePath();
    return new ShapeAsIcon(path, color);
  }
  
  
  private static class ShapeAsIcon implements Icon {

    private Dimension size;
    private Shape shape;
    private Color color;

    private ShapeAsIcon(Shape shape, Color color) {
      this.color = color;
      this.size = shape.getBounds().getSize();
      this.shape = shape;
    }
    private ShapeAsIcon(int size, Shape shape, Color color) {
      this.color = color;
      this.size = new Dimension(size, size);
      this.shape = shape;
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
      if (color!=null)
        g.setColor(color);
      g.translate(x, y);
      ((Graphics2D) g).fill(shape);
      g.translate(-x, -y);
    }

    public int getIconWidth() {
      return size.width;
    }

    public int getIconHeight() {
      return size.height;
    }

  }
}
