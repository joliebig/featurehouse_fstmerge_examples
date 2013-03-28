

package org.jfree.chart.axis;


public interface TickUnitSource {

    
    public TickUnit getLargerTickUnit(TickUnit unit);

    
    public TickUnit getCeilingTickUnit(TickUnit unit);

    
    public TickUnit getCeilingTickUnit(double size);
    
}
