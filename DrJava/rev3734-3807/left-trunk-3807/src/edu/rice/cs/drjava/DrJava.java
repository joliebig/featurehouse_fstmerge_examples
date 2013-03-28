

package edu.rice.cs.drjava;

import static edu.rice.cs.drjava.config.OptionConstants.JAVAC_LOCATION;
import static edu.rice.cs.drjava.config.OptionConstants.WORKING_DIRECTORY;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import edu.rice.cs.drjava.config.FileConfiguration;
import edu.rice.cs.drjava.config.FileOption;
import edu.rice.cs.drjava.ui.DrJavaErrorHandler;
import edu.rice.cs.drjava.ui.ClassPathFilter;
import edu.rice.cs.drjava.ui.SplashScreen;
import edu.rice.cs.util.classloader.ToolsJarClassLoader;
import edu.rice.cs.util.newjvm.ExecJVM;


public class DrJava {
  
  
  
  public static final String TEST_DEBUGGER_CLASS = "com.sun.jdi.Bootstrap";
  
  public static final String TEST_COMPILER_CLASS = "com.sun.tools.javac.main.JavaCompiler";
  
  
  private static final int PAUSE_TIME = 2000;
  


  
  private static ArrayList<String> _filesToOpen = new ArrayList<String>();
  private static ArrayList<String> _jmvArgs = new ArrayList<String>();

  private static boolean _showDebugConsole = false;
  
  

  
  private static File _propertiesFile = new File(System.getProperty("user.home"), ".drjava");
  
  
  private static FileConfiguration _config = _initConfig();
  
  private static ToolsJarClassLoader _toolsLoader = new ToolsJarClassLoader(getConfig().getSetting(JAVAC_LOCATION));
  private static ClassLoader _thisLoader = DrJava.class.getClassLoader();

  
  public static File getPropertiesFile() { return _propertiesFile; }

  
  public static FileConfiguration getConfig() { return _config; }

  
  public static void main(final String[] args) {
    
    final SplashScreen splash = new SplashScreen();
    splash.setVisible(true);
    splash.repaint();

    configureAndLoadDrJavaRoot(args); 
    
    
    SwingUtilities.invokeLater(new Runnable() { 
      public void run() { 
        try { Thread.sleep(PAUSE_TIME); }
        catch(InterruptedException e) { }
        splash.dispose(); 
      }});
  }
  
