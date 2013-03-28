

package org.jfree.chart.plot;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.AxisState;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.axis.ValueTick;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.util.ArrayUtilities;
import org.jfree.chart.util.ObjectUtilities;
import org.jfree.chart.util.PaintUtilities;
import org.jfree.chart.util.RectangleEdge;
import org.jfree.chart.util.RectangleInsets;
import org.jfree.chart.util.SerialUtilities;
import org.jfree.data.Range;


public class FastScatterPlot extends Plot implements ValueAxisPlot,
        Zoomable, Cloneable, Serializable {

    
    private static final long serialVersionUID = 7871545897358563521L;

    
    public static final Stroke DEFAULT_GRIDLINE_STROKE = new BasicStroke(0.5f,
            BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0.0f, new float[]
            {2.0f, 2.0f}, 0.0f);

    
    public static final Paint DEFAULT_GRIDLINE_PAINT = Color.lightGray;

    
    private float[][] data;

    
    private Range xDataRange;

    
    private Range yDataRange;

    
    private ValueAxis domainAxis;

    
    private ValueAxis rangeAxis;

    
    private transient Paint paint;

    
    private boolean domainGridlinesVisible;

    
    private transient Stroke domainGridlineStroke;

    
    private transient Paint domainGridlinePaint;

    
    private boolean rangeGridlinesVisible;

    
    private transient Stroke rangeGridlineStroke;

    
    private transient Paint rangeGridlinePaint;

    
    protected static ResourceBundle localizationResources =
            ResourceBundle.getBundle(
            "org.jfree.chart.plot.LocalizationBundle");

    
    public FastScatterPlot() {
        this(null, new NumberAxis("X"), new NumberAxis("Y"));
    }

    
    public FastScatterPlot(float[][] data,
                           ValueAxis domainAxis, ValueAxis rangeAxis) {

        super();
        if (domainAxis == null) {
            throw new IllegalArgumentException("Null 'domainAxis' argument.");
        }
        if (rangeAxis == null) {
            throw new IllegalArgumentException("Null 'rangeAxis' argument.");
        }

        this.data = data;
        this.xDataRange = calculateXDataRange(data);
        this.yDataRange = calculateYDataRange(data);
        this.domainAxis = domainAxis;
        this.domainAxis.setPlot(this);
        this.domainAxis.addChangeListener(this);
        this.rangeAxis = rangeAxis;
        this.rangeAxis.setPlot(this);
        this.rangeAxis.addChangeListener(this);

        this.paint = Color.red;

        this.domainGridlinesVisible = true;
        this.domainGridlinePaint = FastScatterPlot.DEFAULT_GRIDLINE_PAINT;
        this.domainGridlineStroke = FastScatterPlot.DEFAULT_GRIDLINE_STROKE;

        this.rangeGridlinesVisible = true;
        this.rangeGridlinePaint = FastScatterPlot.DEFAULT_GRIDLINE_PAINT;
        this.rangeGridlineStroke = FastScatterPlot.DEFAULT_GRIDLINE_STROKE;

    }

    
    public String getPlotType() {
        return localizationResources.getString("Fast_Scatter_Plot");
    }

    
    public float[][] getData() {
        return this.data;
    }

    
    public void setData(float[][] data) {
        this.data = data;
        fireChangeEvent();
    }

    
    public PlotOrientation getOrientation() {
        return PlotOrientation.VERTICAL;
    }

    
    public ValueAxis getDomainAxis() {
        return this.domainAxis;
    }

    
    public void setDomainAxis(ValueAxis axis) {
        if (axis == null) {
            throw new IllegalArgumentException("Null 'axis' argument.");
        }
        this.domainAxis = axis;
        fireChangeEvent();
    }

    
    public ValueAxis getRangeAxis() {
        return this.rangeAxis;
    }

    
    public void setRangeAxis(ValueAxis axis) {
        if (axis == null) {
            throw new IllegalArgumentException("Null 'axis' argument.");
        }
        this.rangeAxis = axis;
        fireChangeEvent();
    }

    
    public Paint getPaint() {
        return this.paint;
    }

    
    public void setPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.paint = paint;
        fireChangeEvent();
    }

    
    public boolean isDomainGridlinesVisible() {
        return this.domainGridlinesVisible;
    }

    
    public void setDomainGridlinesVisible(boolean visible) {
        if (this.domainGridlinesVisible != visible) {
            this.domainGridlinesVisible = visible;
            fireChangeEvent();
        }
    }

    
    public Stroke getDomainGridlineStroke() {
        return this.domainGridlineStroke;
    }

    
    public void setDomainGridlineStroke(Stroke stroke) {
        if (stroke == null) {
            throw new IllegalArgumentException("Null 'stroke' argument.");
        }
        this.domainGridlineStroke = stroke;
        fireChangeEvent();
    }

    
    public Paint getDomainGridlinePaint() {
        return this.domainGridlinePaint;
    }

    
    public void setDomainGridlinePaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.domainGridlinePaint = paint;
        fireChangeEvent();
    }

    
    public boolean isRangeGridlinesVisible() {
        return this.rangeGridlinesVisible;
    }

    
    public void setRangeGridlinesVisible(boolean visible) {
        if (this.rangeGridlinesVisible != visible) {
            this.rangeGridlinesVisible = visible;
            fireChangeEvent();
        }
    }

    
    public Stroke getRangeGridlineStroke() {
        return this.rangeGridlineStroke;
    }

    
    public void setRangeGridlineStroke(Stroke stroke) {
        if (stroke == null) {
            throw new IllegalArgumentException("Null 'stroke' argument.");
        }
        this.rangeGridlineStroke = stroke;
        fireChangeEvent();
    }

    
    public Paint getRangeGridlinePaint() {
        return this.rangeGridlinePaint;
    }

    
    public void setRangeGridlinePaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.rangeGridlinePaint = paint;
        fireChangeEvent();
    }

    
    public void draw(Graphics2D g2, Rectangle2D area, Point2D anchor,
                     PlotState parentState,
                     PlotRenderingInfo info) {

        
        if (info != null) {
            info.setPlotArea(area);
        }

        
        RectangleInsets insets = getInsets();
        insets.trim(area);

        AxisSpace space = new AxisSpace();
        space = this.domainAxis.reserveSpace(g2, this, area,
                RectangleEdge.BOTTOM, space);
        space = this.rangeAxis.reserveSpace(g2, this, area, RectangleEdge.LEFT,
                space);
        Rectangle2D dataArea = space.shrink(area, null);

        if (info != null) {
            info.setDataArea(dataArea);
        }

        
        drawBackground(g2, dataArea);

        AxisState domainAxisState = this.domainAxis.draw(g2,
                dataArea.getMaxY(), area, dataArea, RectangleEdge.BOTTOM, info);
        AxisState rangeAxisState = this.rangeAxis.draw(g2, dataArea.getMinX(),
                area, dataArea, RectangleEdge.LEFT, info);
        drawDomainGridlines(g2, dataArea, domainAxisState.getTicks());
        drawRangeGridlines(g2, dataArea, rangeAxisState.getTicks());

        Shape originalClip = g2.getClip();
        Composite originalComposite = g2.getComposite();

        g2.clip(dataArea);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                getForegroundAlpha()));

        render(g2, dataArea, info, null);

        g2.setClip(originalClip);
        g2.setComposite(originalComposite);
        drawOutline(g2, dataArea);

    }

    
    public void render(Graphics2D g2, Rectangle2D dataArea,
                       PlotRenderingInfo info, CrosshairState crosshairState) {


        
        
        g2.setPaint(this.paint);

        
        
        
        
        
        
        
        
        
        
        
        

        if (this.data != null) {
            for (int i = 0; i < this.data[0].length; i++) {
                float x = this.data[0][i];
                float y = this.data[1][i];

                
                
                int transX = (int) this.domainAxis.valueToJava2D(x, dataArea,
                        RectangleEdge.BOTTOM);
                int transY = (int) this.rangeAxis.valueToJava2D(y, dataArea,
                        RectangleEdge.LEFT);
                g2.fillRect(transX, transY, 1, 1);
            }
        }
        
        
        

    }

    
    protected void drawDomainGridlines(Graphics2D g2, Rectangle2D dataArea,
                                       List ticks) {

        
        if (isDomainGridlinesVisible()) {
            Iterator iterator = ticks.iterator();
            while (iterator.hasNext()) {
                ValueTick tick = (ValueTick) iterator.next();
                double v = this.domainAxis.valueToJava2D(tick.getValue(),
                        dataArea, RectangleEdge.BOTTOM);
                Line2D line = new Line2D.Double(v, dataArea.getMinY(), v,
                        dataArea.getMaxY());
                g2.setPaint(getDomainGridlinePaint());
                g2.setStroke(getDomainGridlineStroke());
                g2.draw(line);
            }
        }
    }

    
    protected void drawRangeGridlines(Graphics2D g2, Rectangle2D dataArea,
                                      List ticks) {

        
        if (isRangeGridlinesVisible()) {
            Iterator iterator = ticks.iterator();
            while (iterator.hasNext()) {
                ValueTick tick = (ValueTick) iterator.next();
                double v = this.rangeAxis.valueToJava2D(tick.getValue(),
                        dataArea, RectangleEdge.LEFT);
                Line2D line = new Line2D.Double(dataArea.getMinX(), v,
                        dataArea.getMaxX(), v);
                g2.setPaint(getRangeGridlinePaint());
                g2.setStroke(getRangeGridlineStroke());
                g2.draw(line);
            }
        }

    }

    
    public Range getDataRange(ValueAxis axis) {
        Range result = null;
        if (axis == this.domainAxis) {
            result = this.xDataRange;
        }
        else if (axis == this.rangeAxis) {
            result = this.yDataRange;
        }
        return result;
    }

    
    private Range calculateXDataRange(float[][] data) {

        Range result = null;

        if (data != null) {
            float lowest = Float.POSITIVE_INFINITY;
            float highest = Float.NEGATIVE_INFINITY;
            for (int i = 0; i < data[0].length; i++) {
                float v = data[0][i];
                if (v < lowest) {
                    lowest = v;
                }
                if (v > highest) {
                    highest = v;
                }
            }
            if (lowest <= highest) {
                result = new Range(lowest, highest);
            }
        }

        return result;

    }

    
    private Range calculateYDataRange(float[][] data) {

        Range result = null;

        if (data != null) {
            float lowest = Float.POSITIVE_INFINITY;
            float highest = Float.NEGATIVE_INFINITY;
            for (int i = 0; i < data[0].length; i++) {
                float v = data[1][i];
                if (v < lowest) {
                    lowest = v;
                }
                if (v > highest) {
                    highest = v;
                }
            }
            if (lowest <= highest) {
                result = new Range(lowest, highest);
            }
        }
        return result;

    }

    
    public void zoomDomainAxes(double factor, PlotRenderingInfo info,
                               Point2D source) {
        this.domainAxis.resizeRange(factor);
    }

    
    public void zoomDomainAxes(double factor, PlotRenderingInfo info,
                               Point2D source, boolean useAnchor) {

        if (useAnchor) {
            
            
            double sourceX = source.getX();
            double anchorX = this.domainAxis.java2DToValue(sourceX,
                    info.getDataArea(), RectangleEdge.BOTTOM);
            this.domainAxis.resizeRange(factor, anchorX);
        }
        else {
            this.domainAxis.resizeRange(factor);
        }

    }

    
    public void zoomDomainAxes(double lowerPercent, double upperPercent,
                               PlotRenderingInfo info, Point2D source) {
        this.domainAxis.zoomRange(lowerPercent, upperPercent);
    }

    
    public void zoomRangeAxes(double factor,
                              PlotRenderingInfo info, Point2D source) {
        this.rangeAxis.resizeRange(factor);
    }

    
    public void zoomRangeAxes(double factor, PlotRenderingInfo info,
                              Point2D source, boolean useAnchor) {

        if (useAnchor) {
            
            
            double sourceX = source.getX();
            double anchorX = this.rangeAxis.java2DToValue(sourceX,
                    info.getDataArea(), RectangleEdge.LEFT);
            this.rangeAxis.resizeRange(factor, anchorX);
        }
        else {
            this.rangeAxis.resizeRange(factor);
        }

    }

    
    public void zoomRangeAxes(double lowerPercent, double upperPercent,
                              PlotRenderingInfo info, Point2D source) {
        this.rangeAxis.zoomRange(lowerPercent, upperPercent);
    }

    
    public boolean isDomainZoomable() {
        return true;
    }

    
    public boolean isRangeZoomable() {
        return true;
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof FastScatterPlot)) {
            return false;
        }
        FastScatterPlot that = (FastScatterPlot) obj;
        if (!ArrayUtilities.equal(this.data, that.data)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.domainAxis, that.domainAxis)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.rangeAxis, that.rangeAxis)) {
            return false;
        }
        if (!PaintUtilities.equal(this.paint, that.paint)) {
            return false;
        }
        if (this.domainGridlinesVisible != that.domainGridlinesVisible) {
            return false;
        }
        if (!PaintUtilities.equal(this.domainGridlinePaint,
                that.domainGridlinePaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.domainGridlineStroke,
                that.domainGridlineStroke)) {
            return false;
        }
        if (!this.rangeGridlinesVisible == that.rangeGridlinesVisible) {
            return false;
        }
        if (!PaintUtilities.equal(this.rangeGridlinePaint,
                that.rangeGridlinePaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.rangeGridlineStroke,
                that.rangeGridlineStroke)) {
            return false;
        }
        return true;
    }

    
    public Object clone() throws CloneNotSupportedException {

        FastScatterPlot clone = (FastScatterPlot) super.clone();

        if (this.data != null) {
            clone.data = ArrayUtilities.clone(this.data);
        }

        if (this.domainAxis != null) {
            clone.domainAxis = (ValueAxis) this.domainAxis.clone();
            clone.domainAxis.setPlot(clone);
            clone.domainAxis.addChangeListener(clone);
        }

        if (this.rangeAxis != null) {
            clone.rangeAxis = (ValueAxis) this.rangeAxis.clone();
            clone.rangeAxis.setPlot(clone);
            clone.rangeAxis.addChangeListener(clone);
        }

        return clone;

    }

    
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.paint, stream);
        SerialUtilities.writeStroke(this.domainGridlineStroke, stream);
        SerialUtilities.writePaint(this.domainGridlinePaint, stream);
        SerialUtilities.writeStroke(this.rangeGridlineStroke, stream);
        SerialUtilities.writePaint(this.rangeGridlinePaint, stream);
    }

    
    private void readObject(ObjectInputStream stream)
            throws IOException, ClassNotFoundException {
        stream.defaultReadObject();

        this.paint = SerialUtilities.readPaint(stream);
        this.domainGridlineStroke = SerialUtilities.readStroke(stream);
        this.domainGridlinePaint = SerialUtilities.readPaint(stream);

        this.rangeGridlineStroke = SerialUtilities.readStroke(stream);
        this.rangeGridlinePaint = SerialUtilities.readPaint(stream);

        if (this.domainAxis != null) {
            this.domainAxis.addChangeListener(this);
        }

        if (this.rangeAxis != null) {
            this.rangeAxis.addChangeListener(this);
        }
    }

}
