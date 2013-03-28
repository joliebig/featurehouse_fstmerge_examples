

package edu.rice.cs.drjava;

import static edu.rice.cs.drjava.config.OptionConstants.*;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;

import edu.rice.cs.drjava.config.ResourceBundleConfiguration;
import edu.rice.cs.drjava.config.FileConfiguration;
import edu.rice.cs.drjava.config.OptionConstants;
import edu.rice.cs.drjava.platform.PlatformFactory;
import edu.rice.cs.drjava.ui.DrJavaErrorHandler;
import edu.rice.cs.plt.concurrent.DelayedInterrupter;
import edu.rice.cs.plt.concurrent.JVMBuilder;
import edu.rice.cs.util.ArgumentTokenizer;
import edu.rice.cs.util.Log;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.FileOps;
import edu.rice.cs.drjava.model.DrJavaFileUtils;

import static edu.rice.cs.plt.debug.DebugUtil.debug;


public class DrJava {
  public static volatile Log _log = new Log("DrJava.txt", false);
  
  private static final String DEFAULT_MAX_HEAP_SIZE_ARG = "-Xmx128M";
  
  private static final ArrayList<String> _filesToOpen = new ArrayList<String>();
  private static final ArrayList<String> _jvmArgs = new ArrayList<String>();
  
  static volatile boolean _showDebugConsole = false;
  
  
  static volatile boolean _forceNewInstance = false;
  
  
  static volatile boolean _doRestart = false;
  
  
  static volatile boolean _alreadyRestarted = false;
  
  
  static volatile boolean _restartedDrJavaUsesRemoteControl = true;
  
  
  private static final int WAIT_BEFORE_DECLARING_SUCCESS = 5000;

  
  private static final int NUM_REMOTE_CONTROL_RETRIES = 15;

  
  private static final int WAIT_BEFORE_REMOTE_CONTROL_RETRY = 500;

  
  
  
  public static final File DEFAULT_PROPERTIES_FILE = new File(System.getProperty("user.home"), ".drjava");
  
  
  private static volatile File _propertiesFile = DEFAULT_PROPERTIES_FILE;
  
  
  private static volatile FileConfiguration _config;
  
  
  public static final String RESOURCE_BUNDLE_NAME = "edu.rice.cs.drjava.config.options";
  
  
  public static File getPropertiesFile() { return _propertiesFile; }
  
  
  public static synchronized FileConfiguration getConfig() {
    if (_config==null) {
      _config = _initConfig();  
    }
    return _config;
  }
  
  
  public static synchronized String[] getFilesToOpen() { return _filesToOpen.toArray(new String[0]); }
  
  
  public static synchronized void addFileToOpen(String s) {
    _filesToOpen.add(s);
    boolean isProjectFile =
      s.endsWith(OptionConstants.PROJECT_FILE_EXTENSION) ||
      s.endsWith(OptionConstants.PROJECT_FILE_EXTENSION2) ||
      s.endsWith(OptionConstants.OLD_PROJECT_FILE_EXTENSION);
    _forceNewInstance |= isProjectFile;
    if (_doRestart && _alreadyRestarted) {
      _log.log("addFileToOpen: already done the restart, trying to use remote control");
      
      if (DrJava.getConfig().getSetting(edu.rice.cs.drjava.config.OptionConstants.REMOTE_CONTROL_ENABLED)) {
        _log.log("\tremote control...");
        openWithRemoteControl(_filesToOpen,NUM_REMOTE_CONTROL_RETRIES );
        _log.log("\tclearing _filesToOpen");
        clearFilesToOpen();
      }
    }
  }
  
  
  public static synchronized void clearFilesToOpen() {
    _filesToOpen.clear();
  }
  
  
  public static synchronized boolean openWithRemoteControl(ArrayList<String> files, int numAttempts) {
    if (!DrJava.getConfig().getSetting(edu.rice.cs.drjava.config.OptionConstants.REMOTE_CONTROL_ENABLED) ||
        !_restartedDrJavaUsesRemoteControl ||
        (files.size()==0)) return false;
    
    ArrayList<String> fs = new ArrayList<String>(files);
    int failCount = 0;
    while(failCount<numAttempts) {
      try {
        RemoteControlClient.openFile(null);
        if (RemoteControlClient.isServerRunning()) {
          
          for (int i = 0; i < fs.size(); ++i) {
            _log.log("opening with remote control "+fs.get(i));
            RemoteControlClient.openFile(new File(fs.get(i)));
            files.remove(fs.get(i));
          }
          return true; 
        }
        else {
          ++failCount;
          _log.log("Failed to open with remote control, attempt "+failCount+" of "+NUM_REMOTE_CONTROL_RETRIES);
          if (failCount>=numAttempts) return false; 
          try { Thread.sleep(WAIT_BEFORE_REMOTE_CONTROL_RETRY); }
          catch(InterruptedException ie) {  }
        }
      }
      catch(IOException ioe) {
        ioe.printStackTrace();
      }
    }
    return false; 
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
        if (openWithRemoteControl(_filesToOpen,1)) System.exit(0); 
      }
      
      
      
      
      
