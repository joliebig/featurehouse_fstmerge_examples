

package edu.rice.cs.drjava.model.compiler;

import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
import java.util.*;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.config.OptionConstants;
import edu.rice.cs.drjava.model.DJError;
import edu.rice.cs.drjava.model.GlobalModel;
import edu.rice.cs.drjava.model.OpenDefinitionsDocument;
import edu.rice.cs.drjava.model.definitions.InvalidPackageException;
import edu.rice.cs.plt.io.IOUtil;
import edu.rice.cs.plt.iter.IterUtil;
import edu.rice.cs.plt.collect.CollectUtil;
import edu.rice.cs.util.FileOps;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.swing.Utilities;
import edu.rice.cs.javalanglevels.*;
import edu.rice.cs.javalanglevels.parser.*;
import edu.rice.cs.javalanglevels.tree.*;
import edu.rice.cs.util.swing.ScrollableListDialog;

import static edu.rice.cs.plt.debug.DebugUtil.debug;


public class DefaultCompilerModel implements CompilerModel {
  
  
  private final List<CompilerInterface> _compilers;
  
  
  private CompilerInterface _active;
  
  
  private final CompilerEventNotifier _notifier = new CompilerEventNotifier();
  
  
  private final GlobalModel _model;
  
  
  private CompilerErrorModel _compilerErrorModel;
  
  
  private Object _compilerLock = new Object();
  
  
  public LanguageLevelStackTraceMapper _LLSTM;
  
  
  
  public DefaultCompilerModel(GlobalModel m, Iterable<? extends CompilerInterface> compilers) {
    _compilers = new ArrayList<CompilerInterface>();
    for (CompilerInterface i : compilers) { _compilers.add(i); }
    if (_compilers.size() > 0) { _active = _compilers.get(0); }
    else { _active = NoCompilerAvailable.ONLY; }
    
    _model = m;
    _compilerErrorModel = new CompilerErrorModel(new DJError[0], _model);
    _LLSTM = new LanguageLevelStackTraceMapper(m);
  }
  
  
  
  
  public Object getCompilerLock() { return _compilerLock; }
  
  
  
  
  public void addListener(CompilerListener listener) { _notifier.addListener(listener); }
  
  
  public void removeListener(CompilerListener listener) { _notifier.removeListener(listener); }
  
  
  public void removeAllListeners() { _notifier.removeAllListeners(); }
  
  
  
  
  
  public void compileAll() throws IOException {
    if (_prepareForCompile()) { _doCompile(_model.getOpenDefinitionsDocuments()); }
    else _notifier.compileAborted(new UnexpectedException("Some modified open files are unsaved"));
  }
  
  
  public void compileProject() throws IOException {
    if (! _model.isProjectActive()) 
      throw new UnexpectedException("compileProject invoked when DrJava is not in project mode");
    
    if (_prepareForCompile()) { _doCompile(_model.getProjectDocuments()); }
    else _notifier.compileAborted(new UnexpectedException("Project contains unsaved modified files"));
  }
  
  
  public void compile(List<OpenDefinitionsDocument> defDocs) throws IOException {
    if (_prepareForCompile()) { _doCompile(defDocs); }
    else _notifier.compileAborted(new UnexpectedException("The files to be compiled include unsaved modified files"));
  }
  
  
  public void compile(OpenDefinitionsDocument doc) throws IOException {
    if (_prepareForCompile()) { _doCompile(Arrays.asList(doc)); }
    else _notifier.compileAborted(new UnexpectedException(doc + "is modified but unsaved"));
  }
  
  
  private boolean _prepareForCompile() {
    if (_model.hasModifiedDocuments()) _notifier.saveBeforeCompile();
    
    return ! _model.hasModifiedDocuments();
  }
  
  
  private void _doCompile(List<OpenDefinitionsDocument> docs) throws IOException {
    _LLSTM.clearCache();
    final ArrayList<File> filesToCompile = new ArrayList<File>();
    final ArrayList<File> excludedFiles = new ArrayList<File>();
    final ArrayList<DJError> packageErrors = new ArrayList<DJError>();
    for (OpenDefinitionsDocument doc : docs) {
      if (doc.isSourceFile()) {
        File f = doc.getFile();
        
        if (f != null && f != FileOps.NULL_FILE) { filesToCompile.add(f); }
        doc.setCachedClassFile(FileOps.NULL_FILE); 
        
        try { doc.getSourceRoot(); }
        catch (InvalidPackageException e) {
          packageErrors.add(new DJError(f, e.getMessage(), false));
        }
      }
      else excludedFiles.add(doc.getFile());
    }
    
    Utilities.invokeLater(new Runnable() { public void run() { _notifier.compileStarted(); } });
    try {
      if (! packageErrors.isEmpty()) { _distributeErrors(packageErrors); }
      else {
        try {
          File buildDir = _model.getBuildDirectory();
          if (buildDir != null && buildDir != FileOps.NULL_FILE && ! buildDir.exists() && ! buildDir.mkdirs()) {
            throw new IOException("Could not create build directory: " + buildDir);
          }
          





          
          _compileFiles(filesToCompile, buildDir);
        }
        catch (Throwable t) {
          DJError err = new DJError(t.toString(), false);
          _distributeErrors(Arrays.asList(err));
          throw new UnexpectedException(t);
        }
      }
    }
    finally {
      Utilities.invokeLater(new Runnable() {
        public void run() { _notifier.compileEnded(_model.getWorkingDirectory(), excludedFiles); }
      });
    }
  }
  
  
  
  
  
