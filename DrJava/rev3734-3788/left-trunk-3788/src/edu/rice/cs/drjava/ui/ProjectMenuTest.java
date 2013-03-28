

package edu.rice.cs.drjava.ui;

import edu.rice.cs.drjava.model.MultiThreadedTestCase;
import edu.rice.cs.drjava.model.OpenDefinitionsDocument;
import edu.rice.cs.drjava.model.SingleDisplayModel;
import edu.rice.cs.drjava.project.DocFile;
import edu.rice.cs.drjava.project.MalformedProjectFileException;
import edu.rice.cs.drjava.project.ProjectFileIR;
import edu.rice.cs.drjava.project.ProjectFileParser;

import edu.rice.cs.util.FileOpenSelector;
import edu.rice.cs.util.FileOps;
import edu.rice.cs.util.OperationCanceledException;
import edu.rice.cs.util.swing.Utilities;

import java.io.*;
import java.util.Arrays;
import java.util.List;


public final class ProjectMenuTest extends MultiThreadedTestCase {

  private MainFrame _frame;
  
  private SingleDisplayModel _model;
  
  
  private File _base;
  private File _parent;
  private File _srcDir;
  private File _projDir;
  private File _auxFile;
  private File _projFile;
  private File _file1;
  private File _file2;
  
  private String _file1RelName;
  private String _file2RelName;
  
  
  BufferedReader reader = null;
  
  private String _projFileText = null;
  
  
  public void setUp() throws Exception {
    super.setUp();
    
    
    _base = new File(System.getProperty("java.io.tmpdir")).getCanonicalFile();
    _parent = FileOps.createTempDirectory("proj", _base);
    _srcDir = new File(_parent, "src");
    _srcDir.mkdir(); 

    
    _auxFile = File.createTempFile("aux", ".java").getCanonicalFile();
    File auxFileParent = _auxFile.getParentFile();
    _projFile = new File(_parent, "test.pjt");
    
    _file1 = new File(_srcDir, "test1.java");
    FileOps.writeStringToFile(_file1, "");  
    _file2 = new File(_srcDir, "test2.java");
    FileOps.writeStringToFile(_file2, "");
    

    






    _projFileText =
      ";; DrJava project file.  Written with build: 20040623-1933\n" +
      "(source ;; comment\n" +
      "   (file (name \"src/test1.java\")(select 32 32)(active))" +
      "   (file (name \"src/test2.java\")(select 32 32)))";
    
    FileOps.writeStringToFile(_projFile, _projFileText);

    _frame = new MainFrame();
    _frame.pack();

    _model = _frame.getModel();
  }

  public void tearDown() throws Exception {
    FileOps.deleteDirectoryOnExit(_parent);
    _auxFile.delete();
    _frame.dispose();
    _projFile = null;
    _model = null;
    _frame = null;
    super.tearDown();
  }
  
  public void testSetBuildDirectory() throws MalformedProjectFileException, IOException {
    

    
    
    File f = new File("");
    _model.setBuildDirectory(f);
    assertEquals("Build directory should not have been set", null, _model.getBuildDirectory());
    

    _model.openProject(_projFile);


    
    assertEquals("Build directory should not have been set", null, _model.getBuildDirectory());
    
    _model.setBuildDirectory(f);
    assertEquals("Build directory should have been set", f, _model.getBuildDirectory());
    
  }
  
  public void testCloseAllClosesProject()  throws MalformedProjectFileException, IOException {
    

    _model.openProject(_projFile);
    
    assertTrue("Project should have been opened", _model.isProjectActive());
    _frame.closeAll();
    assertFalse("Project should have been closed", _model.isProjectActive());
  }
  
  public void testSaveProject() throws IOException, MalformedProjectFileException {
    

    
    _frame.openProject(new FileOpenSelector() {
      public File[] getFiles() throws OperationCanceledException { return new File[] {_projFile}; }
    });
        
    
    _frame.open(new FileOpenSelector() {
      public File[] getFiles() throws OperationCanceledException {
        return new File[] {_auxFile};
      }
    });
    _frame._moveToAuxiliary();
    
    _frame.saveProject();
    _frame._closeProject();
    
    List<OpenDefinitionsDocument> docs = _model.getOpenDefinitionsDocuments();
    assertEquals("One empty document remaining", 1, docs.size());
    assertEquals("Name is (Untitled)", "(Untitled)", _model.getActiveDocument().toString());
    
    ProjectFileIR pfir = ProjectFileParser.ONLY.parse(_projFile);
    DocFile[] src = pfir.getSourceFiles();

    DocFile[] aux = pfir.getAuxiliaryFiles();

    assertEquals("Number of saved src files", 2, src.length);
    assertEquals("Number of saved aux files", 1, aux.length);
    assertEquals("wrong name for _file2", _file2.getCanonicalPath(), src[0].getCanonicalPath()); 
    assertEquals("Wrong name for _file1", _file1.getCanonicalPath(), src[1].getCanonicalPath());
    assertEquals("Wrong aux file", _auxFile.getCanonicalPath(), aux[0].getCanonicalPath());
  }
  
}