

package org.jfree.data.xy;

import org.jfree.data.ComparableObjectItem;
import org.jfree.data.ComparableObjectSeries;


public class YIntervalSeries extends ComparableObjectSeries {
    
    
    public YIntervalSeries(Comparable key) {
        this(key, true, true);
    }
    
    
    public YIntervalSeries(Comparable key, boolean autoSort, 
            boolean allowDuplicateXValues) {
        super(key, autoSort, allowDuplicateXValues);
    }
    
    
    public void add(double x, double y, double yLow, double yHigh) {
        super.add(new YIntervalDataItem(x, y, yLow, yHigh), true);
    }
    
    
    public Number getX(int index) {
        YIntervalDataItem item = (YIntervalDataItem) getDataItem(index);
        return item.getX();
    }
    
    
    public double getYValue(int index) {
        YIntervalDataItem item = (YIntervalDataItem) getDataItem(index);
        return item.getYValue();
    }
    
    
    public double getYLowValue(int index) {
        YIntervalDataItem item = (YIntervalDataItem) getDataItem(index);
        return item.getYLowValue();
    }
    
    
    public double getYHighValue(int index) {
        YIntervalDataItem item = (YIntervalDataItem) getDataItem(index);
        return item.getYHighValue();
    }

    
    public ComparableObjectItem getDataItem(int index) {
        return super.getDataItem(index);
    }

}
