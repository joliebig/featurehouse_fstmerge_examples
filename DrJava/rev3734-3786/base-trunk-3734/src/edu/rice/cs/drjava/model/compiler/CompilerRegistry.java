

package edu.rice.cs.drjava.model.compiler;

import java.util.LinkedList;
import java.lang.reflect.Field;
import edu.rice.cs.util.swing.Utilities;
import edu.rice.cs.util.UnexpectedException;


public class CompilerRegistry {
  
  
  
  public static final String[] JAVA_16_COMPILERS = {
    
    "edu.rice.cs.drjava.model.compiler.Javac160FromSetLocation",
    "edu.rice.cs.drjava.model.compiler.Javac160FromClasspath",
    "edu.rice.cs.drjava.model.compiler.Javac160FromToolsJar"
  };
  
  public static final String[] JAVA_15_COMPILERS = {
    
    "edu.rice.cs.drjava.model.compiler.Javac150FromSetLocation",
    "edu.rice.cs.drjava.model.compiler.Javac150FromClasspath",
    "edu.rice.cs.drjava.model.compiler.Javac150FromToolsJar"
  };

  
  public static final String[] JAVA_14_COMPILERS = {
    
    "edu.rice.cs.drjava.model.compiler.Javac141FromSetLocation",
    "edu.rice.cs.drjava.model.compiler.Javac141FromClasspath",
    "edu.rice.cs.drjava.model.compiler.Javac141FromToolsJar"
  };

  
  static final String[][] DEFAULT_COMPILERS = {
    
    JAVA_16_COMPILERS,
    
    JAVA_15_COMPILERS,
    
    JAVA_14_COMPILERS
  };
    
  
  public static final CompilerRegistry ONLY = new CompilerRegistry();

  
  private ClassLoader _baseClassLoader;

  
  private CompilerInterface _activeCompiler = NoCompilerAvailable.ONLY;

  
  private CompilerRegistry() { _baseClassLoader = getClass().getClassLoader(); }

  
  public void setBaseClassLoader(ClassLoader l) { _baseClassLoader = l; }

  
  public ClassLoader getBaseClassLoader() { return _baseClassLoader; }

  
  public CompilerInterface[] getAvailableCompilers() {
    LinkedList<CompilerInterface> availableCompilers = new LinkedList<CompilerInterface>();
    
    String[] candidateCompilers = null;
    
    String version = CompilerProxy.VERSION; 
    
    if (version.equals("1.4")) candidateCompilers = JAVA_14_COMPILERS;
    else if (version.equals("1.5")) candidateCompilers = JAVA_15_COMPILERS;
    else if (version.equals("1.6")) candidateCompilers = JAVA_16_COMPILERS;
    else throw new 
      UnexpectedException("Java specification version " + version + "is not supported.  Must be 1.4, 1.5, or 1.6");

    for (String name : candidateCompilers) {

      try { if (_createCompiler(name, availableCompilers)) break; }
      catch (Throwable t) {
        

        
        
      }
    }

    if (availableCompilers.size() == 0) availableCompilers.add(NoCompilerAvailable.ONLY);
    
    return availableCompilers.toArray(new CompilerInterface[availableCompilers.size()]);
  }

  private boolean _createCompiler(String name, LinkedList<CompilerInterface> availableCompilers) throws Throwable {
    CompilerInterface compiler = _instantiateCompiler(name);
    if (compiler.isAvailable()) {
      

      
      
      if (_activeCompiler == NoCompilerAvailable.ONLY) {
        
        _activeCompiler = compiler;
      }

      availableCompilers.add(compiler);
      return true;
    }
    else return false;
      
  }

  public boolean isNoCompilerAvailable() { return getActiveCompiler() == NoCompilerAvailable.ONLY; }

  
  public void setActiveCompiler(CompilerInterface compiler) {
    if (compiler == null) {
      
      throw new IllegalArgumentException("Cannot set active compiler to null.");
    }
    else _activeCompiler = compiler;
  }

  
  public CompilerInterface getActiveCompiler() {
    
    if (_activeCompiler == NoCompilerAvailable.ONLY) getAvailableCompilers();

    

    if (_activeCompiler.isAvailable()) return _activeCompiler;
    return NoCompilerAvailable.ONLY;
  }

  
  private CompilerInterface _instantiateCompiler(String name) throws Throwable {
    Class<?> clazz = _baseClassLoader.loadClass(name);

    return createCompiler(clazz);
  }

  public static CompilerInterface createCompiler(Class clazz) throws Throwable {
    try {
      Field field = clazz.getField("ONLY");
      Object val = field.get(null);  
      return (CompilerInterface) val;
    }
    catch (Throwable t) {
      
      return (CompilerInterface) clazz.newInstance();
    }
  }
}
