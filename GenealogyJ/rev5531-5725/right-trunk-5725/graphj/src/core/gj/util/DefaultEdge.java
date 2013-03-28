
package gj.util;

import gj.model.Edge;
import gj.model.Vertex;

import java.util.Collection;
import java.util.Iterator;


public class DefaultEdge<T> implements Edge {
  
  private Vertex from,to;
  
  public DefaultEdge(DefaultVertex<T> from, DefaultVertex<T> to) {
    this.from = from;
    this.to = to;
    from.addEdge(this);
    if (!from.equals(to))
      to.addEdge(this);
  }
  
  public DefaultEdge(Collection<Vertex> vertices) {
    if (vertices.size()!=2) 
      throw new IllegalArgumentException("Edge requires exactly two vertices");
    Iterator<Vertex> it = vertices.iterator();
    this.from = it.next();
    this.to = it.next();
  }

  public Vertex getEnd() {
    return to;
  }

  public Vertex getStart() {
    return from;
  }

  @Override
  public int hashCode() {
    return from.hashCode() + to.hashCode();
  }
  
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Edge))
      return false;
    Edge that = (Edge)obj;
    return (this.from.equals(that.getStart()) && this.to.equals(that.getEnd()));
  }
  
  @Override
  public String toString() {
    return from+">"+to;
  }
  
}
