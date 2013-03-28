

package org.jfree.chart.urls.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.urls.CustomPieURLGenerator;


public class CustomPieURLGeneratorTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(CustomPieURLGeneratorTests.class);
    }

    
    public CustomPieURLGeneratorTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        CustomPieURLGenerator g1 = new CustomPieURLGenerator();
        CustomPieURLGenerator g2 = new CustomPieURLGenerator();
        assertTrue(g1.equals(g2));

        Map m1 = new HashMap();
        m1.put("A", "http://www.jfree.org/");
        g1.addURLs(m1);
        assertFalse(g1.equals(g2));
        g2.addURLs(m1);
        assertTrue(g1.equals(g2));
    }

    
    public void testCloning() {
        CustomPieURLGenerator g1 = new CustomPieURLGenerator();
        Map m1 = new HashMap();
        m1.put("A", "http://www.jfree.org/");
        g1.addURLs(m1);
        CustomPieURLGenerator g2 = null;
        try {
            g2 = (CustomPieURLGenerator) g1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(g1 != g2);
        assertTrue(g1.getClass() == g2.getClass());
        assertTrue(g1.equals(g2));

        
        Map m2 = new HashMap();
        m2.put("B", "XYZ");
        g1.addURLs(m2);
        assertFalse(g1.equals(g2));
    }

    
    public void testSerialization() {
        CustomPieURLGenerator g1 = new CustomPieURLGenerator();
        Map m1 = new HashMap();
        m1.put("A", "http://www.jfree.org/");
        g1.addURLs(m1);
        CustomPieURLGenerator g2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(g1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            g2 = (CustomPieURLGenerator) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(g1, g2);
    }

}
