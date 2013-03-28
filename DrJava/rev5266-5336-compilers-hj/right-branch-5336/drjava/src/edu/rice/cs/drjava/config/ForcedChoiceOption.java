

package edu.rice.cs.drjava.config;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;



public class ForcedChoiceOption extends Option<String>
{
  private Collection<String> _choices;
  private Collection<String> _deprecated; 

  
  public ForcedChoiceOption(String key, String def, Collection<String> choices) {
    this(key, def, choices, Arrays.asList(new String[0]));
  }

  
  public ForcedChoiceOption(String key, String def, Collection<String> choices,
                            Collection<String> deprecated) {
    super(key,def);
    _choices = choices;
    _deprecated = deprecated;
  }

  
  public boolean isLegal(String s) {
    return _choices.contains(s);
  }

  
  public boolean isDeprecated(String s) {
    return _deprecated.contains(s);
  }

  
  public Iterator<String> getLegalValues() {
    return _choices.iterator();
  }

  
  public Iterator<String> getDeprecatedValues() {
    return _deprecated.iterator();
  }
  
  
  public int getNumValues() {
    return _choices.size();
  }

  
  public String parse(String s)
  {
    if (isLegal(s)) {
      return s;
    }
    else if (isDeprecated(s)) {
      return defaultValue;
    }
    else {
      throw new OptionParseException(name, s, "Value is not an acceptable choice for this option.");
    }
  }

  
  public String format(String s) {
    return s;
  }
}
