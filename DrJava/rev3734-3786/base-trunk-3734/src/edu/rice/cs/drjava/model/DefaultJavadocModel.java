

package edu.rice.cs.drjava.model;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Collection;

import edu.rice.cs.util.FileOps;
import edu.rice.cs.util.ArgumentTokenizer;
import edu.rice.cs.util.DirectorySelector;
import edu.rice.cs.util.FileOpenSelector;
import edu.rice.cs.drjava.model.FileSaveSelector;
import edu.rice.cs.util.OperationCanceledException;
import edu.rice.cs.util.newjvm.ExecJVM;
import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.config.Configuration;
import edu.rice.cs.drjava.config.OptionConstants;
import edu.rice.cs.drjava.config.FileOption;
import edu.rice.cs.drjava.platform.PlatformFactory;
import edu.rice.cs.drjava.platform.PlatformSupport;
import edu.rice.cs.drjava.model.compiler.CompilerErrorModel;
import edu.rice.cs.drjava.model.compiler.CompilerError;
import edu.rice.cs.drjava.model.definitions.InvalidPackageException;


public class DefaultJavadocModel implements JavadocModel {

  
  private GlobalModel _model;

  
  private final JavadocEventNotifier _notifier = new JavadocEventNotifier();

  
  private CompilerErrorModel _javadocErrorModel;

  
  public DefaultJavadocModel(GlobalModel model) {
    _model = model;
    _javadocErrorModel = new CompilerErrorModel();
  }

  

  
  public void addListener(JavadocListener listener) { _notifier.addListener(listener); }

  
  public void removeListener(JavadocListener listener) { _notifier.removeListener(listener); }

  
  public void removeAllListeners() { _notifier.removeAllListeners(); }

  

  
  public CompilerErrorModel getJavadocErrorModel() { return _javadocErrorModel; }

  
  public void resetJavadocErrors() {
    _javadocErrorModel = new CompilerErrorModel();
  }

  
  
  
  public void javadocAll(DirectorySelector select, final FileSaveSelector saver, final String classPath) 
    throws IOException {
        
    

    if (_model.hasModifiedDocuments() || _model.hasUntitledDocuments()) { return; }  
    
    

       




    Configuration config = DrJava.getConfig();
    File destDir = config.getSetting(OptionConstants.JAVADOC_DESTINATION);
    
    
    try {
      if (destDir.equals(FileOption.NULL_FILE)) {
        
        destDir = select.getDirectory(null);
      }
      else
        
        destDir = select.getDirectory(destDir);
      
      
      while (!destDir.exists() || !destDir.isDirectory() || !destDir.canWrite()) {
        if (!destDir.getPath().equals("") && !destDir.exists()) {
          
          boolean create = select.askUser
            ("The directory you chose does not exist:\\n'" + destDir + "'\nWould you like to create it?",
             "Create Directory?");
          if (create) {
            boolean dirMade = destDir.mkdirs();
            if (! dirMade) throw new IOException("Could not create directory: " + destDir);
          }
          else return;
        }
        else if (!destDir.isDirectory() || destDir.getPath().equals("")) {
          
          select.warnUser("The file you chose is not a directory:\n" +
                          "'" + destDir + "'\n" +
                          "Please choose another.",
                          "Not a Directory!");
          destDir = select.getDirectory(null);
        }
        else {
          
          select.warnUser("The directory you chose is not writable:\n" +
                          "'" + destDir + "'\n" +
                          "Please choose another directory.",
                          "Cannot Write to Destination!");
          destDir = select.getDirectory(null);
        }
      }
    }
    catch (OperationCanceledException oce) { return; } 
  
    
    final File destDirF = destDir;
    new Thread("DrJava Javadoc Thread") {
      public void run() { _javadocAllWorker(destDirF, saver, classPath); }
    }.start();
  }

  
  private void _javadocAllWorker(File destDirFile, FileSaveSelector saver, String classPath) {
    
    if (!_ensureValidToolsJar()) return;

    String destDir = destDirFile.getAbsolutePath();

    
    HashSet<String> docUnits      = new HashSet<String>(); 
    HashSet<File>   sourceRootSet = new HashSet<File>();   
    HashSet<File>   defaultRoots  = new HashSet<File>();   
    HashSet<String> topLevelPacks = new HashSet<String>(); 

    
    boolean docAll = DrJava.getConfig().getSetting(OptionConstants.JAVADOC_FROM_ROOTS).booleanValue();

    
    List<OpenDefinitionsDocument> docs = _model.getOpenDefinitionsDocuments();
    for (OpenDefinitionsDocument doc: docs) {
      File file = null;

      try {
        
        file = _getFileFromDocument(doc, saver);

        
        if (file == null) throw new IllegalStateException("No file for this document.");

        File sourceRoot = doc.getSourceRoot();
        String pack = doc.getPackageName();

        if (pack.equals("")) {
          
          if (! defaultRoots.contains(sourceRoot)) {
            
            
            
            defaultRoots.add(sourceRoot);
            File[] javaFiles = sourceRoot.listFiles(FileOps.JAVA_FILE_FILTER);
            for (File f: javaFiles) { docUnits.add(f.getAbsolutePath());}
          }
        }
        else {
          
          String topLevelPack;
          File searchRoot;

          int index = pack.indexOf('.');
          if (docAll && index != -1) {
            

            
            
            topLevelPack = pack.substring(0, index);
            searchRoot = new File(sourceRoot, topLevelPack);
          }
          else {
            
            topLevelPack = pack;
            searchRoot = new File(sourceRoot, pack.replace('.', File.separatorChar));
          }

          
          if (! topLevelPacks.contains(topLevelPack) || ! sourceRootSet.contains(sourceRoot)) {
            
            topLevelPacks.add(topLevelPack);
            sourceRootSet.add(sourceRoot);
            docUnits.addAll(FileOps.packageExplore(topLevelPack, searchRoot));
          }
        }
      }
      catch (IllegalStateException ise) {
        
      }
      catch (IOException ioe) {
        
        
        _notifier.javadocStarted();  
        _showCompilerError(ioe.getMessage(), file);
        return;
      }
      catch (InvalidPackageException ipe) {
        
        
        _notifier.javadocStarted();  
        _showCompilerError(ipe.getMessage(), file);
         return;
      }
    }

    
    if (docUnits.size() == 0) return;

    
    StringBuffer sourcePath = new StringBuffer();
    String separator = System.getProperty("path.separator");
    sourceRootSet.addAll(defaultRoots);
    File[] sourceRoots = sourceRootSet.toArray(new File[sourceRootSet.size()]);
    for (int a = 0 ; a  < sourceRoots.length; a++) {
      if (a != 0)  sourcePath.append(separator);
      sourcePath.append(sourceRoots[a].getAbsolutePath());
    }

    
    ArrayList<String> args = _buildCommandLineArgs(docUnits, destDir,
                                                   sourcePath.toString(),
                                                   classPath);

    
    _runJavadoc(args, classPath, destDirFile, true);
  }



  

  
  public void javadocDocument(final OpenDefinitionsDocument doc,
                              final FileSaveSelector saver,
                              final String classPath)           throws IOException {
    
    
    if (doc.isUntitled() || doc.isModifiedSinceSave()) _notifier.saveBeforeJavadoc();

    
    if (doc.isUntitled() || doc.isModifiedSinceSave()) {
      
      return;
    }

    
    final File file = _getFileFromDocument(doc, saver);

    
    final File destDir = FileOps.createTempDirectory("DrJava-javadoc");

    
    new Thread("DrJava Javadoc Thread") {
      public void run() {

        _javadocDocumentWorker(destDir, file, classPath);
      }
    }.start();
  }

  
  private void _javadocDocumentWorker(File destDirFile, File docFile, String classPath) {
    if (!_ensureValidToolsJar()) return;

    
    String destDir = destDirFile.getAbsolutePath();
    ArrayList<String> args = _buildCommandLineArgs(docFile, destDir, classPath);

    
    _runJavadoc(args, classPath, destDirFile, false);
  }



  

  
  public File suggestJavadocDestination(OpenDefinitionsDocument doc) {
    _attemptSaveAllDocuments();

    try {
      File sourceRoot = doc.getSourceRoot();
      return new File(sourceRoot, SUGGESTED_DIR_NAME);
    }
    catch (InvalidPackageException ipe) { return null; }
  }

  
  private void _attemptSaveAllDocuments() {
    
    if (_model.hasModifiedDocuments() || _model.hasUntitledDocuments()) _notifier.saveBeforeJavadoc();
  }

  
  private boolean _ensureValidToolsJar() {
    PlatformSupport platform = PlatformFactory.ONLY;
    String version = platform.getJavaSpecVersion();
    if (!"1.3".equals(version) && platform.has13ToolsJar()) {
      String msg =
        "There is an incompatible version of tools.jar on your\n" +
        "classpath, so Javadoc cannot run.\n" +
        "(tools.jar is version 1.3, JDK is version " + version + ")";
      _notifier.javadocStarted();  
      _showCompilerError(msg, null);
      return false;
    }
    return true;
  }

  
  private void _showCompilerError(String msg, File f) {
    CompilerError[] errors = new CompilerError[1];
    errors[0] = new CompilerError(f, -1, -1, msg, false);
    _javadocErrorModel = new CompilerErrorModel(errors, _model);
    _notifier.javadocEnded(false, null, false);
  }

  
  private void _runJavadoc(ArrayList<String> args, String classPath,
                           File destDirFile, boolean allDocs) {
    
    
    boolean result;
    try {
      
      _notifier.javadocStarted();

      result = _javadoc(args.toArray(new String[args.size()]), classPath);

      
      
      if (result && !allDocs) FileOps.deleteDirectoryOnExit(destDirFile);

      
      _notifier.javadocEnded(result, destDirFile, allDocs);
    }
    catch (Throwable e) {
      
      _showCompilerError(e.getMessage(), null);
    }
  }

  
  private boolean _javadoc(String[] args, String classPath) throws IOException {
    final String JAVADOC_CLASS = "com.sun.tools.javadoc.Main";
    Process javadocProcess;
    
    
    
    double version = Double.valueOf(System.getProperty("java.specification.version"));
    String[] jvmArgs = new String[0];
    
    
    javadocProcess =  ExecJVM.runJVM(JAVADOC_CLASS, args, new String[]{classPath}, jvmArgs, FileOption.NULL_FILE);

    

    


    
    LinkedList<String> outLines = new LinkedList<String>();
    LinkedList<String> errLines = new LinkedList<String>();
    boolean done = false;
    while (!done) {
      try {
        Thread.sleep(500);
        javadocProcess.exitValue();
        done = true;
      }
      catch (InterruptedException e) {
        
      }
      catch (IllegalThreadStateException e) {
        ExecJVM.ventBuffers(javadocProcess, outLines, errLines);
      }
    }
    ExecJVM.ventBuffers(javadocProcess, outLines, errLines);


    
    

    ArrayList<CompilerError> errors = _extractErrors(outLines);
    errors.addAll(_extractErrors(errLines));

    _javadocErrorModel = new CompilerErrorModel
      (errors.toArray(new CompilerError[errors.size()]), _model);


    
    return _javadocErrorModel.hasOnlyWarnings();
  }

  
  private ArrayList<CompilerError> _extractErrors(LinkedList lines) {
    
    ArrayList<CompilerError> errors = new ArrayList<CompilerError>(100);

    final String ERROR_INDICATOR = "Error: ";
    final String EXCEPTION_INDICATOR = "Exception: ";
    final String BAD_FLAG_INDICATOR = "invalid flag:";
    while (lines.size() > 0) {


      String output = (String) lines.removeFirst();

      
      int errStart;
      errStart = output.indexOf(ERROR_INDICATOR);

      
      if (errStart == -1) {
        errStart = output.indexOf(EXCEPTION_INDICATOR);
      }

      
      if (errStart == -1) {
        errStart = output.indexOf(BAD_FLAG_INDICATOR);
      }

      if (errStart != -1) {
        
        StringBuffer buf = new StringBuffer(60 * lines.size());
        buf.append(output);
        while (lines.size() > 0) {
          output = (String) lines.removeFirst();
          buf.append('\n');
          buf.append(output);
        }
        errors.add(new CompilerError(buf.toString(), false));
      }
      else {
        
        CompilerError error = _parseJavadocErrorLine(output);
        if (error != null) {
          errors.add(error);

        }
      }
    }

    return errors;
  }

  
  private CompilerError _parseJavadocErrorLine(String line) {
    
    if (line == null) {
      return null;
    }

    final String JAVA_INDICATOR = ".java:";
    final String GJ_INDICATOR = ".gj:";

    CompilerError error = null;

    
    
    int errStart = line.indexOf(JAVA_INDICATOR);

    
    if (errStart == -1) {
      errStart = line.indexOf(GJ_INDICATOR);
    }

    if (errStart != -1) {
      
      String fileName = line.substring(0, errStart+5);

      
      int lineno = -1;
      StringBuffer linenoString = new StringBuffer();
      int pos = errStart+6;
      while ((line.charAt(pos)>='0') && (line.charAt(pos)<='9')) {
        linenoString.append(line.charAt(pos));
        pos++;
      }
      
      
      
      if (line.charAt(pos) == ':') {
        try {
          
          lineno = Integer.valueOf(linenoString.toString()).intValue() -1;
        } catch (NumberFormatException e) {
        }
      } else {
        pos = errStart;
      }

      
      String errMessage = line.substring(pos+2);

      
      boolean isWarning = false;
      if (errMessage.substring(0, 7).equalsIgnoreCase("warning")) {
        isWarning = true;
      }

      if (lineno >= 0) {
        error = new CompilerError(new File(fileName), lineno, 0, errMessage, false);
          
      } else {
        error = new CompilerError(new File(fileName), errMessage, false);
      }
    }
    return error;
  }



  
  private File _getFileFromDocument(OpenDefinitionsDocument doc, FileSaveSelector saver) throws IOException {
    try {
      
      return doc.getFile();
    }
    catch (FileMovedException fme) {
      
      
      if (saver.shouldSaveAfterFileMoved(doc, fme.getFile())) {
        try {
          doc.saveFileAs(saver);
          return doc.getFile();
        }
        catch (FileMovedException fme2) {
          
          
          fme2.printStackTrace();
          throw new IOException("Could not find file: " + fme2);
        }
      }
      else {
        throw new IllegalStateException("No file exists for this document.");
      }
    }
  }

  
  protected ArrayList<String> _buildCommandLineArgs(Collection<String> docUnits,
                                                    String destDir,
                                                    String sourcePath,
                                                    String classPath)
  {
    ArrayList<String> args = new ArrayList<String>();
    _addBasicArguments(args, destDir, sourcePath, classPath);
    _addOnlineLinkArguments(args);
    args.addAll(docUnits);
    return args;
  }

  
  protected ArrayList<String> _buildCommandLineArgs(File file, String destDir,
                                                    String classPath)
  {
    ArrayList<String> args = new ArrayList<String>();
    _addBasicArguments(args, destDir, "", classPath);
    _addSingleDocArguments(args);
    args.add(file.getAbsolutePath());
    return args;
  }

  
  private void _addBasicArguments(ArrayList<String> args,
                                  String destDir,
                                  String sourcePath,
                                  String classPath)
  {
    
    Configuration config = DrJava.getConfig();
    String accLevel = config.getSetting(OptionConstants.JAVADOC_ACCESS_LEVEL);
    StringBuffer accArg = new StringBuffer(10);
    accArg.append('-');
    accArg.append(accLevel);

    
    args.add(accArg.toString());
    if (!sourcePath.equals("")) {
      args.add("-sourcepath");
      args.add(sourcePath);
    }
    args.add("-d");
    args.add(destDir);
    
    
    args.add("-classpath");
    args.add(classPath);

    
    String custom = config.getSetting(OptionConstants.JAVADOC_CUSTOM_PARAMS);
    args.addAll(ArgumentTokenizer.tokenize(custom));
  }

  
  private void _addOnlineLinkArguments(ArrayList<String> args) {
    Configuration config = DrJava.getConfig();
    String linkVersion = config.getSetting(OptionConstants.JAVADOC_LINK_VERSION);
    if (linkVersion.equals(OptionConstants.JAVADOC_1_3_TEXT)) {
      args.add("-link");
      args.add(config.getSetting(OptionConstants.JAVADOC_1_3_LINK));
    }
    else if (linkVersion.equals(OptionConstants.JAVADOC_1_4_TEXT)) {
      args.add("-link");
      args.add(config.getSetting(OptionConstants.JAVADOC_1_4_LINK));
    }
    else if (linkVersion.equals(OptionConstants.JAVADOC_1_5_TEXT)) {
      args.add("-link");
      args.add(config.getSetting(OptionConstants.JAVADOC_1_5_LINK));
    }
  }

  
  private void _addSingleDocArguments(ArrayList<String> args) {
    args.add("-noindex");
    args.add("-notree");
    args.add("-nohelp");
    args.add("-nonavbar");
  }
}
