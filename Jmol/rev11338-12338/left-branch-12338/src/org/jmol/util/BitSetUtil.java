
package org.jmol.util;

import java.util.BitSet;

final public class BitSetUtil {

  
  public static int length(BitSet bs) {
    
    
    
    
    
    int n = bs.size();
    while (--n >= 0)
      if (bs.get(n))
        break;
    return n + 1;
  }

  public static int firstSetBit(BitSet bs) {
    int n;
    if (bs == null || (n = bs.size()) == 0)
      return -1;
    for (int i = 0; i < n; i++)
      if (bs.get(i))
        return i;
    return -1;
  }

  
  public static int cardinalityOf(BitSet bs) {
    int n = 0;
    if (bs != null)
      for (int i = bs.size(); --i >= 0;)
        if (bs.get(i))
          n++;
    return n;
  }

  public static BitSet setAll(int n) {
    BitSet bs = new BitSet(n);
    for (int i = n; --i >= 0;)
      bs.set(i);
    return bs;
  }

  public static void andNot(BitSet a, BitSet b) {
    if (b == null)
      return;
    for (int i = b.size(); --i >= 0;)
      if (b.get(i))
        a.clear(i);
  }

  public static BitSet copy(BitSet bs) {
    return bs == null ? null : (BitSet) bs.clone();
    
  }

  public static void copy(BitSet a, BitSet b) {
    b.clear();
    b.or(a);
  }
  
  
  private final static BitSet bsNull = new BitSet();
  public static void clear(BitSet bs) {
    bs.and(bsNull);
  }

  public static BitSet copyInvert(BitSet bs, int n) {
    if (bs == null)
      return null;
    BitSet allButN = setAll(n);
    andNot(allButN, bs);
    return allButN;
  }
  
  
  public static BitSet invertInPlace(BitSet bs, int n) {
    for (int i = n; --i >= 0;) {
      if (bs.get(i))
        bs.clear(i);
      else
        bs.set(i);
    }
    return bs;
  }

  
  public static BitSet toggleInPlace(BitSet a, BitSet b, int n) {
    for (int i = n; --i >= 0;) {
      if (!b.get(i))
        continue;
      if (a.get(i)) { 
        a.clear(i);
      } else {
        a.or(b); 
        break;
      }
    }
    return a;
  }
  
  public static boolean compareBits(BitSet a, BitSet b) {
    if (a == null || b == null)
      return a == null && b == null;
    for (int i = Math.max(a.size(), b.size()); --i >= 0; )
      if (a.get(i) != b.get(i))
        return false;
    return true;
  }

  public static boolean haveCommon(BitSet a, BitSet b) {
    if (a == null || b == null)
      return false;
    for (int i = Math.min(a.size(), b.size()); --i >= 0; )
      if (a.get(i) && b.get(i))
        return true;
    return false;
  }
  
  public static BitSet deleteBits(BitSet bs, BitSet bsDelete) {
    
    if (bs == null || bsDelete == null)
      return bs;
    int ipt = firstSetBit(bsDelete);
    if (ipt < 0)
      return bs;
    int len = bs.length();
    int lend = Math.min(len, bsDelete.length());
    int i;
    for (i = ipt + 1; i < lend; i++)
      if (!bsDelete.get(i))
        bs.set(ipt++, bs.get(i));
    for (i = lend; i < len; i++)
      bs.set(ipt++, bs.get(i));
    for (i = ipt; i < len; i++)
      bs.clear(i);
    return bs;
  }


}
