

package edu.rice.cs.drjava.config;

import edu.rice.cs.drjava.DrJavaTestCase;


public final class BooleanOptionTest extends DrJavaTestCase
{
  
  public BooleanOptionTest(String name) { super(name); }
  
  public void testGetName()
  {
    BooleanOption bo1 = new BooleanOption("enable JUnit",null);
    BooleanOption bo2 = new BooleanOption("use menu icons",null);
    
    assertEquals("enable JUnit", bo1.getName());
    assertEquals("use menu icons",   bo2.getName());
  }
  
  public void testParse()
  {
    BooleanOption bo = new BooleanOption("enable JUnit",null);
    
    assertEquals(Boolean.TRUE, bo.parse("true"));
    assertEquals(Boolean.FALSE, bo.parse("false"));
    assertEquals(Boolean.FALSE, bo.parse(" faLse "));
    
    try { bo.parse("3"); fail(); }
    catch (OptionParseException e) { }
    
    try { bo.parse("Tue"); fail(); }
    catch (OptionParseException e) { }
  }
  
  public void testFormat()
  {
    BooleanOption bo1 = new BooleanOption("max_files",null);
    BooleanOption bo2 = new BooleanOption("indent_size",null);
    
    assertEquals("true",  bo1.format(Boolean.TRUE));
    assertEquals("true",  bo2.format(Boolean.TRUE));
    assertEquals("false", bo1.format(Boolean.FALSE));
    assertEquals("false", bo2.format(Boolean.FALSE));
  }
}
