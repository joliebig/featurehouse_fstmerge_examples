

package org.jfree.chart.panel.junit;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.panel.CrosshairOverlay;
import org.jfree.chart.plot.Crosshair;


public class CrosshairOverlayTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(CrosshairOverlayTests.class);
    }

    
    public CrosshairOverlayTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        CrosshairOverlay o1 = new CrosshairOverlay();
        CrosshairOverlay o2 = new CrosshairOverlay();
        assertEquals(o1, o2);
    }

    
    public void testSerialization() {
        CrosshairOverlay o1 = new CrosshairOverlay();
        o1.addDomainCrosshair(new Crosshair(99.9));
        o1.addRangeCrosshair(new Crosshair(1.23, new GradientPaint(1.0f, 2.0f,
                Color.red, 3.0f, 4.0f, Color.blue), new BasicStroke(1.1f)));
        CrosshairOverlay o2 = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(o1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            o2 = (CrosshairOverlay) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(o1.equals(o2));
    }

    
    public void testCloning() {
        CrosshairOverlay o1 = new CrosshairOverlay();
        o1.addDomainCrosshair(new Crosshair(99.9));
        o1.addRangeCrosshair(new Crosshair(1.23, new GradientPaint(1.0f, 2.0f,
                Color.red, 3.0f, 4.0f, Color.blue), new BasicStroke(1.1f)));
        CrosshairOverlay o2 = null;
        try {
            o2 = (CrosshairOverlay) o1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(o1 != o2);
        assertTrue(o1.getClass() == o2.getClass());
        assertTrue(o1.equals(o2));

        o1.addDomainCrosshair(new Crosshair(3.21));
        o1.addRangeCrosshair(new Crosshair(4.32));
        assertFalse(o1.equals(o2));
    }

}
