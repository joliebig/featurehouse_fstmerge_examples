

package edu.rice.cs.drjava.model.debug.jpda;

import edu.rice.cs.drjava.model.debug.DebugTestCase;


public abstract class JPDADebugTestCase extends DebugTestCase {

  protected volatile JPDADebugger _debugger;

  public void setUp() throws Exception {
    super.setUp();
    
    _debugger = (JPDADebugger) super._debugger;
  }

}
