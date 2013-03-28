
package gj.util;

import gj.layout.Graph2D;
import gj.layout.Port;
import gj.layout.Routing;
import gj.model.Edge;
import gj.model.Vertex;

import java.awt.Shape;
import java.util.Collection;


public class DelegatingGraph implements Graph2D {
  
  private Graph2D delegated;
  
  
  public DelegatingGraph(Graph2D delegated) {
    this.delegated = delegated;
  }

  
  public Routing getRouting(Edge edge) {
    return delegated.getRouting(edge);
  }

  
  public Shape getShape(Vertex vertex) {
    return delegated.getShape(vertex);
  }

  
  public void setRouting(Edge edge, Routing shape) {
    delegated.setRouting(edge, shape);
  }

  
  public void setShape(Vertex vertex, Shape shape) {
    delegated.setShape(vertex, shape);
  }

  
  public Collection<? extends Edge> getEdges() {
    return delegated.getEdges();
  }

  
  public Collection<? extends Vertex> getVertices() {
    return delegated.getVertices();
  }
  
  
  public Port getPort(Edge edge, Vertex at) {
    return delegated.getPort(edge, at);
  }
  
} 
