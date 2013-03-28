

package edu.rice.cs.drjava.model.compiler;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Iterator;
import java.io.File;
import java.io.IOException;
import java.io.FilenameFilter;
import java.io.FileFilter;
import java.io.InputStream;
import java.io.FileOutputStream;
import edu.rice.cs.plt.io.IOUtil;
import edu.rice.cs.drjava.config.OptionConstants;
import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.model.DJError;
import edu.rice.cs.util.ArgumentTokenizer;
import edu.rice.cs.plt.reflect.JavaVersion;


public abstract class Javac160FilteringCompiler extends JavacCompiler {
  protected final boolean _filterExe;
  protected final File _tempJUnit;
  protected static final String PREFIX = "drjava-junit";
  protected static final String SUFFIX = ".jar";  
  
  protected Javac160FilteringCompiler(JavaVersion.FullVersion version,
                                      String location,
                                      List<? extends File> defaultBootClassPath) {
    super(version, location, defaultBootClassPath);

    _filterExe = version.compareTo(JavaVersion.parseFullVersion("1.6.0_04")) >= 0;
    File tempJUnit = null;
    if (_filterExe) {
      
      
      try {
        
        
        InputStream is = Javac160FilteringCompiler.class.getResourceAsStream("/junit.jar");
        if (is!=null) {
          
          tempJUnit = edu.rice.cs.plt.io.IOUtil.createAndMarkTempFile(PREFIX,SUFFIX);
          FileOutputStream fos = new FileOutputStream(tempJUnit);
          int size = edu.rice.cs.plt.io.IOUtil.copyInputStream(is,fos);
          
        }
        else {
          
          if (tempJUnit!=null) {
            tempJUnit.delete();
            tempJUnit = null;
          }
        }
      }
      catch(IOException ioe) {
        if (tempJUnit!=null) {
          tempJUnit.delete();
          tempJUnit = null;
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
    _tempJUnit = tempJUnit;
  }
  
  protected java.util.List<File> getFilteredClassPath(java.util.List<? extends File> classPath) {
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
    return filteredClassPath;
  }
}
