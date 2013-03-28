
package gj.geom;

import static gj.geom.PathIteratorKnowHow.SEG_CLOSE;
import static gj.geom.PathIteratorKnowHow.SEG_CUBICTO;
import static gj.geom.PathIteratorKnowHow.SEG_LINETO;
import static gj.geom.PathIteratorKnowHow.SEG_MOVETO;
import static gj.geom.PathIteratorKnowHow.SEG_QUADTO;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;


public class ShapeHelper {
  
  
  private static double defaultFlatness = 0.5;
  
  
  public void setDefaultFlatness(double flatness) {
    defaultFlatness = Math.min(flatness, defaultFlatness);
  }

  
  public static Point2D getCenter(Shape shape) {
    Rectangle2D bounds = shape.getBounds2D();
    return new Point2D.Double(bounds.getCenterX(), bounds.getCenterY());
  }
  
  
  public static Shape createShape(Point2D ... points) {
    if (points.length<2)
      throw new IllegalArgumentException("Need minimum of 2 points for shape");
    
    GeneralPath result = new GeneralPath();
    result.moveTo( (float)points[0].getX(), (float)points[0].getY());
    for (int i = 1; i < points.length; i++) {
      result.lineTo( (float)points[i].getX(), (float)points[i].getY());
    }
    
    return result;
  }
  
  
  public static Shape createShape(Shape shape, Point2D center) {
    Point2D oldCenter = getCenter(shape);
    GeneralPath result = new GeneralPath(shape);
    result.transform(AffineTransform.getTranslateInstance(center.getX()-oldCenter.getX(), center.getY()-oldCenter.getY()));
    return result;
  }
  
  
  public static Shape createShape(Shape shape, AffineTransform tx) {
    return new TransformedShapeImpl(shape, tx);
  }
  
  private static class TransformedShapeImpl extends Path2D.Double implements TransformedShape {
    
    private AffineTransform tx;
    
    public TransformedShapeImpl(Shape shape, AffineTransform tx) {
      super(shape);
      Point2D center = getCenter(shape);
      transform(AffineTransform.getTranslateInstance(-center.getX(), -center.getY()));
      transform(tx);
      transform(AffineTransform.getTranslateInstance(center.getX(), center.getY()));

      this.tx = tx;
      if (shape instanceof TransformedShape) 
        this.tx.concatenate(((TransformedShape)shape).getTransformation());
    }
    
    public AffineTransform getTransformation() {
      return tx;
    }
    
  } 
  
  

  
  public static Shape createShape(Shape shape, Point2D axisStart, double axisRadian, int alignment) {
    return new OpAlignShape(shape, axisStart, Geometry.getPoint(axisStart, axisRadian, 1), alignment).getResult();
  }

  
  public static Shape createShape(Shape shape, Point2D axisStart, Point2D axisEnd, int alignment) {
    return new OpAlignShape(shape, axisStart, axisEnd, alignment).getResult();
  }
  
  private static class OpAlignShape implements FlattenedPathConsumer {

    private Shape result;
    private Point2D axisStart, axisEnd;
    private int alignment;
    private double leftmost = Double.MAX_VALUE, rightmost = -Double.MAX_VALUE;
    
    private OpAlignShape(Shape shape, Point2D axisStart, Point2D axisEnd, int alignment) {
      
      this.axisStart = axisStart;
      this.axisEnd = axisEnd;
      this.alignment = alignment;
      
      iterateShape(shape, this);
      
      double delta = 0;
      if (alignment<0)       
        delta = -rightmost;
      else if (alignment>0)  
        delta = -leftmost;
      else                   
        delta = -(rightmost+leftmost)/2;
      
      Point2D vector = new Point2D.Double( -(axisEnd.getY()-axisStart.getY()), axisEnd.getX()-axisStart.getX() );
      double len = Geometry.getLength(vector);
      result = createShape(shape, AffineTransform.getTranslateInstance( vector.getX()/len*delta, vector.getY()/len*delta));
      
    }
    
    public boolean consumeLine(Point2D start, Point2D end) {
      if (leftmost == Double.MAX_VALUE)
        track(start);
      track(end);
      return true;
    }
    
    private void track(Point2D point) {
      double dist = Line2D.ptLineDist(axisStart.getX(), axisStart.getY(), axisEnd.getX(), axisEnd.getY(), point.getX(), point.getY());
      if (dist==0) {
        leftmost = Math.min(leftmost,0);
        rightmost = Math.max(rightmost,0);
      } else {
        if (Geometry.testPointVsLine(axisStart, axisEnd, point)<0) {
          leftmost = Math.min(leftmost, -dist);
          rightmost = Math.max(rightmost, -dist);
        } else {
          leftmost = Math.min(leftmost, dist);
          rightmost = Math.max(rightmost, dist);
        }
      }
    }
    
