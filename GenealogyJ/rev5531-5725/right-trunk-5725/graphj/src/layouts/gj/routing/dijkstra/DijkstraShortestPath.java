
package gj.routing.dijkstra;

import static gj.util.LayoutHelper.getNormalizedEdges;
import static gj.util.LayoutHelper.getOther;
import gj.layout.GraphNotSupportedException;
import gj.model.Edge;
import gj.model.Vertex;
import gj.model.WeightedGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class DijkstraShortestPath {
  
  
  public List<Vertex> getShortestPath(WeightedGraph graph, Vertex source, Vertex sink) throws GraphNotSupportedException {
    
    
    final Map<Vertex,Integer> vertex2distance = new HashMap<Vertex, Integer>();
    
    List<Vertex> considered = new ArrayList<Vertex>();
    
    Vertex cursor = source;
    setDistance(vertex2distance, source, 0);
    
    
    while (!cursor.equals(sink)) {
      
      
      int dist2here = vertex2distance.get(cursor);

      for (Edge edge : getNormalizedEdges(cursor)) {
        Vertex neighbour = getOther(edge, cursor);
        int dist2there = dist2here + getWeight(graph, edge);
        if (dist2there < getDistance(vertex2distance, neighbour)) {
          setDistance(vertex2distance, neighbour, dist2there);

          int pos=0;
          while (pos<considered.size() && dist2there<getDistance(vertex2distance, considered.get(pos))) pos++;
          considered.add(pos, neighbour);
        }
      }
      
      
      cursor = considered.remove(considered.size()-1);
      if (cursor==null)
        throw new GraphNotSupportedException("Graph is not spanning");
      
    }

    
    LinkedList<Vertex> result = new LinkedList<Vertex>();
    int distance = vertex2distance.get(sink);
    result.addFirst(sink);
    while (!cursor.equals(source)) {
      
      
      for (Edge edge : getNormalizedEdges(cursor)) {
        Vertex neighbour = getOther(edge, cursor);
        int dist2there = distance - getWeight(graph, edge);
        if (getDistance(vertex2distance, neighbour) == dist2there) {
          distance = dist2there;
          cursor = neighbour;
          result.addFirst(cursor);
          break;
        }
      }
      
      
    }
    
    
    return result;
  }
  
  
  private int getWeight(WeightedGraph graph, Edge edge) {
    return (int)Math.ceil(graph.getWeight(edge));
  }
  
  
  private void setDistance(Map<Vertex,Integer> vertex2distance, Vertex vertex, double distance) {
    vertex2distance.put(vertex, new Integer((int)Math.ceil(distance))); 
  }
  
  
  private int getDistance(Map<Vertex,Integer> vertex2distance, Vertex vertex) {
    Integer i = vertex2distance.get(vertex);
    return i==null ? Integer.MAX_VALUE : i.intValue();
  }
  

} 
