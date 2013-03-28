

package org.jfree.chart.renderer.category;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import org.jfree.chart.LegendItem;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.statistics.MultiValueCategoryDataset;
import org.jfree.util.BooleanList;
import org.jfree.util.BooleanUtilities;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;
import org.jfree.util.ShapeUtilities;


public class ScatterRenderer extends AbstractCategoryItemRenderer
        implements Cloneable, PublicCloneable, Serializable {

    
    private BooleanList seriesShapesFilled;

    
    private boolean baseShapesFilled;

    
    private boolean useFillPaint;

    
    private boolean drawOutlines;

    
    private boolean useOutlinePaint;

    
    private boolean useSeriesOffset;

    
    private double itemMargin;

    
    public ScatterRenderer() {
        this.seriesShapesFilled = new BooleanList();
        this.baseShapesFilled = true;
        this.useFillPaint = false;
        this.drawOutlines = false;
        this.useOutlinePaint = false;
        this.useSeriesOffset = true;
        this.itemMargin = 0.20;
    }

    
    public boolean getUseSeriesOffset() {
        return this.useSeriesOffset;
    }
    
    
    public void setUseSeriesOffset(boolean offset) {
        this.useSeriesOffset = offset;
        notifyListeners(new RendererChangeEvent(this));
    }
    
    
    public double getItemMargin() {
        return this.itemMargin;
    }
    
    
    public void setItemMargin(double margin) {
        if (margin < 0.0 || margin >= 1.0) {
            throw new IllegalArgumentException("Requires 0.0 <= margin < 1.0.");
        }
        this.itemMargin = margin;
        notifyListeners(new RendererChangeEvent(this));
    }

    
    public boolean getDrawOutlines() {
        return this.drawOutlines;
    }

    
    public void setDrawOutlines(boolean flag) {
        this.drawOutlines = flag;
        notifyListeners(new RendererChangeEvent(this));
    }

    
    public boolean getUseOutlinePaint() {
        return this.useOutlinePaint;
    }

    
    public void setUseOutlinePaint(boolean use) {
        this.useOutlinePaint = use;
        notifyListeners(new RendererChangeEvent(this));
    }

    

    
    public boolean getItemShapeFilled(int series, int item) {
        return getSeriesShapesFilled(series);
    }

    
    public boolean getSeriesShapesFilled(int series) {
        Boolean flag = this.seriesShapesFilled.getBoolean(series);
        if (flag != null) {
            return flag.booleanValue();
        } 
        else {
            return this.baseShapesFilled;
        }

    }

    
    public void setSeriesShapesFilled(int series, Boolean filled) {
        this.seriesShapesFilled.setBoolean(series, filled);
        notifyListeners(new RendererChangeEvent(this));
    }

    
    public void setSeriesShapesFilled(int series, boolean filled) {
        this.seriesShapesFilled.setBoolean(series, 
                BooleanUtilities.valueOf(filled));
        notifyListeners(new RendererChangeEvent(this));
    }

    
    public boolean getBaseShapesFilled() {
        return this.baseShapesFilled;
    }

    
    public void setBaseShapesFilled(boolean flag) {
        this.baseShapesFilled = flag;
        notifyListeners(new RendererChangeEvent(this));
    }

    
    public boolean getUseFillPaint() {
        return this.useFillPaint;
    }

    
    public void setUseFillPaint(boolean flag) {
        this.useFillPaint = flag;
        notifyListeners(new RendererChangeEvent(this));
    }
    
    
    public void drawItem(Graphics2D g2, CategoryItemRendererState state,
            Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis,
            ValueAxis rangeAxis, CategoryDataset dataset, int row, int column,
            int pass) {

        
        if (!getItemVisible(row, column)) {
            return;   
        }

        PlotOrientation orientation = plot.getOrientation();

        MultiValueCategoryDataset d = (MultiValueCategoryDataset) dataset;
        List values = d.getValues(row, column);
        if (values == null) {
            return;
        }
        int valueCount = values.size();
        for (int i = 0; i < valueCount; i++) {
            
            double x1;
            if (this.useSeriesOffset) {
                x1 = domainAxis.getCategorySeriesMiddle(dataset.getColumnKey(
                        column), dataset.getRowKey(row), dataset, 
                        this.itemMargin, dataArea, plot.getDomainAxisEdge());            
            }
            else {
                x1 = domainAxis.getCategoryMiddle(column, getColumnCount(), 
                        dataArea, plot.getDomainAxisEdge());
            }
            Number n = (Number) values.get(i);
            double value = n.doubleValue();
            double y1 = rangeAxis.valueToJava2D(value, dataArea, 
                    plot.getRangeAxisEdge());

            Shape shape = getItemShape(row, column);
            if (orientation == PlotOrientation.HORIZONTAL) {
                shape = ShapeUtilities.createTranslatedShape(shape, y1, x1);
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                shape = ShapeUtilities.createTranslatedShape(shape, x1, y1);
            }
            if (getItemShapeFilled(row, column)) {
                if (this.useFillPaint) {
                    g2.setPaint(getItemFillPaint(row, column));
                }
                else {
                    g2.setPaint(getItemPaint(row, column));   
                }
                g2.fill(shape);
            }
            if (this.drawOutlines) {
                if (this.useOutlinePaint) {
                    g2.setPaint(getItemOutlinePaint(row, column));   
                }
                else {
                    g2.setPaint(getItemPaint(row, column));
                }
                g2.setStroke(getItemOutlineStroke(row, column));
                g2.draw(shape);
            }
        }

    }
    
    
    public LegendItem getLegendItem(int datasetIndex, int series) {

        CategoryPlot cp = getPlot();
        if (cp == null) {
            return null;
        }

        if (isSeriesVisible(series) && isSeriesVisibleInLegend(series)) {
            CategoryDataset dataset = cp.getDataset(datasetIndex);
            String label = getLegendItemLabelGenerator().generateLabel(
                    dataset, series);
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
            Shape shape = lookupSeriesShape(series);
            Paint paint = lookupSeriesPaint(series);
            Paint fillPaint = (this.useFillPaint 
                    ? getItemFillPaint(series, 0) : paint);
            boolean shapeOutlineVisible = this.drawOutlines;
            Paint outlinePaint = (this.useOutlinePaint 
                    ? getItemOutlinePaint(series, 0) : paint);
            Stroke outlineStroke = lookupSeriesOutlineStroke(series);
            LegendItem result = new LegendItem(label, description, toolTipText, 
                    urlText, true, shape, getItemShapeFilled(series, 0),
                    fillPaint, shapeOutlineVisible, outlinePaint, outlineStroke,
                    false, new Line2D.Double(-7.0, 0.0, 7.0, 0.0),
                    getItemStroke(series, 0), getItemPaint(series, 0));
            result.setDataset(dataset);
            result.setDatasetIndex(datasetIndex);
            result.setSeriesKey(dataset.getRowKey(series));
            result.setSeriesIndex(series);
            return result;
        }
        return null;

    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ScatterRenderer)) {
            return false;
        }
        ScatterRenderer that = (ScatterRenderer) obj;
        if (!ObjectUtilities.equal(this.seriesShapesFilled,
                that.seriesShapesFilled)) {
            return false;
        }
        if (this.baseShapesFilled != that.baseShapesFilled) {
            return false;
        }
        if (this.useFillPaint != that.useFillPaint) {
            return false;
        }
        if (this.drawOutlines != that.drawOutlines) {
            return false;
        }
        if (this.useOutlinePaint != that.useOutlinePaint) {
            return false;
        }
        if (this.useSeriesOffset != that.useSeriesOffset) {
            return false;
        }
        if (this.itemMargin != that.itemMargin) {
            return false;
        }
        return super.equals(obj);
    }

    
    public Object clone() throws CloneNotSupportedException {
        ScatterRenderer clone = (ScatterRenderer) super.clone();
        clone.seriesShapesFilled 
                = (BooleanList) this.seriesShapesFilled.clone();
        return clone;
    }

    
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();

    }

    
    private void readObject(ObjectInputStream stream)
            throws IOException, ClassNotFoundException {
        stream.defaultReadObject();

    }

}
