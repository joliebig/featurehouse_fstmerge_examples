

package edu.rice.cs.drjava.config;


public class OptionParseException extends IllegalArgumentException {
  
  public String key;
  public String value;
  public String message;
  
  
  public OptionParseException(String key, String value, String message) {
    this.key = key;
    this.value = value;
    this.message = message;
  }
  
  
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("Could not parse configuration option.\nOption: ");
    sb.append(key);
    sb.append("\nGiven value: \"");
    sb.append(value);
    sb.append("\"\n");
    sb.append(message);
    return sb.toString();
  }
  
}