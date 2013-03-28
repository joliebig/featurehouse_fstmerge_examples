

package edu.rice.cs.drjava.model.junit;

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.rmi.RemoteException;

import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.JOptionPane;

import edu.rice.cs.drjava.model.GlobalModel;
import edu.rice.cs.drjava.model.FileMovedException;
import edu.rice.cs.drjava.model.OpenDefinitionsDocument;
import edu.rice.cs.drjava.model.repl.newjvm.MainJVM;
import edu.rice.cs.drjava.model.compiler.CompilerModel;
import edu.rice.cs.drjava.model.compiler.CompilerListener;
import edu.rice.cs.drjava.model.compiler.DummyCompilerListener;
import edu.rice.cs.drjava.model.definitions.InvalidPackageException;

import edu.rice.cs.plt.io.IOUtil;
import edu.rice.cs.plt.iter.IterUtil;
import edu.rice.cs.plt.lambda.Box;
import edu.rice.cs.plt.lambda.SimpleBox;
import edu.rice.cs.util.FileOps;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.classloader.ClassFileError;
import edu.rice.cs.util.text.SwingDocument;
import edu.rice.cs.util.swing.Utilities;

import org.objectweb.asm.*;

import static edu.rice.cs.plt.debug.DebugUtil.debug;
import edu.rice.cs.drjava.model.compiler.LanguageLevelStackTraceMapper;


