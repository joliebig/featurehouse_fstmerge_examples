
package test.net.sourceforge.pmd.lang.java.rule.design;

import org.junit.Before;
import org.junit.Test;

import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;


public class TooManyHttpFilterTest extends SimpleAggregatorTst {

     private Rule rule;
     private TestDescriptor[] tests;

     @Before
     public void setUp() {


     }

     @Test
     public void testDefault() {
 
     }
     public static junit.framework.Test suite() {
         return new junit.framework.JUnit4TestAdapter(TooManyHttpFilterTest.class);
     }
 }
