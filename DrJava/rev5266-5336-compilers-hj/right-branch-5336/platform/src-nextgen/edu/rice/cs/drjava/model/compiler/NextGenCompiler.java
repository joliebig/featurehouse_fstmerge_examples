

package edu.rice.cs.drjava.model.compiler;

import java.util.MissingResourceException;

import java.io.*;

import java.util.Arrays;
import java.util.Map;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;


import edu.rice.cs.nextgen2.compiler.main.JavaCompiler;
import edu.rice.cs.nextgen2.compiler.util.Context;
import edu.rice.cs.nextgen2.compiler.util.Name;
import edu.rice.cs.nextgen2.compiler.util.Options;
import edu.rice.cs.nextgen2.compiler.util.Position;

import edu.rice.cs.nextgen2.compiler.util.Log;



import edu.rice.cs.drjava.model.DJError;
import edu.rice.cs.plt.reflect.JavaVersion;
import edu.rice.cs.plt.io.IOUtil;
import edu.rice.cs.util.ArgumentTokenizer;

import static edu.rice.cs.plt.debug.DebugUtil.debug;
import static edu.rice.cs.plt.debug.DebugUtil.error;


public class NextGenCompiler extends Javac160FilteringCompiler {
  public NextGenCompiler(JavaVersion.FullVersion version,
                         String location,
                         java.util.List<? extends File> defaultBootClassPath) {
    super(version, location, defaultBootClassPath);
  }
  
  
  private static final PrintWriter NULL_WRITER = new PrintWriter(new Writer() {
    public void write(char cbuf[], int off, int len) throws IOException {}
    public void flush() throws IOException {}
    public void close() throws IOException {}
  });

  public String getName() { return "Nextgen " + _version.versionString(); }
  
  
  public java.util.List<File> additionalBootClassPathForInteractions() {
    System.out.println("NextGenCompiler default boot classpath: "+((_defaultBootClassPath==null)?"null":IOUtil.pathToString(_defaultBootClassPath)));
    System.out.println("NextGenCompiler.additionalBootClassPathForInteractions: "+new File(_location));
    return Arrays.asList(new File(_location));
  }

  
  public String transformCommands(String interactionsString) {
    if (interactionsString.startsWith("applet ")) {
      throw new RuntimeException("Applets not supported by Nextgen.");
    }
    if (interactionsString.startsWith("run ") ||
        interactionsString.startsWith("nextgen ") ||
        interactionsString.startsWith("java ")) interactionsString = _transformNextgenCommand(interactionsString);
    return interactionsString;    
  }
  
  protected static String _transformNextgenCommand(String s) {
    final String command = "edu.rice.cs.nextgen2.classloader.Runner.main(new String[]'{'\"{0}\"{1}'}');";
    if (s.endsWith(";"))  s = _deleteSemiColon(s);
    java.util.List<String> args = ArgumentTokenizer.tokenize(s, true);
    final String classNameWithQuotes = args.get(1); 
    final String className = classNameWithQuotes.substring(1, classNameWithQuotes.length() - 1); 
    final StringBuilder argsString = new StringBuilder();
    for (int i = 2; i < args.size(); i++) {
      argsString.append(",");
      argsString.append(args.get(i));
    }
    return java.text.MessageFormat.format(command, className, argsString.toString());
  }

  public boolean isAvailable() {
    try {
      
      Class.forName("java.lang.Enum");
      
      
      Class.forName("edu.rice.cs.nextgen2.classloader.Runner");
      Class.forName("edu.rice.cs.nextgen2.compiler.Main");
      Class.forName("edu.rice.cs.nextgen2.compiler.main.JavaCompiler");
      return true;
    }
    catch (Exception e) { System.out.println(e); return false; }
    catch (LinkageError e) { return false; }
  }
  

  
  public java.util.List<? extends DJError> compile(java.util.List<? extends File> files,
                                                   java.util.List<? extends File> classPath, 
                                                   java.util.List<? extends File> sourcePath,
                                                   File destination, 
                                                   java.util.List<? extends File> bootClassPath,
                                                   String sourceVersion,
                                                   boolean showWarnings) {
    debug.logStart("compile()");
    debug.logValues(new String[]{ "this", "files", "classPath", "sourcePath", "destination", "bootClassPath", 
                                          "sourceVersion", "showWarnings" },
                    this, files, classPath, sourcePath, destination, bootClassPath, sourceVersion, showWarnings);
    
    Context context = _createContext(classPath, sourcePath, destination, bootClassPath, sourceVersion, showWarnings);
    OurLog log = new OurLog(context);
    JavaCompiler compiler = _makeCompiler(context);
    
    edu.rice.cs.nextgen2.compiler.util.List<String> filesToCompile = _emptyStringList();
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
    if (bootClassPath != null) { System.out.println("bootClassPath: "+IOUtil.pathToString(bootClassPath)); options.put("-bootclasspath", IOUtil.pathToString(bootClassPath)); }
    if (sourceVersion != null) { options.put("-source", sourceVersion); }
    if (!showWarnings) { options.put("-nowarn", ""); }
    
    
    if (sourceVersion != null) { options.put("-target", sourceVersion); }
    else { options.put("-target", "1.5"); }

    return context;
  }

  protected JavaCompiler _makeCompiler(Context context) {
    return JavaCompiler.instance(context);
  }
  
  
  private edu.rice.cs.nextgen2.compiler.util.List<String> _emptyStringList() {
    return edu.rice.cs.nextgen2.compiler.util.List.<String>make();
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
      
      super.note(key, args);
      
      
      
    }
    
    public LinkedList<DJError> getErrors() { return _errors; }
  }
}
