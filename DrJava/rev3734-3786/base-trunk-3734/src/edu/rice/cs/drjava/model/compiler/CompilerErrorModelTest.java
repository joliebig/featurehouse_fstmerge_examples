


package edu.rice.cs.drjava.model.compiler;

import java.io.File;
import java.io.IOException;
import javax.swing.text.Position;

import junit.framework.TestCase;
import edu.rice.cs.drjava.model.*;
import edu.rice.cs.drjava.DrJavaTestCase;
import edu.rice.cs.util.OperationCanceledException;


public final class CompilerErrorModelTest extends DrJavaTestCase {
  private File[] files;
  private String[] texts;
  private TestDocGetter getter;
  private CompilerError[] errors;
  private CompilerErrorModel model;
  
  
  public void testConstructNoErrors() {
    getter = new TestDocGetter();
    model = new CompilerErrorModel(new CompilerError[0], getter);
    
    
    assertEquals("Should have no compiler errors.", 0, model.getNumErrors());
    assertEquals("Should have 0 warnings" , 0, model.getNumWarnings());
    assertEquals("Should have 0 compiler errors" , 0, model.getNumCompErrors());
    assertTrue("hasOnlyWarnings should return true.", model.hasOnlyWarnings());
  }
  
  
  public void testConstructOnlyWarnings() {
    getter = new TestDocGetter();
    errors = new CompilerError[] { 
      new CompilerError("Test warning without File", true),
      new CompilerError("Test warning without File", true) 
    };
    model = new CompilerErrorModel(errors, getter);
    
    
    assertEquals("Should have 2 errors.", 2, model.getNumErrors());
    assertEquals("Should have 2 warnings" , 2, model.getNumWarnings());
    assertEquals("Should have 0 compiler errors" , 0, model.getNumCompErrors());
    assertTrue("hasOnlyWarnings should return true.", model.hasOnlyWarnings());
  }
  
  
  public void testConstructDoclessErrors() {
    getter = new TestDocGetter();
    errors = new CompilerError[] { 
      new CompilerError("Test error without File",false),
      new CompilerError("Test warning without File", true),
      new CompilerError("Test error without File",false) 
    };
    
    CompilerError[] copy = new CompilerError[errors.length];
    for (int i = 0; i < errors.length; i++) copy[i] = errors[i];
    model = new CompilerErrorModel(copy, getter);
    
    
    assertEquals("Should have 3 compiler errors.", 3, model.getNumErrors());
    assertEquals("Should have 1 warning" , 1, model.getNumWarnings());
    assertEquals("Should have 2 compiler errors" , 2, model.getNumCompErrors());

    assertEquals("Errors should be sorted.", errors[1], model.getError(2));
    assertTrue("hasOnlyWarnings should return false.", !model.hasOnlyWarnings());
  }
  
  
  public void testConstructOneDocWithoutLineNums() {
    setupDoc();
    errors = new CompilerError[] { 
      new CompilerError(files[0], "Test error with File", false),
      new CompilerError(files[0], "Test warning with File", true),
      new CompilerError(files[0], "Test error with File", false) 
    };
    
    CompilerError[] copy = new CompilerError[errors.length];
    for (int i = 0; i < errors.length; i++)  copy[i] = errors[i];
    model = new CompilerErrorModel(copy, getter);
    
    
    assertEquals("Should have 3 compiler errors.", 3, model.getNumErrors());
    assertEquals("Should have 1 warning" , 1, model.getNumWarnings());
    assertEquals("Should have 2 compiler errors" , 2, model.getNumCompErrors());
    assertEquals("Errors should be sorted.", errors[1], model.getError(2));
    assertTrue("hasOnlyWarnings should return false.", !model.hasOnlyWarnings());
  }
  
  
  public void testConstructOneDocWithLineNums() {
    setupDoc();
    errors = new CompilerError[] { 
      new CompilerError(files[0], 2, 0, "Test error with File and line", false),
      new CompilerError(files[0], 1, 0, "Test warning with File and line", true),
      new CompilerError(files[0], 3, 0, "Test error with File and line", false),
      new CompilerError(files[0], 1, 0, "Test error with File and line", false) 
    };
    
    CompilerError[] copy = new CompilerError[errors.length];
    for (int i = 0; i < errors.length; i++) copy[i] = errors[i];
    model = new CompilerErrorModel(copy, getter);
    
    
    assertEquals("Should have 4 compiler errors.", 4, model.getNumErrors());
    assertEquals("Should have 1 warning" , 1, model.getNumWarnings());
    assertEquals("Should have  compiler errors" , 3, model.getNumCompErrors());
    assertEquals("Errors should be sorted.", errors[3], model.getError(0));
    assertEquals("Errors should be sorted.", errors[1], model.getError(1));
    assertEquals("Errors should be sorted.", errors[0], model.getError(2));
    assertEquals("Errors should be sorted.", errors[2], model.getError(3));
    assertTrue("hasOnlyWarnings should return false.", !model.hasOnlyWarnings());
  }
  
  
  public void testConstructOneDocWithBoth() {
    setupDoc();
    errors = new CompilerError[] { 
      new CompilerError(files[0], 2, 0, "Test error with File and line", false),
      new CompilerError(files[0], "Test warning with File (no line)", true),
      new CompilerError(files[0], 3, 0, "Test error with File and line", false),
      new CompilerError("Test error without File or line", false),
      new CompilerError(files[0], 3, 0, "Test warning with File and line", true),
      new CompilerError(files[0], "Test error with File (no line)", false),
      new CompilerError(files[0], 1, 0, "Test error with File and line", false) 
    };
    
    CompilerError[] copy = new CompilerError[errors.length];
    for (int i = 0; i < errors.length; i++) copy[i] = errors[i];
    model = new CompilerErrorModel(copy, getter);
    
    
    assertEquals("Should have 7 compiler errors.", 7, model.getNumErrors());
    assertEquals("Should have 2 warnings" , 2, model.getNumWarnings());
    assertEquals("Should have 5 compiler errors" , 5, model.getNumCompErrors());
    assertEquals("Errors should be sorted.", errors[3], model.getError(0));
    assertEquals("Errors should be sorted.", errors[5], model.getError(1));
    assertEquals("Errors should be sorted.", errors[1], model.getError(2));
    assertEquals("Errors should be sorted.", errors[6], model.getError(3));
    assertEquals("Errors should be sorted.", errors[0], model.getError(4));
    assertEquals("Errors should be sorted.", errors[2], model.getError(5));
    assertEquals("Errors should be sorted.", errors[4], model.getError(6));
    assertTrue("hasOnlyWarnings should return false.", !model.hasOnlyWarnings());
  }
  
  
  public void testConstructManyDocsWithoutLineNums() {
    setupDocs();
    errors = new CompilerError[] { 
      new CompilerError(files[0], "Test error with File", false),
      new CompilerError(files[2], "Test warning with File", true),
      new CompilerError(files[4], "Test warning with File", true),
      new CompilerError(files[1], "Test error with File", false),
      new CompilerError(files[3], "Test warning with File", true),
      new CompilerError(files[3], "Test error with File", false),
      new CompilerError(files[4], "Test error with File", false),
      new CompilerError(files[0], "Test error with File", false) 
    };
    
    CompilerError[] copy = new CompilerError[errors.length];
    for (int i = 0; i < errors.length; i++) copy[i] = errors[i];
    model = new CompilerErrorModel(copy, getter);
    
    
    assertEquals("Should have 8 compiler errors.", 8, model.getNumErrors());
    assertEquals("Should have 3 warnings" , 3, model.getNumWarnings());
    assertEquals("Should have 5 compiler errors" , 5, model.getNumCompErrors());
    assertEquals("Errors should be sorted.", errors[0], model.getError(0));
    assertEquals("Errors should be sorted.", errors[7], model.getError(1));
    assertEquals("Errors should be sorted.", errors[3], model.getError(2));
    assertEquals("Errors should be sorted.", errors[1], model.getError(3));
    assertEquals("Errors should be sorted.", errors[5], model.getError(4));
    assertEquals("Errors should be sorted.", errors[4], model.getError(5));
    assertEquals("Errors should be sorted.", errors[6], model.getError(6));
    assertEquals("Errors should be sorted.", errors[2], model.getError(7));
    assertTrue("hasOnlyWarnings should return false.", !model.hasOnlyWarnings());
  }
  
  
  public void testConstructManyDocsWithLineNums() {
    setupDocs();
    errors = new CompilerError[] { 
      new CompilerError(files[0], 2, 0, "Test error with File", false),
      new CompilerError(files[2], 3, 0, "Test warning with File", true),
      new CompilerError(files[4], 1, 0, "Test warning with File", true),
      new CompilerError(files[1], 2, 0, "Test error with File", false),
      new CompilerError(files[2], 2, 0, "Test warning with File", true),
      new CompilerError(files[3], 3, 0, "Test error with File", false),
      new CompilerError(files[4], 3, 0, "Test error with File", false),
      new CompilerError(files[0], 1, 0, "Test error with File", false) 
    };
    
    CompilerError[] copy = new CompilerError[errors.length];
    for (int i = 0; i < errors.length; i++) copy[i] = errors[i];
    model = new CompilerErrorModel(copy, getter);
    
    
    assertEquals("Should have 8 compiler errors.", 8, model.getNumErrors());
    assertEquals("Should have 3 warnings" , 3, model.getNumWarnings());
    assertEquals("Should have 5 compiler errors" , 5, model.getNumCompErrors());
    assertEquals("Errors should be sorted.", errors[7], model.getError(0));
    assertEquals("Errors should be sorted.", errors[0], model.getError(1));
    assertEquals("Errors should be sorted.", errors[3], model.getError(2));
    assertEquals("Errors should be sorted.", errors[4], model.getError(3));
    assertEquals("Errors should be sorted.", errors[1], model.getError(4));
    assertEquals("Errors should be sorted.", errors[5], model.getError(5));
    assertEquals("Errors should be sorted.", errors[2], model.getError(6));
    assertEquals("Errors should be sorted.", errors[6], model.getError(7));
    assertTrue("hasOnlyWarnings should return false.", !model.hasOnlyWarnings());
  }
  
  
  public void testConstructManyDocsWithBoth() {
    fullSetup();
    
    
    assertEquals("Should have 15 compiler errors.", 15, model.getNumErrors());
    assertEquals("Should have 6 warnings" , 6, model.getNumWarnings());
    assertEquals("Should have 9 compiler errors" , 9, model.getNumCompErrors());
    assertEquals("Errors should be sorted.", errors[0], model.getError(0));
    assertEquals("Errors should be sorted.", errors[14], model.getError(1));
    assertEquals("Errors should be sorted.", errors[12], model.getError(2));
    assertEquals("Errors should be sorted.", errors[7], model.getError(3));
    assertEquals("Errors should be sorted.", errors[6], model.getError(4));
    assertEquals("Errors should be sorted.", errors[8], model.getError(5));
    assertEquals("Errors should be sorted.", errors[2], model.getError(6));
    assertEquals("Errors should be sorted.", errors[13], model.getError(7));
    assertEquals("Errors should be sorted.", errors[4], model.getError(8));
    assertEquals("Errors should be sorted.", errors[9], model.getError(9));
    assertEquals("Errors should be sorted.", errors[10], model.getError(10));
    assertEquals("Errors should be sorted.", errors[11], model.getError(11));
    assertEquals("Errors should be sorted.", errors[3], model.getError(12));
    assertEquals("Errors should be sorted.", errors[5], model.getError(13));
    assertEquals("Errors should be sorted.", errors[1], model.getError(14));
    assertTrue("hasOnlyWarnings should return false.", !model.hasOnlyWarnings());
  }
  
  
  public void testGetPosition() {
    fullSetup();
    
    Position pos = model.getPosition(errors[1]);
    assertEquals("Incorrect error Position.", 125, pos.getOffset());
    pos = model.getPosition(errors[5]);
    assertEquals("Incorrect error Position.", 38, pos.getOffset());
  }
  
  
  public void testGetErrorAtOffset() throws IOException, OperationCanceledException {
    fullSetup();
    
    OpenDefinitionsDocument doc = getter.getDocumentForFile(files[4]);
    assertEquals("Wrong error at given offset.", errors[1],
                 model.getErrorAtOffset(doc, 125));
    doc = getter.getDocumentForFile(files[4]);
    assertEquals("Wrong error at given offset.", errors[5],
                 model.getErrorAtOffset(doc, 38));
  }
  
  
  public void testHasErrorsWithPositions() throws IOException, OperationCanceledException {
    fullSetup();
    
    
    OpenDefinitionsDocument doc = getter.getDocumentForFile(files[4]);
    assertTrue("File should have errors with lines.", model.hasErrorsWithPositions(doc));
    
    
    doc.setFile(new File("/tmp/./nowhere5"));
    assertTrue("Same file should have errors with lines.", model.hasErrorsWithPositions(doc));
    
    
    doc = getter.getDocumentForFile(files[1]);
    assertTrue("File shouldn't have errors with lines.", !model.hasErrorsWithPositions(doc));
  }
  
