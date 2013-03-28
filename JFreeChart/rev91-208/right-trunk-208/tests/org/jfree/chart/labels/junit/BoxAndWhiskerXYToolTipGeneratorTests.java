

package org.jfree.chart.labels.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.labels.BoxAndWhiskerXYToolTipGenerator;


public class BoxAndWhiskerXYToolTipGeneratorTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(BoxAndWhiskerXYToolTipGeneratorTests.class);
    }

    
    public BoxAndWhiskerXYToolTipGeneratorTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        
        
        BoxAndWhiskerXYToolTipGenerator g1 
            = new BoxAndWhiskerXYToolTipGenerator();
        BoxAndWhiskerXYToolTipGenerator g2 
            = new BoxAndWhiskerXYToolTipGenerator();
        assertTrue(g1.equals(g2));
        assertTrue(g2.equals(g1));
        
        
        g1 = new BoxAndWhiskerXYToolTipGenerator(
            "{0} --> {1} {2}", 
            new SimpleDateFormat("yyyy"), new DecimalFormat("0.0") 
        );
        g2 = new BoxAndWhiskerXYToolTipGenerator(
            "{1} {2}", new SimpleDateFormat("yyyy"), new DecimalFormat("0.0")
        );
        assertFalse(g1.equals(g2));
        g2 = new BoxAndWhiskerXYToolTipGenerator(
            "{0} --> {1} {2}", 
            new SimpleDateFormat("yyyy"), new DecimalFormat("0.0")
        );
        assertTrue(g1.equals(g2));

        
        g1 = new BoxAndWhiskerXYToolTipGenerator(
            "{0} --> {1} {2}", 
            new SimpleDateFormat("yyyy"), new DecimalFormat("0.0")
        );
        g2 = new BoxAndWhiskerXYToolTipGenerator(
            "{0} --> {1} {2}", 
            new SimpleDateFormat("MMM-yyyy"), new DecimalFormat("0.0")
        );
        assertFalse(g1.equals(g2));
        g2 = new BoxAndWhiskerXYToolTipGenerator(
            "{0} --> {1} {2}", 
            new SimpleDateFormat("yyyy"), new DecimalFormat("0.0")
        );
        assertTrue(g1.equals(g2));

        
        g1 = new BoxAndWhiskerXYToolTipGenerator(
            "{0} --> {1} {2}", 
            new SimpleDateFormat("yyyy"), new DecimalFormat("0.0")
        );
        g2 = new BoxAndWhiskerXYToolTipGenerator(
            "{0} --> {1} {2}", 
            new SimpleDateFormat("yyyy"), new DecimalFormat("0.00")
        );
        assertFalse(g1.equals(g2));
        g2 = new BoxAndWhiskerXYToolTipGenerator(
            "{0} --> {1} {2}", 
            new SimpleDateFormat("yyyy"), new DecimalFormat("0.0")
        );
        assertTrue(g1.equals(g2));
    }

    
    public void testCloning() {
        BoxAndWhiskerXYToolTipGenerator g1 
            = new BoxAndWhiskerXYToolTipGenerator();
        BoxAndWhiskerXYToolTipGenerator g2 = null;
        try {
            g2 = (BoxAndWhiskerXYToolTipGenerator) g1.clone();
        }
        catch (CloneNotSupportedException e) {
            System.err.println("Failed to clone.");
        }
        assertTrue(g1 != g2);
        assertTrue(g1.getClass() == g2.getClass());
        assertTrue(g1.equals(g2));
    }

    
    public void testSerialization() {

        BoxAndWhiskerXYToolTipGenerator g1 
            = new BoxAndWhiskerXYToolTipGenerator();
        BoxAndWhiskerXYToolTipGenerator g2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(g1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            g2 = (BoxAndWhiskerXYToolTipGenerator) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(g1, g2);

    }

}
