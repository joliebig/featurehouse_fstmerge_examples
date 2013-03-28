

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

import org.jfree.chart.block.Block;
import org.jfree.chart.block.BlockContainer;
import org.jfree.chart.block.EmptyBlock;
import org.jfree.chart.block.GridArrangement;
import org.jfree.chart.block.LengthConstraintType;
import org.jfree.chart.block.RectangleConstraint;
import org.jfree.chart.util.Size2D;


public class GridArrangementTests extends TestCase {
    
    
    public static Test suite() {
        return new TestSuite(GridArrangementTests.class);
    }

    
    public GridArrangementTests(String name) {
        super(name);
    }
    
    
    public void testEquals() {
        GridArrangement f1 = new GridArrangement(11, 22);
        GridArrangement f2 = new GridArrangement(11, 22);
        assertTrue(f1.equals(f2));
        assertTrue(f2.equals(f1));

        f1 = new GridArrangement(33, 22);
        assertFalse(f1.equals(f2));
        f2 = new GridArrangement(33, 22);
        assertTrue(f1.equals(f2));

        f1 = new GridArrangement(33, 44);
        assertFalse(f1.equals(f2));
        f2 = new GridArrangement(33, 44);
        assertTrue(f1.equals(f2));   
    }

    
    public void testCloning() {
        GridArrangement f1 = new GridArrangement(1, 2);
        assertFalse(f1 instanceof Cloneable);
    }

    
    public void testSerialization() {
        GridArrangement f1 = new GridArrangement(33, 44);
        GridArrangement f2 = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(f1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            f2 = (GridArrangement) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(f1, f2);
    }
    
    private static final double EPSILON = 0.000000001;
    
    
    public void testNN() {
        BlockContainer c = createTestContainer1();
        Size2D s = c.arrange(null, RectangleConstraint.NONE);
        assertEquals(90.0, s.width, EPSILON);
        assertEquals(33.0, s.height, EPSILON);
    }
   
    
    public void testFN() {
        BlockContainer c = createTestContainer1();
        RectangleConstraint constraint = new RectangleConstraint(
            100.0, null, LengthConstraintType.FIXED, 
            0.0, null, LengthConstraintType.NONE
        );
        Size2D s = c.arrange(null, constraint);
        assertEquals(100.0, s.width, EPSILON);
        assertEquals(33.0, s.height, EPSILON);
    }

    private BlockContainer createTestContainer1() {
        Block b1 = new EmptyBlock(10, 11);
        Block b2 = new EmptyBlock(20, 22);
        Block b3 = new EmptyBlock(30, 33);
        BlockContainer result = new BlockContainer(new GridArrangement(1, 3));
        result.add(b1);
        result.add(b2);
        result.add(b3);
        return result;
    }
    
}
