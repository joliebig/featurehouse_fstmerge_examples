
package gj.geom;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;


public class Geometry {
  
  public final static double 
    ONE_RADIAN = 2 * Math.PI,
    QUARTER_RADIAN = ONE_RADIAN/4,
    HALF_RADIAN = ONE_RADIAN/2;

  
  public static double getRadian(double degree) {
    return degree/360*Geometry.ONE_RADIAN;
  }
  
  
  public static double getRadian(Point2D vector) {
    double dx = vector.getX();
    double dy = vector.getY();
    double r = Math.sqrt(dx*dx + dy*dy);
    double result = dy < 0 ? Math.asin(dx/r) : HALF_RADIAN - Math.asin(dx/r) ;
    if (result<0)
      result += ONE_RADIAN;
    return result;
  }

  
  public static Point2D getClosest(Point2D point, Collection<Point2D> points) {
    if (points.size()==0)
      throw new IllegalArgumentException();
    
    Point2D result = null;
    double distance = Double.MAX_VALUE;
    for (Point2D p : points) {
      double d = p.distance(point);
      if (d<=distance) {
        result = p;
        distance = d;
      }
    }
    
    return result;
  }
  
  
  public static Point2D getFarthest(Point2D point, Collection<Point2D> points) {
    if (points.size()==0)
      throw new IllegalArgumentException();
    
    Point2D result = null;
    double distance = 0;
    for (Point2D p : points) {
      double d = p.distance(point);
      if (d>=distance) {
        result = p;
        distance = d;
      }
    }
    
    return result;
  }
  
  
  public static Point2D getMax(Shape shape, double axis) {
    return new OpGetMax(shape, axis).getResult();
  }
  
  
  private static class OpGetMax implements FlattenedPathConsumer {

    private Point2D vector;
    private Point2D result;

    
    public OpGetMax(Shape shape, double axis) {

      axis = axis - QUARTER_RADIAN;

      vector = new Point2D.Double(
          Math.sin(axis) * 1, -Math.cos(axis) * 1
      );
      
      ShapeHelper.iterateShape(shape, this);
    }

    
    public boolean consumeLine(Point2D start, Point2D end) {

      if (result==null) {
        result = new Point2D.Double(end.getX(), end.getY());
      } else {
        if (testPointVsLine(result, new Point2D.Double(result.getX() + vector.getX(), result.getY() + vector.getY()), end)>0)
          result.setLocation(end);
      }
      
      
      return true;
    }
    
    
    Point2D getResult() {
      return result;
    }
  } 
  
  
  public static double getDistance(Shape shape1, Shape shape2, double axis) {
    return new OpShapeShapeDistance(shape1, shape2, axis).getResult();
  }
  
  
  private static class OpShapeShapeDistance implements FlattenedPathConsumer {
    
    private double result = Double.POSITIVE_INFINITY;
    private double axis;
    
    
    private Point2D vector;

    
    private Shape intersectWith;
    
    
    
