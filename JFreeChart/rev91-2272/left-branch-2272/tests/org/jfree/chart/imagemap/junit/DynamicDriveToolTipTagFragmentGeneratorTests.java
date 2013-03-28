

package org.jfree.chart.imagemap.junit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.imagemap.OverLIBToolTipTagFragmentGenerator;


public class DynamicDriveToolTipTagFragmentGeneratorTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(DynamicDriveToolTipTagFragmentGeneratorTests.class);
    }

    
    public DynamicDriveToolTipTagFragmentGeneratorTests(String name) {
        super(name);
    }

    
    public void testGenerateURLFragment() {
        OverLIBToolTipTagFragmentGenerator g
                = new OverLIBToolTipTagFragmentGenerator();
        assertEquals(" onMouseOver=\"return overlib('abc');\""
                + " onMouseOut=\"return nd();\"",
                g.generateToolTipFragment("abc"));
        assertEquals(" onMouseOver=\"return overlib("
                + "'It\\'s \\\"A\\\", 100.0');\" onMouseOut=\"return nd();\"",
                g.generateToolTipFragment("It\'s \"A\", 100.0"));
    }

}
