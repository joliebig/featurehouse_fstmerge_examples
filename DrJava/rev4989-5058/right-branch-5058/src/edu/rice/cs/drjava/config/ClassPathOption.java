

package edu.rice.cs.drjava.config;

import java.io.File;
import java.util.Vector;

import edu.rice.cs.util.FileOps;


class ClassPathOption {
  
  private String warning =
    "WARNING: Configurability interface only supports path separators of at most one character";

  public VectorOption<File> evaluate(String optionName) {
    
    String ps = System.getProperty("path.separator");
    if (ps.length() > 1) {
      
      System.out.println(warning);
      System.out.println("using '" + ps.charAt(0) + "' for delimiter.");
    }
    FileOption fop = new FileOption("",FileOps.NULL_FILE);
    
    char delim = ps.charAt(0);
    return new VectorOption<File>(optionName,fop,"",delim,"",new Vector<File>());
  }
}
