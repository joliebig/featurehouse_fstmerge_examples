
package gj.layout.graph.circular;

import gj.geom.Geometry;
import gj.geom.ShapeHelper;
import gj.layout.Graph2D;
import gj.layout.GraphLayout;
import gj.layout.LayoutContext;
import gj.layout.LayoutException;
import gj.model.Vertex;
import gj.util.LayoutHelper;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class CircularLayout implements GraphLayout {
  
  
  private final static double TWOPI = 2*Math.PI;
  
  
  private double padNodes = 12.0D;
  
  
  private boolean isSingleCircle = true;
  
  
  public boolean isSingleCircle() {
    return isSingleCircle;
  }
  
  
  public void setSingleCircle(boolean set) {
    isSingleCircle=set;
  }
  
  
  public double getPadding() {
    return padNodes;
  }
  
  
  public void setPadding(double set) {
    padNodes=set;
  }
  
  
  public Shape apply(Graph2D graph2d, LayoutContext context) throws LayoutException {
    
    
    if (graph2d.getVertices().size() < 2) 
      return LayoutHelper.getBounds(graph2d);
    
    
    CircularGraph cgraph = new CircularGraph(graph2d, isSingleCircle);
    
    
    Iterator<CircularGraph.Circle> it = cgraph.getCircles().iterator();
    double x=0,y=0;
    while (it.hasNext()) {
      
      
      CircularGraph.Circle circle = (CircularGraph.Circle)it.next();
      layout(graph2d, circle, x, y);
      
      
      x+=160;
    }
    
    
    
    
    LayoutHelper.setRoutings(graph2d);
    
    
    return LayoutHelper.getBounds(graph2d);
  } 
  
  
  private void layout(Graph2D graph2d, CircularGraph.Circle circle, double cx, double cy) {
    
    
    List<Vertex> nodes = new ArrayList<Vertex>(circle.getNodes());
    
    
    if (nodes.size()==1) {
      Vertex one = nodes.get(0);
      graph2d.setShape(one, ShapeHelper.createShape(graph2d.getShape(one), new Point2D.Double(cx,cy)));
      return;
    }
    
    
    double[] sizes = new double[nodes.size()];
    double circumference = 0;
    
    
    for (int n=0;n<nodes.size();n++) {
        
      
      Rectangle2D bounds = graph2d.getShape(nodes.get(n)).getBounds2D();
      double size = Geometry.getLength(bounds.getWidth()+padNodes, bounds.getHeight()+padNodes);
        
      
      sizes[n] = size;
        
      
      circumference += size;
    }
      
    
    double radius = circumference/TWOPI;
      
    
    double radian = 0;
    for (int n=0;n<nodes.size();n++) {
      double x = (int)(cx + Math.sin(radian)*radius);
      double y = (int)(cy + Math.cos(radian)*radius);
      Vertex node = nodes.get(n);
      Point2D pos = new Point2D.Double(x,y);
      graph2d.setShape(node, ShapeHelper.createShape(graph2d.getShape(node), pos));

      radian += TWOPI*sizes[n]/circumference;
    }
    
    radian=0;
    
  }

} 