  private LinkedList<DJError> _parseExceptions2CompilerErrors(LinkedList<JExprParseException> pes) {
    final LinkedList<DJError> errors = new LinkedList<DJError>();
    Iterator<JExprParseException> iter = pes.iterator();
    while (iter.hasNext()) {
      JExprParseException pe = iter.next();
      errors.addLast(new DJError(pe.getFile(), pe.currentToken.beginLine-1, pe.currentToken.beginColumn-1, 
                                       pe.getMessage(), false));
    }
    return errors;
  }
  
  
  private LinkedList<DJError> _visitorErrors2CompilerErrors(LinkedList<Pair<String, JExpressionIF>> visitorErrors) {
    final LinkedList<DJError> errors = new LinkedList<DJError>();
    Iterator<Pair<String, JExpressionIF>> iter = visitorErrors.iterator();
    while (iter.hasNext()) {
      Pair<String, JExpressionIF> pair = iter.next();
      String message = pair.getFirst();      

      JExpressionIF jexpr = pair.getSecond();
      
      SourceInfo si;
      if (jexpr == null) si = JExprParser.NO_SOURCE_INFO;
      else si = pair.getSecond().getSourceInfo();
      
      errors.addLast(new DJError(si.getFile(), si.getStartLine()-1, si.getStartColumn()-1, message, false));
    }
    return errors;
  }
  
  
  private void _compileFiles(List<File> files, File buildDir) throws IOException {
    if (! files.isEmpty()) {
      
      if (buildDir == FileOps.NULL_FILE) buildDir = null; 
      if (buildDir != null) buildDir = IOUtil.attemptCanonicalFile(buildDir);
      
      List<File> classPath = CollectUtil.makeList(_model.getClassPath());
      
      
      List<File> bootClassPath = null;
      String bootProp = System.getProperty("drjava.bootclasspath");
      if (bootProp != null) { bootClassPath = CollectUtil.makeList(IOUtil.parsePath(bootProp)); }
      
      final LinkedList<DJError> errors = new LinkedList<DJError>();
      
      List<? extends File> preprocessedFiles = _compileLanguageLevelsFiles(files, errors, classPath, bootClassPath);
      
      if (errors.isEmpty()) {
        CompilerInterface compiler = getActiveCompiler();
        
        synchronized(_compilerLock) {
          if (preprocessedFiles == null) {
            errors.addAll(compiler.compile(files, classPath, null, buildDir, bootClassPath, null, true));
          }
          else {
            
            errors.addAll(compiler.compile(preprocessedFiles, classPath, null, buildDir, bootClassPath, null, false));
          }
        }
      }
      _distributeErrors(errors);
    }
    else { 
      
      _distributeErrors(Collections.<DJError>emptyList());
    }
  }
  
  
  private static List<File> _testFileSort(List<File> files) {
    LinkedList<File> testFiles = new LinkedList<File>();
    LinkedList<File> otherFiles = new LinkedList<File>();
    for (File f: files) {
      if (f.getName().contains("Test")) testFiles.add(f);
      else otherFiles.add(f);
    }
    otherFiles.addAll(testFiles);
    return otherFiles;
  }
    
  
  private List<File> _compileLanguageLevelsFiles(List<File> files, List<DJError> errors,
                                                           Iterable<File> classPath, Iterable<File> bootClassPath) {
    
    HashSet<File> javaFileSet = new HashSet<File>();
    LinkedList<File> newFiles = new LinkedList<File>();  
    final LinkedList<File> filesToBeClosed = new LinkedList<File>(); 
    boolean containsLanguageLevels = false;
    for (File f : files) {
      File canonicalFile = IOUtil.attemptCanonicalFile(f);
      String fileName = canonicalFile.getPath();
      int lastIndex = fileName.lastIndexOf(".dj");
      if (lastIndex != -1) {
        containsLanguageLevels = true;
        File javaFile = new File(fileName.substring(0, lastIndex) + ".java");
        
        
        if(files.contains(javaFile)){          
          filesToBeClosed.add(javaFile);
          
        }
        else {
          
          javaFile.delete();
        }
        javaFileSet.add(javaFile);
        newFiles.add(javaFile);
      }   
      else{  
        javaFileSet.add(canonicalFile);
      }
    }
    
    for(File f: filesToBeClosed) {
      
      File canonicalFile = IOUtil.attemptCanonicalFile(f);
      String fileName = canonicalFile.getPath();
      
      if(files.contains(new File(fileName.substring(0,fileName.lastIndexOf(".java"))+".dj0")) ||
         files.contains(new File(fileName.substring(0,fileName.lastIndexOf(".java"))+".dj1")) ||
         files.contains(new File(fileName.substring(0,fileName.lastIndexOf(".java"))+".dj2"))
        ) {
        files.remove(new File(fileName));
      }
      
      
    }
    
    if(!filesToBeClosed.isEmpty()){
      final JButton closeButton = new JButton(new AbstractAction("Close Files") {
        public void actionPerformed(ActionEvent e) {
          
        }
      });
      final JButton keepButton = new JButton(new AbstractAction("Keep Open") {
        public void actionPerformed(ActionEvent e) {
          
          filesToBeClosed.clear();
        }
      });




      ScrollableListDialog<File> dialog = new ScrollableListDialog.Builder<File>()
        .setTitle("Java File"+(filesToBeClosed.size() == 1?"":"s")+" Need to Be Closed")
        .setText("The following .java "+(filesToBeClosed.size() == 1?
                                           "file has a matching .dj? file":
                                           "files have matching .dj? files")+" open.\n"+
                 (filesToBeClosed.size() == 1?
                    "This .java file needs":
                    "These .java files need")+" to be closed for proper compiling.")
        .setItems(filesToBeClosed)
        .setMessageType(JOptionPane.WARNING_MESSAGE)
        .setFitToScreen(true)
        .clearButtons()
        .addButton(closeButton)
        .addButton(keepButton)
        .build();
      
      dialog.showDialog();
      
      LinkedList<OpenDefinitionsDocument> docsToBeClosed = new LinkedList<OpenDefinitionsDocument>();
      for(File f: filesToBeClosed) {
        try {
          docsToBeClosed.add(_model.getDocumentForFile(f));
        }
        catch(IOException ioe) {  }
      }
      _model.closeFiles(docsToBeClosed);
      
      for(File f: filesToBeClosed) {        
        
        f.delete();
      }
    }
    
    if (containsLanguageLevels) {
      
      final File buildDir = _model.getBuildDirectory();
      final File sourceDir = _model.getProjectRoot();
      if (!DrJava.getConfig().getSetting(OptionConstants.DELETE_LL_CLASS_FILES)
            .equals(OptionConstants.DELETE_LL_CLASS_FILES_CHOICES.get(0))) {
        
        final HashSet<File> dirsWithLLFiles = new HashSet<File>();
        for(File f: newFiles) {
          try {
            File dir = f.getParentFile();
            if (buildDir != null && buildDir != FileOps.NULL_FILE &&
                sourceDir != null && sourceDir != FileOps.NULL_FILE) {
              
              String rel = edu.rice.cs.util.FileOps.stringMakeRelativeTo(dir,sourceDir);
              dir = new File(buildDir,rel);
            }            
            dirsWithLLFiles.add(dir);
          }
          catch(IOException ioe) {  }
        }
        
        if (DrJava.getConfig().getSetting(OptionConstants.DELETE_LL_CLASS_FILES)
            .equals(OptionConstants.DELETE_LL_CLASS_FILES_CHOICES.get(1))) {
          
          final JButton deleteButton = new JButton(new AbstractAction("Delete Class Files") {
            public void actionPerformed(ActionEvent e) {
              
            }
          });
          final JButton keepButton = new JButton(new AbstractAction("Keep Class Files") {
            public void actionPerformed(ActionEvent e) {
              
              dirsWithLLFiles.clear();
            }
          });
          ScrollableListDialog<File> dialog = new ScrollableListDialog.Builder<File>()
            .setTitle("Delete Class Files")
            .setText("We suggest that you delete all class files in the directories with language\n"+
                     "level files. Do you want to delete the class files in the following director"+(dirsWithLLFiles.size() == 1?"y":"ies")+"?")
            .setItems(new ArrayList<File>(dirsWithLLFiles))
            .setMessageType(JOptionPane.QUESTION_MESSAGE)
            .setFitToScreen(true)
            .clearButtons()
            .addButton(deleteButton)
            .addButton(keepButton)
            .build();
          
          dialog.showDialog();
        }
        
        
        
        for(File f: dirsWithLLFiles) {
          f.listFiles(new java.io.FilenameFilter() {
            public boolean accept(File dir, String name) {
              int endPos = name.lastIndexOf(".class");
              if (endPos < 0) return false; 
              new File(dir, name).delete();
              
              return false;
            }
          });
        }
      }
      
      
      LanguageLevelConverter llc = new LanguageLevelConverter();
      Options llOpts;
      if (bootClassPath == null) { llOpts = new Options(getActiveCompiler().version(), classPath); }
      else { llOpts = new Options(getActiveCompiler().version(), classPath, bootClassPath); }
      
      
      
      Map<File,Set<String>> sourceToTopLevelClassMap = new HashMap<File,Set<String>>();
      Pair<LinkedList<JExprParseException>, LinkedList<Pair<String, JExpressionIF>>> llErrors = 
        llc.convert(_testFileSort(files).toArray(new File[0]), llOpts, sourceToTopLevelClassMap);
      
      errors.addAll(_parseExceptions2CompilerErrors(llErrors.getFirst()));
      errors.addAll(_visitorErrors2CompilerErrors(llErrors.getSecond()));
      
      
      
      
    }
    
    if (containsLanguageLevels) { return new LinkedList<File>(javaFileSet); }
    else { return null; }
  }
  
  
  private void _distributeErrors(List<? extends DJError> errors) throws IOException {


    _compilerErrorModel = new CompilerErrorModel(errors.toArray(new DJError[0]), _model);
    _model.setNumCompErrors(_compilerErrorModel.getNumCompErrors());  
  }
  
  
  
  
  public CompilerErrorModel getCompilerErrorModel() { return _compilerErrorModel; }
  
  
  public int getNumErrors() { return getCompilerErrorModel().getNumErrors(); }
  
  
  public int getNumCompErrors() { return getCompilerErrorModel().getNumCompErrors(); }
  
    
  public int getNumWarnings() { return getCompilerErrorModel().getNumWarnings(); }
  
  
  public void resetCompilerErrors() {
    
    _compilerErrorModel = new CompilerErrorModel(new DJError[0], _model);
  }
  
  
  
  
  public Iterable<CompilerInterface> getAvailableCompilers() {
    if (_compilers.isEmpty()) { return IterUtil.singleton(NoCompilerAvailable.ONLY); }
    else { return IterUtil.snapshot(_compilers); }
  }
  
  
  public CompilerInterface getActiveCompiler() { return _active; }
  
  
  public void setActiveCompiler(CompilerInterface compiler) {
    if (_compilers.isEmpty() && compiler.equals(NoCompilerAvailable.ONLY)) {
      
    }
    else if (_compilers.contains(compiler)) {
      _active = compiler;
      _notifier.activeCompilerChanged();
    }
    else {
      throw new IllegalArgumentException("Compiler is not in the list of available compilers: " + compiler);
    }
  }
  
  
  public void addCompiler(CompilerInterface compiler) {
    if (_compilers.isEmpty()) {
      _active = compiler;
    }
    _compilers.add(compiler);
  }
  
  
  public void smartDeleteClassFiles(Map<File,Set<String>> sourceToTopLevelClassMap) {
    final File buildDir = _model.getBuildDirectory();
    final File sourceDir = _model.getProjectRoot();
    
    
    
    
    Map<File,Set<String>> dirToClassNameMap = new HashMap<File,Set<String>>();
    for(Map.Entry<File,Set<String>> e: sourceToTopLevelClassMap.entrySet()) {
      try {
        File dir = e.getKey().getParentFile();
        if (buildDir != null && buildDir != FileOps.NULL_FILE &&
            sourceDir != null && sourceDir != FileOps.NULL_FILE) {
          
          String rel = edu.rice.cs.util.FileOps.stringMakeRelativeTo(dir,sourceDir);
          dir = new File(buildDir,rel);
        }
        Set<String> classNames = dirToClassNameMap.get(dir);
        if (classNames == null) classNames = new HashSet<String>();
        classNames.addAll(e.getValue());
        dirToClassNameMap.put(dir,classNames);




      }
      catch(IOException ioe) {  }
    }
    
    
    for(final Map.Entry<File,Set<String>> e: dirToClassNameMap.entrySet()) {


      e.getKey().listFiles(new java.io.FilenameFilter() {
        public boolean accept(File dir, String name) {

          int endPos = name.lastIndexOf(".class");
          if (endPos < 0) return false; 
          int dollarPos = name.indexOf('$');
          if ((dollarPos >= 0) && (dollarPos < endPos)) endPos = dollarPos;
          
          Set<String> classNames = e.getValue();
          if (classNames.contains(name.substring(0,endPos))) { 
            
            new File(dir, name).delete();
            

          }
          return false;
        }
      });
    }
  }
  
  
  
  public LanguageLevelStackTraceMapper getLLSTM(){
    return _LLSTM;
  }
  
}
