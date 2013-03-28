

package org.jfree.chart.block.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.block.BlockContainer;
import org.jfree.chart.block.ColumnArrangement;
import org.jfree.chart.block.EmptyBlock;
import org.jfree.chart.block.FlowArrangement;


public class BlockContainerTests extends TestCase {
    
    
    public static Test suite() {
        return new TestSuite(BlockContainerTests.class);
    }

    
    public BlockContainerTests(String name) {
        super(name);
    }
    
    
    public void testEquals() {
        BlockContainer c1 = new BlockContainer(new FlowArrangement());
        BlockContainer c2 = new BlockContainer(new FlowArrangement());
        assertTrue(c1.equals(c2));
        assertTrue(c2.equals(c2));
        
        c1.setArrangement(new ColumnArrangement());
        assertFalse(c1.equals(c2));
        c2.setArrangement(new ColumnArrangement());
        assertTrue(c1.equals(c2));
        
        c1.add(new EmptyBlock(1.2, 3.4));
        assertFalse(c1.equals(c2));
        c2.add(new EmptyBlock(1.2, 3.4));
        assertTrue(c1.equals(c2));
    }

    
    public void testCloning() {
        BlockContainer c1 = new BlockContainer(new FlowArrangement());
        c1.add(new EmptyBlock(1.2, 3.4));
        
        BlockContainer c2 = null;
        
        try {
            c2 = (BlockContainer) c1.clone();
        }
        catch (CloneNotSupportedException e) {
            System.err.println("Failed to clone.");
        }
        assertTrue(c1 != c2);
        assertTrue(c1.getClass() == c2.getClass());
        assertTrue(c1.equals(c2));
    }

    
    public void testSerialization() {
        BlockContainer c1 = new BlockContainer();
        c1.add(new EmptyBlock(1.2, 3.4));
        BlockContainer c2 = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(c1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            c2 = (BlockContainer) in.readObject();
            in.close();
        }
        catch (Exception e) {
            fail(e.toString());
        }
        assertEquals(c1, c2);
    }
   
}
