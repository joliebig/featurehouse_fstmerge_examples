

package org.jfree.data.xy;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.jfree.util.PublicCloneable;


public class DefaultWindDataset extends AbstractXYDataset
        implements WindDataset, PublicCloneable {

    
    private List seriesKeys;

    
    private List allSeriesData;

    
    public DefaultWindDataset() {
        this.seriesKeys = new java.util.ArrayList();
        this.allSeriesData = new java.util.ArrayList();
    }

    
    public DefaultWindDataset(Object[][][] data) {
        this(seriesNameListFromDataArray(data), data);
    }

    
    public DefaultWindDataset(String[] seriesNames, Object[][][] data) {
        this(Arrays.asList(seriesNames), data);
    }

    
    public DefaultWindDataset(List seriesKeys, Object[][][] data) {
        if (seriesKeys == null) {
            throw new IllegalArgumentException("Null 'seriesKeys' argument.");
        }
        if (seriesKeys.size() != data.length) {
            throw new IllegalArgumentException("The number of series keys does "
                    + "not match the number of series in the data array.");
        }
        this.seriesKeys = seriesKeys;
        int seriesCount = data.length;
        this.allSeriesData = new java.util.ArrayList(seriesCount);

        for (int seriesIndex = 0; seriesIndex < seriesCount; seriesIndex++) {
            List oneSeriesData = new java.util.ArrayList();
            int maxItemCount = data[seriesIndex].length;
            for (int itemIndex = 0; itemIndex < maxItemCount; itemIndex++) {
                Object xObject = data[seriesIndex][itemIndex][0];
                if (xObject != null) {
                    Number xNumber;
                    if (xObject instanceof Number) {
                        xNumber = (Number) xObject;
                    }
                    else {
                        if (xObject instanceof Date) {
                            Date xDate = (Date) xObject;
                            xNumber = new Long(xDate.getTime());
                        }
                        else {
                            xNumber = new Integer(0);
                        }
                    }
                    Number windDir = (Number) data[seriesIndex][itemIndex][1];
                    Number windForce = (Number) data[seriesIndex][itemIndex][2];
                    oneSeriesData.add(new WindDataItem(xNumber, windDir,
                            windForce));
                }
            }
            Collections.sort(oneSeriesData);
            this.allSeriesData.add(seriesIndex, oneSeriesData);
        }

    }

    
    public int getSeriesCount() {
        return this.allSeriesData.size();
    }

    
    public int getItemCount(int series) {
        if (series < 0 || series >= getSeriesCount()) {
            throw new IllegalArgumentException("Invalid series index: "
                    + series);
        }
        List oneSeriesData = (List) this.allSeriesData.get(series);
        return oneSeriesData.size();
    }

    
    public Comparable getSeriesKey(int series) {
        if (series < 0 || series >= getSeriesCount()) {
            throw new IllegalArgumentException("Invalid series index: "
                    + series);
        }
        return (Comparable) this.seriesKeys.get(series);
    }

    
    public Number getX(int series, int item) {
        List oneSeriesData = (List) this.allSeriesData.get(series);
        WindDataItem windItem = (WindDataItem) oneSeriesData.get(item);
        return windItem.getX();
    }

    
    public Number getY(int series, int item) {
        return getWindForce(series, item);
    }

    
    public Number getWindDirection(int series, int item) {
        List oneSeriesData = (List) this.allSeriesData.get(series);
        WindDataItem windItem = (WindDataItem) oneSeriesData.get(item);
        return windItem.getWindDirection();
    }

    
    public Number getWindForce(int series, int item) {
        List oneSeriesData = (List) this.allSeriesData.get(series);
        WindDataItem windItem = (WindDataItem) oneSeriesData.get(item);
        return windItem.getWindForce();
    }

    
    public static List seriesNameListFromDataArray(Object[][] data) {

        int seriesCount = data.length;
        List seriesNameList = new java.util.ArrayList(seriesCount);
        for (int i = 0; i < seriesCount; i++) {
            seriesNameList.add("Series " + (i + 1));
        }
        return seriesNameList;

    }

    
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof DefaultWindDataset)) {
            return false;
        }
        DefaultWindDataset that = (DefaultWindDataset) obj;
        if (!this.seriesKeys.equals(that.seriesKeys)) {
            return false;
        }
        if (!this.allSeriesData.equals(that.allSeriesData)) {
            return false;
        }
        return true;
    }

}


class WindDataItem implements Comparable, Serializable {

    
    private Number x;

    
    private Number windDir;

    
    private Number windForce;

    
    public WindDataItem(Number x, Number windDir, Number windForce) {
        this.x = x;
        this.windDir = windDir;
        this.windForce = windForce;
    }

    
    public Number getX() {
        return this.x;
    }

    
    public Number getWindDirection() {
        return this.windDir;
    }

    
    public Number getWindForce() {
        return this.windForce;
    }

    
    public int compareTo(Object object) {
        if (object instanceof WindDataItem) {
            WindDataItem item = (WindDataItem) object;
            if (this.x.doubleValue() > item.x.doubleValue()) {
                return 1;
            }
            else if (this.x.equals(item.x)) {
                return 0;
            }
            else {
                return -1;
            }
        }
        else {
            throw new ClassCastException("WindDataItem.compareTo(error)");
        }
    }

    
    public boolean equals(Object obj) {
        if (this == obj) {
            return false;
        }
        if (!(obj instanceof WindDataItem)) {
            return false;
        }
        WindDataItem that = (WindDataItem) obj;
        if (!this.x.equals(that.x)) {
            return false;
        }
        if (!this.windDir.equals(that.windDir)) {
            return false;
        }
        if (!this.windForce.equals(that.windForce)) {
            return false;
        }
        return true;
    }

}
