

package edu.rice.cs.util.classloader;

import edu.rice.cs.drjava.DrJavaTestCase;


public class ToolsJarClassLoaderTest extends DrJavaTestCase {
  
  public void testWindowsSDKDirectory() throws Throwable {
    String javahome1 = "C:\\Program Files\\Java\\j2re1.4.0_01";
    String javahome2 = "C:\\Program Files\\Java\\j2re1.4.1";
    String javahome3 = "C:\\Program Files\\JavaSoft\\JRE\\1.3.1_04";

    assertEquals("new versions of Windows J2SDK (1)",
                 "C:\\j2sdk1.4.0_01\\lib\\tools.jar",
                 ToolsJarClassLoader.getWindowsToolsJar(javahome1));

    assertEquals("new versions of Windows J2SDK (2)",
                 "C:\\j2sdk1.4.1\\lib\\tools.jar",
                 ToolsJarClassLoader.getWindowsToolsJar(javahome2));

    assertEquals("old versions of Windows J2SDK",
                 "C:\\jdk1.3.1_04\\lib\\tools.jar",
                 ToolsJarClassLoader.getWindowsToolsJar(javahome3));
  }

}
