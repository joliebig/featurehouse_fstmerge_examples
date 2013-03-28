

package org.jmol.util;

import java.util.BitSet;

public class Parser {

  

  
  

  
  public static void parseFloatArray(String str, BitSet bs, float[] data) {
    parseFloatArray(getTokens(str), bs, data);
  }

  public static float[] parseFloatArray(String str) {
    return parseFloatArray(str, new int[1]);
  }

  
  public static float[] parseFloatArray(String str, int[] next) {
    int pt = next[0];
    if (pt < 0)
      return new float[0];
    pt = str.indexOf("[", pt);
    if (pt >= 0)
      str = str.substring(pt + 1);
    next[0] = pt + 1;
    pt = str.indexOf("]");
    if (pt < 0)
      pt = str.length();
    else
      str = str.substring(0, pt);
    next[0] += pt + 1;
    String[] tokens = getTokens(str);
    float[] f = new float[tokens.length];
    int n = parseFloatArray(tokens, null, f);
    for (int i = n; i < f.length; i++)
      f[i] = Float.NaN;
    return f;
  }
  
  public static int parseFloatArray(String[] tokens, BitSet bs, float[] data) {
    int len = data.length;
    int nTokens = tokens.length;
    int n = 0;
    int max = 0;
    for (int i = 0; i < len && n < nTokens; i++) {
      if (bs != null && !bs.get(i))
        continue;
      float f;
      while (Float.isNaN(f = Parser.parseFloat(tokens[n++])) && n < nTokens) {
      }
      data[(max = i)] = f;
    }
    return max + 1;
  }

  public static float[][] parseFloatArray2d(String str) {
    str = str.replace(';', str.indexOf('\n') < 0 ? '\n' : ' ');
    str = TextFormat.trim(str, "\n \t");
    int[] lines = markLines(str, '\n');
    int nLines = lines.length;
    float[][] data = new float[nLines][];
    for (int iLine = 0, pt = 0; iLine < nLines; pt = lines[iLine++]) {
      String[] tokens = getTokens(str.substring(pt, lines[iLine]));
      parseFloatArray(tokens, data[iLine] = new float[tokens.length]);
    }
    return data;
  }

  public static float[][][] parseFloatArray3d(String str) {
    str = str.replace(';', str.indexOf('\n') < 0 ? '\n' : ' ');
    str = TextFormat.trim(str, "\n \t");
    int[] lines = markLines(str, '\n');
    int nLines = lines.length;
    String[] tokens = getTokens(str.substring(0, lines[0]));
    if (tokens.length != 3)
      return new float[0][0][0];
    int nX = parseInt(tokens[0]);
    int nY = parseInt(tokens[1]);
    int nZ = parseInt(tokens[2]);
    if (nX < 1 || nY < 1 || nZ < 1)
      return new float[1][1][1];
    float[][][] data = new float[nX][nY][];
    int iX = 0;
    int iY = 0;
    for (int iLine = 1, pt = lines[0]; iLine < nLines && iX < nX; pt = lines[iLine++]) {
      tokens = getTokens(str.substring(pt, lines[iLine]));
      if (tokens.length < nZ)
        continue;
      parseFloatArray(tokens, data[iX][iY] = new float[tokens.length]);
      if (++iY == nY) {
        iX++;
        iY = 0;
      } 
    }
    if (iX != nX) {
      Logger.info("Error reading 3D data -- nX = " + nX + ", but only " + iX + " blocks read");      
      return new float[1][1][1];
    }
    return data;
  }

  
  public static void setSelectedFloats(float f, BitSet bs, float[] data) {
    for (int i = 0; i < data.length; i++)
      if (bs == null || bs.get(i))
        data[i] = f;
  }
  
