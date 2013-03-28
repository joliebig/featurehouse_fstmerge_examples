

package org.jfree.chart.renderer.xy.junit;

import java.awt.geom.Rectangle2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.renderer.xy.XYSplineAndShapeRenderer;


public class XYSplineAndShapeRendererTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(XYSplineAndShapeRendererTests.class);
    }

    
    public XYSplineAndShapeRendererTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        
        XYSplineAndShapeRenderer r1 = new XYSplineAndShapeRenderer();
        XYSplineAndShapeRenderer r2 = new XYSplineAndShapeRenderer();
        assertEquals(r1, r2);
        assertEquals(r2, r1);
    
        r1.setPrecision(9);
        assertFalse(r1.equals(r2));
        r2.setPrecision(9);
        assertTrue(r1.equals(r2));
    }

    
    public void testHashcode() {
        XYSplineAndShapeRenderer r1 = new XYSplineAndShapeRenderer();
        XYSplineAndShapeRenderer r2 = new XYSplineAndShapeRenderer();
        assertTrue(r1.equals(r2));
        int h1 = r1.hashCode();
        int h2 = r2.hashCode();
        assertEquals(h1, h2);
    }
    
    
    public void testCloning() {
        Rectangle2D legendShape = new Rectangle2D.Double(1.0, 2.0, 3.0, 4.0);
        XYSplineAndShapeRenderer r1 = new XYSplineAndShapeRenderer();
        r1.setLegendLine(legendShape);
        XYSplineAndShapeRenderer r2 = null;
        try {
            r2 = (XYSplineAndShapeRenderer) r1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(r1 != r2);
        assertTrue(r1.getClass() == r2.getClass());
        assertTrue(r1.equals(r2));
    }

    
    public void testSerialization() {

        XYSplineAndShapeRenderer r1 = new XYSplineAndShapeRenderer();
        XYSplineAndShapeRenderer r2 = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(r1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            r2 = (XYSplineAndShapeRenderer) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(r1, r2);

    }

}
