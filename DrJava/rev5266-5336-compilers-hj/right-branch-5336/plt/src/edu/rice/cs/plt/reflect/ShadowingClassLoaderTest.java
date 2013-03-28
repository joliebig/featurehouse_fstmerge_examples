

package edu.rice.cs.plt.reflect;

import edu.rice.cs.plt.iter.IterUtil;

import static edu.rice.cs.plt.debug.DebugUtil.debug;

public class ShadowingClassLoaderTest extends ClassLoaderTestCase {
  
  private static final ClassLoader BASE_LOADER = ShadowingClassLoaderTest.class.getClassLoader();
  
  public void testShadowedClassLoading() throws ClassNotFoundException {
    debug.logStart();
    
    
    ShadowingClassLoader l = ShadowingClassLoader.blackList(BASE_LOADER, "edu.rice.cs.plt.reflect");
    assertLoadsSameClass(BASE_LOADER, l, "edu.rice.cs.plt.iter.IterUtil");
    assertLoadsClass(BASE_LOADER, "edu.rice.cs.plt.reflect.ReflectUtil");
    assertDoesNotLoadClass(l, "edu.rice.cs.plt.reflect.ReflectUtil");
    
    
    ShadowingClassLoader l2 = ShadowingClassLoader.blackList(BASE_LOADER, "edu.rice.cs.plt.refl");
    assertLoadsSameClass(BASE_LOADER, l2, "edu.rice.cs.plt.iter.IterUtil");
    assertLoadsSameClass(BASE_LOADER, l2, "edu.rice.cs.plt.reflect.ReflectUtil");
    
    
    ShadowingClassLoader l3 = ShadowingClassLoader.whiteList(BASE_LOADER, "edu.rice.cs.plt.reflect");
    assertLoadsClass(BASE_LOADER, "edu.rice.cs.plt.iter.IterUtil");
    assertDoesNotLoadClass(l3, "edu.rice.cs.plt.iter.IterUtil");
    assertLoadsSameClass(BASE_LOADER, l3, "edu.rice.cs.plt.reflect.ReflectUtil");
    
    
    ShadowingClassLoader l4 = ShadowingClassLoader.blackList(BASE_LOADER, "javax", "edu");
    assertLoadsSameClass(BASE_LOADER, l4, "java.lang.Number");
    assertLoadsSameClass(BASE_LOADER, l4, "javax.swing.JFrame");
    assertLoadsClass(BASE_LOADER, "edu.rice.cs.plt.reflect.ReflectUtil");
    assertDoesNotLoadClass(l4, "edu.rice.cs.plt.reflect.ReflectUtil");
    
    
    ShadowingClassLoader l5 = ShadowingClassLoader.whiteList(BASE_LOADER, "javax", "edu.rice.cs.plt.reflect");
    assertLoadsSameClass(BASE_LOADER, l5, "javax.swing.JFrame");
    assertLoadsSameClass(BASE_LOADER, l5, "edu.rice.cs.plt.reflect.ReflectUtil");
    assertLoadsClass(BASE_LOADER, "edu.rice.cs.plt.iter.IterUtil");
    assertDoesNotLoadClass(l5, "edu.rice.cs.plt.iter.IterUtil");
    
    
    ShadowingClassLoader l6 =
    new ShadowingClassLoader(BASE_LOADER, true, IterUtil.make("javax", "edu"), true);
    assertLoadsSameClass(BASE_LOADER, l6, "java.lang.Number");
    assertLoadsClass(BASE_LOADER, "javax.swing.JFrame");
    assertDoesNotLoadClass(l6, "javax.swing.JFrame");
    assertLoadsClass(BASE_LOADER, "edu.rice.cs.plt.reflect.ReflectUtil");
    assertDoesNotLoadClass(l4, "edu.rice.cs.plt.reflect.ReflectUtil");
    
    debug.logEnd();
  }
  
  public void testResourceLoading() {
    debug.logStart();
    
    ShadowingClassLoader l = ShadowingClassLoader.blackList(BASE_LOADER, "edu.rice.cs.plt.reflect");
    assertHasSameResource(BASE_LOADER, l, "edu/rice/cs/plt/iter/IterUtil.class");
    assertHasResource(BASE_LOADER, "edu/rice/cs/plt/reflect/ShadowingClassLoaderTest.class");
    assertDoesNotHaveResource(l, "edu/rice/cs/plt/reflect/ShadowingClassLoaderTest.class");
    
    debug.logEnd();
  }
  
}
