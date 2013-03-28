
package gj.layout.graph.circular;

import gj.model.Graph;
import gj.model.Vertex;
import gj.util.LayoutHelper;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;


 class CircularGraph {

  
  private Set<Circle> circles;
  
  
  private Map<Vertex, Circle> vertex2circle;
  
  
   CircularGraph(Graph graph, boolean isSingleCircle) {
    
    
    if (graph.getVertices().isEmpty()) 
      return;
    
    
    circles = new HashSet<Circle>();
    vertex2circle = new HashMap<Vertex, Circle>(graph.getVertices().size());
    
    
    if (isSingleCircle) {
      new Circle(graph);
      return;
    }
    
    
    Set<? extends Vertex> unvisited = new HashSet<Vertex>(graph.getVertices());
    while (!unvisited.isEmpty()) 
      findCircles(graph, unvisited.iterator().next(), null, new Stack<Vertex>(), unvisited);

    
  }
  
  
  private void findCircles(Graph graph, Vertex vertex, Vertex parent, Stack<Vertex> path, Set<? extends Vertex> unvisited) {
    
    
    if (path.contains(vertex)) {
      Circle circle = getCircle(vertex);
      circle.fold(path, vertex);
      return;
    }
    
    
    unvisited.remove(vertex);
    
    
    new Circle(Collections.singleton(vertex));

    
    path.push(vertex);
    
    
    for (Vertex neighbour : LayoutHelper.getNeighbours(vertex)) {
      
      if (neighbour==vertex||neighbour==parent)
        continue;
      
      findCircles(graph, neighbour, vertex, path, unvisited);
    }
    
    
    path.pop();
    
    
  }
  
  
   Collection<Circle> getCircles() {
    return circles;
  }
  
  
   Circle getCircle(Vertex vertex) {
    Circle result = vertex2circle.get(vertex);
    if (result==null)
      result = new Circle(Collections.singleton(vertex));
    return result;
  }
  
  
   class Circle extends HashSet<Vertex> {

    
    Circle(Graph graph) {
      for (Vertex vertex : graph.getVertices())
        add(vertex);
      circles.add(this);
    }
    
    
    Circle(Collection<Vertex> nodes) {
      addAll(nodes);
      circles.add(this);
    }
    
    
    @Override
    public boolean add(Vertex vertex) {
      
      boolean rc = super.add(vertex);
      
      if (rc)
        vertex2circle.put(vertex, this);
      
      return rc;
    }
    
    
    void fold(Stack<Vertex> path, Vertex stop) {
      
      
      for (int i=path.size()-1;;i--) {
        
        Vertex vertex = path.get(i);
        
        if (vertex==stop) 
          break;
        
        Circle other = getCircle(vertex);
        addAll(other);
        circles.remove(other);
        
      }
      
      
    }
    
    
     Set<Vertex> getNodes() {
      return this;
    }
    
  } 
  
} 
