

package org.jfree.chart.annotations;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;


public class XYDataImageAnnotation extends AbstractXYAnnotation
        implements Cloneable, PublicCloneable {

    
    private transient Image image;

    
    private double x;

    
    private double y;

    
    private double w;

    
    private double h;

    
    public XYDataImageAnnotation(Image image, double x, double y, double w,
            double h) {
        if (image == null) {
            throw new IllegalArgumentException("Null 'image' argument.");
        }
        this.image = image;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    
    public Image getImage() {
        return this.image;
    }

    
    public double getX() {
        return this.x;
    }

    
    public double getY() {
        return this.y;
    }

    
    public double getWidth() {
        return this.w;
    }

    
    public double getHeight() {
        return this.h;
    }

    
    public void draw(Graphics2D g2, XYPlot plot, Rectangle2D dataArea,
                     ValueAxis domainAxis, ValueAxis rangeAxis,
                     int rendererIndex,
                     PlotRenderingInfo info) {

        PlotOrientation orientation = plot.getOrientation();
        AxisLocation xAxisLocation = plot.getDomainAxisLocation();
        AxisLocation yAxisLocation = plot.getRangeAxisLocation();
        RectangleEdge xEdge = Plot.resolveDomainAxisLocation(xAxisLocation,
                orientation);
        RectangleEdge yEdge = Plot.resolveRangeAxisLocation(yAxisLocation,
                orientation);
        float j2DX0 = (float) domainAxis.valueToJava2D(this.x, dataArea, xEdge);
        float j2DY0 = (float) rangeAxis.valueToJava2D(this.y, dataArea, yEdge);
        float j2DX1 = (float) domainAxis.valueToJava2D(this.x + this.w,
                dataArea, xEdge);
        float j2DY1 = (float) rangeAxis.valueToJava2D(this.y + this.h,
                dataArea, yEdge);
        float xx0 = 0.0f;
        float yy0 = 0.0f;
        float xx1 = 0.0f;
        float yy1 = 0.0f;
        if (orientation == PlotOrientation.HORIZONTAL) {
            xx0 = j2DY0;
            xx1 = j2DY1;
            yy0 = j2DX0;
            yy1 = j2DX1;
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            xx0 = j2DX0;
            xx1 = j2DX1;
            yy0 = j2DY0;
            yy1 = j2DY1;
        }
        
        g2.drawImage(this.image, (int) xx0, (int) Math.min(yy0, yy1),
                (int) (xx1 - xx0), (int) Math.abs(yy1 - yy0), null);
        String toolTip = getToolTipText();
        String url = getURL();
        if (toolTip != null || url != null) {
            addEntity(info, new Rectangle2D.Float(xx0, yy0, (xx1 - xx0),
                    (yy1 - yy0)), rendererIndex, toolTip, url);
        }
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof XYDataImageAnnotation)) {
            return false;
        }
        XYDataImageAnnotation that = (XYDataImageAnnotation) obj;
        if (this.x != that.x) {
            return false;
        }
        if (this.y != that.y) {
            return false;
        }
        if (this.w != that.w) {
            return false;
        }
        if (this.h != that.h) {
            return false;
        }
        if (!ObjectUtilities.equal(this.image, that.image)) {
            return false;
        }
        
        return true;
    }

    
    public int hashCode() {
        return this.image.hashCode();
    }

    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        
        
    }

    
    private void readObject(ObjectInputStream stream)
        throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        
        
    }

}
