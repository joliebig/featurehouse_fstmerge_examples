

package edu.rice.cs.drjava.model.compiler;

import java.io.File;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.util.classloader.StickyClassLoader;
import edu.rice.cs.util.ClassPathVector;
import edu.rice.cs.util.swing.Utilities;
import edu.rice.cs.drjava.config.OptionConstants;
import edu.rice.cs.drjava.config.FileOption;


public class CompilerProxy implements CompilerInterface {
  
  public static final String VERSION = System.getProperty("java.specification.version");
  
  
  private CompilerInterface _realCompiler = null;

  private final String _className;
  private final ClassLoader _newLoader;
  private boolean _warningsEnabled = true;
  private File _buildDir;
  private ClassPathVector _extraClassPath = new ClassPathVector();
  
  
  private static final String[] _useOldLoader = {
    "edu.rice.cs.drjava.model.Configuration",
    "edu.rice.cs.drjava.model.compiler.CompilerInterface",
    "edu.rice.cs.drjava.model.compiler.CompilerError"
  };

  

  public CompilerProxy(String className, ClassLoader newLoader) {
    _className = className;
    _newLoader = newLoader;
    _recreateCompiler();
  }

  private void _recreateCompiler() {
    
    StickyClassLoader loader = new StickyClassLoader(_newLoader, getClass().getClassLoader(), _useOldLoader);
    
    try {
      Class<?> c = loader.loadClass(_className);

      _realCompiler = CompilerRegistry.createCompiler(c);
      
      _realCompiler.setBuildDirectory(_buildDir);
      
      _realCompiler.setExtraClassPath(File.pathSeparator + _extraClassPath.toString());
      
      _realCompiler.setWarningsEnabled(_warningsEnabled);
      
      boolean allowAssertions = DrJava.getConfig().getSetting(OptionConstants.RUN_WITH_ASSERT).booleanValue();
      _realCompiler.setAllowAssertions(allowAssertions);
      
      String compilerClass = _realCompiler.getClass().getName();

    }
    catch (Throwable t) {   }
    
  }


  
  public CompilerError[] compile(File sourceRoot, File[] files) {
    _recreateCompiler();

    CompilerError[] ret =  _realCompiler.compile(sourceRoot, files);

    return ret;
  }

  
  public CompilerError[] compile(File[] sourceRoots, File[] files) {







    _recreateCompiler();

    CompilerError[] ret =  _realCompiler.compile(sourceRoots, files);

    return ret;
  }

  
  public boolean isAvailable() {

    if (_realCompiler == null) return false;
    else return _realCompiler.isAvailable();
  }

  
  public String getName() {
    if (! isAvailable())  return "(unavailable)";
    return _realCompiler.getName();
  }

  
  public String toString() { return getName(); }

  
  public void setExtraClassPath( String extraClassPath) { _realCompiler.setExtraClassPath(extraClassPath); }
  
  
  public void setExtraClassPath(ClassPathVector extraClassPath) { _extraClassPath = extraClassPath; }

  
  public void setAllowAssertions(boolean allow) { _realCompiler.setAllowAssertions(allow); }
  
  
  public void setWarningsEnabled(boolean warningsEnabled) {
    _realCompiler.setWarningsEnabled(warningsEnabled);
    _warningsEnabled = warningsEnabled;
  }

  
  public void addToBootClassPath( File cp) { _realCompiler.addToBootClassPath(cp); }

  
  public void setBuildDirectory(File buildDir) {
    _realCompiler.setBuildDirectory(buildDir);
    _buildDir = buildDir;
  }
}



