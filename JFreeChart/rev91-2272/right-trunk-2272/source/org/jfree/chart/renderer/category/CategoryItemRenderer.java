

package org.jfree.chart.renderer.category;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemSource;
import org.jfree.chart.annotations.CategoryAnnotation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.event.RendererChangeListener;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.CategorySeriesLabelGenerator;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.plot.CategoryMarker;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.renderer.RenderAttributes;
import org.jfree.chart.urls.CategoryURLGenerator;
import org.jfree.chart.util.Layer;
import org.jfree.chart.util.RectangleEdge;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;


public interface CategoryItemRenderer extends LegendItemSource {

    
    public CategoryPlot getPlot();

    
    public void setPlot(CategoryPlot plot);

    
    public int getPassCount();

    
    public Range findRangeBounds(CategoryDataset dataset);

    
    public void addChangeListener(RendererChangeListener listener);

    
    public void removeChangeListener(RendererChangeListener listener);

    

    
    public boolean getItemVisible(int series, int item);

    
    public boolean isSeriesVisible(int series);

    
    public Boolean getSeriesVisible(int series);

    
    public void setSeriesVisible(int series, Boolean visible);

    
    public void setSeriesVisible(int series, Boolean visible, boolean notify);

    
    public boolean getBaseSeriesVisible();

    
    public void setBaseSeriesVisible(boolean visible);

    
    public void setBaseSeriesVisible(boolean visible, boolean notify);


    

    
    public boolean isSeriesVisibleInLegend(int series);

    
    public Boolean getSeriesVisibleInLegend(int series);

    
    public void setSeriesVisibleInLegend(int series, Boolean visible);

    
    public void setSeriesVisibleInLegend(int series, Boolean visible,
                                         boolean notify);

    
    public boolean getBaseSeriesVisibleInLegend();

    
    public void setBaseSeriesVisibleInLegend(boolean visible);

    
    public void setBaseSeriesVisibleInLegend(boolean visible, boolean notify);


    

    
    public Paint getItemPaint(int row, int column, boolean selected);

    
    public Paint getSeriesPaint(int series);

    
    public void setSeriesPaint(int series, Paint paint);

    
    public void setSeriesPaint(int series, Paint paint, boolean notify);

    
    public Paint getBasePaint();

    
    public void setBasePaint(Paint paint);

    
    public void setBasePaint(Paint paint, boolean notify);


    

    
    public Paint getItemFillPaint(int row, int column, boolean selected);

    
    public Paint getSeriesFillPaint(int series);

    
    public void setSeriesFillPaint(int series, Paint paint);

    
    public void setSeriesFillPaint(int series, Paint paint, boolean notify);

    
    public Paint getBaseFillPaint();

    
    public void setBaseFillPaint(Paint paint);

    
    public void setBaseFillPaint(Paint paint, boolean notify);


    

    
    public Paint getItemOutlinePaint(int row, int column, boolean selected);

    
    public Paint getSeriesOutlinePaint(int series);

    
    public void setSeriesOutlinePaint(int series, Paint paint);

    
    public void setSeriesOutlinePaint(int series, Paint paint, boolean notify);

    
    public Paint getBaseOutlinePaint();

    
    public void setBaseOutlinePaint(Paint paint);

    
    public void setBaseOutlinePaint(Paint paint, boolean notify);


    

    
    public Stroke getItemStroke(int row, int column, boolean selected);

    
    public Stroke getSeriesStroke(int series);

    
    public void setSeriesStroke(int series, Stroke stroke);

    
    public void setSeriesStroke(int series, Stroke stroke, boolean notify);

    
    public Stroke getBaseStroke();

    
    public void setBaseStroke(Stroke stroke);

    
    public void setBaseStroke(Stroke stroke, boolean notify);


    

    
    public Stroke getItemOutlineStroke(int row, int column, boolean selected);

    
    public Stroke getSeriesOutlineStroke(int series);

    
    public void setSeriesOutlineStroke(int series, Stroke stroke);

    
    public void setSeriesOutlineStroke(int series, Stroke stroke,
            boolean notify);

    
    public Stroke getBaseOutlineStroke();

    
    public void setBaseOutlineStroke(Stroke stroke);

    
    public void setBaseOutlineStroke(Stroke stroke, boolean notify);


    

    
    public Shape getItemShape(int row, int column, boolean selected);

    
    public Shape getSeriesShape(int series);

    
    public void setSeriesShape(int series, Shape shape);

    
    public void setSeriesShape(int series, Shape shape, boolean notify);

    
    public Shape getBaseShape();

    
    public void setBaseShape(Shape shape);

    
    public void setBaseShape(Shape shape, boolean notify);


    

    
    public LegendItem getLegendItem(int datasetIndex, int series);

    
    public CategorySeriesLabelGenerator getLegendItemLabelGenerator();

    
    public void setLegendItemLabelGenerator(
            CategorySeriesLabelGenerator generator);

    
    public CategorySeriesLabelGenerator getLegendItemToolTipGenerator();

    
    public void setLegendItemToolTipGenerator(
            CategorySeriesLabelGenerator generator);

    
    public CategorySeriesLabelGenerator getLegendItemURLGenerator();

    
    public void setLegendItemURLGenerator(
            CategorySeriesLabelGenerator generator);


    

    
    public CategoryToolTipGenerator getToolTipGenerator(int row, int column,
            boolean selected);

    
    public CategoryToolTipGenerator getSeriesToolTipGenerator(int series);

    
    public void setSeriesToolTipGenerator(int series,
                                          CategoryToolTipGenerator generator);

    
    public void setSeriesToolTipGenerator(int series,
            CategoryToolTipGenerator generator, boolean notify);

    
    public CategoryToolTipGenerator getBaseToolTipGenerator();

    
    public void setBaseToolTipGenerator(CategoryToolTipGenerator generator);

    
    public void setBaseToolTipGenerator(CategoryToolTipGenerator generator,
            boolean notify);


    

    
    public CategoryURLGenerator getURLGenerator(int series, int item, boolean
            selected);

    
    public CategoryURLGenerator getSeriesURLGenerator(int series);

    
    public void setSeriesURLGenerator(int series,
            CategoryURLGenerator generator);

    
    public void setSeriesURLGenerator(int series,
            CategoryURLGenerator generator, boolean notify);

    
    public CategoryURLGenerator getBaseURLGenerator();

    
    public void setBaseURLGenerator(CategoryURLGenerator generator);

    
    public void setBaseURLGenerator(CategoryURLGenerator generator,
            boolean notify);


    

    
    public boolean isItemLabelVisible(int row, int column, boolean selected);

    
    public boolean isSeriesItemLabelsVisible(int series);

    
    public Boolean getSeriesItemLabelsVisible(int series);

    
    public void setSeriesItemLabelsVisible(int series, boolean visible);

    
    public void setSeriesItemLabelsVisible(int series, Boolean visible);

    
    public void setSeriesItemLabelsVisible(int series, Boolean visible,
                                           boolean notify);

    
    public boolean getBaseItemLabelsVisible();

    
    public void setBaseItemLabelsVisible(boolean visible);

    
    public void setBaseItemLabelsVisible(boolean visible, boolean notify);


    

    
    public CategoryItemLabelGenerator getItemLabelGenerator(int series,
            int item, boolean selected);

    
    public CategoryItemLabelGenerator getSeriesItemLabelGenerator(int series);

    
    public void setSeriesItemLabelGenerator(int series,
            CategoryItemLabelGenerator generator);

    
    public void setSeriesItemLabelGenerator(int series,
            CategoryItemLabelGenerator generator, boolean notify);

    
    public CategoryItemLabelGenerator getBaseItemLabelGenerator();

    
    public void setBaseItemLabelGenerator(CategoryItemLabelGenerator generator);

    
    public void setBaseItemLabelGenerator(CategoryItemLabelGenerator generator,
            boolean notify);


    

    
    public Font getItemLabelFont(int row, int column, boolean selected);

    
    public Font getSeriesItemLabelFont(int series);

    
    public void setSeriesItemLabelFont(int series, Font font);

    
    public void setSeriesItemLabelFont(int series, Font font, boolean notify);

    
    public Font getBaseItemLabelFont();

    
    public void setBaseItemLabelFont(Font font);

    
    public void setBaseItemLabelFont(Font font, boolean notify);


    

    
    public Paint getItemLabelPaint(int row, int column, boolean selected);

    
    public Paint getSeriesItemLabelPaint(int series);

    
    public void setSeriesItemLabelPaint(int series, Paint paint);

    
    public void setSeriesItemLabelPaint(int series, Paint paint,
            boolean notify);

    
    public Paint getBaseItemLabelPaint();

    
    public void setBaseItemLabelPaint(Paint paint);

    
    public void setBaseItemLabelPaint(Paint paint, boolean notify);


    

    
    public ItemLabelPosition getPositiveItemLabelPosition(int row, int column,
            boolean selected);

    
    public ItemLabelPosition getSeriesPositiveItemLabelPosition(int series);

    
    public void setSeriesPositiveItemLabelPosition(int series,
                                                   ItemLabelPosition position);

    
    public void setSeriesPositiveItemLabelPosition(int series,
            ItemLabelPosition position, boolean notify);

    
    public ItemLabelPosition getBasePositiveItemLabelPosition();

    
    public void setBasePositiveItemLabelPosition(ItemLabelPosition position);

    
    public void setBasePositiveItemLabelPosition(ItemLabelPosition position,
                                                 boolean notify);


    

    
    public ItemLabelPosition getNegativeItemLabelPosition(int row, int column,
            boolean selected);

    
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


    

    
    public boolean getItemCreateEntity(int series, int item, boolean selected);

    
    public Boolean getSeriesCreateEntities(int series);

    
    public void setSeriesCreateEntities(int series, Boolean create);

    
    public void setSeriesCreateEntities(int series, Boolean create,
            boolean notify);

    
    public boolean getBaseCreateEntities();

    
    public void setBaseCreateEntities(boolean create);

    
    public void setBaseCreateEntities(boolean create, boolean notify);


    

    
    public void addAnnotation(CategoryAnnotation annotation);

    
    public void addAnnotation(CategoryAnnotation annotation, Layer layer);

    
    public boolean removeAnnotation(CategoryAnnotation annotation);

    
    public void removeAnnotations();

    
    public void drawAnnotations(Graphics2D g2, Rectangle2D dataArea,
            CategoryAxis domainAxis, ValueAxis rangeAxis, Layer layer,
            PlotRenderingInfo info);


    

    
    public CategoryItemRendererState initialise(Graphics2D g2,
            Rectangle2D dataArea, CategoryPlot plot, CategoryDataset dataset,
            PlotRenderingInfo info);

    
    public void drawBackground(Graphics2D g2, CategoryPlot plot,
            Rectangle2D dataArea);

    
    public void drawOutline(Graphics2D g2, CategoryPlot plot,
            Rectangle2D dataArea);

    
    public void drawItem(Graphics2D g2, CategoryItemRendererState state,
            Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis,
            ValueAxis rangeAxis, CategoryDataset dataset, int row, int column,
            boolean selected, int pass);

    
    public void drawDomainLine(Graphics2D g2, CategoryPlot plot,
            Rectangle2D dataArea, double value, Paint paint, Stroke stroke);

    
    public void drawRangeLine(Graphics2D g2, CategoryPlot plot, ValueAxis axis,
            Rectangle2D dataArea, double value, Paint paint, Stroke stroke);

    
    public void drawDomainMarker(Graphics2D g2, CategoryPlot plot,
            CategoryAxis axis, CategoryMarker marker, Rectangle2D dataArea);

    
    public void drawRangeMarker(Graphics2D g2, CategoryPlot plot,
            ValueAxis axis, Marker marker, Rectangle2D dataArea);

    
    public double getItemMiddle(Comparable rowKey, Comparable columnKey,
            CategoryDataset dataset, CategoryAxis axis, Rectangle2D area,
            RectangleEdge edge);

    
    public RenderAttributes getSelectedItemAttributes();

    
    public Shape createHotSpotShape(Graphics2D g2, Rectangle2D dataArea,
            CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis,
            CategoryDataset dataset, int row, int column, boolean selected,
            CategoryItemRendererState state);

    
    public Rectangle2D createHotSpotBounds(Graphics2D g2, Rectangle2D dataArea,
            CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis,
            CategoryDataset dataset, int series, int item, boolean selected,
            CategoryItemRendererState state, Rectangle2D result);

    
    public boolean hitTest(double xx, double yy, Graphics2D g2,
            Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis,
            ValueAxis rangeAxis, CategoryDataset dataset, int series, int item,
            boolean selected, CategoryItemRendererState state);

}
