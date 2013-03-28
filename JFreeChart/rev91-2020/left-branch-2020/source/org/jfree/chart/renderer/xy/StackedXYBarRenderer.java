

package org.jfree.chart.renderer.xy;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.Range;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.TableXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;


public class StackedXYBarRenderer extends XYBarRenderer {

    
    private static final long serialVersionUID = -7049101055533436444L;

    
    private boolean renderAsPercentages;

    
    public StackedXYBarRenderer() {
        this(0.0);
    }

    
    public StackedXYBarRenderer(double margin) {
        super(margin);
        this.renderAsPercentages = false;

        
        
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
        fireChangeEvent();
    }

    
    public int getPassCount() {
        return 3;
    }

    
    public XYItemRendererState initialise(Graphics2D g2,
                                          Rectangle2D dataArea,
                                          XYPlot plot,
                                          XYDataset data,
                                          PlotRenderingInfo info) {
        return new XYBarRendererState(info);
    }

    
    public Range findRangeBounds(XYDataset dataset) {
        if (dataset != null) {
            if (this.renderAsPercentages) {
                return new Range(0.0, 1.0);
            }
            else {
                return DatasetUtilities.findStackedRangeBounds(
                        (TableXYDataset) dataset);
            }
        }
        else {
            return null;
        }
    }

    
    public void drawItem(Graphics2D g2,
                         XYItemRendererState state,
                         Rectangle2D dataArea,
                         PlotRenderingInfo info,
                         XYPlot plot,
                         ValueAxis domainAxis,
                         ValueAxis rangeAxis,
                         XYDataset dataset,
                         int series,
                         int item,
                         CrosshairState crosshairState,
                         int pass) {

        if (!(dataset instanceof IntervalXYDataset
                && dataset instanceof TableXYDataset)) {
            String message = "dataset (type " + dataset.getClass().getName()
                + ") has wrong type:";
            boolean and = false;
            if (!IntervalXYDataset.class.isAssignableFrom(dataset.getClass())) {
                message += " it is no IntervalXYDataset";
                and = true;
            }
            if (!TableXYDataset.class.isAssignableFrom(dataset.getClass())) {
                if (and) {
                    message += " and";
                }
                message += " it is no TableXYDataset";
            }

            throw new IllegalArgumentException(message);
        }

        IntervalXYDataset intervalDataset = (IntervalXYDataset) dataset;
        double value = intervalDataset.getYValue(series, item);
        if (Double.isNaN(value)) {
            return;
        }

        
        
        
        
        
        
        
        double total = 0.0;
        if (this.renderAsPercentages) {
            total = DatasetUtilities.calculateStackTotal(
                    (TableXYDataset) dataset, item);
            value = value / total;
        }

        double positiveBase = 0.0;
        double negativeBase = 0.0;

        for (int i = 0; i < series; i++) {
            double v = dataset.getYValue(i, item);
            if (!Double.isNaN(v)) {
                if (this.renderAsPercentages) {
                    v = v / total;
                }
                if (v > 0) {
                    positiveBase = positiveBase + v;
                }
                else {
                    negativeBase = negativeBase + v;
                }
            }
        }

        double translatedBase;
        double translatedValue;
        RectangleEdge edgeR = plot.getRangeAxisEdge();
        if (value > 0.0) {
            translatedBase = rangeAxis.valueToJava2D(positiveBase, dataArea,
                    edgeR);
            translatedValue = rangeAxis.valueToJava2D(positiveBase + value,
                    dataArea, edgeR);
        }
        else {
            translatedBase = rangeAxis.valueToJava2D(negativeBase, dataArea,
                    edgeR);
            translatedValue = rangeAxis.valueToJava2D(negativeBase + value,
                    dataArea, edgeR);
        }

        RectangleEdge edgeD = plot.getDomainAxisEdge();
        double startX = intervalDataset.getStartXValue(series, item);
        if (Double.isNaN(startX)) {
            return;
        }
        double translatedStartX = domainAxis.valueToJava2D(startX, dataArea,
                edgeD);

        double endX = intervalDataset.getEndXValue(series, item);
        if (Double.isNaN(endX)) {
            return;
        }
        double translatedEndX = domainAxis.valueToJava2D(endX, dataArea, edgeD);

        double translatedWidth = Math.max(1, Math.abs(translatedEndX
                - translatedStartX));
        double translatedHeight = Math.abs(translatedValue - translatedBase);
        if (getMargin() > 0.0) {
            double cut = translatedWidth * getMargin();
            translatedWidth = translatedWidth - cut;
            translatedStartX = translatedStartX + cut / 2;
        }

        Rectangle2D bar = null;
        PlotOrientation orientation = plot.getOrientation();
        if (orientation == PlotOrientation.HORIZONTAL) {
            bar = new Rectangle2D.Double(Math.min(translatedBase,
                    translatedValue), translatedEndX, translatedHeight,
                    translatedWidth);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            bar = new Rectangle2D.Double(translatedStartX,
                    Math.min(translatedBase, translatedValue),
                    translatedWidth, translatedHeight);
        }
        boolean positive = (value > 0.0);
        boolean inverted = rangeAxis.isInverted();
        RectangleEdge barBase;
        if (orientation == PlotOrientation.HORIZONTAL) {
            if (positive && inverted || !positive && !inverted) {
                barBase = RectangleEdge.RIGHT;
            }
            else {
                barBase = RectangleEdge.LEFT;
            }
        }
        else {
            if (positive && !inverted || !positive && inverted) {
                barBase = RectangleEdge.BOTTOM;
            }
            else {
                barBase = RectangleEdge.TOP;
            }
        }

        if (pass == 0) {
            if (getShadowsVisible()) {
                getBarPainter().paintBarShadow(g2, this, series, item, bar,
                        barBase, false);
            }
        }
        else if (pass == 1) {
            getBarPainter().paintBar(g2, this, series, item, bar, barBase);

            
            if (info != null) {
                EntityCollection entities = info.getOwner()
                        .getEntityCollection();
                if (entities != null) {
                    addEntity(entities, bar, dataset, series, item,
                            bar.getCenterX(), bar.getCenterY());
                }
            }
        }
        else if (pass == 2) {
            
            
            if (isItemLabelVisible(series, item)) {
                XYItemLabelGenerator generator = getItemLabelGenerator(series,
                        item);
                drawItemLabel(g2, dataset, series, item, plot, generator, bar,
                        value < 0.0);
            }
        }

    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof StackedXYBarRenderer)) {
            return false;
        }
        StackedXYBarRenderer that = (StackedXYBarRenderer) obj;
        if (this.renderAsPercentages != that.renderAsPercentages) {
            return false;
        }
        return super.equals(obj);
    }

    
    public int hashCode() {
        int result = super.hashCode();
        result = result * 37 + (this.renderAsPercentages ? 1 : 0);
        return result;
    }

}
