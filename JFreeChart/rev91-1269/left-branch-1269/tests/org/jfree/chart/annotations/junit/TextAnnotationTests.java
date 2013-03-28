

package org.jfree.chart.annotations.junit;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.annotations.CategoryTextAnnotation;
import org.jfree.chart.annotations.TextAnnotation;
import org.jfree.ui.TextAnchor;


public class TextAnnotationTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(TextAnnotationTests.class);
    }

    
    public TextAnnotationTests(String name) {
        super(name);
    }

    
    public void testEquals() {

        TextAnnotation a1 = new CategoryTextAnnotation("Test", "Category", 1.0);
        TextAnnotation a2 = new CategoryTextAnnotation("Test", "Category", 1.0);
        assertTrue(a1.equals(a2));

        
        a1.setText("Text");
        assertFalse(a1.equals(a2));
        a2.setText("Text");
        assertTrue(a1.equals(a2));

        
        a1.setFont(new Font("Serif", Font.BOLD, 18));
        assertFalse(a1.equals(a2));
        a2.setFont(new Font("Serif", Font.BOLD, 18));
        assertTrue(a1.equals(a2));

        
        a1.setPaint(new GradientPaint(1.0f, 2.0f, Color.red,
                3.0f, 4.0f, Color.pink));
        assertFalse(a1.equals(a2));
        a2.setPaint(new GradientPaint(1.0f, 2.0f, Color.red,
                3.0f, 4.0f, Color.pink));
        assertTrue(a1.equals(a2));

        
        a1.setTextAnchor(TextAnchor.BOTTOM_LEFT);
        assertFalse(a1.equals(a2));
        a2.setTextAnchor(TextAnchor.BOTTOM_LEFT);
        assertTrue(a1.equals(a2));

        
        a1.setRotationAnchor(TextAnchor.BOTTOM_LEFT);
        assertFalse(a1.equals(a2));
        a2.setRotationAnchor(TextAnchor.BOTTOM_LEFT);
        assertTrue(a1.equals(a2));

        
        a1.setRotationAngle(Math.PI);
        assertFalse(a1.equals(a2));
        a2.setRotationAngle(Math.PI);
        assertTrue(a1.equals(a2));

    }

    
    public void testHashCode() {
        TextAnnotation a1 = new CategoryTextAnnotation("Test", "Category", 1.0);
        TextAnnotation a2 = new CategoryTextAnnotation("Test", "Category", 1.0);
        assertTrue(a1.equals(a2));
        int h1 = a1.hashCode();
        int h2 = a2.hashCode();
        assertEquals(h1, h2);
    }

}
