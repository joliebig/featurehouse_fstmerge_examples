

package edu.rice.cs.javalanglevels;
import edu.rice.cs.javalanglevels.parser.*;
import edu.rice.cs.javalanglevels.tree.*;
import junit.framework.TestCase;
import java.util.*;
import java.io.*;
import edu.rice.cs.plt.reflect.JavaVersion;
import edu.rice.cs.plt.iter.*;
import edu.rice.cs.plt.io.IOUtil;

import static edu.rice.cs.javalanglevels.ElementaryLevelTest.lf;


public class IntermediateLevelTest extends TestCase {
  File directory;
  
  public void setUp() {
    directory = new File("testFiles/forIntermediateLevelTest");
    
  }

  
  public void testSuccessful() {
    File[] testFiles = directory.listFiles(new FileFilter() {
      public boolean accept(File pathName) {

        return pathName.getAbsolutePath().endsWith(".dj1") && ! pathName.getAbsolutePath().endsWith("Yay.dj1");  
        
      }
    });


    
    LanguageLevelConverter llc = new LanguageLevelConverter();
    Pair<LinkedList<JExprParseException>, LinkedList<Pair<String, JExpressionIF>>> result;
    result = llc.convert(testFiles, new Options(JavaVersion.JAVA_5, IterUtil.make(new File("lib/buildlib/junit.jar"))));
    
    assertEquals("should be no parse exceptions", new LinkedList<JExprParseException>(), result.getFirst());
    
    assertEquals("should be no visitor exceptions", new LinkedList<Pair<String, JExpressionIF>>(), result.getSecond());
    
    

    
    for(int i = 0; i < testFiles.length; i++) {
      File currFile = testFiles[i];
      String fileName = currFile.getAbsolutePath();
      fileName = fileName.substring(0, fileName.length() -4);
      File resultingFile = new File(fileName + ".java");
      File correctFile = new File(fileName + ".expected");
      if (correctFile.exists()) {
        try {

          assertEquals("File " + currFile.getName() + " should have been parsed and augmented correctly.",
                       lf(IOUtil.toString(correctFile)),
                       lf(IOUtil.toString(resultingFile)));
        }
        catch (IOException ioe) {
          fail(ioe.getMessage());
          
        }
      }
    }
  }
  

  
  public void testShouldBeErrors() {
    directory = new File(directory.getAbsolutePath() + System.getProperty("file.separator") + "shouldBreak");
    
    File[] testFiles = directory.listFiles(new FileFilter() {
      public boolean accept(File pathName) {


        return pathName.getAbsolutePath().endsWith("BadClass.dj1");
      }});

    LanguageLevelConverter llc;
    Pair<LinkedList<JExprParseException>, LinkedList<Pair<String, JExpressionIF>>> result;
    for (int i = 0; i < testFiles.length; i++) {

      LanguageLevelConverter llc1 = new LanguageLevelConverter();

      result = llc1.convert(new File[] {testFiles[i]}, new Options(JavaVersion.JAVA_5, EmptyIterable.<File>make()));
      assertTrue("should be parse exceptions or visitor exceptions in file " + testFiles[i].getName(), 
                 ! result.getFirst().isEmpty() || ! result.getSecond().isEmpty());
    }
    
    
    LanguageLevelConverter llc2 = new LanguageLevelConverter();
    File f = new File(new File(directory, "references"), "ReferencingClass.dj1");
    result = llc2.convert(new File[] { f }, new Options(JavaVersion.JAVA_5, EmptyIterable.<File>make()));
    assertTrue("should be parse exceptions or visitor exceptions in file " + f.getName(), 
               ! result.getFirst().isEmpty() || ! result.getSecond().isEmpty());
  }
  
  
  
  public void test14Augmentation() {
        File[] arrayF = new File[]{ new File("testFiles/forIntermediateLevelTest/Yay.dj1")};
      LanguageLevelConverter llc = new LanguageLevelConverter();
      Pair<LinkedList<JExprParseException>, LinkedList<Pair<String, JExpressionIF>>> result;
      result = llc.convert(arrayF, new Options(JavaVersion.JAVA_1_4, EmptyIterable.<File>make()));
      assertEquals("should be no parse exceptions", new LinkedList<JExprParseException>(), result.getFirst());
      
      assertEquals("should be no visitor exceptions", new LinkedList<Pair<String, JExpressionIF>>(), result.getSecond());
      
      
      File currFile = new File("testFiles/forIntermediateLevelTest/Yay.dj1");
      String fileName = currFile.getAbsolutePath();
      fileName = fileName.substring(0, fileName.length() -4);
      File resultingFile = new File(fileName + ".java");
      File correctFile = new File(fileName + ".expected");
        
      try {
        assertEquals("File " + currFile.getName() + " should have been parsed and augmented correctly.",
                     lf(IOUtil.toString(correctFile)),
                     lf(IOUtil.toString(resultingFile)));
        }
      catch (IOException ioe) {
        fail(ioe.getMessage());
        
      }
  }
}
