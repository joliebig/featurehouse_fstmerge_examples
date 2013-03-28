
package org.jmol.util;

import java.util.BitSet;



  
public class FastBitSet implements Cloneable {

  
  
  private int[] bitmap;
  
  private final static int[] emptyBitmap = new int[0];

  public FastBitSet() {
    bitmap = emptyBitmap;
  }

  private FastBitSet(int bitCount) {
    bitmap = new int[getWordCountFromBitCount(bitCount)];
  }

  public FastBitSet(FastBitSet bitsetToCopy) {
    int wordCount = bitmapGetMinimumWordCount(bitsetToCopy.bitmap);
    if (wordCount == 0)
      bitmap = emptyBitmap;
    else {
      bitmap = new int[wordCount];
      System.arraycopy(bitsetToCopy.bitmap, 0, bitmap, 0, wordCount);
    }
  }

  public final static FastBitSet emptySet = new FastBitSet();
  public final static FastBitSet getEmptySet() {
    return emptySet;
  }

  public static FastBitSet allocateBitmap(int bitCount) {
    return new FastBitSet(bitCount);
  }

  public void and(FastBitSet setAnd) {
    bitmapAnd(bitmap, setAnd.bitmap);
  }

  public void andNot(FastBitSet setAndNot) {
    bitmapAndNot(bitmap, setAndNot.bitmap);
  }
  
  public int cardinality() {
    return bitmapGetCardinality(bitmap);
  }

  public void clear() {
    bitmapClear(bitmap);
  }

  public void clear(int bitIndex) {
    int wordIndex = bitIndex >> F_ADDRESS_BITS_PER_WORD;
    if (wordIndex < bitmap.length)
      bitmapClearBit(bitmap, bitIndex);
  }

  public void clear(int fromIndex, int toIndex) {
    int bitmapCount = bitmapGetSizeInBits(bitmap);
    if (fromIndex >= bitmapCount)
      return;
    if (toIndex > bitmapCount)
      toIndex = bitmapCount;
    bitmapClearRange(bitmap, fromIndex, toIndex - fromIndex);
  }

  public Object clone() {
    int bitCount = bitmapGetSizeInBits(bitmap);
    FastBitSet result = new FastBitSet(bitCount);
    System.arraycopy(bitmap, 0, result.bitmap, 0, bitmap.length);
    return result;
  }

  public boolean equals(Object obj) {
    if (obj instanceof FastBitSet) {
      FastBitSet bitset2 = (FastBitSet)obj;
      return bitmapIsEqual(bitmap, bitset2.bitmap);
    }
    return false;
  }

  public void flip(int bitIndex) {
    if (get(bitIndex))
      clear(bitIndex);
    else
      set(bitIndex);
  }

  public void flip(int fromIndex, int toIndex) {
    for (int i = fromIndex ; i < toIndex; ++i)
      flip(i);
  }

  public boolean get(int bitIndex) {
    int bitCount = bitmapGetSizeInBits(bitmap);
    if (bitIndex >= bitCount)
      return false;
    return bitmapGetBit(bitmap, bitIndex);
  }

  public boolean isEmpty() {
    return bitmapIsEmpty(bitmap);
  }

  public int length() {
    int i = bitmapGetMinimumWordCount(bitmap) << F_ADDRESS_BITS_PER_WORD;
    while (--i >= 0 && ! bitmapGetBit(bitmap, i))
      ;
    return i + 1;
  }

  public int nextSetBit(int fromIndex) {
    return bitmapNextSetBit(bitmap, fromIndex);
  }

  public void or(FastBitSet setOr) {
    ensureSufficientWords(setOr.bitmap.length);
    bitmapOr(bitmap, setOr.bitmap);
  }

  public void set(int bitIndex) {
    ensureSufficientBits(bitIndex + 1);
    bitmapSetBit(bitmap, bitIndex);
  }

  public void set(int bitIndex, boolean value) {
    if (value)
      set(bitIndex);
    else
      clear(bitIndex);
  }

  public void set(int fromIndex, int toIndex) {
    ensureSufficientBits(toIndex);
    bitmapSetRange(bitmap, fromIndex, toIndex - fromIndex);
  }

  public void set(int fromIndex, int toIndex, boolean value) {
    if (value)
      set(fromIndex, toIndex);
    else
      clear(fromIndex, toIndex);
  }

  public int size() {
    return bitmapGetSizeInBits(bitmap);
  }

  public void xor(FastBitSet setXor) {
    ensureSufficientWords(setXor.bitmap.length);
    bitmapXor(bitmap, setXor.bitmap);
  }

  
  
  
  
  
  

  public BitSet toBitSet() {
    BitSet bs = new BitSet();
    int i = bitmapGetSizeInBits(bitmap);
    while (--i >= 0)
      if (get(i))
	bs.set(i);
    return bs;
  }

