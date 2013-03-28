

package org.jfree.chart.renderer.category.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import java.util.Arrays;
import java.util.List;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.renderer.category.ScatterRenderer;
import org.jfree.data.Range;
import org.jfree.data.statistics.DefaultMultiValueCategoryDataset;
import org.jfree.util.PublicCloneable;


public class ScatterRendererTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(ScatterRendererTests.class);
    }

    
    public ScatterRendererTests(String name) {
        super(name);
    }

    
    public void testEquals() {

        ScatterRenderer r1 = new ScatterRenderer();
        ScatterRenderer r2 = new ScatterRenderer();
        assertEquals(r1, r2);

        r1.setSeriesShapesFilled(1, true);
        assertFalse(r1.equals(r2));
        r2.setSeriesShapesFilled(1, true);
        assertTrue(r1.equals(r2));

        r1.setBaseShapesFilled(false);
        assertFalse(r1.equals(r2));
        r2.setBaseShapesFilled(false);
        assertTrue(r1.equals(r2));

        r1.setUseFillPaint(true);
        assertFalse(r1.equals(r2));
        r2.setUseFillPaint(true);
        assertTrue(r1.equals(r2));

        r1.setDrawOutlines(true);
        assertFalse(r1.equals(r2));
        r2.setDrawOutlines(true);
        assertTrue(r1.equals(r2));

        r1.setUseOutlinePaint(true);
        assertFalse(r1.equals(r2));
        r2.setUseOutlinePaint(true);
        assertTrue(r1.equals(r2));

        r1.setUseSeriesOffset(false);
        assertFalse(r1.equals(r2));
        r2.setUseSeriesOffset(false);
        assertTrue(r1.equals(r2));

    }

    
    public void testHashcode() {
        ScatterRenderer r1 = new ScatterRenderer();
        ScatterRenderer r2 = new ScatterRenderer();
        assertTrue(r1.equals(r2));
        int h1 = r1.hashCode();
        int h2 = r2.hashCode();
        assertEquals(h1, h2);
    }

    
    public void testCloning() {
        ScatterRenderer r1 = new ScatterRenderer();
        ScatterRenderer r2 = null;
        try {
            r2 = (ScatterRenderer) r1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(r1 != r2);
        assertTrue(r1.getClass() == r2.getClass());
        assertTrue(r1.equals(r2));

        assertTrue(checkIndependence(r1, r2));
    }

    
    public void testPublicCloneable() {
        ScatterRenderer r1 = new ScatterRenderer();
        assertTrue(r1 instanceof PublicCloneable);
    }

    
    private boolean checkIndependence(ScatterRenderer r1,
            ScatterRenderer r2) {

        
        if (!r1.equals(r2)) {
            return false;
        }

        
        r1.setSeriesShapesFilled(1, true);
        if (r1.equals(r2)) {
            return false;
        }
        r2.setSeriesShapesFilled(1, true);
        if (!r1.equals(r2)) {
            return false;
        }

        r1.setBaseShapesFilled(false);
        r2.setBaseShapesFilled(true);
        if (r1.equals(r2)) {
            return false;
        }
        r2.setBaseShapesFilled(false);
        if (!r1.equals(r2)) {
            return false;
        }
        return true;

    }

    
    public void testSerialization() {
        ScatterRenderer r1 = new ScatterRenderer();
        ScatterRenderer r2 = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(r1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            r2 = (ScatterRenderer) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(r1, r2);
    }

    
    public void testFindRangeBounds() {
        ScatterRenderer r = new ScatterRenderer();
        assertNull(r.findRangeBounds(null));

        
        DefaultMultiValueCategoryDataset dataset
                = new DefaultMultiValueCategoryDataset();
        assertNull(r.findRangeBounds(dataset));

        List values = Arrays.asList(new Double[] {new Double(1.0)});
        dataset.add(values, "R1", "C1");
        assertEquals(new Range(1.0, 1.0), r.findRangeBounds(dataset));

        values = Arrays.asList(new Double[] {new Double(2.0), new Double(2.2)});
        dataset.add(values, "R1", "C2");
        assertEquals(new Range(1.0, 2.2), r.findRangeBounds(dataset));

        values = Arrays.asList(new Double[] {new Double(-3.0),
                new Double(-3.2)});
        dataset.add(values, "R1", "C3");
        assertEquals(new Range(-3.2, 2.2), r.findRangeBounds(dataset));

        values = Arrays.asList(new Double[] {new Double(6.0)});
        dataset.add(values, "R2", "C1");
        assertEquals(new Range(-3.2, 6.0), r.findRangeBounds(dataset));

        r.setSeriesVisible(1, Boolean.FALSE);
        assertEquals(new Range(-3.2, 2.2), r.findRangeBounds(dataset));
    }


}
