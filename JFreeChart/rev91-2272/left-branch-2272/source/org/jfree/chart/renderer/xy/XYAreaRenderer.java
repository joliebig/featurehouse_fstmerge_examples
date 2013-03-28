

package org.jfree.chart.renderer.xy;

import java.awt.BasicStroke;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.jfree.chart.HashUtilities;
import org.jfree.chart.LegendItem;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.labels.XYSeriesLabelGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.data.xy.XYDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.GradientPaintTransformer;
import org.jfree.ui.StandardGradientPaintTransformer;
import org.jfree.util.PublicCloneable;
import org.jfree.util.ShapeUtilities;


public class XYAreaRenderer extends AbstractXYItemRenderer
        implements XYItemRenderer, PublicCloneable {

    
    private static final long serialVersionUID = -4481971353973876747L;

    
    static class XYAreaRendererState extends XYItemRendererState {

        
        public Polygon area;

        
        public Line2D line;

        
        public XYAreaRendererState(PlotRenderingInfo info) {
            super(info);
            this.area = new Polygon();
            this.line = new Line2D.Double();
        }

    }

    
    public static final int SHAPES = 1;

    
    public static final int LINES = 2;

    
    public static final int SHAPES_AND_LINES = 3;

    
    public static final int AREA = 4;

    
    public static final int AREA_AND_SHAPES = 5;

    
    private boolean plotShapes;

    
    private boolean plotLines;

    
    private boolean plotArea;

    
    private boolean showOutline;

    
    private transient Shape legendArea;

    
    private boolean useFillPaint;

    
    private GradientPaintTransformer gradientTransformer;

    
    public XYAreaRenderer() {
        this(AREA);
    }

    
    public XYAreaRenderer(int type) {
        this(type, null, null);
    }

    
    public XYAreaRenderer(int type, XYToolTipGenerator toolTipGenerator,
                          XYURLGenerator urlGenerator) {

        super();
        setBaseToolTipGenerator(toolTipGenerator);
        setURLGenerator(urlGenerator);

        if (type == SHAPES) {
            this.plotShapes = true;
        }
        if (type == LINES) {
            this.plotLines = true;
        }
        if (type == SHAPES_AND_LINES) {
            this.plotShapes = true;
            this.plotLines = true;
        }
        if (type == AREA) {
            this.plotArea = true;
        }
        if (type == AREA_AND_SHAPES) {
            this.plotArea = true;
            this.plotShapes = true;
        }
        this.showOutline = false;
        GeneralPath area = new GeneralPath();
        area.moveTo(0.0f, -4.0f);
        area.lineTo(3.0f, -2.0f);
        area.lineTo(4.0f, 4.0f);
        area.lineTo(-4.0f, 4.0f);
        area.lineTo(-3.0f, -2.0f);
        area.closePath();
        this.legendArea = area;
        this.useFillPaint = false;
        this.gradientTransformer = new StandardGradientPaintTransformer();
    }

    
    public boolean getPlotShapes() {
        return this.plotShapes;
    }

    
    public boolean getPlotLines() {
        return this.plotLines;
    }

    
    public boolean getPlotArea() {
        return this.plotArea;
    }

    
    public boolean isOutline() {
        return this.showOutline;
    }

    
    public void setOutline(boolean show) {
        this.showOutline = show;
        fireChangeEvent();
    }

    
    public Shape getLegendArea() {
        return this.legendArea;
    }

    
    public void setLegendArea(Shape area) {
        if (area == null) {
            throw new IllegalArgumentException("Null 'area' argument.");
        }
        this.legendArea = area;
        fireChangeEvent();
    }

    
    public boolean getUseFillPaint() {
        return this.useFillPaint;
    }

    
    public void setUseFillPaint(boolean use) {
        this.useFillPaint = use;
        fireChangeEvent();
    }

    
    public GradientPaintTransformer getGradientTransformer() {
        return this.gradientTransformer;
    }

    
    public void setGradientTransformer(GradientPaintTransformer transformer) {
        if (transformer == null) {
            throw new IllegalArgumentException("Null 'transformer' argument.");
        }
        this.gradientTransformer = transformer;
        fireChangeEvent();
    }

    
    public XYItemRendererState initialise(Graphics2D g2, Rectangle2D dataArea,
            XYPlot plot, XYDataset data, PlotRenderingInfo info) {
        XYAreaRendererState state = new XYAreaRendererState(info);

        
        
        state.setProcessVisibleItemsOnly(false);
        return state;
    }

    
    public LegendItem getLegendItem(int datasetIndex, int series) {
        LegendItem result = null;
        XYPlot xyplot = getPlot();
        if (xyplot != null) {
            XYDataset dataset = xyplot.getDataset(datasetIndex);
            if (dataset != null) {
                XYSeriesLabelGenerator lg = getLegendItemLabelGenerator();
                String label = lg.generateLabel(dataset, series);
                String description = label;
                String toolTipText = null;
                if (getLegendItemToolTipGenerator() != null) {
                    toolTipText = getLegendItemToolTipGenerator().generateLabel(
                            dataset, series);
                }
                String urlText = null;
                if (getLegendItemURLGenerator() != null) {
                    urlText = getLegendItemURLGenerator().generateLabel(
                            dataset, series);
                }
                Paint paint = lookupSeriesPaint(series);
                result = new LegendItem(label, description, toolTipText,
                        urlText, this.legendArea, paint);
                result.setLabelFont(lookupLegendTextFont(series));
                Paint labelPaint = lookupLegendTextPaint(series);
                if (labelPaint != null) {
                    result.setLabelPaint(labelPaint);
                }
                result.setDataset(dataset);
                result.setDatasetIndex(datasetIndex);
                result.setSeriesKey(dataset.getSeriesKey(series));
                result.setSeriesIndex(series);
            }
        }
        return result;
    }

    
    public void drawItem(Graphics2D g2, XYItemRendererState state,
            Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot,
            ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset,
            int series, int item, CrosshairState crosshairState, int pass) {

        if (!getItemVisible(series, item)) {
            return;
        }
        XYAreaRendererState areaState = (XYAreaRendererState) state;

        
        double x1 = dataset.getXValue(series, item);
        double y1 = dataset.getYValue(series, item);
        if (Double.isNaN(y1)) {
            y1 = 0.0;
        }
        double transX1 = domainAxis.valueToJava2D(x1, dataArea,
                plot.getDomainAxisEdge());
        double transY1 = rangeAxis.valueToJava2D(y1, dataArea,
                plot.getRangeAxisEdge());

        
        
        int itemCount = dataset.getItemCount(series);
        double x0 = dataset.getXValue(series, Math.max(item - 1, 0));
        double y0 = dataset.getYValue(series, Math.max(item - 1, 0));
        if (Double.isNaN(y0)) {
            y0 = 0.0;
        }
        double transX0 = domainAxis.valueToJava2D(x0, dataArea,
                plot.getDomainAxisEdge());
        double transY0 = rangeAxis.valueToJava2D(y0, dataArea,
                plot.getRangeAxisEdge());

        double x2 = dataset.getXValue(series, Math.min(item + 1,
                itemCount - 1));
        double y2 = dataset.getYValue(series, Math.min(item + 1,
                itemCount - 1));
        if (Double.isNaN(y2)) {
            y2 = 0.0;
        }
        double transX2 = domainAxis.valueToJava2D(x2, dataArea,
                plot.getDomainAxisEdge());
        double transY2 = rangeAxis.valueToJava2D(y2, dataArea,
                plot.getRangeAxisEdge());

        double transZero = rangeAxis.valueToJava2D(0.0, dataArea,
                plot.getRangeAxisEdge());
        Polygon hotspot = null;
        if (plot.getOrientation() == PlotOrientation.HORIZONTAL) {
            hotspot = new Polygon();
            hotspot.addPoint((int) transZero,
                    (int) ((transX0 + transX1) / 2.0));
            hotspot.addPoint((int) ((transY0 + transY1) / 2.0),
                    (int) ((transX0 + transX1) / 2.0));
            hotspot.addPoint((int) transY1, (int) transX1);
            hotspot.addPoint((int) ((transY1 + transY2) / 2.0),
                    (int) ((transX1 + transX2) / 2.0));
            hotspot.addPoint((int) transZero,
                    (int) ((transX1 + transX2) / 2.0));
        }
        else {  
            hotspot = new Polygon();
            hotspot.addPoint((int) ((transX0 + transX1) / 2.0),
                    (int) transZero);
            hotspot.addPoint((int) ((transX0 + transX1) / 2.0),
                    (int) ((transY0 + transY1) / 2.0));
            hotspot.addPoint((int) transX1, (int) transY1);
            hotspot.addPoint((int) ((transX1 + transX2) / 2.0),
                    (int) ((transY1 + transY2) / 2.0));
            hotspot.addPoint((int) ((transX1 + transX2) / 2.0),
                    (int) transZero);
        }

        if (item == 0) {  
            areaState.area = new Polygon();
            
            double zero = rangeAxis.valueToJava2D(0.0, dataArea,
                    plot.getRangeAxisEdge());
            if (plot.getOrientation() == PlotOrientation.VERTICAL) {
                areaState.area.addPoint((int) transX1, (int) zero);
            }
            else if (plot.getOrientation() == PlotOrientation.HORIZONTAL) {
                areaState.area.addPoint((int) zero, (int) transX1);
            }
        }

        
        if (plot.getOrientation() == PlotOrientation.VERTICAL) {
            areaState.area.addPoint((int) transX1, (int) transY1);
        }
        else if (plot.getOrientation() == PlotOrientation.HORIZONTAL) {
            areaState.area.addPoint((int) transY1, (int) transX1);
        }

        PlotOrientation orientation = plot.getOrientation();
        Paint paint = getItemPaint(series, item);
        Stroke stroke = getItemStroke(series, item);
        g2.setPaint(paint);
        g2.setStroke(stroke);

        Shape shape = null;
        if (getPlotShapes()) {
            shape = getItemShape(series, item);
            if (orientation == PlotOrientation.VERTICAL) {
                shape = ShapeUtilities.createTranslatedShape(shape, transX1,
                        transY1);
            }
            else if (orientation == PlotOrientation.HORIZONTAL) {
                shape = ShapeUtilities.createTranslatedShape(shape, transY1,
                        transX1);
            }
            g2.draw(shape);
        }

        if (getPlotLines()) {
            if (item > 0) {
                if (plot.getOrientation() == PlotOrientation.VERTICAL) {
                    areaState.line.setLine(transX0, transY0, transX1, transY1);
                }
                else if (plot.getOrientation() == PlotOrientation.HORIZONTAL) {
                    areaState.line.setLine(transY0, transX0, transY1, transX1);
                }
                g2.draw(areaState.line);
            }
        }

        
        
        if (getPlotArea() && item > 0 && item == (itemCount - 1)) {

            if (orientation == PlotOrientation.VERTICAL) {
                
                areaState.area.addPoint((int) transX1, (int) transZero);
            }
            else if (orientation == PlotOrientation.HORIZONTAL) {
                
                areaState.area.addPoint((int) transZero, (int) transX1);
            }

            if (this.useFillPaint) {
                paint = lookupSeriesFillPaint(series);
            }
            if (paint instanceof GradientPaint) {
                GradientPaint gp = (GradientPaint) paint;
                GradientPaint adjGP = this.gradientTransformer.transform(gp,
                        dataArea);
                g2.setPaint(adjGP);
            }
            g2.fill(areaState.area);

            
            if (isOutline()) {
                Shape area = areaState.area;

                
                
                
                
                
                Stroke outlineStroke = lookupSeriesOutlineStroke(series);
                if (outlineStroke instanceof BasicStroke) {
                    BasicStroke bs = (BasicStroke) outlineStroke;
                    if (bs.getDashArray() != null) {
                        Area poly = new Area(areaState.area);
                        
                        
                        
                        Area clip = new Area(new Rectangle2D.Double(
                                dataArea.getX() - 5.0, dataArea.getY() - 5.0,
                                dataArea.getWidth() + 10.0,
                                dataArea.getHeight() + 10.0));
                        poly.intersect(clip);
                        area = poly;
                    }
                } 

                g2.setStroke(outlineStroke);
                g2.setPaint(lookupSeriesOutlinePaint(series));
                g2.draw(area);
            }
        }

        int domainAxisIndex = plot.getDomainAxisIndex(domainAxis);
        int rangeAxisIndex = plot.getRangeAxisIndex(rangeAxis);
        updateCrosshairValues(crosshairState, x1, y1, domainAxisIndex,
                rangeAxisIndex, transX1, transY1, orientation);

        
        EntityCollection entities = state.getEntityCollection();
        if (entities != null && hotspot != null) {
            addEntity(entities, hotspot, dataset, series, item, 0.0, 0.0);
        }

    }

    
    public Object clone() throws CloneNotSupportedException {
        XYAreaRenderer clone = (XYAreaRenderer) super.clone();
        clone.legendArea = ShapeUtilities.clone(this.legendArea);
        return clone;
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYAreaRenderer)) {
            return false;
        }
        XYAreaRenderer that = (XYAreaRenderer) obj;
        if (this.plotArea != that.plotArea) {
            return false;
        }
        if (this.plotLines != that.plotLines) {
            return false;
        }
        if (this.plotShapes != that.plotShapes) {
            return false;
        }
        if (this.showOutline != that.showOutline) {
            return false;
        }
        if (this.useFillPaint != that.useFillPaint) {
            return false;
        }
        if (!this.gradientTransformer.equals(that.gradientTransformer)) {
            return false;
        }
        if (!ShapeUtilities.equal(this.legendArea, that.legendArea)) {
            return false;
        }
        return true;
    }

    
    public int hashCode() {
        int result = super.hashCode();
        result = HashUtilities.hashCode(result, this.plotArea);
        result = HashUtilities.hashCode(result, this.plotLines);
        result = HashUtilities.hashCode(result, this.plotShapes);
        result = HashUtilities.hashCode(result, this.useFillPaint);
        return result;
    }

    
    private void readObject(ObjectInputStream stream)
            throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.legendArea = SerialUtilities.readShape(stream);
    }

    
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeShape(this.legendArea, stream);
    }
}
