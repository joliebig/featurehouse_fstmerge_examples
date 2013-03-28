

package org.jfree.chart.block.junit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class BlockPackageTests extends TestCase {

    
    public static Test suite() {
        TestSuite suite = new TestSuite("org.jfree.chart.block");
        suite.addTestSuite(AbstractBlockTests.class);
        suite.addTestSuite(BlockBorderTests.class);
        suite.addTestSuite(BlockContainerTests.class);
        suite.addTestSuite(BorderArrangementTests.class);
        suite.addTestSuite(ColorBlockTests.class);
        suite.addTestSuite(ColumnArrangementTests.class);
        suite.addTestSuite(EmptyBlockTests.class);
        suite.addTestSuite(FlowArrangementTests.class);
        suite.addTestSuite(GridArrangementTests.class);
        suite.addTestSuite(LabelBlockTests.class);
        suite.addTestSuite(LineBorderTests.class);
        suite.addTestSuite(RectangleConstraintTests.class);
        return suite;
    }

    
    public BlockPackageTests(String name) {
        super(name);
    }
    
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

}

