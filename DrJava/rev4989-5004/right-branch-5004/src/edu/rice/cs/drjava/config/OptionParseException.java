

package edu.rice.cs.drjava.config;


public class OptionParseException extends IllegalArgumentException {
  
  public String key;
  public String value;
  public String message;
  public OptionParseException[] causes; 
  
  
  public OptionParseException(String key, String value, String message) {
    this.key = key;
    this.value = value;
    this.message = message;
    this.causes = null;
  }
  
  public OptionParseException(OptionParseException[] causes) {
    this.key = this.value = this.message = null;
    this.causes = causes;
  }
  
  
  public String toString() {
    OptionParseException ope = this;
    if (causes!=null) {
      if (causes.length!=1) return "Could not parse configuration options.";
      ope = causes[0];
    }
    final StringBuilder sb = new StringBuilder();
    sb.append("Could not parse configuration option.\nOption: ");
    sb.append(ope.key);
    sb.append("\nGiven value: \"");
    sb.append(ope.value);
    sb.append("\"\n");
    sb.append(ope.message);
    return sb.toString();
  }
}