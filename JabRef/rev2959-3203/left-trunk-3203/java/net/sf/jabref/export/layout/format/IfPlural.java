
package net.sf.jabref.export.layout.format;

import net.sf.jabref.export.layout.AbstractParamLayoutFormatter;


public class IfPlural extends AbstractParamLayoutFormatter {
  
  protected String pluralText, singularText;
  
  public void setArgument(String arg) {
    String[] parts = parseArgument(arg);

    if (parts.length < 2)
        return; 
    pluralText = parts[0];
    singularText = parts[1];

}

public String format(String fieldText) {
    if (pluralText == null)
        return fieldText; 
    if (fieldText.matches(".*\\sand\\s.*"))
      return pluralText;
    else 
      return singularText;

}


}
