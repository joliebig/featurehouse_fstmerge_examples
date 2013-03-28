

package edu.rice.cs.drjava.model.compiler;

import java.io.File;
import java.io.IOException;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Iterator;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.config.OptionConstants;

import edu.rice.cs.drjava.model.GlobalModel;
import edu.rice.cs.drjava.model.OpenDefinitionsDocument;
import edu.rice.cs.drjava.model.definitions.InvalidPackageException;

import edu.rice.cs.util.ClassPathVector;
import edu.rice.cs.util.swing.Utilities;
import edu.rice.cs.util.FileOps;

import edu.rice.cs.javalanglevels.*;
import edu.rice.cs.javalanglevels.parser.*;
import edu.rice.cs.javalanglevels.tree.*;


public class DefaultCompilerModel implements CompilerModel {

  
  private String[] getCompilableExtensions() { return new String[]{".java", ".dj0", ".dj1", ".dj2"}; }
  
  
  private final CompilerEventNotifier _notifier = new CompilerEventNotifier();

  
  private final GlobalModel _model;

  
  private CompilerErrorModel _compilerErrorModel;
  
  
  private File _workDir;
  
  
  private Object _slaveJVMLock = new Object();

  
  public DefaultCompilerModel(GlobalModel m) {
    _model = m;
    _compilerErrorModel = new CompilerErrorModel(new CompilerError[0], _model);
    _workDir = _model.getWorkingDirectory();
  }
  
  
  
  
  public Object getSlaveJVMLock() { return _slaveJVMLock; }

  

  
  public void addListener(CompilerListener listener) { _notifier.addListener(listener); }

  
  public void removeListener(CompilerListener listener) { _notifier.removeListener(listener); }

  
  public void removeAllListeners() { _notifier.removeAllListeners(); }

  


  
  public void compileAll() throws IOException {
    
    boolean isProjActive = _model.isProjectActive();
    
    List<OpenDefinitionsDocument> defDocs = _model.getOpenDefinitionsDocuments();
    

    
    if (isProjActive) {
      
      
      List<OpenDefinitionsDocument> projectDocs = new LinkedList<OpenDefinitionsDocument>();
      
      for (OpenDefinitionsDocument doc : defDocs) {
        if (doc.isInProjectPath() || doc.isAuxiliaryFile()) projectDocs.add(doc);
      }
      defDocs = projectDocs;
    }
    compile(defDocs);
  }
  
  
  
  
  
  public void compileAll(List<File> sourceRootSet, List<File> filesToCompile) throws IOException {
 
    List<OpenDefinitionsDocument> defDocs;
    
    defDocs = _model.getOpenDefinitionsDocuments(); 
    

    
    
    if (_hasModifiedFiles(defDocs)) _notifier.saveBeforeCompile();
    
    
    
    if (_hasModifiedFiles(defDocs)) return;
    
    
    _rawCompile(sourceRootSet.toArray(new File[0]), filesToCompile.toArray(new File[0]));
  }
  
  
  public void compile(List<OpenDefinitionsDocument> defDocs) throws IOException {
    

    
    
    if (_hasModifiedFiles(defDocs)) _notifier.saveBeforeCompile();
    
    if (_hasModifiedFiles(defDocs)) return;
    
    
    
    
    
    ArrayList<File> filesToCompile = new ArrayList<File>();
    
    File f;
    String[] exts = getCompilableExtensions();
    for (OpenDefinitionsDocument doc : defDocs) {
      f = doc.getFile();

      if (f == null) continue; 
      if (endsWithExt(f, exts)) filesToCompile.add(f);
    } 
    

    
    _rawCompile(getSourceRootSet(), filesToCompile.toArray(new File[0]));
  }
  
  
  
  public void compile(OpenDefinitionsDocument doc) throws IOException {
    
    List<OpenDefinitionsDocument> defDocs;
    defDocs = _model.getOpenDefinitionsDocuments(); 
    
    
    if (_hasModifiedFiles(defDocs)) _notifier.saveBeforeCompile();
    if (_hasModifiedFiles(defDocs)) return;  
    
    
    if (doc.isUntitled()) {
      _notifier.saveUntitled();
      if (doc.isUntitled()) return;
    }
    
    File[] files = { doc.getFile() };  
    
    
     
    _rawCompile(new File[] { doc.getSourceRoot() }, files); 
  }
  
  private void _rawCompile(File[] sourceRoots, File[] files) throws IOException {
    
    File buildDir = _model.getBuildDirectory();
    File workDir = _model.getWorkingDirectory();
     



    
    _notifier.compileStarted();
    try {
      
      _compileFiles(sourceRoots, files, buildDir);
    }
    catch (Throwable t) {
      CompilerError err = new CompilerError(t.toString(), false);
      CompilerError[] errors = new CompilerError[] { err };
      _distributeErrors(errors);
    }
    finally { _notifier.compileEnded(workDir); }
  }
  

  

  
  private LinkedList<CompilerError> _parseExceptions2CompilerErrors(LinkedList<JExprParseException> pes) {
    LinkedList<CompilerError> errors = new LinkedList<CompilerError>();
    Iterator<JExprParseException> iter = pes.iterator();
    while (iter.hasNext()) {
      JExprParseException pe = iter.next();
      errors.addLast(new CompilerError(pe.getFile(), pe.currentToken.beginLine-1, pe.currentToken.beginColumn-1, pe.getMessage(), false));
    }
    return errors;
  }
  
  
  private LinkedList<CompilerError> _visitorErrors2CompilerErrors(LinkedList<Pair<String, JExpressionIF>> visitorErrors) {
    LinkedList<CompilerError> errors = new LinkedList<CompilerError>();
    Iterator<Pair<String, JExpressionIF>> iter = visitorErrors.iterator();
    while (iter.hasNext()) {
      Pair<String, JExpressionIF> pair = iter.next();
      String message = pair.getFirst();      

      JExpressionIF jexpr = pair.getSecond();
      
      SourceInfo si;
      if (jexpr == null) si = JExprParser.NO_SOURCE_INFO;
      else si = pair.getSecond().getSourceInfo();
      
      errors.addLast(new CompilerError(si.getFile(), si.getStartLine()-1, si.getStartColumn()-1, message, false));
    }
    return errors;
  }
  
