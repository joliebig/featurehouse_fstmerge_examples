

package org.jfree.data.xy;

import java.io.Serializable;
import java.util.List;

import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.util.ObjectUtilities;


public class XIntervalSeriesCollection extends AbstractIntervalXYDataset
                                implements IntervalXYDataset, Serializable {

    
    private List data;
    
    
    public XIntervalSeriesCollection() {
        this.data = new java.util.ArrayList();
    }

    
    public void addSeries(XIntervalSeries series) {
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

    
    public XIntervalSeries getSeries(int series) {
        if ((series < 0) || (series >= getSeriesCount())) {
            throw new IllegalArgumentException("Series index out of bounds");
        }
        return (XIntervalSeries) this.data.get(series);
    }

    
    public Comparable getSeriesKey(int series) {
        
        return getSeries(series).getKey();
    }

    
    public int getItemCount(int series) {
        
        return getSeries(series).getItemCount();
    }

    
    public Number getX(int series, int item) {
        XIntervalSeries s = (XIntervalSeries) this.data.get(series);
        XIntervalDataItem di = (XIntervalDataItem) s.getDataItem(item);
        return di.getX();
    }

    
    public Number getY(int series, int item) {
        XIntervalSeries s = (XIntervalSeries) this.data.get(series);
        XIntervalDataItem di = (XIntervalDataItem) s.getDataItem(item);
        return new Double(di.getYValue());
    }

    
    public Number getStartX(int series, int item) {
        XIntervalSeries s = (XIntervalSeries) this.data.get(series);
        XIntervalDataItem di = (XIntervalDataItem) s.getDataItem(item);
        return new Double(di.getXLowValue());
    }

    
    public Number getEndX(int series, int item) {
        XIntervalSeries s = (XIntervalSeries) this.data.get(series);
        XIntervalDataItem di = (XIntervalDataItem) s.getDataItem(item);
        return new Double(di.getXHighValue());
    }

    
    public Number getStartY(int series, int item) {
        return getY(series, item);
    }

    
    public Number getEndY(int series, int item) {
        return getY(series, item);
    }
    
    
    public void removeSeries(int series) {
        if ((series < 0) || (series >= getSeriesCount())) {
            throw new IllegalArgumentException("Series index out of bounds.");
        }
        XIntervalSeries ts = (XIntervalSeries) this.data.get(series);
        ts.removeChangeListener(this);
        this.data.remove(series);
        fireDatasetChanged();
    }

    
    public void removeSeries(XIntervalSeries series) {
        if (series == null) {
            throw new IllegalArgumentException("Null 'series' argument.");
        }
        if (this.data.contains(series)) {
            series.removeChangeListener(this);
            this.data.remove(series);
            fireDatasetChanged();
        }
    }
    
    
    public void removeAllSeries() {
        
        
        for (int i = 0; i < this.data.size(); i++) {
          XIntervalSeries series = (XIntervalSeries) this.data.get(i);
          series.removeChangeListener(this);
        }
        this.data.clear();
        fireDatasetChanged();
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XIntervalSeriesCollection)) {
            return false;
        }
        XIntervalSeriesCollection that = (XIntervalSeriesCollection) obj;
        return ObjectUtilities.equal(this.data, that.data);
    }
    
    
    public Object clone() throws CloneNotSupportedException {
        XIntervalSeriesCollection clone 
                = (XIntervalSeriesCollection) super.clone();
        clone.data = (List) ObjectUtilities.deepClone(this.data);
        return clone;
    }
  
}
