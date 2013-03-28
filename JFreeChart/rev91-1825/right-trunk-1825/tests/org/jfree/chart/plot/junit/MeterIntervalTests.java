

package org.jfree.chart.plot.junit;

import java.awt.BasicStroke;
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

import org.jfree.chart.plot.MeterInterval;
import org.jfree.data.Range;


public class MeterIntervalTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(MeterIntervalTests.class);
    }

    
    public MeterIntervalTests(String name) {
        super(name);
    }

    
    public void testEquals() {

        MeterInterval m1 = new MeterInterval(
            "Label 1", new Range(1.2, 3.4), Color.red, new BasicStroke(1.0f),
            Color.blue
        );
        MeterInterval m2 = new MeterInterval(
            "Label 1", new Range(1.2, 3.4), Color.red, new BasicStroke(1.0f),
            Color.blue
        );
        assertTrue(m1.equals(m2));
        assertTrue(m2.equals(m1));

        m1 = new MeterInterval(
            "Label 2", new Range(1.2, 3.4), Color.red, new BasicStroke(1.0f),
            Color.blue
        );
        assertFalse(m1.equals(m2));
        m2 = new MeterInterval(
            "Label 2", new Range(1.2, 3.4), Color.red, new BasicStroke(1.0f),
            Color.blue
        );
        assertTrue(m1.equals(m2));

    }

    
    public void testCloning() {
        MeterInterval m1 = new MeterInterval("X", new Range(1.0, 2.0));
        assertFalse(m1 instanceof Cloneable);
    }

   
    public void testSerialization() {

        MeterInterval m1 = new MeterInterval("X", new Range(1.0, 2.0));
        MeterInterval m2 = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(m1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            m2 = (MeterInterval) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        boolean b = m1.equals(m2);
        assertTrue(b);

    }

}
