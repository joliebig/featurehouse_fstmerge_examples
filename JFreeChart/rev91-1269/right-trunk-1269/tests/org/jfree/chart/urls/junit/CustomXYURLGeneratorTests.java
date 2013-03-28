

package org.jfree.chart.urls.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.urls.CustomXYURLGenerator;


public class CustomXYURLGeneratorTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(CustomXYURLGeneratorTests.class);
    }

    
    public CustomXYURLGeneratorTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        CustomXYURLGenerator g1 = new CustomXYURLGenerator();
        CustomXYURLGenerator g2 = new CustomXYURLGenerator();
        assertTrue(g1.equals(g2));
        List u1 = new java.util.ArrayList();
        u1.add("URL A1");
        u1.add("URL A2");
        u1.add("URL A3");
        g1.addURLSeries(u1);
        assertFalse(g1.equals(g2));
        List u2 = new java.util.ArrayList();
        u2.add("URL A1");
        u2.add("URL A2");
        u2.add("URL A3");
        g2.addURLSeries(u2);
        assertTrue(g1.equals(g2));
    }

    
    public void testCloning() {
        CustomXYURLGenerator g1 = new CustomXYURLGenerator();
        List u1 = new java.util.ArrayList();
        u1.add("URL A1");
        u1.add("URL A2");
        u1.add("URL A3");
        g1.addURLSeries(u1);
        CustomXYURLGenerator g2 = null;
        try {
            g2 = (CustomXYURLGenerator) g1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(g1 != g2);
        assertTrue(g1.getClass() == g2.getClass());
        assertTrue(g1.equals(g2));

        
        List u2 = new java.util.ArrayList();
        u2.add("URL XXX");
        g1.addURLSeries(u2);
        assertFalse(g1.equals(g2));
    }

    
    public void testSerialization() {

        List u1 = new java.util.ArrayList();
        u1.add("URL A1");
        u1.add("URL A2");
        u1.add("URL A3");

        List u2 = new java.util.ArrayList();
        u2.add("URL B1");
        u2.add("URL B2");
        u2.add("URL B3");

        CustomXYURLGenerator g1 = new CustomXYURLGenerator();
        CustomXYURLGenerator g2 = null;

        g1.addURLSeries(u1);
        g1.addURLSeries(u2);

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(g1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            g2 = (CustomXYURLGenerator) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(g1, g2);

    }

}
