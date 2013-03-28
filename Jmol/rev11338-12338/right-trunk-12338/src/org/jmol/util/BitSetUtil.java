
package org.jmol.util;

import java.util.BitSet;

final public class BitSetUtil {

  public static final BitSet bsNull= new BitSet();

  public static boolean areEqual(BitSet a, BitSet b) {
    return (a == null || b == null ? a == null && b == null : a.equals(b));
  }

  public static boolean haveCommon(BitSet a, BitSet b, BitSet bsTemp) {
    return (a == null || b == null ? false : a.intersects(b));
  }

  
  public static int cardinalityOf(BitSet bs) {
    return (bs == null ? 0 : bs.cardinality());
  }

  public static BitSet newBitSet(int i0, int i1) {
    BitSet bs = new BitSet(i1);
    bs.set(i0, i1);
    return bs;
  }
  
  public static BitSet setAll(int n) {
    BitSet bs = new BitSet(n);
    bs.set(0, n);
    return bs;
  }

  public static BitSet andNot(BitSet a, BitSet b) {
    if (b != null)
      a.andNot(b);
    return a;
  }

  public static BitSet copy(BitSet bs) {
    return bs == null ? null : (BitSet) bs.clone();
  }

  public static BitSet copy(BitSet a, BitSet b) {
    if (a == null || b == null)
      return null;
    b.clear();
    b.or(a);
    return b;
  }
  
  public static BitSet copyInvert(BitSet bs, int n) {
    return (bs == null ? null : andNot(setAll(n), bs));
  }
  
  
  public static BitSet invertInPlace(BitSet bs, int n) {
    return copy(copyInvert(bs, n), bs);
  }

  
  public static BitSet toggleInPlace(BitSet a, BitSet b) {
    if (a.equals(b)) {
      
      a.clear();
    } else if (andNot(copy(b), a).length() == 0) {
      
      andNot(a, b); 
    } else {
      
      
      a.or(b);
    }
    return a;
  }
  
  
  public static BitSet deleteBits(BitSet bs, BitSet bsDelete) {
    if (bs == null || bsDelete == null)
      return bs;
    int ipt = bsDelete.nextSetBit(0);
    if (ipt < 0)
      return bs;
    int len = bs.length();
    int lend = Math.min(len, bsDelete.length());
    int i;
    for (i = bsDelete.nextClearBit(ipt); i < lend && i >= 0; i = bsDelete.nextClearBit(i + 1))
      bs.set(ipt++, bs.get(i));
    for (i = lend; i < len; i++)
      bs.set(ipt++, bs.get(i));
    if (ipt < len)
      bs.clear(ipt, len);
    return bs;
  }
}
