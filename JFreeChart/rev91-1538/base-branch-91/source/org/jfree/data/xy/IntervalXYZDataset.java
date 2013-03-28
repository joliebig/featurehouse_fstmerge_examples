

package org.jfree.data.xy;



public interface IntervalXYZDataset extends XYZDataset {

    
    public Number getStartXValue(int series, int item);

    
    public Number getEndXValue(int series, int item);

    
    public Number getStartYValue(int series, int item);

    
    public Number getEndYValue(int series, int item);

    
    public Number getStartZValue(int series, int item);

    
    public Number getEndZValue(int series, int item);

}
