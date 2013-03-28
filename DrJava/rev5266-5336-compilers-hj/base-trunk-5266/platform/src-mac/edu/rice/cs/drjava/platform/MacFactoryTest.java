

package edu.rice.cs.drjava.platform;

import junit.framework.*;


public class MacFactoryTest extends TestCase {
  
  public void testFactory() {
    PlatformSupport ps = PlatformFactory.ONLY;
    String psClassName = ps.getClass().getName();
    assertEquals("PlatformFactory produced the appropriate PlatformSupport?",
                "edu.rice.cs.drjava.platform.MacPlatform",
                psClassName);
  }
}
