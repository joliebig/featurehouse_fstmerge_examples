

package org.jfree.data.statistics.junit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.data.statistics.Regression;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


public class RegressionTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(RegressionTests.class);
    }

    
    public RegressionTests(String name) {
        super(name);
    }

    
    public void testOLSRegression1a() {

        double[][] data = createSampleData1();
        double[] result1 = Regression.getOLSRegression(data);
        assertEquals(.25680930, result1[0], 0.0000001);
        assertEquals(0.72792106, result1[1], 0.0000001);

    }

    
    public void testOLSRegression1b() {

        double[][] data = createSampleData1();

        XYSeries series = new XYSeries("Test");
        for (int i = 0; i < 11; i++) {
            series.add(data[i][0], data[i][1]);
        }
        XYDataset ds = new XYSeriesCollection(series);
        double[] result2 = Regression.getOLSRegression(ds, 0);

        assertEquals(.25680930, result2[0], 0.0000001);
        assertEquals(0.72792106, result2[1], 0.0000001);

    }

    
    public void testPowerRegression1a() {

        double[][] data = createSampleData1();
        double[] result = Regression.getPowerRegression(data);
        assertEquals(0.91045813, result[0], 0.0000001);
        assertEquals(0.88918346, result[1], 0.0000001);

    }

    
    public void testPowerRegression1b() {

        double[][] data = createSampleData1();

        XYSeries series = new XYSeries("Test");
        for (int i = 0; i < 11; i++) {
            series.add(data[i][0], data[i][1]);
        }
        XYDataset ds = new XYSeriesCollection(series);
        double[] result = Regression.getPowerRegression(ds, 0);

        assertEquals(0.91045813, result[0], 0.0000001);
        assertEquals(0.88918346, result[1], 0.0000001);

    }

    
    public void testOLSRegression2a() {

        double[][] data = createSampleData2();
        double[] result = Regression.getOLSRegression(data);
        assertEquals(53.9729697, result[0], 0.0000001);
        assertEquals(-4.1823030, result[1], 0.0000001);

    }

    
    public void testOLSRegression2b() {

        double[][] data = createSampleData2();

        XYSeries series = new XYSeries("Test");
        for (int i = 0; i < 10; i++) {
            series.add(data[i][0], data[i][1]);
        }
        XYDataset ds = new XYSeriesCollection(series);
        double[] result = Regression.getOLSRegression(ds, 0);

        assertEquals(53.9729697, result[0], 0.0000001);
        assertEquals(-4.1823030, result[1], 0.0000001);

    }

    
    public void testPowerRegression2a() {

        double[][] data = createSampleData2();
        double[] result = Regression.getPowerRegression(data);
        assertEquals(106.1241681, result[0], 0.0000001);
        assertEquals(-0.8466615, result[1], 0.0000001);

    }

    
    public void testPowerRegression2b() {

        double[][] data = createSampleData2();

        XYSeries series = new XYSeries("Test");
        for (int i = 0; i < 10; i++) {
            series.add(data[i][0], data[i][1]);
        }
        XYDataset ds = new XYSeriesCollection(series);
        double[] result = Regression.getPowerRegression(ds, 0);

        assertEquals(106.1241681, result[0], 0.0000001);
        assertEquals(-0.8466615, result[1], 0.0000001);

    }

    
    private double[][] createSampleData1() {

        double[][] result = new double[11][2];

        result[0][0] = 2.00;
        result[0][1] = 1.60;
        result[1][0] = 2.25;
        result[1][1] = 2.00;
        result[2][0] = 2.60;
        result[2][1] = 1.80;
        result[3][0] = 2.65;
        result[3][1] = 2.80;
        result[4][0] = 2.80;
        result[4][1] = 2.10;
        result[5][0] = 3.10;
        result[5][1] = 2.00;
        result[6][0] = 2.90;
        result[6][1] = 2.65;
        result[7][0] = 3.25;
        result[7][1] = 2.25;
        result[8][0] = 3.30;
        result[8][1] = 2.60;
        result[9][0] = 3.60;
        result[9][1] = 3.00;
        result[10][0] = 3.25;
        result[10][1] = 3.10;

        return result;

    }

    
    private double[][] createSampleData2() {

        double[][] result = new double[10][2];

        result[0][0] = 2;
        result[0][1] = 56.27;
        result[1][0] = 3;
        result[1][1] = 41.32;
        result[2][0] = 4;
        result[2][1] = 31.45;
        result[3][0] = 5;
        result[3][1] = 30.05;
        result[4][0] = 6;
        result[4][1] = 24.69;
        result[5][0] = 7;
        result[5][1] = 19.78;
        result[6][0] = 8;
        result[6][1] = 20.94;
        result[7][0] = 9;
        result[7][1] = 16.73;
        result[8][0] = 10;
        result[8][1] = 14.21;
        result[9][0] = 11;
        result[9][1] = 12.44;

        return result;

    }

}
