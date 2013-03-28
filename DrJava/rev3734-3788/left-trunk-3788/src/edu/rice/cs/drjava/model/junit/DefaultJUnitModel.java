

package edu.rice.cs.drjava.model.junit;

import java.io.File;
import java.io.IOException;

import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.model.GlobalModel;
import edu.rice.cs.drjava.model.FileMovedException;
import edu.rice.cs.drjava.model.OpenDefinitionsDocument;
import edu.rice.cs.drjava.model.repl.newjvm.MainJVM;
import edu.rice.cs.drjava.model.compiler.CompilerModel;
import edu.rice.cs.drjava.model.compiler.CompilerListener;
import edu.rice.cs.drjava.model.compiler.DummyCompilerListener;



import edu.rice.cs.util.ClassPathVector;
import edu.rice.cs.util.FileOps;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.classloader.ClassFileError;
import edu.rice.cs.util.text.SwingDocument;
import edu.rice.cs.util.swing.Utilities;

import org.apache.bcel.classfile.*;

import static edu.rice.cs.drjava.config.OptionConstants.*;


public class DefaultJUnitModel implements JUnitModel, JUnitModelCallback {
  
  
  private final JUnitEventNotifier _notifier = new JUnitEventNotifier();
  
  
  private final MainJVM _jvm;
  
  
  private final CompilerModel _compilerModel;
  
  
  private final GlobalModel _model;
  
  
  private JUnitErrorModel _junitErrorModel;
  
  
  private volatile boolean _testInProgress = false;
  
  
  private boolean _forceTestSuffix = false;
  
  
  final private Object _testLock = new Object();
  
  
  private final SwingDocument _junitDoc = new SwingDocument();
  
  
  public DefaultJUnitModel(MainJVM jvm, CompilerModel compilerModel, GlobalModel model) {
    _jvm = jvm;
    _compilerModel = compilerModel;
    _model = model;
    _junitErrorModel = new JUnitErrorModel(new JUnitError[0], _model, false);
  }
  
  
  
  public void setForceTestSuffix(boolean b) { _forceTestSuffix = b; }
  
  
  
  public boolean isTestInProgress() { return _testInProgress;  }

  
  
  
  public void addListener(JUnitListener listener) { _notifier.addListener(listener); }
  
  
  public void removeListener(JUnitListener listener) { _notifier.removeListener(listener); }
  
  
  public void removeAllListeners() { _notifier.removeAllListeners(); }
  

  
  
  
  
  public SwingDocument getJUnitDocument() { return _junitDoc; }
  
  
  public void junitAll() { junitDocs(_model.getOpenDefinitionsDocuments()); }
  
  
  public void junitProject() {
    LinkedList<OpenDefinitionsDocument> lod = new LinkedList<OpenDefinitionsDocument>();
    
    for (OpenDefinitionsDocument doc : _model.getOpenDefinitionsDocuments()) { 
      if (doc.inProjectPath() || doc.isAuxiliaryFile())  lod.add(doc);
    }
    junitDocs(lod);
  }
  
  
  public void junitClasses(List<String> qualifiedClassnames, List<File> files) {

    synchronized(_compilerModel.getSlaveJVMLock()) {
 
       
      synchronized(_testLock) {
        if (_testInProgress) return;
        _testInProgress = true;
      }
      List<String> testClasses;
      try { testClasses = _jvm.findTestClasses(qualifiedClassnames, files); }
      catch(IOException e) { throw new UnexpectedException(e); }
      

      
      if (testClasses.isEmpty()) {
        nonTestCase(true);
        return;
      }
      _notifier.junitClassesStarted(); 
      try { _jvm.runTestSuite(); } 
      catch(Throwable t) {

        _notifier.junitEnded();
        _testInProgress = false;
        throw new UnexpectedException(t); 
      }
    }
  }
  
