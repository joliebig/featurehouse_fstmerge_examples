
package gj.shell.factory;

import gj.geom.ShapeHelper;
import gj.shell.model.EditableGraph;
import gj.shell.model.EditableVertex;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;


public class TreeFactory extends AbstractGraphFactory {
  
  
  private int maxDepth = 5;

  
  private int maxChildren = 4;

  
  private int numNodes = 4;

  
  public int getMaxDepth() {
    return maxDepth;
  }

  
  public void setMaxDepth(int set) {
    maxDepth=set;
  }

  
  public int getMaxChildren() {
    return maxChildren;
  }

  
  public void setMaxChildren(int set) {
    maxChildren=set;
  }

  
  public int getNumNodes() {
    return numNodes;
  }

  
  public void setNumNodes(int set) {
    numNodes=set;
  }

  
  private static final String[][] sample = {
      { "1.1", "1.2" },
      { "1.2", "1.3", "1.2.1.1", "1.2.2.1", "1.2.3.1" },
      { "1.3", "1.4" },
      { "1.4", "1.5", "1.4.1.1" },
      { "1.5", "1.6" },
      { "1.6" },
      
      { "1.2.1.1", "1.2.1.2" },
      { "1.2.1.2" },
      
      { "1.2.2.1", "1.2.2.2" },
      { "1.2.2.2", "1.2.2.3" },
      { "1.2.2.3", "1.2.2.4" },
      { "1.2.2.4" },

      { "1.2.3.1", "1.2.3.2" },
      { "1.2.3.2" },
      
      { "1.4.1.1", "1.4.1.2", "1.4.1.2.1.1"},
      { "1.4.1.2" },
      { "1.4.1.2.1.1",  "1.4.1.2.1.2"},
      { "1.4.1.2.1.2" }
    };
    
  
  @Override
  public EditableGraph create(Rectangle2D bounds) {
    
    
    EditableGraph graph = new EditableGraph();
    
    
    Map<String,EditableVertex> nodes = new HashMap<String,EditableVertex>(sample.length);
    for (int s = 0; s < sample.length; s++) {

      String key = sample[s][0];
      Point2D pos = getRandomPosition(bounds, nodeShape);
      Shape shape = ShapeHelper.createShape(nodeShape, pos);
      EditableVertex vertex = graph.addVertex(shape, key);
      nodes.put(key, vertex);
    }
     
    for (int s = 0; s < sample.length; s++) {
      EditableVertex from = nodes.get(sample[s][0]);
      for (int c = 1; c < sample[s].length; c++) {
        String key = sample[s][c];        
        EditableVertex to = nodes.get(key);
        graph.addEdge(from, to);
      }
    }
    
    
    return graph;
  }

} 
