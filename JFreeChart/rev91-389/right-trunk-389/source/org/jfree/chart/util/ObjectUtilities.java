

package org.jfree.chart.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.StringTokenizer;


public final class ObjectUtilities {

    
    public static final String THREAD_CONTEXT = "ThreadContext";
    
    public static final String CLASS_CONTEXT = "ClassContext";

    
    private static String classLoaderSource = THREAD_CONTEXT;
    
    private static ClassLoader classLoader;

    
    private ObjectUtilities() {
    }

    
    public static String getClassLoaderSource() {
        return classLoaderSource;
    }

    
    public static void setClassLoaderSource(final String classLoaderSource) {
        ObjectUtilities.classLoaderSource = classLoaderSource;
    }

    
    public static boolean equal(final Object o1, final Object o2) {
        if (o1 == o2) {
            return true;
        }
        if (o1 != null) {
            return o1.equals(o2);
        }
        else {
            return false;
        }
    }

    
    public static int hashCode(final Object object) {
        int result = 0;
        if (object != null) {
            result = object.hashCode();
        }
        return result;
    }

    
    public static Object clone(final Object object)
        throws CloneNotSupportedException {
        if (object == null) {
            throw new IllegalArgumentException("Null 'object' argument.");
        }
        if (object instanceof PublicCloneable) {
            final PublicCloneable pc = (PublicCloneable) object;
            return pc.clone();
        }
        else {
            try {
                final Method method = object.getClass().getMethod("clone",
                        (Class[]) null);
                if (Modifier.isPublic(method.getModifiers())) {
                    return method.invoke(object, (Object[]) null);
                }
            }
            catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        throw new CloneNotSupportedException("Failed to clone.");
    }

    
    public static Collection deepClone(final Collection collection)
        throws CloneNotSupportedException {

        if (collection == null) {
            throw new IllegalArgumentException("Null 'collection' argument.");
        }
        
        
        
        final Collection result
            = (Collection) ObjectUtilities.clone(collection);
        result.clear();
        final Iterator iterator = collection.iterator();
        while (iterator.hasNext()) {
            final Object item = iterator.next();
            if (item != null) {
                result.add(clone(item));
            }
            else {
                result.add(null);
            }
        }
        return result;
    }

    
    public synchronized static void setClassLoader(
            final ClassLoader classLoader) {
        ObjectUtilities.classLoader = classLoader;
    }

    
    public static ClassLoader getClassLoader() {
      return classLoader;
    }

    
    public synchronized static ClassLoader getClassLoader(final Class c) {
        if (classLoader != null) {
            return classLoader;
        }
        if ("ThreadContext".equals(classLoaderSource)) {
            final ClassLoader threadLoader
                = Thread.currentThread().getContextClassLoader();
            if (threadLoader != null) {
                return threadLoader;
            }
        }

        
        final ClassLoader applicationCL = c.getClassLoader();
        if (applicationCL == null) {
            return ClassLoader.getSystemClassLoader();
        }
        else {
            return applicationCL;
        }
    }


    
    public static URL getResource(final String name, final Class c) {
        final ClassLoader cl = getClassLoader(c);
        if (cl == null) {
            return null;
        }
        return cl.getResource(name);
    }

    
    public static URL getResourceRelative(final String name, final Class c) {
        final ClassLoader cl = getClassLoader(c);
        final String cname = convertName(name, c);
        if (cl == null) {
            return null;
        }
        return cl.getResource(cname);
    }

    
    private static String convertName(final String name, Class c) {
        if (name.startsWith("/")) {
            
            return name.substring(1);
        }

        
        while (c.isArray()) {
            c = c.getComponentType();
        }
        
        final String baseName = c.getName();
        final int index = baseName.lastIndexOf('.');
        if (index == -1) {
            return name;
        }

        final String pkgName = baseName.substring(0, index);
        return pkgName.replace('.', '/') + "/" + name;
    }

    
    public static InputStream getResourceAsStream(final String name,
                                                  final Class context) {
        final URL url = getResource(name, context);
        if (url == null) {
            return null;
        }

        try {
            return url.openStream();
        }
        catch (IOException e) {
            return null;
        }
    }

    
    public static InputStream getResourceRelativeAsStream
        (final String name, final Class context) {
        final URL url = getResourceRelative(name, context);
        if (url == null) {
            return null;
        }

        try {
            return url.openStream();
        }
        catch (IOException e) {
            return null;
        }
    }

    
    public static Object loadAndInstantiate(final String className,
                                            final Class source) {
        try {
            final ClassLoader loader = getClassLoader(source);
            final Class c = loader.loadClass(className);
            return c.newInstance();
        }
        catch (Exception e) {
            return null;
        }
    }

    
    public static Object loadAndInstantiate(String className,
                                            Class source,
                                            Class type) {
        try {
            ClassLoader loader = getClassLoader(source);
            Class c = loader.loadClass(className);
            if (type.isAssignableFrom(c)) {
                return c.newInstance();
            }
        }
        catch (Exception e) {
            return null;
        }
        return null;
    }

    
    public static boolean isJDK14() {
        ClassLoader loader = getClassLoader(ObjectUtilities.class);
        if (loader != null) {
            try {
              loader.loadClass("java.util.RandomAccess");
              return true;
            }
            catch (ClassNotFoundException e) {
              return false;
            }
            catch(Exception e) {
              
            }
        }
        
        
        try {
            String version = System.getProperty(
                    "java.vm.specification.version");
            
            if (version == null) {
                return false;
            }

            String[] versions = parseVersions(version);
            String[] target = new String[]{ "1", "4" };
            return (ArrayUtilities.compareVersionArrays(versions, target) >= 0);
        }
        catch(Exception e) {
            return false;
        }
    }

    private static String[] parseVersions (String version) {
      if (version == null) {
        return new String[0];
      }

      ArrayList versions = new ArrayList();
      StringTokenizer strtok = new StringTokenizer(version, ".");
      while (strtok.hasMoreTokens()) {
          versions.add (strtok.nextToken());
      }
      return (String[]) versions.toArray(new String[versions.size()]);
    }
}
