

package edu.rice.cs.plt.iter;

import java.util.*;
import java.lang.reflect.Array;
import java.io.Reader;
import java.io.InputStream;
import java.io.IOException;
import java.io.Serializable;

import edu.rice.cs.plt.lambda.*;
import edu.rice.cs.plt.tuple.*;
import edu.rice.cs.plt.collect.CollectUtil;
import edu.rice.cs.plt.collect.ConsList;
import edu.rice.cs.plt.text.TextUtil;
import edu.rice.cs.plt.object.ObjectUtil;



public final class IterUtil {
  
  
  private IterUtil() {}
  
  
  public static boolean isEmpty(Iterable<?> iter) { 
    if (iter instanceof Collection<?>) { return ((Collection<?>) iter).isEmpty(); }
    else if (iter instanceof SizedIterable<?>) { return ((SizedIterable<?>) iter).isEmpty(); }
    else { return ! iter.iterator().hasNext(); }
  }
  
  
  public static int sizeOf(Iterable<?> iter) {
    if (iter instanceof SizedIterable<?>) { return ((SizedIterable<?>) iter).size(); }
    else if (iter instanceof Collection<?>) { return ((Collection<?>) iter).size(); }
    else {
      int result = 0;
      
      
      for (Object o : iter) { result++; if (result == Integer.MAX_VALUE) break; }
      return result;
    }
  }
  
  
  public static int sizeOf(Iterable<?> iter, int bound) {
    if (iter instanceof SizedIterable<?>) { return ((SizedIterable<?>) iter).size(bound); }
    else if (iter instanceof Collection<?>) {
      int result = ((Collection<?>) iter).size();
      return result <= bound ? result : bound;
    }
    else {
      int result = 0;
      
      
      for (Object o : iter) { result++; if (result == bound) break; }
      return result;
    }
  }
  
  
  public static boolean isInfinite(Iterable<?> iter) {
    if (iter instanceof SizedIterable<?>) { return ((SizedIterable<?>) iter).isInfinite(); }
    else { return false; }
  }
  
  
  public static boolean hasFixedSize(Iterable<?> iter) {
    if (iter instanceof SizedIterable<?>) { return ((SizedIterable<?>) iter).hasFixedSize(); }
    else if (iter instanceof Collection<?>) { return isFixedSizeCollection((Collection<?>) iter); }
    else { return false; }
  }
  
  
  private static boolean isFixedSizeCollection(Collection<?> iter) {
    return (iter == Collections.EMPTY_SET) || (iter == Collections.EMPTY_LIST);
  }
  
  
  public static boolean isStatic(Iterable<?> iter) {
    if (iter instanceof SizedIterable<?>) { return ((SizedIterable<?>) iter).isStatic(); }
    else if (iter instanceof Collection<?>) { return isStaticCollection((Collection<?>) iter); }
    else { return false; }
  }
  
  
  private static boolean isStaticCollection(Collection<?> iter) {
    return (iter == Collections.EMPTY_SET) || (iter == Collections.EMPTY_LIST);
  }
  
  
  public static boolean contains(Iterable<?> iter, Object o) {
    if (iter instanceof Collection<?>) { return ((Collection<?>) iter).contains(o); }
    else { return iteratedContains(iter, o); }
  }
  
  
  public static boolean containsAll(Iterable<?> iter, Iterable<?> subset) {
    if (iter instanceof Collection<?>) { return CollectUtil.containsAll((Collection<?>) iter, subset); }
    else {
      for (Object o : subset) {
        if (!iteratedContains(iter, o)) { return false; }
      }
      return true;
    }
  }
  
  
  public static boolean containsAny(Iterable<?> iter, Iterable<?> candidates) {
    if (iter instanceof Collection<?>) { return CollectUtil.containsAny((Collection<?>) iter, candidates); }
    else {
      for (Object o : candidates) {
        if (iteratedContains(iter, o)) { return true; }
      }
      return false;
    }
  }
  
  private static boolean iteratedContains(Iterable<?> iter, Object o) {
    if (o == null) {
      for (Object elt : iter) {
        if (elt == null) { return true; }
      }
      return false;
    }
    else {
      for (Object elt : iter) {
        if (o.equals(elt)) { return true; }
      }
      return false;
    }
  }
  
  
  public static String toString(Iterable<?> iter) {
    return toString(iter, "[", ", ", "]");
  }
  
  
  public static String multilineToString(Iterable<?> iter) {
    return toString(iter, "", TextUtil.NEWLINE, "");
  }
  
  
  public static String toString(Iterable<?> iter, String prefix, String delimiter, String suffix) {
    if (isInfinite(iter)) { iter = compose(new TruncatedIterable<Object>(iter, 8), "..."); }
    StringBuilder result = new StringBuilder();
    result.append(prefix);
    boolean first = true;
    for (Object obj : iter) {
      if (first) { first = false; }
      else { result.append(delimiter); }
      result.append(TextUtil.toString(obj));
    }
    result.append(suffix);
    return result.toString();
  }
  
  
  public static boolean isEqual(Iterable<?> iter1, Iterable<?> iter2) {
    if (iter1 == iter2) { return true; }
    else if (sizeOf(iter1) == sizeOf(iter2)) { return and(iter1, iter2, LambdaUtil.EQUAL); }
    else { return false; }
  }
  
  
  public static int hashCode(Iterable<?> iter) {
    return ObjectUtil.hash(iter);
  }
  
  
  public static <T> ReadOnlyIterator<T> asIterator(final Enumeration<? extends T> en) {
    return new ReadOnlyIterator<T>() {
      public boolean hasNext() { return en.hasMoreElements(); }
      public T next() { return en.nextElement(); }
    };
  }
  
  
  public static ReadOnlyIterator<String> asIterator(final StringTokenizer s) {
    return new ReadOnlyIterator<String>() {
      public boolean hasNext() { return s.hasMoreTokens(); }
      public String next() { return s.nextToken(); }
    };
  }
  
  
  public static ReadOnlyIterator<Character> asIterator(final Reader in) {
    return new ReadOnlyIterator<Character>() {
      private int _lookahead = readNext();

      public boolean hasNext() { return _lookahead >= 0; }
      
      public Character next() {
        if (_lookahead < 0) { throw new NoSuchElementException(); }
        Character result = (char) _lookahead;
        _lookahead = readNext();
        return result;
      }
      
      private int readNext() {
        try { return in.read(); }
        catch (IOException e) { throw new IllegalStateException(e); }
      }
      
    };
  }
  
  
  public static ReadOnlyIterator<Byte> asIterator(final InputStream in) {
    return new ReadOnlyIterator<Byte>() {
      private int _lookahead = readNext();
      
      public boolean hasNext() { return _lookahead >= 0; }
      
      public Byte next() {
        if (_lookahead < 0) { throw new NoSuchElementException(); }
        Byte result = (byte) _lookahead;
        _lookahead = readNext();
        return result;
      }
      
      private int readNext() {
        try { return in.read(); }
        catch (IOException e) { throw new IllegalStateException(e); }
      }
      
    };
  }
  
  
  public static <T> Enumeration<T> asEnumeration(final Iterator<? extends T> iter) {
    return new Enumeration<T>() {
      public boolean hasMoreElements() { return iter.hasNext(); }
      public T nextElement() { return iter.next(); }
    };
  }
  
  
  
  @SuppressWarnings("unchecked") public static <T> EmptyIterable<T> empty() {
    return (EmptyIterable<T>) EmptyIterable.INSTANCE;
  }
  
  
  public static <T> SingletonIterable<T> singleton(T value) {
    return new SingletonIterable<T>(value);
  }
  
  
  public static <T> ComposedIterable<T> compose(T first, Iterable<? extends T> rest) {
    return new ComposedIterable<T>(first, rest);
  }
    
  
  @SuppressWarnings("unchecked") public static <T> Lambda2<T, Iterable<? extends T>, Iterable<T>> composeLeftLambda() {
    return (Lambda2<T, Iterable<? extends T>, Iterable<T>>) (Lambda2<?, ?, ?>) ComposeLeftLambda.INSTANCE;
  }
  
  private static class ComposeLeftLambda<T> implements Lambda2<T, Iterable<? extends T>, Iterable<T>>, Serializable {
    private static ComposeLeftLambda<Object> INSTANCE = new ComposeLeftLambda<Object>();
    private ComposeLeftLambda() {}
    public Iterable<T> value(T first, Iterable<? extends T> rest) {
      return new ComposedIterable<T>(first, rest);
    }
  }
  
  
  public static <T> ComposedIterable<T> compose(Iterable<? extends T> rest, T last) {
    return new ComposedIterable<T>(rest, last);
  }
    
  
  @SuppressWarnings("unchecked") public static <T> Lambda2<Iterable<? extends T>, T, Iterable<T>> composeRightLambda() {
    return (Lambda2<Iterable<? extends T>, T, Iterable<T>>) (Lambda2<?, ?, ?>) ComposeRightLambda.INSTANCE;
  }
  
