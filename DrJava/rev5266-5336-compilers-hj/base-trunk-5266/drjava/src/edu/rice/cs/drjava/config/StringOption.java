

package edu.rice.cs.drjava.config;


public class StringOption extends Option<String> {
  
  public StringOption(String key, String def) { super(key, def); }
  
  
  
  
  
  public String parse(String s) { return s; }
  
  
  public String format(String s) { return s; }
}
