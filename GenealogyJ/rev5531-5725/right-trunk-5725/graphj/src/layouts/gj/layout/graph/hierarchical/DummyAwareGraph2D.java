
package gj.layout.graph.hierarchical;

import gj.layout.Graph2D;
import gj.layout.Port;
import gj.layout.Routing;
import gj.model.Edge;
import gj.model.Vertex;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class DummyAwareGraph2D implements Graph2D {
  
  private Map<Vertex, Shape> dummy2shape = new HashMap<Vertex, Shape>();
  private Graph2D wrapped;
  
  public DummyAwareGraph2D(Graph2D wrapped) {
    this.wrapped = wrapped;
  }
  
  public Collection<? extends Vertex> getVertices() {
    return wrapped.getVertices();
  }
  
  public Collection<? extends Edge> getEdges() {
    return wrapped.getEdges();
  }
  
  public Shape getShape(Vertex vertex) {
    if (!(vertex instanceof LayerAssignment.DummyVertex))
      return wrapped.getShape(vertex);
    Shape result = dummy2shape.get(vertex);
    return result!=null ? result : new Rectangle2D.Double();
  }
  
  public Routing getRouting(Edge edge) {
    return wrapped.getRouting(edge);
  }

  public void setRouting(Edge edge, Routing shape) {
    wrapped.setRouting(edge, shape);
  }

  public void setShape(Vertex vertex, Shape shape) {
    if (!(vertex instanceof LayerAssignment.DummyVertex))
      wrapped.setShape(vertex, shape);
    else
      dummy2shape.put(vertex, shape);
  }
  
  public Port getPort(Edge edge, Vertex at) {
    if (  (edge.getStart() instanceof LayerAssignment.DummyVertex)
        ||(edge.getEnd  () instanceof LayerAssignment.DummyVertex))
      return Port.None;
    return wrapped.getPort(edge, at);
  }

} 
