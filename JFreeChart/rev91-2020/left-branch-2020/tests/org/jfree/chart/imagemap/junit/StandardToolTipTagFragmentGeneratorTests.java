

package org.jfree.chart.imagemap.junit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.imagemap.StandardToolTipTagFragmentGenerator;


public class StandardToolTipTagFragmentGeneratorTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(StandardToolTipTagFragmentGeneratorTests.class);
    }

    
    public StandardToolTipTagFragmentGeneratorTests(String name) {
        super(name);
    }

    
    public void testGenerateURLFragment() {
        StandardToolTipTagFragmentGenerator g
                = new StandardToolTipTagFragmentGenerator();
        assertEquals(" title=\"abc\" alt=\"\"",
                g.generateToolTipFragment("abc"));
        assertEquals(" title=\"Series &quot;A&quot;, 100.0\" alt=\"\"",
                g.generateToolTipFragment("Series \"A\", 100.0"));
    }

}
