
package gj.layout.graph.random;

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
import java.util.Random;


public class RandomLayout implements GraphLayout {
  
  
  private long seed = 0;
  
  
  private boolean isApplyHorizontally = true;

  
  private boolean isApplyVertically = true;

  
  public long getSeed() {
    return seed;
  }
  
  
  public void setSeed(long set) {
    seed=set;
  }
  
  
  public boolean isApplyHorizontally() {
    return isApplyHorizontally;
  }

  
  public void setApplyHorizontally(boolean set) {
    isApplyHorizontally=set;
  }

  
  public boolean isApplyVertically() {
    return isApplyVertically;
  }

  
  public void setApplyVertically(boolean set) {
    isApplyVertically=set;
  }

  
  public Shape apply(Graph2D graph2d, LayoutContext context) throws LayoutException {
    
    
    if (graph2d.getVertices().isEmpty())
      return new Rectangle2D.Double();
    
    
    Random random = new Random(seed++);

    
    for (Vertex vertex : graph2d.getVertices()) {
      
      Rectangle2D nodeCanvas = graph2d.getShape(vertex).getBounds2D();
      Rectangle2D preferred = context.getPreferredBounds();
      if (preferred==null)
        throw new IllegalArgumentException("LayoutContext.getPreferredBounds() cannot be null");

      double 
        x = preferred.getMinX(),
        y = preferred.getMinY(),
        w = preferred.getWidth() - nodeCanvas.getWidth(),
        h = preferred.getHeight() - nodeCanvas.getHeight();
      
      Point2D pos = new Point2D.Double(
        isApplyHorizontally ? (x + random.nextDouble()*w) : 0, 
        isApplyVertically ? (y + random.nextDouble()*h) : 0
      );
      graph2d.setShape(vertex, ShapeHelper.createShape(graph2d.getShape(vertex), pos));

    }
    
    
    LayoutHelper.setRoutings(graph2d);
    
    
    return context.getPreferredBounds();
  }

}
