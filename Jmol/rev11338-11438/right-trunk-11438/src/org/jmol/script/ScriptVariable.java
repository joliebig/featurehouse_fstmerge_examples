

package org.jmol.script;

import java.util.BitSet;
import java.util.Vector;

import javax.vecmath.Point3f;
import javax.vecmath.Point4f;
import javax.vecmath.Vector3f;

import org.jmol.modelset.Bond.BondSet;
import org.jmol.util.ArrayUtil;
import org.jmol.util.BitSetUtil;
import org.jmol.util.Escape;
import org.jmol.util.Logger;
import org.jmol.util.Measure;
import org.jmol.util.Parser;
import org.jmol.util.Quaternion;
import org.jmol.util.TextFormat;

public class ScriptVariable extends Token {

  final private static ScriptVariable vT = new ScriptVariable(on, 1, "true");
  final private static ScriptVariable vF = new ScriptVariable(off, 0, "false");
  final static ScriptVariable vAll = new ScriptVariable(all, "all");

  public int index = Integer.MAX_VALUE;

  private final static int FLAG_CANINCREMENT = 1;
  private final static int FLAG_LOCALVAR = 2;

  private int flags = ~FLAG_CANINCREMENT & FLAG_LOCALVAR;
  private String name;

  public ScriptVariable() {
    tok = string;
    value = "";
  }

  public ScriptVariable(int tok) {
    this.tok = tok;
  }

  public ScriptVariable(int tok, int intValue, Object value) {
    super(tok, intValue, value);
  }

  public ScriptVariable(int tok, Object value) {
    super(tok, value);
  }

  public ScriptVariable(int tok, int intValue) {
    super(tok, intValue);
  }

  public ScriptVariable(BitSet bs, int index) {
    value = bs;
    this.index = index;
    tok = bitset;
  }

  public ScriptVariable(Token theToken) {
    tok = theToken.tok;
    intValue = theToken.intValue;
    value = theToken.value;
  }
  
  static public boolean isVariableType(Object x) {
    return (x instanceof ScriptVariable
        || x instanceof BitSet
        || x instanceof Boolean
        || x instanceof Float
        || x instanceof Integer
        || x instanceof Point3f    
        || x instanceof Vector3f   
        || x instanceof Point4f    
        || x instanceof Quaternion 
        || x instanceof String

        || x instanceof Vector     
        || x instanceof double[]   
        || x instanceof float[]    
        || x instanceof Float[]    
        || x instanceof int[]      
        || x instanceof String[]); 
  }

  static public String typeOf(ScriptVariable x) {
    int tok = (x == null ? nada : x.tok);
    switch (tok) {
    case on:
    case off:
      return "boolean";
    case integer:
    case decimal:
    case point3f:
    case point4f:
    case string:
    case list:
      return astrType[tok];
    }
    return "?";
  }

  public static int sizeOf(Token x) {
    switch (x == null ? nada : x.tok) {
    case bitset:
      return BitSetUtil.cardinalityOf(bsSelect(x));
    case on:
    case off:
      return -1;
    case integer:
      return -2;
    case decimal:
      return -4;
    case point3f:
      return -8;
    case point4f:
      return -16;
    case string:
      return ((String) x.value).length();
    case list:
      return x.intValue == Integer.MAX_VALUE ? ((String[]) x.value).length
          : sizeOf(selectItem(x));
    default:
      return 0;
    }
  }

