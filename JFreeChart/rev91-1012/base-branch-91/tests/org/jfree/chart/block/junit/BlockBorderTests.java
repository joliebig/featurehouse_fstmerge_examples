

package org.jfree.chart.block.junit;

import java.awt.Color;
import java.awt.GradientPaint;
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
import org.jfree.ui.RectangleInsets;
import org.jfree.util.UnitType;


public class BlockBorderTests extends TestCase {
    
    
    public static Test suite() {
        return new TestSuite(BlockBorderTests.class);
    }

    
    public BlockBorderTests(String name) {
        super(name);
    }
    
    
    public void testEquals() {
        BlockBorder b1 = new BlockBorder(new RectangleInsets(1.0, 2.0, 3.0, 
                4.0), Color.red);
        BlockBorder b2 = new BlockBorder(new RectangleInsets(1.0, 2.0, 3.0, 
                4.0), Color.red);
        assertTrue(b1.equals(b2));
        assertTrue(b2.equals(b2));
        
        
        b1 = new BlockBorder(new RectangleInsets(UnitType.RELATIVE, 1.0, 2.0,
                3.0, 4.0), Color.red);
        assertFalse(b1.equals(b2));
        b2 = new BlockBorder(new RectangleInsets(UnitType.RELATIVE, 1.0, 2.0, 
                3.0, 4.0), Color.red);
        assertTrue(b1.equals(b2));
        
        
        b1 = new BlockBorder(new RectangleInsets(1.0, 2.0, 3.0, 4.0), 
                Color.blue);
        assertFalse(b1.equals(b2));
        b2 = new BlockBorder(new RectangleInsets(1.0, 2.0, 3.0, 4.0), 
                Color.blue);
        assertTrue(b1.equals(b2));
    }

    
    public void testCloning() {
        BlockBorder b1 = new BlockBorder();
        assertFalse(b1 instanceof Cloneable);
    }

    
    public void testSerialization() {
        BlockBorder b1 = new BlockBorder(new RectangleInsets(1.0, 2.0, 3.0, 
                4.0), new GradientPaint(1.0f, 2.0f, Color.red, 3.0f, 4.0f, 
                Color.yellow));
        BlockBorder b2 = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(b1);
            out.close();

            ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                    buffer.toByteArray()));
            b2 = (BlockBorder) in.readObject();
            in.close();
        }
        catch (Exception e) {
            fail(e.toString());
        }
        assertTrue(b1.equals(b2));
    }
   
}
