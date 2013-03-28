

package org.jfree.chart.renderer.xy;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.RenderingSource;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.event.AnnotationChangeEvent;
import org.jfree.chart.event.AnnotationChangeListener;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardXYSeriesLabelGenerator;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.labels.XYSeriesLabelGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.DrawingSupplier;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.text.TextUtilities;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.chart.util.GradientPaintTransformer;
import org.jfree.chart.util.Layer;
import org.jfree.chart.util.LengthAdjustmentType;
import org.jfree.chart.util.ObjectList;
import org.jfree.chart.util.ObjectUtilities;
import org.jfree.chart.util.PublicCloneable;
import org.jfree.chart.util.RectangleAnchor;
import org.jfree.chart.util.RectangleInsets;
import org.jfree.data.Range;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.SelectableXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYDatasetSelectionState;


public abstract class AbstractXYItemRenderer extends AbstractRenderer
        implements XYItemRenderer, AnnotationChangeListener,
        Cloneable, Serializable {

    
    private static final long serialVersionUID = 8019124836026607990L;

    
    private XYPlot plot;

    
    private ObjectList itemLabelGeneratorList;

    
    private XYItemLabelGenerator baseItemLabelGenerator;

    
    private ObjectList toolTipGeneratorList;

    
    private XYToolTipGenerator baseToolTipGenerator;

    
    private ObjectList urlGeneratorList;

    
    private XYURLGenerator baseURLGenerator;

    
    private List backgroundAnnotations;

    
    private List foregroundAnnotations;

    
    private XYSeriesLabelGenerator legendItemLabelGenerator;

    
    private XYSeriesLabelGenerator legendItemToolTipGenerator;

    
    private XYSeriesLabelGenerator legendItemURLGenerator;

    
    protected AbstractXYItemRenderer() {
        super();
        this.itemLabelGeneratorList = new ObjectList();
        this.toolTipGeneratorList = new ObjectList();
        this.urlGeneratorList = new ObjectList();
        this.baseURLGenerator = null;
        this.backgroundAnnotations = new java.util.ArrayList();
        this.foregroundAnnotations = new java.util.ArrayList();
        this.legendItemLabelGenerator = new StandardXYSeriesLabelGenerator(
                "{0}");
    }

    
    public int getPassCount() {
        return 1;
    }

    
    public XYPlot getPlot() {
        return this.plot;
    }

    
    public void setPlot(XYPlot plot) {
        this.plot = plot;
    }

    
    protected XYItemRendererState createState(PlotRenderingInfo info) {
        return new XYItemRendererState(info);
    }

    
    public XYItemRendererState initialise(Graphics2D g2, Rectangle2D dataArea,
            XYPlot plot, XYDataset dataset, PlotRenderingInfo info) {

        XYItemRendererState state = createState(info);

        
        XYDatasetSelectionState selectionState = null;
        if (dataset instanceof SelectableXYDataset) {
            SelectableXYDataset sxyd = (SelectableXYDataset) dataset;
            selectionState = sxyd.getSelectionState();
        }
        
        
        if (selectionState == null && info != null) {
            ChartRenderingInfo cri = info.getOwner();
            if (cri != null) {
                RenderingSource rs = cri.getRenderingSource();
                selectionState = (XYDatasetSelectionState)
                        rs.getSelectionState(dataset);
            }
        }
        state.setSelectionState(selectionState);

        return state;
    }

    

    
    public XYItemLabelGenerator getItemLabelGenerator(int series, int item,
            boolean selected) {
        XYItemLabelGenerator generator = (XYItemLabelGenerator)
                this.itemLabelGeneratorList.get(series);
        if (generator == null) {
            generator = this.baseItemLabelGenerator;
        }
        return generator;
    }

    
    public XYItemLabelGenerator getSeriesItemLabelGenerator(int series) {
        return (XYItemLabelGenerator) this.itemLabelGeneratorList.get(series);
    }

    
    public void setSeriesItemLabelGenerator(int series,
                                            XYItemLabelGenerator generator) {
        this.itemLabelGeneratorList.set(series, generator);
        fireChangeEvent();
    }

    
    public void setSeriesItemLabelGenerator(int series,
            XYItemLabelGenerator generator, boolean notify) {
        this.itemLabelGeneratorList.set(series, generator);
        if (notify) {
            fireChangeEvent();
        }
    }

    
    public XYItemLabelGenerator getBaseItemLabelGenerator() {
        return this.baseItemLabelGenerator;
    }

    
    public void setBaseItemLabelGenerator(XYItemLabelGenerator generator) {
        setBaseItemLabelGenerator(generator, true);
    }

    
    public void setBaseItemLabelGenerator(XYItemLabelGenerator generator,
            boolean notify) {
        this.baseItemLabelGenerator = generator;
        if (notify) {
            fireChangeEvent();
        }
    }

    

    
    public XYToolTipGenerator getToolTipGenerator(int series, int item,
            boolean selected) {
        XYToolTipGenerator generator
                = (XYToolTipGenerator) this.toolTipGeneratorList.get(series);
        if (generator == null) {
            generator = this.baseToolTipGenerator;
        }
        return generator;
    }

    
    public XYToolTipGenerator getSeriesToolTipGenerator(int series) {
        return (XYToolTipGenerator) this.toolTipGeneratorList.get(series);
    }

    
    public void setSeriesToolTipGenerator(int series,
                                          XYToolTipGenerator generator) {
        setSeriesToolTipGenerator(series, generator, true);
    }

    
    public void setSeriesToolTipGenerator(int series,
            XYToolTipGenerator generator, boolean notify) {
        this.toolTipGeneratorList.set(series, generator);
        if (notify) {
            fireChangeEvent();
        }
    }

    
    public XYToolTipGenerator getBaseToolTipGenerator() {
        return this.baseToolTipGenerator;
    }

    
    public void setBaseToolTipGenerator(XYToolTipGenerator generator) {
        setBaseToolTipGenerator(generator, true);
    }

    
    public void setBaseToolTipGenerator(XYToolTipGenerator generator,
            boolean notify) {
        this.baseToolTipGenerator = generator;
        if (notify) {
            fireChangeEvent();
        }
    }

    

    
    public XYURLGenerator getURLGenerator(int series, int item,
            boolean selected) {
        XYURLGenerator generator
                = (XYURLGenerator) this.urlGeneratorList.get(series);
        if (generator == null) {
            generator = this.baseURLGenerator;
        }
        return generator;
    }

    
    public XYURLGenerator getSeriesURLGenerator(int series) {
        return (XYURLGenerator) this.urlGeneratorList.get(series);
    }

    
    public void setSeriesURLGenerator(int series, XYURLGenerator generator) {
        setSeriesURLGenerator(series, generator, true);
    }

    
    public void setSeriesURLGenerator(int series, XYURLGenerator generator,
            boolean notify) {
        this.toolTipGeneratorList.set(series, generator);
        if (notify) {
            fireChangeEvent();
        }
    }

    
    public XYURLGenerator getBaseURLGenerator() {
        return this.baseURLGenerator;
    }

    
    public void setBaseURLGenerator(XYURLGenerator generator) {
        setBaseURLGenerator(generator, true);
    }

    
    public void setBaseURLGenerator(XYURLGenerator generator, boolean notify) {
        this.baseURLGenerator = generator;
        if (notify) {
            fireChangeEvent();
        }
    }

    

    
    public void addAnnotation(XYAnnotation annotation) {
        
        addAnnotation(annotation, Layer.FOREGROUND);
    }

    
    public void addAnnotation(XYAnnotation annotation, Layer layer) {
        if (annotation == null) {
            throw new IllegalArgumentException("Null 'annotation' argument.");
        }
        if (layer.equals(Layer.FOREGROUND)) {
            this.foregroundAnnotations.add(annotation);
            annotation.addChangeListener(this);
            fireChangeEvent();
        }
        else if (layer.equals(Layer.BACKGROUND)) {
            this.backgroundAnnotations.add(annotation);
            annotation.addChangeListener(this);
            fireChangeEvent();
        }
        else {
            
            throw new RuntimeException("Unknown layer.");
        }
    }
    
    public boolean removeAnnotation(XYAnnotation annotation) {
        boolean removed = this.foregroundAnnotations.remove(annotation);
        removed = removed & this.backgroundAnnotations.remove(annotation);
        annotation.removeChangeListener(this);
        fireChangeEvent();
        return removed;
    }

    
    public void removeAnnotations() {
        for(int i = 0; i < this.foregroundAnnotations.size(); i++){
            XYAnnotation annotation
                    = (XYAnnotation) this.foregroundAnnotations.get(i);
            annotation.removeChangeListener(this);
        }
         for(int i = 0; i < this.backgroundAnnotations.size(); i++){
            XYAnnotation annotation
                    = (XYAnnotation) this.backgroundAnnotations.get(i);
            annotation.removeChangeListener(this);
        }
        this.foregroundAnnotations.clear();
        this.backgroundAnnotations.clear();
        fireChangeEvent();
    }

    
    public void annotationChanged(AnnotationChangeEvent event) {
        fireChangeEvent();
    }

    
    public Collection getAnnotations() {
        List result = new java.util.ArrayList(this.foregroundAnnotations);
        result.addAll(this.backgroundAnnotations);
        return result;
    }

    
    public XYSeriesLabelGenerator getLegendItemLabelGenerator() {
        return this.legendItemLabelGenerator;
    }

    
    public void setLegendItemLabelGenerator(XYSeriesLabelGenerator generator) {
        if (generator == null) {
            throw new IllegalArgumentException("Null 'generator' argument.");
        }
        this.legendItemLabelGenerator = generator;
        fireChangeEvent();
    }

    
    public XYSeriesLabelGenerator getLegendItemToolTipGenerator() {
        return this.legendItemToolTipGenerator;
    }

    
    public void setLegendItemToolTipGenerator(
            XYSeriesLabelGenerator generator) {
        this.legendItemToolTipGenerator = generator;
        fireChangeEvent();
    }

    
    public XYSeriesLabelGenerator getLegendItemURLGenerator() {
        return this.legendItemURLGenerator;
    }

    
    public void setLegendItemURLGenerator(XYSeriesLabelGenerator generator) {
        this.legendItemURLGenerator = generator;
        fireChangeEvent();
    }

    
    public Range findDomainBounds(XYDataset dataset) {
        return findDomainBounds(dataset, false);
    }

    
    protected Range findDomainBounds(XYDataset dataset,
            boolean includeInterval) {
        if (dataset == null) {
            return null;
        }
        if (getDataBoundsIncludesVisibleSeriesOnly()) {
            List visibleSeriesKeys = new ArrayList();
            int seriesCount = dataset.getSeriesCount();
            for (int s = 0; s < seriesCount; s++) {
                if (isSeriesVisible(s)) {
                    visibleSeriesKeys.add(dataset.getSeriesKey(s));
                }
            }
            return DatasetUtilities.findDomainBounds(dataset,
                    visibleSeriesKeys, includeInterval);
        }
        else {
            return DatasetUtilities.findDomainBounds(dataset, includeInterval);
        }
    }

    
    public Range findRangeBounds(XYDataset dataset) {
        return findRangeBounds(dataset, false);
    }

    
    protected Range findRangeBounds(XYDataset dataset,
            boolean includeInterval) {
        if (dataset == null) {
            return null;
        }
        if (getDataBoundsIncludesVisibleSeriesOnly()) {
            List visibleSeriesKeys = new ArrayList();
            int seriesCount = dataset.getSeriesCount();
            for (int s = 0; s < seriesCount; s++) {
                if (isSeriesVisible(s)) {
                    visibleSeriesKeys.add(dataset.getSeriesKey(s));
                }
            }
            
            
            Range xRange = null;
            XYPlot p = getPlot();
            if (p != null) {
                ValueAxis xAxis = null;
                int index = p.getIndexOf(this);
                if (index >= 0) {
                    xAxis = plot.getDomainAxisForDataset(index);
                }
                if (xAxis != null) {
                    xRange = xAxis.getRange();
                }
            }
            if (xRange == null) {
                xRange = new Range(Double.NEGATIVE_INFINITY,
                        Double.POSITIVE_INFINITY);
            }
            return DatasetUtilities.findRangeBounds(dataset,
                    visibleSeriesKeys, xRange, includeInterval);
        }
        else {
            return DatasetUtilities.findRangeBounds(dataset, includeInterval);
        }
    }

    
    public LegendItemCollection getLegendItems() {
        if (this.plot == null) {
            return new LegendItemCollection();
        }
        LegendItemCollection result = new LegendItemCollection();
        int index = this.plot.getIndexOf(this);
        XYDataset dataset = this.plot.getDataset(index);
        if (dataset != null) {
            int seriesCount = dataset.getSeriesCount();
            for (int i = 0; i < seriesCount; i++) {
                if (isSeriesVisibleInLegend(i)) {
                    LegendItem item = getLegendItem(index, i);
                    if (item != null) {
                        result.add(item);
                    }
                }
            }

        }
        return result;
    }

    
    public LegendItem getLegendItem(int datasetIndex, int series) {
        XYPlot xyplot = getPlot();
        if (xyplot == null) {
            return null;
        }
        XYDataset dataset = xyplot.getDataset(datasetIndex);
        if (dataset == null) {
            return null;
        }
        String label = this.legendItemLabelGenerator.generateLabel(dataset,
                series);
        String description = label;
        String toolTipText = null;
        if (getLegendItemToolTipGenerator() != null) {
            toolTipText = getLegendItemToolTipGenerator().generateLabel(
                    dataset, series);
        }
        String urlText = null;
        if (getLegendItemURLGenerator() != null) {
            urlText = getLegendItemURLGenerator().generateLabel(dataset,
                    series);
        }
        Shape shape = lookupLegendShape(series);
        Paint paint = lookupSeriesPaint(series);
        LegendItem item = new LegendItem(label, paint);
        item.setToolTipText(toolTipText);
        item.setURLText(urlText);
        item.setLabelFont(lookupLegendTextFont(series));
        Paint labelPaint = lookupLegendTextPaint(series);
        if (labelPaint != null) {
            item.setLabelPaint(labelPaint);
        }
        item.setSeriesKey(dataset.getSeriesKey(series));
        item.setSeriesIndex(series);
        item.setDataset(dataset);
        item.setDatasetIndex(datasetIndex);

        if (getTreatLegendShapeAsLine()) {
            item.setLineVisible(true);
            item.setLine(shape);
            item.setLinePaint(paint);
            item.setShapeVisible(false);
        }
        else {
            Paint outlinePaint = lookupSeriesOutlinePaint(series);
            Stroke outlineStroke = lookupSeriesOutlineStroke(series);
            item.setOutlinePaint(outlinePaint);
            item.setOutlineStroke(outlineStroke);
        }
        return item;
    }

    public Rectangle2D createHotSpotBounds(Graphics2D g2, Rectangle2D dataArea,
            XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis,
            XYDataset dataset, int series, int item, boolean selected,
            XYItemRendererState state, Rectangle2D result) {
        if (result == null) {
            result = new Rectangle();
        }
        double x = dataset.getXValue(series, item);
        double y = dataset.getYValue(series, item);
        double xx = domainAxis.valueToJava2D(x, dataArea,
                plot.getDomainAxisEdge());
        double yy = rangeAxis.valueToJava2D(y, dataArea,
                plot.getRangeAxisEdge());
        result.setRect(xx - 2, yy - 2, 4, 4);
        return result;
    }

    public Shape createHotSpotShape(Graphics2D g2, Rectangle2D dataArea,
            XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis,
            XYDataset dataset, int series, int item, XYItemRendererState state,
            boolean selected) {
        
        return createHotSpotBounds(g2, dataArea, plot, domainAxis, rangeAxis,
                dataset, series, item, selected, state, null);
    }

    
    public boolean hitTest(double xx, double yy, Graphics2D g2,
            Rectangle2D dataArea, XYPlot plot, ValueAxis domainAxis,
            ValueAxis rangeAxis, XYDataset dataset, int series, int item,
            XYItemRendererState state, boolean selected) {

        Rectangle2D bounds = createHotSpotBounds(g2, dataArea, plot, 
                domainAxis, rangeAxis, dataset, series, item, selected, 
                state, null);
        if (bounds == null) {
            return false;
        }
        
        
        return bounds.contains(xx, yy);
    }

    
    public void fillDomainGridBand(Graphics2D g2, XYPlot plot, ValueAxis axis,
            Rectangle2D dataArea, double start, double end) {

        double x1 = axis.valueToJava2D(start, dataArea,
                plot.getDomainAxisEdge());
        double x2 = axis.valueToJava2D(end, dataArea,
                plot.getDomainAxisEdge());
        Rectangle2D band;
        if (plot.getOrientation() == PlotOrientation.VERTICAL) {
            band = new Rectangle2D.Double(Math.min(x1, x2), dataArea.getMinY(),
                    Math.abs(x2 - x1), dataArea.getWidth());
        }
        else {
            band = new Rectangle2D.Double(dataArea.getMinX(), Math.min(x1, x2),
                    dataArea.getWidth(), Math.abs(x2 - x1));
        }
        Paint paint = plot.getDomainTickBandPaint();

        if (paint != null) {
            g2.setPaint(paint);
            g2.fill(band);
        }

    }

    
    public void fillRangeGridBand(Graphics2D g2, XYPlot plot, ValueAxis axis,
            Rectangle2D dataArea, double start, double end) {

        double y1 = axis.valueToJava2D(start, dataArea,
                plot.getRangeAxisEdge());
        double y2 = axis.valueToJava2D(end, dataArea, plot.getRangeAxisEdge());
        Rectangle2D band;
        if (plot.getOrientation() == PlotOrientation.VERTICAL) {
            band = new Rectangle2D.Double(dataArea.getMinX(), Math.min(y1, y2),
                dataArea.getWidth(), Math.abs(y2 - y1));
        }
        else {
            band = new Rectangle2D.Double(Math.min(y1, y2), dataArea.getMinY(),
                    Math.abs(y2 - y1), dataArea.getHeight());
        }
        Paint paint = plot.getRangeTickBandPaint();

        if (paint != null) {
            g2.setPaint(paint);
            g2.fill(band);
        }

    }

    
    public void drawDomainGridLine(Graphics2D g2,
                                   XYPlot plot,
                                   ValueAxis axis,
                                   Rectangle2D dataArea,
                                   double value) {

        Range range = axis.getRange();
        if (!range.contains(value)) {
            return;
        }

        PlotOrientation orientation = plot.getOrientation();
        double v = axis.valueToJava2D(value, dataArea,
                plot.getDomainAxisEdge());
        Line2D line = null;
        if (orientation == PlotOrientation.HORIZONTAL) {
            line = new Line2D.Double(dataArea.getMinX(), v,
                    dataArea.getMaxX(), v);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            line = new Line2D.Double(v, dataArea.getMinY(), v,
                    dataArea.getMaxY());
        }

        Paint paint = plot.getDomainGridlinePaint();
        Stroke stroke = plot.getDomainGridlineStroke();
        g2.setPaint(paint != null ? paint : Plot.DEFAULT_OUTLINE_PAINT);
        g2.setStroke(stroke != null ? stroke : Plot.DEFAULT_OUTLINE_STROKE);
        g2.draw(line);

    }

    
    public void drawDomainLine(Graphics2D g2, XYPlot plot, ValueAxis axis,
            Rectangle2D dataArea, double value, Paint paint, Stroke stroke) {

        Range range = axis.getRange();
        if (!range.contains(value)) {
            return;
        }

        PlotOrientation orientation = plot.getOrientation();
        Line2D line = null;
        double v = axis.valueToJava2D(value, dataArea,
                plot.getDomainAxisEdge());
        if (orientation == PlotOrientation.HORIZONTAL) {
            line = new Line2D.Double(dataArea.getMinX(), v, dataArea.getMaxX(),
                    v);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            line = new Line2D.Double(v, dataArea.getMinY(), v,
                    dataArea.getMaxY());
        }

        g2.setPaint(paint);
        g2.setStroke(stroke);
        g2.draw(line);

    }

    
    public void drawRangeLine(Graphics2D g2,
                              XYPlot plot,
                              ValueAxis axis,
                              Rectangle2D dataArea,
                              double value,
                              Paint paint,
                              Stroke stroke) {

        Range range = axis.getRange();
        if (!range.contains(value)) {
            return;
        }

        PlotOrientation orientation = plot.getOrientation();
        Line2D line = null;
        double v = axis.valueToJava2D(value, dataArea, plot.getRangeAxisEdge());
        if (orientation == PlotOrientation.HORIZONTAL) {
            line = new Line2D.Double(v, dataArea.getMinY(), v,
                    dataArea.getMaxY());
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            line = new Line2D.Double(dataArea.getMinX(), v,
                    dataArea.getMaxX(), v);
        }

        g2.setPaint(paint);
        g2.setStroke(stroke);
        g2.draw(line);

    }

    
    public void drawDomainMarker(Graphics2D g2,
                                 XYPlot plot,
                                 ValueAxis domainAxis,
                                 Marker marker,
                                 Rectangle2D dataArea) {

        if (marker instanceof ValueMarker) {
            ValueMarker vm = (ValueMarker) marker;
            double value = vm.getValue();
            Range range = domainAxis.getRange();
            if (!range.contains(value)) {
                return;
            }

            double v = domainAxis.valueToJava2D(value, dataArea,
                    plot.getDomainAxisEdge());

            PlotOrientation orientation = plot.getOrientation();
            Line2D line = null;
            if (orientation == PlotOrientation.HORIZONTAL) {
                line = new Line2D.Double(dataArea.getMinX(), v,
                        dataArea.getMaxX(), v);
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                line = new Line2D.Double(v, dataArea.getMinY(), v,
                        dataArea.getMaxY());
            }

            final Composite originalComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, marker.getAlpha()));
            g2.setPaint(marker.getPaint());
            g2.setStroke(marker.getStroke());
            g2.draw(line);

            String label = marker.getLabel();
            RectangleAnchor anchor = marker.getLabelAnchor();
            if (label != null) {
                Font labelFont = marker.getLabelFont();
                g2.setFont(labelFont);
                g2.setPaint(marker.getLabelPaint());
                Point2D coordinates = calculateDomainMarkerTextAnchorPoint(
                        g2, orientation, dataArea, line.getBounds2D(),
                        marker.getLabelOffset(),
                        LengthAdjustmentType.EXPAND, anchor);
                TextUtilities.drawAlignedString(label, g2,
                        (float) coordinates.getX(), (float) coordinates.getY(),
                        marker.getLabelTextAnchor());
            }
            g2.setComposite(originalComposite);
        }
        else if (marker instanceof IntervalMarker) {
            IntervalMarker im = (IntervalMarker) marker;
            double start = im.getStartValue();
            double end = im.getEndValue();
            Range range = domainAxis.getRange();
            if (!(range.intersects(start, end))) {
                return;
            }

            double start2d = domainAxis.valueToJava2D(start, dataArea,
                    plot.getDomainAxisEdge());
            double end2d = domainAxis.valueToJava2D(end, dataArea,
                    plot.getDomainAxisEdge());
            double low = Math.min(start2d, end2d);
            double high = Math.max(start2d, end2d);

            PlotOrientation orientation = plot.getOrientation();
            Rectangle2D rect = null;
            if (orientation == PlotOrientation.HORIZONTAL) {
                
                low = Math.max(low, dataArea.getMinY());
                high = Math.min(high, dataArea.getMaxY());
                rect = new Rectangle2D.Double(dataArea.getMinX(),
                        low, dataArea.getWidth(),
                        high - low);
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                
                low = Math.max(low, dataArea.getMinX());
                high = Math.min(high, dataArea.getMaxX());
                rect = new Rectangle2D.Double(low,
                        dataArea.getMinY(), high - low,
                        dataArea.getHeight());
            }

            final Composite originalComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, marker.getAlpha()));
            Paint p = marker.getPaint();
            if (p instanceof GradientPaint) {
                GradientPaint gp = (GradientPaint) p;
                GradientPaintTransformer t = im.getGradientPaintTransformer();
                if (t != null) {
                    gp = t.transform(gp, rect);
                }
                g2.setPaint(gp);
            }
            else {
                g2.setPaint(p);
            }
            g2.fill(rect);

            
            if (im.getOutlinePaint() != null && im.getOutlineStroke() != null) {
                if (orientation == PlotOrientation.VERTICAL) {
                    Line2D line = new Line2D.Double();
                    double y0 = dataArea.getMinY();
                    double y1 = dataArea.getMaxY();
                    g2.setPaint(im.getOutlinePaint());
                    g2.setStroke(im.getOutlineStroke());
                    if (range.contains(start)) {
                        line.setLine(start2d, y0, start2d, y1);
                        g2.draw(line);
                    }
                    if (range.contains(end)) {
                        line.setLine(end2d, y0, end2d, y1);
                        g2.draw(line);
                    }
                }
                else { 
                    Line2D line = new Line2D.Double();
                    double x0 = dataArea.getMinX();
                    double x1 = dataArea.getMaxX();
                    g2.setPaint(im.getOutlinePaint());
                    g2.setStroke(im.getOutlineStroke());
                    if (range.contains(start)) {
                        line.setLine(x0, start2d, x1, start2d);
                        g2.draw(line);
                    }
                    if (range.contains(end)) {
                        line.setLine(x0, end2d, x1, end2d);
                        g2.draw(line);
                    }
                }
            }

            String label = marker.getLabel();
            RectangleAnchor anchor = marker.getLabelAnchor();
            if (label != null) {
                Font labelFont = marker.getLabelFont();
                g2.setFont(labelFont);
                g2.setPaint(marker.getLabelPaint());
                Point2D coordinates = calculateDomainMarkerTextAnchorPoint(
                        g2, orientation, dataArea, rect,
                        marker.getLabelOffset(), marker.getLabelOffsetType(),
                        anchor);
                TextUtilities.drawAlignedString(label, g2,
                        (float) coordinates.getX(), (float) coordinates.getY(),
                        marker.getLabelTextAnchor());
            }
            g2.setComposite(originalComposite);

        }

    }

    
    protected Point2D calculateDomainMarkerTextAnchorPoint(Graphics2D g2,
            PlotOrientation orientation,
            Rectangle2D dataArea,
            Rectangle2D markerArea,
            RectangleInsets markerOffset,
            LengthAdjustmentType labelOffsetType,
            RectangleAnchor anchor) {

        Rectangle2D anchorRect = null;
        if (orientation == PlotOrientation.HORIZONTAL) {
            anchorRect = markerOffset.createAdjustedRectangle(markerArea,
                    LengthAdjustmentType.CONTRACT, labelOffsetType);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            anchorRect = markerOffset.createAdjustedRectangle(markerArea,
                    labelOffsetType, LengthAdjustmentType.CONTRACT);
        }
        return RectangleAnchor.coordinates(anchorRect, anchor);

    }

    
    public void drawRangeMarker(Graphics2D g2,
                                XYPlot plot,
                                ValueAxis rangeAxis,
                                Marker marker,
                                Rectangle2D dataArea) {

        if (marker instanceof ValueMarker) {
            ValueMarker vm = (ValueMarker) marker;
            double value = vm.getValue();
            Range range = rangeAxis.getRange();
            if (!range.contains(value)) {
                return;
            }

            double v = rangeAxis.valueToJava2D(value, dataArea,
                    plot.getRangeAxisEdge());
            PlotOrientation orientation = plot.getOrientation();
            Line2D line = null;
            if (orientation == PlotOrientation.HORIZONTAL) {
                line = new Line2D.Double(v, dataArea.getMinY(), v,
                        dataArea.getMaxY());
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                line = new Line2D.Double(dataArea.getMinX(), v,
                        dataArea.getMaxX(), v);
            }

            final Composite originalComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, marker.getAlpha()));
            g2.setPaint(marker.getPaint());
            g2.setStroke(marker.getStroke());
            g2.draw(line);

            String label = marker.getLabel();
            RectangleAnchor anchor = marker.getLabelAnchor();
            if (label != null) {
                Font labelFont = marker.getLabelFont();
                g2.setFont(labelFont);
                g2.setPaint(marker.getLabelPaint());
                Point2D coordinates = calculateRangeMarkerTextAnchorPoint(
                        g2, orientation, dataArea, line.getBounds2D(),
                        marker.getLabelOffset(),
                        LengthAdjustmentType.EXPAND, anchor);
                TextUtilities.drawAlignedString(label, g2,
                        (float) coordinates.getX(), (float) coordinates.getY(),
                        marker.getLabelTextAnchor());
            }
            g2.setComposite(originalComposite);
        }
        else if (marker instanceof IntervalMarker) {
            IntervalMarker im = (IntervalMarker) marker;
            double start = im.getStartValue();
            double end = im.getEndValue();
            Range range = rangeAxis.getRange();
            if (!(range.intersects(start, end))) {
                return;
            }

            double start2d = rangeAxis.valueToJava2D(start, dataArea,
                    plot.getRangeAxisEdge());
            double end2d = rangeAxis.valueToJava2D(end, dataArea,
                    plot.getRangeAxisEdge());
            double low = Math.min(start2d, end2d);
            double high = Math.max(start2d, end2d);

            PlotOrientation orientation = plot.getOrientation();
            Rectangle2D rect = null;
            if (orientation == PlotOrientation.HORIZONTAL) {
                
                low = Math.max(low, dataArea.getMinX());
                high = Math.min(high, dataArea.getMaxX());
                rect = new Rectangle2D.Double(low,
                        dataArea.getMinY(), high - low,
                        dataArea.getHeight());
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                
                low = Math.max(low, dataArea.getMinY());
                high = Math.min(high, dataArea.getMaxY());
                rect = new Rectangle2D.Double(dataArea.getMinX(),
                        low, dataArea.getWidth(),
                        high - low);
            }

            final Composite originalComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, marker.getAlpha()));
            Paint p = marker.getPaint();
            if (p instanceof GradientPaint) {
                GradientPaint gp = (GradientPaint) p;
                GradientPaintTransformer t = im.getGradientPaintTransformer();
                if (t != null) {
                    gp = t.transform(gp, rect);
                }
                g2.setPaint(gp);
            }
            else {
                g2.setPaint(p);
            }
            g2.fill(rect);

            
            if (im.getOutlinePaint() != null && im.getOutlineStroke() != null) {
                if (orientation == PlotOrientation.VERTICAL) {
                    Line2D line = new Line2D.Double();
                    double x0 = dataArea.getMinX();
                    double x1 = dataArea.getMaxX();
                    g2.setPaint(im.getOutlinePaint());
                    g2.setStroke(im.getOutlineStroke());
                    if (range.contains(start)) {
                        line.setLine(x0, start2d, x1, start2d);
                        g2.draw(line);
                    }
                    if (range.contains(end)) {
                        line.setLine(x0, end2d, x1, end2d);
                        g2.draw(line);
                    }
                }
                else { 
                    Line2D line = new Line2D.Double();
                    double y0 = dataArea.getMinY();
                    double y1 = dataArea.getMaxY();
                    g2.setPaint(im.getOutlinePaint());
                    g2.setStroke(im.getOutlineStroke());
                    if (range.contains(start)) {
                        line.setLine(start2d, y0, start2d, y1);
                        g2.draw(line);
                    }
                    if (range.contains(end)) {
                        line.setLine(end2d, y0, end2d, y1);
                        g2.draw(line);
                    }
                }
            }

            String label = marker.getLabel();
            RectangleAnchor anchor = marker.getLabelAnchor();
            if (label != null) {
                Font labelFont = marker.getLabelFont();
                g2.setFont(labelFont);
                g2.setPaint(marker.getLabelPaint());
                Point2D coordinates = calculateRangeMarkerTextAnchorPoint(
                        g2, orientation, dataArea, rect,
                        marker.getLabelOffset(), marker.getLabelOffsetType(),
                        anchor);
                TextUtilities.drawAlignedString(label, g2,
                        (float) coordinates.getX(), (float) coordinates.getY(),
                        marker.getLabelTextAnchor());
            }
            g2.setComposite(originalComposite);
        }
    }

    
    private Point2D calculateRangeMarkerTextAnchorPoint(Graphics2D g2,
                                      PlotOrientation orientation,
                                      Rectangle2D dataArea,
                                      Rectangle2D markerArea,
                                      RectangleInsets markerOffset,
                                      LengthAdjustmentType labelOffsetForRange,
                                      RectangleAnchor anchor) {

        Rectangle2D anchorRect = null;
        if (orientation == PlotOrientation.HORIZONTAL) {
            anchorRect = markerOffset.createAdjustedRectangle(markerArea,
                    labelOffsetForRange, LengthAdjustmentType.CONTRACT);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            anchorRect = markerOffset.createAdjustedRectangle(markerArea,
                    LengthAdjustmentType.CONTRACT, labelOffsetForRange);
        }
        return RectangleAnchor.coordinates(anchorRect, anchor);

    }

    
    protected Object clone() throws CloneNotSupportedException {
        AbstractXYItemRenderer clone = (AbstractXYItemRenderer) super.clone();
        

        clone.itemLabelGeneratorList
                = (ObjectList) this.itemLabelGeneratorList.clone();
        if (this.baseItemLabelGenerator != null
                && this.baseItemLabelGenerator instanceof PublicCloneable) {
            PublicCloneable pc = (PublicCloneable) this.baseItemLabelGenerator;
            clone.baseItemLabelGenerator = (XYItemLabelGenerator) pc.clone();
        }

        clone.toolTipGeneratorList
                = (ObjectList) this.toolTipGeneratorList.clone();
        if (this.baseToolTipGenerator != null
                && this.baseToolTipGenerator instanceof PublicCloneable) {
            PublicCloneable pc = (PublicCloneable) this.baseToolTipGenerator;
            clone.baseToolTipGenerator = (XYToolTipGenerator) pc.clone();
        }

        if (clone.legendItemLabelGenerator instanceof PublicCloneable) {
            clone.legendItemLabelGenerator = (XYSeriesLabelGenerator)
                    ObjectUtilities.clone(this.legendItemLabelGenerator);
        }
        if (clone.legendItemToolTipGenerator instanceof PublicCloneable) {
            clone.legendItemToolTipGenerator = (XYSeriesLabelGenerator)
                    ObjectUtilities.clone(this.legendItemToolTipGenerator);
        }
        if (clone.legendItemURLGenerator instanceof PublicCloneable) {
            clone.legendItemURLGenerator = (XYSeriesLabelGenerator)
                    ObjectUtilities.clone(this.legendItemURLGenerator);
        }

        clone.foregroundAnnotations = (List) ObjectUtilities.deepClone(
                this.foregroundAnnotations);
        clone.backgroundAnnotations = (List) ObjectUtilities.deepClone(
                this.backgroundAnnotations);

        if (clone.legendItemLabelGenerator instanceof PublicCloneable) {
            clone.legendItemLabelGenerator = (XYSeriesLabelGenerator)
                    ObjectUtilities.clone(this.legendItemLabelGenerator);
        }
        if (clone.legendItemToolTipGenerator instanceof PublicCloneable) {
            clone.legendItemToolTipGenerator = (XYSeriesLabelGenerator)
                    ObjectUtilities.clone(this.legendItemToolTipGenerator);
        }
        if (clone.legendItemURLGenerator instanceof PublicCloneable) {
            clone.legendItemURLGenerator = (XYSeriesLabelGenerator)
                    ObjectUtilities.clone(this.legendItemURLGenerator);
        }

        return clone;
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AbstractXYItemRenderer)) {
            return false;
        }
        AbstractXYItemRenderer that = (AbstractXYItemRenderer) obj;
        if (!this.itemLabelGeneratorList.equals(that.itemLabelGeneratorList)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.baseItemLabelGenerator,
                that.baseItemLabelGenerator)) {
            return false;
        }
        if (!this.toolTipGeneratorList.equals(that.toolTipGeneratorList)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.baseToolTipGenerator,
                that.baseToolTipGenerator)) {
            return false;
        }
        if (!this.urlGeneratorList.equals(that.urlGeneratorList)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.baseURLGenerator,
                that.baseURLGenerator)) {
            return false;
        }
        if (!this.foregroundAnnotations.equals(that.foregroundAnnotations)) {
            return false;
        }
        if (!this.backgroundAnnotations.equals(that.backgroundAnnotations)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.legendItemLabelGenerator,
                that.legendItemLabelGenerator)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.legendItemToolTipGenerator,
                that.legendItemToolTipGenerator)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.legendItemURLGenerator,
                that.legendItemURLGenerator)) {
            return false;
        }
        return super.equals(obj);
    }

    
    public DrawingSupplier getDrawingSupplier() {
        DrawingSupplier result = null;
        XYPlot p = getPlot();
        if (p != null) {
            result = p.getDrawingSupplier();
        }
        return result;
    }

    
    protected void updateCrosshairValues(CrosshairState crosshairState,
            double x, double y, int domainAxisIndex, int rangeAxisIndex,
            double transX, double transY, PlotOrientation orientation) {

        if (orientation == null) {
            throw new IllegalArgumentException("Null 'orientation' argument.");
        }

        if (crosshairState != null) {
            
            if (this.plot.isDomainCrosshairLockedOnData()) {
                if (this.plot.isRangeCrosshairLockedOnData()) {
                    
                    crosshairState.updateCrosshairPoint(x, y, domainAxisIndex,
                            rangeAxisIndex, transX, transY, orientation);
                }
                else {
                    
                    crosshairState.updateCrosshairX(x, domainAxisIndex);
                }
            }
            else {
                if (this.plot.isRangeCrosshairLockedOnData()) {
                    
                    crosshairState.updateCrosshairY(y, rangeAxisIndex);
                }
            }
        }

    }

    
    protected void drawItemLabel(Graphics2D g2, PlotOrientation orientation,
            XYDataset dataset, int series, int item, boolean selected,
            double x, double y, boolean negative) {

        XYItemLabelGenerator generator = getItemLabelGenerator(series, item,
                selected);
        if (generator != null) {
            Font labelFont = getItemLabelFont(series, item, selected);
            Paint paint = getItemLabelPaint(series, item, selected);
            g2.setFont(labelFont);
            g2.setPaint(paint);
            String label = generator.generateLabel(dataset, series, item);

            
            ItemLabelPosition position = null;
            if (!negative) {
                position = getPositiveItemLabelPosition(series, item,
                        selected);
            }
            else {
                position = getNegativeItemLabelPosition(series, item,
                        selected);
            }

            
            Point2D anchorPoint = calculateLabelAnchorPoint(
                    position.getItemLabelAnchor(), x, y, orientation);
            TextUtilities.drawRotatedString(label, g2,
                    (float) anchorPoint.getX(), (float) anchorPoint.getY(),
                    position.getTextAnchor(), position.getAngle(),
                    position.getRotationAnchor());
        }

    }

    
    public void drawAnnotations(Graphics2D g2,
                                Rectangle2D dataArea,
                                ValueAxis domainAxis,
                                ValueAxis rangeAxis,
                                Layer layer,
                                PlotRenderingInfo info) {

        Iterator iterator = null;
        if (layer.equals(Layer.FOREGROUND)) {
            iterator = this.foregroundAnnotations.iterator();
        }
        else if (layer.equals(Layer.BACKGROUND)) {
            iterator = this.backgroundAnnotations.iterator();
        }
        else {
            
            throw new RuntimeException("Unknown layer.");
        }
        while (iterator.hasNext()) {
            XYAnnotation annotation = (XYAnnotation) iterator.next();
            int index = this.plot.getIndexOf(this);
            annotation.draw(g2, this.plot, dataArea, domainAxis, rangeAxis,
                    index, info);
        }

    }

    
    protected void addEntity(EntityCollection entities, Shape area,
            XYDataset dataset, int series, int item, boolean selected,
            double entityX, double entityY) {
        
        if (!getItemCreateEntity(series, item, selected)) {
            return;
        }
        Shape hotspot = area;
        if (hotspot == null) {
            double r = getDefaultEntityRadius();
            double w = r * 2;
            if (getPlot().getOrientation() == PlotOrientation.VERTICAL) {
                hotspot = new Ellipse2D.Double(entityX - r, entityY - r, w, w);
            }
            else {
                hotspot = new Ellipse2D.Double(entityY - r, entityX - r, w, w);
            }
        }
        String tip = null;
        XYToolTipGenerator generator = getToolTipGenerator(series, item,
                selected);
        if (generator != null) {
            tip = generator.generateToolTip(dataset, series, item);
        }
        String url = null;
        XYURLGenerator urlster = getURLGenerator(series, item, selected);
        if (urlster != null) {
            url = urlster.generateURL(dataset, series, item);
        }
        XYItemEntity entity = new XYItemEntity(hotspot, dataset, series, item,
                tip, url);
        entities.add(entity);
    }

}
