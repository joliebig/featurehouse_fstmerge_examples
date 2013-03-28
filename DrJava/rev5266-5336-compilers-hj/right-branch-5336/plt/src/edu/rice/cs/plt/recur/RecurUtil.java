

package edu.rice.cs.plt.recur;

import java.util.Arrays;
import edu.rice.cs.plt.lambda.Lambda;
import edu.rice.cs.plt.lambda.Lambda2;
import edu.rice.cs.plt.lambda.Predicate2;
import edu.rice.cs.plt.lambda.LambdaUtil;
import edu.rice.cs.plt.collect.TotalMap;
import edu.rice.cs.plt.reflect.ReflectUtil;
import edu.rice.cs.plt.text.TextUtil;


public final class RecurUtil {
  
  private static final TotalMap<Thread, RecursionStack<Object>> TO_STRING_STACKS;
  private static final TotalMap<Thread, RecursionStack2<Object, Object>> EQUALS_STACKS;
  private static final TotalMap<Thread, RecursionStack<Object>> HASH_CODE_STACKS;
  
  private static final Lambda<ArrayStringMode, Lambda<Object, String>> TO_STRING_GENERATOR;
  private static final Lambda<ArrayStringMode, Lambda<Object, String>> DEFAULT_INF_STRING_GENERATOR;
  private static final Lambda2<Object, Object, Boolean> EQUALS;
  private static final Lambda<Object, Integer> HASH_CODE;
  private static final Lambda<Object, Integer> DEFAULT_INF_HASH_CODE;
  
