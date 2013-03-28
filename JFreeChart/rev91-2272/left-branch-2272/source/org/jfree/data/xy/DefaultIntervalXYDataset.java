

package org.jfree.data.xy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.util.PublicCloneable;


public class DefaultIntervalXYDataset extends AbstractIntervalXYDataset
        implements PublicCloneable {

    
    private List seriesKeys;

    
    private List seriesList;

    
    public DefaultIntervalXYDataset() {
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

    
    public double getYValue(int series, int item) {
        double[][] seriesData = (double[][]) this.seriesList.get(series);
        return seriesData[3][item];
    }

    
    public double getStartXValue(int series, int item) {
        double[][] seriesData = (double[][]) this.seriesList.get(series);
        return seriesData[1][item];
    }

    
    public double getEndXValue(int series, int item) {
        double[][] seriesData = (double[][]) this.seriesList.get(series);
        return seriesData[2][item];
    }

    
    public double getStartYValue(int series, int item) {
        double[][] seriesData = (double[][]) this.seriesList.get(series);
        return seriesData[4][item];
    }

    
    public double getEndYValue(int series, int item) {
        double[][] seriesData = (double[][]) this.seriesList.get(series);
        return seriesData[5][item];
    }

    
    public Number getEndX(int series, int item) {
        return new Double(getEndXValue(series, item));
    }

    
    public Number getEndY(int series, int item) {
        return new Double(getEndYValue(series, item));
    }

    
    public Number getStartX(int series, int item) {
        return new Double(getStartXValue(series, item));
    }

    
    public Number getStartY(int series, int item) {
        return new Double(getStartYValue(series, item));
    }

    
    public Number getX(int series, int item) {
        return new Double(getXValue(series, item));
    }

    
    public Number getY(int series, int item) {
        return new Double(getYValue(series, item));
    }

    
    public void addSeries(Comparable seriesKey, double[][] data) {
        if (seriesKey == null) {
            throw new IllegalArgumentException(
                    "The 'seriesKey' cannot be null.");
        }
        if (data == null) {
            throw new IllegalArgumentException("The 'data' is null.");
        }
        if (data.length != 6) {
            throw new IllegalArgumentException(
                    "The 'data' array must have length == 6.");
        }
        int length = data[0].length;
        if (length != data[1].length || length != data[2].length
                || length != data[3].length || length != data[4].length
                || length != data[5].length) {
            throw new IllegalArgumentException(
                "The 'data' array must contain six arrays with equal length.");
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
        notifyListeners(new DatasetChangeEvent(this, this));
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DefaultIntervalXYDataset)) {
            return false;
        }
        DefaultIntervalXYDataset that = (DefaultIntervalXYDataset) obj;
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
            double[] d1xs = d1[1];
            double[] d2xs = d2[1];
            if (!Arrays.equals(d1xs, d2xs)) {
                return false;
            }
            double[] d1xe = d1[2];
            double[] d2xe = d2[2];
            if (!Arrays.equals(d1xe, d2xe)) {
                return false;
            }
            double[] d1y = d1[3];
            double[] d2y = d2[3];
            if (!Arrays.equals(d1y, d2y)) {
                return false;
            }
            double[] d1ys = d1[4];
            double[] d2ys = d2[4];
            if (!Arrays.equals(d1ys, d2ys)) {
                return false;
            }
            double[] d1ye = d1[5];
            double[] d2ye = d2[5];
            if (!Arrays.equals(d1ye, d2ye)) {
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
        DefaultIntervalXYDataset clone
                = (DefaultIntervalXYDataset) super.clone();
        clone.seriesKeys = new java.util.ArrayList(this.seriesKeys);
        clone.seriesList = new ArrayList(this.seriesList.size());
        for (int i = 0; i < this.seriesList.size(); i++) {
            double[][] data = (double[][]) this.seriesList.get(i);
            double[] x = data[0];
            double[] xStart = data[1];
            double[] xEnd = data[2];
            double[] y = data[3];
            double[] yStart = data[4];
            double[] yEnd = data[5];
            double[] xx = new double[x.length];
            double[] xxStart = new double[xStart.length];
            double[] xxEnd = new double[xEnd.length];
            double[] yy = new double[y.length];
            double[] yyStart = new double[yStart.length];
            double[] yyEnd = new double[yEnd.length];
            System.arraycopy(x, 0, xx, 0, x.length);
            System.arraycopy(xStart, 0, xxStart, 0, xStart.length);
            System.arraycopy(xEnd, 0, xxEnd, 0, xEnd.length);
            System.arraycopy(y, 0, yy, 0, y.length);
            System.arraycopy(yStart, 0, yyStart, 0, yStart.length);
            System.arraycopy(yEnd, 0, yyEnd, 0, yEnd.length);
            clone.seriesList.add(i, new double[][] {xx, xxStart, xxEnd, yy,
                    yyStart, yyEnd});
        }
        return clone;
    }

}
