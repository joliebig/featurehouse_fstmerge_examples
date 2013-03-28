
package org.jmol.util;

import java.util.BitSet;


public class SlowBitSet extends FastBitSet {

  public SlowBitSet() {
    super();
  }
  
  public static FastBitSet allocateBitmap(int count) {
    return new SlowBitSet(count, true);
  }

  private BitSet bs;

  protected SlowBitSet(int count, boolean asBits) {
    bs = new BitSet(asBits ? count : count * 64 ); 
  }
  
  public FastBitSet copyFast() {
    return (SlowBitSet) this.clone();
  }

  public boolean getBit(int i) {
    return bs.get(i);
  }

  public int getPointCount(int dotCount) {
    return bs.cardinality();
  }

  public int getSize() {
    return bs.size();
  }
  
  public void setBit(int i) {
   bs.set(i);
  }

  public void clearBit(int i) {
    bs.clear(i);
  }

  public void setAllBits(int count) {
    bs.set(0, count);
  }
  
  public void clearBitmap() {
    bs.clear();
  }

  public int getMapStorageCount() {
    return bs.size() * 64;
  }

  public int getCardinality() {
    return bs.cardinality();
  }

  public BitSet toBitSet() {
    return BitSetUtil.copy(bs);
  }

  public Object clone() {
    SlowBitSet result = new SlowBitSet();
    result.bs = (BitSet) bs.clone();
    return result;
  }

  public String toString() {
    return Escape.escape(bs);
  }
      
  public int hashCode() {
    return bs.hashCode();
  }
}
