
package gj.shell.model;

import gj.layout.Graph2D;
import gj.layout.Port;
import gj.layout.Routing;
import gj.model.Edge;
import gj.model.Vertex;
import gj.util.LayoutHelper;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


public class EditableGraph implements Graph2D {
  
  
  private Object selection;
  
  
  protected Set<EditableVertex> vertices = new LinkedHashSet<EditableVertex>(10);

  
  private Set<EditableEdge> edges = new LinkedHashSet<EditableEdge>(10);
  
  
  public EditableGraph() {
  }

  
  public EditableGraph(EditableGraph other) {
    this.vertices.addAll(other.vertices);
    this.edges.addAll(other.edges);
  }
  
  
  public EditableEdge addEdge(EditableVertex from, EditableVertex to) {
    if (from==to)
      throw new IllegalArgumentException("no edges between self allowed");
    EditableEdge edge = from.addEdge(to);
    edges.add(edge);
    return edge;
  }
  
  
  public int getNumEdges() {
    return edges.size();
  }
  public Collection<EditableEdge> getEdges() {
    return Collections.unmodifiableCollection(edges);
  }
  
  
  public int getNumEdges(Vertex vertex) {
    return ((EditableVertex)vertex).getEdges().size();
  }
  public Iterable<EditableEdge> getEdges(Vertex vertex) {
    return ((EditableVertex)vertex).getEdges();
  }
  
  
  public void removeEdge(EditableEdge edge) {
    if (!edges.remove(edge))
      throw new IllegalArgumentException("remove on non-graph edge");
    EditableVertex
     start = edge.getStart(),
     end   = edge.getEnd();
    start.removeEdge(edge);
    end  .removeEdge(edge);
    edges.remove(edge);
  }

  
  public EditableVertex addVertex(Shape shape, Object content) {
    EditableVertex node = new EditableVertex(shape, content);
    vertices.add(node);
    return node;
  }

  
  public void removeVertex(EditableVertex node) {
    
    for (EditableEdge edge : new ArrayList<EditableEdge>(node.getEdges()) )
      removeEdge(edge);
      
    vertices.remove(node);
  }
  
  
  public void setSelection(Object set) {
    selection = set;
  }
  
  
  public Object getSelection() {
    return selection;
  }
  
  
  public Object getElement(Point2D point) {
    
    
    Object result = getVertex(point);
    if (result!=null)
      return result;

    
    result = getEdge(point);
    if (result!=null)
      return result;
    
    
    return null;
    
  }
  
  
  public EditableEdge getEdge(Point2D point) {
    
    EditableEdge result = null;

    for (EditableEdge edge : edges) {
      
      if (edge.contains(point))
        result = edge;
      else
        if (result!=null) break;
    }
    
    
    return result;
  }
  
  
  
  public EditableVertex getVertex(Point2D point) {

    
    Iterator<?> it = vertices.iterator();
    while (it.hasNext()) {
      
      
      EditableVertex node = (EditableVertex)it.next();
      if (node.contains(point)) 
        return node;
    }
    
    
    return null;
  }
  
  public Collection<?> getVerticesOfEdge(Object edge) {
    List<EditableVertex> result = new ArrayList<EditableVertex>(2);
    result.add( ((EditableEdge)edge).getStart() );
    result.add( ((EditableEdge)edge).getEnd() );
    return result;
  }
  
  
  public int getDirectionOfEdge(Object from, Object to) {
    return ((EditableVertex)from).getEdge((EditableVertex)to).getStart()==from ? 1 : -1;
  }
  
  
  public int getNumVertices() {
    return vertices.size();
  }
  public Collection<EditableVertex> getVertices() {
    return Collections.unmodifiableCollection(vertices);
  }
  
  
  public int getNumAdjacentVertices(Vertex vertex) {
    return ((EditableVertex)vertex).getNumNeighbours();
  }
  
  
  public Set<Vertex> getNeighbours(Vertex vertex) {
    return LayoutHelper.getNeighbours(vertex);
  }
  
  
  public int getNumDirectPredecessors(Vertex vertex) {
    int result = 0;
    for (EditableEdge edge : ((EditableVertex)vertex).getEdges()) {
      if (edge.getEnd() == vertex) result++;
    }  
    return result;
  }
  
  
  public Iterable<EditableVertex> getDirectPredecessors(Vertex vertex) {

    List<EditableVertex> predecessors = new ArrayList<EditableVertex>();
    for (EditableEdge edge : ((EditableVertex)vertex).getEdges()) {
      if (edge.getEnd() == vertex)
        predecessors.add(edge.getStart());
    }  
    return predecessors;
  }
  
  
  public int getNumDirectSuccessors(Vertex vertex) {
    int result = 0;
    for (EditableEdge edge : ((EditableVertex)vertex).getEdges()) {
      if (edge.getStart() == vertex) result++;
    }  
    return result;
  }
  
  
  public Iterable<EditableVertex> getDirectSuccessors(EditableVertex vertex) {
    List<EditableVertex> successors = new ArrayList<EditableVertex>();
    for (EditableEdge edge : ((EditableVertex)vertex).getEdges()) {
      if (edge.getStart() == vertex)
        successors.add(edge.getEnd());
    }  
    return successors;
  }

  
  public Routing getRouting(Edge edge) {
    return ((EditableEdge)edge).getPath();
  }

  
  public Shape getShape(Vertex node) {
    return ((EditableVertex)node).getShape();
  }

  
  public void setRouting(Edge edge, Routing path) {
    ((EditableEdge)edge).setPath(path);
  }

  
  public void setShape(Vertex node, Shape shape) {
    ((EditableVertex)node).setShape(shape);
  }
  
  
  public Port getPort(Edge edge, Vertex at) {
    return Port.None;
  }
  
} 