

package org.jfree.data.xy;



public abstract class AbstractIntervalXYDataset extends AbstractXYDataset 
                                                implements IntervalXYDataset {

    
    public double getStartXValue(int series, int item) {
        double result = Double.NaN;
        Number x = getStartX(series, item);
        if (x != null) {
            result = x.doubleValue();   
        }
        return result;   
    }

    
    public double getEndXValue(int series, int item) {
        double result = Double.NaN;
        Number x = getEndX(series, item);
        if (x != null) {
            result = x.doubleValue();   
        }
        return result;   
    }

    
    public double getStartYValue(int series, int item) {
        double result = Double.NaN;
        Number y = getStartY(series, item);
        if (y != null) {
            result = y.doubleValue();   
        }
        return result;   
    }

    
    public double getEndYValue(int series, int item) {
        double result = Double.NaN;
        Number y = getEndY(series, item);
        if (y != null) {
            result = y.doubleValue();   
        }
        return result;   
    }

}
