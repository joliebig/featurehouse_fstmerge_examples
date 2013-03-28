

package edu.rice.cs.drjava.model.compiler.descriptors;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import java.util.jar.JarFile;
import edu.rice.cs.plt.reflect.JavaVersion;
import edu.rice.cs.plt.iter.IterUtil;


public class NextGenDescriptor implements JDKDescriptor {
  
  public String getName() {
    return "NextGen";
  }
  
  
  public Set<String> getToolsPackages() {
    HashSet<String> set = new HashSet<String>();
    Collections.addAll(set, new String[] {
      
      "com.sun.codemodel",
        "com.sun.istack.internal.tools", 
        "com.sun.istack.internal.ws",
        "com.sun.source",
        "com.sun.xml.internal.dtdparser", 
        "com.sun.xml.internal.rngom",
        "com.sun.xml.internal.xsom",
        "org.relaxng",
        
        
        "edu.rice.cs.nextgen2" 
    });
    return set;
  }

  
  public Iterable<File> getSearchDirectories() {
    return IterUtil.singleton(edu.rice.cs.util.FileOps.getDrJavaFile().getParentFile());
  }

  
  public Iterable<File> getSearchFiles() {
    Iterable<File> files = IterUtil.asIterable(new File[] {
      new File("/C:/Program Files/JavaPLT/nextgen2/nextgen2.jar"),
        new File("/C:/Program Files/JavaPLT/nextgen2/jars/nextgen2.jar"),
        new File("/C:/Program Files/JavaPLT/nextgen2/nextgen2.jar"),
        new File("/C:/Program Files/JavaPLT/nextgen2/jars/nextgen2.jar"),
        new File("/usr/local/nextgen2/nextgen2.jar"),
        new File("/usr/local/nextgen2/jars/nextgen2.jar")



    });
    try {
      String ngc_home = System.getenv("NGC_HOME");
      if (ngc_home!=null) {
        
        files = IterUtil.compose(files, new File(new File(ngc_home), "jars/nextgen2.jar"));
        files = IterUtil.compose(files, new File(new File(ngc_home), "nextgen2.jar"));
      }
      else {
        
      }
    }
    catch(Exception e) {  }
    
    
    files = IterUtil.compose(files, edu.rice.cs.util.FileOps.getDrJavaFile()); 
    return files;
  }
  
  
  public boolean isCompound() { return true; }
  
  
  public String getAdapterForCompiler() { return "edu.rice.cs.drjava.model.compiler.NextGenCompiler"; }

  
  public String getAdapterForDebugger() { return null; }
  
  
  public boolean containsCompiler(File f) {
    return Util.exists(f,
                       "edu/rice/cs/nextgen2/classloader/Runner.class",
                       "edu/rice/cs/nextgen2/compiler/Main.class");
  }
  
  
  public JavaVersion getMinimumMajorVersion() { return JavaVersion.JAVA_5; }
  
  
  public Iterable<File> getAdditionalCompilerFiles(File compiler) throws FileNotFoundException {
    return IterUtil.empty();
  }

  public String toString() { return getClass().getSimpleName()+" --> "+getAdapterForCompiler(); }  
}
