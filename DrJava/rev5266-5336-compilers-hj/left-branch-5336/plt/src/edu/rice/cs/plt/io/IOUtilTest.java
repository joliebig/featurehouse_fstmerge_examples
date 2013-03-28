

package edu.rice.cs.plt.io;

import junit.framework.TestCase;
import java.io.*;

import static edu.rice.cs.plt.io.IOUtil.*;


public class IOUtilTest extends TestCase {
  
  public void testExtensionFilePredicate() {
    FilePredicate p = extensionFilePredicate("fish");
    assertRejectsFile(p, "fish");
    assertAcceptsFile(p, ".fish");
    assertAcceptsFile(p, "foo.fish");
    assertAcceptsFile(p, "this/is/my/favorite.fish");
    assertRejectsFile(p, "this/is/my/favorite.fishery");
    assertAcceptsFile(p, "/this/is/my/favorite.fish");
    assertRejectsFile(p, "/this/is/my/favorite.fishery");
  }

  
  private void assertAcceptsFile(FilePredicate p, String filename) {
    File f = new File(filename);
    assertTrue(p.contains(f));
    assertTrue(p.accept(f));
  }
  
  
  private void assertRejectsFile(FilePredicate p, String filename) {
    File f = new File(filename);
    assertFalse(p.contains(f));
    assertFalse(p.accept(f));
  }

}
