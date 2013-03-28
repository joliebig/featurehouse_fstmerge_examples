

package edu.rice.cs.drjava.config;

import java.awt.Font;


public class FontOption extends Option<Font> {

  public FontOption(String key, Font def) { super(key,def); }

  
  public Font parse(String s) {
    String newS = s;
    int idx = newS.indexOf("PLAIN-");
    while (idx != -1) {
      newS = newS.substring(0, idx) + newS.substring(idx + 6);
      idx = newS.indexOf("PLAIN-");
    }
    return Font.decode(newS);  
  }

  
  public String format(Font f) {
    final StringBuilder str = new StringBuilder(f.getName());
    str.append("-");
    if (f.isBold()) {
      str.append("BOLD");
    }
    if (f.isItalic()) {
      str.append("ITALIC");
    }



    if (! f.isPlain()) {
      str.append("-");
    }
    str.append(f.getSize());

    return str.toString();
  }
}