  public static float[] extractData(String data, int field, int nBytes,
                                    int firstLine) {
    return parseFloatArrayFromMatchAndField(data, null, 0, 0, null, field,
        nBytes, null, firstLine);
  }

  
  public static float[] parseFloatArrayFromMatchAndField(
                                                         String str,
                                                         BitSet bs,
                                                         int fieldMatch,
                                                         int fieldMatchColumnCount,
                                                         int[] matchData,
                                                         int field,
                                                         int fieldColumnCount,
                                                         float[] data, int firstLine) {
    float f;
    int i = -1;
    boolean isMatch = (matchData != null);
    int[] lines = markLines(str, (str.indexOf('\n') >= 0 ? '\n' : ';'));
    int iLine = (firstLine <= 1 || firstLine >= lines.length ? 0 : firstLine - 1);
    int pt = (iLine == 0 ? 0 : lines[iLine - 1]);
    int nLines = lines.length;
    if (data == null)
      data = new float[nLines - iLine];
    int len = data.length;
    int minLen = (fieldColumnCount <= 0 ? Math.max(field, fieldMatch) : Math
        .max(field + fieldColumnCount, fieldMatch + fieldMatchColumnCount) - 1);

    for (; iLine < nLines; iLine++) {
      String line = str.substring(pt, lines[iLine]).trim();
      pt = lines[iLine];
      String[] tokens = (fieldColumnCount <= 0 ? getTokens(line) : null);
      
      
      if (fieldColumnCount <= 0) {
        if (tokens.length < minLen
            || Float.isNaN(f = parseFloat(tokens[field - 1])))
          continue;
      } else {
        if (line.length() < minLen
            || Float.isNaN(f = parseFloat(line.substring(field - 1, field
                + fieldColumnCount - 1))))
          continue;
      }
      int iData;
      if (isMatch) {
        iData = parseInt(tokens == null ? line.substring(fieldMatch - 1,
            fieldMatch + fieldMatchColumnCount - 1) : tokens[fieldMatch - 1]);
        
        
        if (iData == Integer.MIN_VALUE || iData < 0 || iData >= len
            || (iData = matchData[iData]) < 0)
          continue;
        
        if (bs != null)
          bs.set(iData);
      } else {
        
        
        while (++i < len && bs != null && !bs.get(i)) {
        }
        if (i >= len)
          return data;
        iData = i;
      }
      data[iData] = f;
      
    }
    return data;
  }
  
  
  public static void parseFloatArray(String[] tokens, float[] data) {
    parseFloatArray(tokens, data, data.length);
  }
  
  
  public static void parseFloatArray(String[] tokens, float[] data, int nData) {
    for (int i = nData; --i >= 0;)
      data[i] = (i >= tokens.length ? Float.NaN : Parser.parseFloat(tokens[i]));
  }
 
  public static float parseFloat(String str) {
    return parseFloat(str, new int[] {0});
  }

  public static float parseFloatStrict(String str) {
    
    int cch = str.length();
    if (cch == 0)
      return Float.NaN;
    return parseFloatChecked(str, cch, new int[] {0}, true);
  }

  public static int parseInt(String str) {
    return parseInt(str, new int[] {0});
  }

  public static String[] getTokens(String line) {
    return getTokens(line, 0);
  }

  public static String parseToken(String str) {
    return parseToken(str, new int[] {0});
  }

  public static String parseTrimmed(String str) {
    return parseTrimmed(str, 0, str.length());
  }
  
  public static String parseTrimmed(String str, int ichStart) {
    return parseTrimmed(str, ichStart, str.length());
  }
  
  public static String parseTrimmed(String str, int ichStart, int ichMax) {
    int cch = str.length();
    if (ichMax < cch)
      cch = ichMax;
    if (cch < ichStart)
      return "";
    return parseTrimmedChecked(str, ichStart, cch);
  }

  public static int[] markLines(String data, char eol) {
    int nLines = 0;
    for (int i = data.length(); --i >=0;)
      if (data.charAt(i) == eol)
        nLines++;
    int[] lines = new int[nLines + 1];
    lines[nLines--] = data.length();
    for (int i = data.length(); --i >=0;)
      if (data.charAt(i) == eol)
        lines[nLines--] = i + 1;
    return lines;
  }

  public static float parseFloat(String str, int[] next) {
    int cch = str.length();
    if (next[0] < 0 || next[0] >= cch)
      return Float.NaN;
    return parseFloatChecked(str, cch, next, false);
  }

