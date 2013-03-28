

package edu.rice.cs.drjava.model.javadoc;

import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.awt.EventQueue;

import edu.rice.cs.plt.lambda.Thunk;
import edu.rice.cs.plt.io.IOUtil;
import edu.rice.cs.plt.concurrent.JVMBuilder;
import edu.rice.cs.plt.concurrent.ConcurrentUtil;
import edu.rice.cs.plt.iter.IterUtil;
import edu.rice.cs.plt.text.TextUtil;
import edu.rice.cs.drjava.model.DJError;
import edu.rice.cs.drjava.model.FileSaveSelector;
import edu.rice.cs.drjava.model.OpenDefinitionsDocument;
import edu.rice.cs.drjava.model.GlobalModel;
import edu.rice.cs.drjava.model.FileMovedException;
import edu.rice.cs.drjava.model.definitions.InvalidPackageException;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.config.Configuration;
import edu.rice.cs.drjava.config.OptionConstants;
import edu.rice.cs.drjava.model.compiler.CompilerErrorModel;
import edu.rice.cs.drjava.model.compiler.CompilerListener;
import edu.rice.cs.drjava.model.compiler.DummyCompilerListener;

import edu.rice.cs.util.swing.Utilities;

import edu.rice.cs.util.ArgumentTokenizer;
import edu.rice.cs.util.DirectorySelector;
import edu.rice.cs.util.FileOps;
import edu.rice.cs.util.OperationCanceledException;

import static edu.rice.cs.plt.debug.DebugUtil.error;


public class DefaultJavadocModel implements JavadocModel {
  
  
  private GlobalModel _model;
  
  
  private final JavadocEventNotifier _notifier = new JavadocEventNotifier();

  
  private final JVMBuilder _jvmBuilder;
  
  
  private CompilerErrorModel _javadocErrorModel;
  
  
  public DefaultJavadocModel(GlobalModel model, File javaCommand, Iterable<File> toolsPath) {
    _model = model;
    JVMBuilder builder = JVMBuilder.DEFAULT;
    if (javaCommand != null) { builder = builder.javaCommand(javaCommand); }
    if (toolsPath != null) { builder = builder.classPath(toolsPath); }
    _jvmBuilder = builder;
    _javadocErrorModel = new CompilerErrorModel();
  }
  
