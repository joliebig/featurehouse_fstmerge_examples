
package test.net.sourceforge.pmd.rules.typeresolution;

import org.junit.Before;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class CloneMethodMustImplementCloneableTest extends SimpleAggregatorTst {

    @Before
	public void setUp() {
		addRule("typeresolution", "CloneMethodMustImplementCloneable");
	}

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(CloneMethodMustImplementCloneableTest.class);
    }
}
