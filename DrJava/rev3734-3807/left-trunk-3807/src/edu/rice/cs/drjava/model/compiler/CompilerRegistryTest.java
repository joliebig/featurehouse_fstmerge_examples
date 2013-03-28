

package edu.rice.cs.drjava.model.compiler;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.DrJavaTestCase;
import edu.rice.cs.util.ClassPathVector;
import edu.rice.cs.util.classloader.LimitingClassLoader;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.io.File;


public final class CompilerRegistryTest extends DrJavaTestCase {
  private static final CompilerRegistry _registry = CompilerRegistry.ONLY;
  private static final String[][] _defaultCompilers = CompilerRegistry.DEFAULT_COMPILERS;

  private static final CompilerInterface[] _allAvailableCompilers = _registry.getAvailableCompilers();

  
  private ClassLoader _oldBaseLoader;

  
  public CompilerRegistryTest(String name) { super(name); }

  
  public static Test suite() { return new TestSuite(CompilerRegistryTest.class); }

  
  public void setUp() throws Exception {
    super.setUp();
    _oldBaseLoader = _registry.getBaseClassLoader();
    _registry.setActiveCompiler(NoCompilerAvailable.ONLY);
  }

  public void tearDown() throws Exception {
    _registry.setBaseClassLoader(_oldBaseLoader);
    super.tearDown();
  }

























  
  public void testLimitOneByOne() {
    for (int i = 0; i < _allAvailableCompilers.length; i++) {
      
      _getCompilersAfterDisablingOne(i);
      
    }
  }

  
  public void testLimitAllAtOnce() {
    LimitingClassLoader loader = new LimitingClassLoader(_oldBaseLoader);
    _registry.setBaseClassLoader(loader);

    for (int i = 0; i < _defaultCompilers.length; i++) {
      for (int j = 0; j < _defaultCompilers[i].length; j++) {
        loader.addToRestrictedList(_defaultCompilers[i][j]);
      }
    }

    CompilerInterface[] compilers = _registry.getAvailableCompilers();
    assertEquals("Number of available compilers should be 1 because all real compilers are restricted.", 1,
                 compilers.length);

    assertEquals("Only available compiler should be NoCompilerAvailable.ONLY", NoCompilerAvailable.ONLY,
                 compilers[0]);

    assertEquals("Active compiler",  NoCompilerAvailable.ONLY, _registry.getActiveCompiler());
    
       

  }
  
  
  public void testAvailableCompilerSeenByDrJava() {
    assertEquals("DrJava.java should agree with CompilerRegistry",
                 _registry.getActiveCompiler() != NoCompilerAvailable.ONLY,
                 DrJava.hasAvailableCompiler());
  }

  
  public void testActiveCompilerAllAvailable() {
    CompilerInterface[] compilers = _registry.getAvailableCompilers();

    assertEquals("active compiler before any setActive",
                 compilers[0],
                 _registry.getActiveCompiler());

    for (int i = 0; i < compilers.length; i++) {
      
      
        _registry.setActiveCompiler(compilers[i]);
        assertEquals("active compiler after setActive",
                     compilers[i],
                     _registry.getActiveCompiler());
      
    }
  }

  
  private CompilerInterface[] _getCompilersAfterDisablingOne(int i) {
    return _getCompilersAfterDisablingSome(new int[] { i });
  }

  
  private CompilerInterface[] _getCompilersAfterDisablingSome(int[] indices) {
    LimitingClassLoader loader = new LimitingClassLoader(_oldBaseLoader);
    _registry.setBaseClassLoader(loader);
    
    
    

    for (int i = 0; i < indices.length; i++) {
      
      loader.addToRestrictedList(_allAvailableCompilers[indices[i]].getClass().getName());
    }

    CompilerInterface[] compilers = _registry.getAvailableCompilers();
    
    
    
    
    
    




    int indicesIndex = 0;

    for (int j = 0; j < _allAvailableCompilers.length; j++) {
      if ((indicesIndex < indices.length) && (j == indices[indicesIndex])) {
        
        indicesIndex++;
        continue;
      }

      
      int indexInAvailable = j - indicesIndex;

      assertEquals("Class of available compiler #" + indexInAvailable,
                   _allAvailableCompilers[j].getClass().getName(),
                   compilers[indexInAvailable].getClass().getName());
    }

    return compilers;
  }

  
  public void testCannotSetCompilerToNull() {
    try {
      _registry.setActiveCompiler(null);
      fail("Setting active compiler to null should have caused an exception!");
    }
    catch (IllegalArgumentException e) {
      
    }
  }

  static class Without implements CompilerInterface {
    public boolean testField = false;
    public Without()
    {
      testField = true;
    }

     public void addToBootClassPath(File s) { }
     public CompilerError[] compile(File[] sourceRoots, File[] files) { return null; }
     public CompilerError[] compile(File sourceRoot, File[] files) { return null; }
     public String getName() { return "Without"; }
     public boolean isAvailable() { return false; }
     public void setAllowAssertions(boolean allow) { }
     public void setWarningsEnabled(boolean warningsEnabled) { }
     public void setExtraClassPath(String extraClassPath) { }
     public void setExtraClassPath(ClassPathVector extraClassPath) { }
     public String toString() { return "Without"; }
     public void setBuildDirectory(File builddir) { }
  }

  
   public void testCreateCompiler() {
     try { _registry.createCompiler(Without.class); }
     catch(Throwable e) {
       e.printStackTrace();
       fail("testCreateCompiler: Unexpected Exception for class without ONLY field\n" + e);
     }

     try { _registry.createCompiler(JavacFromClassPath.ONLY.getClass()); 
           _registry.createCompiler(JavacFromToolsJar.ONLY.getClass());
     } 
     catch(Throwable e2) {
        fail("testCreateCompiler: Unexpected Exception for class with ONLY field\n" + e2);
     }
  }
}