  public static float parseFloat(String str, int ichMax, int[] next) {
    int cch = str.length();
    if (ichMax > cch)
      ichMax = cch;
    if (next[0] < 0 || next[0] >= ichMax)
      return Float.NaN;
    return parseFloatChecked(str, ichMax, next, false);
  }

  private final static float[] decimalScale = { 0.1f, 0.01f, 0.001f, 0.0001f, 0.00001f,
      0.000001f, 0.0000001f, 0.00000001f };

  private final static float[] tensScale = { 10, 100, 1000, 10000, 100000, 1000000 };

  private static float parseFloatChecked(String str, int ichMax, int[] next, boolean isStrict) {
    boolean digitSeen = false;
    float value = 0;
    int ich = next[0];
    if (isStrict && str.indexOf('\n') != str.lastIndexOf('\n'))
        return Float.NaN;
    while (ich < ichMax && isWhiteSpace(str, ich))
      ++ich;
    boolean negative = false;
    if (ich < ichMax && str.charAt(ich) == '-') {
      ++ich;
      negative = true;
    }
    char ch = 0;
    while (ich < ichMax && (ch = str.charAt(ich)) >= '0' && ch <= '9') {
      value = value * 10 + (ch - '0');
      ++ich;
      digitSeen = true;
    }
    boolean isDecimal = false;
    if (ch == '.') {
      isDecimal = true;
      int iscale = 0;
      while (++ich < ichMax && (ch = str.charAt(ich)) >= '0' && ch <= '9') {
        if (iscale < decimalScale.length)
          value += (ch - '0') * decimalScale[iscale];
        ++iscale;
        digitSeen = true;
      }
    }
    boolean isExponent = false;
    if (!digitSeen)
      value = Float.NaN;
    else if (negative)
      value = -value;
    if (ich < ichMax && (ch == 'E' || ch == 'e' || ch == 'D')) {
      isExponent = true;
      if (++ich >= ichMax)
        return Float.NaN;
      ch = str.charAt(ich);
      if ((ch == '+') && (++ich >= ichMax))
        return Float.NaN;
      next[0] = ich;
      int exponent = parseIntChecked(str, ichMax, next);
      if (exponent == Integer.MIN_VALUE)
        return Float.NaN;
      if (exponent > 0)
        value *= ((exponent < tensScale.length) ? tensScale[exponent - 1]
            : Math.pow(10, exponent));
      else if (exponent < 0)
        value *= ((-exponent < decimalScale.length) ? decimalScale[-exponent - 1]
            : Math.pow(10, exponent));
    } else {
       next[0] = ich; 
    }
    if (value == Float.NEGATIVE_INFINITY)
      value = -Float.MAX_VALUE;
    else if (value == Float.POSITIVE_INFINITY)
      value= Float.MAX_VALUE;
    return (!isStrict 
        || (!isExponent || isDecimal) && checkTrailingText(str, next[0], ichMax) 
        ? value : Float.NaN);
  }

  private static boolean checkTrailingText(String str, int ich, int ichMax) {
    
    char ch;
    while (ich < ichMax && ((ch = str.charAt(ich)) == ' '
        || ch == '\t' || ch == '\n' || ch == ';'))
      ++ich;
    return (ich == ichMax);
  }
  
  public static int parseInt(String str, int[] next) {
    int cch = str.length();
    if (next[0] < 0 || next[0] >= cch)
      return Integer.MIN_VALUE;
    return parseIntChecked(str, cch, next);
  }

  public static int parseInt(String str, int ichMax, int[] next) {
    int cch = str.length();
    if (ichMax > cch)
      ichMax = cch;
    if (next[0] < 0 || next[0] >= ichMax)
      return Integer.MIN_VALUE;
    return parseIntChecked(str, ichMax, next);
  }

