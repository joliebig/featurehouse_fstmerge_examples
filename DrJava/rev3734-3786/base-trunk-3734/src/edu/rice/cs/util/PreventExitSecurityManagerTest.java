

package edu.rice.cs.util;

import edu.rice.cs.drjava.DrJavaTestCase;


public class PreventExitSecurityManagerTest extends DrJavaTestCase {
  private PreventExitSecurityManager _manager;

  
  public void setUp() throws Exception {
    super.setUp();
    _manager = PreventExitSecurityManager.activate();
  }

  
  public void tearDown() throws Exception {
    _manager.deactivate();
    super.tearDown();
  }

  public void testSystemExitPrevented() {
    try {
      System.exit(1);
      fail("System.exit passed?!");
    }
    catch (ExitingNotAllowedException se) {
      
    }
  }

  public void testExitVMRespectsBlock() {
    _manager.setBlockExit(true);
    try {
      _manager.exitVM(-1);
      fail("exitVM passed while blocked!");
    }
    catch (ExitingNotAllowedException se) {
      
    }
  }

  public void testCanNotChangeSecurityManager() {
    try {
      System.setSecurityManager(null);
      fail("setSecurityManager passed!");
    }
    catch (SecurityException se) {
      
    }
  }
}
