

package org.jmol.util;

import java.text.DecimalFormat;
import java.util.Vector;

import javax.vecmath.Point3f;
import javax.vecmath.Point4f;

public class TextFormat {

  private final static DecimalFormat[] formatters = new DecimalFormat[10];

  private final static String[] formattingStrings = { "0", "0.0", "0.00", "0.000",
      "0.0000", "0.00000", "0.000000", "0.0000000", "0.00000000", "0.000000000" };

  private final static Boolean[] useNumberLocalization = new Boolean[1];
  {
    useNumberLocalization[0] = Boolean.TRUE;
  }
  
  public static void setUseNumberLocalization(boolean TF) {
    useNumberLocalization[0] = (TF ? Boolean.TRUE : Boolean.FALSE);
  }

  public static String formatDecimal(float value, int decimalDigits) {
    if (decimalDigits == Integer.MAX_VALUE)
      return "" + value;
    if (decimalDigits < 0) {
      decimalDigits = -decimalDigits;
      if (decimalDigits > formattingStrings.length)
        decimalDigits = formattingStrings.length;
      if (value == 0)
        return formattingStrings[decimalDigits] + "E+0";
      
      int n = 0;
      double d;
      if (Math.abs(value) < 1) {
        n = 10;
        d = value * 1e-10;
      } else {
        n = -10;
        d = value * 1e10;
      }
      String s = ("" + d).toUpperCase();
      int i = s.indexOf("E");
      n = Parser.parseInt(s.substring(i + 1)) + n;
      return (i < 0 ? "" + value : formatDecimal(Parser.parseFloat(s.substring(
          0, i)), decimalDigits - 1)
          + "E" + (n >= 0 ? "+" : "") + n);
    }

    if (decimalDigits >= formattingStrings.length)
      decimalDigits = formattingStrings.length - 1;
    DecimalFormat formatter = formatters[decimalDigits];
    if (formatter == null)
      formatter = formatters[decimalDigits] = new DecimalFormat(
          formattingStrings[decimalDigits]);
    String s = formatter.format(value);
    return (Boolean.TRUE.equals(useNumberLocalization[0]) ? s : s.replace(',','.'));
  }

  public static String format(float value, int width, int precision,
                              boolean alignLeft, boolean zeroPad) {
    return format(formatDecimal(value, precision), width, 0, alignLeft, zeroPad);
  }

  public static String format(double value, int width, int precision,
                              boolean alignLeft, boolean zeroPad) {
    return format(formatDecimal((float)value, -1 - precision), width, 0, alignLeft, zeroPad);
  }

  
  public static String format(String value, int width, int precision,
                              boolean alignLeft, boolean zeroPad) {
    if (value == null)
      return "";
    int len = value.length();
    if (precision != Integer.MAX_VALUE && precision > 0
        && precision < len)
      value = value.substring(0, precision);
    else if (precision < 0 && len + precision >= 0)
      value = value.substring(len + precision);

    int padLength = width - value.length();
    if (padLength <= 0)
      return value;
    boolean isNeg = (zeroPad && !alignLeft && value.charAt(0) == '-');
    char padChar = (zeroPad ? '0' : ' ');
    char padChar0 = (isNeg ? '-' : padChar);

    StringBuffer sb = new StringBuffer();
    if (alignLeft)
      sb.append(value);
    sb.append(padChar0);
    for (int i = padLength; --i > 0;)
      
      sb.append(padChar);
    if (!alignLeft)
      sb.append(isNeg ? padChar + value.substring(1) : value);
    return sb.toString();
  }

  public static String formatString(String strFormat, String key, String strT) {
    return formatString(strFormat, key, strT, Float.NaN, Double.NaN, false);
  }

  public static String formatString(String strFormat, String key, float floatT) {
    return formatString(strFormat, key, null, floatT, Double.NaN, false);
  }

