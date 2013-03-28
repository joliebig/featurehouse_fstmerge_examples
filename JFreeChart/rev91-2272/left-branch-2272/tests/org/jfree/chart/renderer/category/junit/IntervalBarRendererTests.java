

package org.jfree.chart.renderer.category.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.IntervalBarRenderer;
import org.jfree.data.Range;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.category.DefaultIntervalCategoryDataset;
import org.jfree.util.PublicCloneable;


public class IntervalBarRendererTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(IntervalBarRendererTests.class);
    }

    
    public IntervalBarRendererTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        IntervalBarRenderer r1 = new IntervalBarRenderer();
        IntervalBarRenderer r2 = new IntervalBarRenderer();
        assertEquals(r1, r2);

        
        BarRenderer br = new BarRenderer();
        assertFalse(r1.equals(br));
    }

    
    public void testHashcode() {
        IntervalBarRenderer r1 = new IntervalBarRenderer();
        IntervalBarRenderer r2 = new IntervalBarRenderer();
        assertTrue(r1.equals(r2));
        int h1 = r1.hashCode();
        int h2 = r2.hashCode();
        assertEquals(h1, h2);
    }

    
    public void testCloning() {
        IntervalBarRenderer r1 = new IntervalBarRenderer();
        IntervalBarRenderer r2 = null;
        try {
            r2 = (IntervalBarRenderer) r1.clone();
        }
        catch (CloneNotSupportedException e) {
            System.err.println("Failed to clone.");
        }
        assertTrue(r1 != r2);
        assertTrue(r1.getClass() == r2.getClass());
        assertTrue(r1.equals(r2));
    }

    
    public void testPublicCloneable() {
        IntervalBarRenderer r1 = new IntervalBarRenderer();
        assertTrue(r1 instanceof PublicCloneable);
    }

    
    public void testSerialization() {
        IntervalBarRenderer r1 = new IntervalBarRenderer();
        IntervalBarRenderer r2 = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(r1);
            out.close();
            ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                    buffer.toByteArray()));
            r2 = (IntervalBarRenderer) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(r1, r2);
    }

    
    public void testDrawWithNullInfo() {
        boolean success = false;
        try {
            double[][] starts = new double[][] {{0.1, 0.2, 0.3},
                    {0.3, 0.4, 0.5}};
            double[][] ends = new double[][] {{0.5, 0.6, 0.7}, {0.7, 0.8, 0.9}};
            DefaultIntervalCategoryDataset dataset
                    = new DefaultIntervalCategoryDataset(starts, ends);
            IntervalBarRenderer renderer = new IntervalBarRenderer();
            CategoryPlot plot = new CategoryPlot(dataset,
                    new CategoryAxis("Category"), new NumberAxis("Value"),
                    renderer);
            JFreeChart chart = new JFreeChart(plot);
             chart.createBufferedImage(300, 200,
                    null);
            success = true;
        }
        catch (NullPointerException e) {
            e.printStackTrace();
            success = false;
        }
        assertTrue(success);
    }

    
    public void testFindRangeBounds() {
        IntervalBarRenderer r = new IntervalBarRenderer();
        assertNull(r.findRangeBounds(null));

        
        DefaultIntervalCategoryDataset dataset
                = new DefaultIntervalCategoryDataset(new double[0][0],
                new double[0][0]);
        assertNull(r.findRangeBounds(dataset));

        double[][] starts = new double[][] {{0.1, 0.2, 0.3}, {0.3, 0.4, 0.5}};
        double[][] ends = new double[][] {{0.5, 0.6, 0.7}, {0.7, 0.8, 0.9}};
        dataset = new DefaultIntervalCategoryDataset(starts, ends);
        assertEquals(new Range(0.0, 0.9), r.findRangeBounds(dataset));
        r.setIncludeBaseInRange(false);
        assertEquals(new Range(0.1, 0.9), r.findRangeBounds(dataset));
        r.setIncludeBaseInRange(true);

        r.setSeriesVisible(1, Boolean.FALSE);
        assertEquals(new Range(0.0, 0.7), r.findRangeBounds(dataset));
    }

}