  static {
    Lambda<Thread, RecursionStack<Object>> makeNew = new Lambda<Thread, RecursionStack<Object>>() {
      public RecursionStack<Object> value(Thread t) { return new RecursionStack<Object>(); }
    };
    Lambda<Thread, RecursionStack2<Object, Object>> makeNew2 = 
      new Lambda<Thread, RecursionStack2<Object, Object>>() {
        public RecursionStack2<Object, Object> value(Thread t) { 
          return new RecursionStack2<Object, Object>();
        }
      };
    
    
    TO_STRING_STACKS = new TotalMap<Thread, RecursionStack<Object>>(makeNew, true);
    EQUALS_STACKS = new TotalMap<Thread, RecursionStack2<Object, Object>>(makeNew2, true);
    HASH_CODE_STACKS = new TotalMap<Thread, RecursionStack<Object>>(makeNew, true);
    
    TO_STRING_GENERATOR = LambdaUtil.curry(new Lambda2<ArrayStringMode, Object, String>() {
      public String value(ArrayStringMode mode, Object obj) {
        if (obj.getClass().isArray()) { return arrayToString(obj, mode); }
        else { return obj.toString(); }
      }
    });

    DEFAULT_INF_STRING_GENERATOR = LambdaUtil.curry(new Lambda2<ArrayStringMode, Object, String>() {
      public String value(ArrayStringMode mode, Object obj) { 
        if (obj.getClass().isArray()) { return mode.prefix() + "..." + mode.suffix(); }
        else { return ReflectUtil.simpleName(obj.getClass()) + "..."; }
      }
    });
    
    EQUALS = new Lambda2<Object, Object, Boolean>() {
      public Boolean value(Object obj1, Object obj2) {
        if (obj1.getClass().isArray()) {
          if (obj2.getClass().isArray()) { return arrayEquals(obj1, obj2); }
          else { return false; }
        }
        else {
          if (obj2.getClass().isArray()) { return false; }
          else { return obj1.equals(obj2); }
        }
      }
    };
    
    HASH_CODE = new Lambda<Object, Integer>() {
      public Integer value(Object obj) {
        if (obj.getClass().isArray()) { return arrayHashCode(obj); }
        else { return obj.hashCode(); }
      }
    };
    
    DEFAULT_INF_HASH_CODE = LambdaUtil.valueLambda(0xB32FC891);
  }
  
  
  private RecurUtil() {}
  
  
  public static String safeToString(Object obj) {
    return safeToString(obj, DEFAULT_INF_STRING_GENERATOR.value(ArrayStringMode.DEEP_BRACED), 
                        1, ArrayStringMode.DEEP_BRACED);
  }

  
  public static String safeToString(Object obj, String infiniteString) {
    return safeToString(obj, infiniteString, 1, ArrayStringMode.DEEP_BRACED);
  }

  
  public static <T> String safeToString(T obj, Lambda<? super T, String> infiniteString) {
    return safeToString(obj, infiniteString, 1, ArrayStringMode.DEEP_BRACED);
  }

  
  public static String safeToString(Object obj, ArrayStringMode arrayMode) {
    return safeToString(obj, DEFAULT_INF_STRING_GENERATOR.value(arrayMode), 1, arrayMode);
  }
  
  
  public static String safeToString(Object obj, String infiniteString, ArrayStringMode arrayMode) {
    return safeToString(obj, LambdaUtil.valueLambda(infiniteString), 1, arrayMode);
  }
  
  
  public static <T> String safeToString(T obj, Lambda<? super T, String> infiniteString,
                                        ArrayStringMode arrayMode) {
    return safeToString(obj, infiniteString, 1, arrayMode);
  }
  
  
  public static String safeToString(Object obj, int depth) {
    return safeToString(obj, DEFAULT_INF_STRING_GENERATOR.value(ArrayStringMode.DEEP_BRACED), 
                        depth, ArrayStringMode.DEEP_BRACED);
  }

  
  public static String safeToString(Object obj, String infiniteString, int depth) {
    return safeToString(obj, infiniteString, depth, ArrayStringMode.DEEP_BRACED);
  }

  
  public static <T> String safeToString(T obj, Lambda<? super T, String> infiniteString, int depth) {
    return safeToString(obj, infiniteString, depth, ArrayStringMode.DEEP_BRACED);
  }

  
  public static String safeToString(Object obj, int depth, ArrayStringMode arrayMode) {
    return safeToString(obj, DEFAULT_INF_STRING_GENERATOR.value(arrayMode), depth, arrayMode);
  }
  
  
  public static String safeToString(Object obj, String infiniteString, int depth, 
                                    ArrayStringMode arrayMode) {
    return safeToString(obj, LambdaUtil.valueLambda(infiniteString), depth, arrayMode);
  }
  
  
  public static <T> String safeToString(T obj, Lambda<? super T, String> infiniteString, int depth,
                                        ArrayStringMode arrayMode) {
    if (obj == null) { return "null"; }
    else {
      RecursionStack<Object> stack;
      Thread t = Thread.currentThread();
      synchronized (TO_STRING_STACKS) { stack = TO_STRING_STACKS.get(t); }
      
      String result = stack.<T, String>apply(TO_STRING_GENERATOR.value(arrayMode), infiniteString, obj);
      if (stack.isEmpty()) {
        synchronized (TO_STRING_STACKS) { TO_STRING_STACKS.revert(t); }
      }
      return result;
    }
  }
  
  
  public static String arrayToString(Object array) {
    return arrayToString(array, ArrayStringMode.DEEP_BRACED);
  }
  
  
  public static String arrayToString(Object array, ArrayStringMode stringMode) {
    if (array instanceof Object[]) { return arrayToString((Object[]) array, stringMode); }
    else if (array instanceof int[]) { return arrayToString((int[]) array, stringMode); }
    else if (array instanceof char[]) { return arrayToString((char[]) array, stringMode); }
    else if (array instanceof byte[]) { return arrayToString((byte[]) array, stringMode); }
    else if (array instanceof double[]) { return arrayToString((double[]) array, stringMode); }
    else if (array instanceof boolean[]) { return arrayToString((boolean[]) array, stringMode); }
    else if (array instanceof short[]) { return arrayToString((short[]) array, stringMode); }
    else if (array instanceof long[]) { return arrayToString((long[]) array, stringMode); }
    else if (array instanceof float[]) { return arrayToString((float[]) array, stringMode); }
    else { throw new IllegalArgumentException("Non-array argument"); }
  }
  
  
  public static String arrayToString(Object[] array) {
    return arrayToString(array, ArrayStringMode.DEEP_BRACED);
  }
  
  
  public static String arrayToString(Object[] array, ArrayStringMode stringMode) {
    switch (stringMode) {
      case CLASS_NAME:
        return array.toString();
        
      case TYPE_AND_SIZE:
        return ReflectUtil.simpleName(array.getClass().getComponentType()) + "[" + array.length + "]";

      default:
        StringBuilder result = new StringBuilder();
        result.append(stringMode.prefix());
        boolean first = true;
        for (Object elt : array) {
          if (first) { first = false; }
          else { result.append(stringMode.delimiter()); }
          result.append(safeToString(elt, stringMode.nestedMode()));
        }
        result.append(stringMode.suffix());
        return result.toString();
    }
  }
  
  
  public static String arrayToString(boolean[] array) {
    return arrayToString(array, ArrayStringMode.DEEP_BRACED);
  }
  
  
  public static String arrayToString(boolean[] array, ArrayStringMode stringMode) {
    switch (stringMode) {
      case CLASS_NAME:
        return array.toString();
        
      case TYPE_AND_SIZE:
        return "boolean[" + array.length + "]";

      default:
        StringBuilder result = new StringBuilder();
        result.append(stringMode.prefix());
        boolean first = true;
        for (boolean elt : array) {
          if (first) { first = false; }
          else { result.append(stringMode.delimiter()); }
          result.append(elt);
        }
        result.append(stringMode.suffix());
        return result.toString();
    }
  }
  
  
  public static String arrayToString(char[] array) {
    return arrayToString(array, ArrayStringMode.DEEP_BRACED);
  }
  
  
  public static String arrayToString(char[] array, ArrayStringMode stringMode) {
    switch (stringMode) {
      case CLASS_NAME:
        return array.toString();
        
      case TYPE_AND_SIZE:
        return "char[" + array.length + "]";

      default:
        StringBuilder result = new StringBuilder();
        result.append(stringMode.prefix());
        boolean first = true;
        for (char elt : array) {
          if (first) { first = false; }
          else { result.append(stringMode.delimiter()); }
          result.append(elt);
        }
        result.append(stringMode.suffix());
        return result.toString();
    }
  }
  
  
  public static String arrayToString(byte[] array) {
    return arrayToString(array, ArrayStringMode.DEEP_BRACED);
  }
  
  
  public static String arrayToString(byte[] array, ArrayStringMode stringMode) {
    switch (stringMode) {
      case CLASS_NAME:
        return array.toString();
        
      case TYPE_AND_SIZE:
        return "byte[" + array.length + "]";

      default:
        StringBuilder result = new StringBuilder();
        result.append(stringMode.prefix());
        boolean first = true;
        for (byte elt : array) {
          if (first) { first = false; }
          else { result.append(stringMode.delimiter()); }
          result.append(elt);
        }
        result.append(stringMode.suffix());
        return result.toString();
    }
  }
  
  
  public static String arrayToString(short[] array) {
    return arrayToString(array, ArrayStringMode.DEEP_BRACED);
  }
  
  
  public static String arrayToString(short[] array, ArrayStringMode stringMode) {
    switch (stringMode) {
      case CLASS_NAME:
        return array.toString();
        
      case TYPE_AND_SIZE:
        return "short[" + array.length + "]";

      default:
        StringBuilder result = new StringBuilder();
        result.append(stringMode.prefix());
        boolean first = true;
        for (short elt : array) {
          if (first) { first = false; }
          else { result.append(stringMode.delimiter()); }
          result.append(elt);
        }
        result.append(stringMode.suffix());
        return result.toString();
    }
  }
  
  
  public static String arrayToString(int[] array) {
    return arrayToString(array, ArrayStringMode.DEEP_BRACED);
  }
  
  
  public static String arrayToString(int[] array, ArrayStringMode stringMode) {
    switch (stringMode) {
      case CLASS_NAME:
        return array.toString();
        
      case TYPE_AND_SIZE:
        return "int[" + array.length + "]";

      default:
        StringBuilder result = new StringBuilder();
        result.append(stringMode.prefix());
        boolean first = true;
        for (int elt : array) {
          if (first) { first = false; }
          else { result.append(stringMode.delimiter()); }
          result.append(elt);
        }
        result.append(stringMode.suffix());
        return result.toString();
    }
  }
  
  
  public static String arrayToString(long[] array) {
    return arrayToString(array, ArrayStringMode.DEEP_BRACED);
  }
  
  
  public static String arrayToString(long[] array, ArrayStringMode stringMode) {
    switch (stringMode) {
      case CLASS_NAME:
        return array.toString();
        
      case TYPE_AND_SIZE:
        return "long[" + array.length + "]";

      default:
        StringBuilder result = new StringBuilder();
        result.append(stringMode.prefix());
        boolean first = true;
        for (long elt : array) {
          if (first) { first = false; }
          else { result.append(stringMode.delimiter()); }
          result.append(elt);
        }
        result.append(stringMode.suffix());
        return result.toString();
    }
  }
  
  
  public static String arrayToString(float[] array) {
    return arrayToString(array, ArrayStringMode.DEEP_BRACED);
  }
  
  
  public static String arrayToString(float[] array, ArrayStringMode stringMode) {
    switch (stringMode) {
      case CLASS_NAME:
        return array.toString();
        
      case TYPE_AND_SIZE:
        return "float[" + array.length + "]";

      default:
        StringBuilder result = new StringBuilder();
        result.append(stringMode.prefix());
        boolean first = true;
        for (float elt : array) {
          if (first) { first = false; }
          else { result.append(stringMode.delimiter()); }
          result.append(elt);
        }
        result.append(stringMode.suffix());
        return result.toString();
    }
  }
  
  
  public static String arrayToString(double[] array) {
    return arrayToString(array, ArrayStringMode.DEEP_BRACED);
  }
  
  
  public static String arrayToString(double[] array, ArrayStringMode stringMode) {
    switch (stringMode) {
      case CLASS_NAME:
        return array.toString();
        
      case TYPE_AND_SIZE:
        return "double[" + array.length + "]";

      default:
        StringBuilder result = new StringBuilder();
        result.append(stringMode.prefix());
        boolean first = true;
        for (double elt : array) {
          if (first) { first = false; }
          else { result.append(stringMode.delimiter()); }
          result.append(elt);
        }
        result.append(stringMode.suffix());
        return result.toString();
    }
  }
  
  
  public static boolean safeEquals(Object obj1, Object obj2) {
    return safeEquals(obj1, obj2, LambdaUtil.TRUE);
  }
  
  
  public static <T1, T2> boolean safeEquals(T1 obj1, T2 obj2, 
                                            Predicate2<? super T1, ? super T2> infiniteEquals) {
    if (obj1 == null) { return obj2 == null; }
    else if (obj2 == null) { return false; }
    else {
      RecursionStack2<Object, Object> stack;
      Thread t = Thread.currentThread();
      synchronized (EQUALS_STACKS) { stack = EQUALS_STACKS.get(t); }
      
      boolean result = stack.<T1, T2, Boolean>apply(EQUALS, LambdaUtil.asLambda(infiniteEquals), obj1, obj2);
      if (stack.isEmpty()) {
        synchronized (EQUALS_STACKS) { EQUALS_STACKS.revert(t); }
      }
      return result;
    }
  }
  
  
  public static boolean arrayEquals(Object a1, Object a2) {
    if (!a1.getClass().isArray() || !a2.getClass().isArray()) { 
      throw new IllegalArgumentException("Non-array argument");
    }
    if (a1 instanceof Object[] && a2 instanceof Object[]) { 
      return arrayEquals((Object[]) a1, (Object[]) a2);
    }
    else if (!a1.getClass().equals(a2.getClass())) { return false; }
    else if (a1 instanceof int[]) { return Arrays.equals((int[]) a1, (int[]) a2); }
    else if (a1 instanceof char[]) { return Arrays.equals((char[]) a1, (char[]) a2); }
    else if (a1 instanceof byte[]) { return Arrays.equals((byte[]) a1, (byte[]) a2); }
    else if (a1 instanceof double[]) { return Arrays.equals((double[]) a1, (double[]) a2); }
    else if (a1 instanceof boolean[]) { return Arrays.equals((boolean[]) a1, (boolean[]) a2); }
    else if (a1 instanceof short[]) { return Arrays.equals((short[]) a1, (short[]) a2); }
    else if (a1 instanceof long[]) { return Arrays.equals((long[]) a1, (long[]) a2); }
    else if (a1 instanceof float[]) { return Arrays.equals((float[]) a1, (float[]) a2); }
    else { throw new IllegalArgumentException("Unrecognized array type"); }
  }
  
  
  public static boolean arrayEquals(Object[] a1, Object[] a2) {
    if (a1.length != a2.length) { return false; }
    for (int i = 0; i < a1.length; i++) { if (!safeEquals(a1[i], a2[i])) { return false; } }
    return true;
  }
  
  
  
