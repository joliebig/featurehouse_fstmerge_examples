

package edu.rice.cs.drjava.model.compiler;

import java.util.MissingResourceException;

import com.sun.tools.javac.util.Options;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Log;
import com.sun.tools.javac.util.JavacMessages;
import com.sun.tools.javac.util.PropagatedException;
import com.sun.tools.javac.util.FatalError;
import com.sun.tools.javac.util.ClientCodeException;
import com.sun.tools.javac.processing.AnnotationProcessingError;

import com.sun.tools.javac.main.JavaCompiler;
  
import com.sun.tools.javac.file.JavacFileManager;
import com.sun.tools.javac.file.CacheFSInfo;

import javax.tools.JavaFileObject;
import javax.tools.JavaFileManager;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.annotation.processing.Processor;

import java.io.*;

import java.util.Arrays;
import java.util.Map;
import java.util.LinkedList;
import java.util.Iterator;


import edu.rice.cs.drjava.model.DJError;
import edu.rice.cs.plt.reflect.JavaVersion;
import edu.rice.cs.plt.io.IOUtil;
import edu.rice.cs.util.ArgumentTokenizer;

import static edu.rice.cs.plt.debug.DebugUtil.debug;
import static edu.rice.cs.plt.debug.DebugUtil.error;


public class MintCompiler extends JavacCompiler {

  private final boolean _filterExe;
  private File _tempJUnit = null;
  private final String PREFIX = "drjava-junit";
  private final String SUFFIX = ".jar";  

