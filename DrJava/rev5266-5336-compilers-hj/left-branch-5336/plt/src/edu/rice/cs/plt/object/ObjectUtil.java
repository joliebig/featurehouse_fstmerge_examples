

package edu.rice.cs.plt.object;

import java.util.Comparator;

import edu.rice.cs.plt.iter.IterUtil;


public final class ObjectUtil {
  
  private ObjectUtil() {}
  
  
  public static boolean equal(Object o1, Object o2) {
    return (o1 == null) ? (o2 == null) : o1.equals(o2);
  }
  
  
  
  private static final int KNUTH_CONST = -1640531535; 
  
  
  public static int hash() { return 1; }
  
  
  public static int hash(int a) { return KNUTH_CONST ^ a; }
  
  
  public static int hash(int a, int b) { return ((KNUTH_CONST ^ a) * KNUTH_CONST) ^ b; }
  
  
  public static int hash(int a, int b, int c) {
    return ((((KNUTH_CONST ^ a) * KNUTH_CONST) ^ b) * KNUTH_CONST) ^ c;
  }
  
  
  public static int hash(int a, int b, int c, int d) {
    return ((((((KNUTH_CONST ^ a) * KNUTH_CONST) ^ b) * KNUTH_CONST) ^ c) * KNUTH_CONST) ^ d;
  }
  
  
  public static int hash(int... keys) {
    int len = keys.length;
    int result = 1;
    for (int i = 0; i < len; i++) { result = (result * KNUTH_CONST) ^ keys[i]; }
    return result;
  }

  
  public static int hash(Object a) { return hash((a == null) ? 0 : a.hashCode()); }
  
  
  public static int hash(Object a, Object b) {
    return hash((a == null) ? 0 : a.hashCode(), (b == null) ? 0 : b.hashCode());
  }
  
  
  public static int hash(Object a, Object b, Object c) {
    return hash((a == null) ? 0 : a.hashCode(), (b == null) ? 0 : b.hashCode(),
                (c == null) ? 0 : c.hashCode());
  }
  
  
  public static int hash(Object a, Object b, Object c, Object d) {
    return hash((a == null) ? 0 : a.hashCode(), (b == null) ? 0 : b.hashCode(),
                (c == null) ? 0 : c.hashCode(), (d == null) ? 0 : d.hashCode());
  }
  
  
  public static int hash(Object... objs) {
    int len = objs.length;
    int[] keys = new int[len];
    for (int i = 0; i < len; i++) {
      Object obj = objs[i];
      keys[i] = (obj == null) ? 0 : obj.hashCode();
    }
    return hash(keys);
  }
  
  
  public static int hash(Iterable<?> iter) {
    int result = 1;
    for (Object obj : iter) {
      int key = (obj == null) ? 0 : obj.hashCode();
      result = (result * KNUTH_CONST) ^ key;
    }
    return result;
  }
  
  public static <T1 extends Comparable<? super T1>, T2 extends Comparable<? super T2>>
      int compare(T1 x1, T1 y1, T2 x2, T2 y2) {
    int result = x1.compareTo(y1);
    if (result == 0) { result = x2.compareTo(y2); }
    return result;
  }
  
  public static <T1, T2> int compare(Comparator<? super T1> comp1, T1 x1, T1 y1,
                                       Comparator<? super T2> comp2, T2 x2, T2 y2) {
    int result = comp1.compare(x1, y1);
    if (result == 0) { result = comp2.compare(x2, y2); }
    return result;
  }
  
  public static <T1 extends Comparable<? super T1>, T2 extends Comparable<? super T2>,
                   T3 extends Comparable<? super T3>>
      int compare(T1 x1, T1 y1, T2 x2, T2 y2, T3 x3, T3 y3) {
    int result = x1.compareTo(y1);
    if (result == 0) { result = x2.compareTo(y2); }
    if (result == 0) { result = x3.compareTo(y3); }
    return result;
  }
  
  public static <T1, T2, T3> int compare(Comparator<? super T1> comp1, T1 x1, T1 y1,
                                           Comparator<? super T2> comp2, T2 x2, T2 y2,
                                           Comparator<? super T3> comp3, T3 x3, T3 y3) {
    int result = comp1.compare(x1, y1);
    if (result == 0) { result = comp2.compare(x2, y2); }
    if (result == 0) { result = comp3.compare(x3, y3); }
    return result;
  }

  public static <T1 extends Comparable<? super T1>, T2 extends Comparable<? super T2>,
                   T3 extends Comparable<? super T3>, T4 extends Comparable<? super T4>>
      int compare(T1 x1, T1 y1, T2 x2, T2 y2, T3 x3, T3 y3, T4 x4, T4 y4) {
    int result = x1.compareTo(y1);
    if (result == 0) { result = x2.compareTo(y2); }
    if (result == 0) { result = x3.compareTo(y3); }
    if (result == 0) { result = x4.compareTo(y4); }
    return result;
  }
  
  public static <T1, T2, T3, T4> int compare(Comparator<? super T1> comp1, T1 x1, T1 y1,
                                               Comparator<? super T2> comp2, T2 x2, T2 y2,
                                               Comparator<? super T3> comp3, T3 x3, T3 y3,
                                               Comparator<? super T4> comp4, T4 x4, T4 y4) {
    int result = comp1.compare(x1, y1);
    if (result == 0) { result = comp2.compare(x2, y2); }
    if (result == 0) { result = comp3.compare(x3, y3); }
    if (result == 0) { result = comp4.compare(x4, y4); }
    return result;
  }


  
  public static int compositeHeight(Object obj) {
    if (obj instanceof Composite) { return ((Composite) obj).compositeHeight(); }
    else { return 0; }
  }
  
  
  public static int compositeHeight(Object... objs) { return compositeHeight(IterUtil.asIterable(objs)); }
  
  
  public static int compositeHeight(Iterable<?> objs) {
    int result = 0;
    for (Object obj : objs) {
      int height = compositeHeight(obj);
      if (result < height) { result = height; }
    }
    return result;
  }
  
  
  public static int compositeSize(Object obj) {
    if (obj instanceof Composite) { return ((Composite) obj).compositeSize(); }
    else { return 1; }
  }
  
  
  public static int compositeSize(Object... objs) { return compositeSize(IterUtil.asIterable(objs)); }
  
  
  public static int compositeSize(Iterable<?> objs) {
    int result = 0;
    for (Object obj : objs) { result += compositeSize(obj); }
    return result;
  }
  
}
