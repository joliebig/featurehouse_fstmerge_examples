

package org.jfree.chart.renderer.xy;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.text.TextUtilities;
import org.jfree.chart.util.ObjectUtilities;
import org.jfree.chart.util.PublicCloneable;
import org.jfree.chart.util.RectangleEdge;
import org.jfree.chart.util.ShapeUtilities;
import org.jfree.data.Range;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;


public class YIntervalRenderer extends AbstractXYItemRenderer
        implements XYItemRenderer, Cloneable, PublicCloneable, Serializable {

    
    private static final long serialVersionUID = -2951586537224143260L;

    
    private XYItemLabelGenerator additionalItemLabelGenerator;

    
    public YIntervalRenderer() {
        super();
        this.additionalItemLabelGenerator = null;
    }

    
    public XYItemLabelGenerator getAdditionalItemLabelGenerator() {
        return this.additionalItemLabelGenerator;
    }

    
    public void setAdditionalItemLabelGenerator(
            XYItemLabelGenerator generator) {
        this.additionalItemLabelGenerator = generator;
        fireChangeEvent();
    }

    
    public Range findRangeBounds(XYDataset dataset) {
        return findRangeBounds(dataset, true);
    }

    
    public void drawItem(Graphics2D g2,
                         XYItemRendererState state,
                         Rectangle2D dataArea,
                         PlotRenderingInfo info,
                         XYPlot plot,
                         ValueAxis domainAxis,
                         ValueAxis rangeAxis,
                         XYDataset dataset,
                         int series,
                         int item,
                         CrosshairState crosshairState,
                         int pass) {

        
        EntityCollection entities = null;
        if (info != null) {
            entities = info.getOwner().getEntityCollection();
        }

        IntervalXYDataset intervalDataset = (IntervalXYDataset) dataset;

        double x = intervalDataset.getXValue(series, item);
        double yLow   = intervalDataset.getStartYValue(series, item);
        double yHigh  = intervalDataset.getEndYValue(series, item);

        RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
        RectangleEdge yAxisLocation = plot.getRangeAxisEdge();

        double xx = domainAxis.valueToJava2D(x, dataArea, xAxisLocation);
        double yyLow = rangeAxis.valueToJava2D(yLow, dataArea, yAxisLocation);
        double yyHigh = rangeAxis.valueToJava2D(yHigh, dataArea, yAxisLocation);

        Paint p = getItemPaint(series, item);
        Stroke s = getItemStroke(series, item);

        Line2D line = null;
        Shape shape = getItemShape(series, item);
        Shape top = null;
        Shape bottom = null;
        PlotOrientation orientation = plot.getOrientation();
        if (orientation == PlotOrientation.HORIZONTAL) {
            line = new Line2D.Double(yyLow, xx, yyHigh, xx);
            top = ShapeUtilities.createTranslatedShape(shape, yyHigh, xx);
            bottom = ShapeUtilities.createTranslatedShape(shape, yyLow, xx);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            line = new Line2D.Double(xx, yyLow, xx, yyHigh);
            top = ShapeUtilities.createTranslatedShape(shape, xx, yyHigh);
            bottom = ShapeUtilities.createTranslatedShape(shape, xx, yyLow);
        }
        g2.setPaint(p);
        g2.setStroke(s);
        g2.draw(line);

        g2.fill(top);
        g2.fill(bottom);

        
        
        
        
        if (isItemLabelVisible(series, item)) {
            drawItemLabel(g2, orientation, dataset, series, item, xx, yyHigh,
                    false);
            drawAdditionalItemLabel(g2, orientation, dataset, series, item,
                    xx, yyLow);
        }

        
        if (entities != null) {
            addEntity(entities, line.getBounds(), dataset, series, item, 0.0,
                    0.0);
        }

    }

    
    private void drawAdditionalItemLabel(Graphics2D g2,
            PlotOrientation orientation, XYDataset dataset, int series,
            int item, double x, double y) {

        if (this.additionalItemLabelGenerator == null) {
            return;
        }

        Font labelFont = getItemLabelFont(series, item);
        Paint paint = getItemLabelPaint(series, item);
        g2.setFont(labelFont);
        g2.setPaint(paint);
        String label = this.additionalItemLabelGenerator.generateLabel(dataset,
                series, item);

        ItemLabelPosition position = getNegativeItemLabelPosition(series, item);
        Point2D anchorPoint = calculateLabelAnchorPoint(
                position.getItemLabelAnchor(), x, y, orientation);
        TextUtilities.drawRotatedString(label, g2,
                (float) anchorPoint.getX(), (float) anchorPoint.getY(),
                position.getTextAnchor(), position.getAngle(),
                position.getRotationAnchor());
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof YIntervalRenderer)) {
            return false;
        }
        YIntervalRenderer that = (YIntervalRenderer) obj;
        if (!ObjectUtilities.equal(this.additionalItemLabelGenerator,
                that.additionalItemLabelGenerator)) {
            return false;
        }
        return super.equals(obj);
    }

    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