  public String toString() {
    return Escape.escape(toBitSet());
  }
      
  public int hashCode() {
    long h = 1234;
    for (int i = bitmap.length; --i >= 0;)
      h ^= bitmap[i] * (i + 1);
    return (int) ((h >> 32) ^ h);
  }

  

  private void ensureSufficientBits(int minimumBitCount) {
    int wordCount =
      (minimumBitCount + F_BIT_INDEX_MASK) >> F_ADDRESS_BITS_PER_WORD;
    if (wordCount > bitmap.length) {
      int[] newBitmap = new int[wordCount];
      System.arraycopy(bitmap, 0, newBitmap, 0, bitmap.length);
      bitmap = newBitmap;
    }
  }

  private void ensureSufficientWords(int minimumWordCount) {
    if (minimumWordCount > bitmap.length) {
      int[] newBitmap = new int[minimumWordCount];
      System.arraycopy(bitmap, 0, newBitmap, 0, bitmap.length);
      bitmap = newBitmap;
    }
  }

  


  

  private final static int F_ADDRESS_BITS_PER_WORD = 5;
  private final static int F_BITS_PER_WORD = 1 << F_ADDRESS_BITS_PER_WORD;
  private final static int F_BIT_INDEX_MASK = F_BITS_PER_WORD - 1;

  private final static int[] bitmapAllocateBitCount(int bitCount) {
    return new int[getWordCountFromBitCount(bitCount)];
  }

  private final static boolean bitmapGetBit(int[] bitmap, int i) {
    return ((bitmap[(i >> F_ADDRESS_BITS_PER_WORD)]
	     >> (i & F_BIT_INDEX_MASK)) & 1) != 0;
  }

  private final static void bitmapSetBit(int[] bitmap, int i) {
    bitmap[(i >> F_ADDRESS_BITS_PER_WORD)] |= 1 << (i & F_BIT_INDEX_MASK);
  }

  private final static void bitmapClearBit(int[] bitmap, int i) {
    bitmap[(i >> F_ADDRESS_BITS_PER_WORD)] &= ~(1 << (i & F_BIT_INDEX_MASK));
  }

  private final static int F_INT_SHIFT_MASK =   0x80000000;
  private final static int F_INT_ALL_BITS_SET = 0xFFFFFFFF;

  private final static void bitmapSetAllBits(int[] bitmap, int bitCount) {
    int wholeWordCount = bitCount >> F_ADDRESS_BITS_PER_WORD;
    int fractionalWordBitCount = bitCount & F_BIT_INDEX_MASK;
    if (fractionalWordBitCount > 0)
      bitmap[wholeWordCount] =
	~(F_INT_SHIFT_MASK >> F_BITS_PER_WORD - 1 - fractionalWordBitCount);
    while (--wholeWordCount >= 0)
      bitmap[wholeWordCount] = F_INT_ALL_BITS_SET;
  }

  private final static void bitmapSetRange(int[] bitmap,
					   int iStart,
					   int bitCount) {
    
    while ((iStart & F_BIT_INDEX_MASK) != 0) {
      bitmapSetBit(bitmap, iStart++);
      if (--bitCount == 0)
	return;
    }
    
    while ((bitCount & F_BIT_INDEX_MASK) != 0) {
	bitmapSetBit(bitmap, iStart + --bitCount);
      }
    
    int wordIndex = iStart >> F_ADDRESS_BITS_PER_WORD;
    int wordCount = bitCount >> F_ADDRESS_BITS_PER_WORD;
    while (--wordCount >= 0)
      bitmap[wordIndex++] = F_INT_ALL_BITS_SET;
  }

  private final static void bitmapClearRange(int[] bitmap,
					     int iStart,
					     int bitCount) {
    
    while ((iStart & F_BIT_INDEX_MASK) != 0) {
      bitmapClearBit(bitmap, iStart++);
      if (--bitCount == 0)
	return;
    }
    
    while ((bitCount & F_BIT_INDEX_MASK) != 0)
      bitmapClearBit(bitmap, iStart + --bitCount);
    
    int wordIndex = iStart >> F_ADDRESS_BITS_PER_WORD;
    int wordCount = bitCount >> F_ADDRESS_BITS_PER_WORD;
    while (--wordCount >= 0)
      bitmap[wordIndex++] = 0;
  }

  private final static void bitmapClear(int[] bitmap) {
    for (int i = bitmap.length; --i >= 0; )
      bitmap[i] = 0;
  }

  private final static int bitmapGetMinimumWordCount(int[] bitmap) {
    int indexLast;
    for (indexLast = bitmap.length;
	 --indexLast >= 0 && bitmap[indexLast] == 0;
	 ) {
      
    }
    return indexLast + 1;
  }

