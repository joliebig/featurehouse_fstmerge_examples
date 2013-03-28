

package org.jfree.data.statistics;

import java.util.List;

import org.jfree.data.KeyedObjects2D;
import org.jfree.data.Range;
import org.jfree.data.RangeInfo;
import org.jfree.data.general.AbstractDataset;


public class DefaultStatisticalCategoryDataset extends AbstractDataset
    implements StatisticalCategoryDataset, RangeInfo {

    
    private KeyedObjects2D data;

    
    private double minimumRangeValue;

    
    private double minimumRangeValueIncStdDev;
    
    
    private double maximumRangeValue;

    
    private double maximumRangeValueIncStdDev;

    
    public DefaultStatisticalCategoryDataset() {
        this.data = new KeyedObjects2D();
        this.minimumRangeValue = Double.NaN;
        this.maximumRangeValue = Double.NaN;
        this.minimumRangeValueIncStdDev = Double.NaN;
        this.maximumRangeValueIncStdDev = Double.NaN;
    }

    
    public Number getMeanValue(int row, int column) {
        Number result = null;
        MeanAndStandardDeviation masd 
            = (MeanAndStandardDeviation) this.data.getObject(row, column);
        if (masd != null) {
            result = masd.getMean();
        }
        return result;
    }

    
    public Number getValue(int row, int column) {
        return getMeanValue(row, column);
    }

    
    public Number getValue(Comparable rowKey, Comparable columnKey) {
        return getMeanValue(rowKey, columnKey);
    }

    
    public Number getMeanValue(Comparable rowKey, Comparable columnKey) {
        Number result = null;
        MeanAndStandardDeviation masd
            = (MeanAndStandardDeviation) this.data.getObject(rowKey, columnKey);
        if (masd != null) {
            result = masd.getMean();
        }
        return result;
    }

    
    public Number getStdDevValue(int row, int column) {
        Number result = null;
        MeanAndStandardDeviation masd 
            = (MeanAndStandardDeviation) this.data.getObject(row, column);
        if (masd != null) {
            result = masd.getStandardDeviation();
        }
        return result;
    }

    
    public Number getStdDevValue(Comparable rowKey, Comparable columnKey) {
        Number result = null;
        MeanAndStandardDeviation masd
            = (MeanAndStandardDeviation) this.data.getObject(rowKey, columnKey);
        if (masd != null) {
            result = masd.getStandardDeviation();
        }
        return result;
    }

    
    public int getColumnIndex(Comparable key) {
        return this.data.getColumnIndex(key);
    }

    
    public Comparable getColumnKey(int column) {
        return this.data.getColumnKey(column);
    }

    
    public List getColumnKeys() {
        return this.data.getColumnKeys();
    }

    
    public int getRowIndex(Comparable key) {
        return this.data.getRowIndex(key);
    }

    
    public Comparable getRowKey(int row) {
        return this.data.getRowKey(row);
    }

    
    public List getRowKeys() {
        return this.data.getRowKeys();
    }

    
    public int getRowCount() {
        return this.data.getRowCount();
    }

    
    public int getColumnCount() {
        return this.data.getColumnCount();
    }

    
    public void add(double mean, double standardDeviation,
                    Comparable rowKey, Comparable columnKey) {
        add(new Double(mean), new Double(standardDeviation), rowKey, columnKey);
    }

    
    public void add(Number mean, Number standardDeviation,
                    Comparable rowKey, Comparable columnKey) {
        MeanAndStandardDeviation item = new MeanAndStandardDeviation(
                mean, standardDeviation);
        this.data.addObject(item, rowKey, columnKey);
        double m = 0.0;
        double sd = 0.0;
        if (mean != null) {
            m = mean.doubleValue();
        }
        if (standardDeviation != null) {
            sd = standardDeviation.doubleValue();   
        }
        
        if (!Double.isNaN(m)) {
            if (Double.isNaN(this.maximumRangeValue) 
                    || m > this.maximumRangeValue) {
                this.maximumRangeValue = m;
            }
        }
        
        if (!Double.isNaN(m + sd)) {
            if (Double.isNaN(this.maximumRangeValueIncStdDev) 
                    || (m + sd) > this.maximumRangeValueIncStdDev) {
                this.maximumRangeValueIncStdDev = m + sd;
            }
        }

        if (!Double.isNaN(m)) {
            if (Double.isNaN(this.minimumRangeValue) 
                    || m < this.minimumRangeValue) {
                this.minimumRangeValue = m;
            }
        }

        if (!Double.isNaN(m - sd)) {
            if (Double.isNaN(this.minimumRangeValueIncStdDev) 
                    || (m - sd) < this.minimumRangeValueIncStdDev) {
                this.minimumRangeValueIncStdDev = m - sd;
            }
        }

        fireDatasetChanged();
    }

    
    public double getRangeLowerBound(boolean includeInterval) {
        return this.minimumRangeValue;        
    }

    
    public double getRangeUpperBound(boolean includeInterval) {
        return this.maximumRangeValue;        
    }

    
    public Range getRangeBounds(boolean includeInterval) {
        Range result = null;
        if (includeInterval) {
            if (!Double.isNaN(this.minimumRangeValueIncStdDev) 
                    && !Double.isNaN(this.maximumRangeValueIncStdDev))
            result = new Range(this.minimumRangeValueIncStdDev, 
                    this.maximumRangeValueIncStdDev);
        }
        else {
            if (!Double.isNaN(this.minimumRangeValue) 
                    && !Double.isNaN(this.maximumRangeValue))
            result = new Range(this.minimumRangeValue, this.maximumRangeValue);            
        }
        return result;
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;   
        }
        if (!(obj instanceof DefaultStatisticalCategoryDataset)) {
            return false;   
        }
        DefaultStatisticalCategoryDataset that 
            = (DefaultStatisticalCategoryDataset) obj;
        if (!this.data.equals(that.data)) {
            return false;   
        }
        return true;
    }
}
