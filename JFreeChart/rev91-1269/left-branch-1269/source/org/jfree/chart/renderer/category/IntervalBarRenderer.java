

package org.jfree.chart.renderer.category;

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
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.PublicCloneable;


public class IntervalBarRenderer extends BarRenderer
        implements CategoryItemRenderer, Cloneable, PublicCloneable,
                   Serializable {

    
    private static final long serialVersionUID = -5068857361615528725L;

    
    public IntervalBarRenderer() {
        super();
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

         if (dataset instanceof IntervalCategoryDataset) {
             IntervalCategoryDataset d = (IntervalCategoryDataset) dataset;
             drawInterval(g2, state, dataArea, plot, domainAxis, rangeAxis,
                     d, row, column);
         }
         else {
             super.drawItem(g2, state, dataArea, plot, domainAxis, rangeAxis,
                     dataset, row, column, pass);
         }

     }

     
     protected void drawInterval(Graphics2D g2,
                                 CategoryItemRendererState state,
                                 Rectangle2D dataArea,
                                 CategoryPlot plot,
                                 CategoryAxis domainAxis,
                                 ValueAxis rangeAxis,
                                 IntervalCategoryDataset dataset,
                                 int row,
                                 int column) {

        int seriesCount = getRowCount();
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
            Number tempNum = value1;
            value1 = value0;
            value0 = tempNum;
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
                rectY = rectY + row * (state.getBarWidth() + seriesGap);
            }
            else {
                rectY = rectY + row * state.getBarWidth();
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
                rectX = rectX + row * (state.getBarWidth() + seriesGap);
            }
            else {
                rectX = rectX + row * state.getBarWidth();
            }

            rectY = java2dValue0;
            barBase = RectangleEdge.BOTTOM;
        }
        Rectangle2D bar = new Rectangle2D.Double(rectX, rectY, rectWidth,
                rectHeight);
        BarPainter painter = getBarPainter();
        if (getShadowsVisible()) {
        	painter.paintBarShadow(g2, this, row, column, bar, barBase, false);
        }
        getBarPainter().paintBar(g2, this, row, column, bar, barBase);

        CategoryItemLabelGenerator generator = getItemLabelGenerator(row,
                column);
        if (generator != null && isItemLabelVisible(row, column)) {
            drawItemLabel(g2, dataset, row, column, plot, generator, bar,
                    false);
        }

        
        EntityCollection entities = state.getEntityCollection();
        if (entities != null) {
            addItemEntity(entities, dataset, row, column, bar);
        }

    }

}
