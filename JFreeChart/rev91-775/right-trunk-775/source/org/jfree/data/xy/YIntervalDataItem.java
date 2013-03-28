

package org.jfree.data.xy;

import org.jfree.data.ComparableObjectItem;


public class YIntervalDataItem extends ComparableObjectItem {

    
    public YIntervalDataItem(double x, double y, double yLow, double yHigh) {
        super(new Double(x), new YInterval(y, yLow, yHigh));
    }
    
    
    public Double getX() {
        return (Double) getComparable();
    }
    
    
    public double getYValue() {
        YInterval interval = (YInterval) getObject();
        if (interval != null) {
            return interval.getY();
        }
        else {
            return Double.NaN;
        }
    }
    
    
    public double getYLowValue() {
        YInterval interval = (YInterval) getObject();
        if (interval != null) {
            return interval.getYLow();
        }
        else {
            return Double.NaN;
        }
    }
    
    
    public double getYHighValue() {
        YInterval interval = (YInterval) getObject();
        if (interval != null) {
            return interval.getYHigh();
        }
        else {
            return Double.NaN;
        }
    }
    
}
