

package org.jfree.chart.util;

import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;


public class ShapeUtilities {

    
    private ShapeUtilities() {
    }

    
    public static Shape clone(Shape shape) {

        if (shape instanceof Cloneable) {
            try {
                return (Shape) ObjectUtilities.clone(shape);
            }
            catch (CloneNotSupportedException cnse) {
            }
        }
        Shape result = null;
        return result;
    }
    
    
    public static boolean equal(Shape s1, Shape s2) {
        if (s1 instanceof Line2D && s2 instanceof Line2D) {
            return equal((Line2D) s1, (Line2D) s2);
        }
        else if (s1 instanceof Ellipse2D && s2 instanceof Ellipse2D) {
            return equal((Ellipse2D) s1, (Ellipse2D) s2);
        }
        else if (s1 instanceof Arc2D && s2 instanceof Arc2D) {
            return equal((Arc2D) s1, (Arc2D) s2);   
        }
        else if (s1 instanceof Polygon && s2 instanceof Polygon) {
            return equal((Polygon) s1, (Polygon) s2);   
        }
        else if (s1 instanceof GeneralPath && s2 instanceof GeneralPath) {
            return equal((GeneralPath) s1, (GeneralPath) s2);   
        }
        else {
            
            return ObjectUtilities.equal(s1, s2);
        }
    }

    
    public static boolean equal(Line2D l1, Line2D l2) {
        if (l1 == null) {
            return (l2 == null);
        }
        if (l2 == null) {
            return false;
        }
        if (!l1.getP1().equals(l2.getP1())) {
            return false;
        }
        if (!l1.getP2().equals(l2.getP2())) {
            return false;
        }
        return true;
    }
    
    
    public static boolean equal(Ellipse2D e1, Ellipse2D e2) {
        if (e1 == null) {
            return (e2 == null);
        }
        if (e2 == null) {
            return false;
        }
        if (!e1.getFrame().equals(e2.getFrame())) {
            return false;
        }
        return true;
    }
    
    
    public static boolean equal(Arc2D a1, Arc2D a2) {
        if (a1 == null) {
            return (a2 == null);
        }
        if (a2 == null) {
            return false;
        }
        if (!a1.getFrame().equals(a2.getFrame())) {
            return false;
        }
        if (a1.getAngleStart() != a2.getAngleStart()) {
            return false;   
        }
        if (a1.getAngleExtent() != a2.getAngleExtent()) {
            return false;   
        }
        if (a1.getArcType() != a2.getArcType()) {
            return false;   
        }
        return true;
    }
    
        
    public static boolean equal(Polygon p1, Polygon p2) {
        if (p1 == null) {
            return (p2 == null);
        }
        if (p2 == null) {
            return false;
        }
        if (p1.npoints != p2.npoints) {
            return false;
        }
        if (!Arrays.equals(p1.xpoints, p2.xpoints)) {
            return false;
        }
        if (!Arrays.equals(p1.ypoints, p2.ypoints)) {
            return false;
        }
        return true;
    }
    
        
    public static boolean equal(GeneralPath p1, GeneralPath p2) {
        if (p1 == null) {
            return (p2 == null);
        }
        if (p2 == null) {
            return false;
        }
        if (p1.getWindingRule() != p2.getWindingRule()) {
            return false;
        }
        PathIterator iterator1 = p1.getPathIterator(null);
        PathIterator iterator2 = p1.getPathIterator(null);
        double[] d1 = new double[6];
        double[] d2 = new double[6];
        boolean done = iterator1.isDone() && iterator2.isDone();
        while (!done) {
            if (iterator1.isDone() != iterator2.isDone()) {
                return false;   
            }
            int seg1 = iterator1.currentSegment(d1);
            int seg2 = iterator2.currentSegment(d2);
            if (seg1 != seg2) {
                return false;   
            }
            if (!Arrays.equals(d1, d2)) {
                return false;   
            }
            iterator1.next();
            iterator2.next();
            done = iterator1.isDone() && iterator2.isDone();
        }
        return true;
    }
    
    
    public static Shape createTranslatedShape(Shape shape,
                                              double transX, 
                                              double transY) {
        if (shape == null) {
            throw new IllegalArgumentException("Null 'shape' argument.");
        }
        AffineTransform transform = AffineTransform.getTranslateInstance(
                transX, transY);
        return transform.createTransformedShape(shape);
    }
    
    
    public static Shape createTranslatedShape(Shape shape, 
                                              RectangleAnchor anchor, 
                                              double locationX,
                                              double locationY) {
        if (shape == null) {
            throw new IllegalArgumentException("Null 'shape' argument.");
        }        
        if (anchor == null) {
            throw new IllegalArgumentException("Null 'anchor' argument.");
        }
        Point2D anchorPoint = RectangleAnchor.coordinates(shape.getBounds2D(), 
                anchor);
        AffineTransform transform = AffineTransform.getTranslateInstance(
                locationX - anchorPoint.getX(), locationY - anchorPoint.getY());
        return transform.createTransformedShape(shape);   
    }

    
    public static Shape rotateShape(Shape base, double angle,
                                    float x, float y) {
        if (base == null) {
            return null;
        }
        AffineTransform rotate = AffineTransform.getRotateInstance(angle, x, y);
        Shape result = rotate.createTransformedShape(base);
        return result;
    }

    
    public static void drawRotatedShape(Graphics2D g2, Shape shape,
                                        double angle, float x, float y) {

        AffineTransform saved = g2.getTransform();
        AffineTransform rotate = AffineTransform.getRotateInstance(angle, x, y);
        g2.transform(rotate);
        g2.draw(shape);
        g2.setTransform(saved);

    }

    
    private static final float SQRT2 = (float) Math.pow(2.0, 0.5);

    
    public static Shape createDiagonalCross(float l, float t) {
        GeneralPath p0 = new GeneralPath();
        p0.moveTo(-l - t, -l + t);
        p0.lineTo(-l + t, -l - t);
        p0.lineTo(0.0f, -t * SQRT2);
        p0.lineTo(l - t, -l - t);
        p0.lineTo(l + t, -l + t);
        p0.lineTo(t * SQRT2, 0.0f);
        p0.lineTo(l + t, l - t);
        p0.lineTo(l - t, l + t);
        p0.lineTo(0.0f, t * SQRT2);
        p0.lineTo(-l + t, l + t);
        p0.lineTo(-l - t, l - t);
        p0.lineTo(-t * SQRT2, 0.0f);
        p0.closePath();
        return p0;
    }
    
    
    public static Shape createRegularCross(float l, float t) {
        GeneralPath p0 = new GeneralPath();
        p0.moveTo(-l, t);
        p0.lineTo(-t, t);
        p0.lineTo(-t, l);
        p0.lineTo(t, l);
        p0.lineTo(t, t);
        p0.lineTo(l, t);
        p0.lineTo(l, -t);
        p0.lineTo(t, -t);
        p0.lineTo(t, -l);
        p0.lineTo(-t, -l);
        p0.lineTo(-t, -t);
        p0.lineTo(-l, -t);
        p0.closePath();
        return p0;
    }
    
    
    public static Shape createDiamond(float s) {
        GeneralPath p0 = new GeneralPath();
        p0.moveTo(0.0f, -s);
        p0.lineTo(s, 0.0f);
        p0.lineTo(0.0f, s);
        p0.lineTo(-s, 0.0f);
        p0.closePath();
        return p0;
    }
    
    
    public static Shape createUpTriangle(float s) {
        GeneralPath p0 = new GeneralPath();
        p0.moveTo(0.0f, -s);
        p0.lineTo(s, s);
        p0.lineTo(-s, s);
        p0.closePath();
        return p0;
    }

    
    public static Shape createDownTriangle(float s) {
        GeneralPath p0 = new GeneralPath();
        p0.moveTo(0.0f, s);
        p0.lineTo(s, -s);
        p0.lineTo(-s, -s);
        p0.closePath();
        return p0;
    }

    
    public static Shape createLineRegion(Line2D line, float width) {
        GeneralPath result = new GeneralPath();
        float x1 = (float) line.getX1();
        float x2 = (float) line.getX2();
        float y1 = (float) line.getY1();
        float y2 = (float) line.getY2();
        if ((x2 - x1) != 0.0) {
            double theta = Math.atan((y2 - y1) / (x2 - x1));
            float dx = (float) Math.sin(theta) * width;
            float dy = (float) Math.cos(theta) * width;
            result.moveTo(x1 - dx, y1 + dy);
            result.lineTo(x1 + dx, y1 - dy);
            result.lineTo(x2 + dx, y2 - dy);
            result.lineTo(x2 - dx, y2 + dy);
            result.closePath();
        }
        else {
            
            result.moveTo(x1 - width / 2.0f, y1);
            result.lineTo(x1 + width / 2.0f, y1);
            result.lineTo(x2 + width / 2.0f, y2);
            result.lineTo(x2 - width / 2.0f, y2);
            result.closePath();
        }
        return result;    
    }
        
    
    public static Point2D getPointInRectangle(double x, double y, 
                                              Rectangle2D area) {

        x = Math.max(area.getMinX(), Math.min(x, area.getMaxX()));
        y = Math.max(area.getMinY(), Math.min(y, area.getMaxY()));
        return new Point2D.Double(x, y);

    }

    
    public static boolean contains(Rectangle2D rect1, Rectangle2D rect2) {
        
        double x0 = rect1.getX();
        double y0 = rect1.getY();
        double x = rect2.getX();
        double y = rect2.getY();
        double w = rect2.getWidth();
        double h = rect2.getHeight();

        return ((x >= x0) && (y >= y0) 
                && ((x + w) <= (x0 + rect1.getWidth())) 
                && ((y + h) <= (y0 + rect1.getHeight())));
    
    }
    

    
    public static boolean intersects(Rectangle2D rect1, Rectangle2D rect2) {

      double x0 = rect1.getX();
      double y0 = rect1.getY();

      double x = rect2.getX();
      double width = rect2.getWidth();
      double y = rect2.getY();
      double height = rect2.getHeight();
      return (x + width >= x0 && y + height >= y0 && x <= x0 + rect1.getWidth()
              && y <= y0 + rect1.getHeight());
    }
}
