

package org.jfree.chart.renderer.category.junit;

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

import org.jfree.chart.renderer.category.StackedBarRenderer3D;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.util.PublicCloneable;


public class StackedBarRenderer3DTests extends TestCase {

    
    static class MyRenderer extends StackedBarRenderer3D {
        public static List createStackedValueList(CategoryDataset dataset,
            Comparable category, double base, boolean asPercentages) {
            return StackedBarRenderer3D.createStackedValueList(dataset,
                    category, base, asPercentages);
        }
    }

    
    public static Test suite() {
        return new TestSuite(StackedBarRenderer3DTests.class);
    }

    
    public StackedBarRenderer3DTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        StackedBarRenderer3D r1 = new StackedBarRenderer3D();
        StackedBarRenderer3D r2 = new StackedBarRenderer3D();
        assertEquals(r1, r2);
    }

    
    public void testHashcode() {
        StackedBarRenderer3D r1 = new StackedBarRenderer3D();
        StackedBarRenderer3D r2 = new StackedBarRenderer3D();
        assertTrue(r1.equals(r2));
        int h1 = r1.hashCode();
        int h2 = r2.hashCode();
        assertEquals(h1, h2);
    }

    
    public void testCloning() {
        StackedBarRenderer3D r1 = new StackedBarRenderer3D();
        StackedBarRenderer3D r2 = null;
        try {
            r2 = (StackedBarRenderer3D) r1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(r1 != r2);
        assertTrue(r1.getClass() == r2.getClass());
        assertTrue(r1.equals(r2));
    }

    
    public void testPublicCloneable() {
        StackedBarRenderer3D r1 = new StackedBarRenderer3D();
        assertTrue(r1 instanceof PublicCloneable);
    }

    
    public void testSerialization() {

        StackedBarRenderer3D r1 = new StackedBarRenderer3D();
        StackedBarRenderer3D r2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(r1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            r2 = (StackedBarRenderer3D) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(r1, r2);

    }

    
    public void testCreateStackedValueList1() {
        DefaultCategoryDataset d = new DefaultCategoryDataset();
        d.addValue(1.0, "s0", "c0");
        List l = MyRenderer.createStackedValueList(d, "c0", 0.0, false);
        assertEquals(2, l.size());
        assertEquals(new Double(0.0), ((Object[]) l.get(0))[1]);
        assertEquals(new Double(1.0), ((Object[]) l.get(1))[1]);
    }

    
    public void testCreateStackedValueList2() {
        DefaultCategoryDataset d = new DefaultCategoryDataset();
        d.addValue(-1.0, "s0", "c0");
        List l = MyRenderer.createStackedValueList(d, "c0", 0.0, false);
        assertEquals(2, l.size());
        assertEquals(new Double(-1.0), ((Object[]) l.get(0))[1]);
        assertEquals(new Double(0.0), ((Object[]) l.get(1))[1]);
    }

    
    public void testCreateStackedValueList3() {
        DefaultCategoryDataset d = new DefaultCategoryDataset();
        d.addValue(0.0, "s0", "c0");
        List l = MyRenderer.createStackedValueList(d, "c0", 0.0, false);
        assertEquals(2, l.size());
        assertEquals(new Double(0.0), ((Object[]) l.get(0))[1]);
        assertEquals(new Double(0.0), ((Object[]) l.get(1))[1]);
    }

    
    public void testCreateStackedValueList4() {
        DefaultCategoryDataset d = new DefaultCategoryDataset();
        d.addValue(null, "s0", "c0");
        List l = MyRenderer.createStackedValueList(d, "c0", 0.0, false);
        assertEquals(0, l.size());
    }

    
    public void testCreateStackedValueList1a() {
        DefaultCategoryDataset d = new DefaultCategoryDataset();
        d.addValue(1.0, "s0", "c0");
        d.addValue(1.1, "s1", "c0");
        List l = MyRenderer.createStackedValueList(d, "c0", 0.0, false);
        assertEquals(3, l.size());
        assertEquals(new Double(0.0), ((Object[]) l.get(0))[1]);
        assertEquals(new Double(1.0), ((Object[]) l.get(1))[1]);
        assertEquals(new Double(2.1), ((Object[]) l.get(2))[1]);
    }

    
    public void testCreateStackedValueList1b() {
        DefaultCategoryDataset d = new DefaultCategoryDataset();
        d.addValue(1.0, "s0", "c0");
        d.addValue(-1.1, "s1", "c0");
        List l = MyRenderer.createStackedValueList(d, "c0", 0.0, false);
        assertEquals(3, l.size());
        assertEquals(new Double(-1.1), ((Object[]) l.get(0))[1]);
        assertEquals(new Double(0.0), ((Object[]) l.get(1))[1]);
        assertEquals(new Double(1.0), ((Object[]) l.get(2))[1]);
    }

    
    public void testCreateStackedValueList1c() {
        DefaultCategoryDataset d = new DefaultCategoryDataset();
        d.addValue(1.0, "s0", "c0");
        d.addValue(0.0, "s1", "c0");
        List l = MyRenderer.createStackedValueList(d, "c0", 0.0, false);
        assertEquals(3, l.size());
        assertEquals(new Double(0.0), ((Object[]) l.get(0))[1]);
        assertEquals(new Double(1.0), ((Object[]) l.get(1))[1]);
        assertEquals(new Double(1.0), ((Object[]) l.get(2))[1]);
    }

    
    public void testCreateStackedValueList1d() {
        DefaultCategoryDataset d = new DefaultCategoryDataset();
        d.addValue(1.0, "s0", "c0");
        d.addValue(null, "s1", "c0");
        List l = MyRenderer.createStackedValueList(d, "c0", 0.0, false);
        assertEquals(2, l.size());
        assertEquals(new Double(0.0), ((Object[]) l.get(0))[1]);
        assertEquals(new Double(1.0), ((Object[]) l.get(1))[1]);
    }

    
    public void testCreateStackedValueList2a() {
        DefaultCategoryDataset d = new DefaultCategoryDataset();
        d.addValue(-1.0, "s0", "c0");
        d.addValue(1.1, "s1", "c0");
        List l = MyRenderer.createStackedValueList(d, "c0", 0.0, false);
        assertEquals(3, l.size());
        assertEquals(new Double(-1.0), ((Object[]) l.get(0))[1]);
        assertEquals(new Double(0.0), ((Object[]) l.get(1))[1]);
        assertEquals(new Double(1.1), ((Object[]) l.get(2))[1]);
    }

    
    public void testCreateStackedValueList2b() {
        DefaultCategoryDataset d = new DefaultCategoryDataset();
        d.addValue(-1.0, "s0", "c0");
        d.addValue(-1.1, "s1", "c0");
        List l = MyRenderer.createStackedValueList(d, "c0", 0.0, false);
        assertEquals(3, l.size());
        assertEquals(new Double(-2.1), ((Object[]) l.get(0))[1]);
        assertEquals(new Double(-1.0), ((Object[]) l.get(1))[1]);
        assertEquals(new Double(0.0), ((Object[]) l.get(2))[1]);
    }

    
    public void testCreateStackedValueList2c() {
        DefaultCategoryDataset d = new DefaultCategoryDataset();
        d.addValue(-1.0, "s0", "c0");
        d.addValue(0.0, "s1", "c0");
        List l = MyRenderer.createStackedValueList(d, "c0", 0.0, false);
        assertEquals(3, l.size());
        assertEquals(new Double(-1.0), ((Object[]) l.get(0))[1]);
        assertEquals(new Double(0.0), ((Object[]) l.get(1))[1]);
        assertEquals(new Double(0.0), ((Object[]) l.get(2))[1]);
    }

    
    public void testCreateStackedValueList2d() {
        DefaultCategoryDataset d = new DefaultCategoryDataset();
        d.addValue(-1.0, "s0", "c0");
        d.addValue(null, "s1", "c0");
        List l = MyRenderer.createStackedValueList(d, "c0", 0.0, false);
        assertEquals(2, l.size());
        assertEquals(new Double(-1.0), ((Object[]) l.get(0))[1]);
        assertEquals(new Double(0.0), ((Object[]) l.get(1))[1]);
    }

    
    public void testCreateStackedValueList3a() {
        DefaultCategoryDataset d = new DefaultCategoryDataset();
        d.addValue(0.0, "s0", "c0");
        d.addValue(1.1, "s1", "c0");
        List l = MyRenderer.createStackedValueList(d, "c0", 0.0, false);
        assertEquals(3, l.size());
        assertEquals(new Double(0.0), ((Object[]) l.get(0))[1]);
        assertEquals(new Double(0.0), ((Object[]) l.get(1))[1]);
        assertEquals(new Double(1.1), ((Object[]) l.get(2))[1]);
    }

    
    public void testCreateStackedValueList3b() {
        DefaultCategoryDataset d = new DefaultCategoryDataset();
        d.addValue(0.0, "s0", "c0");
        d.addValue(-1.1, "s1", "c0");
        List l = MyRenderer.createStackedValueList(d, "c0", 0.0, false);
        assertEquals(3, l.size());
        assertEquals(new Double(-1.1), ((Object[]) l.get(0))[1]);
        assertEquals(new Double(0.0), ((Object[]) l.get(1))[1]);
        assertEquals(new Double(0.0), ((Object[]) l.get(2))[1]);
    }

    
    public void testCreateStackedValueList3c() {
        DefaultCategoryDataset d = new DefaultCategoryDataset();
        d.addValue(0.0, "s0", "c0");
        d.addValue(0.0, "s1", "c0");
        List l = MyRenderer.createStackedValueList(d, "c0", 0.0, false);
        assertEquals(3, l.size());
        assertEquals(new Double(0.0), ((Object[]) l.get(0))[1]);
        assertEquals(new Double(0.0), ((Object[]) l.get(1))[1]);
        assertEquals(new Double(0.0), ((Object[]) l.get(2))[1]);
    }

    
    public void testCreateStackedValueList3d() {
        DefaultCategoryDataset d = new DefaultCategoryDataset();
        d.addValue(0.0, "s0", "c0");
        d.addValue(null, "s1", "c0");
        List l = MyRenderer.createStackedValueList(d, "c0", 0.0, false);
        assertEquals(2, l.size());
        assertEquals(new Double(0.0), ((Object[]) l.get(0))[1]);
        assertEquals(new Double(0.0), ((Object[]) l.get(1))[1]);
    }

    
    public void testCreateStackedValueList5() {
        DefaultCategoryDataset d = new DefaultCategoryDataset();
        d.addValue(1.0, "s0", "c0");
        d.addValue(null, "s1", "c0");
        d.addValue(2.0, "s2", "c0");
        List l = MyRenderer.createStackedValueList(d, "c0", 0.0, false);
        assertEquals(3, l.size());
        assertEquals(new Double(0.0), ((Object[]) l.get(0))[1]);
        assertEquals(new Double(1.0), ((Object[]) l.get(1))[1]);
        assertEquals(new Double(3.0), ((Object[]) l.get(2))[1]);
    }
}