    protected OpShapeShapeDistance(Shape shape1, Shape shape2, double axis) throws IllegalArgumentException {

      
      if (axis==QUARTER_RADIAN)
        vector = new Point2D.Double(1,0);
      else 
        vector = new Point2D.Double(Math.sin(axis),-Math.cos(axis));
      
      
      this.axis = axis;
      intersectWith = shape2;
      ShapeHelper.iterateShape(shape1, this);
      
      
      this.axis += HALF_RADIAN;
      intersectWith = shape1;
      ShapeHelper.iterateShape(shape2, this);
      
      
    }
    
    
    protected double getResult() {
      return result;
    }

    
    public boolean consumeLine(Point2D start, Point2D end) {
      
      
      Point2D
        a = new Point2D.Double(end.getX()+vector.getX(), end.getY()+vector.getY()),
        b = new Point2D.Double(end.getX()-vector.getX(), end.getY()-vector.getY());

      
      Collection<Point2D> is = getIntersections(a, b, true, intersectWith);

      
      Point2D p = null;
      for (Point2D i : is) {
        result = Math.min(result, Math.sin(axis)*(i.getX()-end.getX()) - Math.cos(axis)*(i.getY()-end.getY()));
      }
      
      
      return true;
    }
  } 
  
  
  public static double getDistance(Point2D lineStart, Point2D lineEnd, PathIterator shape) {
    return new OpLineShapeDistance(lineStart, lineEnd, shape).getResult();
  }
  
  
  private static class OpLineShapeDistance implements FlattenedPathConsumer {
    
    
    private double delta = Double.MAX_VALUE;
    
    
    private double lineStartX, lineStartY, lineEndX, lineEndY;
    
    
    protected OpLineShapeDistance(Point2D lineStart, Point2D lineEnd, PathIterator shape) {
      
      lineStartX = lineStart.getX();
      lineStartY = lineStart.getY();
      lineEndX   = lineEnd.getX();
      lineEndY   = lineEnd.getY();
      
      ShapeHelper.iterateShape(shape, this);
      
    }
    
    
    protected double getResult() {
      return delta;
    }
    
    
    public boolean consumeLine(Point2D start, Point2D end) {
      
      delta = Math.min(delta, Line2D.ptLineDist(lineStartX, lineStartY, lineEndX, lineEndY, start.getX(), start.getY()));
      delta = Math.min(delta, Line2D.ptLineDist(lineStartX, lineStartY, lineEndX, lineEndY, end  .getX(), end  .getY()));
      
      return true;
    }
    
  } 
  
  
  public static double getMaximumDistance(Point2D point, Shape shape) {
    return new OpPointShapeMaxDistance(point, shape).getResult();
  }
  
  
  private static class OpPointShapeMaxDistance implements FlattenedPathConsumer {
    
    private double result = Double.NEGATIVE_INFINITY;
    private Point2D point;
    
    
    protected OpPointShapeMaxDistance(Point2D point, Shape shape) {
      this.point = point;
      ShapeHelper.iterateShape(shape, this);
    }
    
    
    protected double getResult() {
      return result;
    }
    
    
    public boolean consumeLine(Point2D start, Point2D end) {
      result = Math.max(result, start.distance(point));
      result = Math.max(result, end.distance(point));
      return true;
    }
    
  } 
  
  
  public static double getMinimumDistance(Point2D point, PathIterator shape) {
    return new OpPointShapeMinDistance(point, shape).getResult();
  }
  
  
  private static class OpPointShapeMinDistance implements FlattenedPathConsumer {
    private double result = Double.MAX_VALUE;
    private Point2D point;
    
    
    protected OpPointShapeMinDistance(Point2D point, PathIterator shape) {
      this.point = point;
      ShapeHelper.iterateShape(shape, this);
    }
    
    
    protected double getResult() {
      return result;
    }
    
    
    public boolean consumeLine(Point2D start, Point2D end) {
      
      double distance = Line2D.ptSegDist(start.getX(), start.getY(), end.getX(), end.getY(), point.getX(), point.getY());
      result = Math.min(result, distance);
      return result!=0;
    }
    
  } 
  
  
  public static double getAngle(Point2D vectorA, Point2D vectorB) {
    return (  Math.atan2( (vectorB.getY()-vectorA.getY()), (vectorB.getX()-vectorA.getX()) ) - 2*Math.PI/4) % (2*Math.PI);
  }
  
  
  public static double getCrossProduct(Point2D vectorA, Point2D vectorB) {
    return getCrossProduct( vectorA.getX(), vectorA.getY(), vectorB.getX(), vectorB.getY());
  }
  
  
  public static double getCrossProduct(double vectorAx, double vectorAy, double vectorBx, double vectorBy) {
    return ( vectorAx*vectorBy ) - ( vectorBx*vectorAy );
  }

  
  public static Point2D getPoint(Point2D origin, double radian, double distance) {
    return new Point2D.Double( origin.getX() + Math.sin(radian)*distance, origin.getY() - Math.cos(radian) * distance);
  }