  public static ScriptVariable intVariable(int intValue) {
    return new ScriptVariable(integer, intValue);
  }

  
  public static ScriptVariable getVariable(Object x) {
    if (x == null)
      return new ScriptVariable();
    if (x instanceof ScriptVariable)
      return (ScriptVariable) x;
    
    
    
    
    
    
    
    if (x instanceof Boolean)
      return getBoolean(((Boolean)x).booleanValue());
    if (x instanceof Integer)
      return new ScriptVariable(integer, ((Integer) x).intValue());
    if (x instanceof Float)
      return new ScriptVariable(decimal, x);
    if (x instanceof String) {
      x = unescapePointOrBitsetAsVariable((String) x);
      if (x instanceof ScriptVariable)
        return (ScriptVariable) x;
      return new ScriptVariable(string, x);
    }
    if (x instanceof String)
      return new ScriptVariable(string, x);
    if (x instanceof Point3f)
      return new ScriptVariable(point3f, x);
    if (x instanceof Vector3f)
      return new ScriptVariable(point3f, new Point3f((Vector3f) x));
    if (x instanceof Point4f)
      return new ScriptVariable(point4f, x);
    
    
    
    
    
    
    if (x instanceof Quaternion)
      return new ScriptVariable(point4f, ((Quaternion)x).toPoint4f());
    if (x instanceof BitSet)
      return new ScriptVariable(bitset, x);
    if (x instanceof String[])
      return new ScriptVariable(list, x);
    if (x instanceof Float[])
      return new ScriptVariable(listf, x);
    
    
    
    
    if (x instanceof int[]) {
      int[] ix = (int[]) x;
      String[] s = new String[ix.length];
      for (int i = ix.length; --i >= 0; )
        s[i] = "" + ix[i];
      return new ScriptVariable(list, s);
    }
    if (x instanceof float[]) {
      float[] f = (float[]) x;
      String[] s = new String[f.length];
      for (int i = f.length; --i >= 0; )
        s[i] = "" + f[i];
      return new ScriptVariable(list, s);
    }
    if (x instanceof double[]) {
      double[] f = (double[]) x;
      String[] s = new String[f.length];
      for (int i = f.length; --i >= 0; )
        s[i] = "" + f[i];
      return new ScriptVariable(list, s);
    }
    if (x instanceof Vector) {
        
        Vector v = (Vector) x;
        int len = v.size();
        String[] list = new String[len];
        for (int i = 0; i < len; i++) {
          Object o = v.elementAt(i);
          if (o instanceof String)
            list[i] = (String) o;
          else
            list[i] = Escape.toReadable(o);
        }
        return getVariable(list);
    }

    
    Logger.error("ScriptVariable: unrecognized type for " + x.toString());

    return null;
  }

  public ScriptVariable set(ScriptVariable v) {
    index = v.index;
    intValue = v.intValue;
    tok = v.tok;
    if (tok == Token.list) {
      int n = ((String[])v.value).length;
      value = new String[n];
      System.arraycopy(v.value, 0, value, 0, n);
    } else {
      value = v.value;
    }
    return this;
  }

  public ScriptVariable setName(String name) {
    this.name = name;
    flags |= FLAG_CANINCREMENT;
    
    return this;
  }

  public ScriptVariable setGlobal() {
    flags &= ~FLAG_LOCALVAR;
    return this;
  }

  public boolean canIncrement() {
    return tokAttr(flags, FLAG_CANINCREMENT);
  }

  public boolean increment(int n) {
    if (!canIncrement())
      return false;
    switch (tok) {
    case integer:
      intValue += n;
      break;
    case decimal:
      value = new Float(((Float) value).floatValue() + n);
      break;
    default:
      value = nValue(this);
      if (value instanceof Integer) {
        tok = integer;
        intValue = ((Integer) value).intValue();
      } else {
        tok = decimal;
      }
    }
    return true;
  }

  public static ScriptVariable getVariableSelected(int index, Object value) {
    ScriptVariable v = new ScriptVariable(bitset, value);
    v.index = index;
    return v;
  }

  public boolean asBoolean() {
    return bValue(this);
  }

  public int asInt() {
    return iValue(this);
  }

  public float asFloat() {
    return fValue(this);
  }

  public String asString() {
    return sValue(this);
  }

  public Object getValAsObj() {
    return (tok == integer ? new Integer(intValue) : value);
  }

  

  private final static Point3f pt0 = new Point3f();

  
  
