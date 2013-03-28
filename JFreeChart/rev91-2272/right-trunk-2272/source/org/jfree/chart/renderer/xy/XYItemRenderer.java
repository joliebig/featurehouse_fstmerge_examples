

package org.jfree.chart.renderer.xy;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

import java.util.Collection;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemSource;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.event.RendererChangeListener;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.labels.XYSeriesLabelGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.RenderAttributes;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.chart.util.Layer;
import org.jfree.data.Range;
import org.jfree.data.xy.XYDataset;


public interface XYItemRenderer extends LegendItemSource {

    
    public XYPlot getPlot();

    
    public void setPlot(XYPlot plot);

    
    public int getPassCount();

    
    public Range findDomainBounds(XYDataset dataset);

    
    public Range findRangeBounds(XYDataset dataset);

    
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


    

    
    public XYSeriesLabelGenerator getLegendItemLabelGenerator();

    
    public void setLegendItemLabelGenerator(XYSeriesLabelGenerator generator);

    
    public XYSeriesLabelGenerator getLegendItemToolTipGenerator();

    
    public void setLegendItemToolTipGenerator(XYSeriesLabelGenerator generator);

    
    public XYSeriesLabelGenerator getLegendItemURLGenerator();

    
    public void setLegendItemURLGenerator(XYSeriesLabelGenerator generator);


    

    
    public XYToolTipGenerator getToolTipGenerator(int row, int column,
            boolean selected);

    
    public XYToolTipGenerator getSeriesToolTipGenerator(int series);

    
    public void setSeriesToolTipGenerator(int series,
                                          XYToolTipGenerator generator);

    
    public void setSeriesToolTipGenerator(int series,
            XYToolTipGenerator generator, boolean notify);

    
    public XYToolTipGenerator getBaseToolTipGenerator();

    
    public void setBaseToolTipGenerator(XYToolTipGenerator generator);

    
    public void setBaseToolTipGenerator(XYToolTipGenerator generator,
            boolean notify);


    


    
    public XYURLGenerator getURLGenerator(int series, int item,
            boolean selected);

    
    public XYURLGenerator getSeriesURLGenerator(int series);

    
    public void setSeriesURLGenerator(int series, XYURLGenerator generator);

    
    public void setSeriesURLGenerator(int series, XYURLGenerator generator,
            boolean notify);

    
    public XYURLGenerator getBaseURLGenerator();

    
    public void setBaseURLGenerator(XYURLGenerator generator);

    
    public void setBaseURLGenerator(XYURLGenerator generator, boolean notify);


    

    
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


    

    
    public XYItemLabelGenerator getItemLabelGenerator(int row, int column,
            boolean selected);

    
    public XYItemLabelGenerator getSeriesItemLabelGenerator(int series);

    
    public void setSeriesItemLabelGenerator(int series,
                                            XYItemLabelGenerator generator);

    
    public void setSeriesItemLabelGenerator(int series,
            XYItemLabelGenerator generator, boolean notify);

    
    public XYItemLabelGenerator getBaseItemLabelGenerator();

    
    public void setBaseItemLabelGenerator(XYItemLabelGenerator generator);

    
    public void setBaseItemLabelGenerator(XYItemLabelGenerator generator,
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
                                                   ItemLabelPosition position,
                                                   boolean notify);

    
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


    

    
    public Collection getAnnotations();

    
    public void addAnnotation(XYAnnotation annotation);

    
    public void addAnnotation(XYAnnotation annotation, Layer layer);

    
    public boolean removeAnnotation(XYAnnotation annotation);

    
    public void removeAnnotations();

    
    public void drawAnnotations(Graphics2D g2,
                                Rectangle2D dataArea,
                                ValueAxis domainAxis,
                                ValueAxis rangeAxis,
                                Layer layer,
                                PlotRenderingInfo info);

    

    
    public XYItemRendererState initialise(Graphics2D g2,
                                          Rectangle2D dataArea,
                                          XYPlot plot,
                                          XYDataset dataset,
                                          PlotRenderingInfo info);

    
    public void drawItem(Graphics2D g2, XYItemRendererState state,
            Rectangle2D dataArea, XYPlot plot, ValueAxis domainAxis,
            ValueAxis rangeAxis, XYDataset dataset, int series,
            int item, boolean selected, int pass);

    
    public Shape createHotSpotShape(Graphics2D g2, Rectangle2D dataArea, 
            XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, 
            XYDataset dataset, int series, int item,
            XYItemRendererState state, boolean selected);

    
    public Rectangle2D createHotSpotBounds(Graphics2D g2, Rectangle2D dataArea, 
            XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, 
            XYDataset dataset, int series, int item, boolean selected,
            XYItemRendererState state, Rectangle2D result);
    
    
    public boolean hitTest(double xx, double yy, Graphics2D g2,
            Rectangle2D dataArea, XYPlot plot, ValueAxis domainAxis,
            ValueAxis rangeAxis, XYDataset dataset, int series, int item,
            XYItemRendererState state, boolean selected);

    
    public void drawDomainLine(Graphics2D g2, XYPlot plot, ValueAxis axis,
            Rectangle2D dataArea, double value, Paint paint, Stroke stroke);

    
    public void drawRangeLine(Graphics2D g2, XYPlot plot, ValueAxis axis,
            Rectangle2D dataArea, double value, Paint paint, Stroke stroke);

    
    public void drawDomainMarker(Graphics2D g2, XYPlot plot, ValueAxis axis,
            Marker marker, Rectangle2D dataArea);

    
    public void drawRangeMarker(Graphics2D g2, XYPlot plot, ValueAxis axis,
            Marker marker, Rectangle2D dataArea);

    
    public void fillDomainGridBand(Graphics2D g2, XYPlot plot, ValueAxis axis,
            Rectangle2D dataArea, double start, double end);

    
    public void fillRangeGridBand(Graphics2D g2, XYPlot plot, ValueAxis axis,
            Rectangle2D dataArea, double start, double end);

    
    public RenderAttributes getSelectedItemAttributes();


}
