

package org.jfree.chart.renderer.xy.junit;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import java.util.ArrayList;
import java.util.Date;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.renderer.xy.XYBoxAndWhiskerRenderer;
import org.jfree.data.statistics.BoxAndWhiskerItem;
import org.jfree.data.statistics.DefaultBoxAndWhiskerXYDataset;
import org.jfree.util.PublicCloneable;


public class XYBoxAndWhiskerRendererTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(XYBoxAndWhiskerRendererTests.class);
    }

    
    public XYBoxAndWhiskerRendererTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        XYBoxAndWhiskerRenderer r1 = new XYBoxAndWhiskerRenderer();
        XYBoxAndWhiskerRenderer r2 = new XYBoxAndWhiskerRenderer();
        assertEquals(r1, r2);

        r1.setPaint(new GradientPaint(1.0f, 2.0f, Color.yellow,
                3.0f, 4.0f, Color.red));
        assertFalse(r1.equals(r2));
        r2.setPaint(new GradientPaint(1.0f, 2.0f, Color.yellow,
                3.0f, 4.0f, Color.red));
        assertEquals(r1, r2);

        r1.setArtifactPaint(new GradientPaint(1.0f, 2.0f, Color.green,
                3.0f, 4.0f, Color.red));
        assertFalse(r1.equals(r2));
        r2.setArtifactPaint(new GradientPaint(1.0f, 2.0f, Color.green,
                3.0f, 4.0f, Color.red));
        assertEquals(r1, r2);

        r1.setBoxWidth(0.55);
        assertFalse(r1.equals(r2));
        r2.setBoxWidth(0.55);
        assertEquals(r1, r2);

        r1.setFillBox(!r1.getFillBox());
        assertFalse(r1.equals(r2));
        r2.setFillBox(!r2.getFillBox());
        assertEquals(r1, r2);

        r1.setBoxPaint(Color.yellow);
        assertFalse(r1.equals(r2));
        r2.setBoxPaint(Color.yellow);
        assertEquals(r1, r2);

        
        r1.setBoxPaint(null);
        assertFalse(r1.equals(r2));
        r2.setBoxPaint(null);
        assertEquals(r1, r2);
    }

    
    public void testHashcode() {
        XYBoxAndWhiskerRenderer r1 = new XYBoxAndWhiskerRenderer();
        XYBoxAndWhiskerRenderer r2 = new XYBoxAndWhiskerRenderer();
        assertTrue(r1.equals(r2));
        int h1 = r1.hashCode();
        int h2 = r2.hashCode();
        assertEquals(h1, h2);
    }

    
    public void testCloning() {
        XYBoxAndWhiskerRenderer r1 = new XYBoxAndWhiskerRenderer();
        XYBoxAndWhiskerRenderer r2 = null;
        try {
            r2 = (XYBoxAndWhiskerRenderer) r1.clone();
        }
        catch (CloneNotSupportedException e) {
            System.err.println("Failed to clone.");
        }
        assertTrue(r1 != r2);
        assertTrue(r1.getClass() == r2.getClass());
        assertTrue(r1.equals(r2));
    }

    
    public void testPublicCloneable() {
        XYBoxAndWhiskerRenderer r1 = new XYBoxAndWhiskerRenderer();
        assertTrue(r1 instanceof PublicCloneable);
    }

    
    public void testSerialization() {
        XYBoxAndWhiskerRenderer r1 = new XYBoxAndWhiskerRenderer();
        XYBoxAndWhiskerRenderer r2 = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(r1);
            out.close();
            ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                    buffer.toByteArray()));
            r2 = (XYBoxAndWhiskerRenderer) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(r1, r2);
    }

    
    public void test2909215() {
        DefaultBoxAndWhiskerXYDataset d1 = new DefaultBoxAndWhiskerXYDataset(
                "Series");
        d1.add(new Date(1L), new BoxAndWhiskerItem(new Double(1.0),
                new Double(2.0), new Double(3.0), new Double(4.0),
                new Double(5.0), new Double(6.0), null, null, null));
        JFreeChart chart = ChartFactory.createBoxAndWhiskerChart("Title", "X",
                "Y", d1, true);
        try {
            BufferedImage image = new BufferedImage(400, 200,
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = image.createGraphics();
            chart.draw(g2, new Rectangle2D.Double(0, 0, 400, 200), null, null);
            g2.dispose();
        }
        catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
        assertTrue(true);
    }

}

