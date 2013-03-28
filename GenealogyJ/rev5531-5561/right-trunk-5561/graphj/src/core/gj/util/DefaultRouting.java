
package gj.util;

import static gj.geom.Geometry.HALF_RADIAN;
import static gj.geom.Geometry.QUARTER_RADIAN;
import static gj.geom.Geometry.getLineIntersection;
import static gj.geom.Geometry.getPoint;

import gj.geom.Geometry;
import gj.geom.PathConsumer;
import gj.geom.ShapeHelper;
import gj.layout.Routing;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.logging.Logger;


public class DefaultRouting implements Routing {
  
  private final static Logger LOG = Logger.getLogger("genj.geom");

  
  private GeneralPath gp = new GeneralPath();
  
  
  private double firstAngle = Double.NaN;
  private double lastAngle = Double.NaN;
  
  
  private Point2D.Double firstPoint = null;
  private Point2D.Double lastPoint = null;
  private boolean isInverted = false;
  
  
  public DefaultRouting() {
  }
  
  
  public DefaultRouting(Routing that, AffineTransform at) {
    copy(that, at);
  }
  
  private void copy(Routing that, AffineTransform at) {
    this.gp = new GeneralPath(that);
    if (at!=null)
      this.gp.transform(at);
    this.firstAngle = that.getFirstAngle();
    this.lastAngle = that.getLastAngle();
    this.firstPoint = new Point2D.Double();
    this.firstPoint.setLocation(that.getFirstPoint());
    this.lastPoint = new Point2D.Double();
    this.lastPoint.setLocation(that.getLastPoint());
  }
  
  
  public DefaultRouting(Shape that) {
    
    if (that instanceof Routing) {
      copy((Routing)that, null);
      return;
    }
    
    ShapeHelper.iterateShape(that, new PathConsumer() {

      public boolean consumeCubicCurve(Point2D start, Point2D ctrl1, Point2D ctrl2, Point2D end) {
        if (firstPoint==null)
          start(start);
        else if (!lastPoint.equals(start))
          throw new IllegalArgumentException("gap in path between "+lastPoint+" and "+start);
        curveTo(ctrl1, ctrl2, end);
        return true;
      }

      public boolean consumeLine(Point2D start, Point2D end) {
        if (firstPoint==null)
          start(start);
        else if (!lastPoint.equals(start))
          throw new IllegalArgumentException("gap in path between "+lastPoint+" and "+start);
        lineTo(end);
        return true;
      }

      public boolean consumeQuadCurve(Point2D start, Point2D ctrl, Point2D end) {
        if (firstPoint==null)
          start(start);
        else if (!lastPoint.equals(start))
          throw new IllegalArgumentException("gap in path between "+lastPoint+" and "+start);
        quadTo(ctrl, end);
        return true;
      }
      
      
    });
    
  }
  
  
  public synchronized void setInverted() {
    Point2D.Double p = lastPoint; lastPoint = firstPoint; firstPoint = p;
    double a = lastAngle; lastAngle = firstAngle + HALF_RADIAN; firstAngle = a + HALF_RADIAN;
    isInverted = !isInverted;
  }
  
  public boolean isInverted() {
    return isInverted;
  }
  
  
  public synchronized Point2D getFirstPoint() {
    return new Point2D.Double(firstPoint.x, firstPoint.y);
  }
  
  
  public synchronized Point2D getLastPoint() {
    return new Point2D.Double(lastPoint.x, lastPoint.y);
  }
  
  
  public synchronized double getFirstAngle() {
    return firstAngle;
  }

  
  public synchronized double getLastAngle() {
    return lastAngle;
  }

  
  public synchronized void translate(Point2D delta) {
    translate(delta.getX(), delta.getY());
  }

  
  public synchronized void translate(double dx, double dy) {
    gp.transform(AffineTransform.getTranslateInstance(dx,dy));
    lastPoint.setLocation(
      lastPoint.getX()+dx,
      lastPoint.getY()+dy
    );
  }
  
  
  public synchronized Routing start(Point2D p) {
    
    
    if (firstPoint!=null)
      throw new IllegalArgumentException("start twice");

    
    gp.moveTo((float)p.getX(), (float)p.getY());
    
    
    firstPoint = new Point2D.Double(p.getX(), p.getY());
    lastPoint = new Point2D.Double(p.getX(), p.getY());

    
    return this;
  }
  
