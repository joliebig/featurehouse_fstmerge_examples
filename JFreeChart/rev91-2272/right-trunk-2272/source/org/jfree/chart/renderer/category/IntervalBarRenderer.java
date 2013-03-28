

package org.jfree.chart.renderer.category;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.util.RectangleEdge;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.IntervalCategoryDataset;


public class IntervalBarRenderer extends BarRenderer {

    
    private static final long serialVersionUID = -5068857361615528725L;

    
    public IntervalBarRenderer() {
        super();
    }

    
    public Range findRangeBounds(CategoryDataset dataset) {
        return findRangeBounds(dataset, true);
    }

    
    public void drawItem(Graphics2D g2, CategoryItemRendererState state,
            Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis,
            ValueAxis rangeAxis, CategoryDataset dataset, int row, int column,
            boolean selected, int pass) {

         if (dataset instanceof IntervalCategoryDataset) {
             IntervalCategoryDataset d = (IntervalCategoryDataset) dataset;
             drawInterval(g2, state, dataArea, plot, domainAxis, rangeAxis,
                     d, row, column, selected);
         }
         else {
             super.drawItem(g2, state, dataArea, plot, domainAxis, rangeAxis,
                     dataset, row, column, selected, pass);
         }

     }

     
     protected void drawInterval(Graphics2D g2, 
             CategoryItemRendererState state, Rectangle2D dataArea,
             CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis,
             IntervalCategoryDataset dataset, int row, int column,
             boolean selected) {

        int visibleRow = state.getVisibleSeriesIndex(row);
        if (visibleRow < 0) {
            return;
        }
        int seriesCount = state.getVisibleSeriesCount() >= 0
                ? state.getVisibleSeriesCount() : getRowCount();

        int categoryCount = getColumnCount();

        PlotOrientation orientation = plot.getOrientation();

        double rectX = 0.0;
        double rectY = 0.0;

        RectangleEdge domainAxisLocation = plot.getDomainAxisEdge();
        RectangleEdge rangeAxisLocation = plot.getRangeAxisEdge();

        
        Number value0 = dataset.getEndValue(row, column);
        if (value0 == null) {
            return;
        }
        double java2dValue0 = rangeAxis.valueToJava2D(value0.doubleValue(),
                dataArea, rangeAxisLocation);

        
        Number value1 = dataset.getStartValue(row, column);
        if (value1 == null) {
            return;
        }
        double java2dValue1 = rangeAxis.valueToJava2D(
                value1.doubleValue(), dataArea, rangeAxisLocation);

        if (java2dValue1 < java2dValue0) {
            double temp = java2dValue1;
            java2dValue1 = java2dValue0;
            java2dValue0 = temp;
        }

        
        double rectWidth = state.getBarWidth();

        
        double rectHeight = Math.abs(java2dValue1 - java2dValue0);

        RectangleEdge barBase = RectangleEdge.LEFT;
        if (orientation == PlotOrientation.HORIZONTAL) {
            
            rectY = domainAxis.getCategoryStart(column, getColumnCount(),
                    dataArea, domainAxisLocation);
            if (seriesCount > 1) {
                double seriesGap = dataArea.getHeight() * getItemMargin()
                                   / (categoryCount * (seriesCount - 1));
                rectY = rectY + visibleRow * (state.getBarWidth() + seriesGap);
            }
            else {
                rectY = rectY + visibleRow * state.getBarWidth();
            }

            rectX = java2dValue0;

            rectHeight = state.getBarWidth();
            rectWidth = Math.abs(java2dValue1 - java2dValue0);
            barBase = RectangleEdge.LEFT;
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            
            rectX = domainAxis.getCategoryStart(column, getColumnCount(),
                    dataArea, domainAxisLocation);

            if (seriesCount > 1) {
                double seriesGap = dataArea.getWidth() * getItemMargin()
                                   / (categoryCount * (seriesCount - 1));
                rectX = rectX + visibleRow * (state.getBarWidth() + seriesGap);
            }
            else {
                rectX = rectX + visibleRow * state.getBarWidth();
            }

            rectY = java2dValue0;
            barBase = RectangleEdge.BOTTOM;
        }
        Rectangle2D bar = new Rectangle2D.Double(rectX, rectY, rectWidth,
                rectHeight);
        BarPainter painter = getBarPainter();
        if (getShadowsVisible()) {
            painter.paintBarShadow(g2, this, row, column, selected, bar,
                    barBase, false);
        }
        getBarPainter().paintBar(g2, this, row, column, selected, bar, barBase);

        CategoryItemLabelGenerator generator = getItemLabelGenerator(row,
                column, selected);
        if (generator != null && isItemLabelVisible(row, column, selected)) {
            drawItemLabelForBar(g2, plot, dataset, row, column, selected,
                    generator, bar, false);
        }

        
        EntityCollection entities = state.getEntityCollection();
        if (entities != null) {
            addEntity(entities, bar, dataset, row, column, selected);
        }

    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof IntervalBarRenderer)) {
            return false;
        }
        
        return super.equals(obj);
    }

}
