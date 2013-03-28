

package edu.rice.cs.drjava.model;

import java.io.File;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;
import java.util.jar.Manifest;
import java.util.Enumeration;
import java.io.IOException;
import java.io.FileNotFoundException;

import edu.rice.cs.plt.io.IOUtil;
import edu.rice.cs.plt.iter.IterUtil;
import edu.rice.cs.plt.lambda.Lambda;
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
import edu.rice.cs.drjava.model.compiler.descriptors.JDKDescriptor;


public class JarJDKToolsLibrary extends JDKToolsLibrary {
  
  
  private static final Set<String> TOOLS_PACKAGES = new HashSet<String>();
  static {
    Collections.addAll(TOOLS_PACKAGES, new String[] {
      
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
    });
  }

  
  private final File _location;
  private final List<File> _bootClassPath; 
  
  private JarJDKToolsLibrary(File location, FullVersion version, JDKDescriptor jdkDescriptor,
                             CompilerInterface compiler, Debugger debugger,
                             JavadocModel javadoc, List<File> bootClassPath) {
    super(version, jdkDescriptor, compiler, debugger, javadoc);
    _location = location;
    _bootClassPath = bootClassPath;
  }
  
  public File location() { return _location; }
  public List<File> bootClassPath() { 
    if (_bootClassPath!=null) return new ArrayList<File>(_bootClassPath);
    else return null;
  }
  
