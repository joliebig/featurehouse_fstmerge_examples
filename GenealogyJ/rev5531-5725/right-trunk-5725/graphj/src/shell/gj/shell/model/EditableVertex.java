
package gj.shell.model;

import gj.geom.ShapeHelper;
import gj.model.Vertex;
import gj.util.LayoutHelper;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;


public class EditableVertex implements Vertex {
  
  
  private Object content;
  
  
  private Collection<EditableEdge> edges = new LinkedHashSet<EditableEdge>(3);
  
  
  private Shape originalShape, editedShape;
  
  
  public Shape getShape() {
    return editedShape;
  }
  
  
  public Shape getOriginalShape() {
    return originalShape;
  }
  
  
  public void setOriginalShape(Shape shape) {
    originalShape = shape;
    setShape(shape);
  }
  
  
  public void setShape(Shape set) {
    
    editedShape = set;
    
    Point2D to = ShapeHelper.getCenter(set);
    
    originalShape = ShapeHelper.createShape(originalShape, to);
  }
  
    
  EditableVertex(Shape shape, Object content) {
    
    if (shape==null) shape = new Rectangle();
    
    this.content = content;
    this.originalShape = shape;
    setShape(shape);
  }
  
  
  public int getNumNeighbours() {
    return getNeighbours().size();
  }
  
  
  public Set<Vertex> getNeighbours() {
    return LayoutHelper.getNeighbours(this);
  }

  
  public boolean isNeighbour(EditableVertex v) {
    return getNeighbours().contains(v);
  }
  
  
   EditableEdge addEdge(EditableVertex that) {

    
    if (getNeighbours().contains(that))
      throw new IllegalArgumentException("already exists edge between "+this+" and "+that);
    if (this.equals(that))
      throw new IllegalArgumentException("can't have edge between self ("+this+")");

    
    EditableEdge edge = new EditableEdge(this, that);
    this.edges.add(edge);
    if (that!=this) 
      that.edges.add(edge);
    
    
    return edge;
  }
  
  
  public EditableEdge getEdge(EditableVertex to) {
    for (EditableEdge edge : edges) {
      if (edge.getStart()==this&&edge.getEnd()==to||edge.getStart()==to&&edge.getEnd()==this)
        return edge;
    }
    throw new IllegalArgumentException("no edge between "+this+" and "+to);
  }
  
  
  public Collection<EditableEdge> getEdges() {
    return edges;
  }
  
  
   void removeEdge(EditableEdge edge) {
    edges.remove(edge);
  }
  
  
  public boolean contains(Point2D point) {
    return getShape().contains(point.getX(),point.getY());   
  }
  
  
  public Object getContent() {
    return content;
  }

  
  public void setContent(Object set) {
    content = set;
  }

  
  @Override
  public String toString() {
    if (content==null) {
      return super.toString();
    } else {
      return content.toString();
    }
  }

} 
