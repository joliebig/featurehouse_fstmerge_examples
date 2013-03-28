

package org.jmol;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

  public static Test suite() {
    TestSuite suite = new TestSuite("Test for org.jmol");
    
    suite.addTest(org.jmol.adapter.smarter.TestSmarterJmolAdapter.suite());
    suite.addTest(org.jmol.api.TestScripts.suite());
    suite.addTestSuite(org.jmol.smiles.TestSmilesParser.class);
    suite.addTest(org.jmol.util.AllTests.suite());
    
    return suite;
  }

}
