

package org.jfree.chart.renderer.category;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemSource;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.event.RendererChangeListener;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.plot.CategoryMarker;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.urls.CategoryURLGenerator;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;



public interface CategoryItemRenderer extends LegendItemSource {

    
    public int getPassCount();

    
    public CategoryPlot getPlot();

    
    public void setPlot(CategoryPlot plot);

    
    public void addChangeListener(RendererChangeListener listener);

    
    public void removeChangeListener(RendererChangeListener listener);

    
    public Range findRangeBounds(CategoryDataset dataset);

    
    public CategoryItemRendererState initialise(Graphics2D g2,
                                                Rectangle2D dataArea,
                                                CategoryPlot plot,
                                                int rendererIndex,
                                                PlotRenderingInfo info);

    
    public boolean getItemVisible(int series, int item);

    
    public boolean isSeriesVisible(int series);

    
    public Boolean getSeriesVisible();

    
    public void setSeriesVisible(Boolean visible);

    
    public void setSeriesVisible(Boolean visible, boolean notify);

    
    public Boolean getSeriesVisible(int series);

    
    public void setSeriesVisible(int series, Boolean visible);

    
    public void setSeriesVisible(int series, Boolean visible, boolean notify);

    
    public boolean getBaseSeriesVisible();

    
    public void setBaseSeriesVisible(boolean visible);

    
    public void setBaseSeriesVisible(boolean visible, boolean notify);

    

    
    public boolean isSeriesVisibleInLegend(int series);

    
    public Boolean getSeriesVisibleInLegend();

    
    public void setSeriesVisibleInLegend(Boolean visible);

    
    public void setSeriesVisibleInLegend(Boolean visible, boolean notify);

    
    public Boolean getSeriesVisibleInLegend(int series);

    
    public void setSeriesVisibleInLegend(int series, Boolean visible);

    
    public void setSeriesVisibleInLegend(int series, Boolean visible,
                                         boolean notify);

    
    public boolean getBaseSeriesVisibleInLegend();

    
    public void setBaseSeriesVisibleInLegend(boolean visible);

    
    public void setBaseSeriesVisibleInLegend(boolean visible, boolean notify);


    

    
    public Paint getItemPaint(int row, int column);

    
    public void setPaint(Paint paint);

    
    public Paint getSeriesPaint(int series);

    
    public void setSeriesPaint(int series, Paint paint);

    

    
    public Paint getBasePaint();

    
    public void setBasePaint(Paint paint);

    

    




















































    

    
    public Paint getItemOutlinePaint(int row, int column);

    
    public void setOutlinePaint(Paint paint);

    
    public Paint getSeriesOutlinePaint(int series);

    
    public void setSeriesOutlinePaint(int series, Paint paint);

    

    
    public Paint getBaseOutlinePaint();

    
    public void setBaseOutlinePaint(Paint paint);

    

    

    
    public Stroke getItemStroke(int row, int column);

    
    public void setStroke(Stroke stroke);

    
    public Stroke getSeriesStroke(int series);

    
    public void setSeriesStroke(int series, Stroke stroke);

    

    
    public Stroke getBaseStroke();

    
    public void setBaseStroke(Stroke stroke);

    

    

    
    public Stroke getItemOutlineStroke(int row, int column);

    
    public void setOutlineStroke(Stroke stroke);

    
    public Stroke getSeriesOutlineStroke(int series);

    
    public void setSeriesOutlineStroke(int series, Stroke stroke);

    

    
    public Stroke getBaseOutlineStroke();

    
    public void setBaseOutlineStroke(Stroke stroke);

    

    

    
    public Shape getItemShape(int row, int column);

    
    public void setShape(Shape shape);

    
    public Shape getSeriesShape(int series);

    
    public void setSeriesShape(int series, Shape shape);

    

    
    public Shape getBaseShape();

    
    public void setBaseShape(Shape shape);

    

    

    
    public boolean isItemLabelVisible(int row, int column);

    
    public void setItemLabelsVisible(boolean visible);

    
    public void setItemLabelsVisible(Boolean visible);

    
    public void setItemLabelsVisible(Boolean visible, boolean notify);

    
    public boolean isSeriesItemLabelsVisible(int series);

    
    public void setSeriesItemLabelsVisible(int series, boolean visible);

    
    public void setSeriesItemLabelsVisible(int series, Boolean visible);

    
    public void setSeriesItemLabelsVisible(int series, Boolean visible,
                                           boolean notify);

    
    public Boolean getBaseItemLabelsVisible();

    
    public void setBaseItemLabelsVisible(boolean visible);

    
    public void setBaseItemLabelsVisible(Boolean visible);

    
    public void setBaseItemLabelsVisible(Boolean visible, boolean notify);

    

    
    public CategoryItemLabelGenerator getItemLabelGenerator(int series,
            int item);

    
    public void setItemLabelGenerator(CategoryItemLabelGenerator generator);

    
    public CategoryItemLabelGenerator getSeriesItemLabelGenerator(int series);

    
    public void setSeriesItemLabelGenerator(int series,
            CategoryItemLabelGenerator generator);

    
    

    
    public CategoryItemLabelGenerator getBaseItemLabelGenerator();

    
    public void setBaseItemLabelGenerator(CategoryItemLabelGenerator generator);

    
    

    

    
    public CategoryToolTipGenerator getToolTipGenerator(int row, int column);

    
    public CategoryToolTipGenerator getToolTipGenerator();

    
    public void setToolTipGenerator(CategoryToolTipGenerator generator);

    
    public CategoryToolTipGenerator getSeriesToolTipGenerator(int series);

    
    public void setSeriesToolTipGenerator(int series,
                                          CategoryToolTipGenerator generator);

    
    

    
    public CategoryToolTipGenerator getBaseToolTipGenerator();

    
    public void setBaseToolTipGenerator(CategoryToolTipGenerator generator);

    

    

    
    public Font getItemLabelFont(int row, int column);

    
    public Font getItemLabelFont();

    
    public void setItemLabelFont(Font font);

    
    public Font getSeriesItemLabelFont(int series);

    
    public void setSeriesItemLabelFont(int series, Font font);

    

    
    public Font getBaseItemLabelFont();

    
    public void setBaseItemLabelFont(Font font);

    

    

    
    public Paint getItemLabelPaint(int row, int column);

    
    public Paint getItemLabelPaint();

    
    public void setItemLabelPaint(Paint paint);

    
    public Paint getSeriesItemLabelPaint(int series);

    
    public void setSeriesItemLabelPaint(int series, Paint paint);

    

    
    public Paint getBaseItemLabelPaint();

    
    public void setBaseItemLabelPaint(Paint paint);

    

    

    
    public ItemLabelPosition getPositiveItemLabelPosition(int row, int column);

    
    public ItemLabelPosition getPositiveItemLabelPosition();

    
    public void setPositiveItemLabelPosition(ItemLabelPosition position);

    
    public void setPositiveItemLabelPosition(ItemLabelPosition position,
                                             boolean notify);

    
    public ItemLabelPosition getSeriesPositiveItemLabelPosition(int series);

    
    public void setSeriesPositiveItemLabelPosition(int series,
                                                   ItemLabelPosition position);

    
    public void setSeriesPositiveItemLabelPosition(int series,
            ItemLabelPosition position, boolean notify);

    
    public ItemLabelPosition getBasePositiveItemLabelPosition();

    
    public void setBasePositiveItemLabelPosition(ItemLabelPosition position);

    
    public void setBasePositiveItemLabelPosition(ItemLabelPosition position,
                                                 boolean notify);


    

    
    public ItemLabelPosition getNegativeItemLabelPosition(int row, int column);

    
    public ItemLabelPosition getNegativeItemLabelPosition();

    
    public void setNegativeItemLabelPosition(ItemLabelPosition position);

    
    public void setNegativeItemLabelPosition(ItemLabelPosition position,
                                             boolean notify);

    
    public ItemLabelPosition getSeriesNegativeItemLabelPosition(int series);

    
    public void setSeriesNegativeItemLabelPosition(int series,
                                                   ItemLabelPosition position);

    
    public void setSeriesNegativeItemLabelPosition(int series,
                                                   ItemLabelPosition position,
                                                   boolean notify);

    
    public ItemLabelPosition getBaseNegativeItemLabelPosition();

    
    public void setBaseNegativeItemLabelPosition(ItemLabelPosition position);

    
    public void setBaseNegativeItemLabelPosition(ItemLabelPosition position,
                                                 boolean notify);

    
    

















    

    
    public CategoryURLGenerator getItemURLGenerator(int series, int item);

    
    public void setItemURLGenerator(CategoryURLGenerator generator);

    
    public CategoryURLGenerator getSeriesItemURLGenerator(int series);

    
    public void setSeriesItemURLGenerator(int series,
                                          CategoryURLGenerator generator);

    

    
    public CategoryURLGenerator getBaseItemURLGenerator();

    
    public void setBaseItemURLGenerator(CategoryURLGenerator generator);

    

    
    public LegendItem getLegendItem(int datasetIndex, int series);

    
    public void drawBackground(Graphics2D g2,
                               CategoryPlot plot,
                               Rectangle2D dataArea);

    
    public void drawOutline(Graphics2D g2,
                            CategoryPlot plot,
                            Rectangle2D dataArea);

    
    public void drawItem(Graphics2D g2,
                         CategoryItemRendererState state,
                         Rectangle2D dataArea,
                         CategoryPlot plot,
                         CategoryAxis domainAxis,
                         ValueAxis rangeAxis,
                         CategoryDataset dataset,
                         int row,
                         int column,
                         int pass);

    
    public void drawDomainGridline(Graphics2D g2,
                                   CategoryPlot plot,
                                   Rectangle2D dataArea,
                                   double value);

    
    public void drawRangeGridline(Graphics2D g2,
                                  CategoryPlot plot,
                                  ValueAxis axis,
                                  Rectangle2D dataArea,
                                  double value);

    
    public void drawDomainMarker(Graphics2D g2,
                                 CategoryPlot plot,
                                 CategoryAxis axis,
                                 CategoryMarker marker,
                                 Rectangle2D dataArea);

    
    public void drawRangeMarker(Graphics2D g2,
                                CategoryPlot plot,
                                ValueAxis axis,
                                Marker marker,
                                Rectangle2D dataArea);

}
