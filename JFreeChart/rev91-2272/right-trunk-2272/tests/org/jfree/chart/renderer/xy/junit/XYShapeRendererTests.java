

package org.jfree.chart.renderer.xy.junit;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.renderer.LookupPaintScale;
import org.jfree.chart.renderer.xy.XYShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.DefaultXYZDataset;
import org.jfree.data.xy.XYZDataset;


public class XYShapeRendererTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(XYShapeRendererTests.class);
    }

    
    public XYShapeRendererTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        XYShapeRenderer r1 = new XYShapeRenderer();
        XYShapeRenderer r2 = new XYShapeRenderer();
        assertTrue(r1.equals(r2));
        assertTrue(r2.equals(r1));

        r1.setPaintScale(new LookupPaintScale(1.0, 2.0, Color.white));
        assertFalse(r1.equals(r2));
        r2.setPaintScale(new LookupPaintScale(1.0, 2.0, Color.white));
        assertTrue(r1.equals(r2));

        r1.setDrawOutlines(true);
        assertFalse(r1.equals(r2));
        r2.setDrawOutlines(true);
        assertTrue(r1.equals(r2));

        r1.setUseOutlinePaint(false);
        assertFalse(r1.equals(r2));
        r2.setUseOutlinePaint(false);
        assertTrue(r1.equals(r2));

        r1.setUseFillPaint(true);
        assertFalse(r1.equals(r2));
        r2.setUseFillPaint(true);
        assertTrue(r1.equals(r2));

        r1.setGuideLinesVisible(true);
        assertFalse(r1.equals(r2));
        r2.setGuideLinesVisible(true);
        assertTrue(r1.equals(r2));

        r1.setGuideLinePaint(Color.red);
        assertFalse(r1.equals(r2));
        r2.setGuideLinePaint(Color.red);
        assertTrue(r1.equals(r2));

    }

    
    public void testCloning() {
        XYShapeRenderer r1 = new XYShapeRenderer();
        XYShapeRenderer r2 = null;
        try {
            r2 = (XYShapeRenderer) r1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(r1 != r2);
        assertTrue(r1.getClass() == r2.getClass());
        assertTrue(r1.equals(r2));
    }

    
    public void testSerialization() {
        XYShapeRenderer r1 = new XYShapeRenderer();
        XYShapeRenderer r2 = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(r1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            r2 = (XYShapeRenderer) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(r1, r2);
    }

    
    public void testFindZBounds() {
        XYShapeRenderer r = new XYShapeRenderer();
        assertNull(r.findZBounds(null));

        DefaultXYZDataset dataset = new DefaultXYZDataset();
        Range range;

        double data1[][] = { {1,1,1}, {1,1,1}, {1,2,3} };
        dataset.addSeries("series1", data1);
        range = r.findZBounds(dataset);
        assertNotNull(range);
        assertEquals(1d, range.getLowerBound());
        assertEquals(3d, range.getUpperBound());

        double data2[][] = { {1,1,1}, {1,1,1}, {-1,-2,-3} };
        dataset.removeSeries("series1");
        dataset.addSeries("series2", data2);
        range = r.findZBounds(dataset);
        assertNotNull(range);
        assertEquals(-3d, range.getLowerBound());
        assertEquals(-1d, range.getUpperBound());

        double data3[][] = { {1,1,1}, {1,1,1}, {-1.2,2.9,3.8} };
        dataset.removeSeries("series2");
        dataset.addSeries("series3", data3);
        range = r.findZBounds(dataset);
        assertNotNull(range);
        assertEquals(-1.2d, range.getLowerBound());
        assertEquals(3.8d, range.getUpperBound());
    }
}