  public boolean isStarted() {
    return firstPoint!=null;
  }
  
  
  private void checkContinue() {
    if (firstPoint==null)
      throw new IllegalArgumentException("continue without start");
  }
  
  
  public synchronized Routing lineTo(Point2D p) {
    
    checkContinue();
    
    if (Double.isNaN(firstAngle))
      firstAngle = Geometry.getAngle(firstPoint, p);
    
    gp.lineTo((float)p.getX(), (float)p.getY());
    
    lastAngle = Geometry.getAngle(lastPoint,p);
    lastPoint.setLocation(p.getX(), p.getY());
    return this;
  }
  
  
  public synchronized Routing quadTo(Point2D c, Point2D p) {
    
    checkContinue();
    
    if (Double.isNaN(firstAngle))
      firstAngle = Geometry.getAngle(firstPoint, c);
    
    gp.quadTo((float)c.getX(), (float)c.getY(), (float)p.getX(), (float)p.getY());
    
    lastAngle = Geometry.getAngle(c,p);
    lastPoint.setLocation(p.getX(), p.getY());
    return this;
  }
  
  
  public synchronized Routing curveTo(Point2D c1, Point2D c2, Point2D p) {
    
    checkContinue();
    
    if (Double.isNaN(firstAngle))
      firstAngle = Geometry.getAngle(firstPoint, c1);
    
    gp.curveTo((float)c1.getX(), (float)c1.getY(), (float)c2.getX(), (float)c2.getY(), (float)p.getX(), (float)p.getY());
    
    lastAngle = Geometry.getAngle(c2,p);
    
    lastPoint.setLocation(p.getX(), p.getY());
    return this;
  }
  

  
  public void arcTo(Point2D center, double radius, double fromRadian, double toRadian) {

    
    if (fromRadian==toRadian)
      return;
    
    
    double radians = toRadian-fromRadian;
    int segments = (int)Math.ceil(Math.abs(radians/QUARTER_RADIAN));
    Point2D from = getPoint(center, fromRadian, radius);
    for (int s=1;s<=segments;s++) {
      
      Point2D to = getPoint(center, fromRadian+s*(radians/segments), radius);
      
      
      if (from.distance(to)<1) {
        lineTo(to);
      } else {
        
        Point2D i = getLineIntersection(
          from, new Point2D.Double( from.getX() - (from.getY()-center.getY()), from.getY() + (from.getX()-center.getX()) ), 
          to, new Point2D.Double( to.getX() - (to.getY()-center.getY()), to.getY() + (to.getX()-center.getX()) )
          );
        
        
        double kappa = 0.5522847498;
        Point2D c1 = new Point2D.Double( from.getX() + (i.getX()-from.getX())*kappa , from.getY() + (i.getY()-from.getY())*kappa );
        Point2D c2 = new Point2D.Double( to.getX() + (i.getX()-to.getX())*kappa , to.getY() + (i.getY()-to.getY())*kappa );
        curveTo(c1, c2, to);
      }
      
      
      from = to;
    }
    
    
  }    
  

  
  public boolean contains(double x, double y, double w, double h) {
    return gp.contains(x,y,w,h);
  }

  
  public boolean contains(double x, double y) {
    return gp.contains(x,y);
  }

  
  public boolean contains(Point2D p) {
    return gp.contains(p);
  }

  
  public boolean contains(Rectangle2D r) {
    return gp.contains(r);
  }

  
  public Rectangle getBounds() {
    return gp.getBounds();
  }

  
  public Rectangle2D getBounds2D() {
    return gp.getBounds2D();
  }
  
  
  public PathIterator getPathIterator(AffineTransform at, double flatness) {
    return gp.getPathIterator(at,flatness);
  }

  
  public PathIterator getPathIterator(AffineTransform at) {
    return gp.getPathIterator(at);
  }

  
  public boolean intersects(double x, double y, double w, double h) {
    return gp.intersects(x,y,w,h);
  }

  
  public boolean intersects(Rectangle2D r) {
    return gp.intersects(r);
  }

} 
