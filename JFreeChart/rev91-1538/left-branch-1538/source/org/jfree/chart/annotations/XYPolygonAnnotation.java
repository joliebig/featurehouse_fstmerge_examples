

package org.jfree.chart.annotations;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;

import org.jfree.chart.HashUtilities;
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


public class XYPolygonAnnotation extends AbstractXYAnnotation
        implements Cloneable, PublicCloneable, Serializable {

    
    private static final long serialVersionUID = -6984203651995900036L;

    
    private double[] polygon;

    
    private transient Stroke stroke;

    
    private transient Paint outlinePaint;

    
    private transient Paint fillPaint;

    
    public XYPolygonAnnotation(double[] polygon) {
        this(polygon, new BasicStroke(1.0f), Color.black);
    }

    
    public XYPolygonAnnotation(double[] polygon,
                               Stroke stroke, Paint outlinePaint) {
        this(polygon, stroke, outlinePaint, null);
    }

    
    public XYPolygonAnnotation(double[] polygon,
                               Stroke stroke,
                               Paint outlinePaint, Paint fillPaint) {
        if (polygon == null) {
            throw new IllegalArgumentException("Null 'polygon' argument.");
        }
        if (polygon.length % 2 != 0) {
            throw new IllegalArgumentException("The 'polygon' array must "
                    + "contain an even number of items.");
        }
        this.polygon = (double[]) polygon.clone();
        this.stroke = stroke;
        this.outlinePaint = outlinePaint;
        this.fillPaint = fillPaint;
    }

    
    public double[] getPolygonCoordinates() {
        return (double[]) this.polygon.clone();
    }

    
    public Paint getFillPaint() {
        return this.fillPaint;
    }

    
    public Stroke getOutlineStroke() {
        return this.stroke;
    }

    
    public Paint getOutlinePaint() {
        return this.outlinePaint;
    }

    
    public void draw(Graphics2D g2, XYPlot plot, Rectangle2D dataArea,
                     ValueAxis domainAxis, ValueAxis rangeAxis,
                     int rendererIndex, PlotRenderingInfo info) {

        
        if (this.polygon.length < 4) {
            return;
        }
        PlotOrientation orientation = plot.getOrientation();
        RectangleEdge domainEdge = Plot.resolveDomainAxisLocation(
                plot.getDomainAxisLocation(), orientation);
        RectangleEdge rangeEdge = Plot.resolveRangeAxisLocation(
                plot.getRangeAxisLocation(), orientation);

        GeneralPath area = new GeneralPath();
        double x = domainAxis.valueToJava2D(this.polygon[0], dataArea,
                domainEdge);
        double y = rangeAxis.valueToJava2D(this.polygon[1], dataArea,
                rangeEdge);
        if (orientation == PlotOrientation.HORIZONTAL) {
            area.moveTo((float) y, (float) x);
            for (int i = 2; i < this.polygon.length; i += 2) {
                x = domainAxis.valueToJava2D(this.polygon[i], dataArea,
                        domainEdge);
                y = rangeAxis.valueToJava2D(this.polygon[i + 1], dataArea,
                        rangeEdge);
                area.lineTo((float) y, (float) x);
            }
            area.closePath();
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            area.moveTo((float) x, (float) y);
            for (int i = 2; i < this.polygon.length; i += 2) {
                x = domainAxis.valueToJava2D(this.polygon[i], dataArea,
                        domainEdge);
                y = rangeAxis.valueToJava2D(this.polygon[i + 1], dataArea,
                        rangeEdge);
                area.lineTo((float) x, (float) y);
            }
            area.closePath();
       }


        if (this.fillPaint != null) {
            g2.setPaint(this.fillPaint);
            g2.fill(area);
        }

        if (this.stroke != null && this.outlinePaint != null) {
            g2.setPaint(this.outlinePaint);
            g2.setStroke(this.stroke);
            g2.draw(area);
        }
        addEntity(info, area, rendererIndex, getToolTipText(), getURL());

    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof XYPolygonAnnotation)) {
            return false;
        }
        XYPolygonAnnotation that = (XYPolygonAnnotation) obj;
        if (!Arrays.equals(this.polygon, that.polygon)) {
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
        result = 37 * result + HashUtilities.hashCodeForDoubleArray(
                this.polygon);
        result = 37 * result + HashUtilities.hashCodeForPaint(this.fillPaint);
        result = 37 * result + HashUtilities.hashCodeForPaint(
                this.outlinePaint);
        if (this.stroke != null) {
            result = 37 * result + this.stroke.hashCode();
        }
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
