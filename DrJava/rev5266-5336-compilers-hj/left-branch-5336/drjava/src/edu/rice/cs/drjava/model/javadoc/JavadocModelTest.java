

package edu.rice.cs.drjava.model.javadoc;

import edu.rice.cs.drjava.DrJavaTestCase;
import edu.rice.cs.drjava.model.GlobalModel;
import edu.rice.cs.drjava.model.DummyGlobalModel;
import edu.rice.cs.drjava.model.OpenDefinitionsDocument;
import edu.rice.cs.drjava.model.DummyOpenDefDoc;
import edu.rice.cs.drjava.model.definitions.InvalidPackageException;
import edu.rice.cs.drjava.model.compiler.CompilerListener;

import edu.rice.cs.plt.reflect.ReflectUtil;
import edu.rice.cs.util.FileOps;

import java.io.File;


public class JavadocModelTest extends DrJavaTestCase {
  
  
  private File _storedFile;

  
  public void testSimpleSuggestedDirectory() {
    GlobalModel getDocs = new DummyGlobalModel() {
      public boolean hasModifiedDocuments() {
        return false;  
      }
      public boolean hasUntitledDocuments() {
        return false; 
      }
    };
    
    JavadocModel jModel = new DefaultJavadocModel(getDocs, null, ReflectUtil.SYSTEM_CLASS_PATH);
    final File file = new File(System.getProperty("user.dir"));
    OpenDefinitionsDocument doc = new DummyOpenDefDoc() {
      public File getSourceRoot() throws InvalidPackageException { return file; }
    };

    File suggestion = jModel.suggestJavadocDestination(doc);
    File expected = new File(file, JavadocModel.SUGGESTED_DIR_NAME);
    assertEquals("simple suggested destination", expected, suggestion);
  }
  
  
  public void testUnsavedSuggestedDirectory() {
    _storedFile = FileOps.NULL_FILE;
    
    GlobalModel getDocs = new DummyGlobalModel() {
      public boolean hasModifiedDocuments() {
        return true;  
      }
    };
    JavadocModel jModel = new DefaultJavadocModel(getDocs, null, ReflectUtil.SYSTEM_CLASS_PATH);
    final File file = new File(System.getProperty("user.dir"));

    
    JavadocListener listener = new JavadocListener() {
      public void saveBeforeJavadoc() { _storedFile = file; }
      public void compileBeforeJavadoc(final CompilerListener afterCompile) { }
      public void javadocStarted() { }
      public void javadocEnded(boolean success, File destDir, boolean allDocs) { }
    };
    jModel.addListener(listener);
    
    OpenDefinitionsDocument doc = new DummyOpenDefDoc() {
      public File getSourceRoot() throws InvalidPackageException { return _storedFile; }
    };

    File suggestion = jModel.suggestJavadocDestination(doc);
    File expected = new File(file, JavadocModel.SUGGESTED_DIR_NAME);
    assertEquals("simple suggested destination", expected, suggestion);
  }

  
  public void testNoSuggestedDirectory() {
    GlobalModel getDocs = new DummyGlobalModel() {
      public boolean hasModifiedDocuments() { return false;   }
      public boolean hasUntitledDocuments() { return false;   }
    };
    JavadocModel jModel = new DefaultJavadocModel(getDocs, null, null);

    OpenDefinitionsDocument doc = new DummyOpenDefDoc() {
      public File getSourceRoot() throws InvalidPackageException {
        throw new InvalidPackageException(-1, "invalid package");
      }
    };

    File suggestion = jModel.suggestJavadocDestination(doc);
    assertNull("suggestion should be null", suggestion);
  }

  public void testFileDefaultPackage() { }
  public void testFileOnePackage() { }
  public void testFilesOnePackage() { }
  public void testFilesMultiplePackages() { }
  public void testWarnings() { }
  public void testErrors() { }
  public void testSaveFirst() { }
  public void testPromptForDestination() { }
  public void testExtractErrors() { }
  public void testParseLine() { }
  
  public void testCustomArguments() { }
}