  private static int parseIntChecked(String str, int ichMax, int[] next) {
    boolean digitSeen = false;
    int value = 0;
    int ich = next[0];
    if (ich < 0)
      return Integer.MIN_VALUE;
    char ch;
    while (ich < ichMax && isWhiteSpace(str, ich))
      ++ich;
    boolean negative = false;
    if (ich < ichMax && str.charAt(ich) == '-') {
      negative = true;
      ++ich;
    }
    while (ich < ichMax && (ch = str.charAt(ich)) >= '0' && ch <= '9') {
      value = value * 10 + (ch - '0');
      digitSeen = true;
      ++ich;
    }
    if (!digitSeen)
      value = Integer.MIN_VALUE;
    else if (negative)
      value = -value;
    next[0] = ich;
    return value;
  }

  public static String[] getTokens(String line, int ich) {
    if (line == null)
      return null;
    int cchLine = line.length();
    if (ich < 0 || ich > cchLine)
      return null;
    int tokenCount = countTokens(line, ich);
    String[] tokens = new String[tokenCount];
    int[] next = new int[1];
    next[0] = ich;
    for (int i = 0; i < tokenCount; ++i)
      tokens[i] = parseTokenChecked(line, cchLine, next);
    return tokens;
  }

  private static int countTokens(String line, int ich) {
    int tokenCount = 0;
    if (line != null) {
      int ichMax = line.length();
      while (true) {
        while (ich < ichMax && isWhiteSpace(line, ich))
          ++ich;
        if (ich == ichMax)
          break;
        ++tokenCount;
        do {
          ++ich;
        } while (ich < ichMax && !isWhiteSpace(line, ich));
      }
    }
    return tokenCount;
  }

  public static String parseToken(String str, int[] next) {
    int cch = str.length();
    if (next[0] < 0 || next[0] >= cch)
      return null;
    return parseTokenChecked(str, cch, next);
  }

  public static String parseToken(String str, int ichMax, int[] next) {
    int cch = str.length();
    if (ichMax > cch)
      ichMax = cch;
    if (next[0] < 0 || next[0] >= ichMax)
      return null;
    return parseTokenChecked(str, ichMax, next);
  }

  private static String parseTokenChecked(String str, int ichMax, int[] next) {
    int ich = next[0];
    while (ich < ichMax && isWhiteSpace(str, ich))
      ++ich;
    int ichNonWhite = ich;
    while (ich < ichMax && !isWhiteSpace(str, ich))
      ++ich;
    next[0] = ich;
    if (ichNonWhite == ich)
      return null;
    return str.substring(ichNonWhite, ich);
  }

  private static String parseTrimmedChecked(String str, int ich, int ichMax) {
    while (ich < ichMax && isWhiteSpace(str, ich))
      ++ich;
    int ichLast = ichMax - 1;
    while (ichLast >= ich && isWhiteSpace(str, ichLast))
      --ichLast;
    if (ichLast < ich)
      return "";
    return str.substring(ich, ichLast + 1);
  }

  public static String concatTokens(String[] tokens, int iFirst, int iEnd) {
    String str = "";
    String sep = "";
    for (int i = iFirst; i < iEnd; i++) {
      if (i < tokens.length) {
        str += sep + tokens[i];
        sep = " ";
      }
    }
    return str;
  }
  
  public static String getNextQuotedString(String line, int ipt0) {
    int[] next = new int[] { ipt0 };
    return getNextQuotedString(line, next);
  }
  
  public static String getNextQuotedString(String line, int[] next) {
    String value = line;
    int i = next[0];
    if (i < 0 || (i = value.indexOf("\"", i)) < 0)
      return "";
    next[0] = ++i;
    value = value.substring(i);
    i = -1;
    while (++i < value.length() && value.charAt(i) != '"')
      if (value.charAt(i) == '\\')
        i++;
    next[0] += i + 1;
    return value.substring(0, i);
  }
  
  private static boolean isWhiteSpace(String str, int ich) {
    char ch;
    return (ich >= 0 && ((ch = str.charAt(ich)) == ' ' || ch == '\t' || ch == '\n'));
  }

  public static boolean isOneOf(String key, String semiList) {
    return key.indexOf(";") < 0  && (';' + semiList + ';').indexOf(';' + key + ';') >= 0;
  }
}
