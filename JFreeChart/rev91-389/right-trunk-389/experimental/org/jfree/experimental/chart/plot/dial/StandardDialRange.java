

package org.jfree.experimental.chart.plot.dial;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.jfree.chart.HashUtilities;
import org.jfree.chart.util.PaintUtilities;
import org.jfree.chart.util.PublicCloneable;
import org.jfree.chart.util.SerialUtilities;


public class StandardDialRange extends AbstractDialLayer implements DialLayer, 
        Cloneable, PublicCloneable, Serializable {
    
    
    private double lowerBound;
    
    
    private double upperBound;

    
    private transient Paint paint;
    
    
    private double innerRadius;

    
    private double outerRadius;
    
    
    public StandardDialRange() {
        this(0.0, 100.0, Color.white);
    }
    
    
    public StandardDialRange(double lower, double upper, Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.lowerBound = lower;
        this.upperBound = upper;
        this.paint = paint;
    }
    
    
    public double getLowerBound() {
        return this.lowerBound;
    }
    
    
    public void setLowerBound(double bound) {
        if (bound >= this.upperBound) {
            throw new IllegalArgumentException(
                    "Lower bound must be less than upper bound.");
        }
        this.lowerBound = bound;
        notifyListeners(new DialLayerChangeEvent(this));
    }
    
    
    public double getUpperBound() {
        return this.upperBound;
    }
    
    
    public void setUpperBound(double bound) {
        if (bound <= this.lowerBound) {
            throw new IllegalArgumentException(
                    "Lower bound must be less than upper bound.");
        }
        this.upperBound = bound;
        notifyListeners(new DialLayerChangeEvent(this));
    }
    
    
    public void setBounds(double lower, double upper) {
        if (lower >= upper) {
            throw new IllegalArgumentException(
                    "Lower must be less than upper.");
        }
        this.lowerBound = lower;
        this.upperBound = upper;
        notifyListeners(new DialLayerChangeEvent(this));
    }
        
    
    public Paint getPaint() {
        return this.paint;
    }
    
    
    public void setPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.paint = paint;
        notifyListeners(new DialLayerChangeEvent(this));
    }
    
    
    public double getInnerRadius() {
        return this.innerRadius;
    }
    
    
    public void setInnerRadius(double radius) {
        this.innerRadius = radius;
        notifyListeners(new DialLayerChangeEvent(this));
    }
    
    
    public double getOuterRadius() {
        return this.outerRadius;
    }
    
    
    public void setOuterRadius(double radius) {
        this.outerRadius = radius;
        notifyListeners(new DialLayerChangeEvent(this));
    }
    
    
    public boolean isClippedToWindow() {
        return true;
    }
    
    
    public void draw(Graphics2D g2, DialPlot plot, Rectangle2D frame, 
            Rectangle2D view) {
        
        Rectangle2D arcRectInner = DialPlot.rectangleByRadius(frame, 
                this.innerRadius, this.innerRadius);
        Rectangle2D arcRectOuter = DialPlot.rectangleByRadius(frame, 
                this.outerRadius, this.outerRadius);
        
        DialScale scale = plot.getScaleForDataset(0);
        double angleMin = scale.valueToAngle(this.lowerBound);
        double angleMax = scale.valueToAngle(this.upperBound);

        Arc2D arcInner = new Arc2D.Double(arcRectInner, angleMin, 
                angleMax - angleMin, Arc2D.OPEN);
        Arc2D arcOuter = new Arc2D.Double(arcRectOuter, angleMax, 
                angleMin - angleMax, Arc2D.OPEN);
        
        g2.setPaint(this.paint);
        g2.setStroke(new BasicStroke(2.0f));
        g2.draw(arcInner);
        g2.draw(arcOuter);
    }
    
    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof StandardDialRange)) {
            return false;
        }
        StandardDialRange that = (StandardDialRange) obj;
        if (this.lowerBound != that.lowerBound) {
            return false;
        }
        if (this.upperBound != that.upperBound) {
            return false;
        }
        if (!PaintUtilities.equal(this.paint, that.paint)) {
            return false;
        }
        if (this.innerRadius != that.innerRadius) {
            return false;
        }
        if (this.outerRadius != that.outerRadius) {
            return false;
        }
        return super.equals(obj); 
    }

    
    public int hashCode() {
        int result = 193;     
        long temp = Double.doubleToLongBits(this.lowerBound);
        result = 37 * result + (int) (temp ^ (temp >>> 32));        
        temp = Double.doubleToLongBits(this.upperBound);
        result = 37 * result + (int) (temp ^ (temp >>> 32));        
        temp = Double.doubleToLongBits(this.innerRadius);
        result = 37 * result + (int) (temp ^ (temp >>> 32));        
        temp = Double.doubleToLongBits(this.outerRadius);
        result = 37 * result + (int) (temp ^ (temp >>> 32));        
        result = 37 * result + HashUtilities.hashCodeForPaint(this.paint);
        return result;
    }
    
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.paint, stream);
    }

    
    private void readObject(ObjectInputStream stream) 
            throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.paint = SerialUtilities.readPaint(stream);
    }

}
