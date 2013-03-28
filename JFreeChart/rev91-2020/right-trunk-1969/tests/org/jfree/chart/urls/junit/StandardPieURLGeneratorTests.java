

package org.jfree.chart.urls.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.urls.StandardPieURLGenerator;
import org.jfree.chart.util.PublicCloneable;
import org.jfree.data.general.DefaultPieDataset;


public class StandardPieURLGeneratorTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(StandardPieURLGeneratorTests.class);
    }

    
    public StandardPieURLGeneratorTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        StandardPieURLGenerator g1 = new StandardPieURLGenerator();
        StandardPieURLGenerator g2 = new StandardPieURLGenerator();
        assertTrue(g1.equals(g2));

        g1 = new StandardPieURLGenerator("prefix", "category", "index");
        assertFalse(g1.equals(g2));
        g2 = new StandardPieURLGenerator("prefix", "category", "index");
        assertTrue(g1.equals(g2));

        g1 = new StandardPieURLGenerator("prefix2", "category", "index");
        assertFalse(g1.equals(g2));
        g2 = new StandardPieURLGenerator("prefix2", "category", "index");
        assertTrue(g1.equals(g2));

        g1 = new StandardPieURLGenerator("prefix2", "category2", "index");
        assertFalse(g1.equals(g2));
        g2 = new StandardPieURLGenerator("prefix2", "category2", "index");
        assertTrue(g1.equals(g2));

        g1 = new StandardPieURLGenerator("prefix2", "category2", "index2");
        assertFalse(g1.equals(g2));
        g2 = new StandardPieURLGenerator("prefix2", "category2", "index2");
        assertTrue(g1.equals(g2));

        g1 = new StandardPieURLGenerator("prefix2", "category2", null);
        assertFalse(g1.equals(g2));
        g2 = new StandardPieURLGenerator("prefix2", "category2", null);
        assertTrue(g1.equals(g2));
    }

    
    public void testPublicCloneable() {
        StandardPieURLGenerator g1 = new StandardPieURLGenerator(
                "index.html?", "cat");
        assertFalse(g1 instanceof PublicCloneable);
    }

    
    public void testSerialization() {

        StandardPieURLGenerator g1 = new StandardPieURLGenerator(
                "index.html?", "cat");
        StandardPieURLGenerator g2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(g1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            g2 = (StandardPieURLGenerator) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(g1, g2);

    }

    
    public void testURL() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Alpha '1'", new Double(5.0));
        dataset.setValue("Beta", new Double(5.5));
        StandardPieURLGenerator g1 = new StandardPieURLGenerator(
                "chart.jsp", "category");
        String url = g1.generateURL(dataset, "Beta", 0);
        assertEquals("chart.jsp?category=Beta&amp;pieIndex=0", url);
        url = g1.generateURL(dataset, "Alpha '1'", 0);
        assertEquals("chart.jsp?category=Alpha+%271%27&amp;pieIndex=0", url);
    }

}