  private void _compileFiles(File[] sourceRoots, File[] files, File buildDir) throws IOException {


    

      
    Pair<LinkedList<JExprParseException>, LinkedList<Pair<String, JExpressionIF>>> errors;
    LinkedList<JExprParseException> parseExceptions;

    LinkedList<Pair<String, JExpressionIF>> visitorErrors;
    LinkedList<CompilerError> compilerErrors = new LinkedList<CompilerError>();
    CompilerInterface compiler = CompilerRegistry.ONLY.getActiveCompiler();
    
    
    if (buildDir != null) buildDir = FileOps.getCanonicalFile(buildDir);

    compiler.setBuildDirectory(buildDir);
    ClassPathVector extraClassPath = new ClassPathVector();
    if (_model.isProjectActive()) 
      extraClassPath.addAll(_model.getExtraClassPath());

    for (File f : DrJava.getConfig().getSetting(OptionConstants.EXTRA_CLASSPATH)) extraClassPath.add(f);
    

    compiler.setExtraClassPath(extraClassPath);
    if (files.length > 0) {

      LanguageLevelConverter llc = new LanguageLevelConverter(getActiveCompiler().getName());

      

      errors = llc.convert(files);

      
      compiler.setWarningsEnabled(true);
      
      
      HashSet<File> javaFileSet = new HashSet<File>();
      for (File f : files) {
        File canonicalFile;
        try { canonicalFile = f.getCanonicalFile(); } 
        catch(IOException e) { canonicalFile = f.getAbsoluteFile(); }
        String fileName = canonicalFile.getPath();
        int lastIndex = fileName.lastIndexOf(".dj");
        if (lastIndex != -1) {
          
          compiler.setWarningsEnabled(false);
          javaFileSet.add(new File(fileName.substring(0, lastIndex) + ".java"));
        }
        else javaFileSet.add(canonicalFile);
      }
      files = javaFileSet.toArray(new File[javaFileSet.size()]);
        
      parseExceptions = errors.getFirst();
      compilerErrors.addAll(_parseExceptions2CompilerErrors(parseExceptions));
      visitorErrors = errors.getSecond();
      compilerErrors.addAll(_visitorErrors2CompilerErrors(visitorErrors));
      CompilerError[] compilerErrorsArray = null;
      
      compilerErrorsArray = compilerErrors.toArray(new CompilerError[compilerErrors.size()]);

      
    
      if (compilerErrorsArray.length == 0) 
        synchronized(_slaveJVMLock) { compilerErrorsArray = compiler.compile(sourceRoots, files); }

      _distributeErrors(compilerErrorsArray);
    }
    else _distributeErrors(new CompilerError[0]);
  }
  
  
  private static boolean endsWithExt(File f, String[] exts) {
    for (String ext: exts) { if (f.getName().endsWith(ext)) return true; }
    return false;
  }

  
  private void _distributeErrors(CompilerError[] errors) throws IOException {
    resetCompilerErrors();  
    _compilerErrorModel = new CompilerErrorModel(errors, _model);
  }

  
  public File[] getSourceRootSet() {
    List<OpenDefinitionsDocument> defDocs = _model.getOpenDefinitionsDocuments();
    return getSourceRootSet(defDocs);
  }
  
  
  public static File[] getSourceRootSet(List<OpenDefinitionsDocument> defDocs) {
    
    LinkedList<File> roots = new LinkedList<File>();

    for (int i = 0; i < defDocs.size(); i++) {
      OpenDefinitionsDocument doc = defDocs.get(i);

      try {
        File root = doc.getSourceRoot();
        if (root == null) continue;
        
        if (! roots.contains(root)) { roots.add(root); }
      }
      catch (InvalidPackageException e) {
        
      }
    }

    return roots.toArray(new File[roots.size()]);
  }

  
  protected boolean _hasModifiedFiles(List<OpenDefinitionsDocument> defDocs) {
    boolean isProjActive = _model.isProjectActive();
    for (OpenDefinitionsDocument doc : defDocs) {
      if (doc.isModifiedSinceSave() && ( ! isProjActive || ! doc.isUntitled())) return true;
    }
    return false;
  }
  
  

  
  public CompilerErrorModel getCompilerErrorModel() { return _compilerErrorModel; }

  
  public int getNumErrors() { return getCompilerErrorModel().getNumErrors(); }
  
  
  public int getNumCompErrors() { return getCompilerErrorModel().getNumCompErrors(); }
  
    
  public int getNumWarnings() { return getCompilerErrorModel().getNumWarnings(); }

  
  public void resetCompilerErrors() {
    
    _compilerErrorModel = new CompilerErrorModel(new CompilerError[0], _model);
  }

  

  
  public CompilerInterface[] getAvailableCompilers() {
    return CompilerRegistry.ONLY.getAvailableCompilers();
  }

  
  public CompilerInterface getActiveCompiler() {
    return CompilerRegistry.ONLY.getActiveCompiler();
  }

  
  public void setActiveCompiler(CompilerInterface compiler) {
    CompilerRegistry.ONLY.setActiveCompiler(compiler);
  }
}
