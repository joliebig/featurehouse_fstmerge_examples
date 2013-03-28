
package gj.layout;


import gj.model.Edge;
import gj.model.Graph;
import gj.model.Vertex;

import java.awt.Shape;


public interface Graph2D extends Graph {

  
  public Routing getRouting(Edge edge);

  
  public void setRouting(Edge edge, Routing routing);

  
  public Shape getShape(Vertex vertex);

  
  public void setShape(Vertex vertex, Shape shape);

  
  public Port getPort(Edge edge, Vertex at);
  
} 
