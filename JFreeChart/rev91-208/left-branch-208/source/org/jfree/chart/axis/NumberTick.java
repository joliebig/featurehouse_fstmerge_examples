

package org.jfree.chart.axis;

import org.jfree.ui.TextAnchor;


public class NumberTick extends ValueTick {
    
    
    private Number number;
    
    
    public NumberTick(Number number, String label,
                      TextAnchor textAnchor, 
                      TextAnchor rotationAnchor, double angle) {
                        
        super(number.doubleValue(), label, textAnchor, rotationAnchor, angle);
        this.number = number;
            
    }

    
    public NumberTick(TickType tickType, double value, String label,
                      TextAnchor textAnchor, 
                      TextAnchor rotationAnchor, double angle) {
                        
        super(tickType, value, label, textAnchor, rotationAnchor, angle);
        this.number = new Double(value);
            
    }
    
    
    public Number getNumber() {
        return this.number;    
    }
    
}
