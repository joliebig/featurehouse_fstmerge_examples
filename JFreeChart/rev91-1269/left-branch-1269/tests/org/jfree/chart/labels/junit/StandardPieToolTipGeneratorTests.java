

package org.jfree.chart.labels.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.labels.StandardPieToolTipGenerator;
import org.jfree.util.PublicCloneable;


public class StandardPieToolTipGeneratorTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(StandardPieToolTipGeneratorTests.class);
    }

    
    public StandardPieToolTipGeneratorTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        StandardPieToolTipGenerator g1 = new StandardPieToolTipGenerator();
        StandardPieToolTipGenerator g2 = new StandardPieToolTipGenerator();
        assertTrue(g1.equals(g2));
        assertTrue(g2.equals(g1));

        g1 = new StandardPieToolTipGenerator("{0}",
                new DecimalFormat("#,##0.00"),
                NumberFormat.getPercentInstance());
        assertFalse(g1.equals(g2));
        g2 = new StandardPieToolTipGenerator("{0}",
                new DecimalFormat("#,##0.00"),
                NumberFormat.getPercentInstance());
        assertTrue(g1.equals(g2));

        g1 = new StandardPieToolTipGenerator("{0} {1}",
                new DecimalFormat("#,##0.00"),
                NumberFormat.getPercentInstance());
        assertFalse(g1.equals(g2));
        g2 = new StandardPieToolTipGenerator("{0} {1}",
                new DecimalFormat("#,##0.00"),
                NumberFormat.getPercentInstance());
        assertTrue(g1.equals(g2));

        g1 = new StandardPieToolTipGenerator("{0} {1}",
                new DecimalFormat("#,##0"), NumberFormat.getPercentInstance());
        assertFalse(g1.equals(g2));
        g2 = new StandardPieToolTipGenerator("{0} {1}",
                new DecimalFormat("#,##0"), NumberFormat.getPercentInstance());
        assertTrue(g1.equals(g2));

        g1 = new StandardPieToolTipGenerator("{0} {1}",
                new DecimalFormat("#,##0"), new DecimalFormat("0.000%"));
        assertFalse(g1.equals(g2));
        g2 = new StandardPieToolTipGenerator("{0} {1}",
                new DecimalFormat("#,##0"), new DecimalFormat("0.000%"));
        assertTrue(g1.equals(g2));
    }

    
    public void testHashCode() {
        StandardPieToolTipGenerator g1
                = new StandardPieToolTipGenerator();
        StandardPieToolTipGenerator g2
                = new StandardPieToolTipGenerator();
        assertTrue(g1.equals(g2));
        assertTrue(g1.hashCode() == g2.hashCode());
    }

    
    public void testCloning() {
        StandardPieToolTipGenerator g1 = new StandardPieToolTipGenerator();
        StandardPieToolTipGenerator g2 = null;
        try {
            g2 = (StandardPieToolTipGenerator) g1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(g1 != g2);
        assertTrue(g1.getClass() == g2.getClass());
        assertTrue(g1.equals(g2));
        assertTrue(g1.getNumberFormat() != g2.getNumberFormat());
        assertTrue(g1.getPercentFormat() != g2.getPercentFormat());
    }

    
    public void testPublicCloneable() {
        StandardPieToolTipGenerator g1 = new StandardPieToolTipGenerator();
        assertTrue(g1 instanceof PublicCloneable);
    }

    
    public void testSerialization() {

        StandardPieToolTipGenerator g1 = new StandardPieToolTipGenerator();
        StandardPieToolTipGenerator g2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(g1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            g2 = (StandardPieToolTipGenerator) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(g1, g2);

    }

}
