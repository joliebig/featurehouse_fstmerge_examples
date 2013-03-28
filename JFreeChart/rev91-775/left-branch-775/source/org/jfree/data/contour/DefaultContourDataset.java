

package org.jfree.data.contour;

import java.util.Arrays;
import java.util.Date;
import java.util.Vector;

import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.AbstractXYZDataset;
import org.jfree.data.xy.XYDataset;


public class DefaultContourDataset extends AbstractXYZDataset 
                                   implements ContourDataset {

    
    protected Comparable seriesKey = null;

    
    protected Number[] xValues = null;

    
    protected Number[] yValues = null;

    
    protected Number[] zValues = null;

    
    protected int[] xIndex = null;

    
    boolean[] dateAxis = new boolean[3];

    
    public DefaultContourDataset() {
        super();
    }

    
    public DefaultContourDataset(Comparable seriesKey,
                                 Object[] xData,
                                 Object[] yData,
                                 Object[] zData) {

        this.seriesKey = seriesKey;
        initialize(xData, yData, zData);
    }

    
    public void initialize(Object[] xData,
                           Object[] yData,
                           Object[] zData) {

        this.xValues = new Double[xData.length];
        this.yValues = new Double[yData.length];
        this.zValues = new Double[zData.length];

        
        
        
        
        
        
        

        Vector tmpVector = new Vector(); 
        double x = 1.123452e31; 
        for (int k = 0; k < this.xValues.length; k++) {
            if (xData[k] != null) {
                Number xNumber;
                if (xData[k] instanceof Number) {
                    xNumber = (Number) xData[k];
                }
                else if (xData[k] instanceof Date) {
                    this.dateAxis[0] = true;
                    Date xDate = (Date) xData[k];
                    xNumber = new Long(xDate.getTime()); 
                }
                else {
                    xNumber = new Integer(0);
                }
                this.xValues[k] = new Double(xNumber.doubleValue()); 
                    

                
                if (x != this.xValues[k].doubleValue()) {
                    tmpVector.add(new Integer(k)); 
                                                   
                    x = this.xValues[k].doubleValue(); 
                                             
                }
            }
        }

        Object[] inttmp = tmpVector.toArray();
        this.xIndex = new int[inttmp.length];  
                                               

        for (int i = 0; i < inttmp.length; i++) {
            this.xIndex[i] = ((Integer) inttmp[i]).intValue();
        }
        for (int k = 0; k < this.yValues.length; k++) { 
                                                        
            this.yValues[k] = (Double) yData[k];
            if (zData[k] != null) {
                this.zValues[k] = (Double) zData[k];
            }
        }
    }

    
    public static Object[][] formObjectArray(double[][] data) {
        Object[][] object = new Double[data.length][data[0].length];

        for (int i = 0; i < object.length; i++) {
            for (int j = 0; j < object[i].length; j++) {
                object[i][j] = new Double(data[i][j]);
            }
        }
        return object;
    }

    
    public static Object[] formObjectArray(double[] data) {
        Object[] object = new Double[data.length];
        for (int i = 0; i < object.length; i++) {
            object[i] = new Double(data[i]);
        }
        return object;
    }

    
    public int getItemCount(int series) {
        if (series > 0) {
            throw new IllegalArgumentException("Only one series for contour");
        }
        return this.zValues.length;
    }

    
    public double getMaxZValue() {
        double zMax = -1.e20;
        for (int k = 0; k < this.zValues.length; k++) {
            if (this.zValues[k] != null) {
                zMax = Math.max(zMax, this.zValues[k].doubleValue());
            }
        }
        return zMax;
    }

    
    public double getMinZValue() {
        double zMin = 1.e20;
        for (int k = 0; k < this.zValues.length; k++) {
            if (this.zValues[k] != null) {
                zMin = Math.min(zMin, this.zValues[k].doubleValue());
            }
        }
        return zMin;
    }

    
    public Range getZValueRange(Range x, Range y) {

        double minX = x.getLowerBound();
        double minY = y.getLowerBound();
        double maxX = x.getUpperBound();
        double maxY = y.getUpperBound();

        double zMin = 1.e20;
        double zMax = -1.e20;
        for (int k = 0; k < this.zValues.length; k++) {
            if (this.xValues[k].doubleValue() >= minX
                && this.xValues[k].doubleValue() <= maxX
                && this.yValues[k].doubleValue() >= minY
                && this.yValues[k].doubleValue() <= maxY) {
                if (this.zValues[k] != null) {
                    zMin = Math.min(zMin, this.zValues[k].doubleValue());
                    zMax = Math.max(zMax, this.zValues[k].doubleValue());
                }
            }
        }

        return new Range(zMin, zMax);
    }

    
    public double getMinZValue(double minX, 
                               double minY, 
                               double maxX, 
                               double maxY) {

        double zMin = 1.e20;
        for (int k = 0; k < this.zValues.length; k++) {
            if (this.zValues[k] != null) {
                zMin = Math.min(zMin, this.zValues[k].doubleValue());
            }
        }
        return zMin;

    }

    
    public int getSeriesCount() {
        return 1;
    }

    
    public Comparable getSeriesKey(int series) {
        if (series > 0) {
            throw new IllegalArgumentException("Only one series for contour");
        }
        return this.seriesKey;
    }

    
    public int[] getXIndices() {
        return this.xIndex;
    }

    
    public Number[] getXValues() {
        return this.xValues;
    }

    
    public Number getX(int series, int item) {
        if (series > 0) {
            throw new IllegalArgumentException("Only one series for contour");
        }
        return this.xValues[item];
    }

    
    public Number getXValue(int item) {
        return this.xValues[item];
    }

    
    public Number[] getYValues() {
        return this.yValues;
    }

    
    public Number getY(int series, int item) {
        if (series > 0) {
            throw new IllegalArgumentException("Only one series for contour");
        }
        return this.yValues[item];
    }

    
    public Number[] getZValues() {
        return this.zValues;
    }

    
    public Number getZ(int series, int item) {
        if (series > 0) {
            throw new IllegalArgumentException("Only one series for contour");
        }
        return this.zValues[item];
    }

    
    public int[] indexX() {
        int[] index = new int[this.xValues.length];
        for (int k = 0; k < index.length; k++) {
            index[k] = indexX(k);
        }
        return index;
    }

    
    public int indexX(int k) {
        int i = Arrays.binarySearch(this.xIndex, k);
        if (i >= 0) {
            return i;
        } 
        else {
            return -1 * i - 2;
        }
    }


    
    public int indexY(int k) { 
        return (k / this.xValues.length);
    }

    
    public int indexZ(int i, int j) {
        return this.xValues.length * j + i;
    }

    
    public boolean isDateAxis(int axisNumber) {
        if (axisNumber < 0 || axisNumber > 2) {
            return false; 
        }
        return this.dateAxis[axisNumber];
    }

    
    public void setSeriesKeys(Comparable[] seriesKeys) {
        if (seriesKeys.length > 1) {
            throw new IllegalArgumentException(
                    "Contours only support one series");
        }
        this.seriesKey = seriesKeys[0];
        fireDatasetChanged();
    }

}
