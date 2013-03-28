

package org.jfree.chart.annotations;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintUtilities;
import org.jfree.util.PublicCloneable;


public class XYBoxAnnotation extends AbstractXYAnnotation
        implements Cloneable, PublicCloneable, Serializable {

    
    private static final long serialVersionUID = 6764703772526757457L;

    
    private double x0;

    
    private double y0;

    
    private double x1;

    
    private double y1;

    
    private transient Stroke stroke;

    
    private transient Paint outlinePaint;

    
    private transient Paint fillPaint;

    
    public XYBoxAnnotation(double x0, double y0, double x1, double y1) {
        this(x0, y0, x1, y1, new BasicStroke(1.0f), Color.black);
    }

    
    public XYBoxAnnotation(double x0, double y0, double x1, double y1,
                           Stroke stroke, Paint outlinePaint) {
        this(x0, y0, x1, y1, stroke, outlinePaint, null);
    }

    
    public XYBoxAnnotation(double x0, double y0, double x1, double y1,
                           Stroke stroke, Paint outlinePaint, Paint fillPaint) {
        super();
        this.x0 = x0;
        this.y0 = y0;
        this.x1 = x1;
        this.y1 = y1;
        this.stroke = stroke;
        this.outlinePaint = outlinePaint;
        this.fillPaint = fillPaint;
    }

    
    public void draw(Graphics2D g2, XYPlot plot, Rectangle2D dataArea,
                     ValueAxis domainAxis, ValueAxis rangeAxis,
                     int rendererIndex, PlotRenderingInfo info) {

        PlotOrientation orientation = plot.getOrientation();
        RectangleEdge domainEdge = Plot.resolveDomainAxisLocation(
                plot.getDomainAxisLocation(), orientation);
        RectangleEdge rangeEdge = Plot.resolveRangeAxisLocation(
                plot.getRangeAxisLocation(), orientation);

        double transX0 = domainAxis.valueToJava2D(this.x0, dataArea,
                domainEdge);
        double transY0 = rangeAxis.valueToJava2D(this.y0, dataArea, rangeEdge);
        double transX1 = domainAxis.valueToJava2D(this.x1, dataArea,
                domainEdge);
        double transY1 = rangeAxis.valueToJava2D(this.y1, dataArea, rangeEdge);

        Rectangle2D box = null;
        if (orientation == PlotOrientation.HORIZONTAL) {
            box = new Rectangle2D.Double(transY0, transX1, transY1 - transY0,
                    transX0 - transX1);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            box = new Rectangle2D.Double(transX0, transY1, transX1 - transX0,
                    transY0 - transY1);
        }

        if (this.fillPaint != null) {
            g2.setPaint(this.fillPaint);
            g2.fill(box);
        }

        if (this.stroke != null && this.outlinePaint != null) {
            g2.setPaint(this.outlinePaint);
            g2.setStroke(this.stroke);
            g2.draw(box);
        }
        addEntity(info, box, rendererIndex, getToolTipText(), getURL());

    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof XYBoxAnnotation)) {
            return false;
        }
        XYBoxAnnotation that = (XYBoxAnnotation) obj;
        if (!(this.x0 == that.x0)) {
            return false;
        }
        if (!(this.y0 == that.y0)) {
            return false;
        }
        if (!(this.x1 == that.x1)) {
            return false;
        }
        if (!(this.y1 == that.y1)) {
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
        int result;
        long temp;
        temp = Double.doubleToLongBits(this.x0);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(this.x1);
        result = 29 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(this.y0);
        result = 29 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(this.y1);
        result = 29 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeStroke(this.stroke, stream);
        SerialUtilities.writePaint(this.outlinePaint, stream);
        SerialUtilities.writePaint(this.fillPaint, stream);
    }

    
    private void readObject(ObjectInputStream stream)
        throws IOException, ClassNotFoundException {

        stream.defaultReadObject();
        this.stroke = SerialUtilities.readStroke(stream);
        this.outlinePaint = SerialUtilities.readPaint(stream);
        this.fillPaint = SerialUtilities.readPaint(stream);
    }

}
