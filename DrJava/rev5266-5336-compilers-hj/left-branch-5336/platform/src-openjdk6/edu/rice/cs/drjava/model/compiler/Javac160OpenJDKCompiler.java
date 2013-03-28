

package edu.rice.cs.drjava.model.compiler;

import java.io.File;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.IOException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.tools.JavaFileObject;
import javax.tools.JavaCompiler;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import edu.rice.cs.drjava.model.DJError;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.plt.reflect.JavaVersion;
import edu.rice.cs.plt.io.IOUtil;

import static edu.rice.cs.plt.debug.DebugUtil.debug;
import static edu.rice.cs.plt.debug.DebugUtil.error;


public class Javac160OpenJDKCompiler extends JavacCompiler {
  public static final String COMPILER_CLASS_NAME = "com.sun.tools.javac.main.JavaCompiler";
  
  
  private static final PrintWriter NULL_WRITER = new PrintWriter(new Writer() {
    public void write(char cbuf[], int off, int len) throws IOException {}
    public void flush() throws IOException {}
    public void close() throws IOException {}
  });

  public Javac160OpenJDKCompiler(JavaVersion.FullVersion version, String location, List<? extends File> defaultBootClassPath) {
    super(version, location, defaultBootClassPath);
  }
  
  public boolean isAvailable() {
    try {
      Class.forName("com.sun.tools.javac.main.JavaCompiler");
      try { Class.forName("java.lang.Enum"); }
      catch (Exception e) {
        
        
        
        Class.forName("com.sun.tools.javac.main.Main$14");
      }
      return _isValidVersion();
    }
    catch (Exception e) { return false; }
    catch (LinkageError e) { return false; }
  }
  
  
  @SuppressWarnings("unchecked")
  private boolean _isValidVersion() {
    
    Class log = com.sun.tools.javac.util.Log.class;

    
    Class[] validArgs1 = { com.sun.tools.javac.util.Context.class };
    
    try { 
      
      log.getMethod("instance", validArgs1);  
      return true;
    }
    catch (NoSuchMethodException e) {
      return false;  
    }
  }
  
  
  public List<? extends DJError> compile(List<? extends File> files, List<? extends File> classPath, 
                                               List<? extends File> sourcePath, File destination, 
                                               List<? extends File> bootClassPath, String sourceVersion, boolean showWarnings) {
    debug.logStart("compile()");
    debug.logValues(new String[]{ "this", "files", "classPath", "sourcePath", "destination", "bootClassPath", 
                                  "sourceVersion", "showWarnings" },
                    this, files, classPath, sourcePath, destination, bootClassPath, sourceVersion, showWarnings);

    Iterable<String> options = _createOptions(classPath, sourcePath, destination, bootClassPath, sourceVersion, showWarnings);
    LinkedList<DJError> errors = new LinkedList<DJError>();
    
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    
    
    DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
    StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);    
    Iterable<? extends JavaFileObject> fileObjects = fileManager.getJavaFileObjectsFromFiles(files);
    
    try {
      compiler.getTask(null, fileManager, diagnostics, options, null, fileObjects).call();
      for (Diagnostic<? extends JavaFileObject> d: diagnostics.getDiagnostics()) {
        Diagnostic.Kind dt = d.getKind();
        boolean isWarning = false;  
        
        switch (dt) {
          case OTHER:             continue; 
          case NOTE:              continue; 
          case MANDATORY_WARNING: isWarning = true; break;
          case WARNING:           isWarning = true; break;
          case ERROR:             isWarning = false; break;
        }
        
        
        if (d.getSource()!=null) {
          errors.add(new DJError(new File(d.getSource().toUri().getPath()), 
                                 ((int) d.getLineNumber()) - 1,  
                                 ((int) d.getColumnNumber()) - 1, 
                                 d.getMessage(null),    
                                 isWarning));
        }
        else {
          errors.add(new DJError(d.getMessage(null), isWarning));
        }
      }
      fileManager.close();
    }
    catch(Throwable t) {  
      errors.addFirst(new DJError("Compile exception: " + t, false));
      error.log(t);
    }
    
    debug.logEnd("compile()");
    return errors;
  }
  
  public String getName() {
    return super.getName();
  }
  
  private Iterable<String> _createOptions(List<? extends File> classPath, List<? extends File> sourcePath, File destination, 
                                          List<? extends File> bootClassPath, String sourceVersion, boolean showWarnings) {    
    if (bootClassPath == null) { bootClassPath = _defaultBootClassPath; }

    LinkedList<String> options = new LinkedList<String>();
    for (Map.Entry<String, String> e : CompilerOptions.getOptions(showWarnings).entrySet()) {
      options.add(e.getKey());
      if (e.getValue().length()>0) options.add(e.getValue());
    }
    options.add("-g");

    if (classPath != null) { options.add("-classpath"); options.add(IOUtil.pathToString(classPath)); }
    if (sourcePath != null) { options.add("-sourcepath"); options.add(IOUtil.pathToString(sourcePath)); }
    if (destination != null) { options.add("-d"); options.add(destination.getPath()); }
    if (bootClassPath != null) { options.add("-bootclasspath"); options.add(IOUtil.pathToString(bootClassPath)); }
    if (sourceVersion != null) { options.add("-source"); options.add(sourceVersion); }
    if (!showWarnings) { options.add("-nowarn"); }
    
    
    if (sourceVersion != null) { options.add("-target"); options.add(sourceVersion); }
    else { options.add("-target"); options.add("1.5"); }

    return options;
  }

}