  public static Object oValue(ScriptVariable x) {
    switch (x == null ? nada : x.tok) {
    case on:
      return Boolean.TRUE;
    case nada:
    case off:
      return Boolean.FALSE;
    case integer:
      return new Integer(x.intValue);
    default:
      return x.value;
    }
  }

  
  public static Object nValue(Token x) {
    int iValue;
    switch (x == null ? nada : x.tok) {
    case decimal:
      return x.value;
    case integer:
      iValue = x.intValue;
      break;
    case string:
      if (((String) x.value).indexOf(".") >= 0)
        return new Float(toFloat((String) x.value));
      iValue = (int) toFloat((String) x.value);
      break;
    default:
      iValue = 0;
    }
    return new Integer(iValue);
  }

  
  
  
  public static boolean bValue(Token x) {
    switch (x == null ? nada : x.tok) {
    case on:
      return true;
    case off:
      return false;
    case integer:
      return x.intValue != 0;
    case decimal:
    case string:
    case list:
      return fValue(x) != 0;
    case bitset:
      return iValue(x) != 0;
    case point3f:
    case point4f:
      return Math.abs(fValue(x)) > 0.0001f;
    default:
      return false;
    }
  }

  public static int iValue(Token x) {
    switch (x == null ? nada : x.tok) {
    case on:
      return 1;
    case off:
      return 0;
    case integer:
      return x.intValue;
    case decimal:
    case list:
    case string:
    case point3f:
    case point4f:
      return (int) fValue(x);
    case bitset:
      return BitSetUtil.cardinalityOf(bsSelect(x));
    default:
      return 0;
    }
  }

  public static float fValue(Token x) {
    switch (x == null ? nada : x.tok) {
    case on:
      return 1;
    case off:
      return 0;
    case integer:
      return x.intValue;
    case decimal:
      return ((Float) x.value).floatValue();
    case list:
      int i = x.intValue;
      String[] list = (String[]) x.value;
      if (i == Integer.MAX_VALUE)
        return list.length;
    case string:
      return toFloat(sValue(x));
    case bitset:
      return iValue(x);
    case point3f:
      return ((Point3f) x.value).distance(pt0);
    case point4f:
      return Measure.distanceToPlane((Point4f) x.value, pt0);
    default:
      return 0;
    }
  }

  public static String sValue(Token x) {
    if (x == null)
      return "";
    int i;
    switch (x.tok) {
    case on:
      return "true";
    case off:
      return "false";
    case integer:
      return "" + x.intValue;
    case point3f:
      return Escape.escape((Point3f) x.value);
    case point4f:
      return Escape.escape((Point4f) x.value);
    case bitset:
      return Escape.escape(bsSelect(x), !(x.value instanceof BondSet));
    case list:
      String[] list = (String[]) x.value;
      i = x.intValue;
      if (i <= 0)
        i = list.length - i;
      if (i != Integer.MAX_VALUE)
        return (i < 1 || i > list.length ? "" : list[i - 1]);
      StringBuffer sb = new StringBuffer();
      for (i = 0; i < list.length; i++)
        sb.append(list[i]).append("\n");
      return sb.toString();
    case string:
      String s = (String) x.value;
      i = x.intValue;
      if (i <= 0)
        i = s.length() - i;
      if (i == Integer.MAX_VALUE)
        return s;
      if (i < 1 || i > s.length())
        return "";
      return "" + s.charAt(i - 1);
    case decimal:
    default:
      return "" + x.value;
    }
  }

  private static float toFloat(String s) {
    if (s.equalsIgnoreCase("true"))
      return 1;
    if (s.equalsIgnoreCase("false") || s.length() == 0)
      return 0;
    return Parser.parseFloatStrict(s);
  }

  public static String[] concatList(ScriptVariable x1, ScriptVariable x2) {
    String[] list1 = (x1.tok == list ? (String[]) x1.value : TextFormat.split(
        sValue(x1), "\n"));
    String[] list2 = (x2.tok == list ? (String[]) x2.value : TextFormat.split(
        sValue(x2), "\n"));
    String[] list = new String[list1.length + list2.length];
    int pt = 0;
    for (int i = 0; i < list1.length; i++)
      list[pt++] = list1[i];
    for (int i = 0; i < list2.length; i++)
      list[pt++] = list2[i];
    return list;
  }

  public static BitSet bsSelect(Token token) {
    token = selectItem(token, Integer.MIN_VALUE);
    return (BitSet) token.value;
  }

  public static BitSet bsSelect(ScriptVariable var) {
    if (var.index == Integer.MAX_VALUE)
      var = selectItem(var);
    return (BitSet) var.value;
  }

  public static BitSet bsSelect(Token token, int n) {
    token = selectItem(token);
    token = selectItem(token, 1);
    token = selectItem(token, n);
    return (BitSet) token.value;
  }

