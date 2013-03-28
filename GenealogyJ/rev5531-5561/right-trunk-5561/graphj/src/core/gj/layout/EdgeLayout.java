
package gj.layout;

import gj.model.Edge;


public interface EdgeLayout {

  
  public void apply(Edge edge, Graph2D graph2d, LayoutContext context) throws LayoutException;
  
} 