  public static int safeHashCode(Object obj) {
    return safeHashCode(obj, DEFAULT_INF_HASH_CODE);
  }
  
  
  public static int safeHashCode(Object obj, int infiniteHashCode) {
    return safeHashCode(obj, LambdaUtil.valueLambda(infiniteHashCode));
  }
  
  
  public static <T> int safeHashCode(T obj, Lambda<? super T, Integer> infiniteHashCode) {
    if (obj == null) { return 0; }
    else {
      RecursionStack<Object> stack;
      Thread t = Thread.currentThread();
      synchronized (HASH_CODE_STACKS) { stack = HASH_CODE_STACKS.get(t); }
      
      int result = stack.<T, Integer>apply(HASH_CODE, infiniteHashCode, obj);
      if (stack.isEmpty()) {
        synchronized (HASH_CODE_STACKS) { HASH_CODE_STACKS.revert(t); }
      }
      return result;
    }
  }
  
  
  public static int arrayHashCode(Object array) {
    if (array instanceof Object[]) { return arrayHashCode((Object[]) array); }
    else if (array instanceof int[]) { return arrayHashCode((int[]) array); }
    else if (array instanceof char[]) { return arrayHashCode((char[]) array); }
    else if (array instanceof byte[]) { return arrayHashCode((byte[]) array); }
    else if (array instanceof double[]) { return arrayHashCode((double[]) array); }
    else if (array instanceof boolean[]) { return arrayHashCode((boolean[]) array); }
    else if (array instanceof short[]) { return arrayHashCode((short[]) array); }
    else if (array instanceof long[]) { return arrayHashCode((long[]) array); }
    else if (array instanceof float[]) { return arrayHashCode((float[]) array); }
    else { throw new IllegalArgumentException("Non-array argument"); }
  }
  
  
  public static int arrayHashCode(Object[] array) {
    int result = 1;
    for (Object elt : array) { result = result*31 + safeHashCode(elt); }
    return result;
  }
  
  
  public static int arrayHashCode(boolean[] array) {
    int result = 1;
    for (boolean elt : array) { result = result*31 + ((Boolean) elt).hashCode(); }
    return result;
  }

  
  public static int arrayHashCode(char[] array) {
    int result = 1;
    for (char elt : array) { result = result*31 + ((Character) elt).hashCode(); }
    return result;
  }

  
  public static int arrayHashCode(byte[] array) {
    int result = 1;
    for (byte elt : array) { result = result*31 + ((Byte) elt).hashCode(); }
    return result;
  }

  
  public static int arrayHashCode(short[] array) {
    int result = 1;
    for (short elt : array) { result = result*31 + ((Short) elt).hashCode(); }
    return result;
  }

  
  public static int arrayHashCode(int[] array) {
    int result = 1;
    for (int elt : array) { result = result*31 + ((Integer) elt).hashCode(); }
    return result;
  }

  
  public static int arrayHashCode(long[] array) {
    int result = 1;
    for (long elt : array) { result = result*31 + ((Long) elt).hashCode(); }
    return result;
  }

  
  public static int arrayHashCode(float[] array) {
    int result = 1;
    for (float elt : array) { result = result*31 + ((Float) elt).hashCode(); }
    return result;
  }

  
  public static int arrayHashCode(double[] array) {
    int result = 1;
    for (double elt : array) { result = result*31 + ((Double) elt).hashCode(); }
    return result;
  }


  
  public static enum ArrayStringMode {
    