  public void testErrorsInMultipleDocuments() throws IOException, OperationCanceledException {
    files = new File[] { 
      new File("/tmp/nowhere1"),
      new File("/tmp/nowhere2") 
    };
    texts = new String[] { 
      new String("kfgkasjg\n" + "faijskgisgj\n" + "sifjsidgjsd\n"),
      new String("isdjfdi\n" + "jfa") 
    };
    getter = new TestDocGetter(files, texts);
    
    errors = new CompilerError[] { 
      new CompilerError(files[1], 0, 0, "Test error with File", false),
      new CompilerError(files[0], 0, 0, "Test error with File", false) 
    };
    model = new CompilerErrorModel(errors, getter);
    model.getErrorAtOffset(getter.getDocumentForFile(files[0]), 25);
    String temp = texts[0];
    texts[0] = texts[1];
    texts[1] = temp;
    getter = new TestDocGetter(files, texts);
    errors = new CompilerError[] { 
      new CompilerError(files[0], 0, 0, "Test error with File", false),
      new CompilerError(files[1], 2, 0, "Test error with File", false)
    };
    model = new CompilerErrorModel(errors, getter);
    model.getErrorAtOffset(getter.getDocumentForFile(files[0]), 10);
  }
  
  
  private void setupDoc() {
    files = new File[] { new File("/tmp/nowhere") };
    texts = new String[] { 
      new String("This is a block of test text.\n" + "It doesn't matter what goes in here.\n" +
                 "But it does matter if it is manipulated properly!\n") };
    getter = new TestDocGetter(files, texts);
  }
  
  
  private void setupDocs() {
    files = new File[] { 
      new File("/tmp/nowhere1"),
      new File("/tmp/nowhere2"),
      new File("/tmp/nowhere3"),
      new File("/tmp/nowhere4"),
      new File("/tmp/nowhere5") 
    };
    texts = new String[] { 
      new String("This is the first block of test text.\n" + "It doesn't matter what goes in here.\n" +
                 "But it does matter if it is manipulated properly!\n"),
      new String("This is the second block of test text.\n" + "It doesn't matter what goes in here.\n" +
                 "But it does matter if it is manipulated properly!\n"),
      new String("This is the third block of test text.\n" + "It doesn't matter what goes in here.\n" +
                 "But it does matter if it is manipulated properly!\n"),
      new String("This is the fourth block of test text.\n" + "It doesn't matter what goes in here.\n" +
                 "But it does matter if it is manipulated properly!\n"),
      new String("This is the fifth block of test text.\n" + "It doesn't matter what goes in here.\n" +
                 "But it does matter if it is manipulated properly!\n") };
    getter = new TestDocGetter(files, texts);
  }
  
  
  private void fullSetup() {
    setupDocs();
    errors = new CompilerError[] { 
      new CompilerError(files[0], "Test error with File (no line)", false),
      new CompilerError(files[4], 3, 0, "Test error with File", false),
      new CompilerError(files[2], "Test warning with File (no line)", true),
      new CompilerError(files[4], "Test warning with File (no line)", true),
      new CompilerError(files[2], 3, 0, "Test warning with File", true),
      new CompilerError(files[4], 1, 0, "Test warning with File", true),
      new CompilerError(files[1], "Test warning with File (no line)", true),
      new CompilerError(files[1], "Test error with File (no line)", false),
      new CompilerError(files[2], "Test error with File (no line)", false),
      new CompilerError(files[3], "Test error with File (no line)", false),
      new CompilerError(files[3], 3, 0, "Test error with File", false),
      new CompilerError(files[4], "Test error with File (no line)", false),
      new CompilerError(files[0], 2, 0, "Test error with File", false),
      new CompilerError(files[2], 2, 0, "Test warning with File", true),
      new CompilerError(files[0], 1, 0, "Test error with File", false) 
    };
        
    CompilerError[] copy = new CompilerError[errors.length];
    for (int i = 0; i < errors.length; i++) copy[i] = errors[i];
    model = new CompilerErrorModel(copy, getter);
  }
}
