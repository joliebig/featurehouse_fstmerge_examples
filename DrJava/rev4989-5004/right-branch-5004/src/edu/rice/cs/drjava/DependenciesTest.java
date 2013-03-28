

package edu.rice.cs.drjava;

import junit.framework.*;


public final class DependenciesTest extends DrJavaTestCase {
  public static final String REQUIRED_UTIL_VERSION = "20040521-1616";

  
  public DependenciesTest(String name) {
    super(name);
  }
  
  
  public static Test suite() { return  new TestSuite(DependenciesTest.class); }

  
  public void testUtilVersion() throws Throwable {
    
  }
  
}
