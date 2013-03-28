

package edu.rice.cs.drjava.config;


public class NonNegativeIntegerOption extends IntegerOption
{
  
  public NonNegativeIntegerOption(String key, Integer def) { super(key,def); }

  
  public Integer parse(String s)
  {
    int value;
    try {
      value = Integer.parseInt(s);
      if (value < 0)
        throw new OptionParseException(name, s,
                                     "Must be a non-negative integer value.");
      return new Integer(value); }

    catch (NumberFormatException e)
    {
      throw new OptionParseException(name, s,
                                     "Must be a non-negative integer value.");
    }
  }
}