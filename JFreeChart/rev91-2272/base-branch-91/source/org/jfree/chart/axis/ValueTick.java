
package org.jfree.chart.axis;

import org.jfree.ui.TextAnchor;


public abstract class ValueTick extends Tick {

    
    private double value;
    
    
    public ValueTick(double value, String label, 
                     TextAnchor textAnchor, TextAnchor rotationAnchor, 
                     double angle) {
                          
        super(label, textAnchor, rotationAnchor, angle);
        this.value = value;
        
    }
    
    
    public double getValue() {
        return this.value;
    }
    
    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;   
        }
        if (obj instanceof ValueTick && super.equals(obj)) {
            ValueTick vt = (ValueTick) obj;
            if (!(this.value == vt.value)) {
                return false;   
            }
            return true;
        }
        return false;
    }
    
}
