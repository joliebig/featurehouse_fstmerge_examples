
package genj.util;

import java.util.ArrayList;


public class DirectAccessTokenizer {
  
  private String string, separator;
  private int from, to;
  private int index;
  private boolean skipEmpty;
  
  
  public DirectAccessTokenizer(String string, String separator) {
    this(string, separator, false);
  }
  public DirectAccessTokenizer(String string, String separator, boolean skipEmpty) {
    this.skipEmpty = skipEmpty;
    this.string = string;
    this.separator = separator;
    from = 0;
    to = from-separator.length();
    index = 0;
  }
  
  
  public String[] getTokens() {
    return getTokens(false);
  }
  
  
  public String[] getTokens(boolean trim) {
    ArrayList result = new ArrayList();
    for (int i=0;;i++) {
      String token = get(i, trim);
      if (token==null) break;
      result.add(token);
    }
    return (String[])result.toArray(new String[result.size()]);
  }
  
  
  public int count() {
    int result = 0;
    for (int i=0;;i++) {
      if (get(i)==null) break;
      result++;
    }
    return result;
  }
  
  
  public int getStart() {
    return from;
  }
  
  
  public int getEnd() {
    return to;
  }
  
  
  public String getSubstring(int pos) {
    
    if (get(pos)==null)
      return "";
    
    return string.substring(getStart());
  }
  
  
  public String get(int pos) {
    return get(pos, false);
  }
  
  
  public String get(int pos, boolean trim) {
    
    
    if (pos<0)
      return null;
    
    
    if (pos<index) {
      from = 0;
      to = from-separator.length();
      index = 0;
    }
    
    
    while (index<=pos) {
      
      
      from = to+separator.length();
      
      
      if (from>string.length())
        return null;
      
      
      to = string.indexOf(separator, from);
      
      
      if (to<0) 
        to = string.length();
      
      
      if (!skipEmpty||to>from)
        index++;
    }
    
    
    String result = string.substring(from, to);
    return trim ? result.trim() : result;
  }
  
  
  public String toString() {
    return string.replaceAll(separator, ", ");
  }
  
}

