

package edu.rice.cs.drjava;

import junit.framework.TestCase;
import edu.rice.cs.util.swing.Utilities;


public class DrJavaTestCase extends TestCase {
  
  public DrJavaTestCase() {
    super();
  }

  
  public DrJavaTestCase(String name) {
    super(name);
  }

  
  protected void setUp() throws Exception {
    super.setUp();
    Utilities.TextAreaMessageDialog.TEST_MODE = true;
  }

  
  protected void tearDown() throws Exception {
    super.tearDown();
  }
}
