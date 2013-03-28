

package org.jfree.data.statistics;

import java.util.List;

import org.jfree.chart.util.PublicCloneable;
import org.jfree.data.KeyedObjects2D;
import org.jfree.data.Range;
import org.jfree.data.RangeInfo;
import org.jfree.data.general.AbstractDataset;
import org.jfree.data.general.DatasetChangeEvent;


public class DefaultStatisticalCategoryDataset extends AbstractDataset
        implements StatisticalCategoryDataset, RangeInfo, PublicCloneable {

    
    private KeyedObjects2D data;

    
    private double minimumRangeValue;

    
    private int minimumRangeValueRow;

    
    private int minimumRangeValueColumn;

    
    private double minimumRangeValueIncStdDev;

    
    private int minimumRangeValueIncStdDevRow;

    
    private int minimumRangeValueIncStdDevColumn;

    
    private double maximumRangeValue;

    
    private int maximumRangeValueRow;

    
    private int maximumRangeValueColumn;

    
    private double maximumRangeValueIncStdDev;

    
    private int maximumRangeValueIncStdDevRow;

    
    private int maximumRangeValueIncStdDevColumn;

    
    public DefaultStatisticalCategoryDataset() {
        this.data = new KeyedObjects2D();
        this.minimumRangeValue = Double.NaN;
        this.minimumRangeValueRow = -1;
        this.minimumRangeValueColumn = -1;
        this.maximumRangeValue = Double.NaN;
        this.maximumRangeValueRow = -1;
        this.maximumRangeValueColumn = -1;
        this.minimumRangeValueIncStdDev = Double.NaN;
        this.minimumRangeValueIncStdDevRow = -1;
        this.minimumRangeValueIncStdDevColumn = -1;
        this.maximumRangeValueIncStdDev = Double.NaN;
        this.maximumRangeValueIncStdDevRow = -1;
        this.maximumRangeValueIncStdDevColumn = -1;
    }

    
    public Number getMeanValue(int row, int column) {
        Number result = null;
        MeanAndStandardDeviation masd = (MeanAndStandardDeviation)
                this.data.getObject(row, column);
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
        MeanAndStandardDeviation masd = (MeanAndStandardDeviation)
                this.data.getObject(rowKey, columnKey);
        if (masd != null) {
            result = masd.getMean();
        }
        return result;
    }

    
    public Number getStdDevValue(int row, int column) {
        Number result = null;
        MeanAndStandardDeviation masd = (MeanAndStandardDeviation)
                this.data.getObject(row, column);
        if (masd != null) {
            result = masd.getStandardDeviation();
        }
        return result;
    }

    
    public Number getStdDevValue(Comparable rowKey, Comparable columnKey) {
        Number result = null;
        MeanAndStandardDeviation masd = (MeanAndStandardDeviation)
                this.data.getObject(rowKey, columnKey);
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

        double m = Double.NaN;
        double sd = Double.NaN;
        if (mean != null) {
            m = mean.doubleValue();
        }
        if (standardDeviation != null) {
            sd = standardDeviation.doubleValue();
        }

        
        int r = this.data.getColumnIndex(columnKey);
        int c = this.data.getRowIndex(rowKey);
        if ((r == this.maximumRangeValueRow && c
                == this.maximumRangeValueColumn) || (r
                == this.maximumRangeValueIncStdDevRow && c
                == this.maximumRangeValueIncStdDevColumn) || (r
                == this.minimumRangeValueRow && c
                == this.minimumRangeValueColumn) || (r
                == this.minimumRangeValueIncStdDevRow && c
                == this.minimumRangeValueIncStdDevColumn)) {

            
            updateBounds();
        }
        else {
            if (!Double.isNaN(m)) {
                if (Double.isNaN(this.maximumRangeValue)
                        || m > this.maximumRangeValue) {
                    this.maximumRangeValue = m;
                    this.maximumRangeValueRow = r;
                    this.maximumRangeValueColumn = c;
                }
            }

            if (!Double.isNaN(m + sd)) {
                if (Double.isNaN(this.maximumRangeValueIncStdDev)
                        || (m + sd) > this.maximumRangeValueIncStdDev) {
                    this.maximumRangeValueIncStdDev = m + sd;
                    this.maximumRangeValueIncStdDevRow = r;
                    this.maximumRangeValueIncStdDevColumn = c;
                }
            }

            if (!Double.isNaN(m)) {
                if (Double.isNaN(this.minimumRangeValue)
                        || m < this.minimumRangeValue) {
                    this.minimumRangeValue = m;
                    this.minimumRangeValueRow = r;
                    this.minimumRangeValueColumn = c;
                }
            }

            if (!Double.isNaN(m - sd)) {
                if (Double.isNaN(this.minimumRangeValueIncStdDev)
                        || (m - sd) < this.minimumRangeValueIncStdDev) {
                    this.minimumRangeValueIncStdDev = m - sd;
                    this.minimumRangeValueIncStdDevRow = r;
                    this.minimumRangeValueIncStdDevColumn = c;
                }
            }
        }
        fireDatasetChanged();
    }

    
    public void remove(Comparable rowKey, Comparable columnKey) {
        
        int r = getRowIndex(rowKey);
        int c = getColumnIndex(columnKey);
        this.data.removeObject(rowKey, columnKey);

        
        
        if ((r == this.maximumRangeValueRow && c
                == this.maximumRangeValueColumn) || (r
                == this.maximumRangeValueIncStdDevRow && c
                == this.maximumRangeValueIncStdDevColumn) || (r
                == this.minimumRangeValueRow && c
                == this.minimumRangeValueColumn) || (r
                == this.minimumRangeValueIncStdDevRow && c
                == this.minimumRangeValueIncStdDevColumn)) {

            
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

    
    private void updateBounds() {
        this.maximumRangeValue = Double.NaN;
        this.maximumRangeValueRow = -1;
        this.maximumRangeValueColumn = -1;
        this.minimumRangeValue = Double.NaN;
        this.minimumRangeValueRow = -1;
        this.minimumRangeValueColumn = -1;
        this.maximumRangeValueIncStdDev = Double.NaN;
        this.maximumRangeValueIncStdDevRow = -1;
        this.maximumRangeValueIncStdDevColumn = -1;
        this.minimumRangeValueIncStdDev = Double.NaN;
        this.minimumRangeValueIncStdDevRow = -1;
        this.minimumRangeValueIncStdDevColumn = -1;

        int rowCount = this.data.getRowCount();
        int columnCount = this.data.getColumnCount();
        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                double m = Double.NaN;
                double sd = Double.NaN;
                MeanAndStandardDeviation masd = (MeanAndStandardDeviation)
                        this.data.getObject(r, c);
                if (masd == null) {
                    continue;
                }
                m = masd.getMeanValue();
                sd = masd.getStandardDeviationValue();

                if (!Double.isNaN(m)) {

                    
                    if (Double.isNaN(this.maximumRangeValue)) {
                        this.maximumRangeValue = m;
                        this.maximumRangeValueRow = r;
                        this.maximumRangeValueColumn = c;
                    }
                    else {
                        if (m > this.maximumRangeValue) {
                            this.maximumRangeValue = m;
                            this.maximumRangeValueRow = r;
                            this.maximumRangeValueColumn = c;
                        }
                    }

                    
                    if (Double.isNaN(this.minimumRangeValue)) {
                        this.minimumRangeValue = m;
                        this.minimumRangeValueRow = r;
                        this.minimumRangeValueColumn = c;
                    }
                    else {
                        if (m < this.minimumRangeValue) {
                            this.minimumRangeValue = m;
                            this.minimumRangeValueRow = r;
                            this.minimumRangeValueColumn = c;
                        }
                    }

                    if (!Double.isNaN(sd)) {
                        
                        if (Double.isNaN(this.maximumRangeValueIncStdDev)) {
                            this.maximumRangeValueIncStdDev = m + sd;
                            this.maximumRangeValueIncStdDevRow = r;
                            this.maximumRangeValueIncStdDevColumn = c;
                        }
                        else {
                            if (m + sd > this.maximumRangeValueIncStdDev) {
                                this.maximumRangeValueIncStdDev = m + sd;
                                this.maximumRangeValueIncStdDevRow = r;
                                this.maximumRangeValueIncStdDevColumn = c;
                            }
                        }

                        
                        if (Double.isNaN(this.minimumRangeValueIncStdDev)) {
                            this.minimumRangeValueIncStdDev = m - sd;
                            this.minimumRangeValueIncStdDevRow = r;
                            this.minimumRangeValueIncStdDevColumn = c;
                        }
                        else {
                            if (m - sd < this.minimumRangeValueIncStdDev) {
                                this.minimumRangeValueIncStdDev = m - sd;
                                this.minimumRangeValueIncStdDevRow = r;
                                this.minimumRangeValueIncStdDevColumn = c;
                            }
                        }
                    }
                }
            }
        }
    }

    
    public double getRangeLowerBound(boolean includeInterval) {
        if (includeInterval) {
            return this.minimumRangeValueIncStdDev;
        }
        else {
            return this.minimumRangeValue;
        }
    }

    
    public double getRangeUpperBound(boolean includeInterval) {
        if (includeInterval) {
            return this.maximumRangeValueIncStdDev;
        }
        else {
            return this.maximumRangeValue;
        }
    }

    
    public Range getRangeBounds(boolean includeInterval) {
        Range result = null;
        if (includeInterval) {
            if (!Double.isNaN(this.minimumRangeValueIncStdDev)
                    && !Double.isNaN(this.maximumRangeValueIncStdDev)) {
                result = new Range(this.minimumRangeValueIncStdDev,
                        this.maximumRangeValueIncStdDev);
            }
        }
        else {
            if (!Double.isNaN(this.minimumRangeValue)
                    && !Double.isNaN(this.maximumRangeValue)) {
                result = new Range(this.minimumRangeValue,
                        this.maximumRangeValue);
            }
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

    
    public Object clone() throws CloneNotSupportedException {
        DefaultStatisticalCategoryDataset clone
                = (DefaultStatisticalCategoryDataset) super.clone();
        clone.data = (KeyedObjects2D) this.data.clone();
        return clone;
    }
}
