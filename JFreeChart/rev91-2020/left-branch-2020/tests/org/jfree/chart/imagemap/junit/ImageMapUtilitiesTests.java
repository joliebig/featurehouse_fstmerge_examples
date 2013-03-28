

package org.jfree.chart.imagemap.junit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.imagemap.ImageMapUtilities;


public class ImageMapUtilitiesTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(ImageMapUtilitiesTests.class);
    }

    
    public ImageMapUtilitiesTests(String name) {
        super(name);
    }

    
    public void testHTMLEscape() {
        assertEquals("", ImageMapUtilities.htmlEscape(""));
        assertEquals("abc", ImageMapUtilities.htmlEscape("abc"));
        assertEquals("&amp;", ImageMapUtilities.htmlEscape("&"));
        assertEquals("&quot;", ImageMapUtilities.htmlEscape("\""));
        assertEquals("&lt;", ImageMapUtilities.htmlEscape("<"));
        assertEquals("&gt;", ImageMapUtilities.htmlEscape(">"));
        assertEquals("&#39;", ImageMapUtilities.htmlEscape("\'"));
        assertEquals("&#092;abc", ImageMapUtilities.htmlEscape("\\abc"));
        assertEquals("abc\n", ImageMapUtilities.htmlEscape("abc\n"));
    }

    
    public void testJavascriptEscape() {
        assertEquals("", ImageMapUtilities.javascriptEscape(""));
        assertEquals("abc", ImageMapUtilities.javascriptEscape("abc"));
        assertEquals("\\\'", ImageMapUtilities.javascriptEscape("\'"));
        assertEquals("\\\"", ImageMapUtilities.javascriptEscape("\""));   
        assertEquals("\\\\", ImageMapUtilities.javascriptEscape("\\"));
    }

}
