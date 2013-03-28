

package edu.rice.cs.util.newjvm;

import edu.rice.cs.drjava.config.FileOption;
import edu.rice.cs.util.swing.Utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;


public final class ExecJVM {
  private static final String PATH_SEPARATOR = System.getProperty("path.separator");
  private static final String OS_NAME = System.getProperty("os.name").toLowerCase(Locale.US);

  private ExecJVM() { }

  
  public static Process runJVM(String mainClass, String[] classParams, String[] classPath, String[] jvmParams, File workDir)
    throws IOException {

    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < classPath.length; i++) {
      if (i != 0) buf.append(PATH_SEPARATOR);

      buf.append(classPath[i]);
    }

    return runJVM(mainClass, classParams, buf.toString(), jvmParams, workDir);
  }

  

  
  public static Process runJVM(String mainClass, String[] classParams, String classPath, String[] jvmParams, File workDir)
    throws IOException {

    LinkedList<String> args = new LinkedList<String>();
    args.add("-classpath");
    args.add(edu.rice.cs.util.FileOps.convertToAbsolutePathEntries(classPath));
    _addArray(args, jvmParams);
    String[] jvmWithCP = args.toArray(new String[args.size()]);

    return _runJVM(mainClass, classParams, jvmWithCP, workDir);
  }

  
  public static Process runJVMPropagateClassPath(String mainClass, String[] classParams, String[] jvmParams, File workDir)
    throws IOException {
    String cp = System.getProperty("java.class.path");
    return runJVM(mainClass, classParams, cp, jvmParams, workDir);
  }

  
  public static Process runJVMPropagateClassPath(String mainClass, String[] classParams, File workDir)
    throws IOException {
    return runJVMPropagateClassPath(mainClass, classParams, new String[0], workDir);
  }

  
  private static Process _runJVM(String mainClass, String[] classParams, String[] jvmParams, File workDir) throws IOException {
    LinkedList<String> args = new LinkedList<String>();
    args.add(_getExecutable());
    _addArray(args, jvmParams);
    args.add(mainClass);
    _addArray(args, classParams);

    String[] argArray = args.toArray(new String[args.size()]);

    
    Process p;
    if ((workDir != null) && (workDir != FileOption.NULL_FILE)) {
      
      if (workDir.exists()) p = Runtime.getRuntime().exec(argArray, null, workDir);
      else {
        Utilities.showMessageBox("Working directory does not exist:\n" + workDir +
                                                        "\nThe setting will be ignored. Press OK to continue.",
                                                        "Configuration Error");
        p = Runtime.getRuntime().exec(argArray);
      }
    }
    else {
      
      p = Runtime.getRuntime().exec(argArray);
    }
    return p;
  }

  
  public static void ventBuffers(Process theProc, LinkedList<String> outLines,
                                 LinkedList<String> errLines) throws IOException {
    
    BufferedReader outBuf = new BufferedReader(new InputStreamReader(theProc.getInputStream()));
    BufferedReader errBuf = new BufferedReader(new InputStreamReader(theProc.getErrorStream()));
    String output;

    if (outBuf.ready()) {
      output = outBuf.readLine();

      while (output != null) {
        
        outLines.add(output);
        if (outBuf.ready()) output = outBuf.readLine();
        else output = null;
      }
    }
    outBuf.close();

    if (errBuf.ready()) {
      output = errBuf.readLine();
      while (output != null) {
        
        errLines.add(output);
        if (errBuf.ready()) {
          output = errBuf.readLine();
        }
        else {
          output = null;
        }
      }
    }
    errBuf.close();
  }

  
  public static void printOutput(Process theProc, String msg, String sourceName)
    throws IOException {
    
    System.out.println(msg);

    LinkedList<String> outLines = new LinkedList<String>();
    LinkedList<String> errLines = new LinkedList<String>();

    ventBuffers(theProc, outLines, errLines);

    Iterator<String> it = outLines.iterator();
    String output;
    while (it.hasNext()) {
      output = it.next();
      System.out.println("    [" +sourceName + " stdout]: " + output);
    }

    it = errLines.iterator();
    while (it.hasNext()) {
      output = it.next();
      System.out.println("    [" +sourceName + " stderr]: " + output);
    }
  }

  private static void _addArray(LinkedList<String> list, String[] array) {
    if (array != null) {
      for (int i = 0; i < array.length; i++) {
        list.add(array[i]);
      }
    }
  }

  
  private static boolean _isDOS() {
    return PATH_SEPARATOR.equals(";");
  }

  private static boolean _isNetware() {
    return OS_NAME.indexOf("netware") != -1;
  }

  
  private static String _getExecutable() {
    
    if (_isNetware()) return "java";

    File executable;

    String java_home = System.getProperty("java.home") + "/";

    String[] candidates = { java_home + "../bin/java", java_home + "bin/java", java_home + "java", };

    
    for (int i = 0; i < candidates.length; i++) {
      String current = candidates[i];

      
      if (_isDOS()) {
        executable = new File(current + "w.exe");
        if (! executable.exists())  executable = new File(current + ".exe");
      }
      else executable = new File(current);

      

      if (executable.exists()) {
        
        return executable.getAbsolutePath();
      }
    }

    
    
    return "java";
  }
}

