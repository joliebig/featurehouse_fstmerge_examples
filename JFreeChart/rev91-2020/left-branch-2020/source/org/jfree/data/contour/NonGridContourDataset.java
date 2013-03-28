

package org.jfree.data.contour;

import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.jfree.data.Range;


public class NonGridContourDataset extends DefaultContourDataset {

    
    static final int DEFAULT_NUM_X = 50;

    
    static final int DEFAULT_NUM_Y = 50;

    
    static final int DEFAULT_POWER = 4;

    
    public NonGridContourDataset() {
        super();
    }

    
    public NonGridContourDataset(String seriesName,
                                 Object[] xData, Object[] yData,
                                 Object[] zData) {
        super(seriesName, xData, yData, zData);
        buildGrid(DEFAULT_NUM_X, DEFAULT_NUM_Y, DEFAULT_POWER);
    }

    
    public NonGridContourDataset(String seriesName,
                                 Object[] xData, Object[] yData,
                                 Object[] zData,
                                 int numX, int numY, int power) {
        super(seriesName, xData, yData, zData);
        buildGrid(numX, numY, power);
    }

    
    protected void buildGrid(int numX, int numY, int power) {

        int numValues = numX * numY;
        double[] xGrid = new double[numValues];
        double[] yGrid = new double [numValues];
        double[] zGrid = new double [numValues];

        
        double xMin = 1.e20;
        for (int k = 0; k < this.xValues.length; k++) {
            xMin = Math.min(xMin, this.xValues[k].doubleValue());
        }

        double xMax = -1.e20;
        for (int k = 0; k < this.xValues.length; k++) {
            xMax = Math.max(xMax, this.xValues[k].doubleValue());
        }

        double yMin = 1.e20;
        for (int k = 0; k < this.yValues.length; k++) {
            yMin = Math.min(yMin, this.yValues[k].doubleValue());
        }

        double yMax = -1.e20;
        for (int k = 0; k < this.yValues.length; k++) {
            yMax = Math.max(yMax, this.yValues[k].doubleValue());
        }

        Range xRange = new Range(xMin, xMax);
        Range yRange = new Range(yMin, yMax);

        xRange.getLength();
        yRange.getLength();

        
        double dxGrid = xRange.getLength() / (numX - 1);
        double dyGrid = yRange.getLength() / (numY - 1);

        
        double x = 0.0;
        for (int i = 0; i < numX; i++) {
            if (i == 0) {
                x = xMin;
            }
            else {
                x += dxGrid;
            }
            double y = 0.0;
            for (int j = 0; j < numY; j++) {
                int k = numY * i + j;
                xGrid[k] = x;
                if (j == 0) {
                    y = yMin;
                }
                else {
                    y += dyGrid;
                }
                yGrid[k] = y;
            }
        }

        
        for (int kGrid = 0; kGrid < xGrid.length; kGrid++) {
            double dTotal = 0.0;
            zGrid[kGrid] = 0.0;
            for (int k = 0; k < this.xValues.length; k++) {
                double xPt = this.xValues[k].doubleValue();
                double yPt = this.yValues[k].doubleValue();
                double d = distance(xPt, yPt, xGrid[kGrid], yGrid[kGrid]);
                if (power != 1) {
                    d = Math.pow(d, power);
                }
                d = Math.sqrt(d);
                if (d > 0.0) {
                    d = 1.0 / d;
                }
                else { 
                       
                    d = 1.e20;
                }
                if (this.zValues[k] != null) {
                    
                    zGrid[kGrid] += this.zValues[k].doubleValue() * d;
                }
                dTotal += d;
            }
            zGrid[kGrid] = zGrid[kGrid] / dTotal;  
        }

        
        initialize(
            formObjectArray(xGrid), formObjectArray(yGrid),
            formObjectArray(zGrid)
        );

    }

    
    protected double distance(double xDataPt,
                              double yDataPt,
                              double xGrdPt,
                              double yGrdPt) {
        double dx = xDataPt - xGrdPt;
        double dy = yDataPt - yGrdPt;
        return Math.sqrt(dx * dx + dy * dy);
    }

}
