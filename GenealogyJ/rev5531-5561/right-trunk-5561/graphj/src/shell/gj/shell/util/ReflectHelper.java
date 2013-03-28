
package gj.shell.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;


public class ReflectHelper {
  
  
  public static String getName(Class<?> clazz) {
    String result = clazz.getName();
    int dot = result.lastIndexOf('.');
    return dot<0 ? result : result.substring(dot+1); 
  }

  
  public static List<Method> getMethods(Object instance, String name, Class<?>[] arguments, boolean isPrefix) {
    
    
    Method[] methods = instance.getClass().getMethods();
    ArrayList<Method> collect = new ArrayList<Method>(methods.length);
    compliance: for (int m=0; m<methods.length; m++) {
      
      Method method = methods[m];
      
      if (!method.getName().matches(name)) continue;
      
      if (!Modifier.isPublic(method.getModifiers())) continue;
      
      if (Modifier.isStatic(method.getModifiers())) continue;
      
      Class<?>[] ptypes = method.getParameterTypes();
      for (int a=0; a<ptypes.length; a++) {
        if (a>=arguments.length) {
          if (!isPrefix) continue compliance;
          else break;
        }
        if (arguments[a]!=null&&!ptypes[a].isAssignableFrom(arguments[a])) continue compliance;
      }
      collect.add(method);
    }
    
    
    return collect;
  }

  
  public static List<Property> getProperties(Object instance, boolean primitiveOnly) {
    
    
    List<Property> list = new ArrayList<Property>();
    
    
    List<Method> methods = getMethods(instance, ".*", new Class[0], false);
    for (Method getter : methods) {
      
      if (primitiveOnly&&!(getter.getReturnType().isPrimitive()||getter.getReturnType().isEnum())) 
        continue;
      
      String name = null;
      if (getter.getName().startsWith("is" )) name = getter.getName().substring(2);
      if (getter.getName().startsWith("get")) name = getter.getName().substring(3);
      if (name==null) continue;
      
      Method setter;
      try {
        String t = "set"+Character.toUpperCase(name.charAt(0))+name.substring(1);
        setter = instance.getClass().getMethod(t, new Class[]{getter.getReturnType()});
      } catch (NoSuchMethodException e) {
        continue;
      }
      
      list.add(new Property(instance, name, getter, setter));
    }
    
    
    return list;
  }

  
  public static boolean setValue(Property prop, Object value) {
    
    try {
      prop.setValue(value);
      return true;
    } catch (Throwable t) {
      return false;
    }
  }
  
  
  public static Object getValue(Property prop) {
    
    try {
      return prop.getValue();
    } catch (Throwable t) {
      return null;
    }
  }
  
  
  
  
  public static Object wrap(Object instance, Class<?> target) {
    
    if (Boolean.TYPE.equals(target)) {
      target = Boolean.class;
    }
    if (Integer.TYPE.equals(target)) {
      target = Integer.class;
    }
    if (Short.TYPE.equals(target)) {
      target = Short.class;
    }
    if (Character.TYPE.equals(target)) {
      target = Character.class;
    }
    if (Long.TYPE.equals(target)) {
      target = Long.class;
    }
    if (Double.TYPE.equals(target)) {
      target = Double.class;
    }
    
    if (target.isAssignableFrom(instance.getClass()))
      return instance;
    
    try {
      Constructor<?> constructor = target.getConstructor(new Class[]{ instance.getClass() } );
      return constructor.newInstance(new Object[]{instance});
    } catch (Throwable t) {
      throw new IllegalArgumentException("Couldn't wrap "+instance.getClass()+" in an instance of "+target);
    }
    
  }
  
  
  public static Object getInstance(String type, Class<?> target) {
    try {
      Class<?> c = Class.forName(type);
      if (!target.isAssignableFrom(c)) return null;
      return c.newInstance();
    } catch (Throwable t) {
      return null;
    }
  }
  
  
  public static class Property implements Comparable<Property> {
    
    private Object instance;
    
    private String name;
    
    private Method getter;
    
    private Method setter;
    
    protected Property(Object i, String n, Method g, Method s) {
      instance = i;
      name   = n;
      getter = g;
      setter = s;
    }
    
    public String getName() {
      return name;
    }
    
    public Class<?> getType() {
      return getter.getReturnType();
    }
    
    public Object getInstance() {
      return instance;
    }
    
    public Object getValue() throws IllegalAccessException, InvocationTargetException {
      return getter.invoke(instance, new Object[0]);
    }
    
    public void setValue(Object value) throws IllegalAccessException, InvocationTargetException {
      setter.invoke(instance, new Object[]{ wrap(value, setter.getParameterTypes()[0]) });
    }
    
    @Override
    public String toString() {
      Object value;
      try {
        value = getValue();
      } catch (Throwable t) {
        value = t.toString();
      }
      return name+'='+value;
    }
    
    protected int getHierarchy() {
      int result = 0;
      Class<?> type = getter.getDeclaringClass();
      for (;type!=null;result++) type=type.getSuperclass();
      return result;
    }
    
    public int compareTo(Property other) {
      
      int i = other.getHierarchy()-getHierarchy();
      if (i==0) i = name.compareTo(other.name);
      return i;
    }

  } 
    
}
