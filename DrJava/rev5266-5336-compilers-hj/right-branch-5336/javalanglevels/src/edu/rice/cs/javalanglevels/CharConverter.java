

package edu.rice.cs.javalanglevels;


public abstract class CharConverter {
  
  public static String escapeChar(char c) {
    StringBuffer buf = new StringBuffer();
    escapeChar(c, buf);
    return buf.toString();
  }

  
  public static void escapeChar(char c, StringBuffer buf) {
    switch (c) {
      case '\n': buf.append("\\n"); break;
      case '\t': buf.append("\\t"); break;
      case '\b': buf.append("\\b"); break;
      case '\r': buf.append("\\r"); break;
      case '\f': buf.append("\\f"); break;
      case '\\': buf.append("\\\\"); break;
      case '\'': buf.append("\\\'"); break;
      case '\"': buf.append("\\\""); break;
      default:
        
        if ((c < 32) || (c > 127)) {
          String hex = Integer.toHexString(c);
          buf.append("\\u");

          for (int i = hex.length(); i < 4; i++) {
            buf.append('0');
          }

          buf.append(hex);
        }
        else {
          buf.append(c);
        }
        break;
    }
  }

  
  public static String escapeString(String s) {
    StringBuffer buf = new StringBuffer();

    for (int i = 0; i < s.length(); i++) {
      escapeChar(s.charAt(i), buf);
    }

    return buf.toString();
  }

  
  public static char unescapeChar(String in) {
    StringBuffer buf = new StringBuffer();
    int endPos = unescapeString(in, 0, buf);
    if (endPos < in.length()) {
      throw new IllegalArgumentException((in.length() - endPos) + " trailing" +
                                         " characters at the end of character"+
                                         " literal '" + in + "'");
    }

    return buf.charAt(0);
  }

  
  public static String unescapeString(String in) {
    
    if (in.length() == 0) {
      return in;
    }

    StringBuffer buf = new StringBuffer();
    int nextStart = 0;
    
    while (nextStart < in.length()) {
      nextStart = unescapeString(in, nextStart, buf);
    }

    return buf.toString();
  }

  

  
  public static int unescapeString(final String in,
                                   final int startPos,
                                   final StringBuffer out)
  {
    char first = in.charAt(startPos);

    if (first != '\\') {
      out.append(first);
      return startPos + 1;
    }

    char second = in.charAt(startPos + 1);

    switch (second) {
      case 'n': out.append('\n'); return startPos + 2;
      case 't': out.append('\t'); return startPos + 2;
      case 'b': out.append('\b'); return startPos + 2;
      case 'r': out.append('\r'); return startPos + 2;
      case 'f': out.append('\f'); return startPos + 2;
      case '\\': out.append('\\'); return startPos + 2;
      case '\'': out.append('\''); return startPos + 2;
      case '\"': out.append('\"'); return startPos + 2;
    }

    
    if (_isOctalDigit(second)) {
      
      
      int maxDigits;
      if (second < '4') {
        maxDigits = 3;
      }
      else {
        maxDigits = 2;
      }

      StringBuffer octal = new StringBuffer(maxDigits);
      octal.append(second);

      int nextDigitPos = startPos + 2;
      while ((octal.length() < maxDigits) && (nextDigitPos < in.length())) {
        char nextChar = in.charAt(nextDigitPos);
        if (_isOctalDigit(nextChar)) {
          octal.append(nextChar);
          nextDigitPos++;
        }
        else { 
          break;
        }
      }

      try {
        int charValue = Integer.parseInt(octal.toString(), 8);
        if ((charValue > Character.MAX_VALUE) || (charValue < Character.MIN_VALUE)) {
          throw new IllegalArgumentException("Octal escape beginning at " +
                                             "position " + startPos + " out of range: " + in);
        }

        out.append((char) charValue);
        return nextDigitPos;
      }
      catch (NumberFormatException e) {
        throw new RuntimeException("Impossible to occur, but number format exception in octal escape!");
      }
    }
    else {
      throw new IllegalArgumentException("Invalid escape sequence at position "+
                                         startPos + ": " + in);
    }
  }

  private static boolean _isOctalDigit(char c) {
    return ((c >= '0') && (c <= '7'));
  }
}
