

package edu.rice.cs.drjava;

import static edu.rice.cs.drjava.config.OptionConstants.*;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;

import edu.rice.cs.drjava.config.FileConfiguration;
import edu.rice.cs.drjava.config.OptionConstants;
import edu.rice.cs.drjava.platform.PlatformFactory;
import edu.rice.cs.drjava.ui.DrJavaErrorHandler;
import edu.rice.cs.plt.concurrent.DelayedInterrupter;
import edu.rice.cs.plt.concurrent.JVMBuilder;
import edu.rice.cs.util.ArgumentTokenizer;
import edu.rice.cs.util.Log;
import edu.rice.cs.util.UnexpectedException;

import static edu.rice.cs.plt.debug.DebugUtil.debug;


public class DrJava {
  public static volatile Log _log = new Log("DrJava.txt", false);
  
  private static final String DEFAULT_MAX_HEAP_SIZE_ARG = "-Xmx128M";
  
  private static final ArrayList<String> _filesToOpen = new ArrayList<String>();
  private static final ArrayList<String> _jvmArgs = new ArrayList<String>();
  
  static volatile boolean _showDebugConsole = false;
  
  
  static volatile boolean _forceNewInstance = false;
  
  
  private static final int WAIT_BEFORE_DECLARING_SUCCESS = 5000;

  
  
  
  public static final File DEFAULT_PROPERTIES_FILE = new File(System.getProperty("user.home"), ".drjava");
  
  
  private static volatile File _propertiesFile = DEFAULT_PROPERTIES_FILE;
  
  
  private static volatile FileConfiguration _config = _initConfig();
  
  
  public static File getPropertiesFile() { return _propertiesFile; }
  
  
  public static FileConfiguration getConfig() { return _config; }
  
  
  public static String[] getFilesToOpen() { return _filesToOpen.toArray(new String[0]); }
  
  
  public static void addFileToOpen(String s) {
    _filesToOpen.add(s);
    boolean isProjectFile =
      s.endsWith(OptionConstants.PROJECT_FILE_EXTENSION) ||
      s.endsWith(OptionConstants.PROJECT_FILE_EXTENSION2) ||
      s.endsWith(OptionConstants.OLD_PROJECT_FILE_EXTENSION);
    _forceNewInstance |= isProjectFile;
  }
  
  
  public static boolean getShowDebugConsole() { return _showDebugConsole; }
  
  
  public static void main(final String[] args) {    
    
    if (handleCommandLineArgs(args)) {
      
      PlatformFactory.ONLY.beforeUISetup();
      

      configureAndLoadDrJavaRoot(args); 
    }
  }
  
