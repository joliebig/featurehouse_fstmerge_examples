

package koala.dynamicjava.util;

import java.io.*;
import junit.framework.TestCase;


public class FileFinderTest extends TestCase
{
  private FileFinder ff = new FileFinder();
  
  public FileFinderTest(){}
  
  public void testFindFile() {

    assertNotFound("Empty1.java");
    assertNotFound("Empty2.java");
    assertNotFound("file.doesnotexist");
    
    ff.addPath("testFiles/someDir1/");
    
    assertFound("Empty1.java");
    assertNotFound("Empty2.java");
    assertNotFound("file.doesnotexist");
    
    ff.addPath("testFiles/someDir2");

    assertFound("Empty1.java");
    assertFound("Empty2.java");
    assertNotFound("file.doesnotexist");

  }
  
  
  private void assertFound(String filename) {
    try {
      File f = ff.findFile(filename);
      assertTrue("Found file: " + filename, f != null);
    }
    catch (IOException ioe) { fail(); }
  }

  private void assertNotFound(String filename) {
    try {
      ff.findFile(filename);
      fail();
    }
    catch (IOException ioe) {
      assertEquals("Error message is correct", "File Not Found: " + filename, ioe.getMessage());
    }
  }

}
