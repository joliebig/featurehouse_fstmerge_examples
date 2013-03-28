

package org.jfree.chart.labels.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.labels.StandardCategorySeriesLabelGenerator;
import org.jfree.data.category.DefaultCategoryDataset;


public class StandardCategorySeriesLabelGeneratorTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(StandardCategorySeriesLabelGeneratorTests.class);
    }

    
    public StandardCategorySeriesLabelGeneratorTests(String name) {
        super(name);
    }
    
    
    public void testGenerateLabel() {
        StandardCategorySeriesLabelGenerator g 
                = new StandardCategorySeriesLabelGenerator("{0}");
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(1.0, "R0", "C0");
        dataset.addValue(2.0, "R0", "C1");
        dataset.addValue(3.0, "R1", "C0");
        dataset.addValue(null, "R1", "C1");
        String s = g.generateLabel(dataset, 0);
        assertEquals("R0", s);
    }
    
    
    public void testEquals() {
        StandardCategorySeriesLabelGenerator g1 
                = new StandardCategorySeriesLabelGenerator();
        StandardCategorySeriesLabelGenerator g2 
                = new StandardCategorySeriesLabelGenerator();
        assertTrue(g1.equals(g2));
        assertTrue(g2.equals(g1));
        
        g1 = new StandardCategorySeriesLabelGenerator("{1}");
        assertFalse(g1.equals(g2));
        g2 = new StandardCategorySeriesLabelGenerator("{1}");
        assertTrue(g1.equals(g2));        
    }

    
    public void testHashCode() {
    	StandardCategorySeriesLabelGenerator g1 
    	        = new StandardCategorySeriesLabelGenerator();
    	StandardCategorySeriesLabelGenerator g2 
    	        = new StandardCategorySeriesLabelGenerator();
        assertTrue(g1.equals(g2));
        assertTrue(g1.hashCode() == g2.hashCode());
    }

    
    public void testCloning() {
        StandardCategorySeriesLabelGenerator g1 
                = new StandardCategorySeriesLabelGenerator("{1}");
        StandardCategorySeriesLabelGenerator g2 = null;
        try {
            g2 = (StandardCategorySeriesLabelGenerator) g1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(g1 != g2);
        assertTrue(g1.getClass() == g2.getClass());
        assertTrue(g1.equals(g2));
    }

    
    public void testSerialization() {

        StandardCategorySeriesLabelGenerator g1
                = new StandardCategorySeriesLabelGenerator("{2}");
        StandardCategorySeriesLabelGenerator g2 = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(g1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            g2 = (StandardCategorySeriesLabelGenerator) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(g1, g2);
    }

}