    private Shape getResult() {
      return result;
    }
  } 

  
  public static Shape createShape(double x, double y, double sx, double sy, double[] values) {
    
    
    GeneralPath result = new GeneralPath();

    
    for (int i=0;i<values.length;) {
      double type = values[i++];
      if (type==-1) break;
      switch ((int)type) {
        case SEG_MOVETO:
          result.moveTo ((float)((values[i++]-x)*sx),(float)((values[i++]-y)*sy)); break;
        case SEG_LINETO:
          result.lineTo ((float)((values[i++]-x)*sx),(float)((values[i++]-y)*sy)); break;
        case SEG_QUADTO:
          result.quadTo ((float)((values[i++]-x)*sx),(float)((values[i++]-y)*sy),(float)((values[i++]-x)*sx),(float)((values[i++]-y)*sy)); break;
        case SEG_CUBICTO:
          result.curveTo((float)((values[i++]-x)*sx),(float)((values[i++]-y)*sy),(float)((values[i++]-x)*sx),(float)((values[i++]-y)*sy),(float)((values[i++]-x)*sx),(float)((values[i++]-y)*sy)); break;
        case SEG_CLOSE:
          result.closePath();
      }        
    }
    
    
    return result;        
  }

  
  public static void iterateShape(Shape shape, Point2D pos, FlattenedPathConsumer consumer) {
    iterateShape(shape.getPathIterator(AffineTransform.getTranslateInstance(pos.getX(), pos.getY())), consumer);
  }
  public static void iterateShape(Shape shape, FlattenedPathConsumer consumer) {
    iterateShape(shape.getPathIterator(null), consumer);
  }
  public static void iterateShape(PathIterator iterator, final FlattenedPathConsumer consumer) {
    iterateShape(new FlatteningPathIterator(iterator, defaultFlatness), 
      new PathConsumer() {
        public boolean consumeCubicCurve(Point2D start, Point2D ctrl1, Point2D ctrl2, Point2D end) {
          throw new IllegalStateException("unexpected cubic curve");
        }
        public boolean consumeLine(Point2D start, Point2D end) {
          return consumer.consumeLine(start, end);
        }
        public boolean consumeQuadCurve(Point2D start, Point2D ctrl, Point2D end) {
          throw new IllegalStateException("unexpected quad curve");
        }
      }
    );
  }
  public static void iterateShape(Shape shape, PathConsumer consumer) {
    iterateShape(shape.getPathIterator(null), consumer);
  }
  public static void iterateShape(PathIterator iterator, PathConsumer consumer) {

    
    double[] segment = new double[6];
    Point2D lastPosition = new Point2D.Double();
    Point2D nextPosition = new Point2D.Double();
    Point2D ctrl1Position = new Point2D.Double();
    Point2D ctrl2Position = new Point2D.Double();
    Point2D movePosition = new Point2D.Double();
  
    boolean goon = true;      
    while (!iterator.isDone()) {
      
      switch (iterator.currentSegment(segment)) {
        case (PathIterator.SEG_MOVETO) :
          nextPosition.setLocation(segment[0],segment[1]);
          movePosition.setLocation(nextPosition);
          break;
        case (PathIterator.SEG_LINETO) :
          nextPosition.setLocation(segment[0], segment[1]);
          goon = consumer.consumeLine(lastPosition, nextPosition);
          break;
        case (PathIterator.SEG_CLOSE) :
          if (movePosition.equals(lastPosition))
            break;
          goon = consumer.consumeLine(lastPosition, movePosition);
          break;
        case (PathIterator.SEG_QUADTO) :
          ctrl1Position.setLocation(segment[0],segment[1]);
          nextPosition.setLocation(segment[2],segment[3]);
          goon = consumer.consumeQuadCurve(lastPosition, ctrl1Position, nextPosition);
          break;
        case (PathIterator.SEG_CUBICTO) :
            ctrl1Position.setLocation(segment[0],segment[1]);
            ctrl2Position.setLocation(segment[2],segment[3]);
            nextPosition.setLocation(segment[4],segment[5]);
            goon = consumer.consumeCubicCurve(lastPosition, ctrl1Position, ctrl2Position, nextPosition);
          break;
      }
      
      
      if (!goon) {
        break;
      }
      
      
      lastPosition.setLocation(nextPosition);
      
      
      iterator.next();
    }
    
    
  }  

  
  public static Shape createShape(Shape shape, double scale, Point2D origin) {
    GeneralPath gp = new GeneralPath(shape); 
    if (origin!=null)
      gp.transform(AffineTransform.getTranslateInstance(origin.getX(),origin.getY()));
    gp.transform(AffineTransform.getScaleInstance(scale,scale));
    return gp;
  }
  
  
  public static List<Point2D> getPoints(Shape shape) {
    return getPoints(shape, new Point2D.Double());
  }
  
  
  public static List<Point2D> getPoints(Shape shape, Point2D pos) {
    return getPoints(shape.getPathIterator(AffineTransform.getTranslateInstance(pos.getX(), pos.getY())));
  }
  
  
  public static List<Point2D> getPoints(PathIterator shape) {
    
    final List<Point2D> result = new ArrayList<Point2D>();
    
    iterateShape(shape, new FlattenedPathConsumer() {
      public boolean consumeLine(Point2D start, Point2D end) {
        if (result.isEmpty() || !result.get(result.size()-1).equals(start))
          result.add(new Point2D.Double(start.getX(), start.getY()));
        
        return true;
      }
    });
    
    return result;
  }
    
} 
