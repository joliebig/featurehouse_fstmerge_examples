
package gj.ui;

import static gj.geom.PathIteratorKnowHow.SEG_LINETO;
import static gj.geom.PathIteratorKnowHow.SEG_MOVETO;
import static gj.geom.PathIteratorKnowHow.SEG_CLOSE;
import gj.geom.ShapeHelper;
import gj.layout.Graph2D;
import gj.layout.Routing;
import gj.model.Edge;
import gj.model.Vertex;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.Icon;


public class DefaultGraphRenderer implements GraphRenderer {

  
  private final static Shape ARROW_HEAD = ShapeHelper.createShape(0,0,1,1,new double[]{
      SEG_MOVETO, 0, 0, 
      SEG_LINETO, -3, -7, 
      SEG_LINETO,  3, -7, 
      SEG_CLOSE
  });

  
  public void render(Graph2D graph2d, Graphics2D graphics) {
  
    
    renderEdges(graph2d, graphics);    
    
    
    renderVertices(graph2d, graphics);
  
    
  }

  
  protected void renderVertices(Graph2D graph2d, Graphics2D graphics) {
    
    
    for (Vertex vertex : graph2d.getVertices()) {
      renderVertex(graph2d, vertex, graphics);
    }
    
    
  }

  protected void renderVertex(Graph2D graph2d, Vertex vertex, Graphics2D graphics) {
    
    
    Color color = getColor(vertex);
    Stroke stroke = getStroke(vertex);
  
    
    graphics.setColor(color);
    graphics.setStroke(stroke);
    Shape shape = graph2d.getShape(vertex);
    draw(shape, false, graphics);

    
    String text = getText(vertex);
    Icon icon = getIcon(vertex);

    Shape oldcp = graphics.getClip();
    graphics.clip(shape);
    draw(text, icon, shape.getBounds2D(), 0.5, 0.5, graphics);
    graphics.setClip(oldcp);

    
  }
  
  protected String getText(Vertex vertex) {
    return vertex==null ? "" : vertex.toString();
  }
  
  protected Icon getIcon(Vertex vertex) {
    return null; 
  }
  
  
  protected Color getColor(Vertex vertex) {
    return Color.BLACK;    
  }

  
  protected Color getColor(Edge edge) {
    return Color.BLACK;    
  }

  
  protected Stroke getStroke(Vertex vertex) {
    return new BasicStroke();    
  }

  
  protected void renderEdges(Graph2D graph2d, Graphics2D graphics) {
    
    for (Edge edge : graph2d.getEdges())
      renderEdge(graph2d, edge, graphics);
  
    
  }

  
  protected void renderEdge(Graph2D graph2d, Edge edge, Graphics2D graphics) {
    
    AffineTransform old = graphics.getTransform();
    
    
    graphics.setColor(getColor(edge));
    
    
    Routing path = graph2d.getRouting(edge);
    graphics.draw(graph2d.getRouting(edge));
    
    
    Point2D pos = path.getLastPoint();
    graphics.setBackground(getColor(edge));
    graphics.translate(pos.getX(), pos.getY());
    graphics.rotate(path.getLastAngle());
    graphics.fill(ARROW_HEAD);
    graphics.draw(ARROW_HEAD);
    
    
    graphics.setTransform(old);
  }

  
  protected void draw(Shape shape, boolean fill, Graphics2D graphics) {
    if (fill) graphics.fill(shape);
    else graphics.draw(shape);
  }

  
  protected void draw(String text, Icon icon, Rectangle2D at, double horizontalAlign, double verticalAlign,Graphics2D graphics) {

    
    FontMetrics fm = graphics.getFontMetrics();
    double height = 0;
    double width = 0;
    for (int cursor=0;cursor<text.length();) {
      int newline = text.indexOf('\n', cursor);
      if (newline<0) newline = text.length();
      String line = text.substring(cursor, newline);
      Rectangle2D r = fm.getStringBounds(line, graphics);
      LineMetrics lm = fm.getLineMetrics(line, graphics);
      width = Math.max(width, r.getWidth());
      height += r.getHeight();
      cursor = newline+1;
    }
    
    
    if (icon!=null) { 
      int iwidth = icon.getIconWidth();
      int cwidth = fm.charWidth(' '); 
      icon.paintIcon(null, graphics, (int)(at.getX() + (at.getWidth()-width-iwidth-cwidth)*horizontalAlign), (int)(at.getY() + (at.getHeight()-icon.getIconHeight())*verticalAlign));
      at.setRect(at.getX()+iwidth+cwidth, at.getY(), at.getWidth()-iwidth-cwidth, at.getHeight());
    }
    
    
    double x = at.getX() + (at.getWidth()-width)*horizontalAlign;
    double y = at.getY() + (at.getHeight()-height)*verticalAlign;
    for (int cursor=0;cursor<text.length();) {
      int newline = text.indexOf('\n', cursor);
      if (newline<0) newline = text.length();
      String line = text.substring(cursor, newline);
      Rectangle2D r = fm.getStringBounds(line, graphics);
      LineMetrics lm = fm.getLineMetrics(line, graphics);
      graphics.drawString(line, (float)x, (float)y + lm.getHeight() - lm.getDescent());
      cursor = newline+1;
      y += r.getHeight();
    }

    
  }
  
}
