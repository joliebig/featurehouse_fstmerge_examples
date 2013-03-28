

package edu.rice.cs.javalanglevels;

import java.io.File;
import edu.rice.cs.plt.iter.*;
import edu.rice.cs.plt.io.IOUtil;
import edu.rice.cs.plt.reflect.JavaVersion;

public class Options {
  
  private final JavaVersion _javaVersion;
  private final Iterable<? extends File> _bootClassPath;
  private final Iterable<? extends File> _classPath;
  
  public static final Options DEFAULT = new Options(JavaVersion.JAVA_5, EmptyIterable.<File>make());
  
  public Options(JavaVersion javaVersion, Iterable<? extends File> classPath) {
    this(javaVersion, classPath, IOUtil.parsePath(System.getProperty("sun.boot.class.path", "")));
  }
  
  public Options(JavaVersion javaVersion, Iterable<? extends File> classPath,
                 Iterable<? extends File> bootClassPath) {
    _javaVersion = javaVersion;
    _classPath = classPath;
    _bootClassPath = bootClassPath;
  }
  
  public JavaVersion javaVersion() { return _javaVersion; }
  public Iterable<? extends File> bootClassPath() { return _bootClassPath; }
  public Iterable<? extends File> classPath() { return _classPath; }
}
