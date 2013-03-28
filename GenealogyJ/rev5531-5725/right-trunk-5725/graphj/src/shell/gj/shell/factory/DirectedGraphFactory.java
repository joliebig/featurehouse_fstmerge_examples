
package gj.shell.factory;

import gj.geom.ShapeHelper;
import gj.shell.model.EditableEdge;
import gj.shell.model.EditableGraph;
import gj.shell.model.EditableVertex;
import gj.util.LayoutHelper;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class DirectedGraphFactory extends AbstractGraphFactory {
  
  
  private boolean isConnected = true;
  
  
  

  
  

  
  private int numNodes = 10;
  
  
  private int minArcs = 2;
  
  
  private int minDegree = 1;
  
  
  public int getNumNodes() {
    return numNodes;
  }
  
  
  public void setNumNodes(int set) {
    numNodes=set;
  }
  
  
  public int getMinArcs() {
    return minArcs;
  }
  
  
  public void setMinArcs(int set) {
    minArcs=set;
  }
  
  
  public int getMinDegree() {
    return minDegree;
  }
  
  
  public void setMinDegree(int set) {
    minDegree=set;
  }
  
  
  public boolean getConnected() {
    return isConnected;
  }
  
  
  public void setConnected(boolean set) {
    isConnected=set;
  }
  
  
  @Override
  public EditableGraph create(Rectangle2D bounds) {
    
    
    EditableGraph graph = new EditableGraph();
    
    
    createNodes(graph, bounds);
    
    
    createArcs(graph);
    
    
    return graph;
  }
  
  
  private void createNodes(EditableGraph graph, Rectangle2D canvas) {
    
    
    for (int n=0;n<numNodes;n++) {
      Point2D pos = getRandomPosition(canvas, nodeShape);
      graph.addVertex(ShapeHelper.createShape(nodeShape, pos), ""+(n+1));
    }
    
    
  }

  
  private void createArcs(EditableGraph graph) {
  
    List<EditableVertex> nodes = new ArrayList<EditableVertex>(graph.getNumVertices());
    for (EditableVertex vertex : graph.getVertices())
      nodes.add(vertex);
    
    
    if (nodes.isEmpty())
      return;

    
    for (int i=0;i<minArcs;i++) {
      
      EditableVertex from = super.getRandomNode(nodes, false);
      EditableVertex to   = super.getRandomNode(nodes, false);
      
      if (to.equals(from)) 
        continue;
      
      EditableEdge edge = graph.addEdge(from, to);
    }
    
    
    if (isConnected) 
      ensureConnected(graph);
    
    
    if (minDegree>0) 
      ensureMinDegree(graph);
    
    
  }
  
  
  private void ensureMinDegree(EditableGraph graph) {
    
    List<EditableVertex> nodes = new ArrayList<EditableVertex>(graph.getNumVertices());
    for (EditableVertex vertex : graph.getVertices())
      nodes.add(vertex);
    
    
    
    
    minDegree = Math.min(minDegree, nodes.size()-1);
    
    
    while (true) {
      
      
      EditableVertex vertex = getMinDegNode(graph, nodes, false);
      if (graph.getNumAdjacentVertices(vertex) >= minDegree) 
        break;
      
      
      List<EditableVertex> others = new LinkedList<EditableVertex>(nodes);
      others.removeAll(vertex.getNeighbours());
      
      
      while (true) {
        EditableVertex other = getRandomNode(others,true);
        if (!vertex.equals(other)&&graph.getNumAdjacentVertices(other) < minDegree || others.isEmpty()) {
          graph.addEdge(vertex, other);
          break;
        }
      }
      
      
    }
    
    
  }
  

  
  protected void ensureConnected(EditableGraph graph) {
    
    List<EditableVertex> nodes = new ArrayList<EditableVertex>(graph.getNumVertices());
    for (EditableVertex vertex : graph.getVertices())
      nodes.add(vertex);
    
    while (nodes.size()>1) {
      EditableVertex from = getMinDegNode(graph,nodes,true);
      if (!LayoutHelper.isNeighbour(graph,from,nodes)) {
        EditableVertex to = getMinDegNode(graph, nodes,false);
        graph.addEdge(from, to);
      }
    }
    
    
  }
  
} 
