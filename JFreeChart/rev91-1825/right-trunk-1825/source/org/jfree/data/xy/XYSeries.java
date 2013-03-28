

package org.jfree.data.xy;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.jfree.chart.util.ObjectUtilities;
import org.jfree.data.general.Series;
import org.jfree.data.general.SeriesChangeEvent;
import org.jfree.data.general.SeriesException;


public class XYSeries extends Series implements Cloneable, Serializable {

    
    static final long serialVersionUID = -5908509288197150436L;

    
    
    

    
    protected List data;

    
    private int maximumItemCount = Integer.MAX_VALUE;

    
    private boolean autoSort;

    
    private boolean allowDuplicateXValues;

    
    public XYSeries(Comparable key) {
        this(key, true, true);
    }

    
    public XYSeries(Comparable key, boolean autoSort) {
        this(key, autoSort, true);
    }

    
    public XYSeries(Comparable key,
                    boolean autoSort,
                    boolean allowDuplicateXValues) {
        super(key);
        this.data = new java.util.ArrayList();
        this.autoSort = autoSort;
        this.allowDuplicateXValues = allowDuplicateXValues;
    }

    
    public boolean getAutoSort() {
        return this.autoSort;
    }

    
    public boolean getAllowDuplicateXValues() {
        return this.allowDuplicateXValues;
    }

    
    public int getItemCount() {
        return this.data.size();
    }

    
    public List getItems() {
        return Collections.unmodifiableList(this.data);
    }

    
    public int getMaximumItemCount() {
        return this.maximumItemCount;
    }

    
    public void setMaximumItemCount(int maximum) {
        this.maximumItemCount = maximum;
        boolean dataRemoved = false;
        while (this.data.size() > maximum) {
            this.data.remove(0);
            dataRemoved = true;
        }
        if (dataRemoved) {
            fireSeriesChanged();
        }
    }

    
    public void add(XYDataItem item) {
        
        add(item, true);
    }

    
    public void add(double x, double y) {
        add(new Double(x), new Double(y), true);
    }

    
    public void add(double x, double y, boolean notify) {
        add(new Double(x), new Double(y), notify);
    }

    
    public void add(double x, Number y) {
        add(new Double(x), y);
    }

    
    public void add(double x, Number y, boolean notify) {
        add(new Double(x), y, notify);
    }

    
    public void add(Number x, Number y) {
        
        add(x, y, true);
    }

    
    public void add(Number x, Number y, boolean notify) {
        
        XYDataItem item = new XYDataItem(x, y);
        add(item, notify);
    }

    
    public void add(XYDataItem item, boolean notify) {

        if (item == null) {
            throw new IllegalArgumentException("Null 'item' argument.");
        }

        if (this.autoSort) {
            int index = Collections.binarySearch(this.data, item);
            if (index < 0) {
                this.data.add(-index - 1, item);
            }
            else {
                if (this.allowDuplicateXValues) {
                    
                    int size = this.data.size();
                    while (index < size
                           && item.compareTo(this.data.get(index)) == 0) {
                        index++;
                    }
                    if (index < this.data.size()) {
                        this.data.add(index, item);
                    }
                    else {
                        this.data.add(item);
                    }
                }
                else {
                    throw new SeriesException("X-value already exists.");
                }
            }
        }
        else {
            if (!this.allowDuplicateXValues) {
                
                
                int index = indexOf(item.getX());
                if (index >= 0) {
                    throw new SeriesException("X-value already exists.");
                }
            }
            this.data.add(item);
        }
        if (getItemCount() > this.maximumItemCount) {
            this.data.remove(0);
        }
        if (notify) {
            fireSeriesChanged();
        }
    }

    
    public void delete(int start, int end) {
        for (int i = start; i <= end; i++) {
            this.data.remove(start);
        }
        fireSeriesChanged();
    }

    
    public XYDataItem remove(int index) {
        XYDataItem result = (XYDataItem) this.data.remove(index);
        fireSeriesChanged();
        return result;
    }

    
    public XYDataItem remove(Number x) {
        return remove(indexOf(x));
    }

    
    public void clear() {
        if (this.data.size() > 0) {
            this.data.clear();
            fireSeriesChanged();
        }
    }

    
    public XYDataItem getDataItem(int index) {
        return (XYDataItem) this.data.get(index);
    }

    
    public Number getX(int index) {
        return getDataItem(index).getX();
    }

    
    public Number getY(int index) {
        return getDataItem(index).getY();
    }

    
    public void updateByIndex(int index, Number y) {
        XYDataItem item = getDataItem(index);
        item.setY(y);
        fireSeriesChanged();
    }

    
    public void update(Number x, Number y) {
        int index = indexOf(x);
        if (index < 0) {
            throw new SeriesException("No observation for x = " + x);
        }
        else {
            XYDataItem item = getDataItem(index);
            item.setY(y);
            fireSeriesChanged();
        }
    }

    
    public XYDataItem addOrUpdate(double x, double y) {
        return addOrUpdate(new Double(x), new Double(y));
    }

    
    public XYDataItem addOrUpdate(Number x, Number y) {
        if (x == null) {
            throw new IllegalArgumentException("Null 'x' argument.");
        }
        if (this.allowDuplicateXValues) {
            add(x, y);
            return null;
        }

        
        XYDataItem overwritten = null;
        int index = indexOf(x);
        if (index >= 0) {
            XYDataItem existing = (XYDataItem) this.data.get(index);
            try {
                overwritten = (XYDataItem) existing.clone();
            }
            catch (CloneNotSupportedException e) {
                throw new SeriesException("Couldn't clone XYDataItem!");
            }
            existing.setY(y);
        }
        else {
            
            
            
            
            if (this.autoSort) {
                this.data.add(-index - 1, new XYDataItem(x, y));
            }
            else {
                this.data.add(new XYDataItem(x, y));
            }
            
            if (getItemCount() > this.maximumItemCount) {
                this.data.remove(0);
            }
        }
        fireSeriesChanged();
        return overwritten;
    }

    
    public int indexOf(Number x) {
        if (this.autoSort) {
            return Collections.binarySearch(this.data, new XYDataItem(x, null));
        }
        else {
            for (int i = 0; i < this.data.size(); i++) {
                XYDataItem item = (XYDataItem) this.data.get(i);
                if (item.getX().equals(x)) {
                    return i;
                }
            }
            return -1;
        }
    }

    
    public double[][] toArray() {
        int itemCount = getItemCount();
        double[][] result = new double[2][itemCount];
        for (int i = 0; i < itemCount; i++) {
            result[0][i] = this.getX(i).doubleValue();
            Number y = getY(i);
            if (y != null) {
                result[1][i] = y.doubleValue();
            }
            else {
                result[1][i] = Double.NaN;
            }
        }
        return result;
    }

    
    public Object clone() throws CloneNotSupportedException {
        XYSeries clone = (XYSeries) super.clone();
        clone.data = (List) ObjectUtilities.deepClone(this.data);
        return clone;
    }

    
    public XYSeries createCopy(int start, int end)
        throws CloneNotSupportedException {

        XYSeries copy = (XYSeries) super.clone();
        copy.data = new java.util.ArrayList();
        if (this.data.size() > 0) {
            for (int index = start; index <= end; index++) {
                XYDataItem item = (XYDataItem) this.data.get(index);
                XYDataItem clone = (XYDataItem) item.clone();
                try {
                    copy.add(clone);
                }
                catch (SeriesException e) {
                    System.err.println("Unable to add cloned data item.");
                }
            }
        }
        return copy;

    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYSeries)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        XYSeries that = (XYSeries) obj;
        if (this.maximumItemCount != that.maximumItemCount) {
            return false;
        }
        if (this.autoSort != that.autoSort) {
            return false;
        }
        if (this.allowDuplicateXValues != that.allowDuplicateXValues) {
            return false;
        }
        if (!ObjectUtilities.equal(this.data, that.data)) {
            return false;
        }
        return true;
    }

    
    public int hashCode() {
        int result = super.hashCode();
        
        
        int count = getItemCount();
        if (count > 0) {
            XYDataItem item = getDataItem(0);
            result = 29 * result + item.hashCode();
        }
        if (count > 1) {
            XYDataItem item = getDataItem(count - 1);
            result = 29 * result + item.hashCode();
        }
        if (count > 2) {
            XYDataItem item = getDataItem(count / 2);
            result = 29 * result + item.hashCode();
        }
        result = 29 * result + this.maximumItemCount;
        result = 29 * result + (this.autoSort ? 1 : 0);
        result = 29 * result + (this.allowDuplicateXValues ? 1 : 0);
        return result;
    }

}