  public void junitDocs(List<OpenDefinitionsDocument> lod) { junitOpenDefDocs(lod, true); }
  
  
  public void junit(OpenDefinitionsDocument doc) throws ClassNotFoundException, IOException {

    File testFile;
    try { testFile = doc.getFile(); 
      if (testFile == null) {  
        nonTestCase(false);
        return;
      }
    } 
    catch(FileMovedException fme) {  }
    
    LinkedList<OpenDefinitionsDocument> lod = new LinkedList<OpenDefinitionsDocument>();
    lod.add(doc);
    junitOpenDefDocs(lod, false);
  }
  
  
  private void junitOpenDefDocs(final List<OpenDefinitionsDocument> lod, final boolean allTests) {
    
    

    
    
    synchronized(_testLock) { 
      if (_testInProgress) return; 
      _testInProgress = true;
    }
      
    
    _junitErrorModel = new JUnitErrorModel(new JUnitError[0], null, false);

    
    


    
    
    
    if (_model.hasOutOfSyncDocuments() || _model.hasModifiedDocuments()) { 
    

        
        CompilerListener testAfterCompile = new DummyCompilerListener() {
          public void compileEnded(File workDir, File[] excludedFiles) {
            final CompilerListener listenerThis = this;
            try {
              if (_model.hasOutOfSyncDocuments()) {
                JOptionPane.showMessageDialog(null, "All open source files must be compiled before running a unit test", 
                                              "Must Compile All Before Testing", JOptionPane.ERROR_MESSAGE); 
                nonTestCase(allTests);
                return;
              }
              _rawJUnitOpenDefDocs(lod, allTests);
            }
            finally {  
              SwingUtilities.invokeLater(new Runnable() { 
                public void run() { _compilerModel.removeListener(listenerThis); }
              });
            }
          }
        };
        

        _notifier.compileBeforeJUnit(testAfterCompile);
      }
      
      else _rawJUnitOpenDefDocs(lod, allTests);
  }
  
  
  private void _rawJUnitOpenDefDocs(List<OpenDefinitionsDocument> lod, boolean allTests) {

    File buildDir = _model.getBuildDirectory();

    
    
    HashSet<String> openDocFiles = new HashSet<String>();
    
    
    HashMap<File, File> classDirsAndRoots = new HashMap<File, File>();
    
    
    
    
    for (OpenDefinitionsDocument doc: lod)  {
      if (doc.isSourceFile())  { 
        
        
        openDocFiles.add(doc.getCanonicalPath());
        
        String packagePath = doc.getPackageName().replace('.', File.separatorChar);
        
        
        
        File sourceRoot = doc.getSourceRoot();
        File buildRoot = (buildDir == null) ? sourceRoot: buildDir;
        
        File classFileDir = new File(FileOps.getCanonicalPath(buildRoot) + File.separator + packagePath);
        
        File sourceDir = 
          (buildDir == null) ? classFileDir : new File(FileOps.getCanonicalPath(sourceRoot) + File.separator + packagePath);
        
        if (! classDirsAndRoots.containsKey(classFileDir)) {
          classDirsAndRoots.put(classFileDir, sourceDir);


        }
      }
    }
    

    
    
    Set<File> classDirs = classDirsAndRoots.keySet();
    

        
    
    ArrayList<String> classNames = new ArrayList<String>();
    
    
    ArrayList<File> files = new ArrayList<File>();
    
    
    boolean isProject = _model.isProjectActive();

    try {
      for (File dir: classDirs) { 

        
        File[] listing = dir.listFiles();
        

        
        if (listing != null) { 
          for (File entry : listing) {         
            

            
            
            String name = entry.getName();
            if (! name.endsWith(".class")) continue;
            
            
            if (_forceTestSuffix) {
              String noExtName = name.substring(0, name.length() - 6);  
              int indexOfLastDot = noExtName.lastIndexOf('.');
              String simpleClassName = noExtName.substring(indexOfLastDot + 1);

              if (isProject && ! simpleClassName.endsWith("Test")) continue;
            }
            

            
            
            if (! entry.isFile()) continue;
            
            
            
            
            try {
              JavaClass clazz = new ClassParser(entry.getCanonicalPath()).parse();
              String className = clazz.getClassName(); 

              int indexOfDot = className.lastIndexOf('.');
              
              File rootDir = classDirsAndRoots.get(dir);
              
              
              String javaSourceFileName = rootDir.getCanonicalPath() + File.separator + clazz.getSourceFileName();

              
              
              int indexOfExtDot = javaSourceFileName.lastIndexOf('.');

              if (indexOfExtDot == -1) continue;  

              
              
              String strippedName = javaSourceFileName.substring(0, indexOfExtDot);

              
              String sourceFileName;
              
              if (openDocFiles.contains(javaSourceFileName)) sourceFileName = javaSourceFileName;
              else if (openDocFiles.contains(strippedName + ".dj0")) sourceFileName = strippedName + ".dj0";
              else if (openDocFiles.contains(strippedName + ".dj1")) sourceFileName = strippedName + ".dj1";
              else if (openDocFiles.contains(strippedName + ".dj2")) sourceFileName = strippedName + ".dj2";
              else continue; 
              
              File sourceFile = new File(sourceFileName);
              classNames.add(className);
              files.add(sourceFile);

            }
            catch(IOException e) {  }
            catch(ClassFormatException e) {  }
          }
        }
      }
    }
    catch(Throwable t) {

      throw new UnexpectedException(t); 
    }



    
    
    
   
    synchronized(_compilerModel.getSlaveJVMLock()) {
      
      List<String> tests;
      try { tests = _jvm.findTestClasses(classNames, files); }
      catch(IOException e) { throw new UnexpectedException(e); }
      
      if (tests == null || tests.isEmpty()) {

        nonTestCase(allTests);
        return;
      }
      
      try {
        
        _notifier.junitStarted(); 
        
        _jvm.runTestSuite();
        
      }
      catch(Throwable t) {
        
        
        _notifier.junitEnded();  
        _testInProgress = false;
        throw new UnexpectedException(t);
      }
    }
  }
  
  
  
  
  
  
  public JUnitErrorModel getJUnitErrorModel() { return _junitErrorModel; }
  
  
  public void resetJUnitErrors() {
    _junitErrorModel = new JUnitErrorModel(new JUnitError[0], _model, false);
  }
  
  
  
  
  public void nonTestCase(final boolean isTestAll) {
    
    

      _notifier.nonTestCase(isTestAll);
      _testInProgress = false;
  }
  
  
  public void classFileError(ClassFileError e) { _notifier.classFileError(e); }
  
  
  public void testSuiteStarted(final int numTests) { _notifier.junitSuiteStarted(numTests); }
  
  
  public void testStarted(final String testName) { _notifier.junitTestStarted(testName); }
  
  
  public void testEnded(final String testName, final boolean wasSuccessful, final boolean causedError) {
     _notifier.junitTestEnded(testName, wasSuccessful, causedError);
  }
  
  
  public void testSuiteEnded(JUnitError[] errors) {

    _junitErrorModel = new JUnitErrorModel(errors, _model, true);
    _notifier.junitEnded();
    _testInProgress = false;

  }
  
  
  public File getFileForClassName(String className) {
    return _model.getSourceFile(className + ".java");
  }
  
  
  public ClassPathVector getClassPath() {  return _jvm.getClassPath(); }
  
  
  public void junitJVMReady() {
   
    if (! _testInProgress) return; 
    JUnitError[] errors = new JUnitError[1];
    errors[0] = new JUnitError("Previous test suite was interrupted", true, "");
    _junitErrorModel = new JUnitErrorModel(errors, _model, true);
    _notifier.junitEnded();
    _testInProgress = false; 
  }
}