  private static class ComposeRightLambda<T> implements Lambda2<Iterable<? extends T>, T, Iterable<T>>, Serializable {
    private static ComposeRightLambda<Object> INSTANCE = new ComposeRightLambda<Object>();
    private ComposeRightLambda() {}
    public Iterable<T> value(Iterable<? extends T> rest, T last) {
      return new ComposedIterable<T>(rest, last);
    }
  }
  
  
  public static <T> ComposedIterable<T> compose(Iterable<? extends T> i1, Iterable<? extends T> i2) {
    return new ComposedIterable<T>(i1, i2);
  }
  
  
  @SuppressWarnings("unchecked") 
  public static <T> Lambda2<Iterable<? extends T>, Iterable<? extends T>, Iterable<T>> composeLambda() {
    return (Lambda2<Iterable<? extends T>, Iterable<? extends T>, Iterable<T>>) (Lambda2<?, ?, ?>)
           ComposeLambda.INSTANCE;
  }
  
  private static class ComposeLambda<T> 
    implements Lambda2<Iterable<? extends T>, Iterable<? extends T>, Iterable<T>>, Serializable {
    private static ComposeLambda<Object> INSTANCE = new ComposeLambda<Object>();
    private ComposeLambda() {}
    public Iterable<T> value(Iterable<? extends T> i1, Iterable<? extends T> i2) {
      return new ComposedIterable<T>(i1, i2);
    }
  }
  
  
  
  public static <T> SnapshotIterable<T> snapshot(Iterable<? extends T> iter) {
    return new SnapshotIterable<T>(iter);
  }
  
  
  public static <T> SnapshotIterable<T> snapshot(Iterator<? extends T> iter) {
    return new SnapshotIterable<T>(iter);
  }
  
  
  public static <T> Iterable<T> conditionalSnapshot(Iterable<T> iter, int threshold) {
    if (ObjectUtil.compositeSize(iter) > threshold) { return new SnapshotIterable<T>(iter); }
    else { return iter; }
  }
  
  
  public static <T> ImmutableIterable<T> immutable(Iterable<? extends T> iter) {
    return new ImmutableIterable<T>(iter);
  }
  
  
  public static <T> SizedIterable<T> relax(Iterable<? extends T> iter) {
    return new ImmutableIterable<T>(iter);
  }
    
  
  public static <T> Iterator<T> relax(Iterator<? extends T> iter) {
    return new ImmutableIterator<T>(iter);
  }
    
  
  public static <T> SizedIterable<T> make() {
    @SuppressWarnings("unchecked") EmptyIterable<T> result = (EmptyIterable<T>) EmptyIterable.INSTANCE;
    return result;
  }
  
  
  public static <T> SizedIterable<T> make(T v1) {
    return new SingletonIterable<T>(v1);
  }
  
  
  public static <T> SizedIterable<T> make(T v1, T v2) {
    @SuppressWarnings("unchecked") T[] values = (T[]) new Object[]{ v1, v2 };
    return new ObjectArrayWrapper<T>(values, false);
  }
  
  
  public static <T> SizedIterable<T> make(T v1, T v2, T v3) {
    @SuppressWarnings("unchecked") T[] values = (T[]) new Object[]{ v1, v2, v3 };
    return new ObjectArrayWrapper<T>(values, false);
  }
  
  
  public static <T> SizedIterable<T> make(T v1, T v2, T v3, T v4) {
    @SuppressWarnings("unchecked") T[] values = (T[]) new Object[]{ v1, v2, v3, v4 };
    return new ObjectArrayWrapper<T>(values, false);
  }
  
  
  public static <T> SizedIterable<T> make(T v1, T v2, T v3, T v4, T v5) {
    @SuppressWarnings("unchecked") T[] values = (T[]) new Object[]{ v1, v2, v3, v4, v5 };
    return new ObjectArrayWrapper<T>(values, false);
  }
  
  
  public static <T> SizedIterable<T> make(T v1, T v2, T v3, T v4, T v5, T v6) {
    @SuppressWarnings("unchecked") T[] values = (T[]) new Object[]{ v1, v2, v3, v4, v5, v6 };
    return new ObjectArrayWrapper<T>(values, false);
  }
  
  
  public static <T> SizedIterable<T> make(T v1, T v2, T v3, T v4, T v5, T v6, T v7) {
    @SuppressWarnings("unchecked") T[] values = (T[]) new Object[]{ v1, v2, v3, v4, v5, v6 , v7 };
    return new ObjectArrayWrapper<T>(values, false);
  }
  
  
  public static <T> SizedIterable<T> make(T v1, T v2, T v3, T v4, T v5, T v6, T v7, T v8) {
    @SuppressWarnings("unchecked") T[] values = (T[]) new Object[]{ v1, v2, v3, v4, v5, v6 , v7, v8 };
    return new ObjectArrayWrapper<T>(values, false);
  }
  
  
  public static <T> SizedIterable<T> make(T v1, T v2, T v3, T v4, T v5, T v6, T v7, T v8, T v9) {
    @SuppressWarnings("unchecked") T[] values = (T[]) new Object[]{ v1, v2, v3, v4, v5, v6 , v7, v8, v9 };
    return new ObjectArrayWrapper<T>(values, false);
  }
  
  
  public static <T> SizedIterable<T> make(T v1, T v2, T v3, T v4, T v5, T v6, T v7, T v8, T v9, T v10) {
    @SuppressWarnings("unchecked") T[] values = (T[]) new Object[]{ v1, v2, v3, v4, v5, v6 , v7, v8, v9, v10 };
    return new ObjectArrayWrapper<T>(values, false);
  }
  
  
  public static <T> SizedIterable<T> make(T... vals) {
    return snapshot(new ObjectArrayWrapper<T>(vals));
  }

  
  public static <T> SizedIterable<T> make(T[] vals, int start) {
    return snapshot(new ObjectArrayWrapper<T>(vals, start));
  }

  
  public static <T> SizedIterable<T> make(T[] vals, int start, int end) {
    return snapshot(new ObjectArrayWrapper<T>(vals, start, end));
  }

  
  
  public static <T> SequenceIterable<T> infiniteSequence(T initial, Lambda<? super T, ? extends T> successor) {
    return new SequenceIterable<T>(initial, successor);
  }
  
  
  public static <T> FiniteSequenceIterable<T> finiteSequence(T initial, Lambda<? super T, ? extends T> successor, 
                                                             int size) {
    return new FiniteSequenceIterable<T>(initial, successor, size);
  }
  
  
  public static FiniteSequenceIterable<Integer> integerSequence(int start, int end) {
    return FiniteSequenceIterable.makeIntegerSequence(start, end);
  }
  
  
  public static <T> FiniteSequenceIterable<T> copy(T value, int copies) {
    return FiniteSequenceIterable.makeCopies(value, copies);
  }
  
  
  public static <T> SizedIterable<T> asIterable(T... array) {
    return new ObjectArrayWrapper<T>(array);
  }
  
  
  public static <T> SizedIterable<T> arraySegment(T[] array, int start) {
    return new ObjectArrayWrapper<T>(array, start);
  }
  
  
  public static <T> SizedIterable<T> arraySegment(T[] array, int start, int end) {
    return new ObjectArrayWrapper<T>(array, start, end);
  }
  
  private static final class ObjectArrayWrapper<T> extends AbstractIterable<T> 
      implements SizedIterable<T>, OptimizedLastIterable<T>, Serializable {
    private final T[] _array;
    private final int _start; 
    private final int _end; 
    private final boolean _refs; 

    public ObjectArrayWrapper(T[] array) { this(array, 0, array.length, true); }
    
    public ObjectArrayWrapper(T[] array, int start) {
      this(array, start, array.length, true);
      if (_start < 0 || _start > _end) { throw new IndexOutOfBoundsException(); }
    }
    
    public ObjectArrayWrapper(T[] array, int start, int end) {
      this(array, start, end, true);
      if (_start < 0 || _start > _end || _end > _array.length) { throw new IndexOutOfBoundsException(); }
    }
    
    public ObjectArrayWrapper(T[] array, boolean refs) { this(array, 0, array.length, refs); }
    
    public ObjectArrayWrapper(T[] array, int start, int end, boolean refs) {
      _array = array;
      _start = start;
      _end = end;
      _refs = refs;
    }
    
    public boolean isEmpty() { return _start == _end; }
    public int size() { return _end-_start; }
    public int size(int bound) { int result = _end-_start; return result <= bound ? result : bound; }
    public boolean isInfinite() { return false; }
    public boolean hasFixedSize() { return true; }
    public boolean isStatic() { return !_refs; }
    public T last() { return _array[_end-1]; }
    
    public Iterator<T> iterator() {
      return new IndexedIterator<T>() {
        protected int size() { return _end-_start; }
        protected T get(int i) { return _array[_start+i]; }
      };
    }
  }
  
  
  public static SizedIterable<Boolean> asIterable(boolean[] array) {
    return new BooleanArrayWrapper(array);
  }
  
  
  public static SizedIterable<Boolean> arraySegment(boolean[] array, int start) {
    return new BooleanArrayWrapper(array, start);
  }
  
  
  public static SizedIterable<Boolean> arraySegment(boolean[] array, int start, int end) {
    return new BooleanArrayWrapper(array, start, end);
  }
  
