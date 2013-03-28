

package org.jfree.data.statistics;

import java.util.List;

import org.jfree.data.KeyedObjects2D;
import org.jfree.data.Range;
import org.jfree.data.RangeInfo;
import org.jfree.data.general.AbstractDataset;
import org.jfree.util.ObjectUtilities;


public class DefaultBoxAndWhiskerCategoryDataset extends AbstractDataset
        implements BoxAndWhiskerCategoryDataset, RangeInfo {

    
    protected KeyedObjects2D data;

    
    private Number minimumRangeValue;

    
    private Number maximumRangeValue;

    
    private Range rangeBounds;

    
    public DefaultBoxAndWhiskerCategoryDataset() {
        this.data = new KeyedObjects2D();
        this.minimumRangeValue = null;
        this.maximumRangeValue = null;
        this.rangeBounds = new Range(0.0, 0.0);
    }

    
    public void add(List list, Comparable rowKey, Comparable columnKey) {
        BoxAndWhiskerItem item 
            = BoxAndWhiskerCalculator.calculateBoxAndWhiskerStatistics(list);
        add(item, rowKey, columnKey);
    }
    
    
    public void add(BoxAndWhiskerItem item, 
                    Comparable rowKey, 
                    Comparable columnKey) {

        this.data.addObject(item, rowKey, columnKey);
        double minval = Double.NaN;
        if (item.getMinOutlier() != null) {
            minval = item.getMinOutlier().doubleValue();
        }
        double maxval = Double.NaN;
        if (item.getMaxOutlier() != null) {
            maxval = item.getMaxOutlier().doubleValue();
        }
        
        if (this.maximumRangeValue == null) {
            this.maximumRangeValue = new Double(maxval);
        }
        else if (maxval > this.maximumRangeValue.doubleValue()) {
            this.maximumRangeValue = new Double(maxval);
        }
        
        if (this.minimumRangeValue == null) {
            this.minimumRangeValue = new Double(minval);
        }
        else if (minval < this.minimumRangeValue.doubleValue()) {
            this.minimumRangeValue = new Double(minval);
        }
        
        this.rangeBounds = new Range(this.minimumRangeValue.doubleValue(),
              this.maximumRangeValue.doubleValue());

        fireDatasetChanged();

    }

    
    public BoxAndWhiskerItem getItem(int row, int column) {
        return (BoxAndWhiskerItem) this.data.getObject(row, column);  
    }

    
    public Number getValue(int row, int column) {
        return getMedianValue(row, column);
    }

    
    public Number getValue(Comparable rowKey, Comparable columnKey) {
        return getMedianValue(rowKey, columnKey);
    }

    
    public Number getMeanValue(int row, int column) {

        Number result = null;
        BoxAndWhiskerItem item = (BoxAndWhiskerItem) this.data.getObject(row, 
                column);
        if (item != null) {
            result = item.getMean();
        }
        return result;

    }

    
    public Number getMeanValue(Comparable rowKey, Comparable columnKey) {
        Number result = null;
        BoxAndWhiskerItem item = (BoxAndWhiskerItem) this.data.getObject(
                rowKey, columnKey);
        if (item != null) {
            result = item.getMean();
        }
        return result;
    }

    
    public Number getMedianValue(int row, int column) {
        Number result = null;
        BoxAndWhiskerItem item = (BoxAndWhiskerItem) this.data.getObject(row, 
                column);
        if (item != null) {
            result = item.getMedian();
        }
        return result;
    }

    
    public Number getMedianValue(Comparable rowKey, Comparable columnKey) {
        Number result = null;
        BoxAndWhiskerItem item = (BoxAndWhiskerItem) this.data.getObject(
                rowKey, columnKey);
        if (item != null) {
            result = item.getMedian();
        }
        return result;
    }

    
    public Number getQ1Value(int row, int column) {
        Number result = null;
        BoxAndWhiskerItem item = (BoxAndWhiskerItem) this.data.getObject(
                row, column);
        if (item != null) {
            result = item.getQ1();
        }
        return result;
    }

    
    public Number getQ1Value(Comparable rowKey, Comparable columnKey) {
        Number result = null;
        BoxAndWhiskerItem item = (BoxAndWhiskerItem) this.data.getObject(
                rowKey, columnKey);
        if (item != null) {
            result = item.getQ1();
        }
        return result;
    }

    
    public Number getQ3Value(int row, int column) {
        Number result = null;
        BoxAndWhiskerItem item = (BoxAndWhiskerItem) this.data.getObject(
                row, column);
        if (item != null) {
            result = item.getQ3();
        }
        return result;
    }

    
    public Number getQ3Value(Comparable rowKey, Comparable columnKey) {
        Number result = null;
        BoxAndWhiskerItem item = (BoxAndWhiskerItem) this.data.getObject(
                rowKey, columnKey);
        if (item != null) {
            result = item.getQ3();
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

    
    public double getRangeLowerBound(boolean includeInterval) {
        double result = Double.NaN;
        if (this.minimumRangeValue != null) {
            result = this.minimumRangeValue.doubleValue();
        }
        return result;
    }

    
    public double getRangeUpperBound(boolean includeInterval) {
        double result = Double.NaN;
        if (this.maximumRangeValue != null) {
            result = this.maximumRangeValue.doubleValue();
        }
        return result;
    }

    
    public Range getRangeBounds(boolean includeInterval) {
        return this.rangeBounds;
    }
    
    
    public Number getMinRegularValue(int row, int column) {
        Number result = null;
        BoxAndWhiskerItem item = (BoxAndWhiskerItem) this.data.getObject(
                row, column);
        if (item != null) {
            result = item.getMinRegularValue();
        }
        return result;
    }

    
    public Number getMinRegularValue(Comparable rowKey, Comparable columnKey) {
        Number result = null;
        BoxAndWhiskerItem item = (BoxAndWhiskerItem) this.data.getObject(
                rowKey, columnKey);
        if (item != null) {
            result = item.getMinRegularValue();
        }
        return result;
    }

    
    public Number getMaxRegularValue(int row, int column) {
        Number result = null;
        BoxAndWhiskerItem item = (BoxAndWhiskerItem) this.data.getObject(
                row, column);
        if (item != null) {
            result = item.getMaxRegularValue();
        }
        return result;
    }

    
    public Number getMaxRegularValue(Comparable rowKey, Comparable columnKey) {
        Number result = null;
        BoxAndWhiskerItem item = (BoxAndWhiskerItem) this.data.getObject(
                rowKey, columnKey);
        if (item != null) {
            result = item.getMaxRegularValue();
        }
        return result;
    }

    
    public Number getMinOutlier(int row, int column) {
        Number result = null;
        BoxAndWhiskerItem item = (BoxAndWhiskerItem) this.data.getObject(
                row, column);
        if (item != null) {
            result = item.getMinOutlier();
        }
        return result;
    }

    
    public Number getMinOutlier(Comparable rowKey, Comparable columnKey) {
        Number result = null;
        BoxAndWhiskerItem item = (BoxAndWhiskerItem) this.data.getObject(
                rowKey, columnKey);
        if (item != null) {
            result = item.getMinOutlier();
        }
        return result;
    }

    
    public Number getMaxOutlier(int row, int column) {
        Number result = null;
        BoxAndWhiskerItem item = (BoxAndWhiskerItem) this.data.getObject(
                row, column);
        if (item != null) {
            result = item.getMaxOutlier();
        }
        return result;
    }

    
    public Number getMaxOutlier(Comparable rowKey, Comparable columnKey) {
        Number result = null;
        BoxAndWhiskerItem item = (BoxAndWhiskerItem) this.data.getObject(
                rowKey, columnKey);
        if (item != null) {
            result = item.getMaxOutlier();
        }
        return result;
    }

    
    public List getOutliers(int row, int column) {
        List result = null;
        BoxAndWhiskerItem item = (BoxAndWhiskerItem) this.data.getObject(
                row, column);
        if (item != null) {
            result = item.getOutliers();
        }
        return result;
    }

    
    public List getOutliers(Comparable rowKey, Comparable columnKey) {
        List result = null;
        BoxAndWhiskerItem item = (BoxAndWhiskerItem) this.data.getObject(
                rowKey, columnKey);
        if (item != null) {
            result = item.getOutliers();
        }
        return result;
    }
    
    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;   
        }
        if (obj instanceof DefaultBoxAndWhiskerCategoryDataset) {
            DefaultBoxAndWhiskerCategoryDataset dataset 
                    = (DefaultBoxAndWhiskerCategoryDataset) obj;
            return ObjectUtilities.equal(this.data, dataset.data);
        }
        return false;
    }

}