  public static String formatString(String strFormat, String key, int intT) {
    return formatString(strFormat, key, "" + intT, Float.NaN, Double.NaN, false);
  }
   
  
  public static String sprintf(String strFormat, Object[] values) {
    if (values == null)
      return strFormat;
    for (int o = 0; o < values.length; o++)
      if (values[o] != null) {
        if (values[o] instanceof String) {
          strFormat = formatString(strFormat, "s", (String) values[o],
              Float.NaN, Double.NaN, true);
        } else if (values[o] instanceof Float) {
          strFormat = formatString(strFormat, "f", null, ((Float) values[o])
              .floatValue(), Double.NaN, true);
        } else if (values[o] instanceof Integer) {
          strFormat = formatString(strFormat, "d", "" + values[o], Float.NaN,
              Double.NaN, true);
          strFormat = formatString(strFormat, "i", "" + values[o], Float.NaN,
              Double.NaN, true);
        } else if (values[o] instanceof Double) {
          strFormat = formatString(strFormat, "e", null, Float.NaN,
              ((Double) values[o]).doubleValue(), true);
        } else if (values[o] instanceof Point3f) {
          Point3f pVal = (Point3f) values[o];
          strFormat = formatString(strFormat, "p", null, pVal.x, Double.NaN,
              true);
          strFormat = formatString(strFormat, "p", null, pVal.y, Double.NaN,
              true);
          strFormat = formatString(strFormat, "p", null, pVal.z, Double.NaN,
              true);
        } else if (values[o] instanceof Point4f) {
          Point4f qVal = (Point4f) values[o];
          strFormat = formatString(strFormat, "q", null, qVal.x, Double.NaN,
              true);
          strFormat = formatString(strFormat, "q", null, qVal.y, Double.NaN,
              true);
          strFormat = formatString(strFormat, "q", null, qVal.z, Double.NaN,
              true);
          strFormat = formatString(strFormat, "q", null, qVal.w, Double.NaN,
              true);
        } else if (values[o] instanceof String[]) {
          String[] sVal = (String[]) values[o];
          for (int i = 0; i < sVal.length; i++)
            strFormat = formatString(strFormat, "s", sVal[i], Float.NaN,
                Double.NaN, true);
        } else if (values[o] instanceof float[]) {
          float[] fVal = (float[]) values[o];
          for (int i = 0; i < fVal.length; i++)
            strFormat = formatString(strFormat, "f", null, fVal[i], Double.NaN,
                true);
        } else if (values[o] instanceof double[]) {
          double[] dVal = (double[]) values[o];
          for (int i = 0; i < dVal.length; i++)
            strFormat = formatString(strFormat, "e", null, Float.NaN, dVal[i],
                true);
        } else if (values[o] instanceof int[]) {
          int[] iVal = (int[]) values[o];
          for (int i = 0; i < iVal.length; i++)
            strFormat = formatString(strFormat, "d", "" + iVal[i], Float.NaN,
                Double.NaN, true);
          for (int i = 0; i < iVal.length; i++)
            strFormat = formatString(strFormat, "i", "" + iVal[i], Float.NaN,
                Double.NaN, true);
        }
      }
    return simpleReplace(strFormat, "%%", "%");
  }

  

