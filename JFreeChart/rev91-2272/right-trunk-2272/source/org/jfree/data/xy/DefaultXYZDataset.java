

package org.jfree.data.xy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jfree.chart.event.DatasetChangeInfo;
import org.jfree.chart.util.PublicCloneable;
import org.jfree.data.DomainOrder;
import org.jfree.data.event.DatasetChangeEvent;


public class DefaultXYZDataset extends AbstractXYZDataset
        implements XYZDataset, PublicCloneable {

    
    private List seriesKeys;

    
    private List seriesList;

    
    public DefaultXYZDataset() {
        this.seriesKeys = new java.util.ArrayList();
        this.seriesList = new java.util.ArrayList();
    }

    
    public int getSeriesCount() {
        return this.seriesList.size();
    }

    
    public Comparable getSeriesKey(int series) {
        if ((series < 0) || (series >= getSeriesCount())) {
            throw new IllegalArgumentException("Series index out of bounds");
        }
        return (Comparable) this.seriesKeys.get(series);
    }

    
    public int indexOf(Comparable seriesKey) {
        return this.seriesKeys.indexOf(seriesKey);
    }

    
    public DomainOrder getDomainOrder() {
        return DomainOrder.NONE;
    }

    
    public int getItemCount(int series) {
        if ((series < 0) || (series >= getSeriesCount())) {
            throw new IllegalArgumentException("Series index out of bounds");
        }
        double[][] seriesArray = (double[][]) this.seriesList.get(series);
        return seriesArray[0].length;
    }

    
    public double getXValue(int series, int item) {
        double[][] seriesData = (double[][]) this.seriesList.get(series);
        return seriesData[0][item];
    }

    
    public Number getX(int series, int item) {
        return new Double(getXValue(series, item));
    }

    
    public double getYValue(int series, int item) {
        double[][] seriesData = (double[][]) this.seriesList.get(series);
        return seriesData[1][item];
    }

    
    public Number getY(int series, int item) {
        return new Double(getYValue(series, item));
    }

    
    public double getZValue(int series, int item) {
        double[][] seriesData = (double[][]) this.seriesList.get(series);
        return seriesData[2][item];
    }

    
    public Number getZ(int series, int item) {
        return new Double(getZValue(series, item));
    }

    
    public void addSeries(Comparable seriesKey, double[][] data) {
        if (seriesKey == null) {
            throw new IllegalArgumentException(
                    "The 'seriesKey' cannot be null.");
        }
        if (data == null) {
            throw new IllegalArgumentException("The 'data' is null.");
        }
        if (data.length != 3) {
            throw new IllegalArgumentException(
                    "The 'data' array must have length == 3.");
        }
        if (data[0].length != data[1].length
                || data[0].length != data[2].length) {
            throw new IllegalArgumentException("The 'data' array must contain "
                    + "three arrays all having the same length.");
        }
        int seriesIndex = indexOf(seriesKey);
        if (seriesIndex == -1) {  
            this.seriesKeys.add(seriesKey);
            this.seriesList.add(data);
        }
        else {  
            this.seriesList.remove(seriesIndex);
            this.seriesList.add(seriesIndex, data);
        }
        fireDatasetChanged(new DatasetChangeInfo());
        
    }

    
    public void removeSeries(Comparable seriesKey) {
        int seriesIndex = indexOf(seriesKey);
        if (seriesIndex >= 0) {
            this.seriesKeys.remove(seriesIndex);
            this.seriesList.remove(seriesIndex);
            fireDatasetChanged(new DatasetChangeInfo());
            
        }
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DefaultXYZDataset)) {
            return false;
        }
        DefaultXYZDataset that = (DefaultXYZDataset) obj;
        if (!this.seriesKeys.equals(that.seriesKeys)) {
            return false;
        }
        for (int i = 0; i < this.seriesList.size(); i++) {
            double[][] d1 = (double[][]) this.seriesList.get(i);
            double[][] d2 = (double[][]) that.seriesList.get(i);
            double[] d1x = d1[0];
            double[] d2x = d2[0];
            if (!Arrays.equals(d1x, d2x)) {
                return false;
            }
            double[] d1y = d1[1];
            double[] d2y = d2[1];
            if (!Arrays.equals(d1y, d2y)) {
                return false;
            }
            double[] d1z = d1[2];
            double[] d2z = d2[2];
            if (!Arrays.equals(d1z, d2z)) {
                return false;
            }
        }
        return true;
    }

    
    public int hashCode() {
        int result;
        result = this.seriesKeys.hashCode();
        result = 29 * result + this.seriesList.hashCode();
        return result;
    }

    
    public Object clone() throws CloneNotSupportedException {
        DefaultXYZDataset clone = (DefaultXYZDataset) super.clone();
        clone.seriesKeys = new java.util.ArrayList(this.seriesKeys);
        clone.seriesList = new ArrayList(this.seriesList.size());
        for (int i = 0; i < this.seriesList.size(); i++) {
            double[][] data = (double[][]) this.seriesList.get(i);
            double[] x = data[0];
            double[] y = data[1];
            double[] z = data[2];
            double[] xx = new double[x.length];
            double[] yy = new double[y.length];
            double[] zz = new double[z.length];
            System.arraycopy(x, 0, xx, 0, x.length);
            System.arraycopy(y, 0, yy, 0, y.length);
            System.arraycopy(z, 0, zz, 0, z.length);
            clone.seriesList.add(i, new double[][] {xx, yy, zz});
        }
        return clone;
    }

}
