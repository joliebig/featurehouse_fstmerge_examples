

package org.jfree.chart.plot;

import java.awt.Paint;
import java.awt.Stroke;

import org.jfree.chart.event.MarkerChangeEvent;


public class ValueMarker extends Marker {
    
    
    private double value;

    
    public ValueMarker(double value) {
        super();
        this.value = value;
    }
    
    
    public ValueMarker(double value, Paint paint, Stroke stroke) {
        this(value, paint, stroke, paint, stroke, 1.0f);
    }
    
    
    public ValueMarker(double value, Paint paint, Stroke stroke, 
                       Paint outlinePaint, Stroke outlineStroke, float alpha) {
        super(paint, stroke, outlinePaint, outlineStroke, alpha);
        this.value = value;
    }
    
    
    public double getValue() {
        return this.value;
    }
    
    
    public void setValue(double value) { 
        this.value = value;
        notifyListeners(new MarkerChangeEvent(this));
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof ValueMarker)) {
            return false;
        }
        ValueMarker that = (ValueMarker) obj;
        if (this.value != that.value) {
            return false;
        }
        return true;
    }
}
