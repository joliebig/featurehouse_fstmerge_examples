

package edu.rice.cs.drjava.config;

import edu.rice.cs.drjava.DrJavaTestCase;


public final class IntegerOptionTest extends DrJavaTestCase {
  
  
  public IntegerOptionTest(String name) { super(name); }
   
  public void testGetName() {
    IntegerOption io1 = new IntegerOption("indent_size",null);
    IntegerOption io2 = new IntegerOption("max_files",null);
    
    assertEquals("indent_size", io1.getName());
    assertEquals("max_files",   io2.getName());
  }
  
  public void testParse() {
    IntegerOption io = new IntegerOption("max_files",null);
    
    assertEquals(Integer.valueOf(3), io.parse("3"));
    assertEquals(Integer.valueOf(-3), io.parse("-3"));
    
    try { io.parse("true"); fail(); }
    catch (OptionParseException e) { }
    
    try { io.parse(".33"); fail(); }
    catch (OptionParseException e) { }
  }
  
  public void testFormat() {
    IntegerOption io1 = new IntegerOption("max_files",null);
    IntegerOption io2 = new IntegerOption("indent_size",null);
    
    assertEquals("33",  io1.format(Integer.valueOf(33)));
    assertEquals("33",  io2.format(Integer.valueOf(33)));
    assertEquals("-11", io1.format(Integer.valueOf(-11)));
    assertEquals("-11", io2.format(Integer.valueOf(-11)));
  }
}
