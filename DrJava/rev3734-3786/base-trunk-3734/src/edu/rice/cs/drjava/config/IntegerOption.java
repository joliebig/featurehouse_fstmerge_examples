

package edu.rice.cs.drjava.config;


public class IntegerOption extends Option<Integer>
{
  
  public IntegerOption(String key, Integer def) {
    super(key,def);
  }

  
  public Integer parse(String s) {
    try {
      return new Integer(Integer.parseInt(s));
    }
    catch (NumberFormatException e) {
      throw new OptionParseException(name, s, "Must be a valid integer value.");
    }
  }
}
