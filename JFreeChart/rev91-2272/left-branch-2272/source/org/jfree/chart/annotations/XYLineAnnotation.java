

package org.jfree.chart.annotations;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Line2D;
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
import org.jfree.chart.util.LineUtilities;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintUtilities;
import org.jfree.util.PublicCloneable;
import org.jfree.util.ShapeUtilities;


public class XYLineAnnotation extends AbstractXYAnnotation
        implements Cloneable, PublicCloneable, Serializable {

    
    private static final long serialVersionUID = -80535465244091334L;

    
    private double x1;

    
    private double y1;

    
    private double x2;

    
    private double y2;

    
    private transient Stroke stroke;

    
    private transient Paint paint;

    
    public XYLineAnnotation(double x1, double y1, double x2, double y2) {
        this(x1, y1, x2, y2, new BasicStroke(1.0f), Color.black);
    }

    
    public XYLineAnnotation(double x1, double y1, double x2, double y2,
                            Stroke stroke, Paint paint) {

        super();
        if (stroke == null) {
            throw new IllegalArgumentException("Null 'stroke' argument.");
        }
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.stroke = stroke;
        this.paint = paint;

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
        float j2DX1 = 0.0f;
        float j2DX2 = 0.0f;
        float j2DY1 = 0.0f;
        float j2DY2 = 0.0f;
        if (orientation == PlotOrientation.VERTICAL) {
            j2DX1 = (float) domainAxis.valueToJava2D(this.x1, dataArea,
                    domainEdge);
            j2DY1 = (float) rangeAxis.valueToJava2D(this.y1, dataArea,
                    rangeEdge);
            j2DX2 = (float) domainAxis.valueToJava2D(this.x2, dataArea,
                    domainEdge);
            j2DY2 = (float) rangeAxis.valueToJava2D(this.y2, dataArea,
                    rangeEdge);
        }
        else if (orientation == PlotOrientation.HORIZONTAL) {
            j2DY1 = (float) domainAxis.valueToJava2D(this.x1, dataArea,
                    domainEdge);
            j2DX1 = (float) rangeAxis.valueToJava2D(this.y1, dataArea,
                    rangeEdge);
            j2DY2 = (float) domainAxis.valueToJava2D(this.x2, dataArea,
                    domainEdge);
            j2DX2 = (float) rangeAxis.valueToJava2D(this.y2, dataArea,
                    rangeEdge);
        }
        g2.setPaint(this.paint);
        g2.setStroke(this.stroke);
        Line2D line = new Line2D.Float(j2DX1, j2DY1, j2DX2, j2DY2);
        
        
        boolean visible = LineUtilities.clipLine(line, dataArea);
        if (visible) {
            g2.draw(line);
        }

        String toolTip = getToolTipText();
        String url = getURL();
        if (toolTip != null || url != null) {
            addEntity(info, ShapeUtilities.createLineRegion(line, 1.0f),
                    rendererIndex, toolTip, url);
        }
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof XYLineAnnotation)) {
            return false;
        }
        XYLineAnnotation that = (XYLineAnnotation) obj;
        if (this.x1 != that.x1) {
            return false;
        }
        if (this.y1 != that.y1) {
            return false;
        }
        if (this.x2 != that.x2) {
            return false;
        }
        if (this.y2 != that.y2) {
            return false;
        }
        if (!PaintUtilities.equal(this.paint, that.paint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.stroke, that.stroke)) {
            return false;
        }
        
        return true;
    }

    
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(this.x1);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(this.x2);
        result = 29 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(this.y1);
        result = 29 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(this.y2);
        result = 29 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.paint, stream);
        SerialUtilities.writeStroke(this.stroke, stream);
    }

    
    private void readObject(ObjectInputStream stream)
        throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.paint = SerialUtilities.readPaint(stream);
        this.stroke = SerialUtilities.readStroke(stream);
    }

}
