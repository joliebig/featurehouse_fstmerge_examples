
package gj.layout.graph.hierarchical;

import gj.layout.Graph2D;
import gj.layout.GraphNotSupportedException;
import gj.model.Edge;
import gj.model.Vertex;

import java.util.Collection;
import java.util.Comparator;


public interface LayerAssignment {

  
  public void assignLayers(Graph2D graph, Comparator<Vertex> orderOfVerticesInLayer) throws GraphNotSupportedException;
  
  
  public int getNumDummyVertices();

  
  public int getHeight();

  
  public int getWidth();
  
  
  public int getWidth(int layer);
  
  
  public Vertex getVertex(int layer, int u);

  
  public void swapVertices(int layer, int u, int v);

  
  public Routing getRouting(Edge edge);

  
  public int[] getIncomingIndices(int layer, int u);
  
  
  public int[] getOutgoingIndices(int layer, int u);

  
  public class Routing {
    public int outIndex;
    public int outDegree;
    public int len;
    public int[] layers;
    public int[] positions;
    public int inIndex;
    public int inDegree;
  }

  
  public class DummyVertex implements Vertex {
    @Override
    public String toString() {
      return "Dummy";
    }
    public Collection<? extends Edge> getEdges() {
      throw new IllegalArgumentException("n/a");
    }
  };
}