  private static final class BooleanArrayWrapper extends AbstractIterable<Boolean> 
      implements SizedIterable<Boolean>, OptimizedLastIterable<Boolean>, Serializable {
    private final boolean[] _array;
    private final int _start; 
    private final int _end; 
    public BooleanArrayWrapper(boolean[] array) { _array = array; _start = 0; _end = _array.length; }
    public BooleanArrayWrapper(boolean[] array, int start) {
      if (start < 0 || start > array.length) { throw new IndexOutOfBoundsException(); }
      _array = array; _start = start; _end = array.length;
    }
    public BooleanArrayWrapper(boolean[] array, int start, int end) {
      if (start < 0 || start > end || end > array.length) { throw new IndexOutOfBoundsException(); }
      _array = array; _start = start; _end = end;
    }
    
    public boolean isEmpty() { return _start == _end; }
    public int size() { return _end-_start; }
    public int size(int bound) { int result = _end-_start; return result <= bound ? result : bound; }
    public boolean isInfinite() { return false; }
    public boolean hasFixedSize() { return true; }
    public boolean isStatic() { return false; }
    public Boolean last() { return _array[_end-1]; }
    public Iterator<Boolean> iterator() {
      return new IndexedIterator<Boolean>() {
        protected int size() { return _end-_start; }
        protected Boolean get(int i) { return _array[_start+i]; }
      };
    }
  }
  
  
  public static SizedIterable<Character> asIterable(char[] array) {
    return new CharArrayWrapper(array);
  }
  
  
  public static SizedIterable<Character> arraySegment(char[] array, int start) {
    return new CharArrayWrapper(array, start);
  }
  
  
  public static SizedIterable<Character> arraySegment(char[] array, int start, int end) {
    return new CharArrayWrapper(array, start, end);
  }
  
  private static final class CharArrayWrapper extends AbstractIterable<Character> 
      implements SizedIterable<Character>, OptimizedLastIterable<Character>, Serializable {
    private final char[] _array;
    private final int _start; 
    private final int _end; 
    public CharArrayWrapper(char[] array) { _array = array; _start = 0; _end = _array.length; }
    public CharArrayWrapper(char[] array, int start) {
      if (start < 0 || start > array.length) { throw new IndexOutOfBoundsException(); }
      _array = array; _start = start; _end = array.length;
    }
    public CharArrayWrapper(char[] array, int start, int end) {
      if (start < 0 || start > end || end > array.length) { throw new IndexOutOfBoundsException(); }
      _array = array; _start = start; _end = end;
    }
    
    public boolean isEmpty() { return _start == _end; }
    public int size() { return _end-_start; }
    public int size(int bound) { int result = _end-_start; return result <= bound ? result : bound; }
    public boolean isInfinite() { return false; }
    public boolean hasFixedSize() { return true; }
    public boolean isStatic() { return false; }
    public Character last() { return _array[_end-1]; }
    public Iterator<Character> iterator() {
      return new IndexedIterator<Character>() {
        protected int size() { return _end-_start; }
        protected Character get(int i) { return _array[_start+i]; }
      };
    }
  }
  
  
  public static SizedIterable<Byte> asIterable(byte[] values) {
    return new ByteArrayWrapper(values);
  }
  
  public static SizedIterable<Byte> arraySegment(byte[] array, int start) {
    return new ByteArrayWrapper(array, start);
  }
  
  
  public static SizedIterable<Byte> arraySegment(byte[] array, int start, int end) {
    return new ByteArrayWrapper(array, start, end);
  }
  
  private static final class ByteArrayWrapper extends AbstractIterable<Byte> 
      implements SizedIterable<Byte>, OptimizedLastIterable<Byte>, Serializable {
    private final byte[] _array;
    private final int _start; 
    private final int _end; 
    public ByteArrayWrapper(byte[] array) { _array = array; _start = 0; _end = _array.length; }
    public ByteArrayWrapper(byte[] array, int start) {
      if (start < 0 || start > array.length) { throw new IndexOutOfBoundsException(); }
      _array = array; _start = start; _end = array.length;
    }
    public ByteArrayWrapper(byte[] array, int start, int end) {
      if (start < 0 || start > end || end > array.length) { throw new IndexOutOfBoundsException(); }
      _array = array; _start = start; _end = end;
    }
    
    public boolean isEmpty() { return _start == _end; }
    public int size() { return _end-_start; }
    public int size(int bound) { int result = _end-_start; return result <= bound ? result : bound; }
    public boolean isInfinite() { return false; }
    public boolean hasFixedSize() { return true; }
    public boolean isStatic() { return false; }
    public Byte last() { return _array[_end-1]; }
    public Iterator<Byte> iterator() {
      return new IndexedIterator<Byte>() {
        protected int size() { return _end-_start; }
        protected Byte get(int i) { return _array[_start+i]; }
      };
    }
  }
  
  
  public static SizedIterable<Short> asIterable(short[] values) {
    return new ShortArrayWrapper(values);
  }
  
  public static SizedIterable<Short> arraySegment(short[] array, int start) {
    return new ShortArrayWrapper(array, start);
  }
  
  
  public static SizedIterable<Short> arraySegment(short[] array, int start, int end) {
    return new ShortArrayWrapper(array, start, end);
  }
  
  private static final class ShortArrayWrapper extends AbstractIterable<Short> 
      implements SizedIterable<Short>, OptimizedLastIterable<Short>, Serializable {
    private final short[] _array;
    private final int _start; 
    private final int _end; 
    public ShortArrayWrapper(short[] array) { _array = array; _start = 0; _end = _array.length; }
    public ShortArrayWrapper(short[] array, int start) {
      if (start < 0 || start > array.length) { throw new IndexOutOfBoundsException(); }
      _array = array; _start = start; _end = array.length;
    }
    public ShortArrayWrapper(short[] array, int start, int end) {
      if (start < 0 || start > end || end > array.length) { throw new IndexOutOfBoundsException(); }
      _array = array; _start = start; _end = end;
    }
    
    public boolean isEmpty() { return _start == _end; }
    public int size() { return _end-_start; }
    public int size(int bound) { int result = _end-_start; return result <= bound ? result : bound; }
    public boolean isInfinite() { return false; }
    public boolean hasFixedSize() { return true; }
    public boolean isStatic() { return false; }
    public Short last() { return _array[_end-1]; }
    public Iterator<Short> iterator() {
      return new IndexedIterator<Short>() {
        protected int size() { return _end-_start; }
        protected Short get(int i) { return _array[_start+i]; }
      };
    }
  }
  
  
  public static SizedIterable<Integer> asIterable(int[] values) {
    return new IntArrayWrapper(values);
  }
  
  
  public static SizedIterable<Integer> arraySegment(int[] array, int start) {
    return new IntArrayWrapper(array, start);
  }
  
  
  public static SizedIterable<Integer> arraySegment(int[] array, int start, int end) {
    return new IntArrayWrapper(array, start, end);
  }
  
  private static final class IntArrayWrapper extends AbstractIterable<Integer> 
      implements SizedIterable<Integer>, OptimizedLastIterable<Integer>, Serializable {
    private final int[] _array;
    private final int _start; 
    private final int _end; 
    public IntArrayWrapper(int[] array) { _array = array; _start = 0; _end = _array.length; }
    public IntArrayWrapper(int[] array, int start) {
      if (start < 0 || start > array.length) { throw new IndexOutOfBoundsException(); }
      _array = array; _start = start; _end = array.length;
    }
    public IntArrayWrapper(int[] array, int start, int end) {
      if (start < 0 || start > end || end > array.length) { throw new IndexOutOfBoundsException(); }
      _array = array; _start = start; _end = end;
    }
    
    public boolean isEmpty() { return _start == _end; }
    public int size() { return _end-_start; }
    public int size(int bound) { int result = _end-_start; return result <= bound ? result : bound; }
    public boolean isInfinite() { return false; }
    public boolean hasFixedSize() { return true; }
    public boolean isStatic() { return false; }
    public Integer last() { return _array[_end-1]; }
    public Iterator<Integer> iterator() {
      return new IndexedIterator<Integer>() {
        protected int size() { return _end-_start; }
        protected Integer get(int i) { return _array[_start+i]; }
      };
    }
  }
  
  
  public static SizedIterable<Long> asIterable(long[] values) {
    return new LongArrayWrapper(values);
  }
  
  
  public static SizedIterable<Long> arraySegment(long[] array, int start) {
    return new LongArrayWrapper(array, start);
  }
  
  
  public static SizedIterable<Long> arraySegment(long[] array, int start, int end) {
    return new LongArrayWrapper(array, start, end);
  }
  
