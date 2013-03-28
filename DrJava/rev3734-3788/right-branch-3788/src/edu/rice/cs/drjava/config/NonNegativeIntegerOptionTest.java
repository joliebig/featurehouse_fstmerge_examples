

package edu.rice.cs.drjava.config;

import edu.rice.cs.drjava.DrJavaTestCase;


public final class NonNegativeIntegerOptionTest extends DrJavaTestCase
{
  
  public NonNegativeIntegerOptionTest(String name) { super(name); }
  
  public void testGetName()
  {
    NonNegativeIntegerOption io1 = new NonNegativeIntegerOption("indent_size",null);
    NonNegativeIntegerOption io2 = new NonNegativeIntegerOption("max_files",null);
    
    assertEquals("indent_size", io1.getName());
    assertEquals("max_files",   io2.getName());
  }
  
  public void testParse()
  {
    NonNegativeIntegerOption io = new NonNegativeIntegerOption("max_files",null);
    
    assertEquals(new Integer(3), io.parse("3"));
    try { io.parse("-3"); fail(); }
    catch (OptionParseException e) { }
    
    try { io.parse("true"); fail(); }
    catch (OptionParseException e) { }
    
    try { io.parse(".33"); fail(); }
    catch (OptionParseException e) { }
  }
  
  public void testFormat()
  {
    NonNegativeIntegerOption io1 = new NonNegativeIntegerOption("max_files",null);
    NonNegativeIntegerOption io2 = new NonNegativeIntegerOption("indent_size",null);
    
    assertEquals("33",  io1.format(new Integer(33)));
    assertEquals("33",  io2.format(new Integer(33)));
    assertEquals("-11", io1.format(new Integer(-11)));
    assertEquals("-11", io2.format(new Integer(-11)));
  }
}
