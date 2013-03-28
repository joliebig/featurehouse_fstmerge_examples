

package org.jfree.chart.labels.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.labels.IntervalCategoryItemLabelGenerator;


public class IntervalCategoryLabelGeneratorTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(IntervalCategoryLabelGeneratorTests.class);
    }

    
    public IntervalCategoryLabelGeneratorTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        
        IntervalCategoryItemLabelGenerator g1 
            = new IntervalCategoryItemLabelGenerator();
        IntervalCategoryItemLabelGenerator g2 
            = new IntervalCategoryItemLabelGenerator();
        assertTrue(g1.equals(g2));
        assertTrue(g2.equals(g1));
        
        g1 = new IntervalCategoryItemLabelGenerator(
            "{3} - {4}", new DecimalFormat("0.000")
        );
        assertFalse(g1.equals(g2));
        g2 = new IntervalCategoryItemLabelGenerator(
            "{3} - {4}", new DecimalFormat("0.000")
        );
        assertTrue(g1.equals(g2));
        
        g1 = new IntervalCategoryItemLabelGenerator(
            "{3} - {4}", new SimpleDateFormat("d-MMM")
        );
        assertFalse(g1.equals(g2));
        g2 = new IntervalCategoryItemLabelGenerator(
            "{3} - {4}", new SimpleDateFormat("d-MMM")
        );
        assertTrue(g1.equals(g2));
        
    }

    
    public void testCloning() {
        IntervalCategoryItemLabelGenerator g1 
            = new IntervalCategoryItemLabelGenerator();
        IntervalCategoryItemLabelGenerator g2 = null;
        try {
            g2 = (IntervalCategoryItemLabelGenerator) g1.clone();
        }
        catch (CloneNotSupportedException e) {
            System.err.println("Failed to clone.");
        }
        assertTrue(g1 != g2);
        assertTrue(g1.getClass() == g2.getClass());
        assertTrue(g1.equals(g2));
    }

    
    public void testSerialization() {

        IntervalCategoryItemLabelGenerator g1 
            = new IntervalCategoryItemLabelGenerator(
                "{3} - {4}", DateFormat.getInstance()
            );
        IntervalCategoryItemLabelGenerator g2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(g1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            g2 = (IntervalCategoryItemLabelGenerator) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(g1, g2);

    }

}
