

package edu.rice.cs.drjava.config;

import edu.rice.cs.drjava.platform.PlatformFactory;
import edu.rice.cs.util.UnexpectedException;
import java.lang.reflect.Field;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.awt.Event;
import java.util.HashMap;


public class KeyStrokeOption extends Option<KeyStroke> {
  
  
  static HashMap<Integer, String> keys = new HashMap<Integer, String>();
  public static final KeyStroke NULL_KEYSTROKE = KeyStroke.getKeyStroke(0, 0);
  
  public KeyStrokeOption(String key, KeyStroke def) { super(key,def); }
  
  
  
  
  static {
    try {
      Field[] fields = KeyEvent.class.getFields();
      for (int i = 0; i < fields.length; i++) {
        Field currfield = fields[i];
        String name = currfield.getName();
        if (name.startsWith("VK_")) {
          keys.put(Integer.valueOf(currfield.getInt(null)), name.substring(3));
        }
      }
    }
    catch(IllegalAccessException iae) {
      throw new UnexpectedException(iae);
    }
  }
  
  
  
  public KeyStroke parse(String s) {
    if (s.equals("<none>")) { return NULL_KEYSTROKE; }
    
    
    int cIndex = s.indexOf("command");
    if (cIndex > -1) {
      final StringBuilder sb = new StringBuilder(s.substring(0, cIndex));
      sb.append("meta");
      sb.append(s.substring(cIndex + "command".length(), s.length()));
      s = sb.toString();
    }
    
    
    int oIndex = s.indexOf("option");
    if (oIndex > -1) {
      final StringBuilder sb = new StringBuilder(s.substring(0, oIndex));
      sb.append("alt");
      sb.append(s.substring(oIndex + "option".length(), s.length()));
      s = sb.toString();
    }
    
    KeyStroke ks = KeyStroke.getKeyStroke(s);
    if (ks == null) {
      throw new OptionParseException(name, s, "Must be a valid string representation of a Keystroke.");
    }
    return ks;
  }
  
  
  public String format(KeyStroke k) { return formatKeyStroke(k); }
  
  
  public static String formatKeyStroke(KeyStroke k) {
    if (k == NULL_KEYSTROKE) {
      return "<none>";
    }
    
    
    
    
    
    
    int modifiers = k.getModifiers();
    boolean isMac = PlatformFactory.ONLY.isMacPlatform();
    final StringBuilder buf = new StringBuilder();
    if ((modifiers & Event.META_MASK) > 0) {
      String meta = (! isMac) ? "meta " : "command ";
      buf.append(meta);
    }
    if ((modifiers & Event.CTRL_MASK) > 0) {
      buf.append("ctrl ");
    }
    if ((modifiers & Event.ALT_MASK) > 0) {
      String alt = (!isMac) ? "alt " : "option ";
      buf.append(alt);
    }
    if ((modifiers & Event.SHIFT_MASK) > 0) {
      buf.append("shift ");
    }
    
    
    if (k.getKeyCode() == KeyEvent.VK_UNDEFINED) {
      buf.append("typed ");
      buf.append(k.getKeyChar());
    }
    
    else {
      
      if (k.isOnKeyRelease()) {
        buf.append("released ");
      }
      String key = keys.get(Integer.valueOf(k.getKeyCode()));
      if (key == null) {
        throw new IllegalArgumentException("Invalid keystroke");
      }
      if (key.equals("CONTROL") || key.equals("ALT") || key.equals("META") ||
          key.equals("SHIFT") || key.equals("ALT_GRAPH")) {
        return buf.toString();
      }
      else {
        buf.append(key);
        return buf.toString();
      }
    }
    return buf.toString();
  }
}