  private static final class LongArrayWrapper extends AbstractIterable<Long> 
      implements SizedIterable<Long>, OptimizedLastIterable<Long>, Serializable {
    private final long[] _array;
    private final int _start; 
    private final int _end; 
    public LongArrayWrapper(long[] array) { _array = array; _start = 0; _end = _array.length; }
    public LongArrayWrapper(long[] array, int start) {
      if (start < 0 || start > array.length) { throw new IndexOutOfBoundsException(); }
      _array = array; _start = start; _end = array.length;
    }
    public LongArrayWrapper(long[] array, int start, int end) {
      if (start < 0 || start > end || end > array.length) { throw new IndexOutOfBoundsException(); }
      _array = array; _start = start; _end = end;
    }
    
    public boolean isEmpty() { return _start == _end; }
    public int size() { return _end-_start; }
    public int size(int bound) { int result = _end-_start; return result <= bound ? result : bound; }
    public boolean isInfinite() { return false; }
    public boolean hasFixedSize() { return true; }
    public boolean isStatic() { return false; }
    public Long last() { return _array[_end-1]; }
    public Iterator<Long> iterator() {
      return new IndexedIterator<Long>() {
        protected int size() { return _end-_start; }
        protected Long get(int i) { return _array[_start+i]; }
      };
    }
  }
  
  
  public static SizedIterable<Float> asIterable(float[] values) {
    return new FloatArrayWrapper(values);
  }
  
  
  public static SizedIterable<Float> arraySegment(float[] array, int start) {
    return new FloatArrayWrapper(array, start);
  }
  
  
  public static SizedIterable<Float> arraySegment(float[] array, int start, int end) {
    return new FloatArrayWrapper(array, start, end);
  }
  
  private static final class FloatArrayWrapper extends AbstractIterable<Float> 
      implements SizedIterable<Float>, OptimizedLastIterable<Float>, Serializable {
    private final float[] _array;
    private final int _start; 
    private final int _end; 
    public FloatArrayWrapper(float[] array) { _array = array; _start = 0; _end = _array.length; }
    public FloatArrayWrapper(float[] array, int start) {
      if (start < 0 || start > array.length) { throw new IndexOutOfBoundsException(); }
      _array = array; _start = start; _end = array.length;
    }
    public FloatArrayWrapper(float[] array, int start, int end) {
      if (start < 0 || start > end || end > array.length) { throw new IndexOutOfBoundsException(); }
      _array = array; _start = start; _end = end;
    }
    
    public boolean isEmpty() { return _start == _end; }
    public int size() { return _end-_start; }
    public int size(int bound) { int result = _end-_start; return result <= bound ? result : bound; }
    public boolean isInfinite() { return false; }
    public boolean hasFixedSize() { return true; }
    public boolean isStatic() { return false; }
    public Float last() { return _array[_end-1]; }
    public Iterator<Float> iterator() {
      return new IndexedIterator<Float>() {
        protected int size() { return _end-_start; }
        protected Float get(int i) { return _array[_start+i]; }
      };
    }
  }
  
  
  public static SizedIterable<Double> asIterable(double[] values) {
    return new DoubleArrayWrapper(values);
  }
  
  
  public static SizedIterable<Double> arraySegment(double[] array, int start) {
    return new DoubleArrayWrapper(array, start);
  }
  
  
  public static SizedIterable<Double> arraySegment(double[] array, int start, int end) {
    return new DoubleArrayWrapper(array, start, end);
  }
  
  private static final class DoubleArrayWrapper extends AbstractIterable<Double> 
      implements SizedIterable<Double>, OptimizedLastIterable<Double>, Serializable {
    private final double[] _array;
    private final int _start; 
    private final int _end; 
    public DoubleArrayWrapper(double[] array) { _array = array; _start = 0; _end = _array.length; }
    public DoubleArrayWrapper(double[] array, int start) {
      if (start < 0 || start > array.length) { throw new IndexOutOfBoundsException(); }
      _array = array; _start = start; _end = array.length;
    }
    public DoubleArrayWrapper(double[] array, int start, int end) {
      if (start < 0 || start > end || end > array.length) { throw new IndexOutOfBoundsException(); }
      _array = array; _start = start; _end = end;
    }
    
    public boolean isEmpty() { return _start == _end; }
    public int size() { return _end-_start; }
    public int size(int bound) { int result = _end-_start; return result <= bound ? result : bound; }
    public boolean isInfinite() { return false; }
    public boolean hasFixedSize() { return true; }
    public boolean isStatic() { return false; }
    public Double last() { return _array[_end-1]; }
    public Iterator<Double> iterator() {
      return new IndexedIterator<Double>() {
        protected int size() { return _end-_start; }
        protected Double get(int i) { return _array[_start+i]; }
      };
    }
  }
  
  
  public static SizedIterable<?> arrayAsIterable(Object array) {
    if (array instanceof Object[]) { return new ObjectArrayWrapper<Object>((Object[]) array); }
    else if (array instanceof int[]) { return new IntArrayWrapper((int[]) array); }
    else if (array instanceof char[]) { return new CharArrayWrapper((char[]) array); }
    else if (array instanceof byte[]) { return new ByteArrayWrapper((byte[]) array); }
    else if (array instanceof double[]) { return new DoubleArrayWrapper((double[]) array); }
    else if (array instanceof boolean[]) { return new BooleanArrayWrapper((boolean[]) array); }
    else if (array instanceof short[]) { return new ShortArrayWrapper((short[]) array); }
    else if (array instanceof long[]) { return new LongArrayWrapper((long[]) array); }
    else if (array instanceof float[]) { return new FloatArrayWrapper((float[]) array); }
    else { throw new IllegalArgumentException("Non-array argument"); }
  }
  
  
  
  public static <T> SizedIterable<T> asSizedIterable(Collection<T> coll) {
    if (coll instanceof SizedIterable<?>) { return (SizedIterable<T>) coll; }
    else { return new CollectionWrapper<T>(coll); }
  }
  
  private static final class CollectionWrapper<T> extends AbstractIterable<T> 
                                                  implements SizedIterable<T>, OptimizedLastIterable<T>,
                                                             Serializable {
    private final Collection<T> _c;
    public CollectionWrapper(Collection<T> c) { _c = c; }
    public Iterator<T> iterator() { return _c.iterator(); }
    public boolean isEmpty() { return _c.isEmpty(); }
    public int size() { return _c.size(); }
    public int size(int bound) { int result = _c.size(); return result <= bound ? result : bound; }
    public boolean isInfinite() { return false; }
    public boolean hasFixedSize() { return isFixedSizeCollection(_c); }
    public boolean isStatic() { return isStaticCollection(_c); }
    public T last() { return IterUtil.last(_c); }
  }
  

  
  public static SizedIterable<Character> asIterable(CharSequence sequence) {
    return new CharSequenceWrapper(sequence, true);
  }
  
  
  public static SizedIterable<Character> asIterable(final String sequence) {
    return new CharSequenceWrapper(sequence, false);
  }
  