  private final static int bitmapGetSizeInBits(int[] bitmap) {
    return bitmap.length << F_ADDRESS_BITS_PER_WORD;
  }

  private final static int getWordCountFromBitCount(int bitCount) {
    return (bitCount + F_BITS_PER_WORD - 1) >> F_ADDRESS_BITS_PER_WORD;
  }

  private final static int[] bitmapResizeBitCount(int[] oldBitmap, int bitCount) {
    int newWordCount = getWordCountFromBitCount(bitCount);
    int[] newBitmap = new int[newWordCount];
    int oldWordCount = oldBitmap.length;
    int wordsToCopy =
      (newWordCount < oldWordCount) ? newWordCount : oldWordCount;
    System.arraycopy(oldBitmap, 0, newBitmap, 0, wordsToCopy);
    return newBitmap;
  }

  private final static void bitmapAnd(int[] bitmap, int[] bitmapAnd) {
    int wordCount =
      bitmap.length < bitmapAnd.length ? bitmap.length : bitmapAnd.length;
    while (--wordCount >= 0)
      bitmap[wordCount] &= bitmapAnd[wordCount];
  }

  private final static void bitmapAndNot(int[] bitmap, int[] bitmapAndNot) {
    int wordCount = (bitmap.length < bitmapAndNot.length)
      ? bitmap.length : bitmapAndNot.length;
    while (--wordCount >= 0)
      bitmap[wordCount] &= ~bitmapAndNot[wordCount];
  }

  
  
  
  private final static void bitmapOr(int[] bitmap, int[] bitmapOr) {
    int wordCount = bitmapOr.length;
    while (--wordCount >= 0)
      bitmap[wordCount] |= bitmapOr[wordCount];
  }

  private final static void bitmapXor(int[] bitmap, int[] bitmapXor) {
    int wordCount = bitmapXor.length;
    while (--wordCount >= 0)
      bitmap[wordCount] ^= bitmapXor[wordCount];
  }

  private final static int bitmapNextSetBit(int[] bitmap, int fromIndex) {
    int maxIndex = bitmap.length << F_ADDRESS_BITS_PER_WORD;
    if (fromIndex >= maxIndex)
      return -1;
    
    while ((fromIndex & F_BIT_INDEX_MASK) != 0) {
      if (bitmapGetBit(bitmap, fromIndex))
	return fromIndex;
      ++fromIndex;
    }
    
    while (fromIndex < maxIndex) {
      if (bitmap[fromIndex >> F_ADDRESS_BITS_PER_WORD] != 0)
	break;
      fromIndex += F_BITS_PER_WORD;
    }
    while (fromIndex < maxIndex) {
      if (bitmapGetBit(bitmap, fromIndex))
	return fromIndex;
      ++fromIndex;
    }
    return -1;
  }

  
  
  

  private final static int[] bitmapMinimize(int[] bitmap) {
    int minimumWordCount = bitmapGetMinimumWordCount(bitmap);
    if (minimumWordCount == 0)
      return emptyBitmap;
    if (minimumWordCount == bitmap.length)
      return bitmap;
    int[] newBitmap = new int[minimumWordCount];
    System.arraycopy(bitmap, 0, newBitmap, 0, minimumWordCount);
    return newBitmap;
  }
  
  private final static int bitmapGetCardinality(int[] bitmap) {
    int count = 0;
    for (int i = bitmap.length; --i >= 0; ) {
      if (bitmap[i] != 0)
	count += countBitsInWord(bitmap[i]);
    }
    return count;
  }

  private final static int countBitsInWord(int word) {
    word = (word & 0x55555555) + ((word >> 1) & 0x55555555);
    word = (word & 0x33333333) + ((word >> 2) & 0x33333333);
    word = (word & 0x0F0F0F0F) + ((word >> 4) & 0x0F0F0F0F);
    word = (word & 0x00FF00FF) + ((word >> 8) & 0x00FF00FF);
    word = (word & 0x0000FFFF) + ((word >> 16) & 0x0000FFFF);
    return word;
  }

  private final static boolean bitmapIsEqual(int[] bitmap1, int[] bitmap2) {
    if (bitmap1 == bitmap2)
      return true;
    int count1 = bitmapGetMinimumWordCount(bitmap1);
    int count2 = bitmapGetMinimumWordCount(bitmap2);
    if (count1 != count2)
      return false;
    while (--count1 >= 0)
      if (bitmap1[count1] != bitmap2[count1])
	return false;
    return true;
  }

  private final static boolean bitmapIsEmpty(int[] bitmap) {
    int i = bitmap.length;
    while (--i >= 0)
      if (bitmap[i] != 0)
	return false;
    return true;
  }

}
