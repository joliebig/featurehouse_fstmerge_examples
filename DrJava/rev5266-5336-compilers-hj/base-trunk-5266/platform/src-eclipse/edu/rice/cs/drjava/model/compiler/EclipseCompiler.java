

package edu.rice.cs.drjava.model.compiler;

import java.io.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Locale;


import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.StandardJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.StandardLocation;


import edu.rice.cs.drjava.model.DJError;

import edu.rice.cs.plt.reflect.JavaVersion;
import edu.rice.cs.plt.io.IOUtil;
import edu.rice.cs.plt.iter.IterUtil;

import static edu.rice.cs.plt.debug.DebugUtil.debug;
import static edu.rice.cs.plt.debug.DebugUtil.error;


public class EclipseCompiler extends JavacCompiler {
  
  
  private final boolean _filterExe;
  private File _tempJUnit = null;
  private final String PREFIX = "drjava-junit";
  private final String SUFFIX = ".jar";  
  
  public EclipseCompiler(JavaVersion.FullVersion version, String location, List<? extends File> defaultBootClassPath) {
    super(version, location, defaultBootClassPath);
    _filterExe = version.compareTo(JavaVersion.parseFullVersion("1.6.0_04")) >= 0;
    if (_filterExe) {
      
      
      try {
        
        
        InputStream is = Javac160Compiler.class.getResourceAsStream("/junit.jar");
        if (is!=null) {
          
          _tempJUnit = edu.rice.cs.plt.io.IOUtil.createAndMarkTempFile(PREFIX,SUFFIX);
          FileOutputStream fos = new FileOutputStream(_tempJUnit);
          int size = edu.rice.cs.plt.io.IOUtil.copyInputStream(is,fos);
          
        }
        else {
          
          if (_tempJUnit!=null) {
            _tempJUnit.delete();
            _tempJUnit = null;
          }
        }
      }
      catch(IOException ioe) {
        if (_tempJUnit!=null) {
          _tempJUnit.delete();
          _tempJUnit = null;
        }
      }
      
      
      Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
        public void run() {
          try {
            File temp = File.createTempFile(PREFIX, SUFFIX);
            IOUtil.attemptDelete(temp);
            File[] toDelete = temp.getParentFile().listFiles(new FilenameFilter() {
              public boolean accept(File dir, String name) {
                if ((!name.startsWith(PREFIX)) || (!name.endsWith(SUFFIX))) return false;
                String rest = name.substring(PREFIX.length(), name.length()-SUFFIX.length());
                try {
                  Integer i = new Integer(rest);
                  
                  return true;
                }
                catch(NumberFormatException e) {  }
                return false;
              }
            });
            for(File f: toDelete) {
              f.delete();
            }
          }
          catch(IOException ioe) {  }
        }
      })); 
    }
  }
  
  public boolean isAvailable() {
    try {
      
      Class<?> diagnostic = Class.forName("javax.tools.Diagnostic");
      diagnostic.getMethod("getKind");
      
      Class.forName("org.eclipse.jdt.internal.compiler.tool.EclipseCompiler");
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
    List<File> filteredClassPath = null;
    if (classPath!=null) {
      filteredClassPath = new LinkedList<File>(classPath);
      
      if (_filterExe) {
        FileFilter filter = IOUtil.extensionFilePredicate("exe");
        Iterator<? extends File> i = filteredClassPath.iterator();
        while (i.hasNext()) {
          if (filter.accept(i.next())) { i.remove(); }
        }
        if (_tempJUnit!=null) { filteredClassPath.add(_tempJUnit); }
      }
    }
    
    LinkedList<DJError> errors = new LinkedList<DJError>();
    
    JavaCompiler compiler = new org.eclipse.jdt.internal.compiler.tool.EclipseCompiler();
    CompilerErrorListener diagnosticListener = new CompilerErrorListener(errors);
    StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnosticListener, null, null);
    Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(files);
    Writer out = new OutputStreamWriter(new OutputStream() { 
      public void write(int b) { }
    });

    Iterable<String> classes = null; 
    Iterable<String> options = _getOptions(fileManager,
                                           filteredClassPath, sourcePath, destination,
                                           bootClassPath, sourceVersion, showWarnings);
    
    try {
      JavaCompiler.CompilationTask task = compiler.getTask(out, fileManager, diagnosticListener, options, classes, compilationUnits);
      boolean res = task.call();
      if (!res && (errors.size()==0)) throw new AssertionError("Compile failed. There should be compiler errors, but there aren't.");
    }
    catch(Throwable t) {  
      errors.addFirst(new DJError("Compile exception: " + t, false));
      error.log(t);
    }
    
    debug.logEnd("compile()");
    return errors;
  }
  
  public String getName() {
    try {
      ResourceBundle bundle = ResourceBundle.getBundle("org.eclipse.jdt.internal.compiler.batch.messages");
      String ecjVersion = bundle.getString("compiler.version");
      int commaPos = ecjVersion.indexOf(',');
      if (commaPos>=0) { ecjVersion = ecjVersion.substring(0, commaPos); }
      return "Eclipse Compiler "+ecjVersion;
    }
    catch(Throwable t) {
      return "Eclipse Compiler " + _version.versionString();
    }
  }
  
  
  private static void addOption(List<String> options, String s) {
    if (s.length()>0) options.add(s);
  }
  
  private Iterable<String> _getOptions(StandardJavaFileManager fileManager,
                                       List<? extends File> classPath, List<? extends File> sourcePath, File destination, 
                                       List<? extends File> bootClassPath, String sourceVersion, boolean showWarnings) {
    




    
    if (bootClassPath == null) { bootClassPath = _defaultBootClassPath; }
    
    List<String> options = new ArrayList<String>();
    for (Map.Entry<String, String> e : CompilerOptions.getOptions(showWarnings).entrySet()) {
      addOption(options,e.getKey());
      addOption(options,e.getValue());
    }
    
    
    addOption(options,"-g");
    
    if (classPath != null) {
      addOption(options,"-classpath");
      addOption(options,IOUtil.pathToString(classPath));
      try {
        fileManager.setLocation(StandardLocation.CLASS_PATH, classPath);
      }
      catch(IOException ioe) {  }
    }
    if (sourcePath != null) {
      addOption(options,"-sourcepath");
      addOption(options,IOUtil.pathToString(sourcePath));
      try {
        fileManager.setLocation(StandardLocation.SOURCE_PATH, sourcePath);
      }
      catch(IOException ioe) {  }        
    }
    if (destination != null) {
      addOption(options,"-d");
      addOption(options,destination.getPath());
      try {
        fileManager.setLocation(StandardLocation.CLASS_OUTPUT, IterUtil.asIterable(destination));
      }
      catch(IOException ioe) {  }
    }
    if (bootClassPath != null) {
      addOption(options,"-bootclasspath");
      addOption(options,IOUtil.pathToString(bootClassPath));
      try {
        fileManager.setLocation(StandardLocation.PLATFORM_CLASS_PATH, bootClassPath);
      }
      catch(IOException ioe) {  }
    }
    if (sourceVersion != null) {
      addOption(options,"-source");
      addOption(options,sourceVersion);
    }
    if (!showWarnings) {
      addOption(options,"-nowarn");
    }
    
    return options;
  }
  
  
  private static class CompilerErrorListener implements DiagnosticListener<JavaFileObject> {
    
    private List<? super DJError> _errors;
    
    public CompilerErrorListener(List<? super DJError> errors) {
      _errors = errors;
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
                                d.getMessage(Locale.getDefault()),    
                                isWarning));
      }
      else {
        _errors.add(new DJError(d.getMessage(Locale.getDefault()), isWarning));
      }
    }
  }
  
}
