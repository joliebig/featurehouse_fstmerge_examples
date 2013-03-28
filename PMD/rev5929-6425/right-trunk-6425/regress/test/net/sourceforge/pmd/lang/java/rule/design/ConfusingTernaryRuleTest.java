package test.net.sourceforge.pmd.lang.java.rule.design;

import org.junit.Before;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class ConfusingTernaryRuleTest extends SimpleAggregatorTst {

    @Before
    public void setUp() {
        addRule("design", "ConfusingTernary");
    }

    

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(ConfusingTernaryRuleTest.class);
    }
}
