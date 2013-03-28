

package edu.rice.cs.drjava.config;

import edu.rice.cs.drjava.DrJavaTestCase;

import java.awt.*;


public final class ColorOptionTest extends DrJavaTestCase
{
  
  public ColorOptionTest(String name) { super(name); }
  
  public void testGetName()
  {
    ColorOption io1 = new ColorOption("indent_size",null);
    ColorOption io2 = new ColorOption("max_files",null);
    
    assertEquals("indent_size", io1.getName());
    assertEquals("max_files",   io2.getName());
  }
  
  public void testParse()
  {
    ColorOption io = new ColorOption("max_files",null);
    
    assertEquals(Color.black, io.parse("0x000000"));
    assertEquals(Color.green, io.parse("0x00ff00"));
    
    try { io.parse("true"); fail(); }
    catch (OptionParseException e) { }
    
    try { io.parse("black"); fail(); }
    catch (OptionParseException e) { }
  }
  
 
  public void testFormat()
  {
    ColorOption io1 = new ColorOption("max_files",null);
    ColorOption io2 = new ColorOption("indent_size",null);
    
    assertEquals("#000000",  io1.format(Color.black));
    assertEquals("#ff00ff",  io2.format(Color.magenta));
    assertEquals("#ffffff", io1.format(Color.white));

    ColorOption c = new ColorOption("blue", Color.blue);
    assertEquals("testFormat:", "#000000", c.format(Color.black));
    assertEquals("testFormat:", "#0000ff", c.format(Color.blue));
    assertEquals("testFormat:", "#00ff00", c.format(Color.green));
    assertEquals("testFormat:", "#ff0000", c.format(Color.red));  
  }
}
