
package gj.layout.graph.radial;

import static gj.util.LayoutHelper.assertSpanningTree;
import static gj.util.LayoutHelper.getDiameter;
import static gj.util.LayoutHelper.getNormalizedEdges;
import static gj.util.LayoutHelper.getOther;
import static gj.util.LayoutHelper.setRouting;
import static java.lang.Math.max;
import gj.geom.Geometry;
import gj.geom.ShapeHelper;
import gj.layout.Graph2D;
import gj.layout.LayoutContext;
import gj.layout.LayoutException;
import gj.model.Edge;
import gj.model.Graph;
import gj.model.Vertex;
import gj.util.AbstractGraphLayout;
import gj.util.DefaultRouting;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RadialLayout extends AbstractGraphLayout<GraphAttributes> {

  private double distanceBetweenGenerations = 60;
  private boolean isAdjustDistances = true;
  private boolean isFanOut = false; 
  private double distanceInGeneration = 0;
  private boolean isOrderSiblingsByPosition = true;
  private boolean isRotateShapes = true;
  private boolean isBendArcs = true;

  @Override
  protected GraphAttributes getAttribute(Object graph) {
    GraphAttributes attrs = super.getAttribute(graph);
    if (attrs==null) {
      attrs = new GraphAttributes();
      super.setAttribute(graph, attrs);
    }
    return attrs;
  }
  
  
  public Vertex getRoot(Graph graph) {
    return getAttribute(graph).getRoot();
  }

  
  public void setRoot(Graph graph, Vertex root) {
    getAttribute(graph).setRoot(root);
  }
  
  
  public void setLength(Graph graph, Edge edge, int length) {
    getAttribute(graph).setLength(edge, length);
  }
  
  
  public int getLength(Graph graph, Edge edge) {
    return getAttribute(graph).getLength(edge);
  }
  
  
  public void setDistanceBetweenGenerations(double distanceBetweenGenerations) {
    this.distanceBetweenGenerations = max(1, distanceBetweenGenerations);
  }

  
  public double getDistanceBetweenGenerations() {
    return distanceBetweenGenerations;
  }

  
  public void setDistanceInGeneration(double distanceInGeneration) {
    this.distanceInGeneration = distanceInGeneration;
  }

  
  public double getDistanceInGeneration() {
    return distanceInGeneration;
  }

  
  public boolean isAdjustDistances() {
    return isAdjustDistances;
  }

  
  public void setAdjustDistances(boolean isAdjustDistances) {
    this.isAdjustDistances = isAdjustDistances;
  }

  
  public boolean isFanOut() {
    return isFanOut;
  }

  
  public void setFanOut(boolean isFanOut) {
    this.isFanOut = isFanOut;
  }

  
  public boolean isRotateShapes() {
    return isRotateShapes;
  }

  
  public void setRotateShapes(boolean isRotateShapes) {
    this.isRotateShapes = isRotateShapes;
  }

  
  public boolean isBendArcs() {
    return isBendArcs;
  }

  
  public void setBendArcs(boolean set) {
    isBendArcs=set;
  }
  
  
  public void setOrderSiblingsByPosition(boolean isOrderSiblingsByPosition) {
    this.isOrderSiblingsByPosition = isOrderSiblingsByPosition;
  }

  
  public boolean isOrderSiblingsByPosition() {
    return isOrderSiblingsByPosition;
  }

  
  public Shape apply(Graph2D graph2d, LayoutContext context) throws LayoutException {
    
    
    assertSpanningTree(graph2d);
    
    
    if (graph2d.getVertices().size()==0)
      return new Rectangle2D.Double();
    
    
    Vertex root = getRoot(graph2d);
    if (root==null) 
      root = graph2d.getVertices().iterator().next();
    
    
    return new Recursion(graph2d, root, distanceBetweenGenerations, context).getShape();
    
  }
  
  
  private class Recursion extends Geometry {
    
    Graph2D graph2d;
    LayoutContext context;
    int depth;
    Map<Vertex, Double> vertex2radians = new HashMap<Vertex, Double>();
    Point2D center;
    double distanceBetweenGenerations;
    double maxDiameter;
    GraphAttributes attrs;
    
    Recursion(Graph2D graph2d, Vertex root, double distanceBetweenGenerations, LayoutContext context) {
      
      
      this.graph2d = graph2d;
      this.context = context;
      this.center = ShapeHelper.getCenter(graph2d.getShape(root));
      this.distanceBetweenGenerations =  distanceBetweenGenerations;
      this.attrs = getAttribute(graph2d);
      
      
      getSize(null, root, 0);
      
      
      layout(null, root, 0, Geometry.ONE_RADIAN, 0);
      
      
      if (context.isDebug()) {
        for (int i=1;i<=depth ;i++) 
          context.addDebugShape(getCircle(i*this.distanceBetweenGenerations - this.distanceBetweenGenerations/2));
      }

      
    }
    
    Shape getShape() {
      return getCircle(depth*distanceBetweenGenerations - distanceBetweenGenerations/2);
    }
    
    Shape getCircle(double radius) {
      return new Ellipse2D.Double(center.getX()-radius, center.getY()-radius, radius*2, radius*2);
    }
    
    
    int getLengthOfEdge(Edge e) {
      return attrs.getLength(e);
    }
    
    
    double getSize(Vertex backtrack, Vertex root, int generation) {
      
      
      depth = max(depth, generation+1);

      
      double radiansOfChildren = 0;
      for (Edge edge : getNormalizedEdges(root)) {
        Vertex child = getOther(edge, root);
        if (child.equals(backtrack)) 
          continue;
        int generationOfChild = generation + getLengthOfEdge(edge);
        radiansOfChildren += getSize(root, child, generationOfChild);
      }
      
      
      if (generation==0) {
        double reqDistanceBetweenGenerations = max(
            maxDiameter,
            radiansOfChildren / ONE_RADIAN * distanceBetweenGenerations
            );
        if (isAdjustDistances && reqDistanceBetweenGenerations>distanceBetweenGenerations)
          distanceBetweenGenerations = reqDistanceBetweenGenerations;
        return 0;
      }
      
      
      double diamOfRoot = getDiameter(root, graph2d);
      maxDiameter = max(maxDiameter, diamOfRoot);
      double radiansOfRoot = ( diamOfRoot + distanceInGeneration ) / (generation*distanceBetweenGenerations);
      
      
      double result = max( radiansOfChildren, radiansOfRoot);
      vertex2radians.put(root, new Double(result));
      
      return result;
    }
    
    
    void layout(Vertex backtrack, final Vertex root, double fromRadian, final double toRadian, final double radius) {
      
      
      List<Edge> edges = getNormalizedEdges(root);
      if (backtrack!=null)
        edges.removeAll(backtrack.getEdges());
      if (edges.isEmpty())
        return;
      
      
      if (isOrderSiblingsByPosition)  {
        final double north = fromRadian+(toRadian-fromRadian)/2+Geometry.HALF_RADIAN;
 
        Edge[] tmp = edges.toArray(new Edge[edges.size()]);
        Arrays.sort(tmp, new Comparator<Edge>() {
          public int compare(Edge e1, Edge e2) {
            
            double r1 = getRadian(getDelta(center,ShapeHelper.getCenter(graph2d.getShape(getOther(e1, root)))));
            double r2 = getRadian(getDelta(center,ShapeHelper.getCenter(graph2d.getShape(getOther(e2, root)))));
            
            if (r1>north)
              r1 -= ONE_RADIAN;
            if (r2>north)
              r2 -= ONE_RADIAN;
            
            if (r1<r2) 
              return -1;
            if (r1>r2)
              return 1;
            return 0;
          }
        });
        edges = Arrays.asList(tmp);
      }
      
      
      double radiansOfChildren = 0;
      for (Edge edge : edges) {
        radiansOfChildren += vertex2radians.get(getOther(edge, root)).doubleValue();
      }
      double shareFactor = (toRadian-fromRadian) / radiansOfChildren;

      
      if ( shareFactor>1 && !isFanOut) {  
        if (backtrack!=null)
          fromRadian += ((toRadian-fromRadian) - radiansOfChildren) / 2;
        shareFactor = 1;
      }
      
      double radianOfRoot = fromRadian+radiansOfChildren*shareFactor/2;
      double[] radianOfChild = new double[edges.size()];
      
      
      for (int c=0;c<edges.size();c++) {
        
        Edge edge = edges.get(c);
        
        
        Vertex child = getOther(edge, root);
        double radiusOfChild = radius + getLengthOfEdge(edge) * distanceBetweenGenerations;
        double radiansOfChild = vertex2radians.get(child).doubleValue() * shareFactor;
        radianOfChild[c] = fromRadian + radiansOfChild/2;
        
        graph2d.setShape(child, ShapeHelper.createShape(graph2d.getShape(child),
            getPoint(center, radianOfChild[c], radiusOfChild )));

        
        if (isRotateShapes)
          graph2d.setShape(child, ShapeHelper.createShape(graph2d.getShape(child), AffineTransform.getRotateInstance(HALF_RADIAN + radianOfChild[c])));
        
        
        if (context.isDebug()) {
          context.addDebugShape(new Line2D.Double(getPoint(center, fromRadian, radiusOfChild - distanceBetweenGenerations/2), getPoint(center, fromRadian, radiusOfChild+distanceBetweenGenerations/2)));
          context.addDebugShape(new Line2D.Double(getPoint(center, fromRadian+radiansOfChild, radiusOfChild - distanceBetweenGenerations/2), getPoint(center, fromRadian+radiansOfChild, radiusOfChild+distanceBetweenGenerations/2)));
        }
        
        layout(root, child, fromRadian, fromRadian+radiansOfChild, radiusOfChild);
        
        fromRadian += radiansOfChild;
      }

      
      if (backtrack==null) {
        if (isRotateShapes)
          graph2d.setShape(root, ShapeHelper.createShape(graph2d.getShape(root), AffineTransform.getRotateInstance(HALF_RADIAN + radianOfRoot) ));
      }      

      
      for (int c=0;c<edges.size();c++) {
        Edge edge = edges.get(c);
        layout(edge, root, radianOfRoot, radius+distanceBetweenGenerations/2, radianOfChild[c], getOther(edge, root), radianOfChild);
      }

      
      return;
    }
    
    
    void layout(Edge edge, Vertex parent, double radianOfParent, double radius, double radianOfChild, Vertex child, double[] stopRadians) {

      
      if (!isBendArcs || (radianOfParent==radianOfChild)) {
        setRouting(edge, graph2d);
        return;
      } 
      
      DefaultRouting path = new DefaultRouting();
      
      
      Point2D p1 = ShapeHelper.getCenter(graph2d.getShape(child));
      Point2D p2 = getPoint(center, radianOfChild, radius);
      Collection<Point2D> is = getIntersections(p2, p1, false, graph2d.getShape(child));
      if (is.isEmpty())
        path.start(p1);
      else
        path.start(getFarthest(p1, is));
      path.lineTo(p2);

      
      if (radianOfParent>radianOfChild) {
        for (int stop=0;stopRadians[stop]<radianOfParent;stop++) {
          if (stopRadians[stop]<=radianOfChild)
            continue;
          path.arcTo(center, radius, radianOfChild, stopRadians[stop]);
          radianOfChild = stopRadians[stop];
        }
      } else {
        for (int stop=stopRadians.length-1;stopRadians[stop]>radianOfParent;stop--) {
          if (stopRadians[stop]>=radianOfChild)
            continue;
          path.arcTo(center, radius, radianOfChild, stopRadians[stop]);
          radianOfChild = stopRadians[stop];
        }
      }

      
      Point2D p3 = getPoint(center, radianOfParent, radius);
      Point2D p4 = ShapeHelper.getCenter(graph2d.getShape(parent));
      path.arcTo(center, radius, radianOfChild, radianOfParent);
      is = getIntersections(p3, p4, false, graph2d.getShape(parent));
      if (is.isEmpty())
        path.lineTo(p4);
      else
        path.lineTo(getFarthest(p4, is));

      
      if (parent.equals(edge.getStart())) 
        path.setInverted();
        
      graph2d.setRouting(edge, path);

    }

    
  } 
  
} 
