
package gj.layout.graph.tree;

import static gj.geom.Geometry.getMax;
import static gj.geom.Geometry.getPoint;
import static gj.geom.Geometry.getRadian;
import static gj.geom.ShapeHelper.getCenter;
import static gj.util.LayoutHelper.getNormalizedEdges;
import static gj.util.LayoutHelper.getOther;
import static gj.util.LayoutHelper.getPort;
import static gj.util.LayoutHelper.getRouting;
import gj.geom.Geometry;
import gj.layout.Graph2D;
import gj.layout.GraphNotSupportedException;
import gj.layout.LayoutContext;
import gj.layout.Port;
import gj.layout.Routing;
import gj.layout.edge.visibility.EuclideanShortestPathLayout;
import gj.model.Edge;
import gj.model.Vertex;
import gj.util.LayoutHelper;

import java.awt.geom.Point2D;
import java.util.List;


public enum EdgeLayout {
  
  Polyline { 
    @Override protected Routing routing(Graph2D graph2d, Vertex parent, Edge edge, Vertex child, int i, int j, TreeLayout layout) {
      return getRouting(edge, graph2d);
    }
  },
  
  PortPolyline {
    
    
    private Port side(TreeLayout layout) {
      if (layout.getOrientation()==0)
        return Port.North;
      if (layout.getOrientation()==180)
        return Port.South;
      if (layout.getOrientation()==90)
        return Port.East;
      if (layout.getOrientation()==270)
        return Port.West;
      
      return Port.None;
    }

    @Override protected Routing routing(Graph2D graph2d, Vertex parent, Edge edge, Vertex child, int i, int j, TreeLayout layout) {

      
      

      Port side = side(layout);
      if (edge.getEnd().equals(parent))
        side = side.opposite();
      return getRouting(
          graph2d.getShape(edge.getStart()), getPort(graph2d.getShape(edge.getStart()), i, j, side           ),
          graph2d.getShape(edge.getEnd  ()), getPort(graph2d.getShape(edge.getEnd  ()), 0, 1, side.opposite())
      );
    }
  },
  
  Orthogonal {
    @Override protected Routing routing(Graph2D graph2d, Vertex parent, Edge edge, Vertex child, int i, int j, TreeLayout layout) {
      
      double layoutAxis = getRadian(layout.getOrientation());
      
      Point2D s = getCenter(graph2d.getShape(parent));
      Point2D e = getCenter(graph2d.getShape(child));
      
      Point2D[] points;
      Point2D c;
      switch (layout.getAlignmentOfParents()) {
        case LeftOffset:
        case RightOffset:
          c = Geometry.getIntersection(s, layoutAxis-Geometry.QUARTER_RADIAN, e, layoutAxis);
          points = new Point2D[]{s, c, e};
          break;
        default:
          c = getPoint(getMax(graph2d.getShape(parent), layoutAxis), layoutAxis, layout.getDistanceBetweenGenerations()/2);
          points = new Point2D[]{s, 
              Geometry.getIntersection(s, layoutAxis, c, layoutAxis-Geometry.QUARTER_RADIAN),
              Geometry.getIntersection(e, layoutAxis, c, layoutAxis-Geometry.QUARTER_RADIAN),
              e};
      }
      return getRouting(points, graph2d.getShape(parent), graph2d.getShape(child), !edge.getStart().equals(parent));
    }
  };
  
  
  protected abstract Routing routing(Graph2D graph2d, Vertex parent, Edge edge, Vertex child, int i, int j, TreeLayout layout);

  
  protected void apply(Graph2D graph2d, Branch branch, TreeLayout layout, LayoutContext context)  throws GraphNotSupportedException {
    
    
    Vertex parent = branch.getRoot();
    List<Edge> edges = getNormalizedEdges(parent);
    int i = 0;
    int j = LayoutHelper.getOutDegree(parent);
    for (Edge edge : edges) {
      
      
      if (edge.getStart().equals(parent))
        apply(graph2d, branch, parent, edge, i++, j, layout, context);
      
      
      for (Branch sub : branch.getBranches())
        apply(graph2d, sub, layout, context);
    }
    
    
  }

  
  protected void apply(Graph2D graph2d, Branch branch, Vertex parent, Edge edge, int i, int j, TreeLayout layout, LayoutContext context) throws GraphNotSupportedException {
    
    Vertex child = getOther(edge, parent);
    
    
    for (Branch sub : branch.getBranches()) {
      if (sub.getRoot().equals(child)) {
        graph2d.setRouting(edge, routing(graph2d, parent, edge, child, i, j, layout));
        return;
      }
    }

    
    context.getLogger().info("Routing edge ["+edge+"] via EuclideanShortestPathLayout");
    new EuclideanShortestPathLayout( Math.min(layout.getDistanceBetweenGenerations(), layout.getDistanceInGeneration())/4 )
        .apply(edge, graph2d, context);
  
    
  }
  
} 

