

package org.jfree.chart.plot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.io.Serializable;

import org.jfree.chart.event.MarkerChangeEvent;
import org.jfree.chart.util.GradientPaintTransformer;
import org.jfree.chart.util.LengthAdjustmentType;
import org.jfree.chart.util.ObjectUtilities;


public class IntervalMarker extends Marker implements Cloneable, Serializable {

    
    private static final long serialVersionUID = -1762344775267627916L;

    
    private double startValue;

    
    private double endValue;

    
    private GradientPaintTransformer gradientPaintTransformer;

    
    public IntervalMarker(double start, double end) {
        this(start, end, Color.gray, new BasicStroke(0.5f), Color.gray,
                new BasicStroke(0.5f), 0.8f);
    }

    
    public IntervalMarker(double start, double end, Paint paint) {
        this(start, end, paint, new BasicStroke(0.5f), null, null, 0.8f);
    }

    
    public IntervalMarker(double start, double end,
                          Paint paint, Stroke stroke,
                          Paint outlinePaint, Stroke outlineStroke,
                          float alpha) {

        super(paint, stroke, outlinePaint, outlineStroke, alpha);
        this.startValue = start;
        this.endValue = end;
        this.gradientPaintTransformer = null;
        setLabelOffsetType(LengthAdjustmentType.CONTRACT);

    }

    
    public double getStartValue() {
        return this.startValue;
    }

    
    public void setStartValue(double value) {
        this.startValue = value;
        notifyListeners(new MarkerChangeEvent(this));
    }

    
    public double getEndValue() {
        return this.endValue;
    }

    
    public void setEndValue(double value) {
        this.endValue = value;
        notifyListeners(new MarkerChangeEvent(this));
    }

    
    public GradientPaintTransformer getGradientPaintTransformer() {
        return this.gradientPaintTransformer;
    }

    
    public void setGradientPaintTransformer(
            GradientPaintTransformer transformer) {
        this.gradientPaintTransformer = transformer;
        notifyListeners(new MarkerChangeEvent(this));
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof IntervalMarker)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        IntervalMarker that = (IntervalMarker) obj;
        if (this.startValue != that.startValue) {
            return false;
        }
        if (this.endValue != that.endValue) {
            return false;
        }
        if (!ObjectUtilities.equal(this.gradientPaintTransformer,
                that.gradientPaintTransformer)) {
            return false;
        }
        return true;
    }

    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
