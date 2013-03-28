
package test.net.sourceforge.pmd.lang.java.rule;

import org.junit.Before;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class ScratchpadRulesTest extends SimpleAggregatorTst {

    @Before
    public void setUp() {
        
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(ScratchpadRulesTest.class);
    }

}
