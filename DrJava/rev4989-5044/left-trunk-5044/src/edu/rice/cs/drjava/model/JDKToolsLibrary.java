

package edu.rice.cs.drjava.model;

import java.util.List;
import java.io.File;

import edu.rice.cs.plt.reflect.ReflectUtil;
import edu.rice.cs.plt.reflect.JavaVersion;
import edu.rice.cs.plt.reflect.JavaVersion.FullVersion;
import edu.rice.cs.plt.reflect.ReflectException;
import edu.rice.cs.plt.io.IOUtil;
import edu.rice.cs.plt.collect.CollectUtil;

import edu.rice.cs.drjava.model.compiler.CompilerInterface;
import edu.rice.cs.drjava.model.compiler.NoCompilerAvailable;
import edu.rice.cs.drjava.model.debug.Debugger;
import edu.rice.cs.drjava.model.debug.NoDebuggerAvailable;
import edu.rice.cs.drjava.model.javadoc.JavadocModel;
import edu.rice.cs.drjava.model.javadoc.NoJavadocAvailable;
import edu.rice.cs.drjava.model.javadoc.DefaultJavadocModel;


public class JDKToolsLibrary {
  
  private final FullVersion _version;
  private final CompilerInterface _compiler;
  private final Debugger _debugger;
  private final JavadocModel _javadoc;
  
  protected JDKToolsLibrary(FullVersion version, CompilerInterface compiler, Debugger debugger,
                            JavadocModel javadoc) {
    _version = version;
    _compiler = compiler;
    _debugger = debugger;
    _javadoc = javadoc;
  }
  
  public FullVersion version() { return _version; }
  
  public CompilerInterface compiler() { return _compiler; }
  
  public Debugger debugger() { return _debugger; }
  
  public JavadocModel javadoc() { return _javadoc; }
  
  public boolean isValid() {
    return _compiler.isAvailable() || _debugger.isAvailable() || _javadoc.isAvailable();
  }
  
  public String toString() { return "JDK library " + _version.versionString(); }
  
  protected static String adapterForCompiler(JavaVersion version) {
    switch (version) {
      case JAVA_6: return "edu.rice.cs.drjava.model.compiler.Javac160Compiler";
      case JAVA_5: return "edu.rice.cs.drjava.model.compiler.Javac150Compiler";
      case JAVA_1_4: return "edu.rice.cs.drjava.model.compiler.Javac141Compiler";
      default: return null;
    }
  }
  
  protected static String adapterForDebugger(JavaVersion version) {
    switch (version) {
      case JAVA_6: return "edu.rice.cs.drjava.model.debug.jpda.JPDADebugger";
      case JAVA_5: return "edu.rice.cs.drjava.model.debug.jpda.JPDADebugger";
      case JAVA_1_4: return "edu.rice.cs.drjava.model.debug.jpda.JPDADebugger";
      default: return null;
    }
  }
  
  
  public static JDKToolsLibrary makeFromRuntime(GlobalModel model) {
    FullVersion version = JavaVersion.CURRENT_FULL;

    CompilerInterface compiler = NoCompilerAvailable.ONLY;
    String compilerAdapter = adapterForCompiler(version.majorVersion());
    if (compilerAdapter != null) {
      List<File> bootClassPath = null;
      String bootProp = System.getProperty("sun.boot.class.path");
      if (bootProp != null) { bootClassPath = CollectUtil.makeList(IOUtil.parsePath(bootProp)); }
      try {
        Class<?>[] sig = { FullVersion.class, String.class, List.class };
        Object[] args = { version, "the runtime class path", bootClassPath };
        CompilerInterface attempt = (CompilerInterface) ReflectUtil.loadObject(compilerAdapter, sig, args);
        if (attempt.isAvailable()) { compiler = attempt; }
      }
      catch (ReflectException e) {  }
      catch (LinkageError e) {  }
    }
    
    Debugger debugger = NoDebuggerAvailable.ONLY;
    String debuggerAdapter = adapterForDebugger(version.majorVersion());
    if (debuggerAdapter != null) {
      try {
        Debugger attempt = (Debugger) ReflectUtil.loadObject(debuggerAdapter, new Class<?>[]{GlobalModel.class}, model);
        if (attempt.isAvailable()) { debugger = attempt; }
      }
      catch (ReflectException e) {  }
      catch (LinkageError e) {  }
    }
    
    JavadocModel javadoc = new NoJavadocAvailable(model);
    try {
      Class.forName("com.sun.tools.javadoc.Main");
      javadoc = new DefaultJavadocModel(model, null, ReflectUtil.SYSTEM_CLASS_PATH);
    }
    catch (ClassNotFoundException e) {  }
    catch (LinkageError e) {  }
    
    return new JDKToolsLibrary(version, compiler, debugger, javadoc);
  }
  
}