public class DefaultJUnitModel implements JUnitModel, JUnitModelCallback {
  
  
  private final JUnitEventNotifier _notifier = new JUnitEventNotifier();
  
  
  private final MainJVM _jvm;
  
  
  private final CompilerModel _compilerModel;
  
  
  private final GlobalModel _model;
  
  
  private volatile JUnitErrorModel _junitErrorModel;
  
  
  private volatile boolean _testInProgress = false;
  
  
  private boolean _forceTestSuffix = false;
  
  
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
      if (doc.inProjectPath())  lod.add(doc);
    }
    junitOpenDefDocs(lod, true);
  }
  

































  
  public void junitDocs(List<OpenDefinitionsDocument> lod) { junitOpenDefDocs(lod, true); }
  
  
  public void junit(OpenDefinitionsDocument doc) throws ClassNotFoundException, IOException {
    debug.logStart("junit(doc)");

    File testFile;
    try { 
      testFile = doc.getFile(); 
      if (testFile == null) {  
        nonTestCase(false, false);
        debug.logEnd("junit(doc): no corresponding file");
        return;
      }
    } 
    catch(FileMovedException fme) {  }
    
    LinkedList<OpenDefinitionsDocument> lod = new LinkedList<OpenDefinitionsDocument>();
    lod.add(doc);
    junitOpenDefDocs(lod, false);
    debug.logEnd("junit(doc)");
  }
  
  
  private void junitOpenDefDocs(final List<OpenDefinitionsDocument> lod, final boolean allTests) {
    
    

    
    
    if (_testInProgress) return;
    
    
    _junitErrorModel = new JUnitErrorModel(new JUnitError[0], null, false);
    

    final List<OpenDefinitionsDocument> outOfSync = _model.getOutOfSyncDocuments(lod);
    if ((outOfSync.size()>0) || _model.hasModifiedDocuments(lod)) {
      

      CompilerListener testAfterCompile = new DummyCompilerListener() {
        @Override public void compileAborted(Exception e) {
          
          
          final CompilerListener listenerThis = this;
          try {
            nonTestCase(allTests, false);
          }
          finally {  
            EventQueue.invokeLater(new Runnable() { 
              public void run() { _compilerModel.removeListener(listenerThis); }
            });
          }
        }
        @Override public void compileEnded(File workDir, List<? extends File> excludedFiles) {
          final CompilerListener listenerThis = this;
          try {
            if (_model.hasOutOfSyncDocuments(lod) || _model.getNumCompErrors() > 0) {
              nonTestCase(allTests, _model.getNumCompErrors() > 0);
              return;
            }
            EventQueue.invokeLater(new Runnable() {  
              public void run() { _rawJUnitOpenDefDocs(lod, allTests); }
            });
          }
          finally {  
            EventQueue.invokeLater(new Runnable() { 
              public void run() { _compilerModel.removeListener(listenerThis); }
            });
          }
        }
      };
      

      _testInProgress = true;
      _notifyCompileBeforeJUnit(testAfterCompile, outOfSync);
      _testInProgress = false;
    }
    
    else _rawJUnitOpenDefDocs(lod, allTests);
  }
  
  
  private void _rawJUnitOpenDefDocs(List<OpenDefinitionsDocument> lod, final boolean allTests) {
    File buildDir = _model.getBuildDirectory();

    
    
    HashSet<String> openDocFiles = new HashSet<String>();
    
    
    HashMap<File, File> classDirsAndRoots = new HashMap<File, File>();
    
    
    
    
    for (OpenDefinitionsDocument doc: lod)  {
      if (doc.isSourceFile())  { 
        try {
          File sourceRoot = doc.getSourceRoot(); 
          
          
          openDocFiles.add(doc.getCanonicalPath());
          
          String packagePath = doc.getPackageName().replace('.', File.separatorChar);
          
          
          
          File buildRoot = (buildDir == FileOps.NULL_FILE) ? sourceRoot: buildDir;
          
          File classFileDir = new File(IOUtil.attemptCanonicalFile(buildRoot), packagePath);
          
          File sourceDir = 
            (buildDir == FileOps.NULL_FILE) ? classFileDir : new File(IOUtil.attemptCanonicalFile(sourceRoot), packagePath);
          
          if (! classDirsAndRoots.containsKey(classFileDir)) {
            classDirsAndRoots.put(classFileDir, sourceDir);


          }
        }
        catch (InvalidPackageException e) {  }
      }
    }


    
    
    Set<File> classDirs = classDirsAndRoots.keySet();
    

    
    
    final ArrayList<String> classNames = new ArrayList<String>();
    
    
    final ArrayList<File> files = new ArrayList<File>();
    
    
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
              final Box<String> className = new SimpleBox<String>();
              final Box<String> sourceName = new SimpleBox<String>();
              new ClassReader(IOUtil.toByteArray(entry)).accept(new ClassVisitor() {
                public void visit(int version, int access, String name, String sig, String sup, String[] inters) {
                  className.set(name.replace('/', '.'));
                }
                public void visitSource(String source, String debug) {
                  sourceName.set(source);
                }
                public void visitOuterClass(String owner, String name, String desc) {}
                public AnnotationVisitor visitAnnotation(String desc, boolean visible) { return null; }
                public void visitAttribute(Attribute attr) {}
                public void visitInnerClass(String name, String out, String in, int access) {}
                public FieldVisitor visitField(int a, String n, String d, String s, Object v) { return null; }
                public MethodVisitor visitMethod(int a, String n, String d, String s, String[] e) { return null; }
                public void visitEnd() {}
              }, 0);
              
              File rootDir = classDirsAndRoots.get(dir);
              
              
              String javaSourceFileName = getCanonicalPath(rootDir) + File.separator + sourceName.value();

              
              
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
              classNames.add(className.value());
              files.add(sourceFile);

            }
            catch(IOException e) {  }
          }
        }
      }
    }
    catch(Exception e) {

      throw new UnexpectedException(e); 
    }
    
    
    _testInProgress = true;
    
    new Thread(new Runnable() {
      public void run() { 
        
        
        
        
        
        
        
        
        
        synchronized(_compilerModel.getCompilerLock()) {
          
          
          List<String> tests = _jvm.findTestClasses(classNames, files).unwrap(null);
          if (tests == null || tests.isEmpty()) {
            nonTestCase(allTests, false);
            return;
          }
        }
        
        try {
          
          
          _notifyJUnitStarted(); 
          boolean testsPresent = _jvm.runTestSuite();  
          if (! testsPresent) throw new RemoteException("No unit test classes were passed to the slave JVM");
        }
        catch(RemoteException e) { 
          _notifyJUnitEnded();  
          _testInProgress = false;
        }
      }
    }).start();
  }
  

  
  
  private void _notifyJUnitStarted() { 
    
    EventQueue.invokeLater(new Runnable() { public void run() { _notifier.junitStarted(); } });
  }
  
  
  private void _notifyJUnitEnded() { 
    
    EventQueue.invokeLater(new Runnable() { public void run() { _notifier.junitEnded(); } });
  }
  
  
  private void _notifyCompileBeforeJUnit(final CompilerListener testAfterCompile, final List<OpenDefinitionsDocument> outOfSync) { 
    Utilities.invokeLater(new Runnable() { public void run() { _notifier.compileBeforeJUnit(testAfterCompile, outOfSync); } });
  }
  
  
  private void _notifyNonTestCase(final boolean testAll, final boolean didCompileFail) { 
    Utilities.invokeLater(new Runnable() { public void run() { _notifier.nonTestCase(testAll, didCompileFail); } });
  }
  
  private String getCanonicalPath(File f) throws IOException {
    if (f == null) return "";
    return f.getCanonicalPath();
  }
  
  
  
  
  public JUnitErrorModel getJUnitErrorModel() { return _junitErrorModel; }
  
  
  public void resetJUnitErrors() {
    _junitErrorModel = new JUnitErrorModel(new JUnitError[0], _model, false);
  }
  
  
  
  
  public void nonTestCase(final boolean isTestAll, boolean didCompileFail) {
    
    

    _notifyNonTestCase(isTestAll, didCompileFail);
    _testInProgress = false;
  }
  
  
  public void classFileError(final ClassFileError e) { 
    Utilities.invokeLater(new Runnable() { public void run() {_notifier.classFileError(e); } });
  }
  
  
  public void testSuiteStarted(final int numTests) { 
    Utilities.invokeLater(new Runnable() { public void run() { _notifier.junitSuiteStarted(numTests); } });
  }
  
  
  public void testStarted(final String testName) { 
    Utilities.invokeLater(new Runnable() { public void run() { _notifier.junitTestStarted(testName); } });
  }
  
  
  public void testEnded(final String testName, final boolean wasSuccessful, final boolean causedError) {
    EventQueue.invokeLater(new Runnable() { 
      public void run() { _notifier.junitTestEnded(testName, wasSuccessful, causedError); }
    });
  }
  
  
  public void testSuiteEnded(JUnitError[] errors) {

    
    List<File> files = new ArrayList<File>();
    for(OpenDefinitionsDocument odd: _model.getLLOpenDefinitionsDocuments()){ files.add(odd.getRawFile()); }
    for(JUnitError e: errors){
      e.setStackTrace(_compilerModel.getLLSTM().replaceStackTrace(e.stackTrace(),files));
      File f = e.file();
      if (LanguageLevelStackTraceMapper.isLLFile(f)) {
        String dn = f.getName();
        dn = dn.substring(0, dn.lastIndexOf('.'))+".java";
        StackTraceElement ste = new StackTraceElement(e.className(), "", dn, e.lineNumber());
        ste = _compilerModel.getLLSTM().replaceStackTraceElement(ste, f);
        e.setLineNumber(ste.getLineNumber());
      }
    }
    _junitErrorModel = new JUnitErrorModel(errors, _model, true);
    _notifyJUnitEnded();
    _testInProgress = false;

  }
  
  
  public File getFileForClassName(String className) { return _model.getSourceFile(className + ".java"); }
  
  
  public Iterable<File> getClassPath() {  return _jvm.getClassPath().unwrap(IterUtil.<File>empty()); }
  
  
  public void junitJVMReady() {
    if (! _testInProgress) return;
    
    JUnitError[] errors = new JUnitError[1];
    errors[0] = new JUnitError("Previous test suite was interrupted", true, "");
    _junitErrorModel = new JUnitErrorModel(errors, _model, true);
    _notifyJUnitEnded();
    _testInProgress = false;
  }
}