  public static void configureAndLoadDrJavaRoot(String[] args) {
    try {
      
      
      if (!_forceNewInstance &&
          DrJava.getConfig().getSetting(edu.rice.cs.drjava.config.OptionConstants.REMOTE_CONTROL_ENABLED) &&
          (_filesToOpen.size() > 0)) {
        try {
          RemoteControlClient.openFile(null);
          if (RemoteControlClient.isServerRunning()) {
            
            for (int i = 0; i < _filesToOpen.size(); ++i) {
              RemoteControlClient.openFile(new File(_filesToOpen.get(i)));
            }
            
            System.exit(0);
          }
        }
        catch(IOException ioe) {
          ioe.printStackTrace();
        }      
      }
      
      
      
      
      
      int failCount = 0;
      while(failCount < 2) {
        
        String masterMemory = getConfig().getSetting(MASTER_JVM_XMX).trim();
        boolean restart = (getConfig().getSetting(MASTER_JVM_ARGS).length() > 0)
          || (!"".equals(masterMemory) && !OptionConstants.heapSizeChoices.get(0).equals(masterMemory));
        
        LinkedList<String> classArgs = new LinkedList<String>();
        classArgs.addAll(_filesToOpen);
        
        
        if (_showDebugConsole) { classArgs.addFirst("-debugConsole"); }
        
        if (! _propertiesFile.equals(DEFAULT_PROPERTIES_FILE)) {
          
          classArgs.addFirst(_propertiesFile.getAbsolutePath());
          classArgs.addFirst("-config");
        }
        
        if (restart) {
          
          
          try {
            boolean failed = false;
            Process p = JVMBuilder.DEFAULT.jvmArguments(_jvmArgs).start(DrJavaRoot.class.getName(), classArgs);
            DelayedInterrupter timeout = new DelayedInterrupter(WAIT_BEFORE_DECLARING_SUCCESS);
            try {
              int exitValue = p.waitFor();
              timeout.abort();
              failed = (exitValue != 0);
            }
            catch(InterruptedException e) {  }
            if (failed) {
              if (failCount > 0) {
                
                JOptionPane.showMessageDialog(null,
                                              "DrJava was unable to start, and resetting your configuration\n" + 
                                              "did not help. Please file a support request at\n" + 
                                              "https://sourceforge.net/projects/drjava/",
                                              "Could Not Start DrJava",
                                              JOptionPane.ERROR_MESSAGE);
                System.exit(0);
              }
              else {
                
                int result = JOptionPane.showConfirmDialog(null,
                                                           "DrJava was unable to start. Your configuration file (.drjava)\n" + 
                                                           "might be corrupt. Do you want to reset your configuration?",
                                                           "Could Not Start DrJava",
                                                           JOptionPane.YES_NO_OPTION);
                if (result != JOptionPane.YES_OPTION) { System.exit(0); }
                
                getConfig().resetToDefaults();
                getConfig().saveConfiguration();
                if (!handleCommandLineArgs(args)) { System.exit(0); }
                ++failCount;
                continue;
              }
            }
          }
          catch (IOException ioe) {
            
            final String[] text = {
              "DrJava was unable to load its compiler and debugger.  Would you ",
              "like to start DrJava without a compiler and debugger?", "\nReason: " + ioe.toString()
            };
            int result = JOptionPane.showConfirmDialog(null, text, "Could Not Load Compiler and Debugger",
                                                       JOptionPane.YES_NO_OPTION);
            if (result != JOptionPane.YES_OPTION) { System.exit(0); }
          }
        }
        
        else {
          
          DrJavaRoot.main(classArgs.toArray(new String[0]));
        }
        break;
      }
    }
    catch(Throwable t) {
      
      System.out.println(t.getClass().getName() + ": " + t.getMessage());
      t.printStackTrace(System.err);System.out.println("error thrown");
      DrJavaErrorHandler.record(t);
    }
  }
  
  
  static boolean handleCommandLineArgs(String[] args) {
    boolean heapSizeGiven = false;  
    
    
    int argIndex = 0;
    int len = args.length;
    _log.log("handleCommandLineArgs. _filesToOpen: " + _filesToOpen);
    _log.log("\t_filesToOpen cleared");
    _filesToOpen.clear();
    
    while(argIndex < len) {
      String arg = args[argIndex++];
      
      if (arg.equals("-config")) {
        if (len == argIndex) { 
          
          return true;
        }
        
        setPropertiesFile(args[argIndex++]);
        _config = _initConfig();  
      }
      
      else if (arg.startsWith("-X") || arg.startsWith("-D")) {
        if (arg.startsWith("-Xmx")) { heapSizeGiven = true; }
        _jvmArgs.add(arg); 
      }
      
      else if (arg.equals("-debugConsole")) _showDebugConsole = true;
      
      else if (arg.equals("-new")) _forceNewInstance = true;
      
      else if (arg.equals("-delete-after-restart")) {
        File deleteAfterRestart = new File(args[argIndex++]);
        deleteAfterRestart.delete();
      }
      
      else if (arg.equals("-help") || arg.equals("-?")) {
        displayUsage();
        return false;
      }
      else {
        
        --argIndex;
        break;
      }
    }
    
    if ((!("".equals(getConfig().getSetting(MASTER_JVM_XMX)))) &&
        (!(edu.rice.cs.drjava.config.OptionConstants.heapSizeChoices.get(0).equals(getConfig().getSetting(MASTER_JVM_XMX))))) { 
      _jvmArgs.add("-Xmx" + getConfig().getSetting(MASTER_JVM_XMX).trim() + "M");
      heapSizeGiven = true;
    }
    List<String> configArgs = ArgumentTokenizer.tokenize(getConfig().getSetting(MASTER_JVM_ARGS));
    for (String arg : configArgs) {
      if (arg.startsWith("-Xmx")) { heapSizeGiven = true; }
      _jvmArgs.add(arg);
    }
    
    if (PlatformFactory.ONLY.isMacPlatform()) {
      String iconLoc = System.getProperty("edu.rice.cs.drjava.icon");
      if (iconLoc != null) { 
        _jvmArgs.add("-Xdock:name=DrJava");
        _jvmArgs.add("-Xdock:icon=" + iconLoc);
      }
    }
    
    if (!heapSizeGiven) { _jvmArgs.add(DEFAULT_MAX_HEAP_SIZE_ARG); }
    
    _log.log("_jvmArgs = " + _jvmArgs);
    
    
    
    for (int i = argIndex; i < len; i++) { addFileToOpen(args[i]); }
    _log.log("\t _filesToOpen now contains: " + _filesToOpen);

    return true;
  }
  
  
  static void displayUsage() {
    System.out.println("Usage: java -jar drjava.jar [OPTIONS] [FILES]\n");
    System.out.println("where options include:");
    System.out.println("  -config [FILE]        use a custom config file");
    System.out.println("  -new                  force the creation of a new DrJava instance;");
    System.out.println("                        do not connect to existing instance");
    System.out.println("  -help | -?            print this help message");
    System.out.println("  -X<jvmOption>         specify a JVM configuration option for the master DrJava JVM");      
    System.out.println("  -D<name>[=<value>]    set a Java property for the master DrJava JVM");
  }
  
  
  static void setPropertiesFile(String fileName) {
    if (! fileName.endsWith(".java"))  _propertiesFile = new File(fileName);
  }
  
  
  static FileConfiguration _initConfig() throws IllegalStateException {


    
    FileConfiguration config;
    
    final File propFile = _propertiesFile;    
    
    try { propFile.createNewFile(); }         
    catch (IOException e) {  }
    
    config = new FileConfiguration(propFile);
    try { config.loadConfiguration(); }
    catch (Exception e) {
      
      config.storeStartupException(e);
    }
    _config = config; 
    return config;
  }
  
  
  protected static void _saveConfig() {
    try { getConfig().saveConfiguration(); }
    catch(IOException e) {
      JOptionPane.showMessageDialog(null, 
                                    "Could not save the location of tools.jar in \n" +
                                    "the '.drjava' file in your home directory. \n" +
                                    "Another process may be using the file.\n\n" + e,
                                    "Could Not Save Changes",
                                    JOptionPane.ERROR_MESSAGE);
      
      DrJavaErrorHandler.record(new UnexpectedException(e, "Could not save the location of tools.jar in \n" +
                                                        "the '.drjava' file in your home directory. \n" +
                                                        "Another process may be using the file.\n\n"));
    }
  }
  








































  
  