  public static Point2D getVector(Point2D origin, double radian) {
    return getPoint(origin, radian, 1);
  }
  
  
  public static Point2D getPoint(Point2D p1, Point2D p2, double share) {
    if (share<0||share>1)
      throw new IllegalArgumentException("0 <= share <= 1");
    return new Point2D.Double( p1.getX() + (p2.getX()-p1.getX())*share, p1.getY() +  (p2.getY()-p1.getY())*share  );
  }
  
  public static Point2D getPoint(Point2D p, double factor) {
    return new Point2D.Double(p.getX()*factor, p.getY()*factor);
  }
  
  
  public static Point2D getDelta(Point2D vectorA, Point2D vectorB) {
    return new Point2D.Double(vectorB.getX()-vectorA.getX(), vectorB.getY()-vectorA.getY());
  }
  
  public static Point2D getMid(Point2D a, Point2D b) {
    return new Point2D.Double( (a.getX()+b.getX())/2, (a.getY()+b.getY())/2); 
  }
  
  public static Point2D getNeg(Point2D p) {
    return new Point2D.Double(-p.getX(), -p.getY());
  }
  
  
  public static Point2D getSum(Point2D vectorA, Point2D vectorB) {
    return new Point2D.Double(vectorB.getX()+vectorA.getX(), vectorB.getY()+vectorA.getY());
  }
  
      
  public static double getLength(double x, double y) {
    return Math.sqrt(x*x + y*y);
  }
  
  public static double getLength(Point2D v) {
    return getLength(v.getX(), v.getY());
  }
  
