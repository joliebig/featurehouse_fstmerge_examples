

package org.jfree.chart.labels.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.text.AttributedString;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.util.PublicCloneable;


public class StandardPieSectionLabelGeneratorTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(StandardPieSectionLabelGeneratorTests.class);
    }

    
    public StandardPieSectionLabelGeneratorTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        StandardPieSectionLabelGenerator g1
                = new StandardPieSectionLabelGenerator();
        StandardPieSectionLabelGenerator g2
                = new StandardPieSectionLabelGenerator();
        assertTrue(g1.equals(g2));
        assertTrue(g2.equals(g1));

        g1 = new StandardPieSectionLabelGenerator("{0}",
                new DecimalFormat("#,##0.00"),
                NumberFormat.getPercentInstance());
        assertFalse(g1.equals(g2));
        g2 = new StandardPieSectionLabelGenerator("{0}",
                new DecimalFormat("#,##0.00"),
                NumberFormat.getPercentInstance());
        assertTrue(g1.equals(g2));

        g1 = new StandardPieSectionLabelGenerator("{0} {1}",
                new DecimalFormat("#,##0.00"),
                NumberFormat.getPercentInstance());
        assertFalse(g1.equals(g2));
        g2 = new StandardPieSectionLabelGenerator("{0} {1}",
                new DecimalFormat("#,##0.00"),
                NumberFormat.getPercentInstance());
        assertTrue(g1.equals(g2));

        g1 = new StandardPieSectionLabelGenerator("{0} {1}",
                new DecimalFormat("#,##0"), NumberFormat.getPercentInstance());
        assertFalse(g1.equals(g2));
        g2 = new StandardPieSectionLabelGenerator("{0} {1}",
                new DecimalFormat("#,##0"), NumberFormat.getPercentInstance());
        assertTrue(g1.equals(g2));

        g1 = new StandardPieSectionLabelGenerator("{0} {1}",
                new DecimalFormat("#,##0"), new DecimalFormat("0.000%"));
        assertFalse(g1.equals(g2));
        g2 = new StandardPieSectionLabelGenerator("{0} {1}",
                new DecimalFormat("#,##0"), new DecimalFormat("0.000%"));
        assertTrue(g1.equals(g2));

        AttributedString as = new AttributedString("XYZ");
        g1.setAttributedLabel(0, as);
        assertFalse(g1.equals(g2));
        g2.setAttributedLabel(0, as);
        assertTrue(g1.equals(g2));
    }

    
    public void testHashCode() {
        StandardPieSectionLabelGenerator g1
                = new StandardPieSectionLabelGenerator();
        StandardPieSectionLabelGenerator g2
                = new StandardPieSectionLabelGenerator();
        assertTrue(g1.equals(g2));
        assertTrue(g1.hashCode() == g2.hashCode());
    }

    
    public void testCloning() {
        StandardPieSectionLabelGenerator g1
                = new StandardPieSectionLabelGenerator();
        StandardPieSectionLabelGenerator g2 = null;
        try {
            g2 = (StandardPieSectionLabelGenerator) g1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(g1 != g2);
        assertTrue(g1.getClass() == g2.getClass());
        assertTrue(g1.equals(g2));
    }

    
    public void testPublicCloneable() {
        StandardPieSectionLabelGenerator g1
                = new StandardPieSectionLabelGenerator();
        assertTrue(g1 instanceof PublicCloneable);
    }

    
    public void testSerialization() {

        StandardPieSectionLabelGenerator g1
                = new StandardPieSectionLabelGenerator();
        StandardPieSectionLabelGenerator g2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(g1);
            out.close();

            ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                    buffer.toByteArray()));
            g2 = (StandardPieSectionLabelGenerator) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(g1, g2);

    }

    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

}