  public MintCompiler(JavaVersion.FullVersion version, String location, java.util.List<? extends File> defaultBootClassPath) {
    super(version, location, defaultBootClassPath);
    _filterExe = version.compareTo(JavaVersion.parseFullVersion("1.6.0_04")) >= 0;
    if (_filterExe) {
      
      
      try {
        
        
        InputStream is = MintCompiler.class.getResourceAsStream("/junit.jar");
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

  public String getName() { return "Mint " + _version.versionString(); }
  
  
  public java.util.List<File> additionalBootClassPathForInteractions() {
    return Arrays.asList(new File(_location));
  }

  
  public String transformCommands(String interactionsString) {
    if (interactionsString.startsWith("mint ") ||
        interactionsString.startsWith("java ")) interactionsString = _transformMintCommand(interactionsString);
    return interactionsString;    
  }
  
  protected static String _transformMintCommand(String s) {
    final String command = "edu.rice.cs.mint.runtime.Mint.execute(\"edu.rice.cs.drjava.interactions.class.path\", \"{0}\"{1});";
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
      
      Class<?> diagnostic = Class.forName("javax.tools.Diagnostic");
      diagnostic.getMethod("getKind");
      
      
      Class.forName("com.sun.tools.javac.main.JavaCompiler");
      
      Class.forName("edu.rice.cs.mint.comp.TransStaging");
      Class.forName("com.sun.source.tree.BracketExprTree");
      Class.forName("com.sun.source.tree.BracketStatTree");
      Class.forName("com.sun.source.tree.EscapeExprTree");
      Class.forName("com.sun.source.tree.EscapeStatTree");
      return true;
    }
    catch (Exception e) { return false; }
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
    java.util.List<File> filteredClassPath = null;
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
    Context context = _createContext(filteredClassPath, sourcePath, destination, bootClassPath, sourceVersion, showWarnings);
    new CompilerErrorListener(context, errors);

    int result = compile(new String[] {},
                         ListBuffer.<File>lb().appendArray(files.toArray(new File[0])).toList(),
                         context);
    
    debug.logEnd("compile()");
    return errors;
  }
    
  private Context _createContext(java.util.List<? extends File> classPath,
                                 java.util.List<? extends File> sourcePath,
                                 File destination, 
                                 java.util.List<? extends File> bootClassPath,
                                 String sourceVersion,
                                 boolean showWarnings) {

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
    
    private java.util.List<? super DJError> _errors;
    
    public CompilerErrorListener(Context context, java.util.List<? super DJError> errors) {
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
  
  
  
    
    String ownName = "mint";

    
    PrintWriter out = new PrintWriter(System.err,true);

    
    boolean fatalErrors;

    
    static final int
        EXIT_OK = 0,        
        EXIT_ERROR = 1,     
        EXIT_CMDERR = 2,    
        EXIT_SYSERR = 3,    
        EXIT_ABNORMAL = 4;  

    
    private Options options = null;

    
    public ListBuffer<File> filenames = null; 

    
    public ListBuffer<String> classnames = null; 

    
    void error(String key, Object... args) {
        if (fatalErrors) {
            String msg = getLocalizedString(key, args);
            throw new PropagatedException(new IllegalStateException(msg));
        }
        warning(key, args);
        Log.printLines(out, getLocalizedString("msg.usage", ownName));
    }

    
    void warning(String key, Object... args) {
        Log.printLines(out, ownName + ": "
                       + getLocalizedString(key, args));
    }

    public void setFatalErrors(boolean fatalErrors) {
        this.fatalErrors = fatalErrors;
    }

    
    public int compile(String[] args, List<File> files, Context context) {
        JavacFileManager.preRegister(context); 
        int result = compile(args, files, context, List.<JavaFileObject>nil(), null);
        if (fileManager instanceof JavacFileManager) {
            
            ((JavacFileManager)fileManager).close();
        }
        return result;
    }
    
    
    public int compile(String[] args,
                       List<File> files,
                       Context context,
                       List<JavaFileObject> fileObjects,
                       Iterable<? extends Processor> processors) {
        if (options == null)
            options = Options.instance(context); 

        filenames = new ListBuffer<File>();
        classnames = new ListBuffer<String>();
        JavaCompiler comp = null;
        
        try {
            if (args.length == 0 && files.isEmpty() && fileObjects.isEmpty()) {
                return EXIT_CMDERR;
            }



                if (files == null) {
                    
                    return EXIT_CMDERR;
                } else if (files.isEmpty() && fileObjects.isEmpty() && classnames.isEmpty()) {
                    
                    error("err.no.source.files");
                    return EXIT_CMDERR;
                }






            boolean forceStdOut = options.get("stdout") != null;
            if (forceStdOut) {
                out.flush();
                out = new PrintWriter(System.out, true);
            }
            context.put(Log.outKey, out);

            
            boolean batchMode = (options.get("nonBatchMode") == null
                        && System.getProperty("nonBatchMode") == null);
            if (batchMode)
                CacheFSInfo.preRegister(context);
            fileManager = context.get(JavaFileManager.class);
            comp = JavaCompiler.instance(context);
            if (comp == null) return EXIT_SYSERR;
            Log log = Log.instance(context);
            if (!files.isEmpty()) {
                
                comp = JavaCompiler.instance(context);
                List<JavaFileObject> otherFiles = List.nil();
                JavacFileManager dfm = (JavacFileManager)fileManager;
                for (JavaFileObject fo : dfm.getJavaFileObjectsFromFiles(files))
                    otherFiles = otherFiles.prepend(fo);
                for (JavaFileObject fo : otherFiles)
                    fileObjects = fileObjects.prepend(fo);
            }
            comp.compile(fileObjects,
                         classnames.toList(),
                         processors);
            if (log.expectDiagKeys != null) {
                if (log.expectDiagKeys.size() == 0) {
                    Log.printLines(log.noticeWriter, "all expected diagnostics found");
                    return EXIT_OK;
                } else {
                    Log.printLines(log.noticeWriter, "expected diagnostic keys not found: " + log.expectDiagKeys);
                    return EXIT_ERROR;
                }
            }
            if (comp.errorCount() != 0 ||
                options.get("-Werror") != null && comp.warningCount() != 0)
                return EXIT_ERROR;
        } catch (IOException ex) {
            ioMessage(ex);
            return EXIT_SYSERR;
        } catch (OutOfMemoryError ex) {
            resourceMessage(ex);
            return EXIT_SYSERR;
        } catch (StackOverflowError ex) {
            resourceMessage(ex);
            return EXIT_SYSERR;
        } catch (FatalError ex) {
            feMessage(ex);
            return EXIT_SYSERR;
        } catch(AnnotationProcessingError ex) {
            apMessage(ex);
            return EXIT_SYSERR;
        } catch (ClientCodeException ex) {
            
            
            throw new RuntimeException(ex.getCause());
        } catch (PropagatedException ex) {
            throw ex.getCause();
        } catch (Throwable ex) {
            
            
            
            if (comp == null || comp.errorCount() == 0 ||
                options == null || options.get("dev") != null)
                bugMessage(ex);
            return EXIT_ABNORMAL;
        } finally {
            if (comp != null) comp.close();
            filenames = null;
            options = null;
        }
        return EXIT_OK;
    }

    
    void bugMessage(Throwable ex) {
        Log.printLines(out, getLocalizedString("msg.bug",
                                               JavaCompiler.version()));
        ex.printStackTrace(out);
    }

    
    void feMessage(Throwable ex) {
        Log.printLines(out, ex.getMessage());
    }

    
    void ioMessage(Throwable ex) {
        Log.printLines(out, getLocalizedString("msg.io"));
        ex.printStackTrace(out);
    }

    
    void resourceMessage(Throwable ex) {
        Log.printLines(out, getLocalizedString("msg.resource"));

        ex.printStackTrace(out);
    }

    
    void apMessage(AnnotationProcessingError ex) {
        Log.printLines(out,
                       getLocalizedString("msg.proc.annotation.uncaught.exception"));
        ex.getCause().printStackTrace();
    }

    private JavaFileManager fileManager;

    

    
    public static String getLocalizedString(String key, Object... args) { 
        try {
            if (messages == null)
                messages = new JavacMessages(javacBundleName);
            return messages.getLocalizedString("javac." + key, args);
        }
        catch (MissingResourceException e) {
            throw new Error("Fatal Error: Resource for javac is missing", e);
        }
    }

    public static void useRawMessages(boolean enable) {
        if (enable) {
            messages = new JavacMessages(javacBundleName) {
                    public String getLocalizedString(String key, Object... args) {
                        return key;
                    }
                };
        } else {
            messages = new JavacMessages(javacBundleName);
        }
    }

    private static final String javacBundleName =
        "com.sun.tools.javac.resources.javac";

    private static JavacMessages messages;
}