  private static String formatString(String strFormat, String key, String strT,
                                    float floatT, double doubleT, boolean doOne) {
    if (strFormat == null)
      return null;
    if ("".equals(strFormat))
      return "";
    int len = key.length();
    if (strFormat.indexOf("%") < 0 || len == 0 || strFormat.indexOf(key) < 0)
      return strFormat;

    String strLabel = "";
    int ich, ichPercent, ichKey;
    for (ich = 0; (ichPercent = strFormat.indexOf('%', ich)) >= 0
        && (ichKey = strFormat.indexOf(key, ichPercent + 1)) >= 0;) {
      if (ich != ichPercent)
        strLabel += strFormat.substring(ich, ichPercent);
      ich = ichPercent + 1;
      if (ichKey > ichPercent + 6) {
        strLabel += '%';
        continue;
      }
      try {
        boolean alignLeft = false;
        if (strFormat.charAt(ich) == '-') {
          alignLeft = true;
          ++ich;
        }
        boolean zeroPad = false;
        if (strFormat.charAt(ich) == '0') {
          zeroPad = true;
          ++ich;
        }
        char ch;
        int width = 0;
        while ((ch = strFormat.charAt(ich)) >= '0' && (ch <= '9')) {
          width = (10 * width) + (ch - '0');
          ++ich;
        }
        int precision = Integer.MAX_VALUE;
        boolean isExponential = false;
        if (strFormat.charAt(ich) == '.') {
          ++ich;
          if ((ch = strFormat.charAt(ich)) == '-') {
            isExponential = true;
            ++ich;
          } 
          if ((ch = strFormat.charAt(ich)) >= '0' && ch <= '9') {
            precision = ch - '0';
            ++ich;
          }
          if (isExponential)
            precision = -precision - (strT == null ? 1 : 0);
        }
        String st = strFormat.substring(ich, ich + len);
        if (!st.equals(key)) {
          ich = ichPercent + 1;
          strLabel += '%';
          continue;
        }
        ich += len;
        if (!Float.isNaN(floatT))
          strLabel += TextFormat.format(floatT, width, precision, alignLeft,
              zeroPad);
        else if (strT != null)
          strLabel += TextFormat.format(strT, width, precision, alignLeft,
              zeroPad);
        else if (!Double.isNaN(doubleT))
          strLabel += TextFormat.format(doubleT, width, precision, alignLeft,
              zeroPad);
        if (doOne)
          break;
      } catch (IndexOutOfBoundsException ioobe) {
        ich = ichPercent;
        break;
      }
    }
    strLabel += strFormat.substring(ich);
    
      
    return strLabel;
  }

  
  public static String formatCheck(String strFormat) {
    if (strFormat == null || strFormat.indexOf('p') < 0 && strFormat.indexOf('q') < 0)
      return strFormat;
    strFormat = simpleReplace(strFormat, "%%", "\1");
    strFormat = simpleReplace(strFormat, "%p", "%6.2p");
    strFormat = simpleReplace(strFormat, "%q", "%6.2q");
    String[] format = split(strFormat, '%');
    StringBuffer sb = new StringBuffer();
    sb.append(format[0]);
    for (int i = 1; i < format.length; i++) {
      String f = "%" + format[i];
      int pt;
      if (f.length() >= 3) {
        if ((pt = f.indexOf('p')) >= 0)
          f = fdup(f, pt, 3);
        if ((pt = f.indexOf('q')) >= 0)
          f = fdup(f, pt, 4);
      }
      sb.append(f);
    }
    return sb.toString().replace('\1', '%');
  }

  
  private static String fdup(String f, int pt, int n) {
    char ch;
    int count = 0;
    for (int i = pt; --i >= 1; ) {
      if (Character.isDigit(ch = f.charAt(i)))
        continue;
      switch (ch) {
      case '.':
        if (count++ != 0)
          return f;
        continue;
      case '-':
        if (i != 1)
          return f;
        continue;
      default:
        return f;
      }
    }
    String s = f.substring(0, pt + 1);
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < n; i++)
      sb.append(s);
    sb.append(f.substring(pt + 1));
    return sb.toString();
  }

  
  public static String[] split(String text, String run) {
    int n = 1;
    int i = text.indexOf(run);
    String[] lines;
    int runLen = run.length();
    if (i < 0 || runLen == 0) {
      lines = new String[1];
      lines[0] = text;
      return lines;
    }
    int len = text.length() - runLen;
    for (; i >= 0 && i < len; n++)
      i = text.indexOf(run, i + runLen);
    lines = new String[n];
    i = 0;
    int ipt = 0;
    int pt = 0;
    for (; (ipt = text.indexOf(run, i)) >= 0 && pt + 1 < n;) {
      lines[pt++] = text.substring(i, ipt);
      i = ipt + runLen;
    }
    if (text.indexOf(run, len) != len)
      len += runLen;
    lines[pt] = text.substring(i, len);
    return lines;
  }

  
  public static String replaceAllCharacters(String str, String strFrom,
                                            String strTo) {
    for (int i = strFrom.length(); --i >= 0;) {
      String chFrom = strFrom.substring(i, i + 1);
      str = simpleReplace(str, chFrom, strTo);
    }
    return str;
  }
  
  
  public static String replaceAllCharacters(String str, String strFrom,
                                            char chTo) {
    for (int i = strFrom.length(); --i >= 0;)
      str = str.replace(strFrom.charAt(i), chTo);
    return str;
  }
  
  
  public static String simpleReplace(String str, String strFrom, String strTo) {
    if (str == null || str.indexOf(strFrom) < 0 || strFrom.equals(strTo))
      return str;
    int fromLength = strFrom.length();
    if (fromLength == 0)
      return str;
    boolean isOnce = (strTo.indexOf(strFrom) >= 0);
    int ipt;
    while (str.indexOf(strFrom) >= 0) {
      StringBuffer s = new StringBuffer();
      int ipt0 = 0;
      while ((ipt = str.indexOf(strFrom, ipt0)) >= 0) {
        s.append(str.substring(ipt0, ipt)).append(strTo);
        ipt0 = ipt + fromLength;
      }
      s.append(str.substring(ipt0));
      str = s.toString();
      if (isOnce)
        break;
    }

    return str;
  }

  public static String trim(String str, String chars) {
    if (chars.length() == 0)
      return str.trim();
    int len = str.length();
    int k = 0;
    while (k < len && chars.indexOf(str.charAt(k)) >= 0)
      k++;
    int m = str.length() - 1;
    while (m > k && chars.indexOf(str.charAt(m)) >= 0)
      m--;
    return str.substring(k, m + 1);
  }

  public static String[] split(String text, char ch) {
    return split(text, "" + ch);
  }
  
  public static void lFill(StringBuffer s, String s1, String s2) {
    s.append(s2);
    int n = s1.length() - s2.length();
    if (n > 0)
      s.append(s1.substring(0, n));
  }
  
  public static void rFill(StringBuffer s, String s1, String s2) {
    int n = s1.length() - s2.length();
    if (n > 0)
      s.append(s1.substring(0, n));
    s.append(s2);
  }
  
  public static String safeTruncate(float f, int n) {
    if (f > -0.001 && f < 0.001)
      f = 0;
    return (f + "         ").substring(0,n);
  }

  public static boolean isWild(String s) {
    return s != null && (s.indexOf("*") >= 0 || s.indexOf("?") >= 0);
  }

  public static boolean isMatch(String s, String strWildcard,
                                boolean checkStar, boolean allowInitialStar) {
    int ich = 0;
    int cchWildcard = strWildcard.length();
    int cchs = s.length();
    if (cchs == 0 || cchWildcard == 0)
      return (cchs == cchWildcard || cchWildcard == 1 && strWildcard.charAt(0) == '*');
    boolean isStar0 = (checkStar && allowInitialStar ? strWildcard.charAt(0) == '*' : false);
    if (isStar0 && strWildcard.charAt(cchWildcard - 1) == '*')
      return (cchWildcard < 3 || s.indexOf(strWildcard.substring(1,
          cchWildcard - 1)) >= 0); 
    String qqq = "????";
    while (qqq.length() < s.length())
      qqq += qqq;
    if (checkStar) {
      if (allowInitialStar && isStar0)
        strWildcard = qqq + strWildcard.substring(1);
      if (strWildcard.charAt(ich = strWildcard.length() - 1) == '*')
        strWildcard = strWildcard.substring(0, ich) + qqq;
      cchWildcard = strWildcard.length();
    }

    if (cchWildcard < cchs)
      return false;

    ich = 0;

    

    
    

    

    
    
    

    while (cchWildcard > cchs) {
      if (allowInitialStar && strWildcard.charAt(ich) == '?') {
        ++ich;
      } else if (strWildcard.charAt(ich + cchWildcard - 1) != '?') {
        return false;
      }
      --cchWildcard;
    }

    for (int i = cchs; --i >= 0;) {
      char charWild = strWildcard.charAt(ich + i);
      if (charWild == '?')
        continue;
      if (charWild != s.charAt(i) && (charWild != '\1' || s.charAt(i) != '?'))
          return false;
    }
    return true;
  }

  public static String join(String[] s, char c, int i0) {
    if (s.length < i0)
      return null;
    StringBuffer sb = new StringBuffer(s[i0++]);
    for (int i = i0; i < s.length; i++)
      sb.append(c).append(s[i]);
    return sb.toString();
  }

  public static String replaceQuotedStrings(String s, Vector list,
                                            Vector newList) {
    int n = list.size();
    for (int i = 0; i < n; i++) {
      String name = (String) list.get(i);
      String newName = (String) newList.get(i);
      if (!newName.equals(name))
        s = TextFormat.simpleReplace(s, "\"" + name + "\"", "\"" + newName
            + "\"");
    }
    return s;
  }

  public static String replaceStrings(String s, Vector list,
                                            Vector newList) {
    int n = list.size();
    for (int i = 0; i < n; i++) {
      String name = (String) list.get(i);
      String newName = (String) newList.get(i);
      if (!newName.equals(name))
        s = TextFormat.simpleReplace(s, name, newName);
    }
    return s;
  }

}
