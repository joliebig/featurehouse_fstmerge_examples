

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

import org.jfree.chart.urls.StandardCategoryURLGenerator;
import org.jfree.data.category.DefaultCategoryDataset;


public class StandardCategoryURLGeneratorTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(StandardCategoryURLGeneratorTests.class);
    }

    
    public StandardCategoryURLGeneratorTests(String name) {
        super(name);
    }
    
    
    public void testGenerateURL() {
        StandardCategoryURLGenerator g1 = new StandardCategoryURLGenerator();
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(1.0, "R1", "C1");
        dataset.addValue(2.0, "R2", "C2");
        dataset.addValue(3.0, "R&", "C&");
        assertEquals("index.html?series=R1&amp;category=C1", 
                g1.generateURL(dataset, 0, 0));
        assertEquals("index.html?series=R1&amp;category=C2", 
                g1.generateURL(dataset, 0, 1));
        assertEquals("index.html?series=R2&amp;category=C2", 
                g1.generateURL(dataset, 1, 1));
        assertEquals("index.html?series=R%26&amp;category=C%26", 
                g1.generateURL(dataset, 2, 2));
    }
    
    
    public void testEquals() {
        StandardCategoryURLGenerator g1 = new StandardCategoryURLGenerator();
        StandardCategoryURLGenerator g2 = new StandardCategoryURLGenerator();
        assertTrue(g1.equals(g2));
        
        g1 = new StandardCategoryURLGenerator("index2.html?");
        assertFalse(g1.equals(g2));
        g2 = new StandardCategoryURLGenerator("index2.html?");
        assertTrue(g1.equals(g2));
        
        g1 = new StandardCategoryURLGenerator("index2.html?", "A", "B");
        assertFalse(g1.equals(g2));
        g2 = new StandardCategoryURLGenerator("index2.html?", "A", "B");
        assertTrue(g1.equals(g2));

        g1 = new StandardCategoryURLGenerator("index2.html?", "A", "C");
        assertFalse(g1.equals(g2));
        g2 = new StandardCategoryURLGenerator("index2.html?", "A", "C");
        assertTrue(g1.equals(g2));
    }
    
    
    public void testSerialization() {
        StandardCategoryURLGenerator g1 = new StandardCategoryURLGenerator(
                "index.html?");
        StandardCategoryURLGenerator g2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(g1);
            out.close();

            ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                    buffer.toByteArray()));
            g2 = (StandardCategoryURLGenerator) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(g1, g2);
    }

}
