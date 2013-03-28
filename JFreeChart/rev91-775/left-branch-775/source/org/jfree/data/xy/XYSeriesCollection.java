

package org.jfree.data.xy;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jfree.data.DomainInfo;
import org.jfree.data.Range;
import org.jfree.data.UnknownKeyException;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.util.ObjectUtilities;


public class XYSeriesCollection extends AbstractIntervalXYDataset
                                implements IntervalXYDataset, DomainInfo, 
                                           Serializable {

    
    private static final long serialVersionUID = -7590013825931496766L;
    
    
    private List data;
    
    
    private IntervalXYDelegate intervalDelegate;
    
    
    public XYSeriesCollection() {
        this(null);
    }

    
    public XYSeriesCollection(XYSeries series) {
        this.data = new java.util.ArrayList();
        this.intervalDelegate = new IntervalXYDelegate(this, false);
        addChangeListener(this.intervalDelegate);
        if (series != null) {
            this.data.add(series);
            series.addChangeListener(this);
        }
    }
    
    
    public void addSeries(XYSeries series) {

        if (series == null) {
            throw new IllegalArgumentException("Null 'series' argument.");
        }
        this.data.add(series);
        series.addChangeListener(this);
        fireDatasetChanged();

    }

    
    public void removeSeries(int series) {

        if ((series < 0) || (series >= getSeriesCount())) {
            throw new IllegalArgumentException("Series index out of bounds.");
        }

        
        XYSeries ts = (XYSeries) this.data.get(series);
        ts.removeChangeListener(this);
        this.data.remove(series);
        fireDatasetChanged();

    }

    
    public void removeSeries(XYSeries series) {

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
          XYSeries series = (XYSeries) this.data.get(i);
          series.removeChangeListener(this);
        }

        
        this.data.clear();
        fireDatasetChanged();
    }

    
    public int getSeriesCount() {
        return this.data.size();
    }

    
    public List getSeries() {
        return Collections.unmodifiableList(this.data);
    }
    
    
    public int indexOf(XYSeries series) {
        if (series == null) {
            throw new IllegalArgumentException("Null 'series' argument.");
        }
        return this.data.indexOf(series);
    }

    
    public XYSeries getSeries(int series) {
        if ((series < 0) || (series >= getSeriesCount())) {
            throw new IllegalArgumentException("Series index out of bounds");
        }
        return (XYSeries) this.data.get(series);
    }
    
    
    public XYSeries getSeries(Comparable key) {
        if (key == null) {
            throw new IllegalArgumentException("Null 'key' argument.");
        }
        Iterator iterator = this.data.iterator();
        while (iterator.hasNext()) {
            XYSeries series = (XYSeries) iterator.next();
            if (key.equals(series.getKey())) {
                return series;
            }
        }
        throw new UnknownKeyException("Key not found: " + key);
    }

    
    public Comparable getSeriesKey(int series) {
        
        return getSeries(series).getKey();
    }

    
    public int getItemCount(int series) {
        
        return getSeries(series).getItemCount();
    }

    
    public Number getX(int series, int item) {
        XYSeries ts = (XYSeries) this.data.get(series);
        XYDataItem xyItem = ts.getDataItem(item);
        return xyItem.getX();
    }

    
    public Number getStartX(int series, int item) {
        return this.intervalDelegate.getStartX(series, item);
    }

    
    public Number getEndX(int series, int item) {
        return this.intervalDelegate.getEndX(series, item);
    }

    
    public Number getY(int series, int index) {

        XYSeries ts = (XYSeries) this.data.get(series);
        XYDataItem xyItem = ts.getDataItem(index);
        return xyItem.getY();

    }

    
    public Number getStartY(int series, int item) {
        return getY(series, item);
    }

    
    public Number getEndY(int series, int item) {
        return getY(series, item);
    }

    
    public boolean equals(Object obj) {
        

        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYSeriesCollection)) {
            return false;
        }
        XYSeriesCollection that = (XYSeriesCollection) obj;
        return ObjectUtilities.equal(this.data, that.data);
    }
    
    
    public Object clone() throws CloneNotSupportedException {
        XYSeriesCollection clone = (XYSeriesCollection) super.clone();
        clone.data = (List) ObjectUtilities.deepClone(this.data);
        clone.intervalDelegate 
                = (IntervalXYDelegate) this.intervalDelegate.clone();
        return clone;
    }

    
    public int hashCode() {
        
        return (this.data != null ? this.data.hashCode() : 0);
    }
       
    
    public double getDomainLowerBound(boolean includeInterval) {
        return this.intervalDelegate.getDomainLowerBound(includeInterval);
    }

    
    public double getDomainUpperBound(boolean includeInterval) {
        return this.intervalDelegate.getDomainUpperBound(includeInterval);
    }

    
    public Range getDomainBounds(boolean includeInterval) {
        if (includeInterval) {
            return this.intervalDelegate.getDomainBounds(includeInterval);
        }
        else {
            return DatasetUtilities.iterateDomainBounds(this, includeInterval);
        }
            
    }
    
    
    public double getIntervalWidth() {
        return this.intervalDelegate.getIntervalWidth();
    }
    
    
    public void setIntervalWidth(double width) {
        if (width < 0.0) {
            throw new IllegalArgumentException("Negative 'width' argument.");
        }
        this.intervalDelegate.setFixedIntervalWidth(width);
        fireDatasetChanged();
    }

    
    public double getIntervalPositionFactor() {
        return this.intervalDelegate.getIntervalPositionFactor();
    }
    
    
    public void setIntervalPositionFactor(double factor) {
        this.intervalDelegate.setIntervalPositionFactor(factor);
        fireDatasetChanged();
    }
    
    
    public boolean isAutoWidth() {
        return this.intervalDelegate.isAutoWidth();
    }

    
    public void setAutoWidth(boolean b) {
        this.intervalDelegate.setAutoWidth(b);
        fireDatasetChanged();
    }
    
}
