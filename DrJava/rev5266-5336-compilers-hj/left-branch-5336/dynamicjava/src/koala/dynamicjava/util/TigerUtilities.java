



package koala.dynamicjava.util;


import java.lang.reflect.*;

import koala.dynamicjava.interpreter.error.*;


public class TigerUtilities {

  
  
  
  
  public static final int BRIDGE     = 0x00000040;
  public static final int VARARGS    = 0x00000080;
  public static final int SYNTHETIC  = 0x00001000;
  public static final int ANNOTATION = 0x00002000;
  public static final int ENUM       = 0x00004000;
  
  
  public static final float VERSION = Float.parseFloat(System.getProperty("java.specification.version"));

  
  private static boolean _tigerEnabled;
  
  static {
    resetVersion();
  }
  
  
  
  public static void resetVersion() {
    _tigerEnabled = (VERSION > 1.49); 
  }

  
  public static boolean isTigerEnabled() { return _tigerEnabled; }

  
  public static void setTigerEnabled(boolean enabled) {
    _tigerEnabled = enabled;
  }

  
  public static void assertTigerEnabled(String msg) {
    if(!_tigerEnabled)
      throw new WrongVersionException(msg);
  }

  
  public static boolean isVarArgs(Method m) {
    return _tigerEnabled && ((m.getModifiers() & VARARGS) != 0);
  }

  
  public static boolean isVarArgs(Constructor<?> c) {
    return _tigerEnabled && ((c.getModifiers() & VARARGS) != 0);
  }
  
  
  public static boolean isBridge(Method m) { 
    return _tigerEnabled && ((m.getModifiers() & BRIDGE) != 0);
  }

  
  public static boolean isEnum(Class<?> c) {
    
    
    
    
    
    
    try {
      return _tigerEnabled && (c.getSuperclass() != null) && 
        (c.getSuperclass().equals(Class.forName("java.lang.Enum")));
    }
    catch (ClassNotFoundException e) { return false; }
  }

  
  public static boolean isEnumConstant(Field f) {
    return _tigerEnabled && ((f.getModifiers() & ENUM) != 0);
  }

  
  public static Class<?> correspondingBoxingType(Class<?> primType) {
    if (primType == boolean.class) { return Boolean.class; }
    else if (primType == byte.class) { return Byte.class; }
    else if (primType == char.class) { return Character.class; }
    else if (primType == short.class) { return Short.class; }
    else if (primType == int.class) { return Integer.class; }
    else if (primType == long.class) { return Long.class; }
    else if (primType == float.class) { return Float.class; }
    else if (primType == double.class) { return Double.class; }
    else {
      return primType; 
    }
  }

  
  public static Class<?> correspondingPrimType(Class<?> refType) {
    if (refType == Boolean.class) { return boolean.class; }
    else if (refType == Byte.class) { return byte.class; }
    else if (refType == Character.class) { return char.class; }
    else if (refType == Short.class) { return short.class; }
    else if (refType == Integer.class) { return int.class; }
    else if (refType == Long.class) { return long.class; }
    else if (refType == Float.class) { return float.class; }
    else if (refType == Double.class) { return double.class; }
    else {
      return refType; 
    }
  }

  
  public static boolean isBoxingType(Class<?> c) {
    return (c == Integer.class   || c == Long.class   ||
            c == Boolean.class   || c == Double.class ||
            c == Character.class || c == Short.class  ||
            c == Byte.class      || c == Float.class );
  }

    
  public static boolean isIntegralType(Class<?> c) {
    return (c == int.class   || c == Integer.class   ||
            c == long.class  || c == Long.class      ||
            c == byte.class  || c == Byte.class      ||
            c == char.class  || c == Character.class ||
            c == short.class || c == Short.class);
  }

  
  public static boolean boxesTo(Class<?> prim, Class<?> ref) {
    return
      ((prim == int.class    && ref == Integer.class)     ||
      (prim == long.class    && ref == Long.class)        ||
      (prim == byte.class    && ref == Byte.class)        ||
      (prim == char.class    && ref == Character.class)   ||
      (prim == short.class   && ref == Short.class)       ||
      (prim == boolean.class && ref == Boolean.class)     ||
      (prim == float.class   && ref == Float.class)       ||
      (prim == double.class  && ref == Double.class));
  }
}
