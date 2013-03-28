

package org.jfree.chart.plot.dial;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.jfree.chart.util.HashUtilities;
import org.jfree.chart.util.PaintUtilities;
import org.jfree.chart.util.PublicCloneable;
import org.jfree.chart.util.SerialUtilities;


public class DialCap extends AbstractDialLayer implements DialLayer, Cloneable,
        PublicCloneable, Serializable {

    
    static final long serialVersionUID = -2929484264982524463L;

    
    private double radius;

    
    private transient Paint fillPaint;

    
    private transient Paint outlinePaint;

    
    private transient Stroke outlineStroke;

    
    public DialCap() {
        this.radius = 0.05;
        this.fillPaint = Color.white;
        this.outlinePaint = Color.black;
        this.outlineStroke = new BasicStroke(2.0f);
    }

    
    public double getRadius() {
        return this.radius;
    }

    
    public void setRadius(double radius) {
        if (radius <= 0.0) {
            throw new IllegalArgumentException("Requires radius > 0.0.");
        }
        this.radius = radius;
        notifyListeners(new DialLayerChangeEvent(this));
    }

    
    public Paint getFillPaint() {
        return this.fillPaint;
    }

    
    public void setFillPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.fillPaint = paint;
        notifyListeners(new DialLayerChangeEvent(this));
    }

    
    public Paint getOutlinePaint() {
        return this.outlinePaint;
    }

    
    public void setOutlinePaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.outlinePaint = paint;
        notifyListeners(new DialLayerChangeEvent(this));
    }

    
    public Stroke getOutlineStroke() {
        return this.outlineStroke;
    }

    
    public void setOutlineStroke(Stroke stroke) {
        if (stroke == null) {
            throw new IllegalArgumentException("Null 'stroke' argument.");
        }
        this.outlineStroke = stroke;
        notifyListeners(new DialLayerChangeEvent(this));
    }

    
    public boolean isClippedToWindow() {
        return true;
    }

    
    public void draw(Graphics2D g2, DialPlot plot, Rectangle2D frame,
            Rectangle2D view) {

        g2.setPaint(this.fillPaint);

        Rectangle2D f = DialPlot.rectangleByRadius(frame, this.radius,
                this.radius);
        Ellipse2D e = new Ellipse2D.Double(f.getX(), f.getY(), f.getWidth(),
                f.getHeight());
        g2.fill(e);
        g2.setPaint(this.outlinePaint);
        g2.setStroke(this.outlineStroke);
        g2.draw(e);

    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DialCap)) {
            return false;
        }
        DialCap that = (DialCap) obj;
        if (this.radius != that.radius) {
            return false;
        }
        if (!PaintUtilities.equal(this.fillPaint, that.fillPaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.outlinePaint, that.outlinePaint)) {
            return false;
        }
        if (!this.outlineStroke.equals(that.outlineStroke)) {
            return false;
        }
        return super.equals(obj);
    }

    
    public int hashCode() {
        int result = 193;
        result = 37 * result + HashUtilities.hashCodeForPaint(this.fillPaint);
        result = 37 * result + HashUtilities.hashCodeForPaint(
                this.outlinePaint);
        result = 37 * result + this.outlineStroke.hashCode();
        return result;
    }

    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.fillPaint, stream);
        SerialUtilities.writePaint(this.outlinePaint, stream);
        SerialUtilities.writeStroke(this.outlineStroke, stream);
    }

    
    private void readObject(ObjectInputStream stream)
            throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.fillPaint = SerialUtilities.readPaint(stream);
        this.outlinePaint = SerialUtilities.readPaint(stream);
        this.outlineStroke = SerialUtilities.readStroke(stream);
    }

}

