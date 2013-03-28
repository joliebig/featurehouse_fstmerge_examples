

package org.jfree.chart.renderer.category;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.util.PublicCloneable;
import org.jfree.chart.util.RectangleEdge;
import org.jfree.data.category.CategoryDataset;


public class LevelRenderer extends AbstractCategoryItemRenderer
        implements Cloneable, PublicCloneable, Serializable {

    
    private static final long serialVersionUID = -8204856624355025117L;

    
    public static final double DEFAULT_ITEM_MARGIN = 0.20;

    
    private double itemMargin;

    
    private double maxItemWidth;

    
    public LevelRenderer() {
        super();
        this.itemMargin = DEFAULT_ITEM_MARGIN;
        this.maxItemWidth = 1.0;  
                                  
    }

    
    public double getItemMargin() {
        return this.itemMargin;
    }

    
    public void setItemMargin(double percent) {
        this.itemMargin = percent;
        fireChangeEvent();
    }

    
    public double getMaximumItemWidth() {
        return this.maxItemWidth;
    }

    
    public void setMaximumItemWidth(double percent) {
        this.maxItemWidth = percent;
        fireChangeEvent();
    }

    
    public CategoryItemRendererState initialise(Graphics2D g2,
            Rectangle2D dataArea, CategoryPlot plot, int rendererIndex,
            PlotRenderingInfo info) {

        CategoryItemRendererState state = super.initialise(g2, dataArea, plot,
                rendererIndex, info);
        calculateItemWidth(plot, dataArea, rendererIndex, state);
        return state;

    }

    
    protected void calculateItemWidth(CategoryPlot plot,
            Rectangle2D dataArea, int rendererIndex,
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
            double maxWidth = space * getMaximumItemWidth();
            double categoryMargin = 0.0;
            double currentItemMargin = 0.0;
            if (columns > 1) {
                categoryMargin = domainAxis.getCategoryMargin();
            }
            if (rows > 1) {
                currentItemMargin = getItemMargin();
            }
            double used = space * (1 - domainAxis.getLowerMargin()
                                     - domainAxis.getUpperMargin()
                                     - categoryMargin - currentItemMargin);
            if ((rows * columns) > 0) {
                state.setBarWidth(Math.min(used / (rows * columns), maxWidth));
            }
            else {
                state.setBarWidth(Math.min(used, maxWidth));
            }
        }
    }

    
    protected double calculateBarW0(CategoryPlot plot,
                                    PlotOrientation orientation,
                                    Rectangle2D dataArea,
                                    CategoryAxis domainAxis,
                                    CategoryItemRendererState state,
                                    int row,
                                    int column) {
        
        double space = 0.0;
        if (orientation == PlotOrientation.HORIZONTAL) {
            space = dataArea.getHeight();
        }
        else {
            space = dataArea.getWidth();
        }
        double barW0 = domainAxis.getCategoryStart(column, getColumnCount(),
                dataArea, plot.getDomainAxisEdge());
        int seriesCount = getRowCount();
        int categoryCount = getColumnCount();
        if (seriesCount > 1) {
            double seriesGap = space * getItemMargin()
                    / (categoryCount * (seriesCount - 1));
            double seriesW = calculateSeriesWidth(space, domainAxis,
                    categoryCount, seriesCount);
            barW0 = barW0 + row * (seriesW + seriesGap)
                          + (seriesW / 2.0) - (state.getBarWidth() / 2.0);
        }
        else {
            barW0 = domainAxis.getCategoryMiddle(column, getColumnCount(),
                    dataArea, plot.getDomainAxisEdge()) - state.getBarWidth()
                    / 2.0;
        }
        return barW0;
    }

    
    public void drawItem(Graphics2D g2, CategoryItemRendererState state,
            Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis,
            ValueAxis rangeAxis, CategoryDataset dataset, int row, int column,
            int pass) {

        
        Number dataValue = dataset.getValue(row, column);
        if (dataValue == null) {
            return;
        }

        double value = dataValue.doubleValue();

        PlotOrientation orientation = plot.getOrientation();
        double barW0 = calculateBarW0(plot, orientation, dataArea, domainAxis,
                state, row, column);
        RectangleEdge edge = plot.getRangeAxisEdge();
        double barL = rangeAxis.valueToJava2D(value, dataArea, edge);

        
        Line2D line = null;
        double x = 0.0;
        double y = 0.0;
        if (orientation == PlotOrientation.HORIZONTAL) {
            x = barL;
            y = barW0 + state.getBarWidth() / 2.0;
            line = new Line2D.Double(barL, barW0, barL,
                    barW0 + state.getBarWidth());
        }
        else {
            x = barW0 + state.getBarWidth() / 2.0;
            y = barL;
            line = new Line2D.Double(barW0, barL, barW0 + state.getBarWidth(),
                    barL);
        }
        Stroke itemStroke = getItemStroke(row, column);
        Paint itemPaint = getItemPaint(row, column);
        g2.setStroke(itemStroke);
        g2.setPaint(itemPaint);
        g2.draw(line);

        CategoryItemLabelGenerator generator = getItemLabelGenerator(row,
                column);
        if (generator != null && isItemLabelVisible(row, column)) {
            drawItemLabel(g2, orientation, dataset, row, column, x, y,
                    (value < 0.0));
        }

        
        int datasetIndex = plot.indexOf(dataset);
        updateCrosshairValues(state.getCrosshairState(),
        		dataset.getRowKey(row), dataset.getColumnKey(column), value,
        		datasetIndex, barW0, barL, orientation);

        
        EntityCollection entities = state.getEntityCollection();
        if (entities != null) {
            addItemEntity(entities, dataset, row, column, line.getBounds());
        }

    }

    
    protected double calculateSeriesWidth(double space, CategoryAxis axis,
                                          int categories, int series) {
        double factor = 1.0 - getItemMargin() - axis.getLowerMargin()
                        - axis.getUpperMargin();
        if (categories > 1) {
            factor = factor - axis.getCategoryMargin();
        }
        return (space * factor) / (categories * series);
    }

    
    public double getItemMiddle(Comparable rowKey, Comparable columnKey,
    		CategoryDataset dataset, CategoryAxis axis, Rectangle2D area,
    		RectangleEdge edge) {
        return axis.getCategorySeriesMiddle(columnKey, rowKey, dataset,
        		this.itemMargin, area, edge);
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof LevelRenderer)) {
            return false;
        }
        LevelRenderer that = (LevelRenderer) obj;
        if (this.itemMargin != that.itemMargin) {
            return false;
        }
        if (this.maxItemWidth != that.maxItemWidth) {
            return false;
        }
        return super.equals(obj);
    }

}
