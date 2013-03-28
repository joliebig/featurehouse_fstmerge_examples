

package org.jfree.chart.renderer.xy;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.LookupPaintScale;
import org.jfree.chart.renderer.PaintScale;
import org.jfree.data.Range;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYZDataset;
import org.jfree.ui.RectangleAnchor;
import org.jfree.util.PublicCloneable;


public class XYBlockRenderer extends AbstractXYItemRenderer
        implements XYItemRenderer, Cloneable, PublicCloneable, Serializable {

    
    private double blockWidth = 1.0;

    
    private double blockHeight = 1.0;

    
    private RectangleAnchor blockAnchor = RectangleAnchor.CENTER;

    
    private double xOffset;

    
    private double yOffset;

    
    private PaintScale paintScale;

    
    public XYBlockRenderer() {
        updateOffsets();
        this.paintScale = new LookupPaintScale();
    }

    
    public double getBlockWidth() {
        return this.blockWidth;
    }

    
    public void setBlockWidth(double width) {
        if (width <= 0.0) {
            throw new IllegalArgumentException(
                    "The 'width' argument must be > 0.0");
        }
        this.blockWidth = width;
        updateOffsets();
        fireChangeEvent();
    }

    
    public double getBlockHeight() {
        return this.blockHeight;
    }

    
    public void setBlockHeight(double height) {
        if (height <= 0.0) {
            throw new IllegalArgumentException(
                    "The 'height' argument must be > 0.0");
        }
        this.blockHeight = height;
        updateOffsets();
        fireChangeEvent();
    }

    
    public RectangleAnchor getBlockAnchor() {
        return this.blockAnchor;
    }

    
    public void setBlockAnchor(RectangleAnchor anchor) {
        if (anchor == null) {
            throw new IllegalArgumentException("Null 'anchor' argument.");
        }
        if (this.blockAnchor.equals(anchor)) {
            return;  
        }
        this.blockAnchor = anchor;
        updateOffsets();
        fireChangeEvent();
    }

    
    public PaintScale getPaintScale() {
        return this.paintScale;
    }

    
    public void setPaintScale(PaintScale scale) {
        if (scale == null) {
            throw new IllegalArgumentException("Null 'scale' argument.");
        }
        this.paintScale = scale;
        fireChangeEvent();
    }

    
    private void updateOffsets() {
        if (this.blockAnchor.equals(RectangleAnchor.BOTTOM_LEFT)) {
            this.xOffset = 0.0;
            this.yOffset = 0.0;
        }
        else if (this.blockAnchor.equals(RectangleAnchor.BOTTOM)) {
            this.xOffset = -this.blockWidth / 2.0;
            this.yOffset = 0.0;
        }
        else if (this.blockAnchor.equals(RectangleAnchor.BOTTOM_RIGHT)) {
            this.xOffset = -this.blockWidth;
            this.yOffset = 0.0;
        }
        else if (this.blockAnchor.equals(RectangleAnchor.LEFT)) {
            this.xOffset = 0.0;
            this.yOffset = -this.blockHeight / 2.0;
        }
        else if (this.blockAnchor.equals(RectangleAnchor.CENTER)) {
            this.xOffset = -this.blockWidth / 2.0;
            this.yOffset = -this.blockHeight / 2.0;
        }
        else if (this.blockAnchor.equals(RectangleAnchor.RIGHT)) {
            this.xOffset = -this.blockWidth;
            this.yOffset = -this.blockHeight / 2.0;
        }
        else if (this.blockAnchor.equals(RectangleAnchor.TOP_LEFT)) {
            this.xOffset = 0.0;
            this.yOffset = -this.blockHeight;
        }
        else if (this.blockAnchor.equals(RectangleAnchor.TOP)) {
            this.xOffset = -this.blockWidth / 2.0;
            this.yOffset = -this.blockHeight;
        }
        else if (this.blockAnchor.equals(RectangleAnchor.TOP_RIGHT)) {
            this.xOffset = -this.blockWidth;
            this.yOffset = -this.blockHeight;
        }
    }

    
    public Range findDomainBounds(XYDataset dataset) {
        if (dataset != null) {
            Range r = DatasetUtilities.findDomainBounds(dataset, false);
            if (r == null) {
                return null;
            }
            else {
                return new Range(r.getLowerBound() + this.xOffset,
                        r.getUpperBound() + this.blockWidth + this.xOffset);
            }
        }
        else {
            return null;
        }
    }

    
    public Range findRangeBounds(XYDataset dataset) {
        if (dataset != null) {
            Range r = DatasetUtilities.findRangeBounds(dataset, false);
            if (r == null) {
                return null;
            }
            else {
                return new Range(r.getLowerBound() + this.yOffset,
                        r.getUpperBound() + this.blockHeight + this.yOffset);
            }
        }
        else {
            return null;
        }
    }

    
    public void drawItem(Graphics2D g2, XYItemRendererState state,
            Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot,
            ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset,
            int series, int item, CrosshairState crosshairState, int pass) {

        double x = dataset.getXValue(series, item);
        double y = dataset.getYValue(series, item);
        double z = 0.0;
        if (dataset instanceof XYZDataset) {
            z = ((XYZDataset) dataset).getZValue(series, item);
        }
        Paint p = this.paintScale.getPaint(z);
        double xx0 = domainAxis.valueToJava2D(x + this.xOffset, dataArea,
                plot.getDomainAxisEdge());
        double yy0 = rangeAxis.valueToJava2D(y + this.yOffset, dataArea,
                plot.getRangeAxisEdge());
        double xx1 = domainAxis.valueToJava2D(x + this.blockWidth
                + this.xOffset, dataArea, plot.getDomainAxisEdge());
        double yy1 = rangeAxis.valueToJava2D(y + this.blockHeight
                + this.yOffset, dataArea, plot.getRangeAxisEdge());
        Rectangle2D block;
        PlotOrientation orientation = plot.getOrientation();
        if (orientation.equals(PlotOrientation.HORIZONTAL)) {
            block = new Rectangle2D.Double(Math.min(yy0, yy1),
                    Math.min(xx0, xx1), Math.abs(yy1 - yy0),
                    Math.abs(xx0 - xx1));
        }
        else {
            block = new Rectangle2D.Double(Math.min(xx0, xx1),
                    Math.min(yy0, yy1), Math.abs(xx1 - xx0),
                    Math.abs(yy1 - yy0));
        }
        g2.setPaint(p);
        g2.fill(block);
        g2.setStroke(new BasicStroke(1.0f));
        g2.draw(block);

        EntityCollection entities = state.getEntityCollection();
        if (entities != null) {
            addEntity(entities, block, dataset, series, item, 0.0, 0.0);
        }

    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYBlockRenderer)) {
            return false;
        }
        XYBlockRenderer that = (XYBlockRenderer) obj;
        if (this.blockHeight != that.blockHeight) {
            return false;
        }
        if (this.blockWidth != that.blockWidth) {
            return false;
        }
        if (!this.blockAnchor.equals(that.blockAnchor)) {
            return false;
        }
        if (!this.paintScale.equals(that.paintScale)) {
            return false;
        }
        return super.equals(obj);
    }

    
    public Object clone() throws CloneNotSupportedException {
        XYBlockRenderer clone = (XYBlockRenderer) super.clone();
        if (this.paintScale instanceof PublicCloneable) {
            PublicCloneable pc = (PublicCloneable) this.paintScale;
            clone.paintScale = (PaintScale) pc.clone();
        }
        return clone;
    }

}
