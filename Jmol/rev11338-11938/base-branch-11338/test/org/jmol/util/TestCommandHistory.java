

package org.jmol.util;

import junit.framework.TestCase;


public class TestCommandHistory extends TestCase {

  
  public TestCommandHistory(String arg0) {
    super(arg0);
  }

  
  public void testHistory() {

    
    CommandHistory h = new CommandHistory(4);
    h.addCommand("x");
    h.addCommand("y");
    h.addCommand("z");
    h.addCommand("a");
    h.addCommand("b");
    h.addCommand("c");
    h.addCommand("d");

    
    assertEquals("d", h.getCommandUp());
    assertEquals("c", h.getCommandUp());
    assertEquals("b", h.getCommandUp());
    assertEquals("a", h.getCommandUp());
    assertEquals(null, h.getCommandUp());

    
    assertEquals("b", h.getCommandDown());
    assertEquals("c", h.getCommandDown());
    assertEquals("d", h.getCommandDown());
    assertEquals("" , h.getCommandDown());
    assertEquals(null, h.getCommandDown());

    
    h.setMaxSize(2);

    
    assertEquals("d", h.getCommandUp());
    assertEquals("c", h.getCommandUp());
    assertEquals(null, h.getCommandUp());
    assertEquals(null, h.getCommandUp());
    assertEquals(null, h.getCommandUp());

    
    assertEquals("d", h.getCommandDown());
    assertEquals("" , h.getCommandDown());
    assertEquals(null, h.getCommandDown());

    
    h.setMaxSize(4);

    
    assertEquals("d", h.getCommandUp());
    assertEquals("c", h.getCommandUp());
    assertEquals(null, h.getCommandUp());
    assertEquals(null, h.getCommandUp());
    assertEquals(null, h.getCommandUp());
    
    h.addCommand("e");
    h.addCommand("f");

    
    assertEquals("f", h.getCommandUp());
    assertEquals("e", h.getCommandUp());
    assertEquals("d", h.getCommandUp());
    assertEquals("c", h.getCommandUp());
    assertEquals(null, h.getCommandUp());

    
    assertEquals("d", h.getCommandDown());
    assertEquals("e", h.getCommandDown());
    assertEquals("f", h.getCommandDown());
    assertEquals("" , h.getCommandDown());
    assertEquals(null, h.getCommandDown());
  }
}