  public static Shape getTranslated(Shape s, Point2D d) {
    GeneralPath result = new GeneralPath(s);
    result.transform(AffineTransform.getTranslateInstance(d.getX(), d.getY()));
    return result;
  }
  
  
  public static List<Point2D> getIntersections(Point2D lineStart, Point2D lineEnd, Shape shape) {
    return new OpLineShapeIntersections(lineStart, lineEnd, false, shape.getPathIterator(null)).result;
  }
  
  
  public static List<Point2D> getIntersections(Point2D lineStart, Point2D lineEnd, boolean infinite, PathIterator shape) {
    return new OpLineShapeIntersections(lineStart, lineEnd, infinite, shape).result;
  }
  
  
  public static List<Point2D> getIntersections(Point2D lineStart, Point2D lineEnd, boolean infinite, Shape shape) {
    return getIntersections(lineStart, lineEnd, infinite, shape.getPathIterator(null));
  }
  
  
  private static class OpLineShapeIntersections implements FlattenedPathConsumer {
    
    
    private List<Point2D> result;
    
    
    private double distance = Double.MAX_VALUE;
    
    
    private Point2D lineStart, lineEnd;
    
    
    private boolean infinite;
    
    
    protected OpLineShapeIntersections(Point2D lineStart, Point2D lineEnd, boolean infinite, PathIterator shape) {
      
      this.result = new ArrayList<Point2D>(10);
      this.lineStart = lineStart;
      this.lineEnd   = lineEnd;
      this.infinite = infinite;
      
      ShapeHelper.iterateShape(shape, this);
      
    }
    
    
    public boolean consumeLine(Point2D start, Point2D end) {
      Point2D p = getIntersection(lineStart, lineEnd, infinite, start, end, false);
      if (p!=null) 
        result.add(p);
      return true;
    }
    
  } 

  
  public static boolean testIntersection(Point2D aStart, Point2D aEnd, Point2D bStart, Point2D bEnd) {
    
    
    
    
    
    
    
    
    
    

    Point2D vectorA = getDelta(aStart, aEnd),   
            vector1 = getDelta(aStart, bStart), 
            vector2 = getDelta(aStart, bEnd);   

    
    
    
    
    
    
    
    if (getCrossProduct(vectorA,vector1) * getCrossProduct(vectorA,vector2) >0) {
      return false;
    }
    
    
    Point2D vectorB = getDelta(bStart, bEnd);   
            vector1 = getDelta(bStart, aStart); 
            vector2 = getDelta(bStart, aEnd);   
        
    if (getCrossProduct(vectorB,vector1) * getCrossProduct(vectorB,vector2) >0) {
      return false;
    }
  
    
    return true;  
  }
  
  
  public static Point2D getIntersection(Point2D aStart, Point2D aEnd, Point2D bStart, Point2D bEnd) {
    return getIntersection(aStart, aEnd, false, bStart, bEnd, false);
  }
  
  
  public static Point2D getIntersection(Point2D aStart, Point2D aEnd, boolean aInfinite, Point2D bStart, Point2D bEnd, boolean bInfinite) {
    
    
    Point2D i = getLineIntersection(aStart, aEnd, bStart, bEnd);
    if (i==null)
      return null;

    
    if (!bInfinite && testPointVsLine(aStart, aEnd, bStart)*testPointVsLine(aStart, aEnd, bEnd) > 0)
      return null;
    
    
    if (!aInfinite && testPointVsLine(bStart, bEnd, aStart)*testPointVsLine(bStart, bEnd, aEnd) > 0)
      return null;
    
    return i;
    

























































    
  }
  
  
  public static Point2D getIntersection(Point2D pointA, double radianA, Point2D pointB, double radianB) {
    return getLineIntersection(
        pointA, 
        new Point2D.Double(pointA.getX() + Math.sin(radianA), pointA.getY() - Math.cos(radianA)),
        pointB,
        new Point2D.Double(pointB.getX() + Math.sin(radianB), pointB.getY() - Math.cos(radianB))
    );
  }
  
  
  public static Point2D getLineIntersection(Point2D aStart, Point2D aEnd, Point2D bStart, Point2D bEnd) {
    
    
    double a1 = aEnd.getY() - aStart.getY();
    double b1 = aStart.getX() - aEnd.getX();
    double c1 = aEnd.getX()*aStart.getY() - aStart.getX()*aEnd.getY();  
    
    
    double a2 = bEnd.getY()-bStart.getY();
    double b2 = bStart.getX()-bEnd.getX();
    double c2 = bEnd.getX()*bStart.getY() - bStart.getX()*bEnd.getY();

    
    
    double denom = a1*b2 - a2*b1;
    if (Math.abs(denom)< 0.000000001)
      return null;

    return new Point2D.Double( (b1*c2 - b2*c1)/denom , (a2*c1 - a1*c2)/denom);
  }
  
  
  public static Rectangle2D getBounds(Shape shape) {
    return new OpShapeBounds(shape).getResult();
  }

  
  private static class OpShapeBounds implements FlattenedPathConsumer {
    private Rectangle2D result;
    protected OpShapeBounds(Shape shape) {
      ShapeHelper.iterateShape(shape, this);
    }
    protected Rectangle2D getResult() {
      return result;
    }
    private void add(Point2D p) {
      if (result==null) {
        result = new Rectangle2D.Double(p.getX(),p.getY(),0,0);
      } else {
        result.add(p);
      }
    }
    public boolean consumeLine(Point2D start, Point2D end) {
      add(start);
      add(end);
      return true;
    }
  } 
  
  
  
