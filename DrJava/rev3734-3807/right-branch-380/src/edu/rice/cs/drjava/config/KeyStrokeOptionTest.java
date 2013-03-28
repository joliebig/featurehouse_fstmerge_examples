

package edu.rice.cs.drjava.config;

import edu.rice.cs.drjava.DrJavaTestCase;
import edu.rice.cs.util.newjvm.ExecJVM;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Locale;


public final class KeyStrokeOptionTest extends DrJavaTestCase
{
  
  public KeyStrokeOptionTest(String name) { super(name); }

  public void testGetName()
  {
    KeyStrokeOption io1 = new KeyStrokeOption("indent_size",null);
    KeyStrokeOption io2 = new KeyStrokeOption("max_files",null);

    assertEquals("indent_size", io1.getName());
    assertEquals("max_files",   io2.getName());
  }

  public void testParse()
  {
    KeyStrokeOption io = new KeyStrokeOption("max_files",null);
    assertEquals(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,
                                        InputEvent.CTRL_MASK),
                 io.parse("ctrl ENTER"));
    assertEquals(KeyStrokeOption.NULL_KEYSTROKE,
                 io.parse("<none>"));
    assertEquals(KeyStroke.getKeyStroke(KeyEvent.VK_NUM_LOCK,
                                        InputEvent.ALT_MASK | InputEvent.SHIFT_MASK,
                                        true),
                 io.parse("alt shift released NUM_LOCK"));
    assertEquals(KeyStroke.getKeyStroke(KeyEvent.VK_COMMA,
                                        InputEvent.ALT_MASK | InputEvent.SHIFT_MASK,
                                        false),
                 io.parse("alt shift COMMA"));
    assertEquals(KeyStroke.getKeyStroke('%'),
                 io.parse("typed %"));
    
    

    try { io.parse("true"); fail(); }
    catch (IllegalArgumentException e) { }

    try { io.parse(".33"); fail(); }
    catch (IllegalArgumentException e) { }

    try { io.parse("Alt Z"); fail(); }
    catch (IllegalArgumentException e) { }

    try { io.parse("ctrl alt shift typed F1"); fail(); }
    catch (IllegalArgumentException e) { }
  }

  
  public void testFormat()
  {
    KeyStrokeOption io = new KeyStrokeOption("max_files",null);
    KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,
                                          InputEvent.ALT_MASK | InputEvent.META_MASK);
    assertEquals(ks, io.parse(io.format(ks)));
    ks = KeyStroke.getKeyStroke(KeyEvent.VK_NUMBER_SIGN,
                                InputEvent.ALT_MASK | InputEvent.CTRL_MASK);
    assertEquals(ks, io.parse(io.format(ks)));
    
    
    ks = KeyStroke.getKeyStroke('!');
    assertEquals(ks, io.parse(io.format(ks)));
    ks = KeyStroke.getKeyStroke(KeyEvent.VK_F10,
                                InputEvent.ALT_MASK | InputEvent.SHIFT_MASK,
                                true);
    assertEquals(ks, io.parse(io.format(ks)));
  }

  
  public void testLocaleSpecificFormat()
    throws IOException, InterruptedException
  {
    String className = "edu.rice.cs.drjava.config.KeyStrokeOptionTest";
    String[] args = new String[0];

    Process process = ExecJVM.runJVMPropagateClassPath(className, args, FileOption.NULL_FILE);
    int status = process.waitFor();
    assertEquals("Local specific keystroke test failed!",
                 0, status);
  }

  
  public static void main(String[] args) {
    
    Locale.setDefault(Locale.GERMAN);

    KeyStrokeOption io = new KeyStrokeOption("test",null);
    KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_A,
                                          InputEvent.ALT_MASK |
                                          InputEvent.CTRL_MASK |
                                          InputEvent.SHIFT_MASK |
                                          InputEvent.META_MASK);
    String s = io.format(ks);
    
    if ((s.indexOf("alt") == -1) && (s.indexOf("option") == -1)) {
      System.exit(1);
    }
    
    if (s.indexOf("ctrl") == -1) {
      System.exit(2);
    }
    
    if (s.indexOf("shift") == -1) {
      System.exit(3);
    }
    
    if ((s.indexOf("meta") == -1) && (s.indexOf("command") == -1)) {
      System.exit(4);
    }

    
    System.exit(0);
  }

}
