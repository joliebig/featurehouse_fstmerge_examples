

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

import org.jfree.chart.HashUtilities;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.statistics.StatisticalCategoryDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintUtilities;
import org.jfree.util.PublicCloneable;
import org.jfree.util.ShapeUtilities;


public class StatisticalLineAndShapeRenderer extends LineAndShapeRenderer
        implements Cloneable, PublicCloneable, Serializable {

    
    private static final long serialVersionUID = -3557517173697777579L;

    
    private transient Paint errorIndicatorPaint;

    
    private transient Stroke errorIndicatorStroke;

    
    public StatisticalLineAndShapeRenderer() {
        this(true, true);
    }

    
    public StatisticalLineAndShapeRenderer(boolean linesVisible,
                                           boolean shapesVisible) {
        super(linesVisible, shapesVisible);
        this.errorIndicatorPaint = null;
        this.errorIndicatorStroke = null;
    }

    
    public Paint getErrorIndicatorPaint() {
        return this.errorIndicatorPaint;
    }

    
    public void setErrorIndicatorPaint(Paint paint) {
        this.errorIndicatorPaint = paint;
        fireChangeEvent();
    }

    
    public Stroke getErrorIndicatorStroke() {
        return this.errorIndicatorStroke;
    }

    
    public void setErrorIndicatorStroke(Stroke stroke) {
        this.errorIndicatorStroke = stroke;
        fireChangeEvent();
    }

    
    public Range findRangeBounds(CategoryDataset dataset) {
        return findRangeBounds(dataset, true);
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

        
        if (!getItemVisible(row, column)) {
            return;
        }

        
        
        if (!(dataset instanceof StatisticalCategoryDataset)) {
            super.drawItem(g2, state, dataArea, plot, domainAxis, rangeAxis,
                    dataset, row, column, pass);
            return;
        }

        int visibleRow = state.getVisibleSeriesIndex(row);
        if (visibleRow < 0) {
            return;
        }
		int visibleRowCount = state.getVisibleSeriesCount();

        StatisticalCategoryDataset statDataset
                = (StatisticalCategoryDataset) dataset;
        Number meanValue = statDataset.getMeanValue(row, column);
        if (meanValue == null) {
            return;
        }
        PlotOrientation orientation = plot.getOrientation();

        
        double x1;
        if (getUseSeriesOffset()) {
            x1 = domainAxis.getCategorySeriesMiddle(column,
                    dataset.getColumnCount(),
					visibleRow, visibleRowCount,
                    getItemMargin(), dataArea, plot.getDomainAxisEdge());
        }
        else {
            x1 = domainAxis.getCategoryMiddle(column, getColumnCount(),
                    dataArea, plot.getDomainAxisEdge());
        }
        double y1 = rangeAxis.valueToJava2D(meanValue.doubleValue(), dataArea,
                plot.getRangeAxisEdge());

        
        
        
        Number sdv = statDataset.getStdDevValue(row, column);
        if (pass == 1 && sdv != null) {
            
            RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
            double valueDelta = sdv.doubleValue();
            double highVal, lowVal;
            if ((meanValue.doubleValue() + valueDelta)
                    > rangeAxis.getRange().getUpperBound()) {
                highVal = rangeAxis.valueToJava2D(
                        rangeAxis.getRange().getUpperBound(), dataArea,
                        yAxisLocation);
            }
            else {
                highVal = rangeAxis.valueToJava2D(meanValue.doubleValue()
                        + valueDelta, dataArea, yAxisLocation);
            }

            if ((meanValue.doubleValue() + valueDelta)
                    < rangeAxis.getRange().getLowerBound()) {
                lowVal = rangeAxis.valueToJava2D(
                        rangeAxis.getRange().getLowerBound(), dataArea,
                        yAxisLocation);
            }
            else {
                lowVal = rangeAxis.valueToJava2D(meanValue.doubleValue()
                        - valueDelta, dataArea, yAxisLocation);
            }

            if (this.errorIndicatorPaint != null) {
                g2.setPaint(this.errorIndicatorPaint);
            }
            else {
                g2.setPaint(getItemPaint(row, column));
            }
            if (this.errorIndicatorStroke != null) {
                g2.setStroke(this.errorIndicatorStroke);
            }
            else {
                g2.setStroke(getItemOutlineStroke(row, column));
            }
            Line2D line = new Line2D.Double();
            if (orientation == PlotOrientation.HORIZONTAL) {
                line.setLine(lowVal, x1, highVal, x1);
                g2.draw(line);
                line.setLine(lowVal, x1 - 5.0d, lowVal, x1 + 5.0d);
                g2.draw(line);
                line.setLine(highVal, x1 - 5.0d, highVal, x1 + 5.0d);
                g2.draw(line);
            }
            else {  
                line.setLine(x1, lowVal, x1, highVal);
                g2.draw(line);
                line.setLine(x1 - 5.0d, highVal, x1 + 5.0d, highVal);
                g2.draw(line);
                line.setLine(x1 - 5.0d, lowVal, x1 + 5.0d, lowVal);
                g2.draw(line);
            }

        }

        Shape hotspot = null;
        if (pass == 1 && getItemShapeVisible(row, column)) {
            Shape shape = getItemShape(row, column);
            if (orientation == PlotOrientation.HORIZONTAL) {
                shape = ShapeUtilities.createTranslatedShape(shape, y1, x1);
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                shape = ShapeUtilities.createTranslatedShape(shape, x1, y1);
            }
            hotspot = shape;
            
            if (getItemShapeFilled(row, column)) {
                if (getUseFillPaint()) {
                    g2.setPaint(getItemFillPaint(row, column));
                }
                else {
                    g2.setPaint(getItemPaint(row, column));
                }
                g2.fill(shape);
            }
            if (getDrawOutlines()) {
                if (getUseOutlinePaint()) {
                    g2.setPaint(getItemOutlinePaint(row, column));
                }
                else {
                    g2.setPaint(getItemPaint(row, column));
                }
                g2.setStroke(getItemOutlineStroke(row, column));
                g2.draw(shape);
            }
            
            if (isItemLabelVisible(row, column)) {
                if (orientation == PlotOrientation.HORIZONTAL) {
                    drawItemLabel(g2, orientation, dataset, row, column,
                            y1, x1, (meanValue.doubleValue() < 0.0));
                }
                else if (orientation == PlotOrientation.VERTICAL) {
                    drawItemLabel(g2, orientation, dataset, row, column,
                            x1, y1, (meanValue.doubleValue() < 0.0));
                }
            }
        }

        if (pass == 0 && getItemLineVisible(row, column)) {
            if (column != 0) {

                Number previousValue = statDataset.getValue(row, column - 1);
                if (previousValue != null) {

                    
                    double previous = previousValue.doubleValue();
                    double x0;
                    if (getUseSeriesOffset()) {
                        x0 = domainAxis.getCategorySeriesMiddle(
                                column - 1, dataset.getColumnCount(),
                                visibleRow, visibleRowCount,
                                getItemMargin(), dataArea,
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
            
            EntityCollection entities = state.getEntityCollection();
            if (entities != null) {
                addEntity(entities, hotspot, dataset, row, column, x1, y1);
            }
        }

    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof StatisticalLineAndShapeRenderer)) {
            return false;
        }
        StatisticalLineAndShapeRenderer that
                = (StatisticalLineAndShapeRenderer) obj;
        if (!PaintUtilities.equal(this.errorIndicatorPaint,
                that.errorIndicatorPaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.errorIndicatorStroke,
                that.errorIndicatorStroke)) {
            return false;
        }
        return super.equals(obj);
    }

    
    public int hashCode() {
        int hash = super.hashCode();
        hash = HashUtilities.hashCode(hash, this.errorIndicatorPaint);
        return hash;
    }

    
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.errorIndicatorPaint, stream);
        SerialUtilities.writeStroke(this.errorIndicatorStroke, stream);
    }

    
    private void readObject(ObjectInputStream stream)
            throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.errorIndicatorPaint = SerialUtilities.readPaint(stream);
        this.errorIndicatorStroke = SerialUtilities.readStroke(stream);
    }

}
