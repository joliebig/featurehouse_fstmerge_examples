

package org.jfree.data.xy;

import org.jfree.data.ComparableObjectItem;
import org.jfree.data.ComparableObjectSeries;


public class XIntervalSeries extends ComparableObjectSeries {
    
    
    public XIntervalSeries(Comparable key) {
        this(key, true, true);
    }
    
    
    public XIntervalSeries(Comparable key, boolean autoSort, 
            boolean allowDuplicateXValues) {
        super(key, autoSort, allowDuplicateXValues);
    }
    
    
    public void add(double x, double xLow, double xHigh, double y) {
        super.add(new XIntervalDataItem(x, xLow, xHigh, y), true);
    }
    
    
    public Number getX(int index) {
        XIntervalDataItem item = (XIntervalDataItem) getDataItem(index);
        return item.getX();
    }
    
    
    public double getYValue(int index) {
        XIntervalDataItem item = (XIntervalDataItem) getDataItem(index);
        return item.getYValue();
    }
    
    
    public ComparableObjectItem getDataItem(int index) {
        return super.getDataItem(index);
    }
    
}
