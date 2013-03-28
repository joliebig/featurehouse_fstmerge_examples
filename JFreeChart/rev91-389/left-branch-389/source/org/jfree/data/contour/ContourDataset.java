

package org.jfree.data.contour;

import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.XYZDataset;


public interface ContourDataset extends XYZDataset {

    
    public double getMinZValue();

    
    public double getMaxZValue();

    
    public Number[] getXValues();

    
    public Number[] getYValues();

    
    public Number[] getZValues();

    
    public int[] indexX();
    
    
    public int[] getXIndices();

    
    public Range getZValueRange(Range x, Range y);

    
    public boolean isDateAxis(int axisNumber);

}