  private static final class CharSequenceWrapper extends AbstractIterable<Character> 
      implements SizedIterable<Character>, OptimizedLastIterable<Character>, Serializable {
    private final CharSequence _s;
    private final boolean _mutable; 
    public CharSequenceWrapper(CharSequence s, boolean mutable) { _s = s; _mutable = mutable; }
    public boolean isEmpty() { return _s.length() == 0; }
    public int size() { return _s.length(); }
    public int size(int bound) { int result = _s.length(); return result <= bound ? result : bound; }
    public boolean isInfinite() { return false; }
    public boolean hasFixedSize() { return !_mutable; }
    public boolean isStatic() { return !_mutable; }
    public Character last() { return _s.charAt(_s.length()-1); }
    public Iterator<Character> iterator() {
      return new IndexedIterator<Character>() {
        protected int size() { return _s.length(); }
        protected Character get(int i) { return _s.charAt(i); }
      };
    }
  }
  
  
  public static <T> SizedIterable<T> toIterable(Option<? extends T> option) {
    return option.apply(new OptionVisitor<T, SizedIterable<T>>() {
      public SizedIterable<T> forSome(T val) { return new SingletonIterable<T>(val); }
      @SuppressWarnings("unchecked")
      public SizedIterable<T> forNone() { return (EmptyIterable<T>) EmptyIterable.INSTANCE; }
    });
  }

  
  public static <T> SizedIterable<T> toIterable(Wrapper<? extends T> tuple) {
    return new SingletonIterable<T>(tuple.value());
  }
  
  
  public static <T> SizedIterable<T> toIterable(Pair<? extends T, ? extends T> tuple) {
    @SuppressWarnings("unchecked")
    T[] values = (T[]) new Object[]{ tuple.first(), tuple.second() };
    return new ObjectArrayWrapper<T>(values, false);
  }
  
  
  public static <T> SizedIterable<T> toIterable(Triple<? extends T, ? extends T, ? extends T> tuple) {
    @SuppressWarnings("unchecked")
    T[] values = (T[]) new Object[]{ tuple.first(), tuple.second(), tuple.third() };
    return new ObjectArrayWrapper<T>(values, false);
  }
  
  
  public static <T> SizedIterable<T> toIterable(Quad<? extends T, ? extends T, ? extends T, ? extends T> tuple) {
    @SuppressWarnings("unchecked")
    T[] values = (T[]) new Object[]{ tuple.first(), tuple.second(), tuple.third(), tuple.fourth() };
    return new ObjectArrayWrapper<T>(values, false);
  }

  
  public static <T> SizedIterable<T>
    toIterable(Quint<? extends T, ? extends T, ? extends T, ? extends T, ? extends T> tuple) {
    @SuppressWarnings("unchecked")
    T[] values = (T[]) new Object[]{ tuple.first(), tuple.second(), tuple.third(), tuple.fourth(), 
                                     tuple.fifth() };
    return new ObjectArrayWrapper<T>(values, false);
  }
  
  
  public static <T> SizedIterable<T>
  toIterable(Sextet<? extends T, ? extends T, ? extends T, ? extends T, ? extends T, ? extends T> tuple) {
    @SuppressWarnings("unchecked")
    T[] values = (T[]) new Object[]{ tuple.first(), tuple.second(), tuple.third(), tuple.fourth(),
                                     tuple.fifth(), tuple.sixth() };
    return new ObjectArrayWrapper<T>(values, false);
  }
  
  
  public static <T> SizedIterable<T>
  toIterable(Septet<? extends T, ? extends T, ? extends T, ? extends T, ? extends T, ? extends T, ? extends T> tuple) {
    @SuppressWarnings("unchecked")
    T[] values = (T[]) new Object[]{ tuple.first(), tuple.second(), tuple.third(), tuple.fourth(),
                                     tuple.fifth(), tuple.sixth(), tuple.seventh() };
    return new ObjectArrayWrapper<T>(values, false);
  }
  
  
  public static <T> SizedIterable<T> toIterable(Octet<? extends T, ? extends T, ? extends T, ? extends T,
                                                      ? extends T, ? extends T, ? extends T, ? extends T> tuple) {
    @SuppressWarnings("unchecked")
    T[] values = (T[]) new Object[]{ tuple.first(), tuple.second(), tuple.third(), tuple.fourth(),
                                     tuple.fifth(), tuple.sixth(), tuple.seventh(), tuple.eighth() };
    return new ObjectArrayWrapper<T>(values, false);
  }
  
  
  public static <T> T[] toArray(Iterable<? extends T> iter, Class<T> type) {
    
    @SuppressWarnings("unchecked") T[] result = (T[]) Array.newInstance(type, sizeOf(iter));
    if (iter instanceof Collection<?>) {
      
      @SuppressWarnings("unchecked") T[] newResult = ((Collection<? extends T>) iter).toArray(result);
      result = newResult; 
    }
    else {
      int i = 0;
      for (T t : iter) { result[i++] = t; if (i < 0) break; }
    }
    return result;
  }
  
  
  public static <T> T first(Iterable<? extends T> iter) {
    return iter.iterator().next();
  }
  
  
  public static <T> SkipFirstIterable<T> skipFirst(Iterable<T> iter) {
    return new SkipFirstIterable<T>(iter);
  }
  
  
  public static <T> T last(Iterable<? extends T> iter) {
    if (iter instanceof OptimizedLastIterable<?>) {
      
      @SuppressWarnings("unchecked") OptimizedLastIterable<? extends T> o = (OptimizedLastIterable<? extends T>) iter;
      return o.last();
    }
    else if (iter instanceof List<?>) {
      
      @SuppressWarnings("unchecked") List<? extends T> l = (List<? extends T>) iter;
      int size = l.size();
      if (size == 0) { throw new NoSuchElementException(); }
      return l.get(size - 1);
    }
    else if (iter instanceof SortedSet<?>) {
      
      @SuppressWarnings("unchecked") SortedSet<? extends T> s = (SortedSet<? extends T>) iter;
      return s.last();
    }
    else {
      Iterator<? extends T> i = iter.iterator();
      T result = i.next();
      while (i.hasNext()) { result = i.next(); }
      return result;
    }
  }
  
  
  public static <T> SkipLastIterable<T> skipLast(Iterable<? extends T> iter) {
    return new SkipLastIterable<T>(iter);
  }
  
  
  public static <T> Option<T> makeOption(Iterable<? extends T> iter) {
    int size = sizeOf(iter);
    if (size == 0) { return Option.none(); }
    else if (size == 1) { return Option.some(first(iter)); }
    else {
      throw new IllegalArgumentException("Iterable has more than 1 element: size == " + size);
    }
  }
  
  
  public static <T> Wrapper<T> makeWrapper(Iterable<? extends T> iter) {
    int size = sizeOf(iter);
    if (size != 1) {
      throw new IllegalArgumentException("Iterable does not have 1 element: size == " + size);
    }
    Iterator<? extends T> i = iter.iterator();
    return new Wrapper<T>(i.next());
  }
  
  
  public static <T> Pair<T, T> makePair(Iterable<? extends T> iter) {
    int size = sizeOf(iter);
    if (size != 2) {
      throw new IllegalArgumentException("Iterable does not have 2 elements: size == " + size);
    }
    Iterator<? extends T> i = iter.iterator();
    return new Pair<T, T>(i.next(), i.next());
  }
  
  
  public static <T> Triple<T, T, T> makeTriple(Iterable<? extends T> iter) {
    int size = sizeOf(iter);
    if (size != 3) {
      throw new IllegalArgumentException("Iterable does not have 3 elements: size == " + size);
    }
    Iterator<? extends T> i = iter.iterator();
    return new Triple<T, T, T>(i.next(), i.next(), i.next());
  }
  
  
  public static <T> Quad<T, T, T, T> makeQuad(Iterable<? extends T> iter) {
    int size = sizeOf(iter);
    if (size != 4) {
      throw new IllegalArgumentException("Iterable does not have 4 elements: size == " + size);
    }
    Iterator<? extends T> i = iter.iterator();
    return new Quad<T, T, T, T>(i.next(), i.next(), i.next(), i.next());
  }
  
  
  public static <T> Quint<T, T, T, T, T> makeQuint(Iterable<? extends T> iter) {
    int size = sizeOf(iter);
    if (size != 5) {
      throw new IllegalArgumentException("Iterable does not have 5 elements: size == " + size);
    }
    Iterator<? extends T> i = iter.iterator();
    return new Quint<T, T, T, T, T>(i.next(), i.next(), i.next(), i.next(), i.next());
  }
  
  
  public static <T> Sextet<T, T, T, T, T, T> makeSextet(Iterable<? extends T> iter) {
    int size = sizeOf(iter);
    if (size != 6) {
      throw new IllegalArgumentException("Iterable does not have 6 elements: size == " + size);
    }
    Iterator<? extends T> i = iter.iterator();
    return new Sextet<T, T, T, T, T, T>(i.next(), i.next(), i.next(), i.next(), i.next(), i.next());
  }
  
  
  public static <T> Septet<T, T, T, T, T, T, T> makeSeptet(Iterable<? extends T> iter) {
    int size = sizeOf(iter);
    if (size != 7) {
      throw new IllegalArgumentException("Iterable does not have 7 elements: size == " + size);
    }
    Iterator<? extends T> i = iter.iterator();
    return new Septet<T, T, T, T, T, T, T>(i.next(), i.next(), i.next(), i.next(), i.next(), i.next(), i.next());
  }
  
  
  public static <T> Octet<T, T, T, T, T, T, T, T> makeOctet(Iterable<? extends T> iter) {
    int size = sizeOf(iter);
    if (size != 8) {
      throw new IllegalArgumentException("Iterable does not have 8 elements: size == " + size);
    }
    Iterator<? extends T> i = iter.iterator();
    return new Octet<T, T, T, T, T, T, T, T>(i.next(), i.next(), i.next(), i.next(), i.next(), i.next(), i.next(),
                                             i.next());
  }
  
  
  
  public static <T> SizedIterable<T> reverse(Iterable<? extends T> iter) {
    ConsList<T> result = ConsList.empty();
    for (T elt : iter) { result = ConsList.cons(elt, result); }
    return result;
  }
  
  
  public static <T> SizedIterable<T> shuffle(Iterable<T> iter) {
    ArrayList<T> result = CollectUtil.makeArrayList(iter);
    Collections.shuffle(result);
    return asSizedIterable(result);
  }
  
  
  public static <T> SizedIterable<T> shuffle(Iterable<T> iter, Random random) {
    ArrayList<T> result = CollectUtil.makeArrayList(iter);
    Collections.shuffle(result, random);
    return asSizedIterable(result);
  }
  
  
  public static <T extends Comparable<? super T>> SizedIterable<T> sort(Iterable<T> iter) {
    ArrayList<T> result = CollectUtil.makeArrayList(iter);
    Collections.sort(result);
    return asSizedIterable(result);
  }
  
  
  public static <T> SizedIterable<T> sort(Iterable<T> iter, Comparator<? super T> comp) {
    ArrayList<T> result = CollectUtil.makeArrayList(iter);
    Collections.sort(result, comp);
    return asSizedIterable(result);
  }
  
  
  
