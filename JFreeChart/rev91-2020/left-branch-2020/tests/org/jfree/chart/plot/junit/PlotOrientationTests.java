

package org.jfree.chart.plot.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.plot.PlotOrientation;


public class PlotOrientationTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(PlotOrientationTests.class);
    }

    
    public PlotOrientationTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        assertEquals(PlotOrientation.HORIZONTAL, PlotOrientation.HORIZONTAL);
        assertEquals(PlotOrientation.VERTICAL, PlotOrientation.VERTICAL);
        assertFalse(
            PlotOrientation.HORIZONTAL.equals(PlotOrientation.VERTICAL)
        );
        assertFalse(
            PlotOrientation.VERTICAL.equals(PlotOrientation.HORIZONTAL)
        );
    }

    
    public void testSerialization() {

        PlotOrientation orientation1 = PlotOrientation.HORIZONTAL;
        PlotOrientation orientation2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(orientation1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            orientation2 = (PlotOrientation) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(orientation1, orientation2);
        boolean same = orientation1 == orientation2;
        assertEquals(true, same);
    }

}
