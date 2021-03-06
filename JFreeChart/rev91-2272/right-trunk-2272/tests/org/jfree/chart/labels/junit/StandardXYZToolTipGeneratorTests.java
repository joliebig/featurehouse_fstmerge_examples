

package org.jfree.chart.labels.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.labels.StandardXYZToolTipGenerator;
import org.jfree.chart.util.PublicCloneable;


public class StandardXYZToolTipGeneratorTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(StandardXYZToolTipGeneratorTests.class);
    }

    
    public StandardXYZToolTipGeneratorTests(String name) {
        super(name);
    }

    
    public void testEquals() {

        
        String f1 = "{1}";
        String f2 = "{2}";
        NumberFormat xnf1 = new DecimalFormat("0.00");
        NumberFormat xnf2 = new DecimalFormat("0.000");
        NumberFormat ynf1 = new DecimalFormat("0.00");
        NumberFormat ynf2 = new DecimalFormat("0.000");
        NumberFormat znf1 = new DecimalFormat("0.00");
        NumberFormat znf2 = new DecimalFormat("0.000");

        DateFormat xdf1 = new SimpleDateFormat("d-MMM");
        DateFormat xdf2 = new SimpleDateFormat("d-MMM-yyyy");
        DateFormat ydf1 = new SimpleDateFormat("d-MMM");
        DateFormat ydf2 = new SimpleDateFormat("d-MMM-yyyy");
        DateFormat zdf1 = new SimpleDateFormat("d-MMM");
        DateFormat zdf2 = new SimpleDateFormat("d-MMM-yyyy");

        StandardXYZToolTipGenerator g1 = null;
        StandardXYZToolTipGenerator g2 = null;

        g1 = new StandardXYZToolTipGenerator(f1, xnf1, ynf1, znf1);
        g2 = new StandardXYZToolTipGenerator(f1, xnf1, ynf1, znf1);
        assertTrue(g1.equals(g2));

        
        g1 = new StandardXYZToolTipGenerator(f2, xnf1, ynf1, znf1);
        assertFalse(g1.equals(g2));
        g2 = new StandardXYZToolTipGenerator(f2, xnf1, ynf1, znf1);
        assertTrue(g1.equals(g2));

        
        g1 = new StandardXYZToolTipGenerator(f2, xnf2, ynf1, znf1);
        assertFalse(g1.equals(g2));
        g2 = new StandardXYZToolTipGenerator(f2, xnf2, ynf1, znf1);
        assertTrue(g1.equals(g2));

        
        g1 = new StandardXYZToolTipGenerator(f2, xnf2, ynf2, znf1);
        assertFalse(g1.equals(g2));
        g2 = new StandardXYZToolTipGenerator(f2, xnf2, ynf2, znf1);
        assertTrue(g1.equals(g2));

        
        g1 = new StandardXYZToolTipGenerator(f2, xnf2, ynf2, znf2);
        assertFalse(g1.equals(g2));
        g2 = new StandardXYZToolTipGenerator(f2, xnf2, ynf2, znf2);
        assertTrue(g1.equals(g2));

        g1 = new StandardXYZToolTipGenerator(f2, xdf1, ydf1, zdf1);
        g2 = new StandardXYZToolTipGenerator(f2, xdf1, ydf1, zdf1);
        assertTrue(g1.equals(g2));

        
        g1 = new StandardXYZToolTipGenerator(f2, xdf2, ydf1, zdf1);
        assertFalse(g1.equals(g2));
        g2 = new StandardXYZToolTipGenerator(f2, xdf2, ydf1, zdf1);
        assertTrue(g1.equals(g2));

        
        g1 = new StandardXYZToolTipGenerator(f2, xdf2, ydf2, zdf1);
        assertFalse(g1.equals(g2));
        g2 = new StandardXYZToolTipGenerator(f2, xdf2, ydf2, zdf1);
        assertTrue(g1.equals(g2));

        
        g1 = new StandardXYZToolTipGenerator(f2, xdf2, ydf2, zdf2);
        assertFalse(g1.equals(g2));
        g2 = new StandardXYZToolTipGenerator(f2, xdf2, ydf2, zdf2);
        assertTrue(g1.equals(g2));

    }

    
    public void testHashCode() {
        StandardXYZToolTipGenerator g1
                = new StandardXYZToolTipGenerator();
        StandardXYZToolTipGenerator g2
                = new StandardXYZToolTipGenerator();
        assertTrue(g1.equals(g2));
        assertTrue(g1.hashCode() == g2.hashCode());
    }

    
    public void testCloning() {
        StandardXYZToolTipGenerator g1 = new StandardXYZToolTipGenerator();
        StandardXYZToolTipGenerator g2 = null;
        try {
            g2 = (StandardXYZToolTipGenerator) g1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(g1 != g2);
        assertTrue(g1.getClass() == g2.getClass());
        assertTrue(g1.equals(g2));
    }

    
    public void testPublicCloneable() {
        StandardXYZToolTipGenerator g1 = new StandardXYZToolTipGenerator();
        assertTrue(g1 instanceof PublicCloneable);
    }

    
    public void testSerialization() {

        StandardXYZToolTipGenerator g1 = new StandardXYZToolTipGenerator();
        StandardXYZToolTipGenerator g2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(g1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            g2 = (StandardXYZToolTipGenerator) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(g1, g2);

    }

}