  public static <T> Pair<SizedIterable<T>, SizedIterable<T>> split(Iterable<? extends T> iter, int index) {
    Iterator<? extends T> iterator = iter.iterator();
    @SuppressWarnings("unchecked") SizedIterable<T> left = (EmptyIterable<T>) EmptyIterable.INSTANCE;
    for (int i = 0; i < index && iterator.hasNext(); i++) {
      left = new ComposedIterable<T>(left, iterator.next());
    }
    return new Pair<SizedIterable<T>, SizedIterable<T>>(left, new SnapshotIterable<T>(iterator));
  }
  
  
  public static <T> TruncatedIterable<T> truncate(Iterable<? extends T> iter, int size) {
    return new TruncatedIterable<T>(iter, size);
  }
  
  
  public static <T> CollapsedIterable<T> collapse(Iterable<? extends Iterable<? extends T>> iters) {
    return new CollapsedIterable<T>(iters);
  }
  
  
  public static <T> FilteredIterable<T> filter(Iterable<? extends T> iter, Predicate<? super T> pred) {
    return new FilteredIterable<T>(iter, pred);
  }
  
  
  public static <T> SnapshotIterable<T> filterSnapshot(Iterable<? extends T> iter, Predicate<? super T> pred) {
    return new SnapshotIterable<T>(new FilteredIterable<T>(iter, pred));
  }
  
  
  public static <T> FilteredIterable<T> filterInstances(Iterable<? super T> iter, final Class<? extends T> c) {
    Iterable<T> cast = IterUtil.map(iter, new Lambda<Object, T>() {
      public T value(Object obj) {
        if (c.isInstance(obj)) { return c.cast(obj); }
        else { return null; }
      }
    });
    return new FilteredIterable<T>(cast, LambdaUtil.NOT_NULL);
  }
  
  
  public static <T, R> R fold(Iterable<? extends T> iter, R base,
                              Lambda2<? super R, ? super T, ? extends R> combiner) {
    R result = base;
    for (T elt : iter) { result = combiner.value(result, elt); }
    return result;
  }
  
  
  public static <T> boolean and(Iterable<? extends T> iter, Predicate<? super T> pred) {
    for (T elt : iter) { if (!pred.contains(elt)) { return false; } }
    return true;
  }
  
  
  public static <T> boolean or(Iterable<? extends T> iter, Predicate<? super T> pred) {
    for (T elt : iter) { if (pred.contains(elt)) { return true; } }
    return false;
  }
  
  
  public static <T1, T2> boolean and(Iterable<? extends T1> iter1, Iterable<? extends T2> iter2,
                                     Predicate2<? super T1, ? super T2> pred) {
    Iterator<? extends T1> i1 = iter1.iterator();
    Iterator<? extends T2> i2 = iter2.iterator();
    while (i1.hasNext()) { if (!pred.contains(i1.next(), i2.next())) { return false; } }
    return true;
  }
  
  
  public static <T1, T2> boolean or(Iterable<? extends T1> iter1, Iterable<? extends T2> iter2,
                                    Predicate2<? super T1, ? super T2> pred) {
    Iterator<? extends T1> i1 = iter1.iterator();
    Iterator<? extends T2> i2 = iter2.iterator();
    while (i1.hasNext()) { if (pred.contains(i1.next(), i2.next())) { return true; } }
    return false;
  }
  
  
  public static <T1, T2, T3> boolean and(Iterable<? extends T1> iter1, 
                                         Iterable<? extends T2> iter2,
                                         Iterable<? extends T3> iter3,
                                         Predicate3<? super T1, ? super T2, ? super T3> pred) {
    Iterator<? extends T1> i1 = iter1.iterator();
    Iterator<? extends T2> i2 = iter2.iterator();
    Iterator<? extends T3> i3 = iter3.iterator();
    while (i1.hasNext()) { if (!pred.contains(i1.next(), i2.next(), i3.next())) { return false; } }
    return true;
  }
  
  
  public static <T1, T2, T3> boolean or(Iterable<? extends T1> iter1, 
                                        Iterable<? extends T2> iter2,
                                        Iterable<? extends T3> iter3,
                                        Predicate3<? super T1, ? super T2, ? super T3> pred) {
    Iterator<? extends T1> i1 = iter1.iterator();
    Iterator<? extends T2> i2 = iter2.iterator();
    Iterator<? extends T3> i3 = iter3.iterator();
    while (i1.hasNext()) { if (pred.contains(i1.next(), i2.next(), i3.next())) { return true; } }
    return false;
  }
  
  
  public static <T1, T2, T3, T4> boolean and(Iterable<? extends T1> iter1, 
                                             Iterable<? extends T2> iter2,
                                             Iterable<? extends T3> iter3,
                                             Iterable<? extends T4> iter4,
                                             Predicate4<? super T1, ? super T2, ? super T3, ? super T4> pred) {
    Iterator<? extends T1> i1 = iter1.iterator();
    Iterator<? extends T2> i2 = iter2.iterator();
    Iterator<? extends T3> i3 = iter3.iterator();
    Iterator<? extends T4> i4 = iter4.iterator();
    while (i1.hasNext()) { 
      if (!pred.contains(i1.next(), i2.next(), i3.next(), i4.next())) { return false; }
    }
    return true;
  }
  
  
  public static <T1, T2, T3, T4> boolean or(Iterable<? extends T1> iter1, 
                                            Iterable<? extends T2> iter2,
                                            Iterable<? extends T3> iter3,
                                            Iterable<? extends T4> iter4,
                                            Predicate4<? super T1, ? super T2, ? super T3, ? super T4> pred) {
    Iterator<? extends T1> i1 = iter1.iterator();
    Iterator<? extends T2> i2 = iter2.iterator();
    Iterator<? extends T3> i3 = iter3.iterator();
    Iterator<? extends T4> i4 = iter4.iterator();
    while (i1.hasNext()) { 
      if (pred.contains(i1.next(), i2.next(), i3.next(), i4.next())) { return true; }
    }
    return false;
  }
  
  
  
  public static <T, R> SizedIterable<R> map(Iterable<? extends T> source, Lambda<? super T, ? extends R> map) {
    return new MappedIterable<T, R>(source, map);
  }
  
  
  public static <T, R> SnapshotIterable<R> mapSnapshot(Iterable<? extends T> source,
                                                       Lambda<? super T, ? extends R> map) {
    return new SnapshotIterable<R>(new MappedIterable<T, R>(source, map));
  }
  
  
  public static <T1, T2, R> SizedIterable<R> map(Iterable<? extends T1> iter1, Iterable<? extends T2> iter2, 
                                                 Lambda2<? super T1, ? super T2, ? extends R> map) {
    return new BinaryMappedIterable<T1, T2, R>(iter1, iter2, map);
  }
  
  
  public static <T1, T2, R> SnapshotIterable<R> mapSnapshot(Iterable<? extends T1> iter1, Iterable<? extends T2> iter2,
                                                            Lambda2<? super T1, ? super T2, ? extends R> map) {
    return new SnapshotIterable<R>(new BinaryMappedIterable<T1, T2, R>(iter1, iter2, map));
  }
  
  
  public static <T1, T2, T3, R> SizedIterable<R> map(Iterable<? extends T1> iter1, Iterable<? extends T2> iter2, 
                                                     Iterable<? extends T3> iter3,
                                                     Lambda3<? super T1, ? super T2, ? super T3, ? extends R> map) {
    Iterable<Lambda<T1, Lambda<T2, Lambda<T3, R>>>> r0 = singleton(LambdaUtil.<T1, T2, T3, R>curry(map));
    Iterable<Lambda<T2, Lambda<T3, R>>> r1 =
      cross(r0, iter1, LambdaUtil.<T1, Lambda<T2, Lambda<T3, R>>>applicationLambda());
    Iterable<Lambda<T3, R>> r2 =
      BinaryMappedIterable.make(r1, iter2, LambdaUtil.<T2, Lambda<T3, R>>applicationLambda());
    return BinaryMappedIterable.make(r2, iter3, LambdaUtil.<T3, R>applicationLambda());
  }
  
  
  public static <T1, T2, T3, R> SnapshotIterable<R>
    mapSnapshot(Iterable<? extends T1> iter1, Iterable<? extends T2> iter2, Iterable<? extends T3> iter3,
                Lambda3<? super T1, ? super T2, ? super T3, ? extends R> map) {
    return new SnapshotIterable<R>(map(iter1, iter2, iter3, map));
  }
  
  
  public static <T1, T2, T3, T4, R> SizedIterable<R>
    map(Iterable<? extends T1> iter1, Iterable<? extends T2> iter2, Iterable<? extends T3> iter3, 
        Iterable<? extends T4> iter4, Lambda4<? super T1, ? super T2, ? super T3, ? super T4, ? extends R> map) {
    Iterable<Lambda<T1, Lambda<T2, Lambda<T3, Lambda<T4, R>>>>> r0 = 
      singleton(LambdaUtil.<T1, T2, T3, T4, R>curry(map));
    Iterable<Lambda<T2, Lambda<T3, Lambda<T4, R>>>> r1 =
      cross(r0, iter1, LambdaUtil.<T1, Lambda<T2, Lambda<T3, Lambda<T4, R>>>>applicationLambda());
    Iterable<Lambda<T3, Lambda<T4, R>>> r2 =
      BinaryMappedIterable.make(r1, iter2, LambdaUtil.<T2, Lambda<T3, Lambda<T4, R>>>applicationLambda());
    Iterable<Lambda<T4, R>> r3 =
      BinaryMappedIterable.make(r2, iter3, LambdaUtil.<T3, Lambda<T4, R>>applicationLambda());
    return BinaryMappedIterable.make(r3, iter4, LambdaUtil.<T4, R>applicationLambda());
  }
  
  
  public static <T1, T2, T3, T4, R> SnapshotIterable<R>
    mapSnapshot(Iterable<? extends T1> iter1, Iterable<? extends T2> iter2, Iterable<? extends T3> iter3,
                Iterable<? extends T4> iter4,
                Lambda4<? super T1, ? super T2, ? super T3, ? super T4, ? extends R> map) {
    return new SnapshotIterable<R>(map(iter1, iter2, iter3, iter4, map));
  }
  
  
  public static <T> void run(Iterable<? extends T> iter, Runnable1<? super T> runnable) {
    for (T elt : iter) { runnable.run(elt); }
  }
  
  
  public static <T1, T2> void run(Iterable<? extends T1> iter1, Iterable<? extends T2> iter2,
                                  Runnable2<? super T1, ? super T2> runnable) {
    Iterator<? extends T1> i1 = iter1.iterator();
    Iterator<? extends T2> i2 = iter2.iterator();
    while (i1.hasNext()) { runnable.run(i1.next(), i2.next()); }
  }
  
  
  public static <T1, T2, T3> void run(Iterable<? extends T1> iter1, Iterable<? extends T2> iter2,
                                      Iterable<? extends T3> iter3, 
                                      Runnable3<? super T1, ? super T2, ? super T3> runnable) {
    Iterator<? extends T1> i1 = iter1.iterator();
    Iterator<? extends T2> i2 = iter2.iterator();
    Iterator<? extends T3> i3 = iter3.iterator();
    while (i1.hasNext()) { runnable.run(i1.next(), i2.next(), i3.next()); }
  }
  
  
  public static <T1, T2, T3, T4> void run(Iterable<? extends T1> iter1, Iterable<? extends T2> iter2,
                                          Iterable<? extends T3> iter3, Iterable<? extends T4> iter4,
                                          Runnable4<? super T1, ? super T2, ? super T3, ? super T4> runnable) {
    Iterator<? extends T1> i1 = iter1.iterator();
    Iterator<? extends T2> i2 = iter2.iterator();
    Iterator<? extends T3> i3 = iter3.iterator();
    Iterator<? extends T4> i4 = iter4.iterator();
    while (i1.hasNext()) { runnable.run(i1.next(), i2.next(), i3.next(), i4.next()); }
  }
  
  
  