  public static double getArea(Point2D a, Point2D b, Point2D c) {
    return Math.abs(_getArea(a,b,c));
  }
  private static double _getArea(Point2D a, Point2D b, Point2D c) {
    
    
    
    
    
    
    
    
    double x0 = a.getX();
    double y0 = a.getY();
    double x1 = b.getX();
    double y1 = b.getY();
    double x2 = c.getX();
    double y2 = c.getY();
    double d =  (x1*y2 - y1*x2 -x0*y2 + y0*x2 + x0*y1 - y0*x1);
    return d/2;
    
  }
  
  
  public static double testPointVsLine(Point2D start, Point2D end, Point2D point) {
    
    
    return _getArea(start, end, point);
  }
  
  
  public static ConvexHull getConvexHull(Shape shape) {
    
    if (shape instanceof Rectangle2D) {
      Rectangle2D r = (Rectangle2D)shape;
      ConvexHullImpl result = new ConvexHullImpl();
      result.moveTo(r.getMinX(), r.getMinY());
      result.lineTo(r.getMaxX(), r.getMinY());
      result.lineTo(r.getMaxX(), r.getMaxY());
      result.lineTo(r.getMinX(), r.getMaxY());
      result.closePath();
      return result;
    }
    
    if (shape instanceof ConvexHull)
      return (ConvexHull)shape;
    
    return getConvexHull(shape.getPathIterator(null));
  }
  public static ConvexHull getConvexHull(PathIterator shape) {
    
    
    
    
    
    
    final LinkedList<Point2D> points = new LinkedList<Point2D>();
    final Point2D start = new Point2D.Double(0, Double.MAX_VALUE);
    ShapeHelper.iterateShape(shape, new FlattenedPathConsumer() {
      public boolean consumeLine(Point2D from, Point2D to) {
        
        if (start.getY()==Double.MAX_VALUE)
          start.setLocation(from);

          if (to.getY()<start.getY() || (to.getY()==start.getY() && to.getX()<start.getX()) ) {
            points.add(new Point2D.Double(start.getX(), start.getY()));
            start.setLocation(to.getX(), to.getY());
          } else {
            points.add(new Point2D.Double(to.getX(), to.getY()));
          }

        
        return true;
      }
    });
    
    
    points.add(new Point2D.Double(start.getX(), start.getY()));

    
    ConvexHullImpl result = new ConvexHullImpl();
    result.moveTo( (float)start.getX(), (float)start.getY());
    Point2D from = start;
    while (!points.isEmpty()) {
      
      
      Point2D to = points.removeFirst();

      
      for (ListIterator<Point2D> others = points.listIterator(); others.hasNext(); ) {
        Point2D other = others.next();
        if (to.equals(from) || (!other.equals(from)&&testPointVsLine(from, to, other)<0)) {
          Point2D xchange = to; to = other; others.set(xchange);
        }
      }
      
      
      result.lineTo( (float)to.getX(), (float)to.getY());
      
      from =to;
      if (from.equals(start))
        break;
    }
    
    
    return result;
  }
  
  
  private static class ConvexHullImpl extends java.awt.geom.Path2D.Double implements ConvexHull {
    
  } 
  
  
  public static boolean equals(Point2D p1, Point2D p2, double delta) {
    return Math.abs(p1.getX()-p2.getX()) < delta
      && Math.abs(p1.getY()-p2.getY()) < delta;
  }
  
  
  public static Shape getInterpolation(double t, Shape start, Shape end) {

    assert t>=0;
    assert t<=1.0;
    
    if (t==0.0)
      return start;
    if (t==1.0)
      return end;
    
    GeneralPath result = new GeneralPath();
    
    List<Point2D> s = ShapeHelper.getPoints(start);
    List<Point2D> e = ShapeHelper.getPoints(end);

    boolean isStart = true, isEnd = true;
    
    for (int i=0, n=Math.max(s.size(), e.size()); i<n; i++) {
      Point2D pStart = i>=s.size() ? s.get(s.size()-1) : s.get(i);
      Point2D pEnd = i>=e.size() ? e.get(e.size()-1) : e.get(i);
      Point2D p = getPoint(pStart, pEnd, t);
      if (!equals(p, pStart, 0.001))
        isStart = false;
      if (!equals(p, pEnd, 0.001))
        isEnd = false;
      if (i==0)
        result.moveTo(p.getX(), p.getY());
      else
        result.lineTo(p.getX(), p.getY());
    }
    result.closePath();
    
    if (isEnd)
      return end;
    if (isStart)
      return start;
    
    return result;
    
  }

} 
