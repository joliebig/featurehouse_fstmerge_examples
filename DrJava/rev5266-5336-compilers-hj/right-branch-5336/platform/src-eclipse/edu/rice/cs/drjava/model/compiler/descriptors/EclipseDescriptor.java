

package edu.rice.cs.drjava.model.compiler.descriptors;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import edu.rice.cs.plt.reflect.JavaVersion;
import edu.rice.cs.plt.iter.IterUtil;


public class EclipseDescriptor implements JDKDescriptor {
  public String getName() {
    return "Eclipse";
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
        "org.relaxng"
    });
    return set;
  }

  
  public Iterable<File> getSearchDirectories() {
    return IterUtil.empty(); 
  }

  
  public Iterable<File> getSearchFiles() {
    
    return IterUtil.singleton(edu.rice.cs.util.FileOps.getDrJavaFile()); 
  }
  
  
  public boolean isCompound() {
    
    return false;
  }
  
  
  public boolean containsCompiler(File f) {
    return Util.exists(f, "org/eclipse/jdt/internal/compiler/tool/EclipseCompiler.class");
  }
  
  
  public String getAdapterForCompiler() { return "edu.rice.cs.drjava.model.compiler.EclipseCompiler"; }

  
  public String getAdapterForDebugger() { return null; }
  
  
  public JavaVersion getMinimumMajorVersion() { return JavaVersion.JAVA_6; }

  
  public Iterable<File> getAdditionalCompilerFiles(File compiler) throws FileNotFoundException {
    return IterUtil.empty();
  }

  public String toString() { return getClass().getSimpleName()+" --> "+getAdapterForCompiler(); }
}
