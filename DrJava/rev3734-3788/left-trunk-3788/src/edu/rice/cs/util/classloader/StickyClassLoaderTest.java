

package edu.rice.cs.util.classloader;

import edu.rice.cs.drjava.DrJavaTestCase;

import java.net.URL;
import java.security.SecureClassLoader;


public class StickyClassLoaderTest extends DrJavaTestCase {
  private final String myName = getClass().getName();
  private final ClassLoader myLoader = getClass().getClassLoader();

  
  public void testLoaderSticks() throws Throwable {
    StickyClassLoader loader = new StickyClassLoader(myLoader, myLoader);

    Class c = loader.loadClass(myName);
    assertEquals("getClassLoader()", loader, c.getClassLoader());
    assertEquals("getName()", myName, c.getName());
  }

  
  public void testLoaderUsesSystemForJavaClasses() throws Throwable {
    StickyClassLoader loader = new StickyClassLoader(myLoader, myLoader);

    Class c = loader.loadClass("java.lang.Object");
    assertEquals("java.lang.Object", c.getName());
  }

  
  public void testLoaderFindsNonSystemJavaClasses() throws Throwable {
    class LoadingClassException extends RuntimeException { }

    ClassLoader testLoader = new SecureClassLoader() {
      public URL getResource(String name) {
        throw new LoadingClassException();
      }
    };
    StickyClassLoader loader = new StickyClassLoader(myLoader, testLoader);

    try {
      loader.loadClass("javax.mail.FakeClass");
      
      fail("FakeClass should not exist.");
    }
    catch (LoadingClassException lce) {
      
    }
    
    
  }

  
  public void testLoaderRespectsOldList() throws Throwable {
    StickyClassLoader loader = new StickyClassLoader(myLoader,
                                                     myLoader,
                                                     new String[] { myName });

    Class c = loader.loadClass(myName);
    assertEquals("getClassLoader()", myLoader, c.getClassLoader());
    assertEquals("getName()", myName, c.getName());
  }

  
  public void testLoaderSticksTransitively() throws Throwable {
    String[] names = { myName + "$BMaker" };

    StickyClassLoader loader = new StickyClassLoader(myLoader, myLoader, names);
    Class c = loader.loadClass(myName + "$A");
    assertEquals("getClassLoader()", loader, c.getClassLoader());

    Object aObj = c.newInstance();
    BMaker aCasted = (BMaker) aObj;

    Object b = aCasted.makeB();

    assertEquals("getClass().getName()",
                 myName + "$A$B",
                 b.getClass().getName());

    assertEquals("getClass().getClassLoader()",
                 loader,
                 b.getClass().getClassLoader());
  }

  
  public void testDoesntLoadSameClassTwice() throws Throwable {
    StickyClassLoader loader = new StickyClassLoader(myLoader, myLoader);
    loader.loadClass(myName + "$Two");
    loader.loadClass(myName + "$One");
  }

  public static class One { }
  public static class Two extends One { }

  public interface BMaker {
    public Object makeB();
  }

  public static class A implements BMaker {
    private static class B { }

    public Object makeB() { return new B(); }
  }
}
