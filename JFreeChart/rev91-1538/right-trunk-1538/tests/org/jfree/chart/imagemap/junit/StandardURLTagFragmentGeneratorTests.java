

package org.jfree.chart.imagemap.junit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.imagemap.StandardURLTagFragmentGenerator;


public class StandardURLTagFragmentGeneratorTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(StandardURLTagFragmentGeneratorTests.class);
    }

    
    public StandardURLTagFragmentGeneratorTests(String name) {
        super(name);
    }

    
    public void testGenerateURLFragment() {
        StandardURLTagFragmentGenerator g
                = new StandardURLTagFragmentGenerator();
        assertEquals(" href=\"abc\"", g.generateURLFragment("abc"));
        assertEquals(" href=\"images/abc.png\"",
                g.generateURLFragment("images/abc.png"));
        assertEquals(" href=\"http://www.jfree.org/images/abc.png\"",
                g.generateURLFragment("http://www.jfree.org/images/abc.png"));
    }

}
