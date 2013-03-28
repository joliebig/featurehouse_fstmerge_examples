
package edu.rice.cs.drjava.model;
import edu.rice.cs.drjava.DrJavaTestCase;



public class DummyOpenDefDocTest extends DrJavaTestCase {
  
  
  
  public void testProperExceptionThrowing() {
    try {
      DummyOpenDefDoc dummy = new DummyOpenDefDoc();
      dummy.modifiedOnDisk();
      fail("DummyOpenDefDoc did not throw UnsupportedOperationException");
    }
    catch (UnsupportedOperationException e) {
      
    }
    catch(Exception e) {
      fail("DummyOpenDefDoc did not throw UnsupportedOperationException, but " + e);
    }
  }
}
