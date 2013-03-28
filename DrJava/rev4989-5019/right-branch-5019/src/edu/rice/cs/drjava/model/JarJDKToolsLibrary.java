

package edu.rice.cs.drjava.model;

import java.io.File;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.io.IOException;

import edu.rice.cs.plt.iter.IterUtil;
import edu.rice.cs.plt.io.IOUtil;
import edu.rice.cs.plt.lambda.LambdaUtil;
import edu.rice.cs.plt.lambda.Predicate;
import edu.rice.cs.plt.reflect.ReflectUtil;
import edu.rice.cs.plt.reflect.PathClassLoader;
import edu.rice.cs.plt.reflect.ShadowingClassLoader;
import edu.rice.cs.plt.reflect.PreemptingClassLoader;
import edu.rice.cs.plt.reflect.ReflectException;
import edu.rice.cs.plt.reflect.JavaVersion;
import edu.rice.cs.plt.reflect.JavaVersion.FullVersion;

import edu.rice.cs.drjava.model.compiler.CompilerInterface;
import edu.rice.cs.drjava.model.compiler.NoCompilerAvailable;
import edu.rice.cs.drjava.model.debug.Debugger;
import edu.rice.cs.drjava.model.debug.NoDebuggerAvailable;
import edu.rice.cs.drjava.model.javadoc.JavadocModel;
import edu.rice.cs.drjava.model.javadoc.DefaultJavadocModel;
import edu.rice.cs.drjava.model.javadoc.NoJavadocAvailable;


public class JarJDKToolsLibrary extends JDKToolsLibrary {
  
  
  private static final Iterable<String> TOOLS_PACKAGES = IterUtil.asIterable(new String[]{
      
      "com.sun.javadoc",
      "com.sun.jdi",
      "com.sun.tools",
      "sun.applet", 
      "sun.rmi.rmic",
      
                              
      "sun.tools", 
    
      
      "com.sun.jarsigner",
      "com.sun.mirror",
      "sun.jvmstat",
    
      
      "com.sun.codemodel",
      "com.sun.istack.internal.tools", 
      "com.sun.istack.internal.ws",
      "com.sun.source",
      "com.sun.xml.internal.dtdparser", 
      "com.sun.xml.internal.rngom",
      "com.sun.xml.internal.xsom",
      "org.relaxng"
  });

  
  private final File _location;
  
  private JarJDKToolsLibrary(File location, FullVersion version, CompilerInterface compiler, Debugger debugger,
                             JavadocModel javadoc) {
    super(version, compiler, debugger, javadoc);
    _location = location;
  }
  
  public File location() { return _location; }
  
