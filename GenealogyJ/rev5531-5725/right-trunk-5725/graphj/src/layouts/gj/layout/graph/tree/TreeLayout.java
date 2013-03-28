
package gj.layout.graph.tree;

import static gj.util.LayoutHelper.getInDegree;
import gj.layout.Graph2D;
import gj.layout.GraphNotSupportedException;
import gj.layout.LayoutContext;
import gj.layout.LayoutException;
import gj.model.Vertex;
import gj.util.AbstractGraphLayout;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


public class TreeLayout extends AbstractGraphLayout<Vertex> {

  
  private int distanceInGeneration = 20;

  
  private int distanceBetweenGenerations = 20;

  
  private Alignment alignmentOfParents = Alignment.Center;

  
  private boolean isBalanceChildren = false;

  
  private EdgeLayout edgeLayout = EdgeLayout.Polyline;

  
  private double orientation = 180;

  
  private boolean isOrderSiblingsByPosition = true;
  
  
  private boolean isSingleSourceDAG = true;

  
  public int getDistanceInGeneration() {
    return distanceInGeneration;
  }

  
  public void setDistanceInGeneration(int set) {
    distanceInGeneration = set;
  }

  
  public int getDistanceBetweenGenerations() {
    return distanceBetweenGenerations;
  }

  
  public void setDistanceBetweenGenerations(int set) {
    distanceBetweenGenerations=set;
  }

  
  public Alignment getAlignmentOfParents() {
    return alignmentOfParents;
  }

  
  public void setAlignmentOfParents(Alignment set) {
    alignmentOfParents = set;
  }

  
  public boolean getBalanceChildren() {
    return isBalanceChildren;
  }

  
  public void setBalanceChildren(boolean set) {
    isBalanceChildren=set;
  }

  
  public EdgeLayout getEdgeLayout() {
    return edgeLayout;
  }

  
  public void setEdgeLayout(EdgeLayout set) {
    edgeLayout = set;
  }
  
  
  public void setOrientation(double orientation) {
    this.orientation = orientation;
  }
  
  
  public double getOrientation() {
    return orientation;
  }
  
  
  public Vertex getRoot(Graph2D graph2d) {
    
    Vertex result = getAttribute(graph2d);
    if (result==null) {
      
      for (Vertex v : graph2d.getVertices()) {
        if (getInDegree(v)==0) {
          result = v;
          setRoot(graph2d, result);
          break;
        }
      }
    }
    
    return result;
  }

  
  public void setRoot(Graph2D graph2d, Vertex root) {
    setAttribute(graph2d, root);
  }

  
  public void setOrderSiblingsByPosition(boolean isOrderSiblingsByPosition) {
    this.isOrderSiblingsByPosition = isOrderSiblingsByPosition;
  }

  
  public boolean isOrderSiblingsByPosition() {
    return isOrderSiblingsByPosition;
  }

  
  public boolean isSingleSourceDAG() {
    return isSingleSourceDAG;
  }

  
  public void setSingleSourceDAG(boolean setSingleSourceDAG) {
    this.isSingleSourceDAG = setSingleSourceDAG;
  }

  
  public Shape apply(Graph2D graph2d, LayoutContext context) throws LayoutException {
    
    
    Collection<? extends Vertex> vertices = graph2d.getVertices(); 
    if (vertices.isEmpty())
      return new Rectangle2D.Double();
    
    
    Vertex root = getRoot(graph2d);
    if (root==null)
      throw new GraphNotSupportedException("Graph is not a tree (no vertex with in-degree of zero)");
    
    context.getLogger().fine("root is ["+root+"]");
    
    
    Set<Vertex> visited = new HashSet<Vertex>();
    Branch branch = new Branch(null, root, graph2d, new ArrayDeque<Vertex>(), visited, this, context);
    
    if (context.isDebug())
      context.addDebugShape(branch.getShape());
    
    
    if (isSingleSourceDAG&&visited.size()!=vertices.size()) {
      context.getLogger().fine("not a spanning tree (#visited="+visited.size()+" #vertices="+vertices.size());
      throw new GraphNotSupportedException("Graph is not a spanning tree ("+vertices.size()+"!="+visited.size()+")");
    }
    
    
    edgeLayout.apply(graph2d, branch, this, context);
    
    
    return branch.getShape();
  }
  
} 
