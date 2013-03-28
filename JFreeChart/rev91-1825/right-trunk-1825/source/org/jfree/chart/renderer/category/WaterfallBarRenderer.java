

package org.jfree.chart.renderer.category;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.util.GradientPaintTransformType;
import org.jfree.chart.util.PaintUtilities;
import org.jfree.chart.util.RectangleEdge;
import org.jfree.chart.util.SerialUtilities;
import org.jfree.chart.util.StandardGradientPaintTransformer;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;


public class WaterfallBarRenderer extends BarRenderer {

    
    private static final long serialVersionUID = -2482910643727230911L;

    
    private transient Paint firstBarPaint;

    
    private transient Paint lastBarPaint;

    
    private transient Paint positiveBarPaint;

    
    private transient Paint negativeBarPaint;

    
    public WaterfallBarRenderer() {
        this(new GradientPaint(0.0f, 0.0f, new Color(0x22, 0x22, 0xFF),
                0.0f, 0.0f, new Color(0x66, 0x66, 0xFF)),
                new GradientPaint(0.0f, 0.0f, new Color(0x22, 0xFF, 0x22),
                0.0f, 0.0f, new Color(0x66, 0xFF, 0x66)),
                new GradientPaint(0.0f, 0.0f, new Color(0xFF, 0x22, 0x22),
                0.0f, 0.0f, new Color(0xFF, 0x66, 0x66)),
                new GradientPaint(0.0f, 0.0f, new Color(0xFF, 0xFF, 0x22),
                0.0f, 0.0f, new Color(0xFF, 0xFF, 0x66)));
    }

    
    public WaterfallBarRenderer(Paint firstBarPaint,
                                Paint positiveBarPaint,
                                Paint negativeBarPaint,
                                Paint lastBarPaint) {
        super();
        if (firstBarPaint == null) {
            throw new IllegalArgumentException("Null 'firstBarPaint' argument");
        }
        if (positiveBarPaint == null) {
            throw new IllegalArgumentException(
                    "Null 'positiveBarPaint' argument");
        }
        if (negativeBarPaint == null) {
            throw new IllegalArgumentException(
                    "Null 'negativeBarPaint' argument");
        }
        if (lastBarPaint == null) {
            throw new IllegalArgumentException("Null 'lastBarPaint' argument");
        }
        this.firstBarPaint = firstBarPaint;
        this.lastBarPaint = lastBarPaint;
        this.positiveBarPaint = positiveBarPaint;
        this.negativeBarPaint = negativeBarPaint;
        setGradientPaintTransformer(new StandardGradientPaintTransformer(
                GradientPaintTransformType.CENTER_VERTICAL));
        setMinimumBarLength(1.0);
    }

    
    public Paint getFirstBarPaint() {
        return this.firstBarPaint;
    }

    
    public void setFirstBarPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument");
        }
        this.firstBarPaint = paint;
        fireChangeEvent();
    }

    
    public Paint getLastBarPaint() {
        return this.lastBarPaint;
    }

    
    public void setLastBarPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument");
        }
        this.lastBarPaint = paint;
        fireChangeEvent();
    }

    
    public Paint getPositiveBarPaint() {
        return this.positiveBarPaint;
    }

    
    public void setPositiveBarPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument");
        }
        this.positiveBarPaint = paint;
        fireChangeEvent();
    }

    
    public Paint getNegativeBarPaint() {
        return this.negativeBarPaint;
    }

    
    public void setNegativeBarPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument");
        }
        this.negativeBarPaint = paint;
        fireChangeEvent();
    }

    
    public Range findRangeBounds(CategoryDataset dataset) {

        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }

        boolean allItemsNull = true; 
                                     
        double minimum = 0.0;
        double maximum = 0.0;
        int columnCount = dataset.getColumnCount();
        for (int row = 0; row < dataset.getRowCount(); row++) {
            double runningTotal = 0.0;
            for (int column = 0; column <= columnCount - 1; column++) {
                Number n = dataset.getValue(row, column);
                if (n != null) {
                    allItemsNull = false;
                    double value = n.doubleValue();
                    if (column == columnCount - 1) {
                        
                        runningTotal = value;
                    }
                    else {
                        runningTotal = runningTotal + value;
                    }
                    minimum = Math.min(minimum, runningTotal);
                    maximum = Math.max(maximum, runningTotal);
                }
            }

        }
        if (!allItemsNull) {
            return new Range(minimum, maximum);
        }
        else {
            return null;
        }

    }

    
    public void drawItem(Graphics2D g2,
                         CategoryItemRendererState state,
                         Rectangle2D dataArea,
                         CategoryPlot plot,
                         CategoryAxis domainAxis,
                         ValueAxis rangeAxis,
                         CategoryDataset dataset,
                         int row,
                         int column,
                         int pass) {

        double previous = state.getSeriesRunningTotal();
        if (column == dataset.getColumnCount() - 1) {
            previous = 0.0;
        }
        double current = 0.0;
        Number n = dataset.getValue(row, column);
        if (n != null) {
            current = previous + n.doubleValue();
        }
        state.setSeriesRunningTotal(current);

        int categoryCount = getColumnCount();
        PlotOrientation orientation = plot.getOrientation();

        double rectX = 0.0;
        double rectY = 0.0;

        RectangleEdge rangeAxisLocation = plot.getRangeAxisEdge();

        
        double j2dy0 = rangeAxis.valueToJava2D(previous, dataArea,
                rangeAxisLocation);

        
        double j2dy1 = rangeAxis.valueToJava2D(current, dataArea,
                rangeAxisLocation);

        double valDiff = current - previous;
        if (j2dy1 < j2dy0) {
            double temp = j2dy1;
            j2dy1 = j2dy0;
            j2dy0 = temp;
        }

        
        double rectWidth = state.getBarWidth();

        
        double rectHeight = Math.max(getMinimumBarLength(),
                Math.abs(j2dy1 - j2dy0));

        Comparable seriesKey = dataset.getRowKey(row);
        Comparable categoryKey = dataset.getColumnKey(column);
        if (orientation == PlotOrientation.HORIZONTAL) {
            rectY = domainAxis.getCategorySeriesMiddle(categoryKey, seriesKey,
                    dataset, getItemMargin(), dataArea, RectangleEdge.LEFT);

            rectX = j2dy0;
            rectHeight = state.getBarWidth();
            rectY = rectY - rectHeight / 2.0;
            rectWidth = Math.max(getMinimumBarLength(),
                    Math.abs(j2dy1 - j2dy0));

        }
        else if (orientation == PlotOrientation.VERTICAL) {
            rectX = domainAxis.getCategorySeriesMiddle(categoryKey, seriesKey,
                    dataset, getItemMargin(), dataArea, RectangleEdge.TOP);
            rectX = rectX - rectWidth / 2.0;
            rectY = j2dy0;
        }
        Rectangle2D bar = new Rectangle2D.Double(rectX, rectY, rectWidth,
                rectHeight);
        Paint seriesPaint = getFirstBarPaint();
        if (column == 0) {
            seriesPaint = getFirstBarPaint();
        }
        else if (column == categoryCount - 1) {
            seriesPaint = getLastBarPaint();
        }
        else {
            if (valDiff < 0.0) {
                seriesPaint = getNegativeBarPaint();
            }
            else if (valDiff > 0.0) {
                seriesPaint = getPositiveBarPaint();
            }
            else {
                seriesPaint = getLastBarPaint();
            }
        }
        if (getGradientPaintTransformer() != null
                && seriesPaint instanceof GradientPaint) {
            GradientPaint gp = (GradientPaint) seriesPaint;
            seriesPaint = getGradientPaintTransformer().transform(gp, bar);
        }
        g2.setPaint(seriesPaint);
        g2.fill(bar);

        
        if (isDrawBarOutline()
                && state.getBarWidth() > BAR_OUTLINE_WIDTH_THRESHOLD) {
            Stroke stroke = getItemOutlineStroke(row, column);
            Paint paint = getItemOutlinePaint(row, column);
            if (stroke != null && paint != null) {
                g2.setStroke(stroke);
                g2.setPaint(paint);
                g2.draw(bar);
            }
        }

        CategoryItemLabelGenerator generator
            = getItemLabelGenerator(row, column);
        if (generator != null && isItemLabelVisible(row, column)) {
            drawItemLabel(g2, dataset, row, column, plot, generator, bar,
                    (valDiff < 0.0));
        }

        
        EntityCollection entities = state.getEntityCollection();
        if (entities != null) {
            addItemEntity(entities, dataset, row, column, bar);
        }

    }

    
    public boolean equals(Object obj) {

        if (obj == this) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof WaterfallBarRenderer)) {
            return false;
        }
        WaterfallBarRenderer that = (WaterfallBarRenderer) obj;
        if (!PaintUtilities.equal(this.firstBarPaint, that.firstBarPaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.lastBarPaint, that.lastBarPaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.positiveBarPaint,
                that.positiveBarPaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.negativeBarPaint,
                that.negativeBarPaint)) {
            return false;
        }
        return true;

    }

    
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.firstBarPaint, stream);
        SerialUtilities.writePaint(this.lastBarPaint, stream);
        SerialUtilities.writePaint(this.positiveBarPaint, stream);
        SerialUtilities.writePaint(this.negativeBarPaint, stream);
    }

    
    private void readObject(ObjectInputStream stream)
        throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.firstBarPaint = SerialUtilities.readPaint(stream);
        this.lastBarPaint = SerialUtilities.readPaint(stream);
        this.positiveBarPaint = SerialUtilities.readPaint(stream);
        this.negativeBarPaint = SerialUtilities.readPaint(stream);
    }

}
