

package edu.rice.cs.drjava.ui;

import edu.rice.cs.drjava.model.MultiThreadedTestCase;
import edu.rice.cs.drjava.model.OpenDefinitionsDocument;
import edu.rice.cs.drjava.model.SingleDisplayModel;
import edu.rice.cs.drjava.project.DocFile;
import edu.rice.cs.drjava.project.MalformedProjectFileException;
import edu.rice.cs.drjava.project.ProjectFileIR;
import edu.rice.cs.drjava.project.ProjectFileParserFacade;

import edu.rice.cs.plt.io.IOUtil;
import edu.rice.cs.util.FileOps;
import edu.rice.cs.util.FileOpenSelector;
import edu.rice.cs.util.OperationCanceledException;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.swing.Utilities;

import java.io.*;
import java.util.List;


public final class ProjectMenuTest extends MultiThreadedTestCase {

  private volatile MainFrame _frame;
  
  private volatile SingleDisplayModel _model;
  
  
  private volatile File _base;
  private volatile File _parent;
  private volatile File _srcDir;
  private volatile File _auxFile;
  private volatile File _projFile;
  private volatile File _file1;
  private volatile File _file2;
  
  
  volatile BufferedReader reader = null;
  
  private volatile String _projFileText = null;
  
  
  private void superSetUp() throws Exception { super.setUp(); }
  
  
  public void setUp() throws Exception {
    
    Utilities.invokeAndWait(new Runnable() {
      public void run() {
        try {
	  superSetUp(); 

          
          _base = new File(System.getProperty("java.io.tmpdir")).getCanonicalFile();
          _parent = IOUtil.createAndMarkTempDirectory("proj", "", _base);
          _srcDir = new File(_parent, "src");
          _srcDir.mkdir(); 
          
          
          _auxFile = File.createTempFile("aux", ".java").getCanonicalFile();
          _projFile = new File(_parent, "test.pjt");
          
          _file1 = new File(_srcDir, "test1.java");
          IOUtil.writeStringToFile(_file1, "");  
          _file2 = new File(_srcDir, "test2.java");
          IOUtil.writeStringToFile(_file2, "");
          

          
          _projFileText =
            ";; DrJava project file.  Written with build: 20040623-1933\n" +
            "(source ;; comment\n" +
            "   (file (name \"src/test1.java\")(select 32 32))" +
            "   (file (name \"src/test2.java\")(select 32 32)))";
          
          IOUtil.writeStringToFile(_projFile, _projFileText);
          
          _frame = new MainFrame();
          _frame.pack();
          _model = _frame.getModel();
          _model.ensureJVMStarterFinished();
          superSetUp();
        }
        
        catch(Exception e) { throw new UnexpectedException(e); }
      }
    });
  }

  public void tearDown() throws Exception {
    Utilities.invokeAndWait(new Runnable() {
      public void run() {
        IOUtil.deleteOnExitRecursively(_parent);
        _auxFile.delete();
        
        _frame.dispose();
        _projFile = FileOps.NULL_FILE;
        _model = null;
        _frame = null;
      }
    });
    super.tearDown();
  }
  
  public void testSetBuildDirectory() throws MalformedProjectFileException, IOException {
    

    
    
    File f = FileOps.NULL_FILE;
    _model.setBuildDirectory(f);
    Utilities.clearEventQueue();  
    assertEquals("Build directory should not have been set", FileOps.NULL_FILE, _model.getBuildDirectory());
    

    Utilities.invokeAndWait(new Runnable() { 
      public void run() { 
        try { _model.openProject(_projFile); }
        catch(Exception e) { throw new UnexpectedException(e); }
      } 
    });


    
    Utilities.clearEventQueue();

    assertEquals("Build directory should not have been set", FileOps.NULL_FILE, _model.getBuildDirectory());
    
    _model.setBuildDirectory(f);
    Utilities.clearEventQueue();
    assertEquals("Build directory should have been set", f, _model.getBuildDirectory());
  }
  
  public void testCloseAllClosesProject()  throws MalformedProjectFileException, IOException {
    

    Utilities.invokeAndWait(new Runnable() { 
      public void run() { 
        try { _model.openProject(_projFile); }
        catch(Exception e) { throw new UnexpectedException(e); }
      } 
    });
    Utilities.clearEventQueue();
    
    assertTrue("Project should have been opened", _model.isProjectActive());
    
    Utilities.invokeAndWait(new Runnable() { 
      public void run() { 
        try { _frame.closeAll(); }
        catch(Exception e) { throw new UnexpectedException(e); }
      } 
    });
    Utilities.clearEventQueue();
    
    assertFalse("Project should have been closed", _model.isProjectActive());
  }
  
  public void testSaveProject() throws IOException, MalformedProjectFileException {
    

    
    Utilities.invokeAndWait(new Runnable() { 
      public void run() { 
        _frame.openProject(new FileOpenSelector() {
          public File[] getFiles() throws OperationCanceledException { return new File[] {_projFile}; }
        });
        
        
        _frame.open(new FileOpenSelector() {
          public File[] getFiles() throws OperationCanceledException { return new File[] {_auxFile}; }
        });
        _frame._moveToAuxiliary();

        List<OpenDefinitionsDocument> auxDocs = _model.getAuxiliaryDocuments();
        assertEquals("One auxiliary document", 1, auxDocs.size());
        _frame.saveProject();
        _frame._closeProject();
      } 
    });
    Utilities.clearEventQueue();
    
    List<OpenDefinitionsDocument> docs = _model.getOpenDefinitionsDocuments();
    assertEquals("One empty document remaining", 1, docs.size());
    assertEquals("Name is (Untitled)", "(Untitled)", _model.getActiveDocument().toString());
    
    ProjectFileIR pfir = ProjectFileParserFacade.ONLY.parse(_projFile);
    DocFile[] src = pfir.getSourceFiles();

    DocFile[] aux = pfir.getAuxiliaryFiles();

    assertEquals("Number of saved src files", 2, src.length);
    assertEquals("Number of saved aux files", 1, aux.length);
    assertEquals("wrong name for _file2", _file2.getCanonicalPath(), src[1].getCanonicalPath()); 
    assertEquals("Wrong name for _file1", _file1.getCanonicalPath(), src[0].getCanonicalPath());
    assertEquals("Wrong aux file", _auxFile.getCanonicalPath(), aux[0].getCanonicalPath());
  }
}