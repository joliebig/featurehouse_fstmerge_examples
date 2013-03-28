

package org.jfree.chart.block.junit;

import java.awt.Color;
import java.awt.Font;
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

import org.jfree.chart.block.LabelBlock;


public class LabelBlockTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(LabelBlockTests.class);
    }

    
    public LabelBlockTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        LabelBlock b1 = new LabelBlock("ABC", new Font("Dialog",
                Font.PLAIN, 12), Color.red);
        LabelBlock b2 = new LabelBlock("ABC", new Font("Dialog",
                Font.PLAIN, 12), Color.red);
        assertTrue(b1.equals(b2));
        assertTrue(b2.equals(b2));

        b1 = new LabelBlock("XYZ", new Font("Dialog", Font.PLAIN, 12),
                Color.red);
        assertFalse(b1.equals(b2));
        b2 = new LabelBlock("XYZ", new Font("Dialog", Font.PLAIN, 12),
                Color.red);
        assertTrue(b1.equals(b2));

        b1 = new LabelBlock("XYZ", new Font("Dialog", Font.BOLD, 12),
                Color.red);
        assertFalse(b1.equals(b2));
        b2 = new LabelBlock("XYZ", new Font("Dialog", Font.BOLD, 12),
                Color.red);
        assertTrue(b1.equals(b2));

        b1 = new LabelBlock("XYZ", new Font("Dialog", Font.BOLD, 12),
                Color.blue);
        assertFalse(b1.equals(b2));
        b2 = new LabelBlock("XYZ", new Font("Dialog", Font.BOLD, 12),
                Color.blue);
        assertTrue(b1.equals(b2));

        b1.setToolTipText("Tooltip");
        assertFalse(b1.equals(b2));
        b2.setToolTipText("Tooltip");
        assertTrue(b1.equals(b2));

        b1.setURLText("URL");
        assertFalse(b1.equals(b2));
        b2.setURLText("URL");
        assertTrue(b1.equals(b2));
    }

    
    public void testCloning() {
        LabelBlock b1 = new LabelBlock("ABC", new Font("Dialog",
                Font.PLAIN, 12), Color.red);
        LabelBlock b2 = null;

        try {
            b2 = (LabelBlock) b1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(b1 != b2);
        assertTrue(b1.getClass() == b2.getClass());
        assertTrue(b1.equals(b2));
    }

    
    public void testSerialization() {
        GradientPaint gp = new GradientPaint(1.0f, 2.0f, Color.red, 3.0f, 4.0f,
                Color.blue);
        LabelBlock b1 = new LabelBlock("ABC", new Font("Dialog",
                Font.PLAIN, 12), gp);
        LabelBlock b2 = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(b1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            b2 = (LabelBlock) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(b1, b2);
    }

}
