

package org.jfree.chart.renderer.category;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.LegendItem;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.util.BooleanList;
import org.jfree.chart.util.ObjectUtilities;
import org.jfree.chart.util.PublicCloneable;
import org.jfree.chart.util.ShapeUtilities;
import org.jfree.data.category.CategoryDataset;


public class LineAndShapeRenderer extends AbstractCategoryItemRenderer 
                                  implements Cloneable, PublicCloneable, 
                                             Serializable {

    
    private static final long serialVersionUID = -197749519869226398L;

    
    private BooleanList seriesLinesVisible;

    
    private boolean baseLinesVisible;

    
    private BooleanList seriesShapesVisible;

    
    private boolean baseShapesVisible;
    
    
    private BooleanList seriesShapesFilled;
    
    
    private boolean baseShapesFilled;
    
    
    private boolean useFillPaint;

    
    private boolean drawOutlines;
        
    
    private boolean useOutlinePaint;
    
    
    private boolean useSeriesOffset;

    
    private double itemMargin;
    
    
    public LineAndShapeRenderer() {
        this(true, true);
    }

    
    public LineAndShapeRenderer(boolean lines, boolean shapes) {
        super();
        this.seriesLinesVisible = new BooleanList();
        this.baseLinesVisible = lines;
        this.seriesShapesVisible = new BooleanList();
        this.baseShapesVisible = shapes;
        this.seriesShapesFilled = new BooleanList();
        this.baseShapesFilled = true;
        this.useFillPaint = false;
        this.drawOutlines = true;
        this.useOutlinePaint = false;
        this.useSeriesOffset = false;  
        this.itemMargin = 0.0;
    }
    
    

    
    public boolean getItemLineVisible(int series, int item) {
        Boolean flag = getSeriesLinesVisible(series);
        if (flag != null) {
            return flag.booleanValue();
        }
        else {
            return this.baseLinesVisible;   
        }
    }

    
    public Boolean getSeriesLinesVisible(int series) {
        return this.seriesLinesVisible.getBoolean(series);
    }

    
    public void setSeriesLinesVisible(int series, Boolean flag) {
        this.seriesLinesVisible.setBoolean(series, flag);
        notifyListeners(new RendererChangeEvent(this));
    }

    
    public void setSeriesLinesVisible(int series, boolean visible) {
        setSeriesLinesVisible(series, Boolean.valueOf(visible));
    }
    
    
    public boolean getBaseLinesVisible() {
        return this.baseLinesVisible;
    }

    
    public void setBaseLinesVisible(boolean flag) {
        this.baseLinesVisible = flag;
        notifyListeners(new RendererChangeEvent(this));
    }

    

    
    public boolean getItemShapeVisible(int series, int item) {
        Boolean flag = getSeriesShapesVisible(series);
        if (flag != null) {
            return flag.booleanValue();
        }
        else {
            return this.baseShapesVisible;   
        }
    }

    
    public Boolean getSeriesShapesVisible(int series) {
        return this.seriesShapesVisible.getBoolean(series);
    }

    
    public void setSeriesShapesVisible(int series, boolean visible) {
        setSeriesShapesVisible(series, Boolean.valueOf(visible));
    }
    
    
    public void setSeriesShapesVisible(int series, Boolean flag) {
        this.seriesShapesVisible.setBoolean(series, flag);
        notifyListeners(new RendererChangeEvent(this));
    }

    
    public boolean getBaseShapesVisible() {
        return this.baseShapesVisible;
    }

    
    public void setBaseShapesVisible(boolean flag) {
        this.baseShapesVisible = flag;
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
        Boolean flag = getSeriesShapesFilled(series);
        if (flag != null) {
            return flag.booleanValue();   
        }
        else {
            return this.baseShapesFilled;   
        }
    }

    
    public Boolean getSeriesShapesFilled(int series) {
        return this.seriesShapesFilled.getBoolean(series);
    }
    
    
    public void setSeriesShapesFilled(int series, Boolean filled) {
        this.seriesShapesFilled.setBoolean(series, filled);
        notifyListeners(new RendererChangeEvent(this));
    }

    
    public void setSeriesShapesFilled(int series, boolean filled) {
        this.seriesShapesFilled.setBoolean(series, Boolean.valueOf(filled));
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
            boolean lineVisible = getItemLineVisible(series, 0);
            boolean shapeVisible = getItemShapeVisible(series, 0);
            LegendItem result = new LegendItem(label, description, toolTipText, 
                    urlText, shapeVisible, shape, getItemShapeFilled(series, 0),
                    fillPaint, shapeOutlineVisible, outlinePaint, outlineStroke,
                    lineVisible, new Line2D.Double(-7.0, 0.0, 7.0, 0.0),
                    getItemStroke(series, 0), getItemPaint(series, 0));
            result.setDataset(dataset);
            result.setDatasetIndex(datasetIndex);
            result.setSeriesKey(dataset.getRowKey(series));
            result.setSeriesIndex(series);
            return result;
        }
        return null;

    }

    
    public int getPassCount() {
        return 2;   
    }
    
    
    public void drawItem(Graphics2D g2, CategoryItemRendererState state,
            Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis,
            ValueAxis rangeAxis, CategoryDataset dataset, int row, int column,
            int pass) {

        
        if (!getItemVisible(row, column)) {
            return;   
        }
        
        
        if (!getItemLineVisible(row, column) 
                && !getItemShapeVisible(row, column)) {
            return;
        }

        
        Number v = dataset.getValue(row, column);
        if (v == null) {
            return;
        }

        PlotOrientation orientation = plot.getOrientation();

        
        double x1;
        if (this.useSeriesOffset) {
            x1 = domainAxis.getCategorySeriesMiddle(dataset.getColumnKey(
                    column), dataset.getRowKey(row), dataset, this.itemMargin, 
                    dataArea, plot.getDomainAxisEdge());            
        }
        else {
            x1 = domainAxis.getCategoryMiddle(column, getColumnCount(), 
                    dataArea, plot.getDomainAxisEdge());
        }
        double value = v.doubleValue();
        double y1 = rangeAxis.valueToJava2D(value, dataArea, 
                plot.getRangeAxisEdge());

        if (pass == 0 && getItemLineVisible(row, column)) {
            if (column != 0) {
                Number previousValue = dataset.getValue(row, column - 1);
                if (previousValue != null) {
                    
                    double previous = previousValue.doubleValue();
                    double x0;
                    if (this.useSeriesOffset) {
                        x0 = domainAxis.getCategorySeriesMiddle(
                                dataset.getColumnKey(column - 1), 
                                dataset.getRowKey(row), dataset, 
                                this.itemMargin, dataArea, 
                                plot.getDomainAxisEdge());
                    }
                    else {
                        x0 = domainAxis.getCategoryMiddle(column - 1, 
                                getColumnCount(), dataArea, 
                                plot.getDomainAxisEdge());
                    }
                    double y0 = rangeAxis.valueToJava2D(previous, dataArea, 
                            plot.getRangeAxisEdge());

                    Line2D line = null;
                    if (orientation == PlotOrientation.HORIZONTAL) {
                        line = new Line2D.Double(y0, x0, y1, x1);
                    }
                    else if (orientation == PlotOrientation.VERTICAL) {
                        line = new Line2D.Double(x0, y0, x1, y1);
                    }
                    g2.setPaint(getItemPaint(row, column));
                    g2.setStroke(getItemStroke(row, column));
                    g2.draw(line);
                }
            }
        }

        if (pass == 1) {
            Shape shape = getItemShape(row, column);
            if (orientation == PlotOrientation.HORIZONTAL) {
                shape = ShapeUtilities.createTranslatedShape(shape, y1, x1);
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                shape = ShapeUtilities.createTranslatedShape(shape, x1, y1);
            }

            if (getItemShapeVisible(row, column)) {
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

            
            if (isItemLabelVisible(row, column)) {
                if (orientation == PlotOrientation.HORIZONTAL) {
                    drawItemLabel(g2, orientation, dataset, row, column, y1, 
                            x1, (value < 0.0));
                }
                else if (orientation == PlotOrientation.VERTICAL) {
                    drawItemLabel(g2, orientation, dataset, row, column, x1, 
                            y1, (value < 0.0));
                }
            }

            
            EntityCollection entities = state.getEntityCollection();
            if (entities != null) {
                addItemEntity(entities, dataset, row, column, shape);
            }
        }

    }
    
    
    public boolean equals(Object obj) {

        if (obj == this) {
            return true;
        }
        if (!(obj instanceof LineAndShapeRenderer)) {
            return false;
        }
        
        LineAndShapeRenderer that = (LineAndShapeRenderer) obj;
        if (this.baseLinesVisible != that.baseLinesVisible) {
            return false;
        }
        if (!ObjectUtilities.equal(this.seriesLinesVisible, 
                that.seriesLinesVisible)) {
            return false;
        }
        if (this.baseShapesVisible != that.baseShapesVisible) {
            return false;
        }
        if (!ObjectUtilities.equal(this.seriesShapesVisible, 
                that.seriesShapesVisible)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.seriesShapesFilled, 
                that.seriesShapesFilled)) {
            return false;
        }
        if (this.baseShapesFilled != that.baseShapesFilled) {
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
        LineAndShapeRenderer clone = (LineAndShapeRenderer) super.clone();
        clone.seriesLinesVisible 
                = (BooleanList) this.seriesLinesVisible.clone();
        clone.seriesShapesVisible 
                = (BooleanList) this.seriesShapesVisible.clone();
        clone.seriesShapesFilled 
                = (BooleanList) this.seriesShapesFilled.clone();
        return clone;
    }
    
}
