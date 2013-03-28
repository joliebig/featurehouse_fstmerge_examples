

package edu.rice.cs.drjava.model;

import java.util.List;
import java.util.ArrayList;
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
  
  protected static String adapterForCompiler(JavaVersion.FullVersion version) {
    switch (version.majorVersion()) {
      case JAVA_6: {
        switch (version.vendor()) {
          case OPENJDK: return "edu.rice.cs.drjava.model.compiler.Javac160OpenJDKCompiler";
          case MINT: return "edu.rice.cs.drjava.model.compiler.MintCompiler";
          case HJ: return "edu.rice.cs.drjava.model.compiler.HjCompiler";
          default: return "edu.rice.cs.drjava.model.compiler.Javac160Compiler";
        }
      }
      case JAVA_5: return "edu.rice.cs.drjava.model.compiler.Javac150Compiler";
      default: return null;
    }
  }
  
  protected static String adapterForDebugger(JavaVersion.FullVersion version) {
    switch (version.majorVersion()) {
      case JAVA_6: return "edu.rice.cs.drjava.model.debug.jpda.JPDADebugger";
      case JAVA_5: return "edu.rice.cs.drjava.model.debug.jpda.JPDADebugger";
      default: return null;
    }
  }

  protected static CompilerInterface getCompilerInterface(String className, FullVersion version) {
    if (className != null) {
      List<File> bootClassPath = null;
      String bootProp = System.getProperty("sun.boot.class.path");
      if (bootProp != null) { bootClassPath = CollectUtil.makeList(IOUtil.parsePath(bootProp)); }
      try {
        Class<?>[] sig = { FullVersion.class, String.class, List.class };
        Object[] args = { version, "the runtime class path", bootClassPath };
        CompilerInterface attempt = (CompilerInterface) ReflectUtil.loadObject(className, sig, args);
        msg("                 attempt = "+attempt+", isAvailable() = "+attempt.isAvailable());
        if (attempt.isAvailable()) { return attempt; }
      }
      catch (ReflectException e) {  }
      catch (LinkageError e) {  }
    }
    return NoCompilerAvailable.ONLY;
  }
  
  
  public static Iterable<JDKToolsLibrary> makeFromRuntime(GlobalModel model) {
    FullVersion version = JavaVersion.CURRENT_FULL;

    String compilerAdapter = adapterForCompiler(version);
    msg("makeFromRuntime: compilerAdapter="+compilerAdapter);
    CompilerInterface compiler = getCompilerInterface(compilerAdapter, version);
    msg("                 compiler="+compiler.getClass().getName());
    
    Debugger debugger = NoDebuggerAvailable.ONLY;
    String debuggerAdapter = adapterForDebugger(version);
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

    List<JDKToolsLibrary> list = new ArrayList<JDKToolsLibrary>();
    
    if (compiler!=NoCompilerAvailable.ONLY) {
      
      msg("                 compiler found");
      list.add(new JDKToolsLibrary(version, compiler, debugger, javadoc));
    }
      
    if (JavaVersion.JAVA_6.compareTo(version.majorVersion())>=0) {
      
      msg("                 at least Java 6, try EclipseCompiler");
      
      FullVersion eclipseVersion = JavaVersion.parseFullVersion(JavaVersion.JAVA_6.fullVersion().versionString(),
                                                                "Eclipse","");
      msg("                 version for Eclipse: "+eclipseVersion);
      compiler = getCompilerInterface("edu.rice.cs.drjava.model.compiler.EclipseCompiler", eclipseVersion);
      msg("                 compiler="+compiler.getClass().getName());
      if (compiler!=NoCompilerAvailable.ONLY) {
        
        msg("                 compiler found");
        list.add(new JDKToolsLibrary(eclipseVersion, compiler, debugger, javadoc));
      }
    }
    msg("                 compilers found: "+list.size());
    
    if (list.size()==0) {
      
      msg("                 no compilers found, adding NoCompilerAvailable library");
      list.add(new JDKToolsLibrary(version, NoCompilerAvailable.ONLY, debugger, javadoc));
    }
    
    return list;
  }  
  
  public static final java.io.StringWriter LOG_STRINGWRITER = new java.io.StringWriter();
  protected static final java.io.PrintWriter LOG_PW = new java.io.PrintWriter(LOG_STRINGWRITER);
  
  public static void msg(String s) {   




      LOG_PW.println(s);



  }
}
