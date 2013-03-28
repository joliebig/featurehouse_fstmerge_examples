

package edu.rice.cs.drjava.config;


public class LongOption extends Option<Long> {
  
  
  public LongOption(String key, Long def) {
    super(key,def);
  }
  
  
  public Long parse(String s) {
    try { return Long.valueOf(Long.parseLong(s)); }
    catch (NumberFormatException e) {
      throw new OptionParseException(name, s, "Must be a valid long (integer) value.");
    }
  }
}
