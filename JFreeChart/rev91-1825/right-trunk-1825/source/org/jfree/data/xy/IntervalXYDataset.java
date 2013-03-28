

package org.jfree.data.xy;


public interface IntervalXYDataset extends XYDataset {

    
    public Number getStartX(int series, int item);

    
    public double getStartXValue(int series, int item);

    
    public Number getEndX(int series, int item);

    
    public double getEndXValue(int series, int item);

    
    public Number getStartY(int series, int item);

    
    public double getStartYValue(int series, int item);

    
    public Number getEndY(int series, int item);

    
    public double getEndYValue(int series, int item);

}
