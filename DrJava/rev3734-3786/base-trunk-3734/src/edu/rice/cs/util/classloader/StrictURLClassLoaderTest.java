

package edu.rice.cs.util.classloader;

import edu.rice.cs.drjava.DrJavaTestCase;

import java.net.URL;


public class StrictURLClassLoaderTest extends DrJavaTestCase {
  
  public void testWontLoadFromSystem() throws Throwable {
    StrictURLClassLoader loader = new StrictURLClassLoader(new URL[0]);
    String myName = getClass().getName();

    try {
      loader.loadClass(myName);
      fail("should not have loaded class");
    }
    catch (ClassNotFoundException e) {
      
    }
  }

  
  public void testWontLoadResourceFromBootClassPath() throws Throwable {
    StrictURLClassLoader loader = new StrictURLClassLoader(new URL[0]);
    String compiler = "com/sun/tools/javac/util/Log.class";

    URL resource = loader.getResource(compiler);
    assertTrue("should not have found resource", resource == null);
  }

  
  public void testWillLoadClassFromGivenURLs() throws Throwable {
    String logResource = "com/sun/tools/javac/Main.class";
    String compilerClass = "com.sun.tools.javac.Main";
    URL[] urls = ToolsJarClassLoader.getToolsJarURLs();

    if (urls.length > 0) {
      
      StrictURLClassLoader loader = new StrictURLClassLoader(urls);

      Class c = loader.loadClass(compilerClass);
      assertEquals("loaded class", compilerClass, c.getName());

    
      URL resource = loader.getResource(logResource);
      assertTrue("resource found", resource != null);
    }
  }
}
