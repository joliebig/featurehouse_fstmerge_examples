
 
package org.jfree.chart.annotations;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.jfree.chart.HashUtilities;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.util.ObjectUtilities;
import org.jfree.chart.util.PaintUtilities;
import org.jfree.chart.util.PublicCloneable;
import org.jfree.chart.util.RectangleEdge;
import org.jfree.chart.util.SerialUtilities;


public class XYShapeAnnotation extends AbstractXYAnnotation
                               implements Cloneable, PublicCloneable, 
                                          Serializable {
    
    
    private static final long serialVersionUID = -8553218317600684041L;
    
    
    private transient Shape shape;

    
    private transient Stroke stroke;

    
    private transient Paint outlinePaint;
    
    
    private transient Paint fillPaint;

    
    public XYShapeAnnotation(Shape shape) {
        this(shape, new BasicStroke(1.0f), Color.black);
    }
    
    
    public XYShapeAnnotation(Shape shape, Stroke stroke, Paint outlinePaint) {
        this(shape, stroke, outlinePaint, null);
    }

    
    public XYShapeAnnotation(Shape shape, Stroke stroke, Paint outlinePaint, 
                             Paint fillPaint) {
        if (shape == null) {
            throw new IllegalArgumentException("Null 'shape' argument.");   
        }
        this.shape = shape;
        this.stroke = stroke;
        this.outlinePaint = outlinePaint;
        this.fillPaint = fillPaint;
    }

    
    public void draw(Graphics2D g2, XYPlot plot, Rectangle2D dataArea,
                     ValueAxis domainAxis, ValueAxis rangeAxis, 
                     int rendererIndex,
                     PlotRenderingInfo info) {

        PlotOrientation orientation = plot.getOrientation();
        RectangleEdge domainEdge = Plot.resolveDomainAxisLocation(
                plot.getDomainAxisLocation(), orientation);
        RectangleEdge rangeEdge = Plot.resolveRangeAxisLocation(
                plot.getRangeAxisLocation(), orientation);

        
        
        Rectangle2D bounds = this.shape.getBounds2D();
        double x0 = bounds.getMinX();
        double x1 = bounds.getMaxX();
        double xx0 = domainAxis.valueToJava2D(x0, dataArea, domainEdge);
        double xx1 = domainAxis.valueToJava2D(x1, dataArea, domainEdge);
        double m00 = (xx1 - xx0) / (x1 - x0);
        double m02 = xx0 - x0 * m00;
        
        double y0 = bounds.getMaxY();
        double y1 = bounds.getMinY();
        double yy0 = rangeAxis.valueToJava2D(y0, dataArea, rangeEdge);
        double yy1 = rangeAxis.valueToJava2D(y1, dataArea, rangeEdge);
        double m11 = (yy1 - yy0) / (y1 - y0);
        double m12 = yy0 - m11 * y0;

        
        Shape s = null;
        if (orientation == PlotOrientation.HORIZONTAL) {
            AffineTransform t1 = new AffineTransform(0.0f, 1.0f, 1.0f, 0.0f, 
                    0.0f, 0.0f);
            AffineTransform t2 = new AffineTransform(m11, 0.0f, 0.0f, m00, 
                    m12, m02);
            s = t1.createTransformedShape(this.shape);
            s = t2.createTransformedShape(s);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            AffineTransform t = new AffineTransform(m00, 0, 0, m11, m02, m12);
            s = t.createTransformedShape(this.shape);
        }

        if (this.fillPaint != null) {
            g2.setPaint(this.fillPaint);
            g2.fill(s);
        }
        
        if (this.stroke != null && this.outlinePaint != null) {
            g2.setPaint(this.outlinePaint);
            g2.setStroke(this.stroke);
            g2.draw(s);
        }
        addEntity(info, s, rendererIndex, getToolTipText(), getURL());
        
    }
        
    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof XYShapeAnnotation)) {
            return false;
        }
        XYShapeAnnotation that = (XYShapeAnnotation) obj;
        if (!this.shape.equals(that.shape)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.stroke, that.stroke)) {
            return false;
        }
        if (!PaintUtilities.equal(this.outlinePaint, that.outlinePaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.fillPaint, that.fillPaint)) {
            return false;
        }
        
        return true;
    }
    
    
    public int hashCode() {
        int result = 193;
        result = 37 * result + this.shape.hashCode();
        if (this.stroke != null) {
            result = 37 * result + this.stroke.hashCode();
        }
        result = 37 * result + HashUtilities.hashCodeForPaint(
                this.outlinePaint);
        result = 37 * result + HashUtilities.hashCodeForPaint(this.fillPaint);
        return result;
    }
    
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeShape(this.shape, stream);
        SerialUtilities.writeStroke(this.stroke, stream);
        SerialUtilities.writePaint(this.outlinePaint, stream);
        SerialUtilities.writePaint(this.fillPaint, stream);
    }

    
    private void readObject(ObjectInputStream stream) 
        throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.shape = SerialUtilities.readShape(stream);
        this.stroke = SerialUtilities.readStroke(stream);
        this.outlinePaint = SerialUtilities.readPaint(stream);
        this.fillPaint = SerialUtilities.readPaint(stream);
    }

}
