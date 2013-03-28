

package org.jfree.chart.labels.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.labels.BoxAndWhiskerToolTipGenerator;


public class BoxAndWhiskerToolTipGeneratorTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(BoxAndWhiskerToolTipGeneratorTests.class);
    }

    
    public BoxAndWhiskerToolTipGeneratorTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        
        
        BoxAndWhiskerToolTipGenerator g1 = new BoxAndWhiskerToolTipGenerator();
        BoxAndWhiskerToolTipGenerator g2 = new BoxAndWhiskerToolTipGenerator();
        assertTrue(g1.equals(g2));
        assertTrue(g2.equals(g1));
        
        
        g1 = new BoxAndWhiskerToolTipGenerator("{0} --> {1} {2}", 
        		new DecimalFormat("0.0"));
        g2 = new BoxAndWhiskerToolTipGenerator("{1} {2}", 
        		new DecimalFormat("0.0"));
        assertFalse(g1.equals(g2));
        g2 = new BoxAndWhiskerToolTipGenerator("{0} --> {1} {2}", 
        		new DecimalFormat("0.0"));
        assertTrue(g1.equals(g2));

        
        g1 = new BoxAndWhiskerToolTipGenerator("{0} --> {1} {2}", 
        		new DecimalFormat("0.0"));
        g2 = new BoxAndWhiskerToolTipGenerator("{0} --> {1} {2}", 
        		new DecimalFormat("0.00"));
        assertFalse(g1.equals(g2));
    }

    
    public void testHashCode() {
        BoxAndWhiskerToolTipGenerator g1 = new BoxAndWhiskerToolTipGenerator();
        BoxAndWhiskerToolTipGenerator g2 = new BoxAndWhiskerToolTipGenerator();
        assertTrue(g1.equals(g2));
        assertTrue(g1.hashCode() == g2.hashCode());
    }

    
    public void testCloning() {
        BoxAndWhiskerToolTipGenerator g1 = new BoxAndWhiskerToolTipGenerator();
        BoxAndWhiskerToolTipGenerator g2 = null;
        try {
            g2 = (BoxAndWhiskerToolTipGenerator) g1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(g1 != g2);
        assertTrue(g1.getClass() == g2.getClass());
        assertTrue(g1.equals(g2));
    }

    
    public void testSerialization() {

        BoxAndWhiskerToolTipGenerator g1 = new BoxAndWhiskerToolTipGenerator();
        BoxAndWhiskerToolTipGenerator g2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(g1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            g2 = (BoxAndWhiskerToolTipGenerator) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(g1, g2);

    }

}
