
package gj.shell.model;

import gj.geom.Geometry;
import gj.geom.ShapeHelper;
import gj.layout.Routing;
import gj.model.Edge;
import gj.util.LayoutHelper;

import java.awt.geom.Point2D;


public class EditableEdge implements Edge {
  
  
  private EditableVertex start;

  
  private EditableVertex end;
  
  private Routing path;
  
  private long hash = -1;
  
  
  EditableEdge(EditableVertex start, EditableVertex end) {
    this.start = start;
    this.end = end;
  }
  
  
  public boolean contains(Point2D point) {
    return 8>Geometry.getMinimumDistance(point, getPath().getPathIterator(null));
  }
  
  
  public Routing getPath() {
    
    if (!updateHash()) 
      setPath(makeShape());
    
    return path;
  }
  
  private Routing makeShape() {
    return LayoutHelper.getRouting(
        start.getShape(), ShapeHelper.getCenter(start.getShape()),
        end.getShape(), ShapeHelper.getCenter(end.getShape())
    );   
  }
  
  public void setPath(Routing set) {
    if (set==null)
      set = makeShape();
    path = set;
    updateHash();
  }
  
  boolean updateHash() {
    long oldHash = hash;
    hash = (int)(start.getShape().hashCode()+end.getShape().hashCode());
    return oldHash==hash;
  }

  
  @Override
  public String toString() {
    return start.toString() + ">" + end.toString();
  }
  
  
  public EditableVertex getStart() {
    return start;
  }

  
  public EditableVertex getEnd() {
    return end;
  }

  @Override
  public int hashCode() {
    return start.hashCode() + end.hashCode();
  }
  
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Edge))
      return false;
    Edge that = (Edge)obj;
    return (this.start.equals(that.getStart()) && this.end.equals(that.getEnd()));
  }
  
}
