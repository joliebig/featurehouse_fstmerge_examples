

package edu.rice.cs.util;

import java.io.StringWriter;
import java.io.PrintWriter;



public abstract class StringOps {
  
  public static String replace (String fullString, String toReplace, String replacement) {
    int index = 0;
    int pos;
    int fullStringLength = fullString.length();
    int toReplaceLength = toReplace.length();
    if (toReplaceLength > 0) {
      int replacementLength = replacement.length();
      StringBuffer buff;
      while (index < fullStringLength && 
             ((pos = fullString.indexOf(toReplace, index)) >= 0)) {      
        buff = new StringBuffer(fullString.substring(0, pos));
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
    int endOfLine = fullString.indexOf("\n",rowStartIndex);
    if (endOfLine == -1) {
      endOfLine = fullString.length();
    }
    if (col > (endOfLine - rowStartIndex)) {
      throw new IllegalArgumentException("the given column is past the end of its row");
    }
  }

  
  public static Pair<Integer,Integer> getOffsetAndLength(String fullString, int startRow,
                                                         int startCol, int endRow, int endCol) {
    _ensureStartBeforeEnd(startRow, startCol, endRow, endCol);

    
    int currentChar = 0;
    int linesSeen = 1;
    while (startRow > linesSeen) {
      currentChar = fullString.indexOf("\n",currentChar);
      if (currentChar == -1) {
        throw new IllegalArgumentException("startRow is beyond the end of the string");
      }
      
      currentChar++;
      linesSeen++;
    }
    
    _ensureColInRow(fullString, startCol, currentChar);
    int offset = currentChar + startCol - 1;  

    
    while (endRow > linesSeen) {
      currentChar = fullString.indexOf("\n",currentChar);
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
    return new Pair<Integer,Integer>(new Integer(offset), new Integer(length));
  }

  
  public static String getStackTrace(Throwable t) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    t.printStackTrace(pw);
    return sw.toString();
  }
  
  
  public static boolean isAsciiDigit(char c) {
    return '0' <= c && c <= '9';
  }
  
  
  public static boolean isAnonymousClass(Class c) {
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
  
  
  public static boolean isMemberClass(Class c) {
    String simpleName = c.getName();
    int idx = simpleName.lastIndexOf('$');
    if (idx == -1) {
      return false;
    }
    return !isAnonymousClass(c);
  }
  
  
  public static String getSimpleName(Class c) {
    if (c.isArray())
      return getSimpleName(c.getComponentType())+"[]";

    if (isAnonymousClass(c)) {
      return "";
    }
    
    String simpleName = c.getName();
    int idx = Math.max(simpleName.lastIndexOf('.'), 
                       simpleName.lastIndexOf('$'));
    return simpleName.substring(idx+1); 
  }
  
  
  public static String toString(long[] a) {
    if (a == null)
      return "null";
    if (a.length == 0)
      return "[]";
    
    StringBuilder buf = new StringBuilder();
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
    
    StringBuilder buf = new StringBuilder();
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
    
    StringBuilder buf = new StringBuilder();
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
    
    StringBuilder buf = new StringBuilder();
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
    
    StringBuilder buf = new StringBuilder();
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
    
    StringBuilder buf = new StringBuilder();
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
    
    StringBuilder buf = new StringBuilder();
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
    
    StringBuilder buf = new StringBuilder();
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
    
    StringBuilder buf = new StringBuilder();
    
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
}