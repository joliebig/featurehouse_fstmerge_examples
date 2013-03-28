

package edu.rice.cs.drjava.model.compiler;

import java.io.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Iterator;


import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;


import edu.rice.cs.drjava.model.DJError;


import com.sun.tools.javac.main.JavaCompiler;
import com.sun.tools.javac.main.Main;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.DefaultFileManager;


import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Options;
import com.sun.tools.javac.util.Position;

import com.sun.tools.javac.util.Log;

import edu.rice.cs.plt.reflect.JavaVersion;
import edu.rice.cs.plt.io.IOUtil;

import static edu.rice.cs.plt.debug.DebugUtil.debug;
import static edu.rice.cs.plt.debug.DebugUtil.error;


public class Javac160Compiler extends Javac160FilteringCompiler {
  public Javac160Compiler(JavaVersion.FullVersion version, String location, List<? extends File> defaultBootClassPath) {
    super(version, location, defaultBootClassPath);
  }
  
  public boolean isAvailable() {
    try {
      
      Class<?> diagnostic = Class.forName("javax.tools.Diagnostic");
      diagnostic.getMethod("getKind");
      
      
      Class.forName("com.sun.tools.javac.main.JavaCompiler");
      return true;
    }
    catch (Exception e) { return false; }
    catch (LinkageError e) { return false; }
  }
  

  
  public List<? extends DJError> compile(List<? extends File> files, List<? extends File> classPath, 
                                               List<? extends File> sourcePath, File destination, 
                                               List<? extends File> bootClassPath, String sourceVersion, boolean showWarnings) {
    debug.logStart("compile()");
    debug.logValues(new String[]{ "this", "files", "classPath", "sourcePath", "destination", "bootClassPath", 
                                  "sourceVersion", "showWarnings" },
                              this, files, classPath, sourcePath, destination, bootClassPath, sourceVersion, showWarnings);
    java.util.List<File> filteredClassPath = getFilteredClassPath(classPath);

    Context context = _createContext(filteredClassPath, sourcePath, destination, bootClassPath, sourceVersion, showWarnings);
    LinkedList<DJError> errors = new LinkedList<DJError>();
    new CompilerErrorListener(context, errors);
    
    JavaCompiler compiler = JavaCompiler.instance(context);
    
    
    DefaultFileManager fileManager = (DefaultFileManager) context.get(JavaFileManager.class);
    com.sun.tools.javac.util.List<JavaFileObject> fileObjects = com.sun.tools.javac.util.List.nil();
    for (File f : files) fileObjects = fileObjects.prepend(fileManager.getRegularFile(f));
    
    try { compiler.compile(fileObjects); }
    catch(Throwable t) {  
      errors.addFirst(new DJError("Compile exception: " + t, false));
      error.log(t);
    }
    
    debug.logEnd("compile()");
    return errors;
  }
    
  private Context _createContext(List<? extends File> classPath, List<? extends File> sourcePath, File destination, 
                                 List<? extends File> bootClassPath, String sourceVersion, boolean showWarnings) {

    if (bootClassPath == null) { bootClassPath = _defaultBootClassPath; }
    
    Context context = new Context();
    Options options = Options.instance(context);
    for (Map.Entry<String, String> e : CompilerOptions.getOptions(showWarnings).entrySet()) {
      options.put(e.getKey(), e.getValue());
    }
    
    
    options.put("-g", "");
    
    if (classPath != null) { options.put("-classpath", IOUtil.pathToString(classPath)); }
    if (sourcePath != null) { options.put("-sourcepath", IOUtil.pathToString(sourcePath)); }
    if (destination != null) { options.put("-d", destination.getPath()); }
    if (bootClassPath != null) { options.put("-bootclasspath", IOUtil.pathToString(bootClassPath)); }
    if (sourceVersion != null) { options.put("-source", sourceVersion); }
    if (!showWarnings) { options.put("-nowarn", ""); }
    
    return context;
  }
  
  
  private static class CompilerErrorListener implements DiagnosticListener<JavaFileObject> {
    
    private List<? super DJError> _errors;
    
    public CompilerErrorListener(Context context, List<? super DJError> errors) {
      _errors = errors;
      context.put(DiagnosticListener.class, this);
    }
    
    public void report(Diagnostic<? extends JavaFileObject> d) {
      
      Diagnostic.Kind dt = d.getKind();
      boolean isWarning = false;  
      
      switch (dt) {
        case OTHER:             return;
        case NOTE:              return;
        case MANDATORY_WARNING: isWarning = true; break;
        case WARNING:           isWarning = true; break;
        case ERROR:             isWarning = false; break;
      }
      
      
      
      if (d.getSource()!=null) {
        _errors.add(new DJError(new File(d.getSource().toUri().getPath()), 
                                ((int) d.getLineNumber()) - 1,  
                                ((int) d.getColumnNumber()) - 1, 
                                d.getMessage(null),    
                                isWarning));
      }
      else {
        _errors.add(new DJError(d.getMessage(null), isWarning));
      }
    }
  }
  
}
