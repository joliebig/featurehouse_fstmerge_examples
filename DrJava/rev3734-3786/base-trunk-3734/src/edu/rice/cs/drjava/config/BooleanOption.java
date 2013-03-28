

package edu.rice.cs.drjava.config;


public class BooleanOption extends Option<Boolean> {
  
  
  public BooleanOption(String key, Boolean def) { super(key,def); }
  
  
  public Boolean parse(String s) {
    s= s.trim().toLowerCase();
    if (s.equals("true")) return Boolean.TRUE;
    if (s.equals("false")) return Boolean.FALSE;
    else throw new OptionParseException(name, s, "Must be a String representing a boolean value.");
  }
}
