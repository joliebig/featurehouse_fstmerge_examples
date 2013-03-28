

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

import org.jfree.chart.block.FlowArrangement;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.VerticalAlignment;


public class FlowArrangementTests extends TestCase {
    
    
    public static Test suite() {
        return new TestSuite(FlowArrangementTests.class);
    }

    
    public FlowArrangementTests(String name) {
        super(name);
    }
    
    
    public void testEquals() {
        FlowArrangement f1 = new FlowArrangement(
            HorizontalAlignment.LEFT, VerticalAlignment.TOP, 1.0, 2.0
        );
        FlowArrangement f2 = new FlowArrangement(
            HorizontalAlignment.LEFT, VerticalAlignment.TOP, 1.0, 2.0
        );
        assertTrue(f1.equals(f2));
        assertTrue(f2.equals(f1));

        f1 = new FlowArrangement(
            HorizontalAlignment.RIGHT, VerticalAlignment.TOP, 1.0, 2.0
        );
        assertFalse(f1.equals(f2));
        f2 = new FlowArrangement(
            HorizontalAlignment.RIGHT, VerticalAlignment.TOP, 1.0, 2.0
        );
        assertTrue(f1.equals(f2));

        f1 = new FlowArrangement(
            HorizontalAlignment.RIGHT, VerticalAlignment.BOTTOM, 1.0, 2.0
        );
        assertFalse(f1.equals(f2));
        f2 = new FlowArrangement(
            HorizontalAlignment.RIGHT, VerticalAlignment.BOTTOM, 1.0, 2.0
        );
        assertTrue(f1.equals(f2));
    
        f1 = new FlowArrangement(
            HorizontalAlignment.RIGHT, VerticalAlignment.BOTTOM, 1.1, 2.0
        );
        assertFalse(f1.equals(f2));
        f2 = new FlowArrangement(
            HorizontalAlignment.RIGHT, VerticalAlignment.BOTTOM, 1.1, 2.0
        );
        assertTrue(f1.equals(f2));
        
        f1 = new FlowArrangement(
            HorizontalAlignment.RIGHT, VerticalAlignment.BOTTOM, 1.1, 2.2
        );
        assertFalse(f1.equals(f2));
        f2 = new FlowArrangement(
            HorizontalAlignment.RIGHT, VerticalAlignment.BOTTOM, 1.1, 2.2
        );
        assertTrue(f1.equals(f2));
        
    }

    
    public void testCloning() {
        FlowArrangement f1 = new FlowArrangement();
        assertFalse(f1 instanceof Cloneable);
    }

    
    public void testSerialization() {
        FlowArrangement f1 = new FlowArrangement(
            HorizontalAlignment.LEFT, VerticalAlignment.TOP, 1.0, 2.0
        );
        FlowArrangement f2 = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(f1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            f2 = (FlowArrangement) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(f1, f2);
    }
   
}
