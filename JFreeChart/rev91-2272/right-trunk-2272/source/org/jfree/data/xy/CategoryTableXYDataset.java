

package org.jfree.data.xy;

import org.jfree.chart.event.DatasetChangeInfo;
import org.jfree.chart.util.PublicCloneable;
import org.jfree.data.DefaultKeyedValues2D;
import org.jfree.data.DomainInfo;
import org.jfree.data.Range;
import org.jfree.data.event.DatasetChangeEvent;
import org.jfree.data.general.DatasetUtilities;


public class CategoryTableXYDataset extends AbstractIntervalXYDataset
        implements TableXYDataset, IntervalXYDataset, DomainInfo,
                   PublicCloneable {

    
    private DefaultKeyedValues2D values;

    
    private IntervalXYDelegate intervalDelegate;

    
    public CategoryTableXYDataset() {
        this.values = new DefaultKeyedValues2D(true);
        this.intervalDelegate = new IntervalXYDelegate(this);
        addChangeListener(this.intervalDelegate);
    }

    
    public void add(double x, double y, String seriesName) {
        add(new Double(x), new Double(y), seriesName, true);
    }

    
    public void add(Number x, Number y, String seriesName, boolean notify) {
        this.values.addValue(y, (Comparable) x, seriesName);
        if (notify) {
            fireDatasetChanged(new DatasetChangeInfo());
            
        }
    }

    
    public void remove(double x, String seriesName) {
        remove(new Double(x), seriesName, true);
    }

    
    public void remove(Number x, String seriesName, boolean notify) {
        this.values.removeValue((Comparable) x, seriesName);
        if (notify) {
            fireDatasetChanged(new DatasetChangeInfo());
            
        }
    }


    
    public int getSeriesCount() {
        return this.values.getColumnCount();
    }

    
    public Comparable getSeriesKey(int series) {
        return this.values.getColumnKey(series);
    }

    
    public int getItemCount() {
        return this.values.getRowCount();
    }

    
    public int getItemCount(int series) {
        return getItemCount();  
                                
    }

    
    public Number getX(int series, int item) {
        return (Number) this.values.getRowKey(item);
    }

    
    public Number getStartX(int series, int item) {
        return this.intervalDelegate.getStartX(series, item);
    }

    
    public Number getEndX(int series, int item) {
        return this.intervalDelegate.getEndX(series, item);
    }

    
    public Number getY(int series, int item) {
        return this.values.getValue(item, series);
    }

    
    public Number getStartY(int series, int item) {
        return getY(series, item);
    }

    
    public Number getEndY(int series, int item) {
        return getY(series, item);
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

    
    public double getIntervalPositionFactor() {
        return this.intervalDelegate.getIntervalPositionFactor();
    }

    
    public void setIntervalPositionFactor(double d) {
        this.intervalDelegate.setIntervalPositionFactor(d);
        fireDatasetChanged(new DatasetChangeInfo());
        
    }

    
    public double getIntervalWidth() {
        return this.intervalDelegate.getIntervalWidth();
    }

    
    public void setIntervalWidth(double d) {
        this.intervalDelegate.setFixedIntervalWidth(d);
        fireDatasetChanged(new DatasetChangeInfo());
        
    }

    
    public boolean isAutoWidth() {
        return this.intervalDelegate.isAutoWidth();
    }

    
    public void setAutoWidth(boolean b) {
        this.intervalDelegate.setAutoWidth(b);
        fireDatasetChanged(new DatasetChangeInfo());
        
    }

    
    public boolean equals(Object obj) {
        if (!(obj instanceof CategoryTableXYDataset)) {
            return false;
        }
        CategoryTableXYDataset that = (CategoryTableXYDataset) obj;
        if (!this.intervalDelegate.equals(that.intervalDelegate)) {
            return false;
        }
        if (!this.values.equals(that.values)) {
            return false;
        }
        return true;
    }

    
    public Object clone() throws CloneNotSupportedException {
        CategoryTableXYDataset clone = (CategoryTableXYDataset) super.clone();
        clone.values = (DefaultKeyedValues2D) this.values.clone();
        clone.intervalDelegate = new IntervalXYDelegate(clone);
        
        clone.intervalDelegate.setFixedIntervalWidth(getIntervalWidth());
        clone.intervalDelegate.setAutoWidth(isAutoWidth());
        clone.intervalDelegate.setIntervalPositionFactor(
                getIntervalPositionFactor());
        return clone;
    }

}
