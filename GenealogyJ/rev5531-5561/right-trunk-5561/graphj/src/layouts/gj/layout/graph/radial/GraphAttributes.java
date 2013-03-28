
package gj.layout.graph.radial;

import gj.model.Edge;
import gj.model.Vertex;

import java.util.HashMap;
import java.util.Map;


 class GraphAttributes {

  private Map<Edge, Integer> edge2length = new HashMap<Edge, Integer>();
  private Vertex root;
  
   int getLength(Edge edge) {
    Integer result = edge2length.get(edge);
    if (result!=null)
      return result.intValue();
    return 1;
  }
  
   Vertex getRoot() {
    return root;
  }
  
   void setRoot(Vertex vertex) {
    root = vertex;
  }

   void setLength(Edge edge, int length) {
    if (length < 1)
      edge2length.remove(edge);
    else
      edge2length.put(edge, new Integer(length));
  }
}