  public static <T1, T2, R> SizedIterable<R> cross(Iterable<? extends T1> left, Iterable<? extends T2> right,
                                                   Lambda2<? super T1, ? super T2, ? extends R> combiner) {
    return new CartesianIterable<T1, T2, R>(left, right, combiner);
  }
  
  
  public static <T1, T2> SizedIterable<Pair<T1, T2>> cross(Iterable<? extends T1> left, Iterable<? extends T2> right) {
    return cross(left, right, Pair.<T1, T2>factory());
  }
  
  
  public static <T1, T2, R> SizedIterable<R> diagonalCross(Iterable<? extends T1> left, Iterable<? extends T2> right,
                                                           Lambda2<? super T1, ? super T2, ? extends R> combiner) {
    return new DiagonalCartesianIterable<T1, T2, R>(left, right, combiner);
  }
  
  
  public static <T1, T2> SizedIterable<Pair<T1, T2>> diagonalCross(Iterable<? extends T1> left, 
                                                                   Iterable<? extends T2> right) {
    return diagonalCross(left, right, Pair.<T1, T2>factory());
  }
  
  
  public static <T1, T2, T3, R>
    SizedIterable<R> cross(Iterable<? extends T1> iter1, Iterable<? extends T2> iter2, Iterable<? extends T3> iter3, 
                           Lambda3<? super T1, ? super T2, ? super T3, ? extends R> combiner) {
    Iterable<Lambda<T1, Lambda<T2, Lambda<T3, R>>>> r0 = singleton(LambdaUtil.<T1, T2, T3, R>curry(combiner));
    Iterable<Lambda<T2, Lambda<T3, R>>> r1 =
      CartesianIterable.make(r0, iter1, LambdaUtil.<T1, Lambda<T2, Lambda<T3, R>>>applicationLambda());
    Iterable<Lambda<T3, R>> r2 =
      CartesianIterable.make(r1, iter2,LambdaUtil.<T2, Lambda<T3, R>>applicationLambda());
    return CartesianIterable.make(r2, iter3, LambdaUtil.<T3, R>applicationLambda());
  }
  
  
  public static <T1, T2, T3> SizedIterable<Triple<T1, T2, T3>>
    cross(Iterable<? extends T1> iter1, Iterable<? extends T2> iter2, Iterable<? extends T3> iter3) {
    return cross(iter1, iter2, iter3, Triple.<T1, T2, T3>factory());
  }

  
  public static <T1, T2, T3, R> SizedIterable<R>
    diagonalCross(Iterable<? extends T1> iter1, Iterable<? extends T2> iter2, Iterable<? extends T3> iter3, 
                  Lambda3<? super T1, ? super T2, ? super T3, ? extends R> combiner) {
    Iterable<Lambda<T1, Lambda<T2, Lambda<T3, R>>>> r0 = singleton(LambdaUtil.<T1, T2, T3, R>curry(combiner));
    Iterable<Lambda<T2, Lambda<T3, R>>> r1 =
      DiagonalCartesianIterable.make(r0, iter1, LambdaUtil.<T1, Lambda<T2, Lambda<T3, R>>>applicationLambda());
    Iterable<Lambda<T3, R>> r2 =
      DiagonalCartesianIterable.make(r1, iter2,LambdaUtil.<T2, Lambda<T3, R>>applicationLambda());
    return DiagonalCartesianIterable.make(r2, iter3, LambdaUtil.<T3, R>applicationLambda());
  }
  
  
  public static <T1, T2, T3> SizedIterable<Triple<T1, T2, T3>>
    diagonalCross(Iterable<? extends T1> iter1, Iterable<? extends T2> iter2, Iterable<? extends T3> iter3) {
    return diagonalCross(iter1, iter2, iter3, Triple.<T1, T2, T3>factory());
  }
  
  
  public static <T1, T2, T3, T4, R>
    SizedIterable<R> cross(Iterable<? extends T1> iter1, Iterable<? extends T2> iter2,
                           Iterable<? extends T3> iter3, Iterable<? extends T4> iter4,
                           Lambda4<? super T1, ? super T2, ? super T3, ? super T4, ? extends R> combiner) {
    Iterable<Lambda<T1, Lambda<T2, Lambda<T3, Lambda<T4, R>>>>> r0 =
      singleton(LambdaUtil.<T1, T2, T3, T4, R>curry(combiner));
    Iterable<Lambda<T2, Lambda<T3, Lambda<T4, R>>>> r1 =
      CartesianIterable.make(r0, iter1, LambdaUtil.<T1, Lambda<T2, Lambda<T3, Lambda<T4, R>>>>applicationLambda());
    Iterable<Lambda<T3, Lambda<T4, R>>> r2 =
      CartesianIterable.make(r1, iter2, LambdaUtil.<T2, Lambda<T3, Lambda<T4, R>>>applicationLambda());
    Iterable<Lambda<T4, R>> r3 =
      CartesianIterable.make(r2, iter3,LambdaUtil.<T3, Lambda<T4, R>>applicationLambda());
    return CartesianIterable.make(r3, iter4, LambdaUtil.<T4, R>applicationLambda());
  }
  
  
  public static <T1, T2, T3, T4>
    SizedIterable<Quad<T1, T2, T3, T4>> cross(Iterable<? extends T1> iter1, Iterable<? extends T2> iter2,
                                         Iterable<? extends T3> iter3, Iterable<? extends T4> iter4) {
    return cross(iter1, iter2, iter3, iter4, Quad.<T1, T2, T3, T4>factory());
  }
  
  
  public static <T1, T2, T3, T4, R>
    SizedIterable<R> diagonalCross(Iterable<? extends T1> iter1, Iterable<? extends T2> iter2,
                                   Iterable<? extends T3> iter3, Iterable<? extends T4> iter4,
                                   Lambda4<? super T1, ? super T2, ? super T3, ? super T4, ? extends R> combiner) {
    Iterable<Lambda<T1, Lambda<T2, Lambda<T3, Lambda<T4, R>>>>> r0 =
      singleton(LambdaUtil.<T1, T2, T3, T4, R>curry(combiner));
    Iterable<Lambda<T2, Lambda<T3, Lambda<T4, R>>>> r1 =
      DiagonalCartesianIterable.make(r0, iter1, 
                                     LambdaUtil.<T1, Lambda<T2, Lambda<T3, Lambda<T4, R>>>>applicationLambda());
    Iterable<Lambda<T3, Lambda<T4, R>>> r2 =
      DiagonalCartesianIterable.make(r1, iter2, LambdaUtil.<T2, Lambda<T3, Lambda<T4, R>>>applicationLambda());
    Iterable<Lambda<T4, R>> r3 =
      DiagonalCartesianIterable.make(r2, iter3,LambdaUtil.<T3, Lambda<T4, R>>applicationLambda());
    return DiagonalCartesianIterable.make(r3, iter4, LambdaUtil.<T4, R>applicationLambda());
  }
  
  
  public static <T1, T2, T3, T4>
    SizedIterable<Quad<T1, T2, T3, T4>> diagonalCross(Iterable<? extends T1> iter1, Iterable<? extends T2> iter2,
                                                      Iterable<? extends T3> iter3, Iterable<? extends T4> iter4) {
    return diagonalCross(iter1, iter2, iter3, iter4, Quad.<T1, T2, T3, T4>factory());
  }
  
  
  public static <T> SizedIterable<Iterable<T>> cross(Iterable<? extends Iterable<? extends T>> iters) {
    return crossFold(iters, IterUtil.<T>empty(), IterUtil.<T>composeRightLambda());
  }  
  
  
  public static <T> SizedIterable<Iterable<T>> diagonalCross(Iterable<? extends Iterable<? extends T>> iters) {
    return diagonalCrossFold(iters, IterUtil.<T>empty(), IterUtil.<T>composeRightLambda());
  }  
  
  
  public static <T, R> SizedIterable<R> crossFold(Iterable<? extends Iterable<? extends T>> iters, R base,
                                                  Lambda2<? super R, ? super T, ? extends R> combiner) {
    SizedIterable<R> result = singleton(base);
    for (Iterable<? extends T> iter : iters) {
      result = new CartesianIterable<R, T, R>(result, iter, combiner);
    }
    return result;
  }
  
  
  public static <T, R> SizedIterable<R> diagonalCrossFold(Iterable<? extends Iterable<? extends T>> iters, R base,
                                                          Lambda2<? super R, ? super T, ? extends R> combiner) {
    SizedIterable<R> result = singleton(base);
    for (Iterable<? extends T> iter : iters) {
      result = new DiagonalCartesianIterable<R, T, R>(result, iter, combiner);
    }
    return result;
  }
  
  
  public static <T1, A, S2> Iterable<S2> distribute(Iterable<? extends T1> original,
                                                    Lambda<? super T1, ? extends Iterable<? extends A>> breakT,
                                                    Lambda<? super Iterable<A>, ? extends S2> makeS) {
    
    Iterable<Iterable<? extends A>> sumOfProducts = map(original, breakT);
    Iterable<Iterable<A>> productOfSums = cross(sumOfProducts);
    return map(productOfSums, makeS);
  }

  
  public static <S1, T1, A, S2, T2> T2 distribute(S1 original,
                                                  Lambda<? super S1, ? extends Iterable<? extends T1>> breakS,
                                                  Lambda<? super T1, ? extends Iterable<? extends A>> breakT,
                                                  Lambda<? super Iterable<A>, ? extends S2> makeS,
                                                  Lambda<? super Iterable<S2>, ? extends T2> makeT) {
    return makeT.value(distribute(breakS.value(original), breakT, makeS));
  }
  
  
  
