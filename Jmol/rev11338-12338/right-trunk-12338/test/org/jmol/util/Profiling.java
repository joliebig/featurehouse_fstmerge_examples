

package org.jmol.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class Profiling {

  private static long start;
  private final static Method method;
  private final static String unit;

  static {
    Method tmpMethod = null;
    String tmpUnit   = null;
    try {
      tmpMethod = System.class.getDeclaredMethod("nanoTime", null);
      tmpUnit = "ns";
    } catch (NoSuchMethodException e) {
      
    }
    if (tmpMethod == null) {
      try {
        tmpMethod = System.class.getDeclaredMethod("currentTimeMillis", null);
        tmpUnit = "ms";
      } catch (NoSuchMethodException e) {
        System.err.println("No System.currentTimeMillis() method");
      }
    }
    method = tmpMethod;
    unit = tmpUnit;
  }

  public static void startProfiling() {
    start = getTime();
  }
  
  public static void logProfiling(String txt) {
    long delta = getTime() - start;
    String label = ("            " + delta);
    label = label.substring(label.length() - 12, label.length());
    if (delta > 100000) {
      System.err.println(label + "ns: " + txt);
    }
    System.err.flush();
    start = getTime();
  }

  public static long getTime() {
    if (method == null) {
      return 0;
    }
    Object result = null;
    try {
      result = method.invoke(null, null);
    } catch (IllegalArgumentException e) {
      
    } catch (IllegalAccessException e) {
      
    } catch (InvocationTargetException e) {
      
    }
    if (result instanceof Long) {
      return ((Long) result).longValue();
    }
    return 0;
  }

  public static String getUnit() {
    return unit;
  }
}