    CLASS_NAME { 
      protected String prefix() { throw new IllegalArgumentException(); }
      protected String delimiter() { throw new IllegalArgumentException(); }
      protected String suffix() { throw new IllegalArgumentException(); }
      protected ArrayStringMode nestedMode() { throw new IllegalArgumentException(); }
    }, 
    
    TYPE_AND_SIZE {
      protected String prefix() { throw new IllegalArgumentException(); }
      protected String delimiter() { throw new IllegalArgumentException(); }
      protected String suffix() { throw new IllegalArgumentException(); }
      protected ArrayStringMode nestedMode() { throw new IllegalArgumentException(); }
    },
    
    SHALLOW_BRACKETED { 
      protected String prefix() { return "["; }
      protected String delimiter() { return ", "; }
      protected String suffix() { return "]"; }
      protected ArrayStringMode nestedMode() { return CLASS_NAME; }
    }, 
    
    DEEP_BRACKETED { 
      protected String prefix() { return "["; }
      protected String delimiter() { return ", "; }
      protected String suffix() { return "]"; }
      protected ArrayStringMode nestedMode() { return DEEP_BRACKETED; }
    },
    
    SHALLOW_BRACED { 
      protected String prefix() { return "{ "; }
      protected String delimiter() { return ", "; }
      protected String suffix() { return " }"; }
      protected ArrayStringMode nestedMode() { return TYPE_AND_SIZE; }
    }, 
    
    DEEP_BRACED {
      protected String prefix() { return "{ "; }
      protected String delimiter() { return ", "; }
      protected String suffix() { return " }"; }
      protected ArrayStringMode nestedMode() { return DEEP_BRACED; }
    },
    
    DEEP_MULTILINE {
      protected String prefix() { return ""; }
      protected String delimiter() { return TextUtil.NEWLINE; }
      protected String suffix() { return ""; }
      protected ArrayStringMode nestedMode() { return DEEP_BRACED; }
    },
    
    SHALLOW_MULTILINE {
      protected String prefix() { return ""; }
      protected String delimiter() { return TextUtil.NEWLINE; }
      protected String suffix() { return ""; }
      protected ArrayStringMode nestedMode() { return SHALLOW_BRACED; }
    }; 
    
    protected abstract String prefix();
    protected abstract String delimiter();
    protected abstract String suffix();
    protected abstract ArrayStringMode nestedMode();
  }

}
