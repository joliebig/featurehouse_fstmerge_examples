

package edu.rice.cs.util;

import edu.rice.cs.drjava.DrJavaTestCase;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;


public class ClassPathVectorTest extends DrJavaTestCase {
  
  
  public void test_toString() {
    ClassPathVector v = new ClassPathVector();
    assertEquals("Empty classpath", "", v.toString());
    addElement(v, "file:///jsr14.jar");
    assertEquals("One element classpath", File.separator+"jsr14.jar"+File.pathSeparator,v.toString());
    addElement(v, "file:///wherever/supercool.jar");
    String fileName = File.separator + "wherever" + File.separator + "supercool.jar";
    assertEquals("Multiple element classpath", File.separator+"jsr14.jar" + File.pathSeparator + fileName + File.pathSeparator, v.toString());
    addElement(v, "http://www.drjava.org/hosted.jar");
    assertEquals("Multiple element classpath", File.separator+"jsr14.jar" + File.pathSeparator + fileName + File.pathSeparator + File.separator + "hosted.jar" + File.pathSeparator, v.toString());
  }
  
  
  public void test_OverloadedAdds() {
    ClassPathVector v = new ClassPathVector();
      v.add("asdf");  
      assertEquals("Nothing should be added", "", v.toString());
  }
  
  
  public void test_asFileVector() throws IOException {
    ClassPathVector vu = new ClassPathVector();
    File[] files = new File[]{
      new File("folder1/folder2/file1.ext"),
      new File("folder1/folder2/file2.ext"),
      new File("folder1/folder2/file3.ext")
    };
    for (File f : files) vu.add(f);
    
    Vector<File> vf = vu.asFileVector();
    assertEquals("Size of vectors should agree", vu.size(), vf.size());
    for(int i=0; i<files.length; i++)
      assertEquals(files[i].getCanonicalFile(), vf.get(i));
  }
  
  private void addElement(ClassPathVector v, String element) {
    try {
      v.add(new URL(element));
    } catch(MalformedURLException e) {
      fail("Mysterious MalformedURLException. Probably not our fault.");
    }
  }
  
}
