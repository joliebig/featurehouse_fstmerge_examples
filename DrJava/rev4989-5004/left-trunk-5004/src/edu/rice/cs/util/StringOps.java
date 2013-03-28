

package edu.rice.cs.util;

import edu.rice.cs.plt.tuple.Pair;
import edu.rice.cs.plt.lambda.Lambda2;
import edu.rice.cs.plt.lambda.Lambda;
import edu.rice.cs.drjava.config.*;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.io.IOException;



public abstract class StringOps {
  
  public static final String EOL = System.getProperty("line.separator");
  public static final String NEWLINE = "\n";
  public static final char newline = '\n';
  public static final char SEPARATOR = '\u';
  
   
  private static final String blank0 = "";
  private static final String blank1 = makeBlankString(1);
  private static final String blank2 = makeBlankString(2);
  private static final String blank3 = makeBlankString(3);
  private static final String blank4 = makeBlankString(4);
  private static final String blank5 = makeBlankString(5);
  private static final String blank6 = makeBlankString(6);
  private static final String blank7 = makeBlankString(7);
  private static final String blank8 = makeBlankString(8);
  private static final String blank9 = makeBlankString(9);
  private static final String blank10 = makeBlankString(10);
  private static final String blank11 = makeBlankString(11);
  private static final String blank12 = makeBlankString(12);
  private static final String blank13 = makeBlankString(13);
  private static final String blank14 = makeBlankString(14);
  private static final String blank15 = makeBlankString(15);
  private static final String blank16 = makeBlankString(16);
  
  
  public static String getBlankString(int n) {
    switch (n) {
      case 0: return blank0;
      case 1: return blank1;
      case 2: return blank2;
      case 3: return blank3;
      case 4: return blank4;
      case 5: return blank5;
      case 6: return blank6;
      case 7: return blank7;
      case 8: return blank8;
      case 9: return blank9;
      case 10: return blank10;
      case 11: return blank11;
      case 12: return blank12;
      case 13: return blank13;
      case 14: return blank14;
      case 15: return blank15;
      case 16: return blank16;
      default:
        return makeBlankString(n);
    }
  }
  
  
  private static String makeBlankString(int n) {
    StringBuilder buf = new StringBuilder();
    for (int i = 0; i < n; i++) buf.append(' ');
    return buf.toString();
  }
  
  
  public static String replace (String fullString, String toReplace, String replacement) {
    int index = 0;
    int pos;
    int fullStringLength = fullString.length();
    int toReplaceLength = toReplace.length();
    if (toReplaceLength > 0) {
      int replacementLength = replacement.length();
      StringBuilder buff;
      while (index < fullStringLength && 
             ((pos = fullString.indexOf(toReplace, index)) >= 0)) {      
        buff = new StringBuilder(fullString.substring(0, pos));
        buff.append(replacement);
        buff.append(fullString.substring(pos + toReplaceLength, fullStringLength));
        index = pos + replacementLength;
        fullString = buff.toString();
        fullStringLength = fullString.length();
      }
    }
    return fullString;
  }
  
  
  public static String convertToLiteral(String s) {
    String output = s;
    output = replace(output, "\\", "\\\\"); 
    output = replace(output, "\"", "\\\""); 
    output = replace(output, "\t", "\\t");  
    output = replace(output, "\n", "\\n");  
    return "\"" + output + "\"";
  }
  
  
  private static void _ensureStartBeforeEnd(int startRow, int startCol,
                                            int endRow, int endCol) {
    if (startRow > endRow) {
      throw new IllegalArgumentException("end row before start row: " +
                                         startRow + " > " + endRow);
    }
    else if (startRow == endRow && startCol > endCol) {
      throw new IllegalArgumentException("end before start: (" +
                                         startRow + ", " + startCol +
                                         ") > (" + endRow + ", " + endCol + ")");
    }
  }

  
  private static void _ensureColInRow(String fullString, int col, int rowStartIndex) {
    int endOfLine = fullString.indexOf(NEWLINE,rowStartIndex);
    if (endOfLine == -1) {
      endOfLine = fullString.length();
    }
    if (col > (endOfLine - rowStartIndex)) {
      throw new IllegalArgumentException("the given column is past the end of its row");
    }
  }

  
  public static Pair<Integer, Integer> getOffsetAndLength(String fullString, int startRow,
                                                          int startCol, int endRow, int endCol) {
    _ensureStartBeforeEnd(startRow, startCol, endRow, endCol);

    
    int currentChar = 0;
    int linesSeen = 1;
    while (startRow > linesSeen) {
      currentChar = fullString.indexOf(NEWLINE,currentChar);
      if (currentChar == -1) {
        throw new IllegalArgumentException("startRow is beyond the end of the string");
      }
      
      currentChar++;
      linesSeen++;
    }
    
    _ensureColInRow(fullString, startCol, currentChar);
    int offset = currentChar + startCol - 1;  

    
    while (endRow > linesSeen) {
      currentChar = fullString.indexOf(NEWLINE, currentChar);
      if (currentChar == -1) {
        throw new IllegalArgumentException("endRow is beyond the end of the string");
      }
      currentChar++;
      linesSeen++;
    }

    _ensureColInRow(fullString, endCol, currentChar);
    int length = currentChar + endCol - offset;

    
    if (offset + length > fullString.length()) {
      throw new IllegalArgumentException("Given positions beyond the end of the string");
    }
    return new Pair<Integer, Integer>(Integer.valueOf(offset), Integer.valueOf(length));
  }

  
  public static String getStackTrace(Throwable t) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    t.printStackTrace(pw);
    return sw.toString();
  }
  
  
  public static String getStackTrace() {
    try { throw new Exception(); } 
    catch (Exception e) {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      StackTraceElement[] stes = e.getStackTrace();
      int skip = 1;
      for(StackTraceElement ste: stes) {
        if (skip > 0) { --skip; } else { pw.print("at "); pw.println(ste); }
      }
      return sw.toString();
    }
  }
  
  
  public static boolean isAsciiDigit(char c) {
    return '0' <= c && c <= '9';
  }
  
  
  public static boolean isAnonymousClass(Class<?> c) {
    String simpleName = c.getName();
    int idx = simpleName.lastIndexOf('$');
    if (idx >= 0) {
      
      for (int pos=idx+1; pos < simpleName.length(); ++pos) {
        if (!isAsciiDigit(simpleName.charAt(pos))) {
          return false;
        }
      }
      return true;
    }
    return false;
  }
  
  
  public static boolean isMemberClass(Class<?> c) {
    String simpleName = c.getName();
    int idx = simpleName.lastIndexOf('$');
    if (idx == -1) {
      return false;
    }
    return !isAnonymousClass(c);
  }
  
  
  public static String getSimpleName(Class<?> c) {
    if (c.isArray())
      return getSimpleName(c.getComponentType())+"[]";

    if (isAnonymousClass(c)) {
      return "";
    }
    
    String simpleName = c.getName();
    int idx = Math.max(simpleName.lastIndexOf('.'), 
                       simpleName.lastIndexOf('$'));
    return simpleName.substring(idx + 1); 
  }
  
  
  public static String toString(long[] a) {
    if (a == null)
      return "null";
    if (a.length == 0)
      return "[]";
    
    final StringBuilder buf = new StringBuilder();
    buf.append('[');
    buf.append(a[0]);
    
    for (int i = 1; i < a.length; i++) {
      buf.append(", ");
      buf.append(a[i]);
    }
    
    buf.append("]");
    return buf.toString();
  }
  
  
  public static String toString(int[] a) {
    if (a == null)
      return "null";
    if (a.length == 0)
      return "[]";
    
    final StringBuilder buf = new StringBuilder();
    buf.append('[');
    buf.append(a[0]);
    
    for (int i = 1; i < a.length; i++) {
      buf.append(", ");
      buf.append(a[i]);
    }
    
    buf.append("]");
    return buf.toString();
  }
  
  
  public static String toString(short[] a) {
    if (a == null)
      return "null";
    if (a.length == 0)
      return "[]";
    
    final StringBuilder buf = new StringBuilder();
    buf.append('[');
    buf.append(a[0]);
    
    for (int i = 1; i < a.length; i++) {
      buf.append(", ");
      buf.append(a[i]);
    }
    
    buf.append("]");
    return buf.toString();
  }
  
  
  public static String toString(char[] a) {
    if (a == null)
      return "null";
    if (a.length == 0)
      return "[]";
    
    final StringBuilder buf = new StringBuilder();
    buf.append('[');
    buf.append(a[0]);
    
    for (int i = 1; i < a.length; i++) {
      buf.append(", ");
      buf.append(a[i]);
    }
    
    buf.append("]");
    return buf.toString();
  }
  
  
  public static String toString(byte[] a) {
    if (a == null)
      return "null";
    if (a.length == 0)
      return "[]";
    
    final StringBuilder buf = new StringBuilder();
    buf.append('[');
    buf.append(a[0]);
    
    for (int i = 1; i < a.length; i++) {
      buf.append(", ");
      buf.append(a[i]);
    }
    
    buf.append("]");
    return buf.toString();
  }
  
  
  public static String toString(boolean[] a) {
    if (a == null)
      return "null";
    if (a.length == 0)
      return "[]";
    
    final StringBuilder buf = new StringBuilder();
    buf.append('[');
    buf.append(a[0]);
    
    for (int i = 1; i < a.length; i++) {
      buf.append(", ");
      buf.append(a[i]);
    }
    
    buf.append("]");
    return buf.toString();
  }
  
  
  public static String toString(float[] a) {
    if (a == null)
      return "null";
    if (a.length == 0)
      return "[]";
    
    final StringBuilder buf = new StringBuilder();
    buf.append('[');
    buf.append(a[0]);
    
    for (int i = 1; i < a.length; i++) {
      buf.append(", ");
      buf.append(a[i]);
    }
    
    buf.append("]");
    return buf.toString();
  }
  
  
  public static String toString(double[] a) {
    if (a == null)
      return "null";
    if (a.length == 0)
      return "[]";
    
    final StringBuilder buf = new StringBuilder();
    buf.append('[');
    buf.append(a[0]);
    
    for (int i = 1; i < a.length; i++) {
      buf.append(", ");
      buf.append(a[i]);
    }
    
    buf.append("]");
    return buf.toString();
  }
  
  
  public static String toString(Object[] a) {
    if (a == null)
      return "null";
    if (a.length == 0)
      return "[]";
    
    final StringBuilder buf = new StringBuilder();
    
    for (int i = 0; i < a.length; i++) {
      if (i == 0)
        buf.append('[');
      else
        buf.append(", ");
      
      buf.append(String.valueOf(a[i]));
    }
    
    buf.append("]");
    return buf.toString();
  }

  
  public static String encodeHTML(String s) {
    s = StringOps.replace(s, "&", "&amp;");
    s = StringOps.replace(s, "<", "&lt;");
    s = StringOps.replace(s, ">", "&gt;");
    s = StringOps.replace(s, EOL,"<br>");
    s = StringOps.replace(s, NEWLINE,"<br>");
    return s;
  }

  
  public static String compress(String s) {
    int len = s.length();
    boolean inWSGap = false;
    StringBuilder sb = new StringBuilder(len);
    for (int i = 0; i < len; i++) {
      char ch = s.charAt(i);
      if (Character.isWhitespace(ch)) {
        if (! inWSGap) {
          inWSGap = true;
          sb.append(ch);
        }
      }
      else {
        inWSGap = false;
        sb.append(ch);
      }
    }
    return sb.toString();
  }
     

  public static String flatten(String s) { return s.replace(newline, SEPARATOR); }
      
  
  public static String memSizeToString(long l) {
    String[] sizes = new String[] { "byte", "kilobyte", "megabyte", "gigabyte" };
    double d = l;
    int i = 0;
    while((d >= 1024) && (i < sizes.length)) {
      ++i;
      d /= 1024;
    }
    if (i >= sizes.length) { i = sizes.length - 1; d *= 1024;  }
    StringBuilder sb = new StringBuilder();
    long whole = (long)d;
    if (whole == d) {
      if (whole == 1) {
        sb.append(whole);
        sb.append(' ');
        sb.append(sizes[i]);
      }
      else {
        sb.append(whole);
        sb.append(' ');
        sb.append(sizes[i]);
        sb.append('s');
      }
    }
    else {
      
      DecimalFormat df = new DecimalFormat("#.00");
      sb.append(df.format(d));
      sb.append(' ');
      sb.append(sizes[i]);
      sb.append('s');
    }
    return sb.toString();
  }
  
  
  
  
  public static String escapeFileName(String s) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < s.length(); ++i) {
      if (s.charAt(i) == '\u') {
        sb.append("\u\u");
      }
      else if (s.charAt(i) == ' ') {
        sb.append("\u ");
      }
      else if (s.charAt(i) == java.io.File.pathSeparatorChar) {
        sb.append('\u');
        sb.append(java.io.File.pathSeparatorChar);
      }
      else if (s.charAt(i) == ProcessChain.PROCESS_SEPARATOR_CHAR) {
        sb.append('\u');
        sb.append(ProcessChain.PROCESS_SEPARATOR_CHAR);
      }
      else if (s.charAt(i) == ProcessChain.PIPE_SEPARATOR_CHAR) {
        sb.append('\u');
        sb.append(ProcessChain.PIPE_SEPARATOR_CHAR);
      }
      else if (s.charAt(i) == ':') {
        sb.append("\u:"); 
        
      }
      else {
        sb.append(String.valueOf(s.charAt(i)));
      }
    }
    return sb.toString();
  }
  
  
  public static String unescapeFileName(String s) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < s.length(); ++i) {
      if (s.charAt(i) == '\u') {
        if (i+1<s.length()) {
          char next = s.charAt(i+1);
          if (next=='\u') { sb.append("\u"); ++i; }
          else if (next==' ') { sb.append(" "); ++i; }
          else if (next==java.io.File.pathSeparatorChar) { sb.append(java.io.File.pathSeparatorChar); ++i; }
          else if (next==ProcessChain.PROCESS_SEPARATOR_CHAR) { sb.append(ProcessChain.PROCESS_SEPARATOR_CHAR); ++i; }
          else if (next==ProcessChain.PIPE_SEPARATOR_CHAR) { sb.append(ProcessChain.PIPE_SEPARATOR_CHAR); ++i; }
          else if (next==':') { sb.append(':'); ++i; }
          else { throw new IllegalArgumentException("1b hex followed by character other than space, "+
                                                    "path separator, process separator, pipe, colon, or 1b hex"); }
        }
        else { throw new IllegalArgumentException("1b hex followed by character other than space, "+
                                                    "path separator, process separator, pipe, colon, or 1b hex"); }
      }
      else {
        sb.append(""+s.charAt(i));
      }
    }
    return sb.toString();
  }
  
  
  public static List<List<List<String>>> commandLineToLists(String cmdline) {
    BalancingStreamTokenizer tok = new BalancingStreamTokenizer(new StringReader(cmdline));
    tok.wordRange(0,255);
    tok.addQuotes("${", "}");
    tok.addQuotes("\"", "\"");
    tok.addQuotes("'", "'");
    tok.addQuotes("`", "`");
    tok.addKeyword(ProcessChain.PROCESS_SEPARATOR);
    tok.addKeyword(ProcessChain.PIPE_SEPARATOR);
    
    tok.addKeyword(" ");
    tok.addKeyword(new Character((char)0x09).toString()); 
    tok.addKeyword(new Character((char)0x0A).toString()); 
    tok.addKeyword(new Character((char)0x0B).toString()); 
    tok.addKeyword(new Character((char)0x0C).toString()); 
    tok.addKeyword(new Character((char)0x0D).toString()); 
    tok.addKeyword(new Character((char)0x0E).toString()); 
    tok.addKeyword(new Character((char)0x1C).toString()); 
    tok.addKeyword(new Character((char)0x1D).toString()); 
    tok.addKeyword(new Character((char)0x1E).toString()); 
    tok.addKeyword(new Character((char)0x1F).toString()); 
    
    final String ESCAPE = String.valueOf((char)0x1B);
    final String ESCAPED_SPACE = ESCAPE+" ";
    tok.addKeyword(ESCAPED_SPACE); 
    
    final String ESCAPED_PATH_SEPARATOR = ESCAPE+java.io.File.pathSeparator;
    tok.addKeyword(ESCAPED_PATH_SEPARATOR); 
    
    final String ESCAPED_PROCESS_SEPARATOR = ESCAPE+ProcessChain.PROCESS_SEPARATOR;
    tok.addKeyword(ESCAPED_PROCESS_SEPARATOR); 
    
    final String ESCAPED_PIPE_SEPARATOR = ESCAPE+ProcessChain.PIPE_SEPARATOR;
    tok.addKeyword(ESCAPED_PIPE_SEPARATOR); 
    
    final String ESCAPED_COLON = ESCAPE+":";
    if (!ESCAPED_COLON.equals(ESCAPED_PATH_SEPARATOR)) {
      tok.addKeyword(ESCAPED_COLON); 
    }
    
    final String ESCAPED_ESCAPE = ESCAPE+ESCAPE;
    tok.addKeyword(ESCAPED_ESCAPE); 
    
    String n = null;
    StringBuilder sb = new StringBuilder();
    List<List<List<String>>> lll = new ArrayList<List<List<String>>>();
    List<List<String>> ll = new ArrayList<List<String>>();
    List<String> l = new ArrayList<String>();
    try {
      while((n=tok.getNextToken()) != null) {
        if (tok.token() == BalancingStreamTokenizer.Token.KEYWORD) {
          if (n.equals(ProcessChain.PROCESS_SEPARATOR)) {
            
            String arg = sb.toString();
            sb.setLength(0);
            if (arg.length() > 0) { l.add(arg); }
            
            
            
            ll.add(l);
            l = new ArrayList<String>();

            
            
            lll.add(ll);
            ll = new ArrayList<List<String>>();
          }
          else if (n.equals(ProcessChain.PIPE_SEPARATOR)) {
            
            String arg = sb.toString();
            sb.setLength(0);
            if (arg.length() > 0) { l.add(arg); }
            
            
            
            ll.add(l);
            l = new ArrayList<String>();
          }
          else if (n.equals(ESCAPED_SPACE) ||
                   n.equals(ESCAPED_PATH_SEPARATOR) ||
                   n.equals(ESCAPED_PROCESS_SEPARATOR) ||
                   n.equals(ESCAPED_PIPE_SEPARATOR) ||
                   n.equals(ESCAPED_COLON) ||
                   n.equals(ESCAPED_ESCAPE)) {
            
            sb.append(n.substring(ESCAPE.length()));
          }
          else { 
            
            String arg = sb.toString();
            sb.setLength(0);
            if (arg.length() > 0) { l.add(arg); }
          }
        }
        else {
          sb.append(n);
        }
      }
    }
    catch(IOException e) {  }
    
    
    String arg = sb.toString();
    sb.setLength(0);
    if (arg.length() > 0) { l.add(arg); }
    
    
    
    ll.add(l);
    l = new ArrayList<String>();
    
    
    
    lll.add(ll);
    ll = new ArrayList<List<String>>();    

    return lll;
  }

    
  
  public static String replaceVariables(String str, final PropertyMaps props, final Lambda2<DrJavaProperty,PropertyMaps,String> getter) {
    BalancingStreamTokenizer tok = new BalancingStreamTokenizer(new StringReader(str), '$');
    tok.wordRange(0,255);
    tok.addQuotes("${", "}");
    tok.addQuotes("\"", "\"");
    
    
    
    StringBuilder sb = new StringBuilder();
    String next = null;
    try {
      while((next=tok.getNextToken()) != null) {
        
        if ((tok.token() == BalancingStreamTokenizer.Token.QUOTED) &&
            (next.startsWith("${")) &&
            (next.endsWith("}"))) {
          
          String key;
          String attrList = "";
          int firstCurly = next.indexOf('}');
          int firstSemi = next.indexOf(';');
          if (firstSemi < 0) {
            
            
            key = next.substring(2,firstCurly);
          }
          else {
            
            
            key = next.substring(2,firstSemi);
            
            attrList = next.substring(firstSemi+1,next.length()-1).trim();
          }
          
          
          DrJavaProperty p = props.getProperty(key);
          if (p != null) {
            
            p.resetAttributes();
            
            
            try {
              if (attrList.length() > 0) {
                BalancingStreamTokenizer atok = new BalancingStreamTokenizer(new StringReader(attrList), '$');
                atok.wordRange(0,255);
                atok.whitespaceRange(0,32); 
                atok.addQuotes("\"", "\"");
                atok.addQuotes("${", "}");
                atok.addKeyword(";");
                atok.addKeyword("=");
                
                String n = null;
                HashMap<String,String> attrs = new HashMap<String,String>();
                while((n=atok.getNextToken()) != null) {
                  if ((n == null) || (atok.token() != BalancingStreamTokenizer.Token.NORMAL) ||
                      n.equals(";") || n.equals("=") || n.startsWith("\"")) {
                    throw new IllegalArgumentException("Unknown attribute list format for property "+key+"; expected name, but was "+n);
                  }
                  String name = n;
                  
                  n = atok.getNextToken();
                  if ((n == null) || (atok.token() != BalancingStreamTokenizer.Token.KEYWORD) || (!n.equals("="))) {
                    throw new IllegalArgumentException("Unknown attribute list format for property "+key+"; expected =, but was "+n);
                  }
                  
                  n = atok.getNextToken();
                  if ((n == null) || (atok.token() != BalancingStreamTokenizer.Token.QUOTED) || (!n.startsWith("\""))) {
                    throw new IllegalArgumentException("Unknown attribute list format for property "+key+"; expected \", but was "+n);
                  }
                  String value = "";
                  if (n.length()>1) {
                    value = n.substring(1,n.length()-1);
                    
                  }
                  n = atok.getNextToken();
                  if (((n != null) && ((atok.token() != BalancingStreamTokenizer.Token.KEYWORD) || (!n.equals(";")))) ||
                      ((n == null) && (atok.token() != BalancingStreamTokenizer.Token.END))) {
                    throw new IllegalArgumentException("Unknown attribute list format for property "+key);
                  }
                  
                  
                  
                  
                  
                  attrs.put(name,value);
                  
                  
                  if (n == null) { break; }
                }
                p.setAttributes(attrs, new Lambda<String,String>() {
                  public String value(String param) {
                    return replaceVariables(param, props, getter);
                  }
                });
              }
              
              String finalValue = getter.value(p,props);
              
              sb.append(finalValue);
            }              
            catch(IllegalArgumentException e) {
              sb.append("<-- Error: "+e.getMessage()+" -->");
            }
          }
          else {
            
            sb.append(next);
          }
        }
        else {
          sb.append(next);
        }
      }
    }
    catch(IllegalArgumentException e) {
      return "<-- Error: "+e.getMessage()+" -->";
    }
    catch(IOException e) {
      return "<-- Error: "+e.getMessage()+" -->";
    }
    
    
    
    
    return sb.toString();
  }
  
  
  public static String splitStringAtWordBoundaries(String s, int widthInChars,
                                                   String lineBreak,
                                                   String wordSepChars) {
    StringBuilder sb = new StringBuilder();
    
    while(s.length() > 0) {
      if (wordSepChars.indexOf(String.valueOf(s.charAt(0)))>=0) {

        s = s.substring(1);
      }
      else { break;  }
    }
    
    while(s.length() > 0) {
      if (wordSepChars.indexOf(String.valueOf(s.charAt(s.length()-1)))>=0) {

        s = s.substring(0, s.length()-1);
      }
      else { break;  }
    }


    
    java.util.StringTokenizer tok = new java.util.StringTokenizer(s, wordSepChars);
    StringBuilder sbl = new StringBuilder(); 

    while(tok.hasMoreElements()) {
      String token = tok.nextToken();

      sbl.append(token);

      if (sbl.length()>=widthInChars) {


        if (tok.hasMoreElements()) {

          sbl.append(lineBreak);
        }

        sb.append(sbl.toString());

        sbl.setLength(0);
      }
      else { sbl.append(" "); }
    }

    if (sbl.length() > 0) { sb.append(sbl.toString()); }


    return sb.toString();
  }
  
  
  public static String toStringHexDump(String s) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    StringBuilder sb = new StringBuilder();
    for(int i = 0; i < s.length(); ++i) {
      char ch = s.charAt(i);
      pw.printf("%02x ",(int)ch);
      if (ch<32) ch = ' ';
      sb.append(ch);
      if (i%16==7) {
        pw.printf("   ");
      }
      else if (i%16==15) {
        pw.printf("| %s\n", sb.toString());
        sb.setLength(0);
      }
    }
    if (s.length()%16 > 0) {
      for(int i = 0;i < 16-(s.length()%16);++i) {
        pw.printf("   ");
        sb.append(' ');
        if ((s.length()+i)%16==7) {
          pw.printf("   ");
          sb.append(' ');
        }
        else if ((s.length()+i)%16==15) {
          pw.printf("| %s", sb.toString());
          sb.setLength(0);
        }
      }
    }
    return sw.toString();
  }
}
