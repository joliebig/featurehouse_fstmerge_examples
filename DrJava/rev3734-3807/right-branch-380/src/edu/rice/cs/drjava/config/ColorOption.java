

package edu.rice.cs.drjava.config;
import java.awt.*;


public class ColorOption extends Option<Color>{
  
  public ColorOption(String key, Color def) { super(key,def); }
  
  public Color parse(String s) {
    try { return Color.decode(s); }
    catch (NumberFormatException nfe) {
      throw new OptionParseException(name, s,
                                     "Must be a string that represents an " +
                                     "opaque color as a 24-bit integer.");
    }
  }
  
  public String format(Color c) {
    int len = 6; 
    String str = Integer.toHexString(c.getRGB() & 0xFFFFFF);
    StringBuffer buff = new StringBuffer(str);
    for (int i = 0; i < (len - str.length()); i++)  buff.insert(0, '0');
    buff.insert(0, '#');
    return buff.toString();
  }
}