      int failCount = 0;
      while(failCount < 2) {
        
        String masterMemory = getConfig().getSetting(MASTER_JVM_XMX).trim();
        boolean _doRestart = (getConfig().getSetting(MASTER_JVM_ARGS).length() > 0)
          || (!"".equals(masterMemory) && !OptionConstants.heapSizeChoices.get(0).equals(masterMemory));
        _log.log("_doRestart: "+_doRestart);
        
        LinkedList<String> classArgs = new LinkedList<String>();
        
        
        if (_showDebugConsole) { classArgs.addFirst("-debugConsole"); }
        
        if (! _propertiesFile.equals(DEFAULT_PROPERTIES_FILE)) {
          
          classArgs.addFirst(_propertiesFile.getAbsolutePath());
          classArgs.addFirst("-config");
        }
        
        synchronized(DrJava.class) {
          classArgs.addAll(_filesToOpen);
          clearFilesToOpen();
          _log.log("_filesToOpen copied into class arguments, clearing _filesToOpen");
        }
        
        if (_doRestart) {
          if (DrJava.getConfig().getSetting(edu.rice.cs.drjava.config.OptionConstants.REMOTE_CONTROL_ENABLED)) {
            
            
           _restartedDrJavaUsesRemoteControl = !RemoteControlClient.isServerRunning();
          } else {
            
            _restartedDrJavaUsesRemoteControl = false;
          }
          
          
          try {
            boolean failed = false;
            JVMBuilder jvmb = JVMBuilder.DEFAULT.jvmArguments(_jvmArgs);
            
            
            _log.log("JVMBuilder: classPath = "+jvmb.classPath());
            ArrayList<File> extendedClassPath = new ArrayList<File>();
            for(File f: jvmb.classPath()) { extendedClassPath.add(f); }
            _log.log("JVMBuilder: extendedClassPath = "+extendedClassPath);
            jvmb = jvmb.classPath(edu.rice.cs.plt.iter.IterUtil.asSizedIterable(extendedClassPath));
            _log.log("JVMBuilder: jvmArguments = "+jvmb.jvmArguments());
            _log.log("JVMBuilder: classPath = "+jvmb.classPath());
            _log.log("JVMBuilder: mainParams = "+classArgs);
            
            
            Process p = jvmb.start(DrJavaRoot.class.getName(), classArgs);
            _alreadyRestarted = true;
            _log.log("_alreadyRestarted = true");
            DelayedInterrupter timeout = new DelayedInterrupter(WAIT_BEFORE_DECLARING_SUCCESS);
            try {
              int exitValue = p.waitFor();
              timeout.abort();
              failed = (exitValue != 0);
            }
            catch(InterruptedException e) {  }
            _log.log("failed = "+failed);
            if (failed) {
              if (failCount > 0) {
                
                JOptionPane.showMessageDialog(null,
                                              "DrJava was unable to start, and resetting your configuration\n" + 
                                              "did not help. Please file a support request at\n" + 
                                              "https://sourceforge.net/projects/drjava/",
                                              "Could Not Start DrJava",
                                              JOptionPane.ERROR_MESSAGE);
                System.exit(1);
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
            else {
              
              _log.log("not failed, send remaining files via remote control: "+_filesToOpen);
              openWithRemoteControl(_filesToOpen, NUM_REMOTE_CONTROL_RETRIES);
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
          
          
          
          ArrayList<String> fs = new ArrayList<String>(_filesToOpen);
          for(String f: fs) {
            DrJavaRoot.handleRemoteOpenFile(new File(f), -1);
          }
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
    
    while(argIndex < len) {
      String arg = args[argIndex++];
      
      if (arg.equals("-config")) {
        if (len == argIndex) { 
          
          return true;
        }
        
        setPropertiesFile(args[argIndex++]);
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
      
      else if (arg.equals("-jll")) {
        String[] argsForJLL = new String[args.length-argIndex];
        System.arraycopy(args, argIndex, argsForJLL, 0, argsForJLL.length);
        edu.rice.cs.javalanglevels.LanguageLevelConverter.main(argsForJLL);
        System.exit(0);
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
    
    synchronized(DrJava.class) {
      _config = _initConfig();  
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
    System.out.println("  -jll [ARGS]           invoke the Java Language Level converter, specify files in ARGS");
  }
  
  
  static void setPropertiesFile(String fileName) {
    if (!DrJavaFileUtils.isSourceFile(fileName))  _propertiesFile = new File(fileName);
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
    _config = new ResourceBundleConfiguration(RESOURCE_BUNDLE_NAME,config);
    return _config;
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
    clearFilesToOpen();
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
