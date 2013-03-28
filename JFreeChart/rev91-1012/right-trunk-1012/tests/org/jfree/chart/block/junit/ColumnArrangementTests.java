

package org.jfree.chart.block.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.block.ColumnArrangement;
import org.jfree.chart.block.FlowArrangement;
import org.jfree.chart.util.HorizontalAlignment;
import org.jfree.chart.util.VerticalAlignment;


public class ColumnArrangementTests extends TestCase {
    
    
    public static Test suite() {
        return new TestSuite(ColumnArrangementTests.class);
    }

    
    public ColumnArrangementTests(String name) {
        super(name);
    }
    
    
    public void testEquals() {
        ColumnArrangement c1 = new ColumnArrangement(HorizontalAlignment.LEFT, 
                VerticalAlignment.TOP, 1.0, 2.0);
        ColumnArrangement c2 = new ColumnArrangement(HorizontalAlignment.LEFT, 
                VerticalAlignment.TOP, 1.0, 2.0);
        assertTrue(c1.equals(c2));
        assertTrue(c2.equals(c1));

        c1 = new ColumnArrangement(HorizontalAlignment.RIGHT, 
                VerticalAlignment.TOP, 1.0, 2.0);
        assertFalse(c1.equals(c2));
        c2 = new ColumnArrangement(HorizontalAlignment.RIGHT,
                VerticalAlignment.TOP, 1.0, 2.0);
        assertTrue(c1.equals(c2));

        c1 = new ColumnArrangement(HorizontalAlignment.RIGHT, 
                VerticalAlignment.BOTTOM, 1.0, 2.0);
        assertFalse(c1.equals(c2));
        c2 = new ColumnArrangement(HorizontalAlignment.RIGHT, 
                VerticalAlignment.BOTTOM, 1.0, 2.0);
        assertTrue(c1.equals(c2));
    
        c1 = new ColumnArrangement(HorizontalAlignment.RIGHT, 
                VerticalAlignment.BOTTOM, 1.1, 2.0);
        assertFalse(c1.equals(c2));
        c2 = new ColumnArrangement(HorizontalAlignment.RIGHT, 
                VerticalAlignment.BOTTOM, 1.1, 2.0);
        assertTrue(c1.equals(c2));
        
        c1 = new ColumnArrangement(HorizontalAlignment.RIGHT, 
                VerticalAlignment.BOTTOM, 1.1, 2.2);
        assertFalse(c1.equals(c2));
        c2 = new ColumnArrangement(HorizontalAlignment.RIGHT, 
                VerticalAlignment.BOTTOM, 1.1, 2.2);
        assertTrue(c1.equals(c2));
    }

    
    public void testCloning() {
        FlowArrangement f1 = new FlowArrangement();
        assertFalse(f1 instanceof Cloneable);
    }

    
    public void testSerialization() {
        FlowArrangement f1 = new FlowArrangement(HorizontalAlignment.LEFT, 
                VerticalAlignment.TOP, 1.0, 2.0);
        FlowArrangement f2 = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(f1);
            out.close();

            ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                    buffer.toByteArray()));
            f2 = (FlowArrangement) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(f1, f2);
    }
   
}
