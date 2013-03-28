

package edu.rice.cs.drjava.config;
import java.util.Collection;
import java.util.Iterator;



public class ForcedChoiceOption extends Option<String>
{
  private Collection<String> _choices;

  
  public ForcedChoiceOption(String key, String def, Collection<String> choices) {
    super(key,def);
    _choices = choices;
  }

  
  public boolean isLegal(String s) {
    return _choices.contains(s);
  }

  
  public Iterator<String> getLegalValues() {
    return _choices.iterator();
  }

  
  public int getNumValues() {
    return _choices.size();
  }

  
  public String parse(String s)
  {
    if (isLegal(s)) {
      return s;
    }
    else {
      throw new OptionParseException(name, s, "Value is not an acceptable choice for this option.");
    }
  }

  
  public String format(String s) {
    return s;
  }
}
