

package edu.rice.cs.drjava.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import edu.rice.cs.drjava.model.compiler.CompilerInterface;
import edu.rice.cs.drjava.model.compiler.NoCompilerAvailable;
import edu.rice.cs.drjava.model.debug.Debugger;
import edu.rice.cs.drjava.model.debug.NoDebuggerAvailable;
import edu.rice.cs.drjava.model.javadoc.DefaultJavadocModel;
import edu.rice.cs.drjava.model.javadoc.JavadocModel;
import edu.rice.cs.drjava.model.javadoc.NoJavadocAvailable;
import edu.rice.cs.plt.io.IOUtil;
import edu.rice.cs.plt.iter.IterUtil;
import edu.rice.cs.plt.lambda.LambdaUtil;
import edu.rice.cs.plt.lambda.Predicate;
import edu.rice.cs.plt.reflect.JavaVersion;
import edu.rice.cs.plt.reflect.PathClassLoader;
import edu.rice.cs.plt.reflect.PreemptingClassLoader;
import edu.rice.cs.plt.reflect.ReflectException;
import edu.rice.cs.plt.reflect.ReflectUtil;
import edu.rice.cs.plt.reflect.ShadowingClassLoader;
import edu.rice.cs.plt.reflect.JavaVersion.FullVersion;


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
      "org.relaxng",
        
      
      "com.sun.tools.javac",
      "com.sun.tools.javac.tree",
      "com.sun.tools.javac.comp",
      "com.sun.tools.javac.main",
      "edu.rice.cs.mint",
      "edu.rice.cs.mint.comp",
      "edu.rice.cs.mint.runtime",
      "edu.rice.cs.mint.runtime.mspTree"
  });

  
  private final File _location;
  private final List<File> _bootClassPath; 
  
  private JarJDKToolsLibrary(File location, FullVersion version, CompilerInterface compiler, Debugger debugger,
                             JavadocModel javadoc, List<File> bootClassPath) {
    super(version, compiler, debugger, javadoc);
    _location = location;
    _bootClassPath = bootClassPath;
  }
  
  public File location() { return _location; }
  public List<File> bootClassPath() { 
    if (_bootClassPath!=null) return new ArrayList<File>(_bootClassPath);
    else return null;
  }
  
  public String toString() { return super.toString() + " at " + _location + ", boot classpath: " + bootClassPath(); }

  
  public static JarJDKToolsLibrary makeFromFile(File f, GlobalModel model) {
    return makeFromFile(f, model, new ArrayList<File>());
  }

  
  public static JarJDKToolsLibrary makeFromFile(File f, GlobalModel model, List<File> additionalBootClassPath) {
    FullVersion version = guessVersion(f);

    CompilerInterface compiler = NoCompilerAvailable.ONLY;
    Debugger debugger = NoDebuggerAvailable.ONLY;
    JavadocModel javadoc = new NoJavadocAvailable(model);
    
    
    List<File> bootClassPath = null;
    if (JavaVersion.CURRENT.supports(version.majorVersion())) {
      
      ClassLoader loader =
        new ShadowingClassLoader(JarJDKToolsLibrary.class.getClassLoader(), true, TOOLS_PACKAGES, true);
      Iterable<File> path = IterUtil.singleton(IOUtil.attemptAbsoluteFile(f));
      
      String compilerAdapter = adapterForCompiler(version);
      if (compilerAdapter != null) {
        
        
        File libDir = null;
        if (f.getName().equals("classes.jar")) { libDir = f.getParentFile(); }
        else if (f.getName().equals("hjc.jar")) { libDir = f.getParentFile(); }
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
        
        
        bootClassPath = new ArrayList<File>();
        if (libDir != null) {
          File[] jars = IOUtil.attemptListFiles(libDir, IOUtil.extensionFilePredicate("jar"));
          if (jars != null) { bootClassPath.addAll(Arrays.asList(jars)); }
        }
        bootClassPath.addAll(additionalBootClassPath);
        if (bootClassPath.isEmpty()) { bootClassPath = null; } 

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
      
      String debuggerAdapter = adapterForDebugger(version);
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
    
    return new JarJDKToolsLibrary(f, version, compiler, debugger, javadoc, bootClassPath);
  }
  
  private static FullVersion guessVersion(File f) {
    FullVersion result = null;

    
    File current = IOUtil.attemptCanonicalFile(f);
    String parsedVersion = "";
    String vendor = "";
    do {
      String name = current.getName();
      String path = current.getAbsolutePath();
      if (path.startsWith("/System/Library/Frameworks/JavaVM.framework")) vendor = "apple";
      else if (path.toLowerCase().contains("openjdk")) vendor = "openjdk";
      if (name.startsWith("jdk")) { result = JavaVersion.parseFullVersion(parsedVersion = name.substring(3),vendor,vendor); }
      else if (name.startsWith("j2sdk")) { result = JavaVersion.parseFullVersion(parsedVersion = name.substring(5),vendor,vendor); }
      else if (name.matches("\\d+\\.\\d+\\.\\d+")) { result = JavaVersion.parseFullVersion(parsedVersion = name,vendor,vendor); }
      current = current.getParentFile();
    } while (current != null && result == null);
    if (result == null || result.majorVersion().equals(JavaVersion.UNRECOGNIZED)) {
      JarFile jf = null;
      try {
        jf = new JarFile(f);
        Manifest mf = jf.getManifest();
        if (mf != null) {
          String v = mf.getMainAttributes().getValue("Created-By");
          if (v != null) {
            int space = v.indexOf(' ');
            if (space >= 0) v = v.substring(0,space);
            result = JavaVersion.parseFullVersion(parsedVersion = v,vendor,vendor);
          }
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
        parsedVersion = result.versionString();
      }
    }
    
    if ((result == null) ||
        (result.vendor()==JavaVersion.VendorType.UNKNOWN &&
         result.majorVersion().compareTo(JavaVersion.JAVA_6)>=0) ||
        (f.getAbsolutePath().toLowerCase().contains("mint")) || 
        (f.getAbsolutePath().toLowerCase().contains("hj"))) {
      JarFile jf = null;
      try {
        jf = new JarFile(f);
        if (jf.getJarEntry("edu/rice/cs/mint/comp/TransStaging.class")!=null &&
            jf.getJarEntry("com/sun/source/tree/BracketExprTree.class")!=null &&
            jf.getJarEntry("com/sun/source/tree/BracketStatTree.class")!=null &&
            jf.getJarEntry("com/sun/source/tree/EscapeExprTree.class")!=null &&
            jf.getJarEntry("com/sun/source/tree/EscapeStatTree.class")!=null &&
            jf.getJarEntry("com/sun/tools/javac/util/DefaultFileManager.class")==null) {
          vendor = "mint";
        } else if (f.getName().equals("hjc.jar")) {
    		vendor = "hj";
    		parsedVersion = "1.6.0";
    	}
        else if (jf.getJarEntry("com/sun/tools/javac/util/DefaultFileManager.class")==null) {
          vendor = "openjdk";
        }
        result = JavaVersion.parseFullVersion(parsedVersion,vendor,vendor);
      }
      catch(IOException ioe) {  }
      finally {
        try {
          if (jf != null) jf.close();
        }
        catch(IOException ioe) {  }
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
    
    

    String hj_home = System.getenv("HJ_HOME");
    if (hj_home!=null) {
      JDKToolsLibrary.msg("HJ_HOME environment variable set to: "+hj_home);
      addIfFile(new File(new File(hj_home), "lib/hjc.jar"), jars);
      addIfFile(new File(new File(hj_home), "lib/hj.jar"), jars);
    }
    else {
      JDKToolsLibrary.msg("HJ_HOME not set");
    }

    
    
    addIfFile(new File("/C:/Program Files/JavaMint/langtools/dist/lib/classes.jar"), jars);
    addIfFile(new File("/C:/Program Files/JavaMint/langtools/dist/lib/tools.jar"), jars);
    addIfFile(new File("/usr/local/soylatte/lib/classes.jar"), jars);
    addIfFile(new File("/usr/local/soylatte/lib/tools.jar"), jars);
    addIfFile(new File("/usr/local/JavaMint/langtools/dist/lib/classes.jar"), jars);
    addIfFile(new File("/usr/local/JavaMint/langtools/dist/lib/tools.jar"), jars);
    try {
      String mint_home = System.getenv("MINT_HOME");
      if (mint_home!=null) {
        JDKToolsLibrary.msg("MINT_HOME environment variable set to: "+mint_home);
        addIfFile(new File(new File(mint_home), "langtools/dist/lib/classes.jar"), jars);
        addIfFile(new File(new File(mint_home), "langtools/dist/lib/tools.jar"), jars);
      }
      else {
        JDKToolsLibrary.msg("MINT_HOME not set");
      }
    }
    catch(Exception e) {  }
    addIfFile(edu.rice.cs.util.FileOps.getDrJavaFile(), jars); 
    
    
    Map<FullVersion, Iterable<JarJDKToolsLibrary>> results = 
      new TreeMap<FullVersion, Iterable<JarJDKToolsLibrary>>();
    Map<FullVersion, Iterable<JarJDKToolsLibrary>> mintResults =
      new TreeMap<FullVersion, Iterable<JarJDKToolsLibrary>>();
    
    for (File jar : jars) {
      JarJDKToolsLibrary lib = makeFromFile(jar, model);
      if (lib.isValid()) {
        FullVersion v = lib.version();
        Map<FullVersion, Iterable<JarJDKToolsLibrary>> mapToAddTo = results;
        if ((v.vendor().equals(JavaVersion.VendorType.MINT)) ) { mapToAddTo = mintResults; }
        
        if (mapToAddTo.containsKey(v)) { mapToAddTo.put(v, IterUtil.compose(lib, mapToAddTo.get(v))); }
        else { mapToAddTo.put(v, IterUtil.singleton(lib)); }
      }
    }
    
    Iterable<JarJDKToolsLibrary> collapsed = IterUtil.reverse(IterUtil.collapse(results.values()));
    Iterable<JarJDKToolsLibrary> mintCollapsed = IterUtil.reverse(IterUtil.collapse(mintResults.values()));
    
    Map<FullVersion, Iterable<JarJDKToolsLibrary>> javaMintResults =
      new TreeMap<FullVersion, Iterable<JarJDKToolsLibrary>>();
    
    for(JarJDKToolsLibrary mintLib: mintCollapsed) {
      JDKToolsLibrary.msg("mintLib: "+mintLib.version());
      JDKToolsLibrary.msg("\t"+mintLib.location());
      FullVersion mintVersion = mintLib.version();
      JarJDKToolsLibrary found = null;
      
      for(JarJDKToolsLibrary javaLib: collapsed) {
        JDKToolsLibrary.msg("\texact? "+javaLib.version());
        FullVersion javaVersion = javaLib.version();
        if ((javaVersion.majorVersion().equals(mintVersion.majorVersion())) &&
            (javaVersion.maintenance()==mintVersion.maintenance()) &&
            (javaVersion.update()==mintVersion.update()) &&
            (javaVersion.release()==mintVersion.release())) {
          JDKToolsLibrary.msg("\t\tfound");
          found = javaLib;
          break;
        }
      }
      
      if (found==null) {
        for(JarJDKToolsLibrary javaLib: collapsed) {
          JDKToolsLibrary.msg("\tmajor? "+javaLib.version());
          FullVersion javaVersion = javaLib.version();
          if (javaVersion.majorVersion().equals(mintVersion.majorVersion())) {
            JDKToolsLibrary.msg("\t\tfound");
            found = javaLib;
            break;
          }
        }
      }
      
      if (found!=null) {
        JarJDKToolsLibrary lib = makeFromFile(mintLib.location(), model, found.bootClassPath());
        if (lib.isValid()) {
          JDKToolsLibrary.msg("\t==> "+lib.version());
          FullVersion v = lib.version();
          if (javaMintResults.containsKey(v)) { javaMintResults.put(v, IterUtil.compose(lib, javaMintResults.get(v))); }
          else { javaMintResults.put(v, IterUtil.singleton(lib)); }
        }
      }
    }
    JDKToolsLibrary.msg("Result:");
    Iterable<JarJDKToolsLibrary> result = IterUtil.
      compose(collapsed,IterUtil.reverse(IterUtil.collapse(javaMintResults.values())));
    for(JarJDKToolsLibrary lib: result) {
      JDKToolsLibrary.msg("Found library: "+lib);
    }
    return result;
  }
  
  
  private static void addIfDir(File f, Set<? super File> set) {
    f = IOUtil.attemptCanonicalFile(f);
    if (IOUtil.attemptIsDirectory(f)) { set.add(f); JDKToolsLibrary.msg("Dir added:     "+f); }
    else { JDKToolsLibrary.msg("Dir not added: "+f); }
  }
  
  
  private static void addIfFile(File f, Set<? super File> set) {
    f = IOUtil.attemptCanonicalFile(f);
    if (IOUtil.attemptIsFile(f)) { set.add(f); JDKToolsLibrary.msg("File added:     "+f); }
    else { JDKToolsLibrary.msg("File not added: "+f); }
  }
}
