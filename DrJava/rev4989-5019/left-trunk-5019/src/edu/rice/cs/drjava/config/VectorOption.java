

package edu.rice.cs.drjava.config;

import java.util.Vector;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.io.IOException;


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
    Vector<T> res = new Vector<T>();
    if (s.equals("")) { return res; }

    int startFirstElement = header.length();
    int startFooter = s.length() - footer.length();

    if (!s.startsWith(header) && !s.endsWith(footer)) {
      
      res.add(parser.parse(s));
      return res;
    }
    if (startFooter < startFirstElement || !s.startsWith(header) || ! s.endsWith(footer)) {
      throw new OptionParseException(name, s, "Value must start with " + header + " and end " + "with " + footer + 
                                     " to be a valid vector.");
    }
    s = s.substring(startFirstElement, startFooter);
    if (s.equals("")) {
      res.add(parser.parse(""));
      return res;
    }
    


    StreamTokenizer st = new StreamTokenizer(new StringReader(s));
    st.resetSyntax();
    st.wordChars(0,255);
    st.ordinaryChar('|');
    st.ordinaryChar(delim);
    try {
      int tok = st.nextToken();
      int prevtok = -4;
      StringBuilder sb = new StringBuilder();
      while (tok!=StreamTokenizer.TT_EOF) {
        if (tok=='|') {
          if (prevtok=='|') {
            
            sb.append('|');
            prevtok = tok = -4;
          }
          else {
            
            prevtok = tok;
          }
        }
        else if (tok==delim) {
          if (prevtok=='|') {
            
            
            sb.append(delim);
            prevtok = tok = -4;
          }
          else {
            
            res.add(parser.parse(sb.toString()));
            sb.setLength(0); 
            prevtok = tok;
          }
        }
        else {
          
          if (prevtok=='|') {
            
            
            throw new OptionParseException(name, s, "A pipe | was discovered before the token '" + st.sval +
                                           "'. A pipe is only allowed in front of another pipe " +
                                           "or the delimiter "+delim+".");
          }
          sb.append(st.sval);
          prevtok = tok;
        }
        
        tok = st.nextToken();
      }
      
      res.add(parser.parse(sb.toString()));      
    }
    catch(IOException ioe) {
      throw new OptionParseException(name, s, "An IOException occurred while parsing a vector.");
    }

    return res;
  }

  
  public String format(Vector<T> v) {
    if (v.size() == 0) { return ""; }
    

    final StringBuilder res = new StringBuilder(header);

    int size = v.size();
    int i = 0;
    while (i < size) {
      String str = formatter.format(v.get(i));
      str = str.replaceAll("\\|","||");
      str = str.replaceAll(",","|,");
      res.append(str);
      i++;
      if (i < size) res.append(delim);
    }
    String str = res.append(footer).toString();
    return str;
  }
}

