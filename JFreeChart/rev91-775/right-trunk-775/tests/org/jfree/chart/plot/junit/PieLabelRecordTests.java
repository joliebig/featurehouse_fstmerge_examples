

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

import org.jfree.chart.plot.CategoryMarker;
import org.jfree.chart.plot.PieLabelRecord;
import org.jfree.chart.text.TextBox;


public class PieLabelRecordTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(PieLabelRecordTests.class);
    }

    
    public PieLabelRecordTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        PieLabelRecord p1 = new PieLabelRecord("A", 1.0, 2.0, new TextBox("B"), 
                3.0, 4.0, 5.0);
        PieLabelRecord p2 = new PieLabelRecord("A", 1.0, 2.0, new TextBox("B"), 
                3.0, 4.0, 5.0);
        assertTrue(p1.equals(p2));
        assertTrue(p2.equals(p1));
        
        p1 = new PieLabelRecord("B", 1.0, 2.0, new TextBox("B"), 3.0, 4.0, 5.0);
        assertFalse(p1.equals(p2));
        p2 = new PieLabelRecord("B", 1.0, 2.0, new TextBox("B"), 3.0, 4.0, 5.0);
        assertTrue(p1.equals(p2));

        p1 = new PieLabelRecord("B", 1.1, 2.0, new TextBox("B"), 3.0, 4.0, 5.0);
        assertFalse(p1.equals(p2));
        p2 = new PieLabelRecord("B", 1.1, 2.0, new TextBox("B"), 3.0, 4.0, 5.0);
        assertTrue(p1.equals(p2));

        p1 = new PieLabelRecord("B", 1.1, 2.2, new TextBox("B"), 3.0, 4.0, 5.0);
        assertFalse(p1.equals(p2));
        p2 = new PieLabelRecord("B", 1.1, 2.2, new TextBox("B"), 3.0, 4.0, 5.0);
        assertTrue(p1.equals(p2));

        p1 = new PieLabelRecord("B", 1.1, 2.2, new TextBox("C"), 3.0, 4.0, 5.0);
        assertFalse(p1.equals(p2));
        p2 = new PieLabelRecord("B", 1.1, 2.2, new TextBox("C"), 3.0, 4.0, 5.0);
        assertTrue(p1.equals(p2));

        p1 = new PieLabelRecord("B", 1.1, 2.2, new TextBox("C"), 3.3, 4.0, 5.0);
        assertFalse(p1.equals(p2));
        p2 = new PieLabelRecord("B", 1.1, 2.2, new TextBox("C"), 3.3, 4.0, 5.0);
        assertTrue(p1.equals(p2));

        p1 = new PieLabelRecord("B", 1.1, 2.2, new TextBox("C"), 3.3, 4.4, 5.0);
        assertFalse(p1.equals(p2));
        p2 = new PieLabelRecord("B", 1.1, 2.2, new TextBox("C"), 3.3, 4.4, 5.0);
        assertTrue(p1.equals(p2));

        p1 = new PieLabelRecord("B", 1.1, 2.2, new TextBox("C"), 3.3, 4.4, 5.5);
        assertFalse(p1.equals(p2));
        p2 = new PieLabelRecord("B", 1.1, 2.2, new TextBox("C"), 3.3, 4.4, 5.5);
        assertTrue(p1.equals(p2));
    
    }
        
    
    public void testCloning() {
        PieLabelRecord p1 = new PieLabelRecord("A", 1.0, 2.0, new TextBox("B"), 
                3.0, 4.0, 5.0);
        assertFalse(p1 instanceof Cloneable);
    }

   
    public void testSerialization() {

        PieLabelRecord p1 = new PieLabelRecord("A", 1.0, 2.0, new TextBox("B"), 
                3.0, 4.0, 5.0);
        PieLabelRecord p2 = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(p1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            p2 = (PieLabelRecord) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        boolean b = p1.equals(p2);
        assertTrue(b);

    }
}
