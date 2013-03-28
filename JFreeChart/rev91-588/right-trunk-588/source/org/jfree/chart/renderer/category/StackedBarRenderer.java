

package org.jfree.chart.renderer.category;

import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.text.TextAnchor;
import org.jfree.chart.util.GradientPaintTransformer;
import org.jfree.chart.util.PublicCloneable;
import org.jfree.chart.util.RectangleEdge;
import org.jfree.data.DataUtilities;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;


public class StackedBarRenderer extends BarRenderer 
                                implements Cloneable, PublicCloneable, 
                                           Serializable {

    
    static final long serialVersionUID = 6402943811500067531L;
    
    
    private boolean renderAsPercentages;
    
    
    public StackedBarRenderer() {
        this(false);
    }
    
    
    public StackedBarRenderer(boolean renderAsPercentages) {
        super();
        this.renderAsPercentages = renderAsPercentages;
        
        
        
        ItemLabelPosition p = new ItemLabelPosition(ItemLabelAnchor.CENTER, 
                TextAnchor.CENTER);
        setBasePositiveItemLabelPosition(p);
        setBaseNegativeItemLabelPosition(p);
        setPositiveItemLabelPositionFallback(null);
        setNegativeItemLabelPositionFallback(null);
    }

    
    public boolean getRenderAsPercentages() {
        return this.renderAsPercentages;   
    }
    
    
    public void setRenderAsPercentages(boolean asPercentages) {
        this.renderAsPercentages = asPercentages; 
        notifyListeners(new RendererChangeEvent(this));
    }
    
    
    public int getPassCount() {
        return 2;
    }
    
    
    public Range findRangeBounds(CategoryDataset dataset) {
        if (this.renderAsPercentages) {
            return new Range(0.0, 1.0);   
        }
        else {
            return DatasetUtilities.findStackedRangeBounds(dataset, getBase());
        }
    }

    
    protected void calculateBarWidth(CategoryPlot plot, 
                                     Rectangle2D dataArea, 
                                     int rendererIndex,
                                     CategoryItemRendererState state) {

        
        CategoryAxis xAxis = plot.getDomainAxisForDataset(rendererIndex);
        CategoryDataset data = plot.getDataset(rendererIndex);
        if (data != null) {
            PlotOrientation orientation = plot.getOrientation();
            double space = 0.0;
            if (orientation == PlotOrientation.HORIZONTAL) {
                space = dataArea.getHeight();
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                space = dataArea.getWidth();
            }
            double maxWidth = space * getMaximumBarWidth();
            int columns = data.getColumnCount();
            double categoryMargin = 0.0;
            if (columns > 1) {
                categoryMargin = xAxis.getCategoryMargin();
            }

            double used = space * (1 - xAxis.getLowerMargin() 
                                     - xAxis.getUpperMargin()
                                     - categoryMargin);
            if (columns > 0) {
                state.setBarWidth(Math.min(used / columns, maxWidth));
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
                         CategoryDataset dataset,
                         int row,
                         int column,
                         int pass) {
     
        
        Number dataValue = dataset.getValue(row, column);
        if (dataValue == null) {
            return;
        }
        
        double value = dataValue.doubleValue();
        double total = 0.0;  
        if (this.renderAsPercentages) {
            total = DataUtilities.calculateColumnTotal(dataset, column);
            value = value / total;
        }
        
        PlotOrientation orientation = plot.getOrientation();
        double barW0 = domainAxis.getCategoryMiddle(column, getColumnCount(), 
                dataArea, plot.getDomainAxisEdge()) 
                - state.getBarWidth() / 2.0;

        double positiveBase = getBase();
        double negativeBase = positiveBase;

        for (int i = 0; i < row; i++) {
            Number v = dataset.getValue(i, column);
            if (v != null) {
                double d = v.doubleValue();
                if (this.renderAsPercentages) {
                    d = d / total;
                }
                if (d > 0) {
                    positiveBase = positiveBase + d;
                }
                else {
                    negativeBase = negativeBase + d;
                }
            }
        }

        double translatedBase;
        double translatedValue;
        RectangleEdge location = plot.getRangeAxisEdge();
        if (value >= 0.0) {
            translatedBase = rangeAxis.valueToJava2D(positiveBase, dataArea, 
                    location);
            translatedValue = rangeAxis.valueToJava2D(positiveBase + value, 
                    dataArea, location);
        }
        else {
            translatedBase = rangeAxis.valueToJava2D(negativeBase, dataArea, 
                    location);
            translatedValue = rangeAxis.valueToJava2D(negativeBase + value, 
                    dataArea, location);
        }
        double barL0 = Math.min(translatedBase, translatedValue);
        double barLength = Math.max(Math.abs(translatedValue - translatedBase),
                getMinimumBarLength());

        Rectangle2D bar = null;
        if (orientation == PlotOrientation.HORIZONTAL) {
            bar = new Rectangle2D.Double(barL0, barW0, barLength, 
                    state.getBarWidth());
        }
        else {
            bar = new Rectangle2D.Double(barW0, barL0, state.getBarWidth(), 
                    barLength);
        }
        if (pass == 0) {
            Paint itemPaint = getItemPaint(row, column);
            GradientPaintTransformer t = getGradientPaintTransformer();
            if (t != null && itemPaint instanceof GradientPaint) {
                itemPaint = t.transform((GradientPaint) itemPaint, bar);
            }
            g2.setPaint(itemPaint);
            g2.fill(bar);
            if (isDrawBarOutline() 
                    && state.getBarWidth() > BAR_OUTLINE_WIDTH_THRESHOLD) {
                g2.setStroke(getItemOutlineStroke(row, column));
                g2.setPaint(getItemOutlinePaint(row, column));
                g2.draw(bar);
            }

            
            EntityCollection entities = state.getEntityCollection();
            if (entities != null) {
                addItemEntity(entities, dataset, row, column, bar);
            }
        }
        else if (pass == 1) {
            CategoryItemLabelGenerator generator = getItemLabelGenerator(row, 
                    column);
            if (generator != null && isItemLabelVisible(row, column)) {
                drawItemLabel(g2, dataset, row, column, plot, generator, bar, 
                        (value < 0.0));
            }
        }        
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;   
        }
        if (!(obj instanceof StackedBarRenderer)) {
            return false;   
        }
        StackedBarRenderer that = (StackedBarRenderer) obj;
        if (this.renderAsPercentages != that.renderAsPercentages) {
            return false;   
        }
        return super.equals(obj);
    }

}