  public boolean isAvailable() { return true; }
  
  
  
  
  public void addListener(JavadocListener listener) { _notifier.addListener(listener); }
  
  
  public void removeListener(JavadocListener listener) { _notifier.removeListener(listener); }
  
  
  public void removeAllListeners() { _notifier.removeAllListeners(); }
  
  
  
  
  public CompilerErrorModel getJavadocErrorModel() { return _javadocErrorModel; }
  
  
  public void resetJavadocErrors() {
    _javadocErrorModel = new CompilerErrorModel();
  }
  
  
  
  
  public void javadocAll(DirectorySelector select, final FileSaveSelector saver) throws IOException {
    
    
    if (_model.hasModifiedDocuments() || _model.hasUntitledDocuments()) { return; }  
    
    Configuration config = DrJava.getConfig();
    File destDir = config.getSetting(OptionConstants.JAVADOC_DESTINATION);
    
    
    try {
      if (destDir.equals(FileOps.NULL_FILE)) {
        
        destDir = select.getDirectory(null);
      }
      else
        
        destDir = select.getDirectory(destDir);
      
      
      while (!destDir.exists() || !destDir.isDirectory() || !destDir.canWrite()) {
        if (!destDir.getPath().equals("") && !destDir.exists()) {
          
          boolean create = select.askUser
            ("The directory you chose does not exist:\n'" + destDir + "'\nWould you like to create it?",
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
    
    _notifier.javadocStarted();  
    
    final File destDirF = destDir;
    new Thread("DrJava Javadoc Thread") {
      public void run() { _javadocAllWorker(destDirF, saver); }
    }.start();
  }
  
  
  private void _javadocAllWorker(final File destDirFile, FileSaveSelector saver) {
    
    
    
    final List<String> docFiles = new ArrayList<String>(); 

    final List<OpenDefinitionsDocument> llDocs = new ArrayList<OpenDefinitionsDocument>();
    for (OpenDefinitionsDocument doc: _model.getOpenDefinitionsDocuments()) {
      try {
        
        File docFile = _getFileFromDocument(doc, saver);
        final File file = IOUtil.attemptCanonicalFile(docFile);

        
        if (isLLFile(file)) {
          
          llDocs.add(doc);
          docFiles.add(getJavaForLLFile(file).getPath());
        }
        else {
          docFiles.add(file.getPath());
        }
      }
      catch (IllegalStateException e) {
        
      }
      catch (IOException ioe) {
        
      }
    }
    
    
    if (docFiles.size() == 0) {
      
      EventQueue.invokeLater(new Runnable() { public void run() { _notifier.javadocEnded(false, destDirFile, true); } });
      return;
    }
    
    Utilities.invokeLater(new Runnable() { public void run() {
      if (_model.hasOutOfSyncDocuments(llDocs)) {
        
        CompilerListener javadocAfterCompile = new DummyCompilerListener() {
          @Override public void compileAborted(Exception e) {
            
            
            final CompilerListener listenerThis = this;
            
            
            EventQueue.invokeLater(new Runnable() { 
              public void run() {
                _model.getCompilerModel().removeListener(listenerThis);
                
                _notifier.javadocEnded(false, destDirFile, true);
              }
            });
          }
          @Override public void compileEnded(File workDir, List<? extends File> excludedFiles) {
            final CompilerListener listenerThis = this;
            try {
              
              if (_model.hasOutOfSyncDocuments(llDocs) || _model.getNumCompErrors() > 0) {
                
                
                EventQueue.invokeLater(new Runnable() { public void run() { _notifier.javadocEnded(false, destDirFile, true); } });
                return;
              }
              EventQueue.invokeLater(new Runnable() {  
                public void run() {
                  
                  
                  _runJavadoc(docFiles, destDirFile, IterUtil.<String>empty(), true);
                }
              });
            }
            finally {  
              EventQueue.invokeLater(new Runnable() { 
                public void run() { _model.getCompilerModel().removeListener(listenerThis); }
              });
            }
          }
        };
        
        _notifyCompileBeforeJavadoc(javadocAfterCompile);
        return;
      }
      
      
      _runJavadoc(docFiles, destDirFile, IterUtil.<String>empty(), true);
    } });
  }
  
  
  
  
  
  
  public void javadocDocument(final OpenDefinitionsDocument doc, final FileSaveSelector saver) throws IOException {
    
    
    if (doc.isUntitled() || doc.isModifiedSinceSave()) _notifier.saveBeforeJavadoc();
    
    
    if (doc.isUntitled() || doc.isModifiedSinceSave()) return;  
    
    
    File docFile = _getFileFromDocument(doc, saver);
    final File file = IOUtil.attemptCanonicalFile(docFile);
    final File javaFile = getJavaForLLFile(file);

    Utilities.invokeLater(new Runnable() { public void run() {
      
      if (isLLFile(file)) {
        
        final List<OpenDefinitionsDocument> singleton = new ArrayList<OpenDefinitionsDocument>();
        singleton.add(doc);
        if (_model.hasOutOfSyncDocuments(singleton)) {
          
          CompilerListener javadocAfterCompile = new DummyCompilerListener() {
            @Override public void compileAborted(Exception e) {
              
              
              final CompilerListener listenerThis = this;
              
              EventQueue.invokeLater(new Runnable() { 
                public void run() { 
                  _model.getCompilerModel().removeListener(listenerThis);
                  
                  _notifier.javadocEnded(false, null, false);
                }
              });
            }
            @Override public void compileEnded(File workDir, List<? extends File> excludedFiles) {
              final CompilerListener listenerThis = this;
              try {
                
                if (_model.hasOutOfSyncDocuments(singleton) || _model.getNumCompErrors() > 0) {
                  
                  
                  EventQueue.invokeLater(new Runnable() { public void run() { _notifier.javadocEnded(false, null, false); } });
                  return;
                }
                EventQueue.invokeLater(new Runnable() {  
                  public void run() {
                    try {
                      
                      _rawJavadocDocument(javaFile);
                    }
                    catch(IOException ioe) {
                      
                      EventQueue.invokeLater(new Runnable() { public void run() { _notifier.javadocEnded(false, null, false); } });
                    }
                  }
                });
              }
              finally {  
                EventQueue.invokeLater(new Runnable() { 
                  public void run() { _model.getCompilerModel().removeListener(listenerThis); }
                });
              }
            }
          };
          
          _notifyCompileBeforeJavadoc(javadocAfterCompile);
          return;
        }
      }
      try {
        _rawJavadocDocument(javaFile);
      }
      catch(IOException ioe) {
        
        EventQueue.invokeLater(new Runnable() { public void run() { _notifier.javadocEnded(false, null, false); } });
      }
    } });
  }
    
  private void _rawJavadocDocument(final File file) throws IOException {
    
    final File destDir = IOUtil.createAndMarkTempDirectory("DrJava-javadoc", "");
    
    _notifier.javadocStarted();  
    
    new Thread("DrJava Javadoc Thread") {
      public void run() {
        Iterable<String> extraArgs = IterUtil.make("-noindex", "-notree", "-nohelp", "-nonavbar");
        _runJavadoc(IterUtil.make(file.getPath()), destDir, extraArgs, false);
      }
    }.start();
  }
  
  

  
  private void _notifyCompileBeforeJavadoc(final CompilerListener afterCompile) { 
    Utilities.invokeLater(new Runnable() { 
      public void run() { _notifier.compileBeforeJavadoc(afterCompile); } 
    });
  }

  
  private static boolean isLLFile(File f) {
    File canonicalFile = IOUtil.attemptCanonicalFile(f);
    String fileName = canonicalFile.getPath();
    int lastIndex = fileName.lastIndexOf(OptionConstants.DJ_FILE_EXTENSION);
    return (lastIndex != -1);
  }

  
  private static File getJavaForLLFile(File f) {
    File canonicalFile = IOUtil.attemptCanonicalFile(f);
    String fileName = canonicalFile.getPath();
    int lastIndex = fileName.lastIndexOf(OptionConstants.DJ_FILE_EXTENSION);
    if (lastIndex != -1) {
      return new File(fileName.substring(0, lastIndex) + OptionConstants.JAVA_FILE_EXTENSION);
    }
    else {
      return f;
    }
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
  
  
  private void _runJavadoc(Iterable<String> files, final File destDir, Iterable<String> extraArgs, final boolean allDocs) {    
    Iterable<String> args = IterUtil.empty();
    args = IterUtil.compose(args, IterUtil.make("-d", destDir.getPath()));
    args = IterUtil.compose(args, IterUtil.make("-classpath", IOUtil.pathToString(_model.getClassPath())));
    args = IterUtil.compose(args, _getLinkArgs());
    args = IterUtil.compose(args, "-" + DrJava.getConfig().getSetting(OptionConstants.JAVADOC_ACCESS_LEVEL));
    args = IterUtil.compose(args, extraArgs);
    String custom = DrJava.getConfig().getSetting(OptionConstants.JAVADOC_CUSTOM_PARAMS);
    args = IterUtil.compose(args, ArgumentTokenizer.tokenize(custom));
    args = IterUtil.compose(args, files);
    
    List<DJError> errors = new ArrayList<DJError>();
    try {
      Process p = _jvmBuilder.start("com.sun.tools.javadoc.Main", args);
      Thunk<String> outputString = ConcurrentUtil.processOutAsString(p);
      Thunk<String> errorString = ConcurrentUtil.processErrAsString(p);
      p.waitFor();
      errors.addAll(_extractErrors(outputString.value()));
      errors.addAll(_extractErrors(errorString.value()));
    }
    catch (IOException e) {
      errors.add(new DJError("IOException: " + e.getMessage(), false));
    }
    catch (InterruptedException e) {
      errors.add(new DJError("InterruptedException: " + e.getMessage(), false));
    }
    
    _javadocErrorModel = new CompilerErrorModel(IterUtil.toArray(errors, DJError.class), _model);
    
    
    final boolean success = _javadocErrorModel.hasOnlyWarnings();
    if (!allDocs) { IOUtil.deleteOnExitRecursively(destDir); }
    
    EventQueue.invokeLater(new Runnable() { public void run() { _notifier.javadocEnded(success, destDir, allDocs); } });
  }
  
  private Iterable<String> _getLinkArgs() {
    Configuration config = DrJava.getConfig();
    String linkVersion = config.getSetting(OptionConstants.JAVADOC_LINK_VERSION);
    if (linkVersion.equals(OptionConstants.JAVADOC_1_3_TEXT)) {
      return IterUtil.make("-link", config.getSetting(OptionConstants.JAVADOC_1_3_LINK));
    }
    else if (linkVersion.equals(OptionConstants.JAVADOC_1_4_TEXT)) {
      return IterUtil.make("-link", config.getSetting(OptionConstants.JAVADOC_1_4_LINK));
    }
    else if (linkVersion.equals(OptionConstants.JAVADOC_1_5_TEXT)) {
      return IterUtil.make("-link", config.getSetting(OptionConstants.JAVADOC_1_5_LINK));
    }
    else if (linkVersion.equals(OptionConstants.JAVADOC_1_6_TEXT)) {
      return IterUtil.make("-link", config.getSetting(OptionConstants.JAVADOC_1_6_LINK));
    }
    else {
      
      return IterUtil.empty();
    }
  }
  
  
  private List<DJError> _extractErrors(String text) {
    BufferedReader r = new BufferedReader(new StringReader(text));
    List<DJError> result = new ArrayList<DJError>();
    
    String[] errorIndicators = new String[]{ "Error: ", "Exception: ", "invalid flag:" };
    
    try {
      String output = r.readLine();
      while (output != null) {
        if (TextUtil.containsAny(output, errorIndicators)) {
          
          result.add(new DJError(output + '\n' + IOUtil.toString(r), false));
        }
        else {
          
          DJError error = _parseJavadocErrorLine(output);
          if (error != null) { result.add(error); }
        }
        output = r.readLine();
      }
    }
    catch (IOException e) { error.log(e);  }
    
    return result;
  }
  
  
  private DJError _parseJavadocErrorLine(String line) {
    int errStart = line.indexOf(".java:");
    if (errStart == -1) { return null;  }
    
    String fileName = line.substring(0, errStart+5);
    
    
    int lineno = -1;
    final StringBuilder linenoString = new StringBuilder();
    int pos = errStart+6;
    while ((line.charAt(pos) >= '0') && (line.charAt(pos) <= '9')) {
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
    
    if (lineno >= 0) { return new DJError(new File(fileName), lineno, 0, errMessage, isWarning); }
    else { return new DJError(new File(fileName), errMessage, isWarning); }
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
  
}
