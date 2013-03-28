
package gj.layout.graph.tree;

import static gj.geom.Geometry.HALF_RADIAN;
import static gj.geom.Geometry.QUARTER_RADIAN;
import static gj.geom.Geometry.getConvexHull;
import static gj.geom.Geometry.getDelta;
import static gj.geom.Geometry.getDistance;
import static gj.geom.Geometry.getMax;
import static gj.geom.Geometry.getMid;
import static gj.geom.Geometry.getPoint;
import static gj.geom.Geometry.getRadian;
import static gj.geom.Geometry.getTranslated;
import static gj.geom.ShapeHelper.createShape;
import static gj.geom.ShapeHelper.getCenter;
import static gj.util.LayoutHelper.getChildren;
import static gj.util.LayoutHelper.getNeighbours;
import static gj.util.LayoutHelper.translate;
import gj.geom.ConvexHull;
import gj.geom.Geometry;
import gj.layout.Graph2D;
import gj.layout.GraphNotSupportedException;
import gj.layout.LayoutContext;
import gj.layout.LayoutException;
import gj.model.Vertex;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.Set;


 class Branch implements Comparator<Vertex> {
  
  
  private Graph2D graph2d;
  
  
  private Vertex root;
  
  
  private List<Branch> branches;
  
  
  private Point2D top;
  private ConvexHull shape;
  private double orientation;
  
  
   Branch(Vertex backtrack, Vertex parent, Graph2D graph2d, Deque<Vertex> stack, Set<Vertex> visited, TreeLayout layout, LayoutContext context) throws LayoutException, GraphNotSupportedException{
    
    
    visited.add(parent);

    
    this.graph2d = graph2d;
    this.root = parent;
    this.orientation = layout.getOrientation();
    
    
    List<Vertex> children = children(backtrack, parent, layout);
    if (layout.isOrderSiblingsByPosition()) 
      Collections.sort(children, this);
    
    
    stack.addLast(parent);
    branches = new ArrayList<Branch>(children.size());
    for (Vertex child : children) {
      
      
      if (visited.contains(child)) {
        
        
        if (stack.contains(child)) {
          context.getLogger().info("cannot handle directed cycle at all");
          throw new GraphNotSupportedException("Graph contains cycle involving ["+parent+">"+child+"]");
        }

        
        if (!layout.isSingleSourceDAG()) {
          context.getLogger().info("cannot handle undirected graph with cycle unless isConsiderDirection=true");
          throw new GraphNotSupportedException("Non Digraph contains non-directed cycle involving ["+parent+">"+child+"]");
        }
        
        
        continue;
      }
      
      
      Branch branch = new Branch(parent, child, graph2d, stack, visited, layout, context);
      branches.add(branch);
    }
    stack.removeLast();
    
    
    if (branches.isEmpty()) {
      
      shape = getConvexHull(graph2d.getShape(parent));
      
      return;
    }
    
    
    double layoutAxis = getRadian(layout.getOrientation());
    double lrAlignment = layoutAxis - QUARTER_RADIAN;
    Point2D[] lrDeltas  = new Point2D[branches.size()];
    lrDeltas[0] = new Point2D.Double();
    for (int i=1;i<branches.size();i++) {
      
      lrDeltas[i] = getDelta(branches.get(i).top(), branches.get(0).top());
      
      double distance = Double.MAX_VALUE;
      for (int j=0;j<i;j++) {
        distance = Math.min(distance, getDistance(getTranslated(branches.get(j).shape, lrDeltas[j]), getTranslated(branches.get(i).shape, lrDeltas[i]), lrAlignment) - layout.getDistanceInGeneration());
      }
      
      lrDeltas[i] = getPoint(lrDeltas[i], lrAlignment, -distance);
    }
    
    
    branches.get(branches.size()-1).moveBy(lrDeltas[lrDeltas.length-1]);

    
    Point2D[] rlDeltas  = lrDeltas;
    if (branches.size()>2 && layout.getBalanceChildren()) {
      rlDeltas = new Point2D[branches.size()];
      double rlAlignment = layoutAxis + QUARTER_RADIAN;
      rlDeltas [rlDeltas.length-1] = new Point2D.Double();
      for (int i=rlDeltas.length-2;i>=0;i--) {
        
        rlDeltas[i] = getDelta(branches.get(i).top(), branches.get(branches.size()-1).top());
        
        double distance = Double.MAX_VALUE;
        for (int j=rlDeltas.length-1;j>i;j--) {
          distance = Math.min(distance, getDistance(getTranslated(branches.get(j).shape, rlDeltas[j]), getTranslated(branches.get(i).shape, rlDeltas[i]), rlAlignment) - layout.getDistanceInGeneration());
        }
        assert distance != Double.MAX_VALUE;
        
        rlDeltas[i] = getPoint(rlDeltas[i], rlAlignment, -distance);
      }
    }
    
    
    for (int i=1; i<branches.size()-1; i++) {
      branches.get(i).moveBy(getMid(lrDeltas[i], rlDeltas[i]));
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    Point2D a = getPoint(branches.get(0).top(), layoutAxis-HALF_RADIAN, layout.getDistanceBetweenGenerations());
    Point2D b = getMax(graph2d.getShape(branches.get(0).root), layoutAxis+QUARTER_RADIAN); 
    Point2D c = getMax(graph2d.getShape(branches.get(branches.size()-1).root), layoutAxis-QUARTER_RADIAN);
    
    switch (layout.getAlignmentOfParents()) {
      default: case Center:
        graph2d.setShape(parent, createShape(graph2d.getShape(parent), getPoint(b, c, 0.5)));
        break;
      case Left:
        graph2d.setShape(parent, createShape(graph2d.getShape(parent), b, layoutAxis, -1));
        break;
      case Right:
        graph2d.setShape(parent, createShape(graph2d.getShape(parent), c, layoutAxis, +1));
        break;
      case LeftOffset:
        b = getPoint(b, layoutAxis+QUARTER_RADIAN, layout.getDistanceInGeneration());
        graph2d.setShape(parent, createShape(graph2d.getShape(parent), b, layoutAxis, +1));
        break;
      case RightOffset:
        c = getPoint(c, layoutAxis-QUARTER_RADIAN, layout.getDistanceInGeneration());
        graph2d.setShape(parent, createShape(graph2d.getShape(parent), c, layoutAxis, -1));
        break;
    }
    
    graph2d.setShape(
      parent,
      createShape(graph2d.getShape(parent), a, QUARTER_RADIAN, -1)
    );
    
    
    GeneralPath gp = new GeneralPath();
    gp.append(graph2d.getShape(parent), false);
    for (Branch branch : branches)
      gp.append(branch.shape, false);
    
    
    Point2D b1,b2;
    switch (layout.getAlignmentOfParents()) {
      case LeftOffset:
        b1 = Geometry.getIntersection(c, layoutAxis, getCenter(graph2d.getShape(parent)), layoutAxis-QUARTER_RADIAN);
        b2 = b1;
        break;
      case RightOffset:
        b1 = Geometry.getIntersection(b, layoutAxis, getCenter(graph2d.getShape(parent)), layoutAxis-QUARTER_RADIAN);
        b2 = b1;
        break;
      default:
        b1 = Geometry.getIntersection(b, layoutAxis, a, layoutAxis-QUARTER_RADIAN);
        b2 = Geometry.getIntersection(c, layoutAxis, a, layoutAxis-QUARTER_RADIAN);
        break;
    }
    gp.lineTo(b2.getX(), b2.getY());
    gp.lineTo(b1.getX(), b1.getY());
    
    
    
    shape = getConvexHull(gp);
   
    
  }
  
   Shape getShape() {
    return shape;
  }
  
   Vertex getRoot() {
    return root;
  }
  
   List<Branch> getBranches() {
    return branches;
  }
  
  
  private List<Vertex> children(Vertex backtrack, Vertex parent, TreeLayout layout) throws GraphNotSupportedException {
    
    List<Vertex> result = new ArrayList<Vertex>(10);
    
    
    if (layout.isSingleSourceDAG()) {
      result.addAll(getChildren(parent));
      if (backtrack!=null && result.contains(backtrack))
        throw new GraphNotSupportedException("Graph contains backtracking edge ["+parent+">"+backtrack+"]");
    } else {
      result.addAll(getNeighbours(parent));
      result.remove(backtrack);
    }
    
    
    return result;      
  }
  
  private Point2D top() throws LayoutException{
    if (top==null)
      top= getMax(shape, getRadian(orientation) - HALF_RADIAN);
    if (top==null)
      throw new LayoutException("branch for vertex "+root+" has no valid shape containing (0,0)");
    return top;
    
  }
  
  
  private void moveBy(Point2D delta) {
    
    translate(graph2d, root, delta);
    
    for (Branch branch : branches) 
      branch.moveBy(delta);
    
    top = null;
    
    shape.transform(AffineTransform.getTranslateInstance(delta.getX(), delta.getY()));
  }
  
  
  public int compare(Vertex v1,Vertex v2) {
    
    double layoutAxis = getRadian(orientation);
    Point2D p1 = getCenter(graph2d.getShape(v1));
    Point2D p2 = getCenter(graph2d.getShape(v2));
    
    double delta =
      Math.cos(layoutAxis) * (p2.getX()-p1.getX()) + Math.sin(layoutAxis) * (p2.getY()-p1.getY());
    
    return (int)(delta);
  }
  
} 