  public String toString() {
    return super.toString() + " at " + _location + ", boot classpath: " + bootClassPath();
  }

  
  public static JarJDKToolsLibrary makeFromFile(File f, GlobalModel model, JDKDescriptor desc) {
    return makeFromFile(f, model, desc, new ArrayList<File>());
  }

  
  public static JarJDKToolsLibrary makeFromFile(File f, GlobalModel model, JDKDescriptor desc,
                                                List<File> additionalBootClassPath) {
    CompilerInterface compiler = NoCompilerAvailable.ONLY;
    Debugger debugger = NoDebuggerAvailable.ONLY;
    JavadocModel javadoc = new NoJavadocAvailable(model);
    
    FullVersion version = guessVersion(f, desc);
    JDKToolsLibrary.msg("makeFromFile: "+f+" --> "+version+", vendor: "+version.vendor());
    JDKToolsLibrary.msg("\tdesc = "+desc);
    
    boolean isSupported = JavaVersion.CURRENT.supports(version.majorVersion());
    Iterable<File> additionalCompilerFiles = IterUtil.empty();
    if (desc!=null) {
      isSupported |= JavaVersion.CURRENT.supports(desc.getMinimumMajorVersion());
      try {
        additionalCompilerFiles = desc.getAdditionalCompilerFiles(f);
      }
      catch(FileNotFoundException fnfe) {
        
        isSupported = false;
      }
    }
    
    
    List<File> bootClassPath = null;
    if (isSupported) {
      
      ClassLoader loader =
        new ShadowingClassLoader(JarJDKToolsLibrary.class.getClassLoader(), true, TOOLS_PACKAGES, true);
      Iterable<File> path = IterUtil.map(IterUtil.compose(additionalCompilerFiles, f), new Lambda<File,File>() {
        public File value(File arg) { return IOUtil.attemptAbsoluteFile(arg); }
      });
      
      String compilerAdapter = adapterForCompiler(version);
      if (desc!=null) {
        compilerAdapter = desc.getAdapterForCompiler();
      }
      
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
        bootClassPath = new ArrayList<File>();
        if (libDir != null) {
          File[] jars = IOUtil.attemptListFiles(libDir, IOUtil.extensionFilePredicate("jar"));
          if (jars != null) { bootClassPath.addAll(Arrays.asList(jars)); }
        }
        else {
          
          
          bootClassPath.add(f);
          for(File acf: additionalCompilerFiles) { bootClassPath.add(acf); };
        }
        if (additionalBootClassPath!=null) { bootClassPath.addAll(additionalBootClassPath); }
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
      if (desc!=null) {
        debuggerAdapter = desc.getAdapterForDebugger();
      }
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
    
    return new JarJDKToolsLibrary(f, version, desc, compiler, debugger, javadoc, bootClassPath);
  }
  
  public static FullVersion guessVersion(File f, JDKDescriptor desc) {
    FullVersion result = null;
    
    boolean forceUnknown = (desc!=null) && desc.isCompound();
    
    
    File current = IOUtil.attemptCanonicalFile(f);
    String parsedVersion = "";
    String vendor = "";
    do {
      String name = current.getName();
      String path = current.getAbsolutePath();
      if (!forceUnknown) {
        if (path.startsWith("/System/Library/Frameworks/JavaVM.framework")) vendor = "apple";
        else if (path.toLowerCase().contains("openjdk")) vendor = "openjdk";
        else if (path.toLowerCase().contains("sun")) vendor = "sun";
      }
      if (name.startsWith("jdk-")) {
        result = JavaVersion.parseFullVersion(parsedVersion = name.substring(4),vendor,vendor,f);
      }
      else if (name.startsWith("jdk")) {
        result = JavaVersion.parseFullVersion(parsedVersion = name.substring(3),vendor,vendor,f);
      }
      else if (name.startsWith("j2sdk")) {
        result = JavaVersion.parseFullVersion(parsedVersion = name.substring(5),vendor,vendor,f);
      }
      else if (name.matches("\\d+\\.\\d+\\.\\d+")) {
        result = JavaVersion.parseFullVersion(parsedVersion = name,vendor,vendor,f);
      }
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
            result = JavaVersion.parseFullVersion(parsedVersion = v,vendor,vendor,f);
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
    
    if ((result == null) || (result.vendor()==JavaVersion.VendorType.UNKNOWN)) {
      if (!forceUnknown) {
        if (result.majorVersion().compareTo(JavaVersion.JAVA_6)<0) {
          
          vendor = "sun";
        }
        else {
          
          JarFile jf = null;
          try {
            jf = new JarFile(f);
             if (jf.getJarEntry("com/sun/tools/javac/util/JavacFileManager.class")!=null) {
               vendor = "openjdk";
             }
             else if (jf.getJarEntry("com/sun/tools/javac/util/DefaultFileManager.class")!=null) {
               vendor = "sun";
             }
          }
          catch(IOException ioe) {  }
          finally {
            try {
              if (jf != null) jf.close();
            }
            catch(IOException ioe) {  }
          }
        }
      }
      result = JavaVersion.parseFullVersion(parsedVersion,vendor,vendor,f);
    }
    return result;
  }
  
  
  protected static LinkedHashMap<File,Set<JDKDescriptor>> getDefaultSearchRoots() {
    
    LinkedHashMap<File,Set<JDKDescriptor>> roots = new LinkedHashMap<File,Set<JDKDescriptor>>();
    
    String javaHome = System.getProperty("java.home");
    String envJavaHome = null;
    String programFiles = null;
    String systemDrive = null;
    if (JavaVersion.CURRENT.supports(JavaVersion.JAVA_5)) {
      
      
      envJavaHome = System.getenv("JAVA_HOME");
      programFiles = System.getenv("ProgramFiles");
      systemDrive = System.getenv("SystemDrive");
    }
   
    if (javaHome != null) {
      addIfDir(new File(javaHome), null, roots);
      addIfDir(new File(javaHome, ".."), null, roots);
      addIfDir(new File(javaHome, "../.."), null, roots);
    }
    if (envJavaHome != null) {
      addIfDir(new File(envJavaHome), null, roots);
      addIfDir(new File(envJavaHome, ".."), null, roots);
      addIfDir(new File(envJavaHome, "../.."), null, roots);
    }
    
    if (programFiles != null) {
      addIfDir(new File(programFiles, "Java"), null, roots);
      addIfDir(new File(programFiles), null, roots);
    }
    addIfDir(new File("/C:/Program Files/Java"), null, roots);
    addIfDir(new File("/C:/Program Files"), null, roots);
    if (systemDrive != null) {
      addIfDir(new File(systemDrive, "Java"), null, roots);
      addIfDir(new File(systemDrive), null, roots);
    }
    addIfDir(new File("/C:/Java"), null, roots);
    addIfDir(new File("/C:"), null, roots);
    
    addIfDir(new File("/System/Library/Frameworks/JavaVM.framework/Versions"), null, roots);

    addIfDir(new File("/usr/java"), null, roots);
    addIfDir(new File("/usr/j2se"), null, roots);
    addIfDir(new File("/usr"), null, roots);
    addIfDir(new File("/usr/local/java"), null, roots);
    addIfDir(new File("/usr/local/j2se"), null, roots);
    addIfDir(new File("/usr/local"), null, roots);

    
    addIfDir(new File("/usr/lib/jvm"), null, roots);
    addIfDir(new File("/usr/lib/jvm/java-6-sun"), null, roots);
    addIfDir(new File("/usr/lib/jvm/java-1.5.0-sun"), null, roots);
    addIfDir(new File("/usr/lib/jvm/java-6-openjdk"), null, roots);

    addIfDir(new File("/home/javaplt/java/Linux-i686"), null, roots);
    
    return roots;
  }
  
  
  protected static void searchRootsForJars(LinkedHashMap<File,Set<JDKDescriptor>> roots,
                                           LinkedHashMap<File,Set<JDKDescriptor>> jars) {
    
    
    Predicate<File> subdirFilter = LambdaUtil.or(IOUtil.regexCanonicalCaseFilePredicate("j2sdk.*"),
                                                 IOUtil.regexCanonicalCaseFilePredicate("jdk.*"),
                                                 LambdaUtil.or(IOUtil.regexCanonicalCaseFilePredicate("\\d+\\.\\d+\\.\\d+"),
                                                               IOUtil.regexCanonicalCaseFilePredicate("java.*")));
    for (Map.Entry<File,Set<JDKDescriptor>> root : roots.entrySet()) {
      for (File subdir : IOUtil.attemptListFilesAsIterable(root.getKey(), subdirFilter)) {
        addIfFile(new File(subdir, "lib/tools.jar"), root.getValue(), jars);
        addIfFile(new File(subdir, "Classes/classes.jar"), root.getValue(), jars);
      }
    }
  }
  
  
  protected static void collectValidResults(GlobalModel model,
                                            LinkedHashMap<File,Set<JDKDescriptor>> jars,
                                            Map<FullVersion, Iterable<JarJDKToolsLibrary>> results,
                                            Map<FullVersion, Iterable<JarJDKToolsLibrary>> compoundResults) {
    for (Map.Entry<File,Set<JDKDescriptor>> jar : jars.entrySet()) {
      for (JDKDescriptor desc : jar.getValue()) {
        if (desc!=null) {
          boolean containsCompiler = desc.containsCompiler(jar.getKey());
          JDKToolsLibrary.msg("Checking file "+jar.getKey()+" for "+desc);
          JDKToolsLibrary.msg("\t"+containsCompiler);
          if (!containsCompiler) continue;
        }
        JarJDKToolsLibrary lib = makeFromFile(jar.getKey(), model, desc);
        if (lib.isValid()) {
          FullVersion v = lib.version();
          Map<FullVersion, Iterable<JarJDKToolsLibrary>> mapToAddTo = results;
          if ((desc!=null)&&(desc.isCompound())) { mapToAddTo = compoundResults; }
          
          if (mapToAddTo.containsKey(v)) { mapToAddTo.put(v, IterUtil.compose(lib, mapToAddTo.get(v))); }
          else { mapToAddTo.put(v, IterUtil.singleton(lib)); }
        }
        else {
          JDKToolsLibrary.msg("\tlibrary is not valid: compiler="+lib.compiler().isAvailable()+
                              " debugger="+lib.debugger().isAvailable()+" javadoc="+lib.javadoc().isAvailable());
        }
      }
    }
  }
  
  
  protected static Map<FullVersion, Iterable<JarJDKToolsLibrary>>
    getCompletedCompoundResults(GlobalModel model,
                                Iterable<JarJDKToolsLibrary> collapsed,
                                Iterable<JarJDKToolsLibrary> compoundCollapsed) {
    Map<FullVersion, Iterable<JarJDKToolsLibrary>> completedResults =
      new TreeMap<FullVersion, Iterable<JarJDKToolsLibrary>>();
    
    
    for(JarJDKToolsLibrary compoundLib: compoundCollapsed) {
      JDKToolsLibrary.msg("compoundLib: "+compoundLib.version());
      JDKToolsLibrary.msg("\t"+compoundLib.location());
      FullVersion compoundVersion = compoundLib.version();
      JarJDKToolsLibrary found = null;
      
      for(JarJDKToolsLibrary javaLib: collapsed) {
        JDKToolsLibrary.msg("\texact? "+javaLib.version());
        FullVersion javaVersion = javaLib.version();
        if ((javaVersion.majorVersion().equals(compoundVersion.majorVersion())) &&
            (javaVersion.maintenance()==compoundVersion.maintenance()) &&
            (javaVersion.update()==compoundVersion.update()) &&
            (javaVersion.release()==compoundVersion.release()) &&
            (javaVersion.supports(compoundLib.jdkDescriptor().getMinimumMajorVersion()))) {
          JDKToolsLibrary.msg("\t\tfound");
          found = javaLib;
          break;
        }
      }
      
      if (found==null) {
        for(JarJDKToolsLibrary javaLib: collapsed) {
          JDKToolsLibrary.msg("\tmajor? "+javaLib.version());
          FullVersion javaVersion = javaLib.version();
          if (javaVersion.majorVersion().equals(compoundVersion.majorVersion()) &&
              javaVersion.supports(compoundLib.jdkDescriptor().getMinimumMajorVersion())) {
            JDKToolsLibrary.msg("\t\tfound");
            found = javaLib;
            break;
          }
        }
      }
      
      if (found!=null) {
        JarJDKToolsLibrary lib = makeFromFile(compoundLib.location(), model, compoundLib.jdkDescriptor(),
                                              found.bootClassPath());
        if (lib.isValid()) {
          JDKToolsLibrary.msg("\t==> "+lib.version());
          FullVersion v = lib.version();
          if (completedResults.containsKey(v)) {
            completedResults.put(v, IterUtil.compose(lib, completedResults.get(v)));
          }
          else {
            completedResults.put(v, IterUtil.singleton(lib));
          }
        }
      }
    }
    return completedResults;
  }
  
  
  public static Iterable<JarJDKToolsLibrary> search(GlobalModel model) {
    
    LinkedHashMap<File,Set<JDKDescriptor>> roots = getDefaultSearchRoots();

    
    LinkedHashMap<File,Set<JDKDescriptor>> jars = new LinkedHashMap<File,Set<JDKDescriptor>>();

    
    Iterable<JDKDescriptor> descriptors = searchForJDKDescriptors(); 
    for(JDKDescriptor desc: descriptors) {
      
      for(File f: desc.getSearchDirectories()) { addIfDir(f, desc, roots); }
      for(File f: desc.getSearchFiles()) { addIfFile(f, desc, jars); }
      
      TOOLS_PACKAGES.addAll(desc.getToolsPackages());
    }
    
    
    searchRootsForJars(roots, jars);

    
    Map<FullVersion, Iterable<JarJDKToolsLibrary>> results = 
      new TreeMap<FullVersion, Iterable<JarJDKToolsLibrary>>();
    Map<FullVersion, Iterable<JarJDKToolsLibrary>> compoundResults =
      new TreeMap<FullVersion, Iterable<JarJDKToolsLibrary>>();
    
    collectValidResults(model, jars, results, compoundResults);
    
    
    Iterable<JarJDKToolsLibrary> collapsed = IterUtil.reverse(IterUtil.collapse(results.values()));
    Iterable<JarJDKToolsLibrary> compoundCollapsed = IterUtil.reverse(IterUtil.collapse(compoundResults.values()));
    
    
    
    Map<FullVersion, Iterable<JarJDKToolsLibrary>> completedResults =
      getCompletedCompoundResults(model, collapsed, compoundCollapsed);
    
    JDKToolsLibrary.msg("Result:");
    Iterable<JarJDKToolsLibrary> result = IterUtil.
      compose(collapsed,IterUtil.reverse(IterUtil.collapse(completedResults.values())));
    for(JarJDKToolsLibrary lib: result) {
      JDKToolsLibrary.msg("Found library: "+lib);
    }
    
    return result;
  }
  
  
  private static void addIfDir(File f, JDKDescriptor c, Map<? super File, Set<JDKDescriptor>> map) {
    f = IOUtil.attemptCanonicalFile(f);
    if (IOUtil.attemptIsDirectory(f)) {
      Set<JDKDescriptor> set = map.get(f);
      if (set==null) {
        set = new LinkedHashSet<JDKDescriptor>();
        map.put(f, set);
      }
      set.add(c);
      JDKToolsLibrary.msg("Dir added:     "+f);
    }
    else { JDKToolsLibrary.msg("Dir not added: "+f); }
  }
  
  
  private static void addIfFile(File f, JDKDescriptor c, Map<? super File,Set<JDKDescriptor>> map) {
    addIfFile(f, Collections.singleton(c), map);
  }

  
  private static void addIfFile(File f, Set<JDKDescriptor> cs,
                                Map<? super File,Set<JDKDescriptor>> map) {
    f = IOUtil.attemptCanonicalFile(f);
    if (IOUtil.attemptIsFile(f)) {
      Set<JDKDescriptor> set = map.get(f);
      if (set==null) {
        set = new LinkedHashSet<JDKDescriptor>();
        map.put(f, set);
      }
      set.addAll(cs);
      JDKToolsLibrary.msg("File added:     "+f);
    }
    else { JDKToolsLibrary.msg("File not added: "+f); }
  }
  
  public static Iterable<JDKDescriptor> searchForJDKDescriptors() {
    JDKToolsLibrary.msg("---- Searching for descriptors ----");
    long t0 = System.currentTimeMillis();
    JDKToolsLibrary.msg("ms: "+t0);
    Iterable<JDKDescriptor> descriptors = IterUtil.empty();
    try {
      File f = edu.rice.cs.util.FileOps.getDrJavaFile();
      JDKToolsLibrary.msg("drjava.jar: "+f);
      JarFile jf = new JarFile(f);
      JDKToolsLibrary.msg("jar file: "+jf);
      Enumeration<JarEntry> entries = jf.entries();
      while(entries.hasMoreElements()) {
        JarEntry je = entries.nextElement();
        String name = je.getName();
        if (name.startsWith("edu/rice/cs/drjava/model/compiler/descriptors/") &&
            name.endsWith(".class") &&
            !name.equals("edu/rice/cs/drjava/model/compiler/descriptors/JDKDescriptor.class") &&
            (name.indexOf('$')<0)) {
          int dotPos = name.indexOf(".class");
          String className = name.substring(0, dotPos).replace('/','.');
          try {
            JDKToolsLibrary.msg("\tclass name: "+className);
            Class<?> clazz = Class.forName(className);
            Class<? extends JDKDescriptor> descClass = clazz.asSubclass(JDKDescriptor.class);
            JDKDescriptor desc = descClass.newInstance();
            JDKToolsLibrary.msg("\t\tloaded!");
            descriptors = IterUtil.compose(descriptors, desc);
          }
          catch(LinkageError le) { JDKToolsLibrary.msg("LinkageError: "+le);  } 
          catch(ClassNotFoundException cnfe) { JDKToolsLibrary.msg("ClassNotFoundException: "+cnfe);  }
          catch(ClassCastException cce) { JDKToolsLibrary.msg("ClassCastException: "+cce);  }
          catch(IllegalAccessException iae) { JDKToolsLibrary.msg("IllegalAccessException: "+iae);  }
          catch(InstantiationException ie) { JDKToolsLibrary.msg("InstantiationException: "+ie);  }
        }
     }
    }
    catch(IOException ioe) {
      
    }
    long t1 = System.currentTimeMillis();
    JDKToolsLibrary.msg("ms: "+t1);
    JDKToolsLibrary.msg("duration ms: "+(t1-t0));
    JDKToolsLibrary.msg("---- Done searching for descriptors ----");
    return descriptors;
  }
}
