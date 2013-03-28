

package edu.rice.cs.drjava.model;

import java.io.File;

import edu.rice.cs.drjava.DrJavaTestCase;


public class DummyGlobalModelTest extends DrJavaTestCase {
  
  
  public void testGetDocumentForFile() throws java.io.IOException {
    DummyGlobalModel dummy = new DummyGlobalModel();
    try { dummy.getDocumentForFile(new File("")); }
    catch (UnsupportedOperationException e) {
      assertTrue("This message should never be seen", true);
      return;
    }
    fail("expected that UnsupportedOperationException is thrown");
  }
  
  
  public void testIsAlreadyOpen() throws java.io.IOException {
    DummyGlobalModel dummy = new DummyGlobalModel();
    try { dummy.getDocumentForFile(new File("")); }
    catch (UnsupportedOperationException e) {
      assertTrue("This message should never be seen", true);
      return;
    }
    fail("expected that UnsupportedOperationException is thrown");
  }
  
  
  
  public void testGetDefinitionsDocuments() {
    DummyGlobalModel dummy = new DummyGlobalModel();
    try { dummy.getOpenDefinitionsDocuments(); }
    catch (UnsupportedOperationException e) {
      assertTrue("This message should never be seen", true);
      return;
    }
    fail("expected that UnsupportedOperationException is thrown");
  }
  
  
  
  public void testHasModifiedDocuments() {
    DummyGlobalModel dummy = new DummyGlobalModel();
    try { dummy.hasModifiedDocuments(); }
    catch (UnsupportedOperationException e) {
      assertTrue("This message should never be seen", true);
      return;
    }
    fail("expected that UnsupportedOperationException is thrown");
  }
}
