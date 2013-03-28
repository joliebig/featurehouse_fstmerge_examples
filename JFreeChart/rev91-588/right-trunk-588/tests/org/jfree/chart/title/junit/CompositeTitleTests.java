

package org.jfree.chart.title.junit;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.block.BlockContainer;
import org.jfree.chart.title.CompositeTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.util.RectangleInsets;


public class CompositeTitleTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(CompositeTitleTests.class);
    }

    
    public CompositeTitleTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        CompositeTitle t1 = new CompositeTitle(new BlockContainer());
        CompositeTitle t2 = new CompositeTitle(new BlockContainer());
        assertEquals(t1, t2);
        assertEquals(t2, t1);
        
        
        t1.setMargin(new RectangleInsets(1.0, 2.0, 3.0, 4.0));
        assertFalse(t1.equals(t2));
        t2.setMargin(new RectangleInsets(1.0, 2.0, 3.0, 4.0));
        assertTrue(t1.equals(t2));
        
        
        t1.setFrame(new BlockBorder(Color.red));
        assertFalse(t1.equals(t2));
        t2.setFrame(new BlockBorder(Color.red));
        assertTrue(t1.equals(t2));
       
        
        t1.setPadding(new RectangleInsets(1.0, 2.0, 3.0, 4.0));
        assertFalse(t1.equals(t2));
        t2.setPadding(new RectangleInsets(1.0, 2.0, 3.0, 4.0));
        assertTrue(t1.equals(t2));
        
        
        t1.getContainer().add(new TextTitle("T1"));
        assertFalse(t1.equals(t2));
        t2.getContainer().add(new TextTitle("T1"));
        assertTrue(t1.equals(t2));
        
    }

    
    public void testHashcode() {
        CompositeTitle t1 = new CompositeTitle(new BlockContainer());
        t1.getContainer().add(new TextTitle("T1"));
        CompositeTitle t2 = new CompositeTitle(new BlockContainer());
        t2.getContainer().add(new TextTitle("T1"));
        assertTrue(t1.equals(t2));
        int h1 = t1.hashCode();
        int h2 = t2.hashCode();
        assertEquals(h1, h2);
    }
    
    
    public void testCloning() {
        CompositeTitle t1 = new CompositeTitle(new BlockContainer());
        t1.getContainer().add(new TextTitle("T1"));
        CompositeTitle t2 = null;
        try {
            t2 = (CompositeTitle) t1.clone();
        }
        catch (CloneNotSupportedException e) {
            fail(e.toString());
        }
        assertTrue(t1 != t2);
        assertTrue(t1.getClass() == t2.getClass());
        assertTrue(t1.equals(t2));
    }

    
    public void testSerialization() {
        CompositeTitle t1 = new CompositeTitle(new BlockContainer());
        t1.getContainer().add(new TextTitle("T1"));
        CompositeTitle t2 = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(t1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            t2 = (CompositeTitle) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(t1, t2);
    }

}
