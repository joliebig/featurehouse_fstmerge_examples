

package koala.dynamicjava.util;

import java.util.*;



public class LocalizedMessageReader {
  
  private final static char ESCAPE_CHAR = '%';
  
  
  private ResourceBundle bundle;
  
  
  public LocalizedMessageReader(String name) {
    bundle = ResourceBundle.getBundle(name);
  }
  
  
  public String getMessage(String key, String[] strings) {
    String rawMessage = bundle.getString(key);
    String result = "";
    
    if (rawMessage != null) {
      for (int i = 0; i < rawMessage.length(); i++) {
        char c = rawMessage.charAt(i);
        if (c == ESCAPE_CHAR) {
          c = rawMessage.charAt(++i);
          if (c == ESCAPE_CHAR) {
            result += c;
          } else {
            String numb = "";
            do {
              if (!Character.isDigit(c = rawMessage.charAt(i))) {
                i--;
                break;
              }
              numb += c;
            } while (++i < rawMessage.length());
            int n = Integer.parseInt(numb);
            if (n >= strings.length) {
              throw new IllegalArgumentException("Missing argument " + n + " for error code " + key);
            }
            result += strings[n];
          }
        } else {
          result += c;
        }
      }
    }
    return result;
  }
}
