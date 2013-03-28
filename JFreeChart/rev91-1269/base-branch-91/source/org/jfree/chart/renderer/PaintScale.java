

package org.jfree.chart.renderer;

import java.awt.Paint;

import org.jfree.chart.renderer.xy.XYBlockRenderer;


public interface PaintScale {
    
    
    public double getLowerBound();
    
    
    public double getUpperBound();
    
    
    public Paint getPaint(double value);
    
}
