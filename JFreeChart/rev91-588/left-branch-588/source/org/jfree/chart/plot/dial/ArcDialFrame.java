

package org.jfree.chart.plot.dial;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.jfree.chart.HashUtilities;
import org.jfree.io.SerialUtilities;
import org.jfree.util.PaintUtilities;
import org.jfree.util.PublicCloneable;


public class ArcDialFrame extends AbstractDialLayer implements DialFrame, 
        Cloneable, PublicCloneable, Serializable {
    
    
    static final long serialVersionUID = -4089176959553523499L;

    
    private transient Paint backgroundPaint;
    
    
    private transient Paint foregroundPaint;
    
    
    private transient Stroke stroke;
       
    
    private double startAngle;
    
    
    private double extent;
    
    
    private double innerRadius;
    
    
    private double outerRadius;
   
    
    public ArcDialFrame() {
        this(0, 180);
    }
    
    
    public ArcDialFrame(double startAngle, double extent) {
        this.backgroundPaint = Color.gray;
        this.foregroundPaint = new Color(100, 100, 150);
        this.stroke = new BasicStroke(2.0f);
        this.innerRadius = 0.25;
        this.outerRadius = 0.75;
        this.startAngle = startAngle;
        this.extent = extent;        
    }
    
    
    public Paint getBackgroundPaint() {
        return this.backgroundPaint;
    }
    
    
    public void setBackgroundPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.backgroundPaint = paint;
        notifyListeners(new DialLayerChangeEvent(this));
    }
    
    
    public Paint getForegroundPaint() {
        return this.foregroundPaint;
    }
    
    
    public void setForegroundPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.foregroundPaint = paint;
        notifyListeners(new DialLayerChangeEvent(this));
    }
    
    
    public Stroke getStroke() {
        return this.stroke;
    }
    
    
    public void setStroke(Stroke stroke) {
        if (stroke == null) { 
            throw new IllegalArgumentException("Null 'stroke' argument.");
        }
        this.stroke = stroke;
        notifyListeners(new DialLayerChangeEvent(this));
    }

    
    public double getInnerRadius() {
        return this.innerRadius;
    }
    
    
    public void setInnerRadius(double radius) {
        if (radius < 0.0) {
            throw new IllegalArgumentException("Negative 'radius' argument.");
        }
        this.innerRadius = radius;
        notifyListeners(new DialLayerChangeEvent(this));
    }
    
    
    public double getOuterRadius() {
        return this.outerRadius;
    }
    
    
    public void setOuterRadius(double radius) {
        if (radius < 0.0) {
            throw new IllegalArgumentException("Negative 'radius' argument.");
        }
        this.outerRadius = radius;
        notifyListeners(new DialLayerChangeEvent(this));
    }
    
    
    public double getStartAngle() {
        return this.startAngle;
    }
    
    
    public void setStartAngle(double angle) {
        this.startAngle = angle;
        notifyListeners(new DialLayerChangeEvent(this));
    }
    
    
    public double getExtent() {
        return this.extent;
    }
    
    
    public void setExtent(double extent) {
        this.extent = extent;
        notifyListeners(new DialLayerChangeEvent(this));
    }
    
    
    public Shape getWindow(Rectangle2D frame) {
        
        Rectangle2D innerFrame = DialPlot.rectangleByRadius(frame, 
                this.innerRadius, this.innerRadius);
        Rectangle2D outerFrame = DialPlot.rectangleByRadius(frame, 
                this.outerRadius, this.outerRadius);
        Arc2D inner = new Arc2D.Double(innerFrame, this.startAngle, 
                this.extent, Arc2D.OPEN);
        Arc2D outer = new Arc2D.Double(outerFrame, this.startAngle 
                + this.extent, -this.extent, Arc2D.OPEN);
        GeneralPath p = new GeneralPath();
        Point2D point1 = inner.getStartPoint();
        p.moveTo((float) point1.getX(), (float) point1.getY());
        p.append(inner, true);
        p.append(outer, true);
        p.closePath();
        return p;
        
    }
    
    
    protected Shape getOuterWindow(Rectangle2D frame) {
        double radiusMargin = 0.02;
        double angleMargin = 1.5;
        Rectangle2D innerFrame = DialPlot.rectangleByRadius(frame, 
                this.innerRadius - radiusMargin, this.innerRadius 
                - radiusMargin);
        Rectangle2D outerFrame = DialPlot.rectangleByRadius(frame, 
                this.outerRadius + radiusMargin, this.outerRadius 
                + radiusMargin);
        Arc2D inner = new Arc2D.Double(innerFrame, this.startAngle 
                - angleMargin, this.extent + 2 * angleMargin, Arc2D.OPEN);
        Arc2D outer = new Arc2D.Double(outerFrame, this.startAngle 
                + angleMargin + this.extent, -this.extent - 2 * angleMargin, 
                Arc2D.OPEN);
        GeneralPath p = new GeneralPath();
        Point2D point1 = inner.getStartPoint();
        p.moveTo((float) point1.getX(), (float) point1.getY());
        p.append(inner, true);
        p.append(outer, true);
        p.closePath();
        return p;
    }
    
    
    public void draw(Graphics2D g2, DialPlot plot, Rectangle2D frame, 
            Rectangle2D view) {
        
        Shape window = getWindow(frame);
        Shape outerWindow = getOuterWindow(frame);

        Area area1 = new Area(outerWindow);
        Area area2 = new Area(window);
        area1.subtract(area2);
        g2.setPaint(Color.lightGray);
        g2.fill(area1);
        
        g2.setStroke(this.stroke);
        g2.setPaint(this.foregroundPaint);
        g2.draw(window);
        g2.draw(outerWindow);
        
        
    }

    
    public boolean isClippedToWindow() {
        return false;
    }
    
    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ArcDialFrame)) {
            return false;
        }
        ArcDialFrame that = (ArcDialFrame) obj;
        if (!PaintUtilities.equal(this.backgroundPaint, that.backgroundPaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.foregroundPaint, that.foregroundPaint)) {
            return false;
        }
        if (this.startAngle != that.startAngle) {
            return false;
        }
        if (this.extent != that.extent) {
            return false;
        }
        if (this.innerRadius != that.innerRadius) {
            return false;
        }
        if (this.outerRadius != that.outerRadius) {
            return false;
        }
        if (!this.stroke.equals(that.stroke)) {
            return false;
        }
        return super.equals(obj);
    }
    
    
    public int hashCode() {
        int result = 193;
        long temp = Double.doubleToLongBits(this.startAngle);
        result = 37 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(this.extent);
        result = 37 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(this.innerRadius);
        result = 37 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(this.outerRadius);
        result = 37 * result + (int) (temp ^ (temp >>> 32));
        result = 37 * result + HashUtilities.hashCodeForPaint(
                this.backgroundPaint);
        result = 37 * result + HashUtilities.hashCodeForPaint(
                this.foregroundPaint);
        result = 37 * result + this.stroke.hashCode();
        return result;
    }
    
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.backgroundPaint, stream);
        SerialUtilities.writePaint(this.foregroundPaint, stream);
        SerialUtilities.writeStroke(this.stroke, stream);
    }

    
    private void readObject(ObjectInputStream stream) 
            throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.backgroundPaint = SerialUtilities.readPaint(stream);
        this.foregroundPaint = SerialUtilities.readPaint(stream);
        this.stroke = SerialUtilities.readStroke(stream);
    }
    
}
