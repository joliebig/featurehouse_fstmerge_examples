

package koala.dynamicjava.tree;

import java.math.BigInteger;



public class LongLiteral extends Literal {
  
  public LongLiteral(String rep) throws NumberFormatException {
    this(rep, SourceInfo.NONE);
  }
  
  
  public LongLiteral(String rep, SourceInfo si) throws NumberFormatException {
    super(rep,
          parse(rep.substring(0, rep.length())),
          long.class,
          si);
  }
  
  
  private static Long parse(String s) throws NumberFormatException {
    int radix = 10;
    int start = 0;
    boolean negate = false;
    int end = s.length();
    if (s.endsWith("l") || s.endsWith("L")) { end--; }
    
    if ((end-start>1) && (s.startsWith("-"))) { start++; negate = true; }
    if ((end-start>2) && (s.startsWith("0x",start))) { radix = 16; start += 2; }
    else if ((end-start>1) && (s.startsWith("0",start)) && (s.length() > 1)) { radix = 8; start++; }
    
    BigInteger val = new BigInteger(s.substring(start, end), radix);
    if (negate) { val = val.negate(); }
    long result = val.longValue();
    if (val.bitLength() > 64 || (radix == 10 && !val.equals(BigInteger.valueOf(result)))) {
      throw new NumberFormatException("Literal is out of range: "+s);
    }
    return result;
  }
  
}
