

package edu.rice.cs.drjava.config;

import java.util.Vector;
import java.util.StringTokenizer;

public class VectorOption<T> extends Option<Vector<T>> {

  protected ParseStrategy<T> parser;
  protected FormatStrategy<T> formatter;
  public final String header;
  public final char delim;
  public final String footer;

  
  private VectorOption(String key, ParseStrategy<T> parser, FormatStrategy<T> formatter,
                       String header, char delim, String footer, Vector<T> def) {
    super(key,def);
    this.parser = parser;
    this.formatter = formatter;
    this.header = header;
    this.delim = delim;
    this.footer = footer;
  }

  public VectorOption(String key, Option<T> strategy, String header,
                      char delim, String footer, Vector<T> def) {
    this(key, strategy, strategy, header, delim, footer,def);
  }

  
  public VectorOption(String key, Option<T> option, Vector<T> def) {
    this(key,option,option,"[",',',"]",def);
  }

  
  public Vector<T> parse(String s) {
    s= s.trim();
    int startFirstElement = header.length();
    int startFooter = s.length() - footer.length();

    if (startFooter < startFirstElement ||
        !s.startsWith(header) ||
        !s.endsWith(footer)) {
      throw new OptionParseException(name, s,
                                     "Value must start with "+header+" and end "+
                                     "with "+footer+" to be a valid vector.");
    }
    s = s.substring(startFirstElement, startFooter);
    String d = String.valueOf(delim);
    StringTokenizer st = new StringTokenizer(s,d,true);

    Vector<T> res = new Vector<T>();
    boolean sawDelim = st.hasMoreTokens();

    while(st.hasMoreTokens()) {
      String token = st.nextToken();
      boolean isDelim = token.equals(d);

      if (!isDelim) {
        res.add(parser.parse(token));
      } else if (sawDelim) { 
        throw new OptionParseException(name, s,
                                       "Argument contains delimiter with no preceding list element.");
      }
      sawDelim = isDelim;
    }
    if (sawDelim) {
      throw new OptionParseException(name, s,
                                     "Value shouldn't end with a delimiter.");
    }
    return res;
  }

  
  public String format(Vector<T> v) {
    StringBuffer res = new StringBuffer(header);

    int size = v.size();
    int i = 0;
    while (i < size) {
      res.append(formatter.format(v.get(i)));
      i++;
      if (i < size) res.append(delim);
    }
    return res.append(footer).toString();
  }


}