  public static <R> SizedIterable<R> valuesOf(Iterable<? extends Thunk<? extends R>> iter) {
    return new MappedIterable<Thunk<? extends R>, R>(iter, LambdaUtil.<R>thunkValueLambda());
  }
  
  
  public static <T, R> Iterable<R> valuesOf(Iterable<? extends Lambda<? super T, ? extends R>> iter, T arg) {
    Lambda<Lambda<? super T, ? extends R>, R> l = LambdaUtil.bindSecond(LambdaUtil.<T, R>applicationLambda(), arg);
    return new MappedIterable<Lambda<? super T, ? extends R>, R>(iter, l);
  }
    
  
  public static <T1, T2, R> SizedIterable<R> 
    valuesOf(Iterable<? extends Lambda2<? super T1, ? super T2, ? extends R>> iter, T1 arg1, T2 arg2) {
    Lambda<Lambda2<? super T1, ? super T2, ? extends R>, R> l = 
      LambdaUtil.bindSecond(LambdaUtil.bindThird(LambdaUtil.<T1, T2, R>binaryApplicationLambda(), arg2), arg1);
    return new MappedIterable<Lambda2<? super T1, ? super T2, ? extends R>, R>(iter, l);
  }
  
  
  public static <T1, T2, T3, R> SizedIterable<R>
    valuesOf(Iterable<? extends Lambda3<? super T1, ? super T2, ? super T3, ? extends R>> iter, 
             T1 arg1, T2 arg2, T3 arg3) {
    Lambda<Lambda3<? super T1, ? super T2, ? super T3, ? extends R>, R> l = 
      LambdaUtil.bindSecond(LambdaUtil.bindThird(LambdaUtil.bindFourth(
                            LambdaUtil.<T1, T2, T3, R>ternaryApplicationLambda(), arg3), arg2), arg1);
    return new MappedIterable<Lambda3<? super T1, ? super T2, ? super T3, ? extends R>, R>(iter, l);
  }
  

  
  public static <T> SizedIterable<T> pairFirsts(Iterable<? extends Pair<? extends T, ?>> iter) {
    return new MappedIterable<Pair<? extends T, ?>, T>(iter, Pair.<T>firstGetter());
  }
  
  
  public static <T> SizedIterable<T> pairSeconds(Iterable<? extends Pair<?, ? extends T>> iter) {
    return new MappedIterable<Pair<?, ? extends T>, T>(iter, Pair.<T>secondGetter());
  }
  
  
  public static <T> SizedIterable<T> tripleFirsts(Iterable<? extends Triple<? extends T, ?, ?>> iter) {
    return new MappedIterable<Triple<? extends T, ?, ?>, T>(iter, Triple.<T>firstGetter());
  }
  
  
  public static <T> SizedIterable<T> tripleSeconds(Iterable<? extends Triple<?, ? extends T, ?>> iter) {
    return new MappedIterable<Triple<?, ? extends T, ?>, T>(iter, Triple.<T>secondGetter());
  }
  
  
  public static <T> SizedIterable<T> tripleThirds(Iterable<? extends Triple<?, ?, ? extends T>> iter) {
    return new MappedIterable<Triple<?, ?, ? extends T>, T>(iter, Triple.<T>thirdGetter());
  }
  
  
  public static <T> SizedIterable<T> quadFirsts(Iterable<? extends Quad<? extends T, ?, ?, ?>> iter) {
    return new MappedIterable<Quad<? extends T, ?, ?, ?>, T>(iter, Quad.<T>firstGetter());
  }
  
  
  public static <T> SizedIterable<T> quadSeconds(Iterable<? extends Quad<?, ? extends T, ?, ?>> iter) {
    return new MappedIterable<Quad<?, ? extends T, ?, ?>, T>(iter, Quad.<T>secondGetter());
  }
  
  
  public static <T> SizedIterable<T> quadThirds(Iterable<? extends Quad<?, ?, ? extends T, ?>> iter) {
    return new MappedIterable<Quad<?, ?, ? extends T, ?>, T>(iter, Quad.<T>thirdGetter());
  }
  
  
  public static <T> SizedIterable<T> quadFourths(Iterable<? extends Quad<?, ?, ?, ? extends T>> iter) {
    return new MappedIterable<Quad<?, ?, ?, ? extends T>, T>(iter, Quad.<T>fourthGetter());
  }
  
  
  
  public static <T1, T2> SizedIterable<Pair<T1, T2>> zip(Iterable<? extends T1> iter1, Iterable<? extends T2> iter2) {
    return new BinaryMappedIterable<T1, T2, Pair<T1, T2>>(iter1, iter2, Pair.<T1, T2>factory());
  }
    
  
  public static <T1, T2, T3> SizedIterable<Triple<T1, T2, T3>> zip(Iterable<? extends T1> iter1, 
                                                                   Iterable<? extends T2> iter2,
                                                                   Iterable<? extends T3> iter3) {
    return map(iter1, iter2, iter3, Triple.<T1, T2, T3>factory());
  }
    
  
  public static <T1, T2, T3, T4> SizedIterable<Quad<T1, T2, T3, T4>> zip(Iterable<? extends T1> iter1, 
                                                                         Iterable<? extends T2> iter2,
                                                                         Iterable<? extends T3> iter3,
                                                                         Iterable<? extends T4> iter4) {
    return map(iter1, iter2, iter3, iter4, Quad.<T1, T2, T3, T4>factory());
  }
  
}
