

package org.jfree.chart.plot.junit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.plot.ColorPalette;
import org.jfree.chart.plot.GreyPalette;


public class ColorPaletteTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(ColorPaletteTests.class);
    }

    
    public ColorPaletteTests(String name) {
        super(name);
    }

    
    public void testCloning() {
        ColorPalette p1 = new GreyPalette();
        ColorPalette p2 = null;
        try {
            p2 = (ColorPalette) p1.clone();
        }
        catch (CloneNotSupportedException e) {
            System.err.println("Failed to clone.");
        }
        assertTrue(p1 != p2);
        assertTrue(p1.getClass() == p2.getClass());
        assertTrue(p1.equals(p2));
    }

    
    public void testEquals() {
        ColorPalette p1 = new GreyPalette();
        ColorPalette p2 = new GreyPalette();
        assertTrue(p1.equals(p2));
    }

}