  public static void configureAndLoadDrJavaRoot(String[] args) {
    try {
      
      if (handleCommandLineArgs(args)) {
        
        
        checkForCompilersAndDebugger(args);
        
        
        String pathSep = System.getProperty("path.separator");
        String classPath = edu.rice.cs.util.FileOps.convertToAbsolutePathEntries(System.getProperty("java.class.path"));
        
        






        
        
        File toolsFromConfig = getConfig().getSetting(JAVAC_LOCATION);
        classPath += pathSep + ToolsJarClassLoader.getToolsJarClassPath(toolsFromConfig);
        
        File workDir = getConfig().getSetting(WORKING_DIRECTORY);
        if (workDir == null) workDir = FileOption.NULL_FILE;
        
        
        if (_showDebugConsole) _filesToOpen.add(pathSep);  
        
        String[] jvmArgs = _jmvArgs.toArray(new String[0]);
        String[] classArgs = _filesToOpen.toArray(new String[0]);
        
        
        try {


          ExecJVM.runJVM("edu.rice.cs.drjava.DrJavaRoot", classArgs, classPath, jvmArgs, workDir);
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
    }
    catch (Throwable t) {
      
      System.out.println(t.getClass().getName() + ": " + t.getMessage());
      t.printStackTrace(System.err);System.out.println("error thrown");
      new DrJavaErrorHandler().handle(t);
    }
  }
  
  
  static boolean handleCommandLineArgs(String[] args) {
    
    
    int firstFile = 0;
    int len = args.length;
    _filesToOpen = new ArrayList<String>();
    
    for (int i = 0; i < len; i++) {
      String arg = args[i];
      
      if (arg.equals("-config")) {
        if (len == i + 1) { 
          
          return true;
        }
        
        
        setPropertiesFile(args[i + 1]);
        firstFile = i + 2;
        _config = _initConfig();  
      }
      else if ((arg.length() > 1) && (arg.substring(0,2).equals("-X"))) _jmvArgs.add(arg); 
      
      else if (arg.equals("-debugConsole")) _showDebugConsole = true;
      
      else if (arg.equals("-help") || arg.equals("-?")) {
        displayUsage();
        return false;
      }
      else {
        firstFile = i;
        break;
      }
    }

    

    for (int i = firstFile; i < len; i++) _filesToOpen.add(args[i]);
    return true;
  }

  
  static void displayUsage() {
    StringBuffer buf = new StringBuffer();
    buf.append("Usage: java -jar drjava.jar [OPTIONS] [FILES]\n\n");
    buf.append("where options include:\n");
    buf.append("  -config [FILE]        use a custom config file\n");
    buf.append("  -help | -?            print this help message\n");
    buf.append("  -X<jvmOption>         specify a JVM configuration option for the master DrJava JVM\n");      
    System.out.print(buf.toString());
  }
  
   
  static void checkForCompilersAndDebugger(String[] args) {
    
    boolean needCompiler = ! hasAvailableCompiler();
    boolean needDebugger = ! hasAvailableDebugger();

    
    if (needCompiler || needDebugger) promptForToolsJar(needCompiler, needDebugger);
  }

  
  public static boolean hasAvailableDebugger() {
    return canLoad(_thisLoader, TEST_DEBUGGER_CLASS) || canLoad(_toolsLoader, TEST_DEBUGGER_CLASS);
  }
  
  public static boolean hasAvailableCompiler() {
   return canLoad(_thisLoader, TEST_COMPILER_CLASS) || canLoad(_toolsLoader, TEST_COMPILER_CLASS);
  }
  
  
   public static boolean canLoad(ClassLoader cl, String className) {
    try {
      cl.loadClass(className);
      return true;
    }
    catch(ClassNotFoundException e) { return false; }
    catch(RuntimeException e) { return false; }
  }
  
  
  public static void promptForToolsJar(boolean needCompiler, boolean needDebugger) {
    final String[] text  = new String[] {
      "DrJava cannot find a 'tools.jar' file for the version of Java ",
        "that is being used to run DrJava.  Would you like to specify the ",
        "location of the requisite 'tools.jar' file?   If you say 'No', ",
        "DrJava might be unable to compile or debug Java programs.)"
    };
    
    int result = JOptionPane.showConfirmDialog(null, text, "Locate 'tools.jar'?", JOptionPane.YES_NO_OPTION);

    if (result == JOptionPane.YES_OPTION) {
      JFileChooser chooser = new JFileChooser();
      chooser.setFileFilter(new ClassPathFilter() {
        public boolean accept(File f) {
          if (f.isDirectory()) return true;
          String ext = getExtension(f);
          return ext != null && ext.equals("jar");
        }
        public String getDescription() { return "Jar Files"; }
      });

      
      do {
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
          File jar = chooser.getSelectedFile();

          if (jar != null) {
            
            getConfig().setSetting(JAVAC_LOCATION, jar);

            
            if (needCompiler && classLoadersCanFind(TEST_COMPILER_CLASS)) needCompiler = false;

            
            if (needDebugger && classLoadersCanFind(TEST_DEBUGGER_CLASS)) needDebugger = false;
          }
        }

      }
      while ((needCompiler || needDebugger) && _userWantsToPickAgain());
      
      
      if ((! needCompiler) && (! needDebugger)) _saveConfig();
    }
  }
  
 
  
  public static boolean classLoadersCanFind(String className) {
    
    File jar = getConfig().getSetting(JAVAC_LOCATION);
    if (jar != FileOption.NULL_FILE) {
      try {
        URL[] urls = new URL[] { jar.toURL() };
        URLClassLoader loader = new URLClassLoader(urls);
        if (canLoad(loader, className)) return true;
      }
      catch(MalformedURLException e) {  }
    }
    return canLoad(_toolsLoader, className);
  }
    
  
  static void setPropertiesFile(String fileName) {
    if (!fileName.endsWith(".java"))  _propertiesFile = new File(fileName);
  }
  
  
  static FileConfiguration _initConfig() throws IllegalStateException {


    
    FileConfiguration config;

    try {
      _propertiesFile.createNewFile();
      
    }
    catch (IOException e) {
      
    }
    config = new FileConfiguration(_propertiesFile);
    try { config.loadConfiguration(); }
    catch (Exception e) {
      
      
      config.resetToDefaults();
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
      
    }
  }

  
  private static boolean _userWantsToPickAgain() {
    final String[] text = new String[] {
        "The file you chose did not appear to be the correct 'tools.jar'. ",
        "(Your choice might be an incompatible version of the file.) ",
        "Would you like to pick again?  The 'tools.jar' file is ",
        "generally located in the 'lib' subdirectory under your ",
        "JDK installation directory.",
        "(If you say 'No', DrJava might be unable to compile or ",
        "debug programs.)"
      };

    int result = JOptionPane.showConfirmDialog(null, text, "Locate 'tools.jar'?", JOptionPane.YES_NO_OPTION);
    return result == JOptionPane.YES_OPTION;
  }
}

