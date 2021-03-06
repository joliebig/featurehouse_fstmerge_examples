

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

import org.jfree.chart.labels.BubbleXYItemLabelGenerator;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


public class BubbleXYItemLabelGeneratorTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(BubbleXYItemLabelGeneratorTests.class);
    }

    
    public BubbleXYItemLabelGeneratorTests(String name) {
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

        BubbleXYItemLabelGenerator g1 = null;
        BubbleXYItemLabelGenerator g2 = null;
        
        g1 = new BubbleXYItemLabelGenerator(f1, xnf1, ynf1, znf1);
        g2 = new BubbleXYItemLabelGenerator(f1, xnf1, ynf1, znf1);
        assertTrue(g1.equals(g2));
        assertTrue(g2.equals(g1));
        
        g1 = new BubbleXYItemLabelGenerator(f2, xnf1, ynf1, znf1);
        assertFalse(g1.equals(g2));
        g2 = new BubbleXYItemLabelGenerator(f2, xnf1, ynf1, znf1);
        assertTrue(g1.equals(g2));
                
        g1 = new BubbleXYItemLabelGenerator(f2, xnf2, ynf1, znf1);
        assertFalse(g1.equals(g2));
        g2 = new BubbleXYItemLabelGenerator(f2, xnf2, ynf1, znf1);
        assertTrue(g1.equals(g2));

        g1 = new BubbleXYItemLabelGenerator(f2, xnf2, ynf2, znf1);
        assertFalse(g1.equals(g2));
        g2 = new BubbleXYItemLabelGenerator(f2, xnf2, ynf2, znf1);
        assertTrue(g1.equals(g2));
                
        g1 = new BubbleXYItemLabelGenerator(f2, xnf2, ynf2, znf2);
        assertFalse(g1.equals(g2));
        g2 = new BubbleXYItemLabelGenerator(f2, xnf2, ynf2, znf2);
        assertTrue(g1.equals(g2));

        DateFormat xdf1 = new SimpleDateFormat("d-MMM");
        DateFormat xdf2 = new SimpleDateFormat("d-MMM-yyyy");
        DateFormat ydf1 = new SimpleDateFormat("d-MMM");
        DateFormat ydf2 = new SimpleDateFormat("d-MMM-yyyy");
        DateFormat zdf1 = new SimpleDateFormat("d-MMM");
        DateFormat zdf2 = new SimpleDateFormat("d-MMM-yyyy");

        g1 = new BubbleXYItemLabelGenerator(f1, xdf1, ydf1, zdf1);
        g2 = new BubbleXYItemLabelGenerator(f1, xdf1, ydf1, zdf1);
        assertTrue(g1.equals(g2));
        assertTrue(g2.equals(g1));
        
        g1 = new BubbleXYItemLabelGenerator(f1, xdf2, ydf1, zdf1);
        assertFalse(g1.equals(g2));
        g2 = new BubbleXYItemLabelGenerator(f1, xdf2, ydf1, zdf1);
        assertTrue(g1.equals(g2));
                
        g1 = new BubbleXYItemLabelGenerator(f1, xdf2, ydf2, zdf1);
        assertFalse(g1.equals(g2));
        g2 = new BubbleXYItemLabelGenerator(f1, xdf2, ydf2, zdf1);
        assertTrue(g1.equals(g2));

        g1 = new BubbleXYItemLabelGenerator(f1, xdf2, ydf2, zdf2);
        assertFalse(g1.equals(g2));
        g2 = new BubbleXYItemLabelGenerator(f1, xdf2, ydf2, zdf2);
        assertTrue(g1.equals(g2));
    }
    
    
    public void testHashCode() {
    	BubbleXYItemLabelGenerator g1 
    	        = new BubbleXYItemLabelGenerator();
    	BubbleXYItemLabelGenerator g2 
    	        = new BubbleXYItemLabelGenerator();
        assertTrue(g1.equals(g2));
        assertTrue(g1.hashCode() == g2.hashCode());
    }

    
    public void testCloning() {
        BubbleXYItemLabelGenerator g1 = new BubbleXYItemLabelGenerator();
        BubbleXYItemLabelGenerator g2 = null;
        try {
            g2 = (BubbleXYItemLabelGenerator) g1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(g1 != g2);
        assertTrue(g1.getClass() == g2.getClass());
        assertTrue(g1.equals(g2));
    }

    
    public void testSerialization() {

        BubbleXYItemLabelGenerator g1 = new BubbleXYItemLabelGenerator();
        BubbleXYItemLabelGenerator g2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(g1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            g2 = (BubbleXYItemLabelGenerator) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(g1, g2);

    }
    
    
    public void testGenerateLabel() {
        
        
        XYSeries s1 = new XYSeries("S1");
        s1.add(1.0, 2.0);
        s1.add(2.2, 3.3);
        XYSeriesCollection dataset = new XYSeriesCollection(s1);
        BubbleXYItemLabelGenerator g = new BubbleXYItemLabelGenerator();
        assertEquals("{3}", g.generateLabel(dataset, 0, 0));
        assertEquals("{3}", g.generateLabel(dataset, 0, 1));
    }

}
