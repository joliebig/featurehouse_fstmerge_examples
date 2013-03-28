

package org.jfree.data.gantt;

import java.util.Collections;
import java.util.List;

import org.jfree.chart.event.DatasetChangeInfo;
import org.jfree.chart.util.PublicCloneable;
import org.jfree.data.UnknownKeyException;
import org.jfree.data.general.AbstractDataset;
import org.jfree.data.event.DatasetChangeEvent;


public class SlidingGanttCategoryDataset extends AbstractDataset
        implements GanttCategoryDataset {

    
    private GanttCategoryDataset underlying;

    
    private int firstCategoryIndex;

    
    private int maximumCategoryCount;

    
    public SlidingGanttCategoryDataset(GanttCategoryDataset underlying,
            int firstColumn, int maxColumns) {
        this.underlying = underlying;
        this.firstCategoryIndex = firstColumn;
        this.maximumCategoryCount = maxColumns;
    }

    
    public GanttCategoryDataset getUnderlyingDataset() {
        return this.underlying;
    }

    
    public int getFirstCategoryIndex() {
        return this.firstCategoryIndex;
    }

    
    public void setFirstCategoryIndex(int first) {
        if (first < 0 || first >= this.underlying.getColumnCount()) {
            throw new IllegalArgumentException("Invalid index.");
        }
        this.firstCategoryIndex = first;
        fireDatasetChanged(new DatasetChangeInfo());
        
    }

    
    public int getMaximumCategoryCount() {
        return this.maximumCategoryCount;
    }

    
    public void setMaximumCategoryCount(int max) {
        if (max < 0) {
            throw new IllegalArgumentException("Requires 'max' >= 0.");
        }
        this.maximumCategoryCount = max;
        fireDatasetChanged(new DatasetChangeInfo());
        
    }

    
    private int lastCategoryIndex() {
        if (this.maximumCategoryCount == 0) {
            return -1;
        }
        return Math.min(this.firstCategoryIndex + this.maximumCategoryCount,
                this.underlying.getColumnCount()) - 1;
    }

    
    public int getColumnIndex(Comparable key) {
        int index = this.underlying.getColumnIndex(key);
        if (index >= this.firstCategoryIndex && index <= lastCategoryIndex()) {
            return index - this.firstCategoryIndex;
        }
        return -1;  
    }

    
    public Comparable getColumnKey(int column) {
        return this.underlying.getColumnKey(column + this.firstCategoryIndex);
    }

    
    public List getColumnKeys() {
        List result = new java.util.ArrayList();
        int last = lastCategoryIndex();
        for (int i = this.firstCategoryIndex; i < last; i++) {
            result.add(this.underlying.getColumnKey(i));
        }
        return Collections.unmodifiableList(result);
    }

    
    public int getRowIndex(Comparable key) {
        return this.underlying.getRowIndex(key);
    }

    
    public Comparable getRowKey(int row) {
        return this.underlying.getRowKey(row);
    }

    
    public List getRowKeys() {
        return this.underlying.getRowKeys();
    }

    
    public Number getValue(Comparable rowKey, Comparable columnKey) {
        int r = getRowIndex(rowKey);
        int c = getColumnIndex(columnKey);
        if (c != -1) {
            return this.underlying.getValue(r, c + this.firstCategoryIndex);
        }
        else {
            throw new UnknownKeyException("Unknown columnKey: " + columnKey);
        }
    }

    
    public int getColumnCount() {
        int last = lastCategoryIndex();
        if (last == -1) {
            return 0;
        }
        else {
            return Math.max(last - this.firstCategoryIndex + 1, 0);
        }
    }

    
    public int getRowCount() {
        return this.underlying.getRowCount();
    }

    
    public Number getValue(int row, int column) {
        return this.underlying.getValue(row, column + this.firstCategoryIndex);
    }

    
    public Number getPercentComplete(Comparable rowKey, Comparable columnKey) {
        int r = getRowIndex(rowKey);
        int c = getColumnIndex(columnKey);
        if (c != -1) {
            return this.underlying.getPercentComplete(r,
                    c + this.firstCategoryIndex);
        }
        else {
            throw new UnknownKeyException("Unknown columnKey: " + columnKey);
        }
    }

    
    public Number getPercentComplete(Comparable rowKey, Comparable columnKey,
            int subinterval) {
        int r = getRowIndex(rowKey);
        int c = getColumnIndex(columnKey);
        if (c != -1) {
            return this.underlying.getPercentComplete(r,
                    c + this.firstCategoryIndex, subinterval);
        }
        else {
            throw new UnknownKeyException("Unknown columnKey: " + columnKey);
        }
    }

    
    public Number getEndValue(Comparable rowKey, Comparable columnKey,
            int subinterval) {
        int r = getRowIndex(rowKey);
        int c = getColumnIndex(columnKey);
        if (c != -1) {
            return this.underlying.getEndValue(r,
                    c + this.firstCategoryIndex, subinterval);
        }
        else {
            throw new UnknownKeyException("Unknown columnKey: " + columnKey);
        }
    }

    
    public Number getEndValue(int row, int column, int subinterval) {
        return this.underlying.getEndValue(row,
                column + this.firstCategoryIndex, subinterval);
    }

    
    public Number getPercentComplete(int series, int category) {
        return this.underlying.getPercentComplete(series,
                category + this.firstCategoryIndex);
    }

    
    public Number getPercentComplete(int row, int column, int subinterval) {
        return this.underlying.getPercentComplete(row,
                column + this.firstCategoryIndex, subinterval);
    }

    
    public Number getStartValue(Comparable rowKey, Comparable columnKey,
            int subinterval) {
        int r = getRowIndex(rowKey);
        int c = getColumnIndex(columnKey);
        if (c != -1) {
            return this.underlying.getStartValue(r,
                    c + this.firstCategoryIndex, subinterval);
        }
        else {
            throw new UnknownKeyException("Unknown columnKey: " + columnKey);
        }
    }

    
    public Number getStartValue(int row, int column, int subinterval) {
        return this.underlying.getStartValue(row,
                column + this.firstCategoryIndex, subinterval);
    }

    
    public int getSubIntervalCount(Comparable rowKey, Comparable columnKey) {
        int r = getRowIndex(rowKey);
        int c = getColumnIndex(columnKey);
        if (c != -1) {
            return this.underlying.getSubIntervalCount(r,
                    c + this.firstCategoryIndex);
        }
        else {
            throw new UnknownKeyException("Unknown columnKey: " + columnKey);
        }
    }

    
    public int getSubIntervalCount(int row, int column) {
        return this.underlying.getSubIntervalCount(row,
                column + this.firstCategoryIndex);
    }

    
    public Number getStartValue(Comparable rowKey, Comparable columnKey) {
        int r = getRowIndex(rowKey);
        int c = getColumnIndex(columnKey);
        if (c != -1) {
            return this.underlying.getStartValue(r, c + this.firstCategoryIndex);
        }
        else {
            throw new UnknownKeyException("Unknown columnKey: " + columnKey);
        }
    }

    
    public Number getStartValue(int row, int column) {
        return this.underlying.getStartValue(row,
                column + this.firstCategoryIndex);
    }

    
    public Number getEndValue(Comparable rowKey, Comparable columnKey) {
        int r = getRowIndex(rowKey);
        int c = getColumnIndex(columnKey);
        if (c != -1) {
            return this.underlying.getEndValue(r, c + this.firstCategoryIndex);
        }
        else {
            throw new UnknownKeyException("Unknown columnKey: " + columnKey);
        }
    }

    
    public Number getEndValue(int series, int category) {
        return this.underlying.getEndValue(series,
                category + this.firstCategoryIndex);
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof SlidingGanttCategoryDataset)) {
            return false;
        }
        SlidingGanttCategoryDataset that = (SlidingGanttCategoryDataset) obj;
        if (this.firstCategoryIndex != that.firstCategoryIndex) {
            return false;
        }
        if (this.maximumCategoryCount != that.maximumCategoryCount) {
            return false;
        }
        if (!this.underlying.equals(that.underlying)) {
            return false;
        }
        return true;
    }

    
    public Object clone() throws CloneNotSupportedException {
        SlidingGanttCategoryDataset clone
                = (SlidingGanttCategoryDataset) super.clone();
        if (this.underlying instanceof PublicCloneable) {
            PublicCloneable pc = (PublicCloneable) this.underlying;
            clone.underlying = (GanttCategoryDataset) pc.clone();
        }
        return clone;
    }

}
