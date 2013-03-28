

package org.jfree.chart.axis.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.axis.CategoryTick;
import org.jfree.chart.text.TextAnchor;
import org.jfree.chart.text.TextBlock;
import org.jfree.chart.text.TextBlockAnchor;
import org.jfree.chart.text.TextLine;


public class CategoryTickTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(CategoryTickTests.class);
    }

    
    public CategoryTickTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        
        Comparable c1 = "C1";
        Comparable c2 = "C2";
        TextBlock tb1 = new TextBlock();
        tb1.addLine(new TextLine("Block 1"));
        TextBlock tb2 = new TextBlock();
        tb1.addLine(new TextLine("Block 2"));
        TextBlockAnchor tba1 = TextBlockAnchor.CENTER;
        TextBlockAnchor tba2 = TextBlockAnchor.BOTTOM_CENTER;
        TextAnchor ta1 = TextAnchor.CENTER;
        TextAnchor ta2 = TextAnchor.BASELINE_LEFT;
        
        CategoryTick t1 = new CategoryTick(c1, tb1, tba1, ta1, 1.0f);
        CategoryTick t2 = new CategoryTick(c1, tb1, tba1, ta1, 1.0f);
        assertTrue(t1.equals(t2));
        
        t1 = new CategoryTick(c2, tb1, tba1, ta1, 1.0f);
        assertFalse(t1.equals(t2));
        t2 = new CategoryTick(c2, tb1, tba1, ta1, 1.0f);
        assertTrue(t1.equals(t2));

        t1 = new CategoryTick(c2, tb2, tba1, ta1, 1.0f);
        assertFalse(t1.equals(t2));
        t2 = new CategoryTick(c2, tb2, tba1, ta1, 1.0f);
        assertTrue(t1.equals(t2));

        t1 = new CategoryTick(c2, tb2, tba2, ta1, 1.0f);
        assertFalse(t1.equals(t2));
        t2 = new CategoryTick(c2, tb2, tba2, ta1, 1.0f);
        assertTrue(t1.equals(t2));

        t1 = new CategoryTick(c2, tb2, tba2, ta2, 1.0f);
        assertFalse(t1.equals(t2));
        t2 = new CategoryTick(c2, tb2, tba2, ta2, 1.0f);
        assertTrue(t1.equals(t2));

        t1 = new CategoryTick(c2, tb2, tba2, ta2, 2.0f);
        assertFalse(t1.equals(t2));
        t2 = new CategoryTick(c2, tb2, tba2, ta2, 2.0f);
        assertTrue(t1.equals(t2));
        
    }

    
    public void testHashCode() {
        Comparable c1 = "C1";
        TextBlock tb1 = new TextBlock();
        tb1.addLine(new TextLine("Block 1"));
        tb1.addLine(new TextLine("Block 2"));
        TextBlockAnchor tba1 = TextBlockAnchor.CENTER;
        TextAnchor ta1 = TextAnchor.CENTER;
        
        CategoryTick t1 = new CategoryTick(c1, tb1, tba1, ta1, 1.0f);
        CategoryTick t2 = new CategoryTick(c1, tb1, tba1, ta1, 1.0f);
        assertTrue(t1.equals(t2));
        int h1 = t1.hashCode();
        int h2 = t2.hashCode();
        assertEquals(h1, h2);
    }

    
    public void testCloning() {
        CategoryTick t1 = new CategoryTick(
            "C1", new TextBlock(), TextBlockAnchor.CENTER, 
            TextAnchor.CENTER, 1.5f
        );
        CategoryTick t2 = null;
        try {
            t2 = (CategoryTick) t1.clone();
        }
        catch (CloneNotSupportedException e) {
            System.err.println("Failed to clone.");
        }
        assertTrue(t1 != t2);
        assertTrue(t1.getClass() == t2.getClass());
        assertTrue(t1.equals(t2));
    }

    
    public void testSerialization() {

        CategoryTick t1 = new CategoryTick(
            "C1", new TextBlock(), TextBlockAnchor.CENTER, 
            TextAnchor.CENTER, 1.5f
        );
        CategoryTick t2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(t1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            t2 = (CategoryTick) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(t1, t2);

    }

}