  public static ScriptVariable selectItem(ScriptVariable var) {
    
    
    if (var.index != Integer.MAX_VALUE || 
        var.tok == list && var.intValue == Integer.MAX_VALUE)
      return var;
    return (ScriptVariable) selectItem(var, Integer.MIN_VALUE);
  }

  public static Token selectItem(Token var) {
    return selectItem(var, Integer.MIN_VALUE);
  }

  public static ScriptVariable selectItem(ScriptVariable var, int i2) {
    return (ScriptVariable) selectItem((Token) var, i2);
  }

  public static Token selectItem(Token tokenIn, int i2) {
    if (tokenIn.tok != bitset && tokenIn.tok != list && tokenIn.tok != string)
      return tokenIn;

    

    BitSet bs = null;
    String[] st = null;
    String s = null;

    int i1 = tokenIn.intValue;
    if (i1 == Integer.MAX_VALUE) {
      
      
      
      
      if (i2 == Integer.MIN_VALUE)
        i2 = i1;
      return new ScriptVariable(tokenIn.tok, i2, tokenIn.value);
    }
    int len = 0;
    boolean isInputSelected = (tokenIn instanceof ScriptVariable 
        && ((ScriptVariable) tokenIn).index != Integer.MAX_VALUE);
    ScriptVariable tokenOut = new ScriptVariable(tokenIn.tok, Integer.MAX_VALUE);

    switch (tokenIn.tok) {
    case bitset:
      if (tokenIn.value instanceof BondSet) {
        tokenOut.value = new BondSet((BitSet) tokenIn.value,
            ((BondSet) tokenIn.value).getAssociatedAtoms());
        bs = (BitSet) tokenOut.value;
        len = BitSetUtil.cardinalityOf(bs);
        break;
      }
      bs = BitSetUtil.copy((BitSet) tokenIn.value);
      len = (isInputSelected ? 1 : BitSetUtil.cardinalityOf(bs));
      tokenOut.value = bs;
      break;
    case list:
      st = (String[]) tokenIn.value;
      len = st.length;
      break;
    case string:
      s = (String) tokenIn.value;
      len = s.length();
    }

    
    
    
    
    
    if (i1 <= 0)
      i1 = len + i1;
    if (i1 < 1)
      i1 = 1;
    if (i2 == 0)
      i2 = len;
    else if (i2 < 0)
      i2 = len + i2;

    if (i2 > len)
      i2 = len;
    else if (i2 < i1)
      i2 = i1;

    switch (tokenIn.tok) {
    case bitset:
      if (isInputSelected) {
        if (i1 > 1)
          bs.clear();
        break;
      }
      len = BitSetUtil.length(bs);
      int n = 0;
      for (int j = 0; j < len; j++)
        if (bs.get(j) && (++n < i1 || n > i2))
          bs.clear(j);
      break;
    case string:
      if (i1 < 1 || i1 > len)
        tokenOut.value = "";
      else
        tokenOut.value = s.substring(i1 - 1, i2);
      break;
    case list:
      if (i1 < 1 || i1 > len || i2 > len)
        return new ScriptVariable(string, "");
      if (i2 == i1)
        return fromString(st[i1 - 1]);
      String[] list = new String[i2 - i1 + 1];
      for (int i = 0; i < list.length; i++)
        list[i] = st[i + i1 - 1];
      tokenOut.value = list;
      break;
    }
    return tokenOut;
  }

  
  public static ScriptVariable fromString(String str) {
    Object v = unescapePointOrBitsetAsVariable(str);
    if (!(v instanceof String))
      return (ScriptVariable) v;
    String s = (String) v;
    if (s.toLowerCase() == "true")
      return getBoolean(true);
    if (s.toLowerCase() == "false")
      return getBoolean(false);
    float f = Parser.parseFloatStrict(s);
    return (Float.isNaN(f) ? new ScriptVariable(string, v) 
        : s.indexOf(".") < 0 ? new ScriptVariable(integer, (int) f)
        : new ScriptVariable(decimal, new Float(f)));
  }

