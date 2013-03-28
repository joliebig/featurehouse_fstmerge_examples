
package org.jmol.util;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Vector;

final public class ArrayUtil {

  public static Object ensureLength(Object array, int minimumLength) {
    if (array != null && Array.getLength(array) >= minimumLength)
      return array;
    return setLength(array, minimumLength);
  }

  public static String[] ensureLength(String[] array, int minimumLength) {
    if (array != null && array.length >= minimumLength)
      return array;
    return setLength(array, minimumLength);
  }

  public static float[] ensureLength(float[] array, int minimumLength) {
    if (array != null && array.length >= minimumLength)
      return array;
    return setLength(array, minimumLength);
  }

  public static int[] ensureLength(int[] array, int minimumLength) {
    if (array != null && array.length >= minimumLength)
      return array;
    return setLength(array, minimumLength);
  }

  public static short[] ensureLength(short[] array, int minimumLength) {
    if (array != null && array.length >= minimumLength)
      return array;
    return setLength(array, minimumLength);
  }

  public static byte[] ensureLength(byte[] array, int minimumLength) {
    if (array != null && array.length >= minimumLength)
      return array;
    return setLength(array, minimumLength);
  }

  public static Object doubleLength(Object array) {
    return setLength(array, (array == null ? 16 : 2 * Array.getLength(array)));
  }

  public static String[] doubleLength(String[] array) {
    return setLength(array, (array == null ? 16 : 2 * array.length));
  }

  public static float[] doubleLength(float[] array) {
    return setLength(array, (array == null ? 16 : 2 * array.length));
  }

  public static int[] doubleLength(int[] array) {
    return setLength(array, (array == null ? 16 : 2 * array.length));
  }

  public static short[] doubleLength(short[] array) {
    return setLength(array, (array == null ? 16 : 2 * array.length));
  }

  public static byte[] doubleLength(byte[] array) {
    return setLength(array, (array == null ? 16 : 2 * array.length));
  }

  public static boolean[] doubleLength(boolean[] array) {
    return setLength(array, (array == null ? 16 : 2 * array.length));
  }

  public static Object setLength(Object array, int newLength) {
    if (array == null) {
      return null; 
    }
    Object t = Array
        .newInstance(array.getClass().getComponentType(), newLength);
    int oldLength = Array.getLength(array);
    System.arraycopy(array, 0, t, 0, oldLength < newLength ? oldLength
        : newLength);
    return t;
  }

  public static Object deleteElements(Object array, int firstElement,
                                     int nElements) {
    if (nElements == 0 || array == null)
      return array;
    int oldLength = Array.getLength(array);
    if (oldLength - nElements <= 0)
      return array;
    Object t = Array.newInstance(array.getClass().getComponentType(), oldLength
        - nElements);
    if (firstElement > 0)
      System.arraycopy(array, 0, t, 0, firstElement);
    int n = oldLength - firstElement - nElements;
    if (n > 0)
      System.arraycopy(array, firstElement + nElements, t, firstElement, n);
    return t;
  }

  public static String[] setLength(String[] array, int newLength) {
    String[] t = new String[newLength];
    if (array != null) {
      int oldLength = array.length;
      System.arraycopy(array, 0, t, 0, oldLength < newLength ? oldLength
          : newLength);
    }
    return t;
  }

  public static float[] setLength(float[] array, int newLength) {
    float[] t = new float[newLength];
    if (array != null) {
      int oldLength = array.length;
      System.arraycopy(array, 0, t, 0, oldLength < newLength ? oldLength
          : newLength);
    }
    return t;
  }

  public static int[] setLength(int[] array, int newLength) {
    int[] t = new int[newLength];
    if (array != null) {
      int oldLength = array.length;
      System.arraycopy(array, 0, t, 0, oldLength < newLength ? oldLength
          : newLength);
    }
    return t;
  }

  public static int[] arrayCopy(int[] array, int i0, int n, boolean isReverse) {
    if (array == null)
      return null;
    int oldLength = array.length;
    if (n == -1) n = oldLength;
    if (n == -2) n = oldLength / 2;
    n = n - i0;
    int[] t = new int[n];
    System.arraycopy(array, i0, t, 0, n);
    if (isReverse)
      for (int i = n / 2; --i >= 0;)
        swap(t, i, n - 1 - i);
    return t;
  }

  public static short[] setLength(short[] array, int newLength) {
    short[] t = new short[newLength];
    if (array != null) {
      int oldLength = array.length;
      System.arraycopy(array, 0, t, 0, oldLength < newLength ? oldLength
          : newLength);
    }
    return t;
  }

  public static byte[] setLength(byte[] array, int newLength) {
    byte[] t = new byte[newLength];
    if (array != null) {
      int oldLength = array.length;
      System.arraycopy(array, 0, t, 0, oldLength < newLength ? oldLength
          : newLength);
    }
    return t;
  }

  public static boolean[] setLength(boolean[] array, int newLength) {
    boolean[] t = new boolean[newLength];
    if (array != null) {
      int oldLength = array.length;
      System.arraycopy(array, 0, t, 0, oldLength < newLength ? oldLength
          : newLength);
    }
    return t;
  }

  public static void swap(int[] array, int indexA, int indexB) {
    int t = array[indexA];
    array[indexA] = array[indexB];
    array[indexB] = t;
  }

  
  
  public static String dumpArray(String msg, float[][] A, int x1, int x2, int y1, int y2) {
    String s = "dumpArray: " + msg + "\n";
    for (int x = x1; x <= x2; x++)
      s += "\t*" + x + "*";
    for (int y = y2; y >= y1; y--) {
      s += "\n*" + y + "*";
      for (int x = x1; x <= x2; x++)
        s += "\t" + (x < A.length && y < A[x].length ? A[x][y] : Float.NaN);
    }
    return s;
  }

  public static String dumpIntArray(int[] A, int n) {
    String str = "";
    for (int i = 0; i < n; i++)
      str += " " + A[i];
    return str;
  }

  public static String sortedItem(Vector v, int n) {
    if (v.size() == 0)
      return null;
    if (v.size() == 1)
      return (String) v.get(0);    
    String[] keys = new String[v.size()];
    for (int i = 0; i < keys.length; i++)
      keys[i] = (String) v.get(i);
    Arrays.sort(keys);
    return keys[n % keys.length];
  }

}
