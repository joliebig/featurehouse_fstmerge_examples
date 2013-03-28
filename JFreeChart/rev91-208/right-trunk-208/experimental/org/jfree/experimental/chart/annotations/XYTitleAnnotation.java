

package org.jfree.experimental.chart.annotations;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.annotations.AbstractXYAnnotation;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.block.BlockParams;
import org.jfree.chart.block.EntityBlockResult;
import org.jfree.chart.block.RectangleConstraint;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.Title;
import org.jfree.chart.util.ObjectUtilities;
import org.jfree.chart.util.PublicCloneable;
import org.jfree.chart.util.RectangleAnchor;
import org.jfree.chart.util.RectangleEdge;
import org.jfree.chart.util.Size2D;
import org.jfree.data.Range;
import org.jfree.experimental.chart.util.XYCoordinateType;


public class XYTitleAnnotation extends AbstractXYAnnotation
                               implements Cloneable, PublicCloneable, 
                                          Serializable {

    
    private static final long serialVersionUID = -4364694501921559958L;
    
    
    private XYCoordinateType coordinateType;
    
    
    private double x;

    
    private double y;
    
    private double maxWidth;
    
    private double maxHeight;

    
    private Title title;

    
    private RectangleAnchor anchor;
    
    
    public XYTitleAnnotation(double x, double y, Title title) {
        this(x, y, title, RectangleAnchor.CENTER);
    }
    
    
    public XYTitleAnnotation(double x, double y, Title title, 
            RectangleAnchor anchor) {
        if (title == null) {
            throw new IllegalArgumentException("Null 'title' argument.");      
        }
        if (anchor == null) {
            throw new IllegalArgumentException("Null 'anchor' argument.");
        }
        this.coordinateType = XYCoordinateType.RELATIVE;
        this.x = x;
        this.y = y;
        this.maxWidth = 0.0;
        this.maxHeight = 0.0;
        this.title = title;
        this.anchor = anchor;
    }    
    
    
    public XYCoordinateType getCoordinateType() {
        return this.coordinateType;
    }
    
    
    public double getX() {
        return this.x;
    }
    
    
    public double getY() {
        return this.y;
    }
    
    
    public Title getTitle() {
        return this.title;
    }
    
    
    public RectangleAnchor getTitleAnchor() {
        return this.anchor;
    }
    
    
    public double getMaxWidth() {
        return this.maxWidth;
    }
    
    
    public void setMaxWidth(double max) {
        this.maxWidth = max;
    }
    
    
    public double getMaxHeight() {
        return this.maxHeight;
    }
    
    
    public void setMaxHeight(double max) {
        this.maxHeight = max;
    }

    
    public void draw(Graphics2D g2, XYPlot plot, Rectangle2D dataArea,
                     ValueAxis domainAxis, ValueAxis rangeAxis, 
                     int rendererIndex,
                     PlotRenderingInfo info) {

        PlotOrientation orientation = plot.getOrientation();
        AxisLocation domainAxisLocation = plot.getDomainAxisLocation();
        AxisLocation rangeAxisLocation = plot.getRangeAxisLocation();
        RectangleEdge domainEdge = Plot.resolveDomainAxisLocation(
                domainAxisLocation, orientation);
        RectangleEdge rangeEdge = Plot.resolveRangeAxisLocation(
                rangeAxisLocation, orientation);
        Range xRange = domainAxis.getRange();
        Range yRange = rangeAxis.getRange();
        double anchorX = 0.0;
        double anchorY = 0.0;
        if (this.coordinateType == XYCoordinateType.RELATIVE) {
            anchorX = xRange.getLowerBound() + (this.x * xRange.getLength());
            anchorY = yRange.getLowerBound() + (this.y * yRange.getLength());
        }
        else {
            anchorX = domainAxis.valueToJava2D(this.x, dataArea, domainEdge);
            anchorY = rangeAxis.valueToJava2D(this.y, dataArea, rangeEdge);
        }
        
        float j2DX = (float) domainAxis.valueToJava2D(anchorX, dataArea, 
                domainEdge);
        float j2DY = (float) rangeAxis.valueToJava2D(anchorY, dataArea, 
                rangeEdge);
        float xx = 0.0f;
        float yy = 0.0f;
        if (orientation == PlotOrientation.HORIZONTAL) {
            xx = j2DY;
            yy = j2DX;
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            xx = j2DX;
            yy = j2DY;
        }
        
        double maxW = dataArea.getWidth();
        double maxH = dataArea.getHeight();
        if (this.coordinateType == XYCoordinateType.RELATIVE) {
            if (this.maxWidth > 0.0) {
                maxW = maxW * this.maxWidth;
            }
            if (this.maxHeight > 0.0) {
                maxH = maxH * this.maxHeight;
            }
        }
        if (this.coordinateType == XYCoordinateType.DATA) {
            maxW = this.maxWidth;
            maxH = this.maxHeight;
        }
        RectangleConstraint rc = new RectangleConstraint(
                new Range(0, maxW), new Range(0, maxH));
        
        Size2D size = this.title.arrange(g2, rc);
        Rectangle2D titleRect = new Rectangle2D.Double(0, 0, size.width, 
                size.height);
        Point2D anchorPoint = RectangleAnchor.coordinates(titleRect, 
                this.anchor);
        xx = xx - (float) anchorPoint.getX();
        yy = yy - (float) anchorPoint.getY();
        titleRect.setRect(xx, yy, titleRect.getWidth(), titleRect.getHeight());
        BlockParams p = new BlockParams();
        if (info != null) {
            if (info.getOwner().getEntityCollection() != null) {
                p.setGenerateEntities(true);
            }
        }
        Object result = this.title.draw(g2, titleRect, p);
        if (result instanceof EntityBlockResult) {
            EntityBlockResult ebr = (EntityBlockResult) result;
            info.getOwner().getEntityCollection().addAll(
                    ebr.getEntityCollection());
        }
        String toolTip = getToolTipText();
        String url = getURL();
        if (toolTip != null || url != null) {
            addEntity(info, new Rectangle2D.Float(xx, yy, (float) size.width, 
                    (float) size.height), rendererIndex, toolTip, url);
        }
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYTitleAnnotation)) {
            return false;
        }
        XYTitleAnnotation that = (XYTitleAnnotation) obj;
        if (this.coordinateType != that.coordinateType) {
            return false;
        }
        if (this.x != that.x) {
            return false;
        }
        if (this.y != that.y) {
            return false;
        }
        if (this.maxWidth != that.maxWidth) {
            return false;
        }
        if (this.maxHeight != that.maxHeight) {
            return false;
        }
        if (!ObjectUtilities.equal(this.title, that.title)) {
            return false;
        }
        if (!this.anchor.equals(that.anchor)) {
            return false;
        }
        return super.equals(obj);
    }
    
    
    public int hashCode() {
        
        return this.title.hashCode();
    }
    
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
