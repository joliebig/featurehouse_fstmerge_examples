

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


import com.sun.tools.javac.main.JavaCompiler;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Options;
import com.sun.tools.javac.util.Position;

import com.sun.tools.javac.util.Log;

import edu.rice.cs.drjava.model.DJError;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.plt.reflect.JavaVersion;
import edu.rice.cs.plt.io.IOUtil;

import static edu.rice.cs.plt.debug.DebugUtil.debug;
import static edu.rice.cs.plt.debug.DebugUtil.error;


public class Javac150Compiler extends JavacCompiler {
  
  private boolean _supportsJSR14v2_4;
  private boolean _isJSR14v2_5;
    
  public static final String COMPILER_CLASS_NAME = "com.sun.tools.javac.main.JavaCompiler";
  
  
  private static final PrintWriter NULL_WRITER = new PrintWriter(new Writer() {
    public void write(char cbuf[], int off, int len) throws IOException {}
    public void flush() throws IOException {}
    public void close() throws IOException {}
  });

  public Javac150Compiler(JavaVersion.FullVersion version, String location, List<? extends File> defaultBootClassPath) {
    super(version, location, defaultBootClassPath);
    _isJSR14v2_5 = false;
    _supportsJSR14v2_4 = _supportsJSR14v2_4();
  }
  
