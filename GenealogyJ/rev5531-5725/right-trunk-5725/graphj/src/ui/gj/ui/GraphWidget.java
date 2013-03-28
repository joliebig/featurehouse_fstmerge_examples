
package gj.ui;

import gj.layout.Graph2D;
import gj.model.Vertex;
import gj.util.EmptyGraph;
import gj.util.LayoutHelper;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;

import javax.swing.JComponent;


public class GraphWidget extends JComponent {

  
  private Graph2D graph2d;
  
  
  private Rectangle graphBounds;
  
  
  private boolean isAntialiasing = true;
  
  
  private GraphRenderer renderer;
  
  
  public GraphWidget() {
    this(new EmptyGraph(), new DefaultGraphRenderer());
  }
  
  
  public GraphWidget(Graph2D graphLayout) {
    this(graphLayout, new DefaultGraphRenderer());
  }
  
  
  public GraphWidget(Graph2D graph2d, GraphRenderer renderer) {
    this.renderer = renderer;
    this.graph2d = graph2d;
    graphBounds = LayoutHelper.getBounds(graph2d).getBounds();
  }
  
  
  public void setRenderer(GraphRenderer renderer) {
    
    this.renderer = renderer;
    repaint();
    
  }
  
  
  public void setGraph2D(Graph2D graph2d) {
    setGraph2D(graph2d, null);
  }
    
  public void setGraph2D(Graph2D graph2d, Rectangle bounds) {
    
    this.graph2d = graph2d;
    graphBounds = bounds != null ? bounds : LayoutHelper.getBounds(graph2d).getBounds();
    
    
    revalidate();
    repaint();
    
    
  }
  
  
  public Graph2D getGraph2D() {
    return graph2d;
  }
  
  
  public boolean isAntialiasing() {
    return isAntialiasing;
  }

  
  public void setAntialiasing(boolean set) {
    isAntialiasing=set;
    repaint();
  }
  
  
  private int getXOffset() {
    if (graph2d==null) return 0;
    return -graphBounds.x+(getWidth()-graphBounds.width)/2;
  }
  
  
  private int getYOffset() {
    if (graph2d==null) return 0;
    return -graphBounds.y+(getHeight()-graphBounds.height)/2;
  }
  
  
  @Override
  public Dimension getPreferredSize() {
    if (graph2d==null) return new Dimension();
    return graphBounds.getSize();
  }

  
  @Override
  protected void paintComponent(Graphics g) {
    
    
    g.setColor(Color.white);
    g.fillRect(0,0,getWidth(),getHeight());

    
    if (graph2d==null) 
      return;
    
    
    Graphics2D graphics = (Graphics2D)g;
    
    
    graphics.setRenderingHint(
      RenderingHints.KEY_ANTIALIASING,
      isAntialiasing ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF
    );
    
    
    synchronized (graph2d) {
      
      
      graphics.translate(getXOffset(),getYOffset());
      
      renderer.render(graph2d, graphics);
    }

    
  }
  
  
  protected Point getPoint(Point p) {
    return new Point(
      p.x - getXOffset(),
      p.y - getYOffset()
    );
  }
  
  protected Point model2screen(Point2D point) {
    return new Point(
        ((int)point.getX()) + getXOffset(),
        ((int)point.getY()) + getYOffset()
    );
  }
  
  
  public Vertex getVertexAt(Point point) {
    if (graph2d==null)
      return null;
    
    point = getPoint(point);
    for (Vertex v : graph2d.getVertices()) {
      if (graph2d.getShape(v).contains(point.x, point.y))
        return v;
    }
    return null;
  }
  
}
