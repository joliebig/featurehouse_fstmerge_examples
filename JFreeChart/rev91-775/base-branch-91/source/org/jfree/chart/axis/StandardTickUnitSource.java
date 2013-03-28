

package org.jfree.chart.axis;

import java.text.DecimalFormat;


public class StandardTickUnitSource implements TickUnitSource {

    
    private static final double LOG_10_VALUE = Math.log(10.0);
    
    
    public TickUnit getLargerTickUnit(TickUnit unit) {
        double x = unit.getSize();
        double log = Math.log(x) / LOG_10_VALUE;
        double higher = Math.ceil(log);
        return new NumberTickUnit(Math.pow(10, higher), 
                new DecimalFormat("0.0E0"));
    }

    
    public TickUnit getCeilingTickUnit(TickUnit unit) {
        return getLargerTickUnit(unit);
    }

    
    public TickUnit getCeilingTickUnit(double size) {
        double log = Math.log(size) / LOG_10_VALUE;
        double higher = Math.ceil(log);
        return new NumberTickUnit(Math.pow(10, higher), 
                new DecimalFormat("0.0E0"));
    }
    
}
