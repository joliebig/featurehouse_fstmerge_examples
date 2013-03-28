

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


public class MintDescriptor implements JDKDescriptor {
  
  public String getName() {
    return "Mint";
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
        
        
        "com.sun.tools.javac",
        "com.sun.tools.javac.tree",
        "com.sun.tools.javac.comp",
        "com.sun.tools.javac.main",
        "edu.rice.cs.mint",
        "edu.rice.cs.mint.comp",
        "edu.rice.cs.mint.runtime",
        "edu.rice.cs.mint.runtime.mspTree"
    });
    return set;
  }

  
  public Iterable<File> getSearchDirectories() {
    return IterUtil.singleton(edu.rice.cs.util.FileOps.getDrJavaFile().getParentFile());
  }

  
  public Iterable<File> getSearchFiles() {
    Iterable<File> files = IterUtil.asIterable(new File[] {
      new File("/C:/Program Files/JavaPLT/JavaMint/langtools/dist/lib/classes.jar"),
        new File("/C:/Program Files/JavaPLT/JavaMint/langtools/dist/lib/tools.jar"),
        new File("/C:/Program Files/JavaMint/langtools/dist/lib/classes.jar"),
        new File("/C:/Program Files/JavaMint/langtools/dist/lib/tools.jar"),
        new File("/usr/local/soylatte/lib/classes.jar"),
        new File("/usr/local/soylatte/lib/tools.jar"),
        new File("/usr/local/JavaMint/langtools/dist/lib/classes.jar"),
        new File("/usr/local/JavaMint/langtools/dist/lib/tools.jar")
    });
    try {
      String mint_home = System.getenv("MINT_HOME");
      if (mint_home!=null) {
        
        files = IterUtil.compose(files, new File(new File(mint_home), "langtools/dist/lib/classes.jar"));
        files = IterUtil.compose(files, new File(new File(mint_home), "langtools/dist/lib/tools.jar"));
      }
      else {
        
      }
    }
    catch(Exception e) {  }
    
    
    files = IterUtil.compose(files, edu.rice.cs.util.FileOps.getDrJavaFile()); 
    return files;
  }
  
  
  public boolean isCompound() { return true; }
  
  
  public boolean containsCompiler(File f) {
    return Util.exists(f,
                       "edu/rice/cs/mint/comp/TransStaging.class",
                       "com/sun/source/tree/BracketExprTree.class",
                       "com/sun/source/tree/BracketStatTree.class",
                       "com/sun/source/tree/EscapeExprTree.class",
                       "com/sun/source/tree/EscapeStatTree.class");
  }
  
  
  public String getAdapterForCompiler() { return "edu.rice.cs.drjava.model.compiler.MintCompiler"; }

  
  public String getAdapterForDebugger() { return null; }
  
  
  public JavaVersion getMinimumMajorVersion() { return JavaVersion.JAVA_6; }
  
  
  public Iterable<File> getAdditionalCompilerFiles(File compiler) throws FileNotFoundException {
    return IterUtil.empty();
  }
  
  public String toString() { return getClass().getSimpleName()+" --> "+getAdapterForCompiler(); }
}