  public boolean setSelectedValue(int selector, ScriptVariable var) {
    if (selector == Integer.MAX_VALUE || tok != string && tok != list)
      return false;
    String s = sValue(var);
    switch (tok) {
    case list:
      String[] array = (String[]) value;
      if (selector <= 0)
        selector = array.length + selector;
      if (--selector < 0)
        selector = 0;
      String[] arrayNew = array;
      if (arrayNew.length <= selector) {
        value = arrayNew = ArrayUtil.ensureLength(array, selector + 1);
        for (int i = array.length; i <= selector; i++)
          arrayNew[i] = "";
      }
      arrayNew[selector] = s;
      break;
    case string:
      String str = (String) value;
      int pt = str.length();
      if (selector <= 0)
        selector = pt + selector;
      if (--selector < 0)
        selector = 0;
      while (selector >= str.length())
        str += " ";
      value = str.substring(0, selector) + s + str.substring(selector + 1);
      break;
    }
    return true;
  }

  public String escape() {
    switch (tok) {
    case Token.on:
      return "true";
    case Token.off:
      return "false";
    case Token.integer:
      return "" + intValue;
    case Token.bitset:
      return Escape.escape((BitSet)value);
    case Token.list:
      return Escape.escape((String[])value);
    case Token.point3f:
      return Escape.escape((Point3f)value);
    case Token.point4f:
      return Escape.escape((Point4f)value);
    default:
      return Escape.escape(value);
    }
  }

  public static Object unescapePointOrBitsetAsVariable(String s) {
    if (s == null || s.length() == 0)
      return s;
    Object v = Escape.unescapePointOrBitset(s);
    if (v instanceof Point3f)
      return (new ScriptVariable(Token.point3f, v));
    if (v instanceof Point4f)
      return new ScriptVariable(Token.point4f, v);
    if (v instanceof BitSet)
      return new ScriptVariable(Token.bitset, v);
    return s;
  }

  public static ScriptVariable getBoolean(boolean value) {
    return new ScriptVariable(value ? vT : vF);
  }
  
  public static Object sprintf(String strFormat, ScriptVariable var) {
    if (var == null)
      return strFormat;
    int[] vd = (strFormat.indexOf("d") >= 0 || strFormat.indexOf("i") >= 0 ? new int[1]
        : null);
    float[] vf = (strFormat.indexOf("f") >= 0 ? new float[1] : null);
    double[] ve = (strFormat.indexOf("e") >= 0 ? new double[1] : null);
    boolean getS = (strFormat.indexOf("s") >= 0);
    boolean getP = (strFormat.indexOf("p") >= 0 && var.tok == Token.point3f
        || strFormat.indexOf("q") >= 0 && var.tok == Token.point4f);
    Object[] of = new Object[] { vd, vf, ve, null, null};
    if (var.tok != Token.list)
      return sprintf(strFormat, var, of, vd, vf, ve, getS, getP);
    String[] list = (String[]) var.value;
    String[] list2 = new String[list.length];
    for (int i = 0; i < list.length; i++) {
      String s = strFormat;
      list2[i] = sprintf(s, fromString(list[i]), of, vd, vf, ve, getS, getP);
    }
    return list2;
  }

  private static String sprintf(String strFormat, ScriptVariable var, Object[] of, 
                                int[] vd, float[] vf, double[] ve, boolean getS, boolean getP) {
    if (vd != null)
      vd[0] = iValue(var);
    if (vf != null)
      vf[0] = fValue(var);
    if (ve != null)
      ve[0] = fValue(var);
    if (getS)
      of[3] = sValue(var);
    if (getP)
      of[4]= var.value;
    return TextFormat.sprintf(strFormat, of );
  }

  
  public static String sprintf(ScriptVariable[] args) {
    switch(args.length){
    case 0:
      return "";
    case 1:
      return sValue(args[0]);
    }
    String[] format = TextFormat.split(TextFormat.simpleReplace(sValue(args[0]), "%%","\1"), '%');
    StringBuffer sb = new StringBuffer();
    sb.append(format[0]);
    for (int i = 1; i < format.length; i++)
      sb.append(sprintf(TextFormat.formatCheck("%" + format[i]), (i < args.length ? args[i] : null)));
    return sb.toString();
  }
  
  public String toString() {
    return super.toString() + "[" + name + "] index =" + index + " hashcode=" + hashCode();
  }

}