  public boolean isAvailable() {
    try {
      Class.forName("com.sun.tools.javac.main.JavaCompiler");
      try { Class.forName("java.lang.Enum"); }
      catch (Exception e) {
        
        
        
        Class.forName("com.sun.tools.javac.main.Main$14");
        _isJSR14v2_5 = true;
      }
      return _isValidVersion();
    }
    catch (Exception e) { return false; }
    catch (LinkageError e) { return false; }
  }
  
  
  private boolean _isValidVersion() {
    
    Class log = com.sun.tools.javac.util.Log.class;

    
    Class[] validArgs1 = { Context.class };
    
    try { 
      log.getMethod("instance", validArgs1);  
      try {
        log.getMethod("hasDiagnosticListener");  
        return false; 
      }
      catch(NoSuchMethodException e) {
        return true; 
      }
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
    
    Context context = _createContext(classPath, sourcePath, destination, bootClassPath, sourceVersion, showWarnings);
    OurLog log = new OurLog(context);
    JavaCompiler compiler = _makeCompiler(context);
    
    com.sun.tools.javac.util.List<String> filesToCompile = _emptyStringList();
    for (File f : files) {
      
      filesToCompile = filesToCompile.prepend(f.getAbsolutePath());
    }
    
    try { compiler.compile(filesToCompile); }
    catch (Throwable t) {
      
      
      
      
      
      LinkedList<DJError> errors = log.getErrors();
      errors.addFirst(new DJError("Compile exception: " + t, false));
      error.log(t);
      debug.logEnd("compile() (caught an exception)");
      return errors;
    }
    
    debug.logEnd("compile()");
    return log.getErrors();
  }
  
  public String getName() {
    if (_isJSR14v2_5) return "JSR-14 v2.5";
    
    else return super.getName();
  }
  
  private Context _createContext(List<? extends File> classPath, List<? extends File> sourcePath, File destination, 
                                 List<? extends File> bootClassPath, String sourceVersion, boolean showWarnings) {

    if (bootClassPath == null) { bootClassPath = _defaultBootClassPath; }
    
    Context context = new Context();
    Options options = Options.instance(context);
    options.putAll(CompilerOptions.getOptions(showWarnings));
    
    
    options.put("-g", "");

    if (classPath != null) { options.put("-classpath", IOUtil.pathToString(classPath)); }
    if (sourcePath != null) { options.put("-sourcepath", IOUtil.pathToString(sourcePath)); }
    if (destination != null) { options.put("-d", destination.getPath()); }
    if (bootClassPath != null) { options.put("-bootclasspath", IOUtil.pathToString(bootClassPath)); }
    if (sourceVersion != null) { options.put("-source", sourceVersion); }
    if (!showWarnings) { options.put("-nowarn", ""); }
    
    
    if (sourceVersion != null) { options.put("-target", sourceVersion); }
    else { options.put("-target", "1.5"); }

    return context;
  }

  protected JavaCompiler _makeCompiler(Context context) {
    
    
    Class javaCompilerClass = JavaCompiler.class;

    Class[] validArgs1 = {
      Context.class
    };
    Method m;    
    if (_supportsJSR14v2_4) {    
      try { 
        m = javaCompilerClass.getMethod("instance", validArgs1);
        return (JavaCompiler)m.invoke(null, new Object[] {context});
      }
      catch (NoSuchMethodException e) { throw new UnexpectedException(e); }
      catch (IllegalAccessException e) { throw new UnexpectedException(e); }
      catch (InvocationTargetException e) {
        e.printStackTrace();
        throw new UnexpectedException(e);
      }      
    }
    else {
      try { 
        m = javaCompilerClass.getMethod("make", validArgs1);
        return (JavaCompiler)m.invoke(null, new Object[] {context});
      }
      catch (NoSuchMethodException e) { throw new UnexpectedException(e); }
      catch (IllegalAccessException e) { throw new UnexpectedException(e); }
      catch (InvocationTargetException e) { throw new UnexpectedException(e); }

    }
  }
  
  
  private boolean _supportsJSR14v2_4() {
    try {
      Class.forName("com.sun.tools.javac.main.Main$14");
      return true;
    }
    catch (Exception e) {
      try {
        Class.forName("com.sun.tools.javac.main.Main+1");
        return true;
      }
      catch (Exception e2) { return false; }
    }
  }

  
  private com.sun.tools.javac.util.List<String> _emptyStringList() {
    try {
      Method nil = com.sun.tools.javac.util.List.class.getMethod("nil");
      @SuppressWarnings("unchecked") com.sun.tools.javac.util.List<String> result = 
        (com.sun.tools.javac.util.List<String>) nil.invoke(null);
      return result;
    }
    catch (InvocationTargetException e) {
      throw new RuntimeException("Exception occured when invoking com.sun.tools.javac.util.List.nil()", e);
    }
    catch (Exception e) {
      try { 
        @SuppressWarnings("unchecked") com.sun.tools.javac.util.List<String> result = 
          (com.sun.tools.javac.util.List<String>) com.sun.tools.javac.util.List.class.newInstance();
        return result;
      }
      catch (Exception e2) {
        throw new RuntimeException("Unable to create an instance of com.sun.tools.javac.util.List", e);
      }
    }
  }

  
  private static class OurLog extends Log {
    
    private LinkedList<DJError> _errors = new LinkedList<DJError>();
    private String _sourceName = "";

    public OurLog(Context context) { super(context, NULL_WRITER, NULL_WRITER, NULL_WRITER); }

    
    public void warning(int pos, String key, Object ... args) {
      super.warning(pos, key, args);
      

      String msg = getText("compiler.warn." + key, args);
      
      if (currentSource()!=null) {
        _errors.addLast(new DJError(new File(currentSource().toString()),
                                    Position.line(pos) - 1, 
                                    Position.column(pos) - 1,
                                    msg,
                                    true));
      }
      else {
        _errors.addLast(new DJError(msg, true));
      }
    }

    
    public void mandatoryWarning(int pos, String key, Object ... args) {
      super.mandatoryWarning(pos, key, args);
      
      
      String msg = getText("compiler.warn." + key, args);
      
      if (currentSource()!=null) {
        _errors.addLast(new DJError(new File(currentSource().toString()),
                                    Position.line(pos) - 1, 
                                    Position.column(pos) - 1,
                                    msg,
                                    true));
      }
      else {
        _errors.addLast(new DJError(msg, true));
      }
    }
    
    
    public void error(int pos, String key, Object ... args) {
      super.error(pos, key, args);
      

      String msg = getText("compiler.err." + key, args);

      if (currentSource()!=null) {
        _errors.addLast(new DJError(new File(currentSource().toString()),
                                    Position.line(pos) - 1, 
                                    Position.column(pos) - 1,
                                    msg,
                                    false));
      }
      else {
        _errors.addLast(new DJError(msg, false));
      }
    }

    public void note(String key, Object ... args) {
      super.note(key, args);
      
      
      
    }
    
    public void mandatoryNote(String key, Object ... args) {
      super.mandatoryNote(key, args);
      
      
      
    }
    
    public LinkedList<DJError> getErrors() { return _errors; }
  }
}