  public String toString() { return super.toString() + " at " + _location; }
  
  
  public static JarJDKToolsLibrary makeFromFile(File f, GlobalModel model) {
    FullVersion version = guessVersion(f);
    CompilerInterface compiler = NoCompilerAvailable.ONLY;
    Debugger debugger = NoDebuggerAvailable.ONLY;
    JavadocModel javadoc = new NoJavadocAvailable(model);
    
    
    if (JavaVersion.CURRENT.supports(version.majorVersion())) {
      
      ClassLoader loader =
        new ShadowingClassLoader(JarJDKToolsLibrary.class.getClassLoader(), true, TOOLS_PACKAGES, true);
      Iterable<File> path = IterUtil.singleton(IOUtil.attemptAbsoluteFile(f));
      
      String compilerAdapter = adapterForCompiler(version.majorVersion());
      if (compilerAdapter != null) {
        
        
        File libDir = null;
        if (f.getName().equals("classes.jar")) { libDir = f.getParentFile(); }
        else if (f.getName().equals("tools.jar")) {
          File jdkLibDir = f.getParentFile();
          if (jdkLibDir != null) {
            File jdkRoot = jdkLibDir.getParentFile();
            if (jdkRoot != null) {
              File jreLibDir = new File(jdkRoot, "jre/lib");
              if (IOUtil.attemptExists(new File(jreLibDir, "rt.jar"))) { libDir = jreLibDir; }
            }
            if (libDir == null) {
              if (IOUtil.attemptExists(new File(jdkLibDir, "rt.jar"))) { libDir = jdkLibDir; }
            }
          }
        }
        List<File> bootClassPath = null; 
        if (libDir != null) {
          File[] jars = IOUtil.attemptListFiles(libDir, IOUtil.extensionFilePredicate("jar"));
          if (jars != null) { bootClassPath = Arrays.asList(jars); }
        }

        try {
          Class<?>[] sig = { FullVersion.class, String.class, List.class };
          Object[] args = { version, f.toString(), bootClassPath };
          CompilerInterface attempt = (CompilerInterface) ReflectUtil.loadLibraryAdapter(loader, path, compilerAdapter, 
                                                                                         sig, args);
          if (attempt.isAvailable()) { compiler = attempt; }
        }
        catch (ReflectException e) {  }
        catch (LinkageError e) {  }
      }
      
      String debuggerAdapter = adapterForDebugger(version.majorVersion());
      String debuggerPackage = "edu.rice.cs.drjava.model.debug.jpda";
      if (debuggerAdapter != null) {
        try {
          Class<?>[] sig = { GlobalModel.class };
          
          ClassLoader debugLoader = new PreemptingClassLoader(new PathClassLoader(loader, path), debuggerPackage);
          Debugger attempt = (Debugger) ReflectUtil.loadObject(debugLoader, debuggerAdapter, sig, model);        
          if (attempt.isAvailable()) { debugger = attempt; }
        }
        catch (ReflectException e) {  }
        catch (LinkageError e) {  }
      }
      
      try {
        new PathClassLoader(loader, path).loadClass("com.sun.tools.javadoc.Main");
        File bin = new File(f.getParentFile(), "../bin");
        if (!IOUtil.attemptIsDirectory(bin)) { bin = new File(f.getParentFile(), "../Home/bin"); }
        if (!IOUtil.attemptIsDirectory(bin)) { bin = new File(System.getProperty("java.home", f.getParent())); }
        javadoc = new DefaultJavadocModel(model, bin, path);
      }
      catch (ClassNotFoundException e) {  }
      catch (LinkageError e) {  }
        
    }
    
    return new JarJDKToolsLibrary(f, version, compiler, debugger, javadoc);
  }
  
  private static FullVersion guessVersion(File f) {
    FullVersion result = null;

    
    File current = IOUtil.attemptCanonicalFile(f);
    do {
      String name = current.getName();
      if (name.startsWith("jdk")) { result = JavaVersion.parseFullVersion(name.substring(3)); }
      else if (name.startsWith("j2sdk")) { result = JavaVersion.parseFullVersion(name.substring(5)); }
      else if (name.matches("\\d+\\.\\d+\\.\\d+")) { result = JavaVersion.parseFullVersion(name); }
      current = current.getParentFile();
    } while (current != null && result == null);
    
    if (result == null || result.majorVersion().equals(JavaVersion.UNRECOGNIZED)) {
      JarFile jf = null;
      try {
        jf = new JarFile(f);
        Manifest mf = jf.getManifest();
        String v = mf.getMainAttributes().getValue("Created-By");
        if (v != null) {
          int space = v.indexOf(' ');
          if (space>=0) v = v.substring(0,space);
          result = JavaVersion.parseFullVersion(v);
        }
      }
      catch(IOException ioe) { result = null; }
      finally {
        try {
          if (jf != null) jf.close();
        }
        catch(IOException ioe) {  }
      }
      if (result == null || result.majorVersion().equals(JavaVersion.UNRECOGNIZED)) {
        
        
        result = JavaVersion.CURRENT_FULL;
      }
    }
    return result;
  }
  
































  
  
