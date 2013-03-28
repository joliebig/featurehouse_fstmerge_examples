

package org.jfree.data.statistics;

import java.util.List;

import org.jfree.data.KeyedObjects2D;
import org.jfree.data.Range;
import org.jfree.data.RangeInfo;
import org.jfree.data.general.AbstractDataset;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;


public class DefaultBoxAndWhiskerCategoryDataset extends AbstractDataset
        implements BoxAndWhiskerCategoryDataset, RangeInfo, PublicCloneable {

    
    protected KeyedObjects2D data;

    
    private double minimumRangeValue;
    
    
    private int minimumRangeValueRow;
    
    
    private int minimumRangeValueColumn;

    
    private double maximumRangeValue;

    
    private int maximumRangeValueRow;
    
    
    private int maximumRangeValueColumn;
    
    
    public DefaultBoxAndWhiskerCategoryDataset() {
        this.data = new KeyedObjects2D();
        this.minimumRangeValue = Double.NaN;
        this.minimumRangeValueRow = -1;
        this.minimumRangeValueColumn = -1;
        this.maximumRangeValue = Double.NaN;
        this.maximumRangeValueRow = -1;
        this.maximumRangeValueColumn = -1;
    }

    
    public void add(List list, Comparable rowKey, Comparable columnKey) {
        BoxAndWhiskerItem item = BoxAndWhiskerCalculator
                .calculateBoxAndWhiskerStatistics(list);
        add(item, rowKey, columnKey);
    }
    
    
    public void add(BoxAndWhiskerItem item, Comparable rowKey, 
            Comparable columnKey) {

        this.data.addObject(item, rowKey, columnKey);
        
        
        int r = this.data.getRowIndex(rowKey);
        int c = this.data.getColumnIndex(columnKey);
        if ((this.maximumRangeValueRow == r && this.maximumRangeValueColumn 
                == c) || (this.minimumRangeValueRow == r 
                && this.minimumRangeValueColumn == c))  {
            updateBounds();
        }
        else {
        
            double minval = Double.NaN;
            if (item.getMinOutlier() != null) {
                minval = item.getMinOutlier().doubleValue();
            }
            double maxval = Double.NaN;
            if (item.getMaxOutlier() != null) {
                maxval = item.getMaxOutlier().doubleValue();
            }
        
            if (Double.isNaN(this.maximumRangeValue)) {
                this.maximumRangeValue = maxval;
                this.maximumRangeValueRow = r;
                this.maximumRangeValueColumn = c;
            }
            else if (maxval > this.maximumRangeValue) {
                this.maximumRangeValue = maxval;
                this.maximumRangeValueRow = r;
                this.maximumRangeValueColumn = c;
            }
        
            if (Double.isNaN(this.minimumRangeValue)) {
                this.minimumRangeValue = minval;
                this.minimumRangeValueRow = r;
                this.minimumRangeValueColumn = c;
            }
            else if (minval < this.minimumRangeValue) {
                this.minimumRangeValue = minval;
                this.minimumRangeValueRow = r;
                this.minimumRangeValueColumn = c;
            }
        }
        
        fireDatasetChanged();

    }
    
    
    public void remove(Comparable rowKey, Comparable columnKey) {
        
        int r = getRowIndex(rowKey);
        int c = getColumnIndex(columnKey);
        this.data.removeObject(rowKey, columnKey);
        
        
        
        if ((this.maximumRangeValueRow == r && this.maximumRangeValueColumn 
                == c) || (this.minimumRangeValueRow == r 
                && this.minimumRangeValueColumn == c))  {
            updateBounds();
        }
        
        fireDatasetChanged();
    }

    
    public void removeRow(int rowIndex) {
        this.data.removeRow(rowIndex);
        updateBounds();
        fireDatasetChanged();
    }

    
    public void removeRow(Comparable rowKey) {
        this.data.removeRow(rowKey);
        updateBounds();
        fireDatasetChanged();
    }

    
    public void removeColumn(int columnIndex) {
        this.data.removeColumn(columnIndex);
        updateBounds();
        fireDatasetChanged();
    }

    
    public void removeColumn(Comparable columnKey) {
        this.data.removeColumn(columnKey);
        updateBounds();
        fireDatasetChanged();
    }

    
    public void clear() {
        this.data.clear();
        updateBounds();
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
        return this.minimumRangeValue;
    }

    
    public double getRangeUpperBound(boolean includeInterval) {
        return this.maximumRangeValue;
    }

    
    public Range getRangeBounds(boolean includeInterval) {
        return new Range(this.minimumRangeValue, this.maximumRangeValue);
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
    
    
    private void updateBounds() {
        this.minimumRangeValue = Double.NaN;
        this.minimumRangeValueRow = -1;
        this.minimumRangeValueColumn = -1;
        this.maximumRangeValue = Double.NaN;
        this.maximumRangeValueRow = -1;
        this.maximumRangeValueColumn = -1;
        int rowCount = getRowCount();
        int columnCount = getColumnCount();
        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                BoxAndWhiskerItem item = getItem(r, c);
                if (item != null) {
                    Number min = item.getMinOutlier();
                    if (min != null) {
                        double minv = min.doubleValue();
                        if (!Double.isNaN(minv)) {
                            if (minv < this.minimumRangeValue || Double.isNaN(
                                    this.minimumRangeValue)) {
                                this.minimumRangeValue = minv;
                                this.minimumRangeValueRow = r;
                                this.minimumRangeValueColumn = c;
                            }
                        }
                    }
                    Number max = item.getMaxOutlier();
                    if (max != null) {
                        double maxv = max.doubleValue();
                        if (!Double.isNaN(maxv)) {
                            if (maxv > this.maximumRangeValue || Double.isNaN(
                                    this.maximumRangeValue)) {
                                this.maximumRangeValue = maxv;
                                this.maximumRangeValueRow = r;
                                this.maximumRangeValueColumn = c;
                            }
                        }
                    }
                }
            }
        }
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
    
    
    public Object clone() throws CloneNotSupportedException {
        DefaultBoxAndWhiskerCategoryDataset clone 
                = (DefaultBoxAndWhiskerCategoryDataset) super.clone();
        clone.data = (KeyedObjects2D) this.data.clone();
        return clone;
    }

}
