

package edu.rice.cs.drjava.config;

import edu.rice.cs.drjava.DrJavaTestCase;


public final class LongOptionTest extends DrJavaTestCase {
  
  
  public LongOptionTest(String name) { super(name); }
   
  public void testGetName() {
    LongOption io1 = new LongOption("indent_size",null);
    LongOption io2 = new LongOption("max_files",null);
    
    assertEquals("indent_size", io1.getName());
    assertEquals("max_files",   io2.getName());
  }
  
  public void testParse() {
    LongOption io = new LongOption("max_files",null);
    
    assertEquals(new Long(Integer.MAX_VALUE+1), io.parse(new Long(Integer.MAX_VALUE+1).toString()));
    assertEquals(new Long(Integer.MIN_VALUE-1), io.parse(new Long(Integer.MIN_VALUE-1).toString()));
    
    try { io.parse("true"); fail(); }
    catch (OptionParseException e) { }
    
    try { io.parse(".33"); fail(); }
    catch (OptionParseException e) { }
  }
  
  public void testFormat() {
    LongOption io1 = new LongOption("max_files",null);
    LongOption io2 = new LongOption("indent_size",null);
    
    assertEquals(new Long(Integer.MAX_VALUE+1).toString(), io1.format(new Long(Integer.MAX_VALUE+1)));
    assertEquals(new Long(Integer.MAX_VALUE+1).toString(), io2.format(new Long(Integer.MAX_VALUE+1)));
    assertEquals(new Long(Integer.MIN_VALUE-1).toString(), io1.format(new Long(Integer.MIN_VALUE-1)));
    assertEquals(new Long(Integer.MIN_VALUE-1).toString(), io2.format(new Long(Integer.MIN_VALUE-1)));
  }
}
