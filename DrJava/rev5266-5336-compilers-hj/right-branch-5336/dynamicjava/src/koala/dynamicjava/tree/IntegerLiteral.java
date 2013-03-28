

package koala.dynamicjava.tree;

import java.math.BigInteger;



public class IntegerLiteral extends Literal {
  
  public IntegerLiteral(String rep) throws NumberFormatException {
    this(rep, SourceInfo.NONE);
  }
  
  
  public IntegerLiteral(String rep, SourceInfo si) throws NumberFormatException {
    super(rep,
          parse(rep),
          int.class,
          si);
  }
  
  
  private static Integer parse(String s) throws NumberFormatException {
    int radix = 10;
    int start = 0;
    boolean negate = false;
    int end = s.length();
    
    if ((end-start>1) && (s.startsWith("-"))) { start++; negate = true; }
    if ((end-start>2) && (s.startsWith("0x",start))) { radix = 16; start += 2; }
    else if ((end-start>1) && (s.startsWith("0",start)) && (s.length() > 1)) { radix = 8; start++; }
    
    BigInteger val = new BigInteger(s.substring(start), radix);
    if (negate) { val = val.negate(); }
    int result = val.intValue();
    if (val.bitLength() > 32 || (radix == 10 && !val.equals(BigInteger.valueOf(result)))) {
      throw new NumberFormatException("Literal is out of range: "+s);
    }
    return result;
  }
  
}