  public static void cleanUp() {
    _log.log("cleanUp. _filesToOpen: " + _filesToOpen);
    _filesToOpen.clear();
    _log.log("\t_filesToOpen cleared");
    _jvmArgs.clear();
    
  }

  
  public static boolean warnIfLinuxWithCompiz() {
    try {
      if (!System.getProperty("os.name").equals("Linux")) return false; 
      if (!DrJava.getConfig().getSetting(edu.rice.cs.drjava.config.OptionConstants.WARN_IF_COMPIZ)) return false; 
      
      
      File ps = new File("/bin/ps");
      
      
      ProcessBuilder pb = new ProcessBuilder(ps.getAbsolutePath(), "-A");
      Process psProc = pb.start();
      psProc.waitFor();
      
      
      BufferedReader br = new BufferedReader(new InputStreamReader(psProc.getInputStream()));
      boolean compiz = false;
      String line = null;
      while((line=br.readLine()) != null) {
        
        if ((line.endsWith("compiz")) ||
            (line.endsWith("compiz.real"))) {
          compiz = true;
          break;
        }
      }
      if (!compiz) return false; 
      
      String[] options = new String[] { "Yes", "Yes, and ignore from now on", "No" };
      int res = javax.swing.JOptionPane.showOptionDialog(null,
                                                         "<html>DrJava has detected that you are using Compiz.<br>" + 
                                                         "<br>" + 
                                                         "Compiz and Java Swing are currently incompatible and can cause<br>" + 
                                                         "DrJava or your computer to crash.<br>" + 
                                                         "<br>" + 
                                                         "We recommend that you <b>disable Compiz</b>. On Ubuntu, go to<br>" + 
                                                         "System->Preferences->Appearence, display the Visual Effects tab,<br>" + 
                                                         "and select 'None'.<br>" + 
                                                         "<br>" + 
                                                         "For more information, please go to http://drjava.org/compiz<br>" + 
                                                         "<br>" + 
                                                         "Do you want to start DrJava anyway?</html>",
                                                         "Compiz detected",
                                                         JOptionPane.DEFAULT_OPTION,
                                                         javax.swing.JOptionPane.WARNING_MESSAGE,
                                                         null,
                                                         options,
                                                         options[0]);
      switch(res) {
        case 1:
          
          DrJava.getConfig().setSetting(edu.rice.cs.drjava.config.OptionConstants.WARN_IF_COMPIZ, false);
          break;
        case 2:
          System.exit(0);
          break;
      }
      return compiz;
    }
    catch(IOException ioe) {
      return false; 
    }
    catch(InterruptedException ie) {
      return false; 
    }
  }
}
