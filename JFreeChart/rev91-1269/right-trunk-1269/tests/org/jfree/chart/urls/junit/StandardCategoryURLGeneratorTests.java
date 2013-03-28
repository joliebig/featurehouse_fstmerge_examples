

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


public class StandardCategoryURLGeneratorTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(StandardCategoryURLGeneratorTests.class);
    }

    
    public StandardCategoryURLGeneratorTests(String name) {
        super(name);
    }

    
    public void testSerialization() {

        StandardCategoryURLGenerator g1 = new StandardCategoryURLGenerator(
            "index.html?"
        );
        StandardCategoryURLGenerator g2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(g1);
            out.close();

            ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                buffer.toByteArray())
            );
            g2 = (StandardCategoryURLGenerator) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(g1, g2);

    }

}
