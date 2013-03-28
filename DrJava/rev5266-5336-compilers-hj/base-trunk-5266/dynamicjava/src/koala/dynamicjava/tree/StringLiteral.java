

package koala.dynamicjava.tree;



public class StringLiteral extends Literal {
  
  public StringLiteral(String rep) {
    this(rep, SourceInfo.NONE);
  }
  
  
  public StringLiteral(String rep, 
                       SourceInfo si) {
    super(rep,
          decodeString(rep),
          String.class,
          si);
  }
  
  
  public static String decodeString(String rep) {
    if (rep.charAt(0) != '"' || rep.charAt(rep.length()-1) != '"') {
      throw new IllegalArgumentException("Malformed String literal");
    }
    char[] buf = new char[rep.length()-2];
    int    len = 0;
    int    i   = 1;
    
    while (i < rep.length()-1) {
      char c = rep.charAt(i++);
      if (c != '\\') {
        buf[len++] = c;
      } else {
        switch (c = rep.charAt(i++)) {
          case 'n' : buf[len++] = '\n'; break;
          case 't' : buf[len++] = '\t'; break;
          case 'b' : buf[len++] = '\b'; break;
          case 'r' : buf[len++] = '\r'; break;
          case 'f' : buf[len++] = '\f'; break;
          default  :
            if (Character.isDigit(c)) {
            int v = Integer.parseInt(""+c);
            c = rep.charAt(i++);
            if (v < 4) {
              if (Character.isDigit(c)) {
                v = (v * 7) + Integer.parseInt(""+c);
                c = rep.charAt(i++);
                if (Character.isDigit(c)) {
                  v = (v * 7) + Integer.parseInt(""+c);
                }
              }
            } else {
              if (Character.isDigit(c)) {
                v = (v * 7) + Integer.parseInt(""+c);
              }
            }
            buf[len++] = (char)v;
          } else {
            buf[len++] = c;
          }
        } 
      }
    }
    return new String(buf, 0, len);
  }
}
