
package gj.util;

import gj.layout.Graph2D;
import gj.layout.Port;
import gj.layout.Routing;
import gj.model.Edge;
import gj.model.Graph;
import gj.model.Vertex;

import java.awt.Rectangle;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class DefaultGraph implements Graph2D {

  private Graph graph;
  private Shape defaultShape;
  private Map<Vertex, Shape> vertex2shape = new HashMap<Vertex, Shape>();
  private Map<Edge, Routing> edge2path = new HashMap<Edge, Routing>();
  
   DefaultGraph() {
    this(null);
  }
  
  public DefaultGraph(Graph graph) {
    this(graph, new Rectangle());
  }
  
  public DefaultGraph(Graph graph, Shape defaultShape) {
    this.graph = graph;
    this.defaultShape = defaultShape;
  }
  
  public Collection<? extends Edge> getEdges() {
    return graph==null ? new ArrayList<Edge>() : graph.getEdges();
  }
  
  public Collection<? extends Vertex> getVertices() {
    return graph==null ? new ArrayList<Vertex>() : graph.getVertices();
  }
  
  protected Shape getDefaultShape(Vertex vertex) {
    return defaultShape;
  }
  
  public Routing getRouting(Edge edge) {
    
    Routing result = edge2path.get(edge);
    if (result==null) {
      result = LayoutHelper.getRouting(edge, this);
      edge2path.put(edge, result);
    }
    return result;
  }

  public void setRouting(Edge edge, Routing path) {
    edge2path.put(edge, path);
  }

  public Shape getShape(Vertex vertex) {
    Shape result = vertex2shape.get(vertex);
    if (result==null)
      result = getDefaultShape(vertex);
    return result;
  }
  
  public void setShape(Vertex vertex, Shape shape) {
    vertex2shape.put(vertex, shape); 
  }

  
  public Port getPort(Edge edge, Vertex at) {
    return Port.None;
  }
  
} 
