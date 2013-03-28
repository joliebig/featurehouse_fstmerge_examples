

package edu.rice.cs.drjava.model.junit;

import edu.rice.cs.drjava.config.OptionConstants;
import edu.rice.cs.util.swing.ScrollableDialog;
import junit.runner.*;


public class DrJavaTestSuiteLoader implements TestSuiteLoader, OptionConstants {
  
  private final JUnitModelCallback _jmc;
  private TestCaseClassLoader _loader;
  
  public DrJavaTestSuiteLoader(JUnitModelCallback jmc) {
    _jmc = jmc;
    String classPath = _jmc.getClassPath().toString();
    classPath += System.getProperty("path.separator");
    classPath += System.getProperty("java.class.path");
    _loader = new DrJavaTestCaseClassLoader(classPath);
  }

  public Class<?> load(String className) throws ClassNotFoundException {
    return _loader.loadClass(className, true);
  }

  public Class<?> reload(Class c) throws ClassNotFoundException {
    return load(c.getName());
  }
}
