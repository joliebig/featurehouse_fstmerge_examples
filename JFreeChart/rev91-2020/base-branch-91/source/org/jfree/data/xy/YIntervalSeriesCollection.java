

package org.jfree.data.xy;

import java.io.Serializable;
import java.util.List;

import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.util.ObjectUtilities;


public class YIntervalSeriesCollection extends AbstractIntervalXYDataset
                                implements IntervalXYDataset, Serializable {

    
    private List data;
    
    
    public YIntervalSeriesCollection() {
        this.data = new java.util.ArrayList();
    }

    
    public void addSeries(YIntervalSeries series) {
        if (series == null) {
            throw new IllegalArgumentException("Null 'series' argument.");
        }
        this.data.add(series);
        series.addChangeListener(this);
        fireDatasetChanged();
    }

    
    public int getSeriesCount() {
        return this.data.size();
    }

    
    public YIntervalSeries getSeries(int series) {
        if ((series < 0) || (series >= getSeriesCount())) {
            throw new IllegalArgumentException("Series index out of bounds");
        }
        return (YIntervalSeries) this.data.get(series);
    }

    
    public Comparable getSeriesKey(int series) {
        
        return getSeries(series).getKey();
    }

    
    public int getItemCount(int series) {
        
        return getSeries(series).getItemCount();
    }

    
    public Number getX(int series, int item) {
        YIntervalSeries s = (YIntervalSeries) this.data.get(series);
        return s.getX(item);
    }

    
    public double getYValue(int series, int item) {
        YIntervalSeries s = (YIntervalSeries) this.data.get(series);
        return s.getYValue(item);
    }

    
    public double getStartYValue(int series, int item) {
        YIntervalSeries s = (YIntervalSeries) this.data.get(series);
        return s.getYLowValue(item);
    }

    
    public double getEndYValue(int series, int item) {
        YIntervalSeries s = (YIntervalSeries) this.data.get(series);
        return s.getYHighValue(item);
    }

    
    public Number getY(int series, int item) {
        YIntervalSeries s = (YIntervalSeries) this.data.get(series);
        return new Double(s.getYValue(item));
    }

    
    public Number getStartX(int series, int item) {
        return getX(series, item);
    }

    
    public Number getEndX(int series, int item) {
        return getX(series, item);
    }

    
    public Number getStartY(int series, int item) {
        YIntervalSeries s = (YIntervalSeries) this.data.get(series);
        return new Double(s.getYLowValue(item));
    }

    
    public Number getEndY(int series, int item) {
        YIntervalSeries s = (YIntervalSeries) this.data.get(series);
        return new Double(s.getYHighValue(item));
    }
    
    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof YIntervalSeriesCollection)) {
            return false;
        }
        YIntervalSeriesCollection that = (YIntervalSeriesCollection) obj;
        return ObjectUtilities.equal(this.data, that.data);
    }
    
    
    public Object clone() throws CloneNotSupportedException {
        YIntervalSeriesCollection clone 
                = (YIntervalSeriesCollection) super.clone();
        clone.data = (List) ObjectUtilities.deepClone(this.data);
        return clone;
    }
    
}
