

package edu.rice.cs.javalanglevels;
import edu.rice.cs.javalanglevels.parser.*;
import edu.rice.cs.javalanglevels.tree.*;
import junit.framework.TestCase;
import java.util.*;
import java.io.*;
import edu.rice.cs.plt.reflect.JavaVersion;
import edu.rice.cs.plt.iter.*;
import edu.rice.cs.plt.io.IOUtil;


public class ElementaryLevelTest extends TestCase {
  File directory;
  
  public void setUp() {
    directory = new File("testFiles" + File.separatorChar + "forElementaryLevelTest");
  }
  
  
  public void testSuccessful() {
    File[] testFiles = directory.listFiles(new FileFilter() {
      public boolean accept(File pathName) {
        return pathName.getAbsolutePath().endsWith(".dj0");
      }
    });

    LanguageLevelConverter llc = new LanguageLevelConverter();
    Pair<LinkedList<JExprParseException>, LinkedList<Pair<String, JExpressionIF>>> result;
    result = llc.convert(testFiles, new Options(JavaVersion.JAVA_5,
                                                IterUtil.make(new File("lib/buildlib/junit.jar"))));
    
    assertEquals("should be no parse exceptions", new LinkedList<JExprParseException>(), result.getFirst());
    assertEquals("should be no visitor exceptions", new LinkedList<Pair<String, JExpressionIF>>(), result.getSecond());
    
    
    for (int i = 0; i < testFiles.length; i++) {
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
    directory = new File(directory, "shouldBreak");
    File[] testFiles = directory.listFiles(new FileFilter() {
      public boolean accept(File pathName) {
        return pathName.getAbsolutePath().endsWith(".dj0");
      }});
    

    Pair<LinkedList<JExprParseException>, LinkedList<Pair<String, JExpressionIF>>> result;
    for (int i = 0; i < testFiles.length; i++) {
      LanguageLevelConverter llc = new LanguageLevelConverter();
      result = llc.convert(new File[]{ testFiles[i] }, new Options(JavaVersion.JAVA_5, EmptyIterable.<File>make()));

      assertTrue("should be parse exceptions or visitor exceptions in file " + testFiles[i].getName(),
                 ! result.getFirst().isEmpty() || ! result.getSecond().isEmpty());
    }
  }
  
  
  public void testSomeFilesCompiled() {
    directory = new File(directory, "someCompiled");
    File[] testFiles = directory.listFiles(new FileFilter() {
      public boolean accept(File pathName) {
        return pathName.getName().equals("UseOtherClassAsField.dj0") || pathName.getName().equals("SubClass.dj0");
      }});
    
    LanguageLevelConverter llc = new LanguageLevelConverter();
    Pair<LinkedList<JExprParseException>, LinkedList<Pair<String, JExpressionIF>>> result;
    result = llc.convert(testFiles, new Options(JavaVersion.JAVA_5, EmptyIterable.<File>make()));
    
    assertEquals("should be no parse exceptions", new LinkedList<JExprParseException>(), result.getFirst());
    assertEquals("should be no visitor exceptions", new LinkedList<Pair<String, JExpressionIF>>(), result.getSecond());
    
    
    for(int i = 0; i < testFiles.length; i++) {
      File currFile = testFiles[i];
      String fileName = currFile.getAbsolutePath();
      fileName = fileName.substring(0, fileName.length() -4);
      File resultingFile = new File(fileName + ".java");
      File correctFile = new File(fileName + ".expected");
      
      try {
        assertEquals("File " + currFile.getName() + " should have been handled correctly",
                     lf(IOUtil.toString(correctFile)),
                     lf(IOUtil.toString(resultingFile)));
      }
      catch (IOException ioe) {
        fail(ioe.getMessage());
        
      }
    }
    
    
    File superClass = new File(directory.getAbsolutePath() + "SuperClass.java");
    File classAsField = new File(directory.getAbsolutePath() + "ClassAsField.java");
    assertFalse("superClass.java should not exist", superClass.exists());
    assertFalse("ClassAsField.java should not exist", classAsField.exists());
  }
  
  
  public void testOrderMatters() {
    directory = new File(directory, "orderMatters");
    File[] files = new File[]{ new File(directory, "Empty.dj0"), new File(directory, "List.dj0"), new File(directory, "NonEmpty.dj0") };
    LanguageLevelConverter llc = new LanguageLevelConverter();
    Pair<LinkedList<JExprParseException>, LinkedList<Pair<String, JExpressionIF>>> result;
    result = llc.convert(files, new Options(JavaVersion.JAVA_5, EmptyIterable.<File>make()));
    
    assertEquals("should be no parse exceptions", new LinkedList<JExprParseException>(), result.getFirst());
    assertEquals("should be 1 visitor exception", 1, result.getSecond().size());
    assertEquals("the error message should be correct", "Could not resolve symbol f", result.getSecond().getFirst().getFirst());
  }
  
  
  public void testEmptyFileNoAction() {
    directory = new File(directory, "emptyFile");
    File[] files = new File[]{ new File(directory, "EmptyFile.dj0")};
    LanguageLevelConverter llc = new LanguageLevelConverter();
    Pair<LinkedList<JExprParseException>, LinkedList<Pair<String, JExpressionIF>>> result;
    result = llc.convert(files, new Options(JavaVersion.JAVA_5, EmptyIterable.<File>make()));
    
    assertEquals("should be no parse exceptions", new LinkedList<JExprParseException>(), result.getFirst());
    assertEquals("should be no visitor exceptions", 0, result.getSecond().size());
    assertFalse("Should be no .java file", (new File(directory, "EmptyFile.java")).exists());
  }
  
  
  public void testRequiresAutoboxing() {
    directory = new File(directory, "requiresAutoboxing");
    File[] testFiles = directory.listFiles(new FileFilter() {
      public boolean accept(File pathName) {
        return pathName.getAbsolutePath().endsWith(".dj0");
      }});
    
    Pair<LinkedList<JExprParseException>, LinkedList<Pair<String, JExpressionIF>>> result;
    
    for (int i = 0; i <testFiles.length; i++) {
      LanguageLevelConverter llc4 = new LanguageLevelConverter();
      result = llc4.convert(new File[]{testFiles[i]}, new Options(JavaVersion.JAVA_1_4, EmptyIterable.<File>make()));
      assertTrue("should be parse exceptions or visitor exceptions", !result.getFirst().isEmpty() || !result.getSecond().isEmpty());
    }
    
    LanguageLevelConverter llc = new LanguageLevelConverter();
    
    result = llc.convert(testFiles, new Options(JavaVersion.JAVA_5, EmptyIterable.<File>make()));
    
    assertEquals("should be no parse exceptions", new LinkedList<JExprParseException>(), result.getFirst());
    assertEquals("should be no visitor exceptions", new LinkedList<Pair<String, JExpressionIF>>(), result.getSecond());
    
    
    for(int i = 0; i < testFiles.length; i++) {
      File currFile = testFiles[i];
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

  
  public static String lf(String s) {
      return s.trim().replaceAll(edu.rice.cs.plt.text.TextUtil.NEWLINE_PATTERN,"\n");
  }
}
