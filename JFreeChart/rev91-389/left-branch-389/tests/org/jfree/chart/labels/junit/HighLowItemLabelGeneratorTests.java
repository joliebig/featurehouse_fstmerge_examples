

package org.jfree.chart.labels.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.labels.HighLowItemLabelGenerator;


public class HighLowItemLabelGeneratorTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(HighLowItemLabelGeneratorTests.class);
    }

    
    public HighLowItemLabelGeneratorTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        HighLowItemLabelGenerator g1 = new HighLowItemLabelGenerator();
        HighLowItemLabelGenerator g2 = new HighLowItemLabelGenerator();
        assertTrue(g1.equals(g2));
        assertTrue(g2.equals(g1));
        
        g1 = new HighLowItemLabelGenerator(
            new SimpleDateFormat("d-MMM-yyyy"), NumberFormat.getInstance()
        );
        assertFalse(g1.equals(g2));
        g2 = new HighLowItemLabelGenerator(
            new SimpleDateFormat("d-MMM-yyyy"), NumberFormat.getInstance()
        );
        assertTrue(g1.equals(g2));
        
        g1 = new HighLowItemLabelGenerator(
            new SimpleDateFormat("d-MMM-yyyy"), new DecimalFormat("0.000")
        );
        assertFalse(g1.equals(g2));
        g2 = new HighLowItemLabelGenerator(
            new SimpleDateFormat("d-MMM-yyyy"), new DecimalFormat("0.000")
        );
        assertTrue(g1.equals(g2));
            
    }
    
    
    
    public void testCloning() {
        HighLowItemLabelGenerator g1 = new HighLowItemLabelGenerator();
        HighLowItemLabelGenerator g2 = null;
        try {
            g2 = (HighLowItemLabelGenerator) g1.clone();
        }
        catch (CloneNotSupportedException e) {
            System.err.println("Failed to clone.");
        }
        assertTrue(g1 != g2);
        assertTrue(g1.getClass() == g2.getClass());
        assertTrue(g1.equals(g2));
    }

    
    public void testSerialization() {

        HighLowItemLabelGenerator g1 = new HighLowItemLabelGenerator();
        HighLowItemLabelGenerator g2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(g1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            g2 = (HighLowItemLabelGenerator) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(g1, g2);

    }

}
