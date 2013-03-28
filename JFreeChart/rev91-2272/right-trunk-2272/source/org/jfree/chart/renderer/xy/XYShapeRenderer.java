

package org.jfree.chart.renderer.xy;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.LookupPaintScale;
import org.jfree.chart.renderer.PaintScale;
import org.jfree.chart.util.PublicCloneable;
import org.jfree.chart.util.SerialUtilities;
import org.jfree.chart.util.ShapeUtilities;
import org.jfree.data.Range;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYZDataset;


public class XYShapeRenderer extends AbstractXYItemRenderer
        implements XYItemRenderer, Cloneable, Serializable {

    
    private static final long serialVersionUID = 8320552104211173221L;

    
    private PaintScale paintScale;

    
    private boolean drawOutlines;

    
    private boolean useOutlinePaint;

    
    private boolean useFillPaint;

    
    private boolean guideLinesVisible;

    
    private transient Paint guideLinePaint;

    
    private transient Stroke guideLineStroke;

    
    public XYShapeRenderer() {
        this.paintScale = new LookupPaintScale();
        this.useFillPaint = false;
        this.drawOutlines = false;
        this.useOutlinePaint = true;
        this.guideLinesVisible = false;
        this.guideLinePaint = Color.darkGray;
        this.guideLineStroke = new BasicStroke();
        setBaseShape(new Ellipse2D.Double(-5.0, -5.0, 10.0, 10.0));
        setAutoPopulateSeriesShape(false);
    }

    
    public PaintScale getPaintScale() {
        return this.paintScale;
    }

    
    public void setPaintScale(PaintScale scale) {
        if (scale == null) {
            throw new IllegalArgumentException("Null 'scale' argument.");
        }
        this.paintScale = scale;
        notifyListeners(new RendererChangeEvent(this));
    }

    
    public boolean getDrawOutlines() {
        return this.drawOutlines;
    }

    
    public void setDrawOutlines(boolean flag) {
        this.drawOutlines = flag;
        fireChangeEvent();
    }

    
    public boolean getUseFillPaint() {
        return this.useFillPaint;
    }

    
    public void setUseFillPaint(boolean flag) {
        this.useFillPaint = flag;
        fireChangeEvent();
    }

    
    public boolean getUseOutlinePaint() {
        return this.useOutlinePaint;
    }

    
    public void setUseOutlinePaint(boolean use) {
        this.useOutlinePaint = use;
        fireChangeEvent();
    }

    
    public boolean isGuideLinesVisible() {
        return this.guideLinesVisible;
    }

    
    public void setGuideLinesVisible(boolean visible) {
        this.guideLinesVisible = visible;
        fireChangeEvent();
    }

    
    public Paint getGuideLinePaint() {
        return this.guideLinePaint;
    }

    
    public void setGuideLinePaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.guideLinePaint = paint;
        fireChangeEvent();
    }

    
    public Stroke getGuideLineStroke() {
        return this.guideLineStroke;
    }

    
    public void setGuideLineStroke(Stroke stroke) {
        if (stroke == null) {
            throw new IllegalArgumentException("Null 'stroke' argument.");
        }
        this.guideLineStroke = stroke;
        fireChangeEvent();
    }

    
    public Range findDomainBounds(XYDataset dataset) {
        if (dataset != null) {
            Range r = DatasetUtilities.findDomainBounds(dataset, false);
            double offset = 0; 
            return new Range(r.getLowerBound() + offset,
                             r.getUpperBound() + offset);
        }
        else {
            return null;
        }
    }

    
    public Range findRangeBounds(XYDataset dataset) {
        if (dataset != null) {
            Range r = DatasetUtilities.findRangeBounds(dataset, false);
            double offset = 0; 
            return new Range(r.getLowerBound() + offset, r.getUpperBound()
                    + offset);
        }
        else {
            return null;
        }
    }

    
    public Range findZBounds(XYZDataset dataset) {
        if (dataset != null) {
            return DatasetUtilities.findZBounds(dataset);
        }
        else {
            return null;
        }
    }

    
    public int getPassCount() {
        return 2;
    }

    
    public void drawItem(Graphics2D g2, XYItemRendererState state,
            Rectangle2D dataArea, XYPlot plot, ValueAxis domainAxis,
            ValueAxis rangeAxis, XYDataset dataset, int series, int item,
            boolean selected, int pass) {

        Shape hotspot = null;
        EntityCollection entities = null;
        if (state.getInfo() != null) {
            entities = state.getInfo().getOwner().getEntityCollection();
        }

        double x = dataset.getXValue(series, item);
        double y = dataset.getYValue(series, item);
        if (Double.isNaN(x) || Double.isNaN(y)) {
            
            return;
        }

        double transX = domainAxis.valueToJava2D(x, dataArea,
                plot.getDomainAxisEdge());
        double transY = rangeAxis.valueToJava2D(y, dataArea,
                plot.getRangeAxisEdge());

        PlotOrientation orientation = plot.getOrientation();

        
        if ((pass == 0) && this.guideLinesVisible) {
            g2.setStroke(this.guideLineStroke);
            g2.setPaint(this.guideLinePaint);
            if (orientation == PlotOrientation.HORIZONTAL) {
                g2.draw(new Line2D.Double(transY, dataArea.getMinY(), transY,
                        dataArea.getMaxY()));
                g2.draw(new Line2D.Double(dataArea.getMinX(), transX,
                        dataArea.getMaxX(), transX));
            }
            else {
                g2.draw(new Line2D.Double(transX, dataArea.getMinY(), transX,
                        dataArea.getMaxY()));
                g2.draw(new Line2D.Double(dataArea.getMinX(), transY,
                        dataArea.getMaxX(), transY));
            }
        }
        else if (pass == 1) {
            Shape shape = getItemShape(series, item, selected);
            if (orientation == PlotOrientation.HORIZONTAL) {
                shape = ShapeUtilities.createTranslatedShape(shape, transY,
                        transX);
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                shape = ShapeUtilities.createTranslatedShape(shape, transX,
                        transY);
            }
            hotspot = shape;
            if (shape.intersects(dataArea)) {
                
                    g2.setPaint(getPaint(dataset, series, item, selected));
                    g2.fill(shape);
               
                if (this.drawOutlines) {
                    if (getUseOutlinePaint()) {
                        g2.setPaint(getItemOutlinePaint(series, item,
                                selected));
                    }
                    else {
                        g2.setPaint(getItemPaint(series, item, selected));
                    }
                    g2.setStroke(getItemOutlineStroke(series, item, selected));
                    g2.draw(shape);
                }
            }

            
            if (entities != null) {
                addEntity(entities, hotspot, dataset, series, item, selected, 
                        transX, transY);
            }
        }
    }

    
    protected Paint getPaint(XYDataset dataset, int series, int item,
            boolean selected) {
        Paint p = null;
        if (dataset instanceof XYZDataset) {
            double z = ((XYZDataset) dataset).getZValue(series, item);
            p = this.paintScale.getPaint(z);
        }
        else {
            if (this.useFillPaint) {
                p = getItemFillPaint(series, item, selected);
            }
            else {
                p = getItemPaint(series, item, selected);
            }
        }
        return p;
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYShapeRenderer)) {
            return false;
        }
        XYShapeRenderer that = (XYShapeRenderer) obj;
        if (!this.paintScale.equals(that.paintScale)) {
            return false;
        }
        if (this.drawOutlines != that.drawOutlines) {
            return false;
        }
        if (this.useOutlinePaint != that.useOutlinePaint) {
            return false;
        }
        if (this.useFillPaint != that.useFillPaint) {
            return false;
        }
        if (this.guideLinesVisible != that.guideLinesVisible) {
            return false;
        }
        if (!this.guideLinePaint.equals(that.guideLinePaint)) {
            return false;
        }
        if (!this.guideLineStroke.equals(that.guideLineStroke)) {
            return false;
        }
        return super.equals(obj);
    }

    
    public Object clone() throws CloneNotSupportedException {
        XYShapeRenderer clone = (XYShapeRenderer) super.clone();
        if (this.paintScale instanceof PublicCloneable) {
            PublicCloneable pc = (PublicCloneable) this.paintScale;
            clone.paintScale = (PaintScale) pc.clone();
        }
        return clone;
    }

    
    private void readObject(ObjectInputStream stream)
            throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.guideLinePaint = SerialUtilities.readPaint(stream);
        this.guideLineStroke = SerialUtilities.readStroke(stream);
    }

    
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.guideLinePaint, stream);
        SerialUtilities.writeStroke(this.guideLineStroke, stream);
    }

}
