
package genj.util.swing;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Stack;

public class UnitGraphics {
  
  
  private Stack stackTransformations = new Stack();

  
  private Stack clipStack = new Stack();

  
  private double unitx = 1, unity = 1;
  
  
  private Graphics2D graphics;
  
  
  private Line2D.Double line = new Line2D.Double();

  
  public UnitGraphics(Graphics g, double unitX, double unitY) {
    
    
    graphics = (Graphics2D)g;
    
    
    graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    
    
    pushTransformation();
    
    
    unitx = unitX;
    unity = unitY;
  }
  
    public Point2D getUnit() {
    return new Point2D.Double(unitx,unity);
  }
  
      public void setAntialiasing(boolean set) {
    graphics.setRenderingHint(
      RenderingHints.KEY_ANTIALIASING,
      set ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF
    );
  }
  
    public void translate(double dx, double dy) {
    
    
    
    graphics.translate((int)(dx * unitx), (int)(dy * unity));
  }
  
  
  public void scale(double sx, double sy) {
    graphics.scale(sx * unitx, sy * unity);
  }
  
    public Graphics2D getGraphics() {
    return graphics;
  }
  
    public Rectangle2D getClip() {
    Rectangle r = graphics.getClipBounds();
    return new Rectangle2D.Double(
      r.getMinX()/unitx,
      r.getMinY()/unity,
      r.getWidth()/unitx,
      r.getHeight()/unity
    );
  }
  
  
  public FontMetrics getFontMetrics() {
    return graphics.getFontMetrics();
  }  
  
  public void setColor(Color color) {
    if (color!=null) graphics.setColor(color);
  }
  
  
  public void setFont(Font font) {
    if (font!=null) graphics.setFont(font);    
  }
  
  
  public void draw(double x1, double y1, double x2, double y2) {
    line.setLine(x1*unitx,y1*unity,x2*unitx,y2*unity);
    graphics.draw(line);
  }
  
  
  public void draw(String str, double x, double y, double xalign, double yalign) {
    draw(str,x,y,xalign,yalign,0,0);
  }
  
  
  public void draw(String str, double x, double y, double xalign, double yalign, int dx, int dy) {
    
    FontMetrics fm = graphics.getFontMetrics();
    Rectangle2D r = fm.getStringBounds(str, graphics);
    LineMetrics lm = fm.getLineMetrics(str, graphics);
    
    float
      w = (float)r.getWidth(),
      h = (float)r.getHeight();
      
    x = x*unitx - w*xalign + dx;
    y = y*unity - h*yalign + h - lm.getDescent() + dy; 
      
    graphics.drawString(str, (float)x, (float)y);
  }
  
    public void draw(Shape shape, double x, double y) {
    draw(shape,x,y,false);
  }

  
  public void draw(Shape shape, double x, double y, boolean fill) {
    
    AffineTransform at = new AffineTransform();
    at.scale(unitx, unity);
    at.translate(x,y);
    GeneralPath gp = new GeneralPath(shape);
    gp.transform(at);
    shape = gp;
    
    if (fill) graphics.fill(shape);
    else graphics.draw(shape);
  }
  
  
  public void draw(ImageIcon img, double x, double y, double xalign, double yalign) {
    int
      ix = (int)(x*unitx - xalign*img.getIconWidth ()),
      iy = (int)(y*unity - yalign*img.getIconHeight());
    img.paintIcon(graphics, ix, iy);
  }
  
  
  
  public void popTransformation() {
    graphics.setTransform((AffineTransform)stackTransformations.pop());
  }

  
  public void pushTransformation() {
    stackTransformations.push(graphics.getTransform());
  }

  
  public void pushClip(double x, double y, Rectangle2D r) {
    pushClip(x+r.getMinX(), y+r.getMinY(), x+r.getMaxX(), y+r.getMaxY());
  }

  
  public void pushClip(Rectangle2D r) {
    pushClip(r.getMinX(), r.getMinY(), r.getMaxX(), r.getMaxY());
  }

  
  public void pushClip(double x1, double y1, double x2, double y2) {
    clipStack.push(graphics.getClip());
    double
      x =  x1*unitx,
      y =  y1*unity,
      w = (x2*unitx)-x,
      h = (y2*unity)-y;
    graphics.clip(new Rectangle2D.Double(x,y,w,h));
  }

  
  public void popClip() {
    graphics.setClip((Shape)clipStack.pop());    
  }

  
  public Rectangle getRectangle(Rectangle2D bounds) {
    int
      x1 = (int)Math.ceil(bounds.getMinX  ()*unitx),
      y1 = (int)Math.ceil(bounds.getMinY  ()*unity),
      x2 = (int)Math.floor (bounds.getMaxX  ()*unitx),
      y2 = (int)Math.floor (bounds.getMaxY  ()*unity);

    return new Rectangle(x1,y1,x2-x1,y2-y1);
  }

} 
