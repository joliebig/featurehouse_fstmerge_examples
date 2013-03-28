
package gj.util;

import gj.model.Vertex;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;



public class DefaultVertex<T> implements Vertex {
  
  private T content;
  private Set<DefaultEdge<T>> edges = new HashSet<DefaultEdge<T>>();
  
  public DefaultVertex(T content) {
    this.content = content;
  }
  
  public T getContent() {
    return content;
  }
  
   void addEdge(DefaultEdge<T> edge) {
    edges.add(edge);
  }
  
  public Collection<DefaultEdge<T>> getEdges() {
    return Collections.unmodifiableCollection(edges);
  }
  
  @Override
  public int hashCode() {
    return content.hashCode();
  }
  
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof DefaultVertex))
      return false;
    DefaultVertex<?> that = (DefaultVertex<?>)obj;
    return this.content.equals(that.content);
  }
  
  @Override
  public String toString() {
    return content.toString();
  }

  public static Set<Vertex> wrap(Object[] vertices) {
    Set<Vertex> result = new LinkedHashSet<Vertex>();
    for (Object vertex : vertices)
      result.add(new DefaultVertex<Object>(vertex));
    return result;
  }
  
  public static Set<Vertex> wrap(Collection<Object> vertices) {
    Set<Vertex> result = new LinkedHashSet<Vertex>();
    for (Object vertex : vertices)
      result.add(new DefaultVertex<Object>(vertex));
    return result;
  }
  
}
