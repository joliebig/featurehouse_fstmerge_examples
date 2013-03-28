


package edu.rice.cs.drjava.model.compiler;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.io.FilenameFilter;
import java.util.Scanner;




import hj.lang.Runtime;
import soot.Main;
import edu.rice.cs.plt.reflect.PathClassLoader;
import edu.rice.cs.plt.reflect.ShadowingClassLoader;
import edu.rice.cs.drjava.model.DJError;
import edu.rice.cs.drjava.model.JarJDKToolsLibrary;
import edu.rice.cs.plt.io.IOUtil;
import edu.rice.cs.plt.reflect.JavaVersion;
import edu.rice.cs.util.ArgumentTokenizer;


public class HjCompiler extends JavacCompiler {

  private final boolean _filterExe;
  private File _tempJUnit = null;
  private final String PREFIX = "drjava-junit";
  private final String SUFFIX = ".jar";  
  private static String Dir ="";

  public HjCompiler(JavaVersion.FullVersion version, String location, java.util.List<? extends File> defaultBootClassPath) {
    super(version, location, defaultBootClassPath);
    _filterExe = version.compareTo(JavaVersion.parseFullVersion("1.6.0_04")) >= 0;
    if (_filterExe) {
      
      
      try {
        
        
        InputStream is = HjCompiler.class.getResourceAsStream("/junit.jar");
        if (is!=null) {
          
          _tempJUnit = edu.rice.cs.plt.io.IOUtil.createAndMarkTempFile(PREFIX,SUFFIX);
          FileOutputStream fos = new FileOutputStream(_tempJUnit);
          int size = edu.rice.cs.plt.io.IOUtil.copyInputStream(is,fos);
          
        }
        else {
          
          if (_tempJUnit!=null) {
            _tempJUnit.delete();
            _tempJUnit = null;
          }
        }
      }
      catch(IOException ioe) {
        if (_tempJUnit!=null) {
          _tempJUnit.delete();
          _tempJUnit = null;
        }
      }
      
      
      java.lang.Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
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
  }

  public String getName() { return "HJ " + _version.versionString(); }
  
  
  public java.util.List<File> additionalBootClassPathForInteractions() {
	  String hj_home = System.getenv("HJ_HOME") + "/lib";
	  File dir = new File(hj_home); 
	  File[] jarfiles = dir.listFiles(filter);
	  
	  File f= new File("/Users/triplezero163/Work/workspace/Test/src");
	  File [] dirs = {f};
	  
    
	  return Collections.EMPTY_LIST;
  }
  

  FilenameFilter filter = new FilenameFilter() {
   public boolean accept(File dir, String name) 
   { 
	   return name.endsWith(".jar"); 
   } 
  };

  
  public String transformCommands(String interactionsString) {
	  System.out.println(interactionsString);
	  if (interactionsString.startsWith("hj ")){
		  interactionsString = interactionsString.replace("hj ", "hj hj.lang.Runtime ");
		  interactionsString = transformHJCommand(interactionsString);
		  System.out.println(interactionsString);
	  }
	  if (interactionsString.startsWith("java "))  {
		  interactionsString = interactionsString.replace("java ", "java hj.lang.Runtime ");
		  interactionsString = transformHJCommand(interactionsString);
	  }
	  
	  if (interactionsString.startsWith("run "))  {
		  interactionsString = interactionsString.replace("run ", "java hj.lang.Runtime ");
		  interactionsString = transformHJCommand(interactionsString);
		  System.out.println(interactionsString);
	  }
	  
	  
		    return interactionsString;
	  
  }
  
  public static String transformHJCommand(String s) {
	    
	  
	

	  
	  final String HJcommand = "hj.lang.Runtime.mainEntry(new String[]'{'\"-rt=wsh\",{1}'}');";
	  
	  
	    if (s.endsWith(";"))  s = _deleteSemiColon(s);
	    java.util.List<String> args = ArgumentTokenizer.tokenize(s, true);
	    final String classNameWithQuotes = args.get(1); 
	    final String className = classNameWithQuotes.substring(1, classNameWithQuotes.length() - 1); 
	    final StringBuilder argsString = new StringBuilder();
	    for (int i = 2; i < args.size(); i++) {
	      
	      argsString.append(args.get(i));
	    }
	   String name = args.get(2);
	 
	    
	    return java.text.MessageFormat.format(HJcommand, className, argsString.toString());
	  
	  }
  

  public boolean isAvailable() {
	  return true;
  }
  

  
  public java.util.List<? extends DJError> compile(java.util.List<? extends File> files,
                                                   java.util.List<? extends File> classPath, 
                                                   java.util.List<? extends File> sourcePath,
                                                   File destination, 
                                                   java.util.List<? extends File> bootClassPath,
                                                   String sourceVersion,
                                                   boolean showWarnings) {
	  
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
    String s ="";
    Iterator<? extends File> ti = _defaultBootClassPath.listIterator();
    while(ti.hasNext()){
    	s += ":" + ti.next().getPath();
    }
    String [] testCommand = new String[11];
        
    testCommand[0] = "-hj";
    testCommand[1] = "-info";
    testCommand[2] = "-sp";
    testCommand[4] = "-cp";
    testCommand[5] = s;
    testCommand[6] = "-d";
    testCommand[8] = "-w";
    testCommand[9] = "-pp";
    
    
    Iterator<? extends File> it = files.listIterator();
    while(it.hasNext()){
    	File next = it.next();
    	 testCommand[3] = next.getParent();
    	 Dir = testCommand[3];
    	 testCommand[7] = next.getParent();
    	 testCommand[10] = next.getName();
    	 File path = new File(next.getParent());
    	
    	 String name = next.getName().replace(".hj", "");
    	 try {
    	 soot.Main.mainEntry(testCommand); 
    	
    	 }
    	 catch(Exception e) {
    		 e.printStackTrace();
    	 }
    }
    

   

       return Collections.EMPTY_LIST;
  }
  
      
  
}