  public static Iterable<JarJDKToolsLibrary> search(GlobalModel model) {
    String javaHome = System.getProperty("java.home");
    String envJavaHome = null;
    String programFiles = null;
    String systemDrive = null;
    if (JavaVersion.CURRENT.supports(JavaVersion.JAVA_5)) {
      
      
      envJavaHome = System.getenv("JAVA_HOME");
      programFiles = System.getenv("ProgramFiles");
      systemDrive = System.getenv("SystemDrive");
    }
    
    
    LinkedHashSet<File> roots = new LinkedHashSet<File>();
    
    if (javaHome != null) {
      addIfDir(new File(javaHome), roots);
      addIfDir(new File(javaHome, ".."), roots);
      addIfDir(new File(javaHome, "../.."), roots);
    }
    if (envJavaHome != null) {
      addIfDir(new File(envJavaHome), roots);
      addIfDir(new File(envJavaHome, ".."), roots);
      addIfDir(new File(envJavaHome, "../.."), roots);
    }
    
    if (programFiles != null) {
      addIfDir(new File(programFiles, "Java"), roots);
      addIfDir(new File(programFiles), roots);
    }
    addIfDir(new File("/C:/Program Files/Java"), roots);
    addIfDir(new File("/C:/Program Files"), roots);
    if (systemDrive != null) {
      addIfDir(new File(systemDrive, "Java"), roots);
      addIfDir(new File(systemDrive), roots);
    }
    addIfDir(new File("/C:/Java"), roots);
    addIfDir(new File("/C:"), roots);
    
    addIfDir(new File("/System/Library/Frameworks/JavaVM.framework/Versions"), roots);
    
    addIfDir(new File("/usr/java"), roots);
    addIfDir(new File("/usr/j2se"), roots);
    addIfDir(new File("/usr"), roots);
    addIfDir(new File("/usr/local/java"), roots);
    addIfDir(new File("/usr/local/j2se"), roots);
    addIfDir(new File("/usr/local"), roots);

    
    addIfDir(new File("/usr/lib/jvm"), roots);
    addIfDir(new File("/usr/lib/jvm/java-6-sun"), roots);
    addIfDir(new File("/usr/lib/jvm/java-1.5.0-sun"), roots);
    addIfDir(new File("/usr/lib/jvm/java-6-openjdk"), roots);

    addIfDir(new File("/home/mgricken/usr/lib/jvm"), roots);
    addIfDir(new File("/home/mgricken/usr/lib/jvm/java-6-sun"), roots);
    addIfDir(new File("/home/mgricken/usr/lib/jvm/java-1.5.0-sun"), roots);
    addIfDir(new File("/home/mgricken/usr/lib/jvm/java-6-openjdk"), roots);
    addIfDir(new File("/home/javaplt/java/Linux-i686"), roots);

    
    LinkedHashSet<File> jars = new LinkedHashSet<File>();
    
    Predicate<File> subdirFilter = LambdaUtil.or(IOUtil.regexCanonicalCaseFilePredicate("j2sdk.*"),
                                                 IOUtil.regexCanonicalCaseFilePredicate("jdk.*"),
                                                 LambdaUtil.or(IOUtil.regexCanonicalCaseFilePredicate("\\d+\\.\\d+\\.\\d+"),
                                                               IOUtil.regexCanonicalCaseFilePredicate("java.*")));
    for (File root : roots) {
      for (File subdir : IOUtil.attemptListFilesAsIterable(root, subdirFilter)) {
        addIfFile(new File(subdir, "lib/tools.jar"), jars);
        addIfFile(new File(subdir, "Classes/classes.jar"), jars);
      }
    }
    
    
    Map<FullVersion, Iterable<JarJDKToolsLibrary>> results = 
      new TreeMap<FullVersion, Iterable<JarJDKToolsLibrary>>();
    for (File jar : jars) {
      JarJDKToolsLibrary lib = makeFromFile(jar, model);
      if (lib.isValid()) {
        FullVersion v = lib.version();
        if (results.containsKey(v)) { results.put(v, IterUtil.compose(lib, results.get(v))); }
        else { results.put(v, IterUtil.singleton(lib)); }
      }
    }
    return IterUtil.reverse(IterUtil.collapse(results.values()));
  }
  
  
  private static void addIfDir(File f, Set<? super File> set) {
    f = IOUtil.attemptCanonicalFile(f);
    if (IOUtil.attemptIsDirectory(f)) { set.add(f); }
  }
  
  
  private static void addIfFile(File f, Set<? super File> set) {
    f = IOUtil.attemptCanonicalFile(f);
    if (IOUtil.attemptIsFile(f)) { set.add(f); }
  }
  
}
