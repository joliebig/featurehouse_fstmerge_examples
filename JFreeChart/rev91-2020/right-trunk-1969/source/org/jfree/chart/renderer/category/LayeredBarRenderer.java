

package org.jfree.chart.renderer.category;

import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.util.GradientPaintTransformer;
import org.jfree.chart.util.ObjectList;
import org.jfree.chart.util.RectangleEdge;
import org.jfree.data.category.CategoryDataset;


public class LayeredBarRenderer extends BarRenderer implements Serializable {

    
    private static final long serialVersionUID = -8716572894780469487L;

    
    protected ObjectList seriesBarWidthList;

    
    public LayeredBarRenderer() {
        super();
        this.seriesBarWidthList = new ObjectList();
    }

    
    public double getSeriesBarWidth(int series) {
        double result = Double.NaN;
        Number n = (Number) this.seriesBarWidthList.get(series);
        if (n != null) {
            result = n.doubleValue();
        }
        return result;
    }

    
    public void setSeriesBarWidth(int series, double width) {
        this.seriesBarWidthList.set(series, new Double(width));
    }

    
    protected void calculateBarWidth(CategoryPlot plot,
                                     Rectangle2D dataArea,
                                     int rendererIndex,
                                     CategoryItemRendererState state) {

        
        
        
        
        CategoryAxis domainAxis = getDomainAxis(plot, rendererIndex);
        CategoryDataset dataset = plot.getDataset(rendererIndex);
        if (dataset != null) {
            int columns = dataset.getColumnCount();
            int rows = dataset.getRowCount();
            double space = 0.0;
            PlotOrientation orientation = plot.getOrientation();
            if (orientation == PlotOrientation.HORIZONTAL) {
                space = dataArea.getHeight();
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                space = dataArea.getWidth();
            }
            double maxWidth = space * getMaximumBarWidth();
            double categoryMargin = 0.0;
            if (columns > 1) {
                categoryMargin = domainAxis.getCategoryMargin();
            }
            double used = space * (1 - domainAxis.getLowerMargin()
                - domainAxis.getUpperMargin() - categoryMargin);
            if ((rows * columns) > 0) {
                state.setBarWidth(Math.min(used / (dataset.getColumnCount()),
                        maxWidth));
            }
            else {
                state.setBarWidth(Math.min(used, maxWidth));
            }
        }
    }

    
    public void drawItem(Graphics2D g2,
                         CategoryItemRendererState state,
                         Rectangle2D dataArea,
                         CategoryPlot plot,
                         CategoryAxis domainAxis,
                         ValueAxis rangeAxis,
                         CategoryDataset data,
                         int row,
                         int column,
                         int pass) {

        PlotOrientation orientation = plot.getOrientation();
        if (orientation == PlotOrientation.HORIZONTAL) {
            drawHorizontalItem(g2, state, dataArea, plot, domainAxis, rangeAxis,
                    data, row, column);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            drawVerticalItem(g2, state, dataArea, plot, domainAxis, rangeAxis,
                    data, row, column);
        }

    }

    
    protected void drawHorizontalItem(Graphics2D g2,
                                      CategoryItemRendererState state,
                                      Rectangle2D dataArea,
                                      CategoryPlot plot,
                                      CategoryAxis domainAxis,
                                      ValueAxis rangeAxis,
                                      CategoryDataset dataset,
                                      int row,
                                      int column) {

        
        Number dataValue = dataset.getValue(row, column);
        if (dataValue == null) {
            return;
        }

        
        double value = dataValue.doubleValue();
        double base = 0.0;
        double lclip = getLowerClip();
        double uclip = getUpperClip();
        if (uclip <= 0.0) {  
            if (value >= uclip) {
                return; 
            }
            base = uclip;
            if (value <= lclip) {
                value = lclip;
            }
        }
        else if (lclip <= 0.0) { 
            if (value >= uclip) {
                value = uclip;
            }
            else {
                if (value <= lclip) {
                    value = lclip;
                }
            }
        }
        else { 
            if (value <= lclip) {
                return; 
            }
            base = lclip;
            if (value >= uclip) {
                value = uclip;
            }
        }

        RectangleEdge edge = plot.getRangeAxisEdge();
        double transX1 = rangeAxis.valueToJava2D(base, dataArea, edge);
        double transX2 = rangeAxis.valueToJava2D(value, dataArea, edge);
        double rectX = Math.min(transX1, transX2);
        double rectWidth = Math.abs(transX2 - transX1);

        
        double rectY = domainAxis.getCategoryMiddle(column, getColumnCount(),
                dataArea, plot.getDomainAxisEdge()) - state.getBarWidth() / 2.0;

        int seriesCount = getRowCount();

        
        double shift = 0.0;
        double rectHeight = 0.0;
        double widthFactor = 1.0;
        double seriesBarWidth = getSeriesBarWidth(row);
        if (!Double.isNaN(seriesBarWidth)) {
            widthFactor = seriesBarWidth;
        }
        rectHeight = widthFactor * state.getBarWidth();
        rectY = rectY + (1 - widthFactor) * state.getBarWidth() / 2.0;
        if (seriesCount > 1) {
            shift = rectHeight * 0.20 / (seriesCount - 1);
        }

        Rectangle2D bar = new Rectangle2D.Double(rectX,
                (rectY + ((seriesCount - 1 - row) * shift)), rectWidth,
                (rectHeight - (seriesCount - 1 - row) * shift * 2));

        Paint itemPaint = getItemPaint(row, column);
        GradientPaintTransformer t = getGradientPaintTransformer();
        if (t != null && itemPaint instanceof GradientPaint) {
            itemPaint = t.transform((GradientPaint) itemPaint, bar);
        }
        g2.setPaint(itemPaint);
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
                    (transX1 > transX2));
        }

        
        EntityCollection entities = state.getEntityCollection();
        if (entities != null) {
            addItemEntity(entities, dataset, row, column, bar);
        }
    }

    
    protected void drawVerticalItem(Graphics2D g2,
                                    CategoryItemRendererState state,
                                    Rectangle2D dataArea,
                                    CategoryPlot plot,
                                    CategoryAxis domainAxis,
                                    ValueAxis rangeAxis,
                                    CategoryDataset dataset,
                                    int row,
                                    int column) {

        
        Number dataValue = dataset.getValue(row, column);
        if (dataValue == null) {
            return;
        }

        
        double rectX = domainAxis.getCategoryMiddle(column, getColumnCount(),
                dataArea, plot.getDomainAxisEdge()) - state.getBarWidth() / 2.0;

        int seriesCount = getRowCount();

        
        double value = dataValue.doubleValue();
        double base = 0.0;
        double lclip = getLowerClip();
        double uclip = getUpperClip();

        if (uclip <= 0.0) {  
            if (value >= uclip) {
                return; 
            }
            base = uclip;
            if (value <= lclip) {
                value = lclip;
            }
        }
        else if (lclip <= 0.0) { 
            if (value >= uclip) {
                value = uclip;
            }
            else {
                if (value <= lclip) {
                    value = lclip;
                }
            }
        }
        else { 
            if (value <= lclip) {
                return; 
            }
            base = getLowerClip();
            if (value >= uclip) {
               value = uclip;
            }
        }

        RectangleEdge edge = plot.getRangeAxisEdge();
        double transY1 = rangeAxis.valueToJava2D(base, dataArea, edge);
        double transY2 = rangeAxis.valueToJava2D(value, dataArea, edge);
        double rectY = Math.min(transY2, transY1);

        double rectWidth = state.getBarWidth();
        double rectHeight = Math.abs(transY2 - transY1);

        
        double shift = 0.0;
        rectWidth = 0.0;
        double widthFactor = 1.0;
        double seriesBarWidth = getSeriesBarWidth(row);
        if (!Double.isNaN(seriesBarWidth)) {
            widthFactor = seriesBarWidth;
        }
        rectWidth = widthFactor * state.getBarWidth();
        rectX = rectX + (1 - widthFactor) * state.getBarWidth() / 2.0;
        if (seriesCount > 1) {
            
            shift = rectWidth * 0.20 / (seriesCount - 1);
        }

        Rectangle2D bar = new Rectangle2D.Double(
            (rectX + ((seriesCount - 1 - row) * shift)), rectY,
            (rectWidth - (seriesCount - 1 - row) * shift * 2), rectHeight);
        Paint itemPaint = getItemPaint(row, column);
        GradientPaintTransformer t = getGradientPaintTransformer();
        if (t != null && itemPaint instanceof GradientPaint) {
            itemPaint = t.transform((GradientPaint) itemPaint, bar);
        }
        g2.setPaint(itemPaint);
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

        
        double transX1 = rangeAxis.valueToJava2D(base, dataArea, edge);
        double transX2 = rangeAxis.valueToJava2D(value, dataArea, edge);

        CategoryItemLabelGenerator generator
            = getItemLabelGenerator(row, column);
        if (generator != null && isItemLabelVisible(row, column)) {
            drawItemLabel(g2, dataset, row, column, plot, generator, bar,
                    (transX1 > transX2));
        }

        
        EntityCollection entities = state.getEntityCollection();
        if (entities != null) {
            addItemEntity(entities, dataset, row, column, bar);
        }
    }

}
