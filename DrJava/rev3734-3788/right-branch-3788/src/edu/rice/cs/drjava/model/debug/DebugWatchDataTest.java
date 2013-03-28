



package edu.rice.cs.drjava.model.debug;

import edu.rice.cs.drjava.DrJavaTestCase;


public final class DebugWatchDataTest extends DrJavaTestCase {
 
  
  public void testFirstCreation() {
    DebugWatchData data = new DebugWatchData("foo");
    assertEquals("should have a name on startup",
                 "foo", data.getName());
    assertEquals("should have no value on startup",
                 "", data.getValue());
    assertEquals("should have no type on startup",
                 "", data.getType());
    assertTrue("should not be changed on startup", !data.isChanged());
  }
  
  
  public void testInScopeThenCleared() {
    DebugWatchData data = new DebugWatchData("foo");

    
    data.setValue(new Integer(7));
    data.setType("java.lang.Integer");
    assertEquals("should have a value", "7", data.getValue());
    assertEquals("should have a type", "java.lang.Integer", data.getType());
    assertTrue("should be changed", data.isChanged());
    
    
    data.hideValueAndType();
    assertEquals("should have no value after hide",
                 "", data.getValue());
    assertEquals("should have no type after hide",
                 "", data.getType());
    assertTrue("should not be changed after hide", !data.isChanged());
    
    
    data.setValue(new Integer(7));
    assertTrue("should not be changed after setting same value",
               !data.isChanged());
    
    
    data.setValue(new Integer(8));
    assertTrue("should be changed after setting different value",
               data.isChanged());
  }
  
  
  public void testNotInScope() {
    DebugWatchData data = new DebugWatchData("bar");
    data.setNoValue();
    data.setNoType();
    
    assertEquals("should not be in scope",
                 DebugWatchData.NO_VALUE, data.getValue());
    assertEquals("should not have a type",
                 DebugWatchData.NO_TYPE, data.getType());
    assertTrue("should not appear changed", !data.isChanged());
  }
  
  
}