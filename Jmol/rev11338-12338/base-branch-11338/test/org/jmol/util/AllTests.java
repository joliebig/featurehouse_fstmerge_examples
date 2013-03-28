

package org.jmol.util;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

  public static Test suite() {
    TestSuite suite = new TestSuite("Test for org.jmol.util");
    
    suite.addTestSuite(TestCommandHistory.class);
    suite.addTestSuite(TestIntInt2ObjHash.class);
    suite.addTestSuite(TestInt2ObjHash.class);
    
    return suite;
  }

}
